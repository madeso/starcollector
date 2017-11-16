package com.madeso.starcollector

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.FillViewport
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.StretchViewport

class StarCollector(disposer: Disposer)
{
    val world_camera = OrthographicCamera()
    val world_display = FitViewport(1f, 1f, world_camera)

    val background_camera = OrthographicCamera()
    val background_display = FillViewport(1f, 1f, background_camera)

    val text_camera = OrthographicCamera()
    val text_display = ExtendViewport(600f, 600f, text_camera)

    val batch = SpriteBatch()
    val fontbatch = SpriteBatch()

    val BUTTONSIZE = 0.1f
    val PLAYERCOUNT = 5
    val WORLDCOUNT = 10
    val SCROLLSPEED = 0.02f
    val SIZE = 0.04f

    val starSprite = disposer.CreateSprite(disposer.CreateTexture("data/star.png"), SIZE)

    val playerSprite = Array(PLAYERCOUNT)
    {
        disposer.CreateSprite(disposer.CreateTexture("player/" + Integer.toString(it + 1) + ".png"), SIZE * 2)
    }

    val allSprite = disposer.CreateSprite(disposer.CreateTexture("input/all.png"), BUTTONSIZE * 2)
    val leftSprite = disposer.CreateSprite(disposer.CreateTexture("input/left.png"), BUTTONSIZE)
    val upSprite = disposer.CreateSprite(disposer.CreateTexture("input/up.png"), BUTTONSIZE)
    val rightSprite = disposer.CreateSprite(disposer.CreateTexture("input/right.png"), BUTTONSIZE)
    val downSprite = disposer.CreateSprite(disposer.CreateTexture("input/down.png"), BUTTONSIZE)
    val notSprite = disposer.CreateSprite(disposer.CreateTexture("input/not.png"), BUTTONSIZE)

    val backgroundsTexture = disposer.CreateTexture("data/backgrounds.png")
    val backgroundsSprite = Sprite(backgroundsTexture)

    val worldSprite = Array(WORLDCOUNT)
    {
        disposer.CreateSprite(disposer.CreateTexture("world/" + Integer.toString(it + 1) + "-mid.png"), SIZE * 2)
    }

    val sndScore = disposer.CreateSound("data/score.wav")
    val sndStep = disposer.CreateSound("data/step.wav")
    val sndDie = disposer.CreateSound("data/die.wav")

    val music = disposer.CreateMusic("data/Malloga_Ballinga_Mastered_mp_0.mp3")

    val font = BitmapFont() // BitmapFont(Gdx.files.internal("Calibri.fnt"),Gdx.files.internal("Calibri.png"),false);

    val game = Game(sndScore, sndStep, sndDie, PLAYERCOUNT, WORLDCOUNT, SCROLLSPEED, SIZE)

    fun OnSize()
    {
        val w = Gdx.graphics.width
        val h = Gdx.graphics.height

        background_display.update(w,h, true)
        world_display.update(w, h, false)
        text_display.update(w, h, true)
    }

    init {
        backgroundsTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
        backgroundsSprite.setPosition(0.0f, 0.0f)
        backgroundsSprite.setSize(1f, 1f)
        music.setVolume(0.5f)
        music.setLooping(true)
        music.play()
        game.genworld()

        OnSize()
    }

    private var first_touch_position: Vector3? = null

    fun GetTouchPosition(): Vector3
    {
        val touchPos = Vector3()
        touchPos.set(Gdx.input.getX(0).toFloat(), Gdx.input.getY(0).toFloat(), 0f)
        world_camera.unproject(touchPos)
        return touchPos
    }

    fun Classify(afrom: Vector3, ato: Vector3) : Maths.Direction
    {
        val from = Vector3(afrom)
        val to = Vector3(ato)

        val diff = 0.04f
        var dist = to.sub(from)
        dist.y = -dist.y

        dist = dist.scl(1.0f / diff)

        return Maths.Classify(dist.x, dist.y)
    }

    fun SelectSpriteBasedOnDirection(dir: Maths.Direction) : Sprite
    {
        return when (dir) {
            Maths.Direction.center -> allSprite
            Maths.Direction.left -> leftSprite
            Maths.Direction.up -> downSprite
            Maths.Direction.right -> rightSprite
            Maths.Direction.down -> upSprite
            else -> notSprite
        }
    }

    fun render() {
        game.update(Gdx.graphics.deltaTime)

        var ui_icon : Sprite? = null

        val is_currently_touching = Gdx.input.isTouched(0)
        val current_touch_position = GetTouchPosition()

        if (is_currently_touching) {
            val first_touch_position = this.first_touch_position ?: current_touch_position
            this.first_touch_position = first_touch_position

            val dir = Classify(first_touch_position, current_touch_position)

            if(game.isAlive && game.isStopped)
            {
                val icon = SelectSpriteBasedOnDirection(dir)
                icon.setPosition(first_touch_position.x - icon.width / 2, first_touch_position.y - icon.height / 2)
                ui_icon = icon
            }
        } else {
            // there is no touch , check for release touch
            val first_touch_position = this.first_touch_position
            this.first_touch_position = null
            if (first_touch_position != null) {
                // touch was released, act on it

                val dir = Classify(first_touch_position, current_touch_position)

                when (dir) {
                    Maths.Direction.center -> game.input(Game.Input.tap)
                    Maths.Direction.left -> game.input(Game.Input.left)
                    Maths.Direction.right -> game.input(Game.Input.right)
                    Maths.Direction.up -> game.input(Game.Input.up)
                    Maths.Direction.down -> game.input(Game.Input.down)
                    else -> {}
                }
            }
        }

        Gdx.gl.glClearColor(94 / 255.0f, 129 / 255.0f, 162 / 255.0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        val h = Gdx.graphics.height.toFloat()
        val w = Gdx.graphics.width.toFloat()

        background_display.apply()
        batch.projectionMatrix = background_camera.combined
        batch.begin()
        game.drawBackground(batch, backgroundsSprite)
        batch.end()

        // draw world
        world_display.apply()
        batch.projectionMatrix = world_camera.combined
        batch.begin()
        game.draw(batch, worldSprite, starSprite, playerSprite)
        batch.end()

        if(ui_icon != null)
        {
            batch.begin()
            ui_icon.draw(batch)
            batch.end()
        }

        text_display.apply()
        fontbatch.projectionMatrix = text_camera.combined
        fontbatch.begin()
        font.setColor(0f, 0f, 0f, 1.0f)
        game.draw_text(fontbatch, font)
        fontbatch.end()
    }
}
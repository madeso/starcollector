package com.madeso.starcollector

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.Texture.TextureWrap
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound


class StarCollectorGame : ApplicationListener {
    private var camera: OrthographicCamera? = null
    private var batch: SpriteBatch? = null
    private var fontbatch: SpriteBatch? = null

    internal lateinit var starTexture: Texture
    internal lateinit var playerTexture: Array<Texture>
    internal lateinit var allTexture: Texture
    internal lateinit var leftTexture: Texture
    internal lateinit var upTexture: Texture
    internal lateinit var rightTexture: Texture
    internal lateinit var downTexture: Texture
    internal lateinit var notTexture: Texture

    internal lateinit var worldTexture: Array<Texture>
    internal lateinit var backgroundsTexture: Texture

    internal lateinit var starSprite: Sprite
    internal lateinit var playerSprite: Array<Sprite>
    internal lateinit var allSprite: Sprite
    internal lateinit var leftSprite: Sprite
    internal lateinit var upSprite: Sprite
    internal lateinit var rightSprite: Sprite
    internal lateinit var downSprite: Sprite
    internal lateinit var notSprite: Sprite

    internal lateinit var worldSprite: Array<Sprite>
    internal lateinit var backgroundsSprite: Sprite

    internal lateinit var game: Game

    internal lateinit var shapes: ShapeRenderer
    private var font: BitmapFont? = null
    private var sndScore: Sound? = null
    private var sndStep: Sound? = null
    private var sndDie: Sound? = null
    private var music: Music? = null

    private val BUTTONSIZE = 0.1f

    override fun create() {
        val w = Gdx.graphics.width.toFloat()
        val h = Gdx.graphics.height.toFloat()

        camera = OrthographicCamera(1f, h / w)
        batch = SpriteBatch()
        fontbatch = SpriteBatch()

        starTexture = CreateTexture("data/star.png")
        starSprite = createSprite(starTexture, Game.SIZE)


        playerTexture = Array(PLAYERCOUNT)
        {
            CreateTexture("player/" + Integer.toString(it + 1) + ".png")
        }
        playerSprite = Array(PLAYERCOUNT)
        {
            createSprite(playerTexture[it], Game.SIZE * 2)
        }

        allTexture = CreateTexture("input/all.png")
        allSprite = createSprite(allTexture, BUTTONSIZE * 2)
        leftTexture = CreateTexture("input/left.png")
        leftSprite = createSprite(leftTexture, BUTTONSIZE)
        upTexture = CreateTexture("input/up.png")
        upSprite = createSprite(upTexture, BUTTONSIZE)
        rightTexture = CreateTexture("input/right.png")
        rightSprite = createSprite(rightTexture, BUTTONSIZE)
        downTexture = CreateTexture("input/down.png")
        downSprite = createSprite(downTexture, BUTTONSIZE)
        notTexture = CreateTexture("input/not.png")
        notSprite = createSprite(notTexture, BUTTONSIZE)

        backgroundsTexture = CreateTexture("data/backgrounds.png")
        backgroundsTexture.setWrap(TextureWrap.Repeat, TextureWrap.Repeat)
        backgroundsSprite = Sprite(backgroundsTexture)
        backgroundsSprite.setSize(w, h)
        backgroundsSprite.setPosition(0.0f, 0.0f)

        worldTexture = Array(WORLDCOUNT)
        {
            CreateTexture("world/" + Integer.toString(it + 1) + "-mid.png")
        }
        worldSprite = Array(WORLDCOUNT)
        {
            createSprite(worldTexture[it], Game.SIZE * 2)
        }

        shapes = ShapeRenderer()

        sndScore = Gdx.audio.newSound(Gdx.files.internal("data/score.wav"))
        sndStep = Gdx.audio.newSound(Gdx.files.internal("data/step.wav"))
        sndDie = Gdx.audio.newSound(Gdx.files.internal("data/die.wav"))

        music = Gdx.audio.newMusic(Gdx.files.internal("data/Malloga_Ballinga_Mastered_mp_0.mp3"))

        font = BitmapFont() // BitmapFont(Gdx.files.internal("Calibri.fnt"),Gdx.files.internal("Calibri.png"),false);

        music!!.setVolume(0.5f)
        music!!.setLooping(true)
        music!!.play()

        game = Game(sndScore!!, sndStep!!, sndDie!!)
        game.genworld()
    }

    override fun dispose() {
        batch!!.dispose()
        fontbatch!!.dispose()
        starTexture.dispose()
        for (p in 0..PLAYERCOUNT - 1) {
            playerTexture[p].dispose()
        }

        allTexture.dispose()
        leftTexture.dispose()
        upTexture.dispose()
        rightTexture.dispose()
        downTexture.dispose()
        notTexture.dispose()
        backgroundsTexture.dispose()

        for (w1 in 0..WORLDCOUNT - 1) {
            worldTexture[w1].dispose()
        }

        font!!.dispose()
        sndScore!!.dispose()
        sndStep!!.dispose()
        sndDie!!.dispose()
        music!!.stop()
        music!!.dispose()
    }

    private var touchdown = false
    private var touchpos: Vector3? = null

    override fun render() {
        game.update(Gdx.graphics.deltaTime)

        Gdx.gl.glClearColor(94 / 255.0f, 129 / 255.0f, 162 / 255.0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        val normalProjection = Matrix4().setToOrtho2D(0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        fontbatch!!.projectionMatrix = normalProjection

        batch!!.projectionMatrix = camera!!.combined
        shapes.projectionMatrix = camera!!.combined
        // fontbatch.setProjectionMatrix(camera.combined);

        fontbatch!!.begin()
        game.drawBackground(fontbatch!!, backgroundsSprite)
        fontbatch!!.end()

        batch!!.begin()
        game.draw(batch!!, worldSprite, starSprite, playerSprite)
        batch!!.end()

        /*shapes.begin(ShapeType.Line);
		game.draw_lines(shapes);
		shapes.end();*/

        fontbatch!!.begin()
        font!!.setColor(0f, 0f, 0f, 1.0f)
        game.draw_text(fontbatch!!, font!!)
        fontbatch!!.end()

        val diff = 0.04f

        if (Gdx.input.isTouched(0)) {
            if (touchdown == false) {
                touchdown = true
                touchpos = touchPosScreen
            }

            val rtouchPos = Vector3(touchpos)

            val newTouchPos = touchPosScreen
            var dist = newTouchPos.sub(touchpos!!)
            dist.y = -dist.y

            dist = dist.scl(1.0f / diff)

            val dir = Maths.Classify(dist.x, dist.y)

            if (game.isAlive) {
                var icon = notSprite
                if (game.isStopped) {
                    when (dir) {
                        5 -> icon = allSprite
                        4 -> icon = leftSprite
                        8 -> icon = downSprite
                        6 -> icon = rightSprite
                        2 -> icon = upSprite
                    }
                }
                icon.setPosition(rtouchPos.x - icon.width / 2, rtouchPos.y - icon.height / 2)
                batch!!.begin()
                icon.draw(batch!!)
                batch!!.end()
            }
        } else {
            if (touchdown) {
                touchdown = false
                val newTouchPos = touchPosScreen
                var dist = newTouchPos.sub(touchpos!!)
                dist.y = -dist.y
                val d = dist.len()

                dist = dist.scl(1.0f / diff)

                val dir = Maths.Classify(dist.x, dist.y)

                when (dir) {
                    5 -> game.input(Game.Input.tap)
                    4 -> game.input(Game.Input.left)
                    6 -> game.input(Game.Input.right)
                    8 -> game.input(Game.Input.up)
                    2 -> game.input(Game.Input.down)
                }

                if (d > 1.0f) {
                }
            }
        }
    }

    private val touchPosScreen: Vector3
        get() {
            val touchPos = Vector3()
            touchPos.set(Gdx.input.getX(0).toFloat(), Gdx.input.getY(0).toFloat(), 0f)
            camera!!.unproject(touchPos)
            return touchPos
        }

    override fun resize(width: Int, height: Int) {}

    override fun pause() {}

    override fun resume() {}

    companion object {

        val PLAYERCOUNT = 5
        val WORLDCOUNT = 10

        private fun createSprite(texture: Texture, size: Float): Sprite {
            val sprite = Sprite(TextureRegion(texture))
            sprite.setSize(size, size)
            sprite.setOrigin(sprite.width / 2,
                    sprite.height / 2)
            return sprite
        }

        private fun CreateTexture(path: String): Texture {
            val texture = Texture(Gdx.files.internal(path))
            texture.setFilter(TextureFilter.Linear, TextureFilter.Linear)
            return texture
        }
    }
}
package com.madeso.starcollector

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2


class Game(val assets: Assets, val playercount : Int, val worldcount: Int, val size: Float, val number_of_stars : Int, val world : World<Int>, val width : Int, val height: Int, val state : States) {

    private val items = 10

    val STEPTIME = 0.1f

    internal var itemsleft = 0
    internal var playerx: Int = 0
    internal var playery: Int = 0

    internal var last_playerx: Int = 0
    internal var last_playery: Int = 0

    internal var timer = 0.0f
    internal var isAlive = true
    internal var canplaytext = ""
    internal var dx = 0
    internal var dy = 0

    val rand = Randomizer()

    private var playerIndex: Int = 0
    private var worldIndex: Int = 0

    val sln = SolutionMap(width, height)

    internal fun transform(x: Int, y: Int): Vector2 {
        val step = size

        val startx = step - step * width / 2.0f
        val starty = step - step * height / 2.0f

        return Vector2(startx + step * (x - 1), starty + step * (y - 1))
    }

    internal fun Inter(f: Float, t: Float, a: Float) = Interpolation.pow3In.apply(t, f, a)
    internal fun PlayerInterpolate(f: Vector2, t: Vector2, a: Float) = Vector2(Inter(f.x, t.x, a), Inter(f.y, t.y, a))

    internal fun gameover() {
        isAlive = false
        canplaytext = "Game over. Tap to restart."
        assets.sDie.play()
        println("FAIL!")
        Gdx.input.cancelVibrate()
        Gdx.input.vibrate(500)
    }

    internal fun win() {
        isAlive = false
        canplaytext = "Level completed. Tap to restart"
        println("WIN!")
    }

    internal fun score() {
        assets.sScore.play()
        println("Score!")
        itemsleft = itemsleft - 1
        if (itemsleft == 0) {
            win()
            Gdx.input.cancelVibrate()
            Gdx.input.vibrate(500)
        } else {
            Gdx.input.cancelVibrate()
            Gdx.input.vibrate(100)
        }
    }

    internal fun step(x: Int, y: Int): Boolean {
        playerx = playerx + x
        playery = playery + y

        if (playerx < 0) {
            gameover()
            return false
        }
        if (playery < 0) {
            gameover()
            return false
        }

        if (playerx >= width) {
            gameover()
            return false
        }

        if (playery >= height) {
            gameover()
            return false
        }

        if (!world.IsFree(playerx, playery)) {
            world.RemoveStar(playerx, playery)
            score()
            return false
        }

        assets.sStep.play()

        return true
    }

    private fun setuplast()
    {
        last_playerx = playerx
        last_playery = playery
    }

    internal fun update(dt: Float) {
        if (isAlive) {
            if (dx != 0 || dy != 0) {
                if (timer <= 0) {
                    setuplast()
                    if (step(dx, dy)) {
                        timer += STEPTIME
                    } else {
                        dx = 0
                        dy = 0
                        timer = 0f
                    }
                } else {
                    timer = timer - dt
                }
            }
        }
    }

    enum class Input {
        left, right, up, down, tap
    }

    fun input(input: Input) {
        if (isAlive) {
            if (dx != 0 || dy != 0) {
            } else {
                if (input == Input.right) {
                    dx = 1
                    dy = 0
                }

                if (input == Input.left) {
                    dx = -1
                    dy = 0
                }

                if (input == Input.up) {
                    dx = 0
                    dy = -1
                }

                if (input == Input.down) {
                    dx = 0
                    dy = 1
                }

            }
        } else {
            if (input == Input.tap) {
                state.CreateNewWorld()
            }
        }
    }

    internal val isStopped: Boolean
        get() = dx == 0 && dy == 0

    fun CreateWorld() {
        canplaytext = ""
        isAlive = true

        isAlive = true
        canplaytext = ""

        itemsleft = items

        timer = 0f
        dx = 0
        dy = 0

        val generator = WorldGenerator(world, width, height, items, sln)
        generator.genworld(number_of_stars)
        playerx = generator.playerx
        playery = generator.playery

        playerIndex = rand.random(playercount)
        worldIndex = rand.random(worldcount)
    }

    fun draw(batch: SpriteBatch, world: Array<WorldTexture>, paths: Array<Sprite>, star: Array<Sprite>, player: Array<Sprite>) {
        drawPlayer(batch, player, height)
        for (y in height - 1 downTo 0) {
            drawWorld(batch, world, y)
            drawPath(batch, paths, y)
            drawStars(batch, star, y)
            drawPlayer(batch, player, y)
        }
        drawPlayer(batch, player,-1)
    }

    private fun drawStars(batch: SpriteBatch, stars: Array<Sprite>, y: Int) {
        for (x in 0..width - 1) {
            val star_index = world.GetStarIndex(x, y)
            if ( star_index != null ) {
                val star = stars[star_index]
                val p2 = transform(x, y)
                star.setPosition(p2.x, p2.y - size * (8.0f / 16.0f)  + size*starydisp )
                star.draw(batch)
            }
        }
    }

    private fun drawWorld(batch: SpriteBatch, sprite: Array<WorldTexture>, y: Int) {
        for (x in 0..width - 1) {
            val p2 = transform(x, y)
            val texture = sprite[worldIndex]
            val xval = when(x)
            {
                0 -> 0
                width-1 -> 2
                else -> 1
            }
            val yval = when(y)
            {
                0 -> 0
                height-1 -> 2
                else -> 1
            }
            val sp = texture.sprite(xval, yval)
            sp.setPosition(p2.x, p2.y - size * (8.0f / 16.0f))
            sp.draw(batch)
        }
    }

    private fun drawPlayer(batch: SpriteBatch, player: Array<Sprite>, y: Int) {
        if (y == playery) {
            val p = if(isStopped )
            {
                transform(playerx, playery)
            }
            else
            {
                PlayerInterpolate(transform(last_playerx, last_playery), transform(playerx, playery), timer/STEPTIME)
            }
            player[playerIndex].setPosition(p.x, p.y)
            player[playerIndex].draw(batch)
        }
    }

    val pathydisp = 0.15f
    val starydisp = 0.15f

    fun drawPath(batch: SpriteBatch, stars: Array<Sprite>, y: Int) {
        for (x in 0..width - 1) {
            if(sln.HasValueAt(x,y))
            {
                val index = sln.ValueAt(x,y)-1
                val sp = stars[index]
                val p2 = transform(x, y)
                sp.setPosition(p2.x, p2.y - size * (8.0f / 16.0f) + size*pathydisp)
                sp.draw(batch)
            }
        }
    }

    fun draw_text(batch: SpriteBatch, font: BitmapFont) {
        if (isAlive == false) {
            font.draw(batch, canplaytext, 4f, 15f)
        }
    }
}

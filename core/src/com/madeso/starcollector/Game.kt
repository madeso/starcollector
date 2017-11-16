package com.madeso.starcollector

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import java.util.*


class Game(internal var sScore: Sound, internal var sStep: Sound, internal var sDie: Sound, val playercount : Int, val worldcount: Int, val scrollspeed : Float, val size: Float) {

    private val width = 20
    private val height = 20
    private val items = 10

    internal var itemsleft = 0
    internal var playerx: Int = 0
    internal var playery: Int = 0

    internal var timer = 0.0f
    internal var isAlive = true
    internal var canplaytext = ""
    internal var dx = 0
    internal var dy = 0

    internal var rand = Random()
    private var playerIndex: Int = 0
    private var worldIndex: Int = 0
    private var backgroundIndex: Int = 0
    private var backgroundTimer = 0.0f

    internal var solution = ArrayList<Vec2i>()
    private val world = World(width, height)
    private var mem = Array(width) { BooleanArray(height) }

    fun isfree(x: Int, y: Int): Boolean {
        return if (playerx == x && playery == y) {
            false
        } else mem[x][y] == false
    }

    internal fun remember(sx: Int, sy: Int, nx: Int, ny: Int) {
        var x = sx
        var y = sy
        var dx = 0
        var dy = 0

        if (x > nx) {
            dx = -1
        } else if (x < nx) {
            dx = 1
        }

        if (y > ny) {
            dy = -1
        } else if (y < ny) {
            dy = 1
        }

        do {
            mem[x][y] = true

            // for solution traversing
            solution.add(Vec2i(x, y))

            x = x + dx
            y = y + dy
        } while (false == (x == nx && y == ny))
    }

    internal fun listValidPositions(sx: Int, sy: Int): List<Vec2i> {
        val r = ArrayList<Vec2i>()

        for (x in 0..width - 1) {
            if (x != sx) {
                r.add(Vec2i(x, sy))
            }
        }

        for (y in 0..height - 1) {
            if (y != sy) {
                r.add(Vec2i(sx, y))
            }
        }

        return r
    }

    internal fun fillworld(): Boolean {
        var x = playerx
        var y = playery
        var nx: Int
        var ny: Int

        var done = false

        var positions: List<Vec2i> = ArrayList()
        var pos: Vec2i? = null
        var generate = true
        var randomindex = 0
        var i = 0

        do {
            if (generate) {
                positions = listValidPositions(x, y)
                // generated valid positions
                generate = false
            }

            if (positions.size == 0) {
                // no more valid positions, failing...
                return false
            }

            randomindex = math_random(positions.size)
            pos = positions[randomindex]
            nx = pos.x
            ny = pos.y

            if (world.IsFree(nx, ny) && isfree(nx, ny)) {
                remember(x, y, nx, ny)
                x = nx
                y = ny
                world.PlaceStar(x, y)
                itemsleft = itemsleft + 1

                i = i + 1
                if (i > items) {
                    done = true
                }

                generate = true
            }
        } while (done == false)

        // for solution traversing
        solution.add(Vec2i(x, y))

        return true
    }

    private fun math_random(size: Int): Int {
        return rand.nextInt(size)
    }

    internal fun gameover() {
        isAlive = false
        canplaytext = "Game over. Tap to restart."
        sDie.play()
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
        sScore.play()
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

        sStep.play()

        return true
    }

    internal fun update(dt: Float) {
        backgroundTimer += dt * scrollspeed
        while (backgroundTimer > 1.0f) {
            backgroundTimer -= 1.0f
        }

        if (isAlive) {
            if (dx != 0 || dy != 0) {
                if (timer <= 0) {
                    if (step(dx, dy)) {
                        timer += 0.1f
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
                genworld()
            }
        }
    }

    internal val isStopped: Boolean
        get() = dx == 0 && dy == 0

    internal fun dogenworld(): Boolean {
        solution.clear()

        playerx = math_random(width)
        playery = math_random(height)
        isAlive = true
        canplaytext = ""
        playerIndex = math_random(playercount)
        worldIndex = math_random(worldcount)
        backgroundIndex = math_random(3)

        itemsleft = 0

        timer = 0f
        dx = 0
        dy = 0

        world.Clear()
        for (x in 0..width - 1) {
            for (y in 0..height - 1) {
                mem[x][y] = false
            }
        }

        return fillworld()
    }

    fun genworld() {
        var complete = false
        do {
            complete = dogenworld()
        } while (complete == false)
    }

    internal fun transform(x: Int, y: Int): Vector2 {
        val step = size

        val startx = step - step * width / 2.0f
        val starty = step - step * height / 2.0f

        return Vector2(startx + step * (x - 1), starty + step * (y - 1))
    }

    fun drawBackground(batch: SpriteBatch, background: Sprite) {
        background.u = backgroundTimer
        background.u2 = backgroundTimer + 1

        val HEIGHT = 1 / 3.0f
        background.v = backgroundIndex * HEIGHT
        background.v2 = (backgroundIndex + 1) * HEIGHT
        background.draw(batch)
    }

    fun draw(batch: SpriteBatch, world: Array<Sprite>, star: Sprite, player: Array<Sprite>) {
        drawPlayer(batch, player, height)
        for (y in height - 1 downTo 0) {
            drawWorld(batch, world, y)
            drawStars(batch, star, y)
            drawPlayer(batch, player, y)
        }
        drawPlayer(batch, player,-1)
    }

    private fun drawStars(batch: SpriteBatch, star: Sprite, y: Int) {
        for (x in 0..width - 1) {
            if ( !world.IsFree(x, y) ) {
                val p2 = transform(x, y)
                star.setPosition(p2.x, p2.y)
                star.draw(batch)
            }
        }
    }

    private fun drawWorld(batch: SpriteBatch, sprite: Array<Sprite>, y: Int) {
        for (x in 0..width - 1) {
            val p2 = transform(x, y)
            sprite[worldIndex].setPosition(p2.x, p2.y - size * (8.0f / 16.0f))
            sprite[worldIndex].draw(batch)
        }
    }

    private fun drawPlayer(batch: SpriteBatch, player: Array<Sprite>, y: Int) {
        if (y == playery) {
            val p = transform(playerx, playery)
            player[playerIndex].setPosition(p.x, p.y)
            player[playerIndex].draw(batch)
        }
    }

    fun draw_text(batch: SpriteBatch, font: BitmapFont) {
        if (isAlive == false) {
            font.draw(batch, canplaytext, 4f, 15f)
        }
    }
}

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


class Game(internal var sScore: Sound, internal var sStep: Sound, internal var sDie: Sound, val playercount : Int, val worldcount: Int) {

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

    fun isfree(x: Int, y: Int): Boolean {
        return if (playerx == x && playery == y) {
            false
        } else mem!![x][y] == false
    }

    internal lateinit var solution: MutableList<Vec2i>
    private var world: Array<IntArray>? = null
    private var mem: Array<BooleanArray>? = null

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
            mem!![x][y] = true

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
                // print("generated valid positions");
                generate = false
            }

            if (positions.size == 0) {
                // print("no more valid positions, failing...")
                return false
            }

            randomindex = math_random(positions.size)
            pos = positions[randomindex]
            nx = pos.x
            ny = pos.y

            if (world!![nx][ny] == 0 && isfree(nx, ny)) {
                remember(x, y, nx, ny)
                x = nx
                y = ny
                world!![x][y] = 1
                itemsleft = itemsleft + 1

                i = i + 1
                if (i > items) {
                    done = true
                }

                generate = true
                // print "placing item"
            } else {
                // print("invalid suggestion, retrying", #positions)
                // table.remove(positions, randomindex)
            }
        } while (done == false)

        // for solution traversing
        solution.add(Vec2i(x, y))

        return true
    }

    internal var rand = Random()
    private var playerIndex: Int = 0
    private var worldIndex: Int = 0
    private var backgroundIndex: Int = 0

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
        // playSound(sDie);
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
            // star wars!
            // long[] pattern = {0, 500, 110, 500, 110, 450, 110, 200, 110, 170, 40, 450, 110, 200, 110, 170, 40, 500};
            // Gdx.input.vibrate(pattern, -1);
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

        if (world!![playerx][playery] != 0) {
            world!![playerx][playery] = 0
            score()
            return false
        }

        sStep.play()

        // Gdx.input.cancelVibrate();
        // Gdx.input.vibrate(10);

        return true
    }

    internal fun update(dt: Float) {
        backgroundTimer += dt * SCROLLSPEED
        while (backgroundTimer > 1.0f) {
            backgroundTimer -= 1.0f
        }
        /*
		 * if showsolution then solutiontimer = solutiontimer + dt if
		 * solutiontimer > kSolutionTime then solutiontimer = solutiontimer -
		 * kSolutionTime if solutionindex == #solution then solutionindex = 1
		 * else solutionindex = solutionindex +1 end end end
		 */
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

    private var backgroundTimer = 0.0f

    internal fun dogenworld(): Boolean {
        world = Array(width) { IntArray(height) }
        mem = Array(width) { BooleanArray(height) }
        solution = ArrayList()

        playerx = math_random(width)
        playery = math_random(height)
        isAlive = true
        canplaytext = ""
        playerIndex = math_random(playercount)
        worldIndex = math_random(worldcount)
        backgroundIndex = math_random(3)

        // solutionindex = 1;
        // solutiontimer = 0;

        itemsleft = 0

        timer = 0f
        dx = 0
        dy = 0

        for (x in 0..width - 1) {
            for (y in 0..height - 1) {
                world!![x][y] = 0
                mem!![x][y] = false
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
        // float rw = Gdx.graphics.getWidth();
        // float rh = Gdx.graphics.getHeight();

        // float w = 2.0f;
        // float h = 2*(rh/rw);

        val step = SIZE

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
        drawPlayer(batch, player, -1)


        /*
		 * if(showsolution) { for(Iterator<Vec2i> ii = solution.iterator(); ii
		 * != null;) { Vec2i n = ii.next(); int i = n.x; int j = n.y;
		 * if(solutionindex == i ) { love.graphics.setColor(255,0,0, 255) } else
		 * { love.graphics.setColor(0,0,255, 50) } local a,b =
		 * transform(v.x,v.y) love.graphics.rectangle("fill", a, b, 16,16) } }
		 */

        /*
		 * love.graphics.setColor(255,255,255) if canplay == false then
		 * love.graphics.print(canplaytext, 20, 10) end
		 *
		 * love.graphics.setColor(255,255,255, 255) comment this this introduce
		 * a delay/sleep in rendering
		 * love.graphics.print("game & idea by sirGustav, sound by sfxr, music by "
		 * , 0, love.graphics.getHeight() - 15)
		 */
    }

    private fun drawStars(batch: SpriteBatch, star: Sprite, y: Int) {
        for (x in 0..width - 1) {
            if (world!![x][y] != 0) {
                val p2 = transform(x, y)
                star.setPosition(p2.x, p2.y)
                star.draw(batch)
            }
        }
    }

    private fun drawWorld(batch: SpriteBatch, sprite: Array<Sprite>, y: Int) {
        for (x in 0..width - 1) {
            val p2 = transform(x, y)
            sprite[worldIndex].setPosition(p2.x, p2.y - SIZE * (8.0f / 16.0f))
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
        // font.draw(batch, "Helloworld", 4, 15);

        if (isAlive == false) {
            font.draw(batch, canplaytext, 4f, 15f)
        }
    }

    companion object {

        private val SCROLLSPEED = 0.02f

        val SIZE = 0.025f
    }
}
package com.madeso.me.starcollector;

import java.util.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Game {

	private int width = 20;
	private int height = 20;
	private int items = 10;

	final float kSolutionTime = 0.05f;

	int itemsleft = 0;
	int playerx;
	int playery;

	float timer = 0.0f;
	boolean canplay = true;
	String canplaytext = "";
	int dx = 0;
	int dy = 0;
	
	Sound sScore;
	Sound sStep;
	Sound sDie;
	
	public Game(Sound score, Sound step, Sound die) {
		this.sScore = score;
		this.sStep = step;
		this.sDie = die;
	}

	public boolean isfree(int x, int y) {
		if (playerx == x && playery == y) {
			return false;
		}
		return mem[x][y] == false;
	}

	List<Vec2i> solution;
	private int[][] world;
	private boolean[][] mem;

	void remember(int sx, int sy, int nx, int ny) {
		int x = sx, y = sy;
		int dx = 0, dy = 0;

		if (x > nx) {
			dx = -1;
		} else if (x < nx) {
			dx = 1;
		}

		if (y > ny) {
			dy = -1;
		} else if (y < ny) {
			dy = 1;
		}

		do {
			mem[x][y] = true;

			// for solution traversing
			solution.add(new Vec2i(x, y));

			x = x + dx;
			y = y + dy;
		} while (false == (x == nx && y == ny));
	}

	List<Vec2i> listValidPositions(int sx, int sy) {
		ArrayList<Vec2i> r = new ArrayList<Vec2i>();

		for (int x = 0; x < width; ++x) {
			if (x != sx) {
				r.add(new Vec2i(x, sy));
			}
		}

		for (int y = 0; y < height; ++y) {
			if (y != sy) {
				r.add(new Vec2i(sx, y));
			}
		}

		return r;
	}

	boolean fillworld() {
		int x = playerx, y = playery;
		int nx, ny;

		boolean done = false;

		List<Vec2i> positions = new ArrayList<Vec2i>();
		Vec2i pos = null;
		boolean generate = true;
		int randomindex = 0;
		int i = 0;

		do {
			if (generate) {
				positions = listValidPositions(x, y);
				// print("generated valid positions");
				generate = false;
			}

			if (positions.size() == 0) {
				// print("no more valid positions, failing...")
				return false;
			}

			randomindex = math_random(positions.size());
			pos = positions.get(randomindex);
			nx = pos.x;
			ny = pos.y;

			if (world[nx][ny] == 0 && isfree(nx, ny)) {
				remember(x, y, nx, ny);
				x = nx;
				y = ny;
				world[x][y] = 1;
				itemsleft = itemsleft + 1;

				i = i + 1;
				if (i > items) {
					done = true;
				}

				generate = true;
				// print "placing item"
			} else {
				// print("invalid suggestion, retrying", #positions)
				// table.remove(positions, randomindex)
			}
		} while (done == false);

		// for solution traversing
		solution.add(new Vec2i(x, y));

		return true;
	}

	Random rand = new Random();
	private int playerIndex;
	private int worldIndex;

	private int math_random(int size) {
		return rand.nextInt(size);
	}

	void gameover() {
		canplay = false;
		canplaytext = "Game over. Tap to restart.";
		sDie.play();
		System.out.println("FAIL!");
		Gdx.input.cancelVibrate();
		Gdx.input.vibrate(500);
	}

	void win() {
		canplay = false;
		canplaytext = "Level completed. Tap to restart";
		// playSound(sDie);
		System.out.println("WIN!");		
	}

	void score() {
		sScore.play();
		System.out.println("Score!");
		itemsleft = itemsleft - 1;
		if (itemsleft == 0) {
			win();
			Gdx.input.cancelVibrate();
			Gdx.input.vibrate(500);
			// star wars!
			// long[] pattern = {0, 500, 110, 500, 110, 450, 110, 200, 110, 170, 40, 450, 110, 200, 110, 170, 40, 500};
			// Gdx.input.vibrate(pattern, -1); 
		}
		else {
			Gdx.input.cancelVibrate();
			Gdx.input.vibrate(100);
		}
	}

	boolean step(int x, int y) {
		playerx = playerx + x;
		playery = playery + y;

		if (playerx < 0) {
			gameover();
			return false;
		}
		if (playery < 0) {
			gameover();
			return false;
		}

		if (playerx >= width) {
			gameover();
			return false;
		}

		if (playery >= height) {
			gameover();
			return false;
		}

		if (world[playerx][playery] != 0) {
			world[playerx][playery] = 0;
			score();
			return false;
		}

		sStep.play();
		
		// Gdx.input.cancelVibrate();
		// Gdx.input.vibrate(10);

		return true;
	}

	void update(float dt) {
		/*
		 * if showsolution then solutiontimer = solutiontimer + dt if
		 * solutiontimer > kSolutionTime then solutiontimer = solutiontimer -
		 * kSolutionTime if solutionindex == #solution then solutionindex = 1
		 * else solutionindex = solutionindex +1 end end end
		 */
		if (canplay) {
			if (dx != 0 || dy != 0) {
				if (timer <= 0) {
					if (step(dx, dy)) {
						timer += 0.1;
					} else {
						dx = 0;
						dy = 0;
						timer = 0;
					}
				} else {
					timer = timer - dt;
				}
			}
		}
	}

	enum Input {
		left, right, up, down, tap
	}

	public void input(Input input) {
		if (canplay) {
			if (dx != 0 || dy != 0) {
			} else {
				if (input == Input.right) {
					dx = 1;
					dy = 0;
				}

				if (input == Input.left) {
					dx = -1;
					dy = 0;
				}

				if (input == Input.up) {
					dx = 0;
					dy = -1;
				}

				if (input == Input.down) {
					dx = 0;
					dy = 1;
				}

			}
		} else {
			if (input == Input.tap) {
				genworld();
			}
		}
	}
	
	boolean isAlive() {
		return canplay;
	}
	
	boolean isStopped() {
		return dx == 0 && dy==0;
	}

	boolean dogenworld() {
		world = new int[width][height];
		mem = new boolean[width][height];
		solution = new ArrayList<Vec2i>();

		playerx = math_random(width);
		playery = math_random(height);
		canplay = true;
		canplaytext = "";
		playerIndex = math_random(StarCollectorGame.PLAYERCOUNT);
		worldIndex = math_random(StarCollectorGame.WORLDCOUNT);

		// solutionindex = 1;
		// solutiontimer = 0;

		itemsleft = 0;

		timer = 0;
		dx = 0;
		dy = 0;

		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				world[x][y] = 0;
				mem[x][y] = false;
			}
		}

		return fillworld();
	}

	public void genworld() {
		boolean complete = false;
		do {
			complete = dogenworld();
		} while (complete == false);
	}
	
	public static final float SIZE = 0.025f;

	Vector2 transform(int x, int y) {
		// float rw = Gdx.graphics.getWidth();
		// float rh = Gdx.graphics.getHeight();

		// float w = 2.0f;
		// float h = 2*(rh/rw);

		float step = SIZE;

		float startx = step - (step * width) / 2.0f;
		float starty = step - (step * height) / 2.0f;

		return new Vector2(startx + step * (x - 1), starty + step * (y - 1));
	}

	void line(ShapeRenderer shapes, int cr, int cg, int cb, int a, int b,
			int x, int y) {
		Vector2 from = transform(a, b);
		Vector2 to = transform(x, y);
		shapes.setColor(new Color(0.0f, 0.0f, 0.0f, 255.0f));
		shapes.line(from.x, from.y, to.x, to.y);
	}

	public void draw_lines(ShapeRenderer shapes) {
		for (int x = 0; x < width + 1; ++x) {
			line(shapes, 0, 0, 0, x, 0, x, height);
		}
		for (int y = 0; y < height + 1; ++y) {
			line(shapes, 0, 0, 0, 0, y, width, y);
		}
	}

	public void draw(SpriteBatch batch, Sprite[] world, Sprite star, Sprite[] player) {
		drawPlayer(batch, player, height);
		for (int y = height-1; y >= 0; --y) {
			drawWorld(batch, world, y);
			drawStars(batch, star, y);
			drawPlayer(batch, player, y);
		}
		drawPlayer(batch, player, -1);
		

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

	private void drawStars(SpriteBatch batch, Sprite star, int y) {
		for (int x = 0; x < width; ++x) {			
			if (world[x][y] != 0) {
				Vector2 p2 = transform(x, y);
				star.setPosition(p2.x, p2.y);
				star.draw(batch);
			}
		}
	}
	
	private void drawWorld(SpriteBatch batch, Sprite[] sprite, int y) {
		for (int x = 0; x < width; ++x) {
			Vector2 p2 = transform(x, y);
			sprite[worldIndex].setPosition(p2.x, p2.y - SIZE * (8.0f/16.0f));
			sprite[worldIndex].draw(batch);
		}
	}

	private void drawPlayer(SpriteBatch batch, Sprite[] player, int y) {
		if( y == playery ) {
			Vector2 p = transform(playerx, playery);
			player[playerIndex].setPosition(p.x, p.y);
			player[playerIndex].draw(batch);
		}
	}
	
	public void draw_text(SpriteBatch batch, BitmapFont font) {
		// font.draw(batch, "Helloworld", 4, 15);
		
		if ( canplay == false ) {
			font.draw(batch, canplaytext, 4, 15);
		}
	}
}

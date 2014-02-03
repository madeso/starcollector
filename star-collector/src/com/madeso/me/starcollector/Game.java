package com.madeso.me.starcollector;

import java.util.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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
	
	public boolean isfree(int x, int y) {
		if (playerx ==x && playery == y) {
			return false;
		}
		return mem[x][y] == false;
	}
	
	List<Vec2i> solution;
	private int[][] world;
	private boolean[][] mem;

	void remember(int sx,int sy,int nx,int ny) {
		int x=sx,y=sy;
		int dx=0,dy=0;
		
		if(x > nx) {
			dx = -1;
		} else if(x < nx) {
			dx = 1;
			}
		
		if(y > ny) {
			dy = -1;
		} else if(y < ny) {
			dy = 1;
			}
		
		do {
			mem[x][y] = true;
			
			// for solution traversing
			solution.add(new Vec2i(x,y));
			
			x=x+dx;
			y=y+dy;
		} while( false==(x==nx && y==ny) );
	}

	List<Vec2i> listValidPositions(int sx, int sy) {
		ArrayList<Vec2i> r = new ArrayList<Vec2i>();
		
		for( int x=0; x<width; ++x) {
			if(x != sx ) {
				r.add(new Vec2i(x,sy));
			}
		}
		
		for( int y=0; y<height; ++y) {
			if(y != sy ) {
				r.add(new Vec2i(sx,y));
			}
		}
		
		return r;
	}

	boolean fillworld() {
		int x=playerx, y=playery;
		int nx, ny;
		
		boolean done = false;
		
		List<Vec2i> positions = new ArrayList<Vec2i>();
		Vec2i pos = null;
		boolean generate = true;
		int randomindex = 0;
		int i = 0;
		
		do {
			if(generate) {
				positions = listValidPositions(x,y);
				// print("generated valid positions");
				generate = false;
			}
			
			if (positions.size() == 0 ) {
				// print("no more valid positions, failing...")
				return false;
			}
			
			randomindex = math_random(positions.size());
			pos = positions.get(randomindex);
			nx = pos.x;
			ny = pos.y;
			
			if(world[nx][ny] == 0 && isfree(nx,ny)) {
				remember(x,y,nx,ny);
				x = nx;
				y = ny;
				world[x][y] = 1;
				itemsleft = itemsleft + 1;
				
				i = i+1;
				if(i>items) {
					done = true;
				}
				
				generate = true;
				// print "placing item"
			} else {
				// print("invalid suggestion, retrying", #positions)
				// table.remove(positions, randomindex)
			}
		} while(done == false);
		
		// for solution traversing
		solution.add(new Vec2i(x,y));
		
		return true;
	}

	Random rand = new Random();
	private int math_random(int size) {
		return rand.nextInt(size);
	}

	boolean dogenworld() {
		world = new int[width][height];
		mem = new boolean[width][height];
		solution = new ArrayList<Vec2i>();
		
		playerx = math_random(width);
		playery = math_random(height);
		// canplay = true;
		// canplaytext = "";
		
		// solutionindex = 1;
		// solutiontimer = 0;
		
		itemsleft = 0;
		
		// timer = 0
		//dx = 0
		//dy = 0
		
		for(int x=0; x<width; ++x) {
			for(int y=0; y<height; ++y) {
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
		} while(complete == false);
	}
	
	Vector2 transform(int x, int y) {
		// float rw = Gdx.graphics.getWidth();
		// float rh = Gdx.graphics.getHeight();
		
		// float w = 2.0f;
		// float h = 2*(rh/rw);
		
		float step = 0.03f;
		
		float startx = 0 - (step*width)/2.0f;
		float starty = 0 - (step*height)/2.0f;
		
		return new Vector2(startx+step*(x-1),starty+step*(y-1));
	}

	void line(ShapeRenderer shapes, int cr, int cg, int cb, int a, int b, int x, int y) {
		Vector2 from = transform(a,b);
		Vector2 to = transform(x,y);
		shapes.setColor(new Color(0.0f, 0.0f, 0.0f, 255.0f));
		shapes.line(from.x, from.y, to.x, to.y);
	}
	
	public void draw_lines(ShapeRenderer shapes) {
		for(int x=1; x<=width+1; ++x) {
			line(shapes, 0,0,0, x,1,x,height+1);
		}
		for(int y=1; y<=height+1; ++y) {
			line(shapes, 0,0,0, 1,y,width+1,y);
		}
	}

	public void draw(SpriteBatch batch, Sprite star, Sprite player) {
		for(int x=1; x<=width; ++x) {
			for(int y=1;y<=height;++y) {
				if(world[x-1][y-1] != 0) {
					Vector2 p2 = transform(x, y);
					star.setPosition(p2.x, p2.y);
					star.draw(batch);
				}
			}
		}
		
		Vector2 p = transform(playerx, playery);
		player.setPosition(p.x, p.y);
		player.draw(batch);
		
		/*if(showsolution) {
			for(Iterator<Vec2i> ii = solution.iterator(); ii != null;) {
				Vec2i n = ii.next();
				int i = n.x;
				int j = n.y;
				if(solutionindex == i ) {
					love.graphics.setColor(255,0,0, 255)
				} else {
					love.graphics.setColor(0,0,255, 50)
				}
				local a,b = transform(v.x,v.y)
				love.graphics.rectangle("fill", a, b, 16,16)
			}
		}*/
		
		/*
		love.graphics.setColor(255,255,255)
		if canplay == false then
		love.graphics.print(canplaytext, 20, 10)
		end
		
		love.graphics.setColor(255,255,255, 255)
		comment this this introduce a delay/sleep in rendering
		love.graphics.print("game & idea by sirGustav, sound by sfxr, music by ", 0, love.graphics.getHeight() - 15)
		*/
	}
}
package com.madeso.me.starcollector;

import java.util.*;

import com.badlogic.gdx.Gdx;

public class Game {

	private int width = 20;
	private int height = 20;
	private int items = 10;

	final float kSolutionTime = 0.05f;

	public boolean isfree(int x, int y) {
		if (playerx ==x && playery == y) {
			return false;
		}
		return mem[x][y] == false;
	}
	
	List<Vec2i> solution;

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
			solution.add(new Vec21(x,y));
			
			x=x+dx;
			y=y+dy;
		while( false==(x==nx and y==ny) );
	}

	List<Vec2i> listValidPositions(int sx, int sy) {
		ArrayList<Vec2i> r = new ArrayList<Vec2i>();
		
		for( int x=1; x<=width; ++x) {
			if(x != sx ) {
				r.add(new Vec2i(x,sy));
			}
		}
		
		for( int y=1; y<=height; ++y) {
			if(y != sy ) {
				r.add(new Vec2i(sx,y));
			}
		}
		
		return r;
	}

	boolean fillworld()
		int x=playerx, y=playery;
		int nx, ny;
		
		local done = false
		
		local positions = {}
		local pos = {}
		local generate = true
		local randomindex = 0
		local i = 0
		
		repeat
			if generate then
				positions = listValidPositions(x,y)
				print("generated valid positions")
				generate = false
			end
			
			if #positions == 0 then
				print("no more valid positions, failing...")
				return false
			end
			
			
			randomindex = math.random(#positions)
			pos = positions[ randomindex ]
			nx,ny = pos.x, pos.y
			
			if world[nx][ny] == 0 and isfree(nx,ny) then
				remember(x,y,nx,ny)
				x,y = nx,ny
				world[x][y] = 1
				itemsleft = itemsleft + 1
				
				i = i+1
				if i>items then
					done = true
				end
				
				generate = true
				print "placing item"
			else
				print("invalid suggestion, retrying", #positions)
				table.remove(positions, randomindex)
			end
		until done
		
		-- for solution traversing
		p = {}
		p.x=x
		p.y=y
		table.insert(solution, p)
		
		return true;
	}

	boolean dogenworld()
		world = {}
		mem = {}
		solution = {}
		
		playerx = math.random(width)
		playery = math.random(height)
		canplay = true
		canplaytext = ""
		
		solutionindex = 1
		solutiontimer = 0
		
		itemsleft = 0
		
		timer = 0
		dx = 0
		dy = 0
		
		for(int x=1; x<=width;++x) {
			local temp = {}
			local t = {}
			for y=1,height do
				temp[y] = 0
				t[y] = false
			end
			world[x] = temp
			mem[x] = t
		}
		
		return fillworld();
	}

	void genworld()
		bool complete = false;
		do { 
			complete = dogenworld();
		} while(complete == false);
	}
	
	Vec2i transform(int x, int y) {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		int step = 16;
		
		int startx = w/2.0 - (step*width)/2.0
		int starty = h/2.0 - (step*height)/2.0
		
		return new Vec2i(startx+step*(x-1),starty+step*(y-1));
	end

	function line(a,b, x,y)
		a,b = transform(a,b)
		x,y = transform(x,y)
		love.graphics.line(a,b,x,y)
	end

	void draw()
		love.graphics.setColor(0,0,0)
		
		for x=1,width+1 do
			line(x,1,x,height+1)
		end
		for y=1,height+1 do
			line(1,y,width+1,y)
		end
		
		love.graphics.setColor(255,255,255)
		love.graphics.draw(player, transform(playerx, playery))
		
		love.graphics.setColor(255,255,255)
		for x=1,width do
			for y=1,height do
				if world[x][y] ~= 0 then
					love.graphics.draw(item, transform(x, y))
				end
			end
		end
		
		if showsolution then
			--local v = solution[solutionindex]
			for i,v in ipairs(solution) do
				if solutionindex == i then
					love.graphics.setColor(255,0,0, 255)
				else
					love.graphics.setColor(0,0,255, 50)
				end
				local a,b = transform(v.x,v.y)
				love.graphics.rectangle("fill", a, b, 16,16)
			end
		end
		
		love.graphics.setColor(255,255,255)
		if canplay == false then
			love.graphics.print(canplaytext, 20, 10)
		end
		
		love.graphics.setColor(255,255,255, 255)
		-- comment this this introduce a delay/sleep in rendering
		love.graphics.print("game & idea by sirGustav, sound by sfxr, music by ", 0, love.graphics.getHeight() - 15)
	end
}

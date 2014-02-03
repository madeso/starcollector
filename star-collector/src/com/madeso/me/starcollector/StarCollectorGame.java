package com.madeso.me.starcollector;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class StarCollectorGame implements ApplicationListener {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	
	Texture starTexture;
	Sprite starSprite;
	
	Texture playerTexture;
	Sprite playerSprite;
	
	Game game = new Game();
	
	ShapeRenderer shapes;
	
	@Override
	public void create() {		
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		camera = new OrthographicCamera(1, h/w);
		batch = new SpriteBatch();
		
		starTexture = CreateTexture("data/star.png");
		starSprite = new Sprite(new TextureRegion(starTexture));
		starSprite.setSize(0.03f,  0.03f);
		starSprite.setOrigin(starSprite.getWidth()/2, starSprite.getHeight()/2);
		
		playerTexture = CreateTexture("data/player.png");
		playerSprite = new Sprite(new TextureRegion(playerTexture));
		playerSprite.setSize(0.03f,  0.03f);
		playerSprite.setOrigin(playerSprite.getWidth()/2, playerSprite.getHeight()/2);
		
		shapes = new ShapeRenderer();
		
		game.genworld();
	}

	private static Texture CreateTexture(String path) {
		Texture texture = new Texture(Gdx.files.internal(path));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		return texture;
	}

	@Override
	public void dispose() {
		batch.dispose();
		starTexture.dispose();
		playerTexture.dispose();
	}

	@Override
	public void render() {		
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		batch.setProjectionMatrix(camera.combined);
		shapes.setProjectionMatrix(camera.combined);
		
		shapes.begin(ShapeType.Line);
		game.draw_lines(shapes);
		shapes.end();
		
		batch.begin();
		game.draw(batch, starSprite, playerSprite);
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}

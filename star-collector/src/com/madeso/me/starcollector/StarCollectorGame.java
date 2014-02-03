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

public class StarCollectorGame implements ApplicationListener {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	
	Texture starTexture;
	Sprite starSprite;
	
	Texture playerTexture;
	Sprite playerSprite;
	
	@Override
	public void create() {		
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		camera = new OrthographicCamera(1, h/w);
		batch = new SpriteBatch();
		
		starTexture = CreateTexture("data/star.png");
		starSprite = new Sprite(new TextureRegion(starTexture));
		starSprite.setSize(0.02f,  0.02f);
		starSprite.setOrigin(starSprite.getWidth()/2, starSprite.getHeight()/2);
		
		playerTexture = CreateTexture("data/star.png");
		playerSprite = new Sprite(new TextureRegion(playerTexture));
		playerSprite.setSize(0.02f,  0.02f);
		playerSprite.setOrigin(playerSprite.getWidth()/2, playerSprite.getHeight()/2);
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
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
		starSprite.setPosition(0.010f,  0.010f);
		starSprite.draw(batch);
		
		starSprite.setPosition(0.05f, 0.05f);
		starSprite.draw(batch);
		
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

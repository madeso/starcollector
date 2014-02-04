package com.madeso.me.starcollector;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class StarCollectorGame implements ApplicationListener {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private SpriteBatch fontbatch;

	Texture starTexture;
	Sprite starSprite;

	Texture playerTexture;
	Sprite playerSprite;

	Game game = new Game();

	ShapeRenderer shapes;
	private BitmapFont font;

	@Override
	public void create() {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		camera = new OrthographicCamera(1, h / w);
		batch = new SpriteBatch();
		fontbatch = new SpriteBatch();

		starTexture = CreateTexture("data/star.png");
		starSprite = new Sprite(new TextureRegion(starTexture));
		starSprite.setSize(0.03f, 0.03f);
		starSprite.setOrigin(starSprite.getWidth() / 2,
				starSprite.getHeight() / 2);

		playerTexture = CreateTexture("data/player.png");
		playerSprite = new Sprite(new TextureRegion(playerTexture));
		playerSprite.setSize(0.03f, 0.03f);
		playerSprite.setOrigin(playerSprite.getWidth() / 2,
				playerSprite.getHeight() / 2);

		shapes = new ShapeRenderer();
		
		font = new BitmapFont(); // BitmapFont(Gdx.files.internal("Calibri.fnt"),Gdx.files.internal("Calibri.png"),false);
		
		System.out.println( font.getLineHeight() );

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
		fontbatch.dispose();
		starTexture.dispose();
		playerTexture.dispose();
	}

	private boolean touchdown = false;
	private Vector3 touchpos = null;

	@Override
	public void render() {
		game.update(Gdx.graphics.getDeltaTime());

		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		Matrix4 normalProjection = new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(),  Gdx.graphics.getHeight());
		fontbatch.setProjectionMatrix(normalProjection);

		batch.setProjectionMatrix(camera.combined);
		shapes.setProjectionMatrix(camera.combined);
		// fontbatch.setProjectionMatrix(camera.combined);

		shapes.begin(ShapeType.Line);
		game.draw_lines(shapes);
		shapes.end();

		batch.begin();
		game.draw(batch, starSprite, playerSprite);
		batch.end();
		
		fontbatch.begin();
		font.setColor(0, 0, 0, 1.0f);
		game.draw_text(fontbatch, font);
		fontbatch.end();

		float diff = 0.03f;
		int segments = 50;

		if (Gdx.input.isTouched()) {
			if (touchdown == false) {
				touchdown = true;
				touchpos = getTouchPosScreen();
			}

			shapes.begin(ShapeType.Line);
			shapes.setColor(0, 0, 1, 0.20f);
			Vector3 rtouchPos = new Vector3(touchpos);
			shapes.circle(rtouchPos.x, rtouchPos.y, diff, segments);

			Vector3 newTouchPos = getTouchPosScreen();
			Vector3 dist = newTouchPos.sub(touchpos);
			dist.y = -dist.y;
			float d = dist.len();

			if (d > diff) {
				shapes.setColor(0, 1, 0, 0.5f);
				shapes.circle(rtouchPos.x, rtouchPos.y, d, segments);
			}
			shapes.end();
		} else {
			if (touchdown) {
				touchdown = false;
				Vector3 newTouchPos = getTouchPosScreen();
				Vector3 dist = newTouchPos.sub(touchpos);
				dist.y = -dist.y;
				float d = dist.len();

				dist = dist.scl(1.0f / diff);

				int dir = Maths.Classify(dist.x, dist.y);

				switch (dir) {
				case 5:
					game.input(Game.Input.tap);
					break;
				case 4:
					game.input(Game.Input.left);
					break;
				case 6:
					game.input(Game.Input.right);
					break;
				case 8:
					game.input(Game.Input.up);
					break;
				case 2:
					game.input(Game.Input.down);
					break;
				}

				if (d > 1.0f) {
					Gdx.input.vibrate(100);
				}
			}
		}
	}

	private Vector3 getTouchPosScreen() {
		Vector3 touchPos = new Vector3();
		touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		camera.unproject(touchPos);
		return touchPos;
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

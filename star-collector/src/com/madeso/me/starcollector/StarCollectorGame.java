package com.madeso.me.starcollector;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
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
	Texture[] playerTexture;
	Texture allTexture;
	Texture leftTexture;
	Texture upTexture;
	Texture rightTexture;
	Texture downTexture;
	Texture notTexture;
	
	Texture[] worldTexture;
	
	public static final int PLAYERCOUNT = 5;
	public static final int WORLDCOUNT = 10;
	
	Sprite starSprite;
	Sprite[] playerSprite;
	Sprite allSprite;
	Sprite leftSprite;
	Sprite upSprite;
	Sprite rightSprite;
	Sprite downSprite;
	Sprite notSprite;
	
	Sprite[] worldSprite;

	Game game;

	ShapeRenderer shapes;
	private BitmapFont font;
	private Sound sndScore;
	private Sound sndStep;
	private Sound sndDie;
	private Music music;
	
	private final float BUTTONSIZE = 0.1f;

	@Override
	public void create() {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		camera = new OrthographicCamera(1, h / w);
		batch = new SpriteBatch();
		fontbatch = new SpriteBatch();
		
		starSprite = createSprite(starTexture = CreateTexture("data/star.png"), Game.SIZE);
		
		playerSprite = new Sprite[PLAYERCOUNT];
		playerTexture = new Texture[PLAYERCOUNT];
		for(int p=0; p<PLAYERCOUNT; ++p) {
			playerSprite[p] = createSprite(playerTexture[p] = CreateTexture("player/" + Integer.toString(p+1) + ".png"), Game.SIZE * 2);
		}
		
		allSprite = createSprite(allTexture = CreateTexture("input/all.png"), BUTTONSIZE*2);
		leftSprite = createSprite(leftTexture = CreateTexture("input/left.png"), BUTTONSIZE);
		upSprite = createSprite(upTexture = CreateTexture("input/up.png"), BUTTONSIZE);
		rightSprite = createSprite(rightTexture = CreateTexture("input/right.png"), BUTTONSIZE);
		downSprite = createSprite(downTexture = CreateTexture("input/down.png"), BUTTONSIZE);
		notSprite = createSprite(notTexture = CreateTexture("input/not.png"), BUTTONSIZE);
		
		worldSprite = new Sprite[WORLDCOUNT];
		worldTexture = new Texture[WORLDCOUNT];
		for(int w1=0; w1<WORLDCOUNT; ++w1) {
			worldSprite[w1] = createSprite(worldTexture[w1] = CreateTexture("world/" +Integer.toString(w1+1)+ "-mid.png"), Game.SIZE*2);
		}

		shapes = new ShapeRenderer();
		
		sndScore = Gdx.audio.newSound(Gdx.files.internal("data/score.wav"));
		sndStep = Gdx.audio.newSound(Gdx.files.internal("data/step.wav"));
		sndDie = Gdx.audio.newSound(Gdx.files.internal("data/die.wav"));
		
		music = Gdx.audio.newMusic(Gdx.files.internal("data/Malloga_Ballinga_Mastered_mp_0.mp3"));
		
		font = new BitmapFont(); // BitmapFont(Gdx.files.internal("Calibri.fnt"),Gdx.files.internal("Calibri.png"),false);
		
		music.setVolume(0.5f);
		music.setLooping(true);
		music.play();
		
		game = new Game(sndScore, sndStep, sndDie);
		game.genworld();
	}

	private static Sprite createSprite(Texture texture, float size) {
		Sprite sprite = new Sprite(new TextureRegion(texture));
		sprite.setSize(size, size);
		sprite.setOrigin(sprite.getWidth() / 2,
				sprite.getHeight() / 2);
		return sprite;
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
		for(int p=0; p<PLAYERCOUNT; ++p) {
			playerTexture[p].dispose();
		}
		
		allTexture.dispose();
		leftTexture.dispose();
		upTexture.dispose();
		rightTexture.dispose();
		downTexture.dispose();
		notTexture.dispose();
		
		for(int w1=0; w1<WORLDCOUNT; ++w1) {
			worldTexture[w1].dispose();
		}
		
		font.dispose();
		sndScore.dispose();
		sndStep.dispose();
		sndDie.dispose();
		music.stop();
		music.dispose();
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

		batch.begin();
		game.draw(batch, worldSprite, starSprite, playerSprite);
		batch.end();
		
		/*shapes.begin(ShapeType.Line);
		game.draw_lines(shapes);
		shapes.end();*/
		
		fontbatch.begin();
		font.setColor(0, 0, 0, 1.0f);
		game.draw_text(fontbatch, font);
		fontbatch.end();

		float diff = 0.04f;

		if (Gdx.input.isTouched(0)) {
			if (touchdown == false) {
				touchdown = true;
				touchpos = getTouchPosScreen();
			}

			Vector3 rtouchPos = new Vector3(touchpos);

			Vector3 newTouchPos = getTouchPosScreen();
			Vector3 dist = newTouchPos.sub(touchpos);
			dist.y = -dist.y;
			
			dist = dist.scl(1.0f / diff);

			int dir = Maths.Classify(dist.x, dist.y);
			
			if( game.isAlive() ) {
				Sprite icon = notSprite;
				if( game.isStopped() ) {
					switch(dir) {
					case 5:
						icon = allSprite;
						break;
					case 4:
						icon = leftSprite;
						break;
					case 8:
						icon = downSprite;
						break;
					case 6:
						icon = rightSprite;
						break;
					case 2:
						icon = upSprite;
						break;
					}
				}
				icon.setPosition(rtouchPos.x - icon.getWidth()/2, rtouchPos.y - icon.getHeight()/2);
				batch.begin();
				icon.draw(batch);
				batch.end();
			}
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
				}
			}
		}
	}

	private Vector3 getTouchPosScreen() {
		Vector3 touchPos = new Vector3();
		touchPos.set(Gdx.input.getX(0), Gdx.input.getY(0), 0);
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

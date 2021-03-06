package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;


import java.util.ArrayList;
import java.util.List;


public class MyGdxGame extends ApplicationAdapter implements InputProcessor {
	private final Runnable mPlayNegative;
	private OrthographicCamera cam;
	private SpriteBatch batch;
	private SpriteBatch batchLarge;
	private OrthographicCamera camLarge;

	public static final float ANIMATION_SPEED = 3;
	Runnable mOnQuizEnd;
	Runnable mPlayPositive;
	public MyGdxGame(Runnable onQuizEnd, Runnable playPositive, Runnable playNegative) {
		mOnQuizEnd = onQuizEnd;
		mPlayPositive = playPositive;
		mPlayNegative = playNegative;
	}


	int currentQuestion = 0;
	Question[] mQuestions;
	Baba baba;
	List<Answer> answers;
	private Question mLastQuestion;

	Question getCurrentQuestion() {
		return mQuestions[currentQuestion%mQuestions.length];
	}
	
	List<PositionedTexture> mTextures = new ArrayList<PositionedTexture>();
	DropBox dropBox;

	void loadQuestion() {
		mQuestions = new Question[] {
				new Question("ans_1_a.png", "ans_1_b.png", "ans_1_c.png", "pytanie_1.png", true, false, false),
				new Question("ans_2_a.png", "ans_2_b.png", "ans_2_c.png", "pytanie_2.png", false, true, false),
				new Question("ans_3_a.png", "ans_3_b.png", "ans_3_c.png", "pytanie_3.png", true, false, false)
		};
	}

	void onQuizEnd() {
		mOnQuizEnd.run();
	}
	int left = 0;
	int top = 0;

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);


		cam.viewportWidth = 1280;
		cam.viewportHeight = 800;
		camLarge.viewportWidth = width;
		camLarge.viewportHeight = height;
		camLarge.position.set(camLarge.viewportWidth / 2f, camLarge.viewportHeight / 2f, 0);

		cam.update();
		camLarge.update();
	}


	private boolean m_fboEnabled = true;
	private FrameBuffer m_fbo = null;
	private TextureRegion m_fboRegion = null;
	@Override
	public void render() {
		// | GL20.GL_DEPTH_BUFFER_BIT);

		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();

		if(m_fboEnabled)      // enable or disable the supersampling
		{
			if(m_fbo == null)
			{
				// m_fboScaler increase or decrease the antialiasing quality

				m_fbo = new FrameBuffer(Pixmap.Format.RGBA8888, (int)(1280), (int)(800), false);
				m_fboRegion = new TextureRegion(m_fbo.getColorBufferTexture());
				m_fboRegion.flip(false, true);
			}

			m_fbo.begin();
			Gdx.gl.glClearColor(0,0,0,0);

			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			// | GL20.GL_DEPTH_BUFFER_BIT);
		}

		// this is the main render function
		renderToTexture();



		if(m_fbo != null)
		{
			m_fbo.end();

			Gdx.gl.glClearColor(1, 1, 1, 1);

			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

			batchLarge.begin();



			float ratio = 800f/1280f;
			float height2 = width*ratio;
			float y = height-height2;
			y/=2;


			float tr = width/(float)mTextBottom.getWidth();
			float fh = mTextBottom.getHeight()* tr;


			batchLarge.draw(mTextBottom,0,0, width, fh);
			tr = width/(float)mTextTop.getWidth();
			fh = mTextTop.getHeight()* tr;
			batchLarge.draw(mTextTop,0,height-fh, width, fh);
			batchLarge.draw(m_fboRegion, 0, y, width, height2);


			batchLarge.end();
		}
	}

	public void renderToTexture () {


				answers = getCurrentQuestion().getAnswers();
		AnimatablePositionedTexture question = getCurrentQuestion().getQuestion();

		batch.begin();
		cam.update();
		camLarge.update();

		batch.setProjectionMatrix(cam.combined);
		batchLarge.setProjectionMatrix(camLarge.combined);
		for(PositionedTexture positionedTexture : mTextures) {
			positionedTexture.draw(batch);
		}

		dropBox.draw(batch);
		question.draw(batch);

		float deltaTime = Gdx.graphics.getDeltaTime();
		if(mLastQuestion!=null) {
			if(mLastQuestion.isAnimating()) {
				mLastQuestion.animate(deltaTime);
				mLastQuestion.draw(batch);
				if(!mLastQuestion.isAnimating()) {
					mLastQuestion.reset();
					mLastQuestion = null;
				}
			} else {
			}
		}
		for(Answer answer : answers) {
			answer.draw(batch);
		}

		baba.draw(batch);

		batch.end();

		dropBox.isWrong((int) (deltaTime * 1000), new Runnable() {
			@Override
			public void run() {
				baba.state = Baba.State.CZEKA;
			}
		});
		dropBox.isGood((int) (deltaTime * 1000), new Runnable() {
			@Override
			public void run() {
				getCurrentQuestion().startAnimationOut();
				mLastQuestion = getCurrentQuestion();
				currentQuestion++;
				if(currentQuestion==3) {
					onQuizEnd();
					return;
				}
				getCurrentQuestion().startAnimationIn();
				dropBox.reset();
				baba.state = Baba.State.CZEKA;
			}
		});
		for(Answer answer : answers) {
			answer.animate(deltaTime);
		}
		question.animate(deltaTime);
	}
	Texture mTextTop;
	Texture mTextBottom;
	@Override
	public void create () {
		batch = new SpriteBatch();
		batchLarge = new SpriteBatch();
		PositionedTexture.screenHeight = 800;
		mTextTop = new Texture("pytanie_bg_top.png");
		mTextBottom = new Texture("pytanie_bg_bottom.jpg");
		loadQuestion();

		baba = new Baba(
				new PositionedTexture("baba_czeka.png", 76,235),
				new PositionedTexture("baba_dobra.png", 12,200),
				new PositionedTexture("baba_zla.png", 87,225)
		);

		dropBox = new DropBox(263,107,  new Texture("chmurka_shape.png"), new Texture("zla_odp.png"), new Texture("answer_blue.png"));
		Gdx.input.setInputProcessor(this);

		cam = new OrthographicCamera(1200, 800);
		camLarge = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
		camLarge.position.set(camLarge.viewportWidth / 2f, camLarge.viewportHeight / 2f, 0);
		cam.update();
		camLarge.update();
	}




	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		Gdx.app.log("input", "touchDown");
		if(dropBox.isWrong()) {
			return false;
		}

//		int x = (int) (((float)screenX/(float)Gdx.graphics.getWidth())* cam.viewportWidth);
//		int y = (int) ((1- ((float)screenY/(float)Gdx.graphics.getHeight()))* cam.viewportHeight);

		int x=screenX;
		int y=800-screenY;

		for(Answer answer : answers) {
			if (answer.contains(x,y)) {
				answer.startDrag(x,y);
				return false;
			}
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		Gdx.app.log("input", "touchUp");
//		int x = (int) (((float)screenX/(float)Gdx.graphics.getWidth())* cam.viewportWidth);
//		int y = (int) ((1- ((float)screenY/(float)Gdx.graphics.getHeight()))* cam.viewportHeight);

		int x=screenX;
		int y=800-screenY;

		for(final Answer answer : answers) {
			answer.onEndDrag(x,y, new Answer.EndDragCallback() {
				@Override
				public void onEndDrag(int endX, int endY) {
					if (dropBox.contains(endX, endY)) {
						if (answer.isCorrect()) {
							answer.startAnimation(answer.getPosition(), dropBox.getPosition(), 5f);
							baba.state = Baba.State.ZADOWOLONA;
							answer.showGood();
							dropBox.setGood();
							mPlayPositive.run();
						} else {
							answer.startAnimation(answer.getPosition(), answer.getStartPosition(), 4f);
							baba.state = Baba.State.ZLA;
							dropBox.setWrong();
							mPlayNegative.run();
						}


					} else {
						answer.startAnimation(answer.getPosition(), answer.getStartPosition(), 1.2f);
						baba.state = Baba.State.CZEKA;
					}
				}
			});
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
//		int x = (int) (((float)screenX/(float)Gdx.graphics.getWidth())* cam.viewportWidth);
//		int y = (int) ((1- ((float)screenY/(float)Gdx.graphics.getHeight()))* cam.viewportHeight);
		int x=screenX;
		int y=800-screenY;

		for(Answer answer : answers) {
			answer.onDrag(x,y);
		}
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	@Override
	public void dispose() {
		super.dispose();
	}
}

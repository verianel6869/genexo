package com.mygdx.game;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AppcompatAndroidApplication;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.model.UserData;
import com.mygdx.game.util.SoundHelper;

public class AndroidLauncher extends AndroidApplication {
	UserData mUserData;
	private View touchView;
	SoundHelper mSoundHelper;
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mSoundHelper = SoundHelper.getInstance(this);
		mSoundHelper.load(R.raw.positive);
		mSoundHelper.load(R.raw.negative);


		setContentView(R.layout.quiz_activity);

		if(getIntent()!=null) {
			mUserData = (UserData) getIntent().getSerializableExtra("user_data");
		//	Log.d("user data", mUserData.toString());
		}



		AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
		final MyGdxGame myGdxGame = new MyGdxGame(new Runnable() {
			@Override
			public void run() {
				onQuizEnd();
			}
		}, new Runnable(){
			@Override
			public void run() {
				mSoundHelper.play(R.raw.positive);
			}
		}, new Runnable(){
			@Override
			public void run() {
				mSoundHelper.play(R.raw.negative);
			}
		});

		View mGdxView = initializeForView(myGdxGame, cfg);

		touchView = new View(this);
		//touchView.setBackgroundColor(0xff0f0fff);
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;
		Log.d("size", width+" x "+height);

		float ratio = 800f/1280f; // h/w
		int heightNew = (int)(ratio * width);


		Log.d("size", width + " x " + heightNew);
		touchView.setLayoutParams(new FrameLayout.LayoutParams(width, heightNew, Gravity.CENTER));

		FrameLayout container = (FrameLayout) findViewById(R.id.contianer);
		container.addView(mGdxView);
		container.addView(touchView);

		touchView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int x = (int) (1280 * event.getX()/ v.getWidth());
				int y = (int) (800* event.getY() / v.getHeight());
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						myGdxGame.touchDown(x,y,0,0);
						Log.d("xy", " ACTION_DOWN x="+x+" y="+y);
						return true;
					case MotionEvent.ACTION_MOVE:
						myGdxGame.touchDragged(x,y,0);
						Log.d("xy", " ACTION_MOVE x="+x+" y="+y);
						return true;
					case MotionEvent.ACTION_UP:
						myGdxGame.touchUp(x, y, 0, 0);
						Log.d("xy", " ACTION_UP x="+x+" y="+y);
						return true;
				}
				return false;
			}
		});
	}


	@Override
	protected void onResume() {
		super.onResume();
		if(touchView!=null) {
			Display display = getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			int width = size.x;
			int height = size.y;
			Log.d("size", width+" x "+height);

			float ratio = 800f/1280f; // h/w
			int heightNew = (int)(ratio * width);


			Log.d("size", width + " x " + heightNew);
			touchView.setLayoutParams(new FrameLayout.LayoutParams(width, heightNew, Gravity.CENTER));
		}
	}

	private void onQuizEnd() {
		Intent intent = new Intent(this, SendingDataActivity.class);
		intent.putExtra("user_data", mUserData);
		finish();
		startActivity(intent);
	}
}

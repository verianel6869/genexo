package com.mygdx.game;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.mygdx.game.model.UserData;
import com.mygdx.game.util.SoundHelper;

/**
 * Created by Radek on 28.02.2016.
 */
public class PodiumActivity extends AndroidApplication {
    UserData mUserData;
    SoundHelper mSoundHelper;
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSoundHelper = SoundHelper.getInstance(this);
        mSoundHelper.load(R.raw.brawo);


        setContentView(R.layout.quiz_activity);

        if(getIntent()!=null) {
            mUserData = (UserData) getIntent().getSerializableExtra("user_data");
            //	Log.d("user data", mUserData.toString());
        }



        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        final PodiumGame myGdxGame = new PodiumGame(new Runnable(){
            @Override
            public void run() {
                mSoundHelper.play(R.raw.brawo);
            }
        });

        View mGdxView = initializeForView(myGdxGame, cfg);

        View click = new View(this);
        click.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));




        FrameLayout container = (FrameLayout) findViewById(R.id.contianer);
        container.addView(mGdxView);
        container.addView(click);
        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent startIntent = new Intent(PodiumActivity.this, ZapraszamyActivity.class);
                startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startIntent);
            }
        });

    }



    private void onQuizEnd() {
        Intent intent = new Intent(this, SendingDataActivity.class);
        intent.putExtra("user_data", mUserData);
        finish();
        startActivity(intent);
    }
}
package com.tiffin_dabba.admin.tiffindabba;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class SplashScreen extends AppCompatActivity {

    Handler handler = new Handler();
    int pro = 1;
    ImageView imageView;
    TextView tiffinDabbaTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        imageView=(ImageView) findViewById(R.id.LogoIV);
        tiffinDabbaTv=(TextView)findViewById(R.id.TiffinDabbaTV);


        Animation animation= AnimationUtils.loadAnimation(SplashScreen.this,R.anim.splash_animation);
        imageView.setAnimation(animation);
        tiffinDabbaTv.setAnimation(animation);

       handler.postDelayed(new Runnable() {
           @Override
           public void run() {

               startActivity(new Intent(SplashScreen.this,LoginSignupActivity.class));
               finish();

               //Toast.makeText(SplashScreen.this, "Over", Toast.LENGTH_SHORT).show();
           }
       },2500);

    }
}

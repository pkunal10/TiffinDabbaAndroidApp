package com.tiffin_dabba.admin.tiffindabba;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tiffin_dabba.admin.tiffindabba.Fragment.LoginFragment;
import com.tiffin_dabba.admin.tiffindabba.Fragment.SignupFragment;

public class LoginSignupActivity extends AppCompatActivity {

    TextView loginTv,signupTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_signup);

        loginTv=(TextView) findViewById(R.id.LoginTv);
        signupTv=(TextView) findViewById(R.id.SignupTv);

        getSupportFragmentManager().beginTransaction().replace(R.id.FragmentFl, new LoginFragment()).commit();
        loginTv.setTextColor(Color.parseColor("#000000"));
        loginTv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        loginTv.setBackgroundResource(R.drawable.shape_bc);


        loginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right).replace(R.id.FragmentFl, new LoginFragment()).commit();
                loginTv.setTextColor(Color.parseColor("#000000"));
                signupTv.setTextColor(Color.parseColor("#ffffff"));
                loginTv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                loginTv.setBackgroundResource(R.drawable.shape_bc);
                signupTv.setBackgroundResource(0);
            }
        });

        signupTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right).replace(R.id.FragmentFl, new SignupFragment()).commit();
                signupTv.setTextColor(Color.parseColor("#000000"));
                loginTv.setTextColor(Color.parseColor("#ffffff"));
                signupTv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                signupTv.setBackgroundResource(R.drawable.shape_bc);
                loginTv.setBackgroundResource(0);
            }
        });

    }
}

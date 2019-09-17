package com.tiffin_dabba.admin.tiffindabba;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.tiffin_dabba.admin.tiffindabba.Fragment.FastFoodProductFragment;

public class ProductListingActivity extends AppCompatActivity {

    Handler handler = new Handler();
    SharedPreferences sp;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_listing);
        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();

        sp = getSharedPreferences("pref", MODE_PRIVATE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (bundle.getString("ProductType").equalsIgnoreCase("FastFood")) {
            String fastfoodCategoryId = bundle.getString("CategoryId");
            String fastfoodCategoryName = bundle.getString("CategoryName");
            //String fastfoodNotiProductId = bundle.getString("NotiProductId");

            //  Toast.makeText(this, fastfoodNotiProductId, Toast.LENGTH_SHORT).show();

            getSupportFragmentManager().beginTransaction().replace(R.id.ProductFragmentContainer, new FastFoodProductFragment(fastfoodCategoryId, fastfoodCategoryName)).commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.drawer, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.OpMenuLogOut) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Logging Out....");
            progressDialog.setMessage("Please Wait....");
            progressDialog.setCancelable(false);
            progressDialog.show();

            SharedPreferences.Editor editor = sp.edit();
            editor.remove("UserId");
            editor.commit();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                    startActivity(new Intent(ProductListingActivity.this, LoginSignupActivity.class));
                    finish();
                }
            }, 1500);
        }
        else if(id==R.id.OpMenuCart)
        {
            startActivity(new Intent(ProductListingActivity.this,CartActivity.class));
        }else if(id==R.id.OpMenuViewProfile)
        {
            startActivity(new Intent(ProductListingActivity.this,ViewProfileActivity.class));
        }
        else if(id==android.R.id.home)
        {
            finish();
        }


        return super.onOptionsItemSelected(item);
    }
}

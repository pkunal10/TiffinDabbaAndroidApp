package com.tiffin_dabba.admin.tiffindabba;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.tiffin_dabba.admin.tiffindabba.Fragment.FastFoodCategoryFragment;
import com.tiffin_dabba.admin.tiffindabba.Fragment.HomeFragment;
import com.tiffin_dabba.admin.tiffindabba.Fragment.MonthlyBookingFragment;
import com.tiffin_dabba.admin.tiffindabba.Fragment.OrderHistoryFragment;
import com.tiffin_dabba.admin.tiffindabba.Fragment.SimpleTiffinFragment;
import com.tiffin_dabba.admin.tiffindabba.Fragment.TiffinBookFragment;
import com.tiffin_dabba.admin.tiffindabba.Fragment.TiffinWithSweetFragment;
import com.tiffin_dabba.admin.tiffindabba.ImportantClasses.XMLParser;

import org.ksoap2.serialization.SoapObject;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView drawerNameTv;
    ProgressDialog progressDialog;
    SharedPreferences sp;
    XMLParser xmlParser=new XMLParser();
    Handler handler=new Handler();

    public final static String SOAP_ACTION = "http://tempuri.org/GetUserName";
    public final static String METHOD_NAME = "GetUserName";
    public final static String NAME_SPACE = "http://tempuri.org/";
    public final static String URL = "http://tiffindabba.net/MyService.asmx";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header=navigationView.getHeaderView(0);
        drawerNameTv=(TextView) header.findViewById(R.id.DrawerNameTv);
        sp=getSharedPreferences("pref",MODE_PRIVATE);
//        String uId=sp.getString("UserId","");
//        drawerNameTv.setText(uId);


        SharedPreferences spt;
       // spt = getApplicationContext().getSharedPreferences("DeviceToken", MODE_PRIVATE);
       // Toast.makeText(this, spt.getString("token",""), Toast.LENGTH_SHORT).show();

        getSupportFragmentManager().beginTransaction().replace(R.id.DrawerFragmentsContainer,new HomeFragment()).commit();
        getSupportActionBar().setTitle("Home");

        new GetUserName().execute();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to exit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finishAffinity();
                            //finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.OpMenuViewProfile) {
            startActivity(new Intent(DrawerActivity.this,ViewProfileActivity.class));
        }

        else if(id==R.id.OpMenuLogOut)
        {
            progressDialog=new ProgressDialog(this);
            progressDialog.setTitle("Logging Out....");
            progressDialog.setMessage("Please Wait....");
            progressDialog.setCancelable(false);
            progressDialog.show();

            SharedPreferences.Editor editor=sp.edit();
            editor.remove("UserId");
            editor.commit();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                    startActivity(new Intent(DrawerActivity.this,LoginSignupActivity.class));
                    finish();
                }
            },1500);
        }
        else if(id==R.id.OpMenuCart)
        {
            startActivity(new Intent(DrawerActivity.this,CartActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_Home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.DrawerFragmentsContainer,new HomeFragment()).commit();
            getSupportActionBar().setTitle("Home");
        } else if (id == R.id.nav_FastFood) {

            getSupportFragmentManager().beginTransaction().replace(R.id.DrawerFragmentsContainer,new FastFoodCategoryFragment()).commit();
            getSupportActionBar().setTitle("Fast Food Categories");

        }else if(id==R.id.nav_TiffinBook)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.DrawerFragmentsContainer,new TiffinBookFragment()).commit();
            getSupportActionBar().setTitle("Tiffin Book");
        }
        else if (id == R.id.nav_OrderHistory) {
            getSupportFragmentManager().beginTransaction().replace(R.id.DrawerFragmentsContainer,new OrderHistoryFragment()).commit();
            getSupportActionBar().setTitle("Order History");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class GetUserName extends AsyncTask<String,Void,String>
    {
        String response="";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(DrawerActivity.this);
            progressDialog.setTitle("Loading....");
            progressDialog.setMessage("Please Wait....");
            progressDialog.setCancelable(false);
            progressDialog.show();


        }

        @Override
        protected String doInBackground(String... params) {

            SoapObject request=new SoapObject(NAME_SPACE,METHOD_NAME);

            request.addProperty("UserId",sp.getString("UserId",""));

            response=xmlParser.getServiceResultString(URL,SOAP_ACTION,request);

            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            progressDialog.dismiss();
            if(!s.equalsIgnoreCase("Fail"))
            {
                drawerNameTv.setText("Welcome:-  "+s);
            }
        }
    }
}

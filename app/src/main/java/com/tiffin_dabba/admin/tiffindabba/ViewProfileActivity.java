package com.tiffin_dabba.admin.tiffindabba;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.tiffin_dabba.admin.tiffindabba.ImportantClasses.XMLParser;

import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ViewProfileActivity extends AppCompatActivity {

    CollapsingToolbarLayout collapsingToolbarLayout;
    ImageView colapseIv;
    SharedPreferences sp;
    XMLParser xmlParser = new XMLParser();
    TextView profileNameTv,profileAddressTv,profileMobNoTv,profileEmailIdTv;
    ImageView profileNameEditBtn,profileAddressEditBtn,profileMobNoEditBtn;

    public final static String NAME_SPACE = "http://tempuri.org/";
    public final static String URL = "http://tiffindabba.net/MyService.asmx";

    public final static String SOAP_ACTION_FETCH_PROFILE = "http://tempuri.org/GetUserProfile";
    public final static String METHOD_NAME_FETCH_PROFILE = "GetUserProfile";

    public final static String SOAP_ACTION_UPDATE_PROFILE = "http://tempuri.org/UpdateUserProfile";
    public final static String METHOD_NAME_UPDATE_PROFILE = "UpdateUserProfile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        colapseIv = (ImageView) findViewById(R.id.colapseIv);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        sp = getSharedPreferences("pref", MODE_PRIVATE);
        profileNameTv=(TextView) findViewById(R.id.ProfileNameTv);
        profileAddressTv=(TextView) findViewById(R.id.ProfileAddressTv);
        profileMobNoTv=(TextView) findViewById(R.id.ProfileMobNoTv);
        profileEmailIdTv=(TextView) findViewById(R.id.ProfileEmalIdTv);
        profileNameEditBtn=(ImageView) findViewById(R.id.ProfileEditNameBtn);
        profileAddressEditBtn=(ImageView) findViewById(R.id.ProfileEditAddressBtn);
        profileMobNoEditBtn=(ImageView) findViewById(R.id.ProfileEditMobNoBtn);


        setSupportActionBar(toolbar);
        collapsingToolbarLayout.setTitle("My Profile");
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        profileEmailIdTv.setText(sp.getString("UserId",null));

        new FetchUserProfile().execute();

//        Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.jyoti);
//        Palette.generateAsync(bitmap,
//                new Palette.PaletteAsyncListener() {
//                    @Override
//                    public void onGenerated(Palette palette) {
//                        Palette.Swatch vibrant =
//                                palette.getVibrantSwatch();
//                        int mutedColor = palette.getVibrantSwatch().getRgb();
//                        if (vibrant != null) {
//                            // If we have a vibrant color
//                            // update the title TextView
//                            collapsingToolbarLayout.setBackgroundColor(mutedColor);
//                            //  mutedColor = palette.getMutedColor(R.attr.colorPrimary);
//                            // collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkMutedColor(mutedColor));
//                            collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(mutedColor));
//
//                        }
//                    }
//                });

        profileNameEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Name=profileNameTv.getText().toString();
                OpenEditPopUp(Name,"Name");
            }
        });

        profileAddressEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Address=profileAddressTv.getText().toString();
                OpenEditPopUp(Address,"Address");
            }
        });

        profileMobNoEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String MobNo=profileMobNoTv.getText().toString();
                OpenEditPopUp(MobNo,"Mobile");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();

        if(id==android.R.id.home)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void OpenEditPopUp(String Value, final String Field) {

        TextView profileEditPopupTitleTv;
        final EditText profileEditPopupValueEt;
        Button profileEditPopupUpdateBtn;
        ImageView profileEditPopupCloseBtn;

        View profileEdit= LayoutInflater.from(ViewProfileActivity.this).inflate(R.layout.profile_edit_popup_layout,null);

        profileEditPopupTitleTv=(TextView) profileEdit.findViewById(R.id.ProfileEditPopupTitleTv);
        profileEditPopupValueEt=(EditText) profileEdit.findViewById(R.id.ProfileEditPopupValueEt);
        profileEditPopupUpdateBtn=(Button) profileEdit.findViewById(R.id.ProfileEditPopupUpdateBtn);
        profileEditPopupCloseBtn=(ImageView) profileEdit.findViewById(R.id.ProfileEditCloseBtn);

        AlertDialog.Builder builderProfileEditPopup=new AlertDialog.Builder(ViewProfileActivity.this);
        builderProfileEditPopup.setView(profileEdit);
        builderProfileEditPopup.setCancelable(false);
        final AlertDialog alertDialogProfileEditPoup=builderProfileEditPopup.create();
        alertDialogProfileEditPoup.show();

        profileEditPopupTitleTv.append(Field);
        profileEditPopupValueEt.setText(Value);


        profileEditPopupUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Field.equalsIgnoreCase("Name")){
                    if(!validatename(profileEditPopupValueEt.getText().toString()))
                    {
                        profileEditPopupValueEt.setError("Enter Proper Name.");
                    }
                    else
                    {
                        new UpdateProfile(profileEditPopupValueEt.getText().toString(),Field).execute();
                        alertDialogProfileEditPoup.dismiss();
                    }
                }
                if(Field.equalsIgnoreCase("Address")){
                    if(!validateaddress(profileEditPopupValueEt.getText().toString()))
                    {
                        profileEditPopupValueEt.setError("Enter Proper Address.");
                    }
                    else
                    {
                        new UpdateProfile(profileEditPopupValueEt.getText().toString(),Field).execute();
                        alertDialogProfileEditPoup.dismiss();
                    }
                }
                if(Field.equalsIgnoreCase("Mobile")){
                    if(!validatemobile(profileEditPopupValueEt.getText().toString()))
                    {
                        profileEditPopupValueEt.setError("Enter Proper Mobile No.");
                    }
                    else
                    {
                        new UpdateProfile(profileEditPopupValueEt.getText().toString(),Field).execute();
                        alertDialogProfileEditPoup.dismiss();
                    }
                }
            }
        });


        profileEditPopupCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogProfileEditPoup.dismiss();
            }
        });


    }

    public class FetchUserProfile extends AsyncTask<String, Void, SoapObject> {
        ProgressDialog progressDialogUserProfile;
        String Uid = sp.getString("UserId", null);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialogUserProfile = new ProgressDialog(ViewProfileActivity.this);
            progressDialogUserProfile.setTitle("Fetching Profile....");
            progressDialogUserProfile.setMessage("Please Wait....");
            progressDialogUserProfile.setCancelable(false);
            progressDialogUserProfile.show();
        }

        @Override
        protected SoapObject doInBackground(String... params) {

            SoapObject request = new SoapObject(NAME_SPACE, METHOD_NAME_FETCH_PROFILE);

            request.addProperty("UserId", Uid);

            try {
                return xmlParser.getServiceResultSoapObj(URL, SOAP_ACTION_FETCH_PROFILE, request);
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(SoapObject soapObject) {
            super.onPostExecute(soapObject);
            progressDialogUserProfile.dismiss();
            if (soapObject != null) {

                String path = soapObject.getPropertyAsString("ProfileImage").substring(1);
                Picasso.with(ViewProfileActivity.this).load("http://tiffindabba.net" + path).placeholder(R.mipmap.ic_launcher).into(colapseIv);
                profileNameTv.setText(soapObject.getPropertyAsString("Name"));
                profileAddressTv.setText(soapObject.getPropertyAsString("Address"));
                profileMobNoTv.setText(soapObject.getPropertyAsString("MobileNo"));
            }
        }
    }

    public class UpdateProfile extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialogUpdateProfile;
        String Uid = sp.getString("UserId", null);
        String value,field;

        public UpdateProfile(String value, String field) {
            this.value=value;
            this.field=field;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialogUpdateProfile = new ProgressDialog(ViewProfileActivity.this);
            progressDialogUpdateProfile.setTitle("Updating Data....");
            progressDialogUpdateProfile.setMessage("Please Wait....");
            progressDialogUpdateProfile.setCancelable(false);
            progressDialogUpdateProfile.show();
        }

        @Override
        protected String doInBackground(String... params) {

            SoapObject request = new SoapObject(NAME_SPACE, METHOD_NAME_UPDATE_PROFILE);

            request.addProperty("Value", value);
            request.addProperty("Field", field);
            request.addProperty("UserId", Uid);

            return xmlParser.getServiceResultString(URL,SOAP_ACTION_UPDATE_PROFILE,request);

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            progressDialogUpdateProfile.dismiss();

            if(s.equalsIgnoreCase("Success"))
            {
                Toast.makeText(ViewProfileActivity.this, "Profile Updated.", Toast.LENGTH_SHORT).show();
                new FetchUserProfile().execute();
            }
            else
            {
                Toast.makeText(ViewProfileActivity.this, "Profile Not Updated.", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private boolean validatemobile(String mobile)
    {
        String mobilepattren="^[0-9]{10}$";

        Pattern pattern=Pattern.compile(mobilepattren);
        Matcher matcher=pattern.matcher(mobile);

        if (!mobile.matches("") && matcher.matches())
        {
            return true;
        }
        else
        {
            return false;
        }

    }

    private boolean validateaddress(String address)
    {

        if (!address.matches(""))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean validatename(String name)
    {

//        return true;

        String patternname="[a-zA-Z ]*$";

        Pattern pattern= Pattern.compile(patternname);
        Matcher matcher=pattern.matcher(name);
        if (!name.matches("") && matcher.matches())
        {
            return  true;
        }
        else
        {
            return false;
        }
    }
}

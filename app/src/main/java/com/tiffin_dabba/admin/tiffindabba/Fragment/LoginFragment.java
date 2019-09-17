package com.tiffin_dabba.admin.tiffindabba.Fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tiffin_dabba.admin.tiffindabba.DrawerActivity;
import com.tiffin_dabba.admin.tiffindabba.ImportantClasses.CommonFunction;
import com.tiffin_dabba.admin.tiffindabba.ImportantClasses.XMLParser;
import com.tiffin_dabba.admin.tiffindabba.R;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.ObjectInput;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ADMIN on 11-09-2017.
 */

public class LoginFragment extends android.support.v4.app.Fragment {

    EditText emaiEt, passwordEt,forgetEmailEt;
    Button loginBtn,forgetSubmitBtn;
    String emailId, password,forgetEmail;
    XMLParser xmlParser = new XMLParser();
    SharedPreferences sp;
    CommonFunction commonFunction = new CommonFunction();
    ProgressDialog progressDialog;
    TextView forgetTv;
    AlertDialog.Builder builder ;


    public final static String SOAP_ACTION = "http://tempuri.org/Login";
    public final static String METHOD_NAME = "Login";
    public final static String NAME_SPACE = "http://tempuri.org/";
    public final static String URL = "http://tiffindabba.net/MyService.asmx";

    public final static String SOAP_ACTION_FORGET = "http://tempuri.org/ForgetPasswordAPI";
    public final static String METHOD_NAME_FORGET = "ForgetPasswordAPI";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.login_fragment_layout, container, false);

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sp = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);

        if (sp.contains("UserId")) {
            startActivity(new Intent(getActivity(), DrawerActivity.class));
            getActivity().finish();
        }

        if (!commonFunction.IsInternetAvailable(getActivity())) {
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        }

        emaiEt = (EditText) view.findViewById(R.id.EmailIdEt);
        passwordEt = (EditText) view.findViewById(R.id.PasswordEt);
        loginBtn = (Button) view.findViewById(R.id.LoginBtn);
        forgetTv = (TextView) view.findViewById(R.id.ForgetTv);


        final View alertView=getActivity().getLayoutInflater().inflate(R.layout.forget_password_layout,null);

        forgetTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder= new AlertDialog.Builder(getActivity());
                builder.setTitle("Forget Password");
                builder.setView(alertView);
                builder.show();
            }
        });

        forgetEmailEt=(EditText)alertView.findViewById(R.id.ForgetEmailIdEt);
        forgetSubmitBtn=(Button)alertView.findViewById(R.id.ForgetSubmitBtn);

        forgetSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validateemail(forgetEmailEt.getText().toString()))
                {
                    forgetEmailEt.setError("Enter Proper Email Id");
                    forgetEmailEt.requestFocus();
                }
                else
                {
                    forgetEmail=forgetEmailEt.getText().toString();
                    new ForgetPassword().execute();
                }
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                emailId = emaiEt.getText().toString();
                password = passwordEt.getText().toString();

                if (emailId.matches("")) {
                    emaiEt.setError("Enter Email Id");
                    emaiEt.requestFocus();
                } else {
                    if (password.matches("")) {
                        passwordEt.setError("Enter Password");
                        passwordEt.requestFocus();
                    } else {

                        new LoginUser().execute();
                        //Toast.makeText(getActivity(), "Perfect", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public class ForgetPassword extends AsyncTask<String, Object, String> {
        String response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Sending Password....");
            progressDialog.setMessage("Please Wait....");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }


        @Override
        protected String doInBackground(String... params) {

            SoapObject request = new SoapObject(NAME_SPACE, METHOD_NAME_FORGET);

            request.addProperty("Email", forgetEmail);

            response = xmlParser.getServiceResultString(URL, SOAP_ACTION_FORGET, request);

            return response;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            progressDialog.dismiss();

//            forgetEmailEt.setText(s);
            if(s.equalsIgnoreCase("Success"))
            {
                Toast.makeText(getActivity(), "Password Is Sent to your Email Id.", Toast.LENGTH_LONG).show();
                forgetEmailEt.setText("");
            }
            else if(s.equalsIgnoreCase("InvalidId"))
            {
                Toast.makeText(getActivity(), "Your Email Id was not found in our record.", Toast.LENGTH_LONG).show();
                forgetEmailEt.setText("");
            }

        }
    }

    public class LoginUser extends AsyncTask<String, Object, String> {
        String response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Loging In....");
            progressDialog.setMessage("Please Wait....");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }


        @Override
        protected String doInBackground(String... params) {

            SoapObject request = new SoapObject(NAME_SPACE, METHOD_NAME);

            request.addProperty("EmailId", emailId);
            request.addProperty("Password", password);

            response = xmlParser.getServiceResultString(URL, SOAP_ACTION, request);

            return response;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            progressDialog.dismiss();

            //emaiEt.setText(s);

            if (s.equalsIgnoreCase("Success")) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("UserId", emailId);
                editor.commit();

                emaiEt.setText("");
                passwordEt.setText("");

                Toast.makeText(getActivity(), "Login Successful.", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(getActivity(), DrawerActivity.class));
                getActivity().finish();
            } else if (s.equalsIgnoreCase("NotAllowed")) {
                Toast.makeText(getActivity(), "Your account is blocked.", Toast.LENGTH_LONG).show();
            } else if (s.equalsIgnoreCase("Invalid")) {
                Toast.makeText(getActivity(), "Invalid Id Or Password.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean validateemail(String email)
    {
        String emailpattren="\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";

        Pattern pattern=Pattern.compile(emailpattren);
        Matcher matcher=pattern.matcher(email);

        if (!email.matches("") && matcher.matches())
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}

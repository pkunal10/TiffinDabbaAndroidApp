package com.tiffin_dabba.admin.tiffindabba.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tiffin_dabba.admin.tiffindabba.ImportantClasses.CommonFunction;
import com.tiffin_dabba.admin.tiffindabba.ImportantClasses.XMLParser;
import com.tiffin_dabba.admin.tiffindabba.R;

import org.ksoap2.serialization.SoapObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ADMIN on 11-09-2017.
 */

public class SignupFragment extends Fragment {


    EditText nameEt,addressEt,mobileEt,emailEt,passwordEt,confmPasswordEt;
    Button signupBtn,clearBtn;
    TextView loginTv;
    XMLParser xmlParser=new XMLParser();
    String name,address,mobile,email,password;
    CommonFunction commonFunction=new CommonFunction();
    ProgressDialog progressDialog;
    SharedPreferences sp;

    public final static String SOAP_ACTION = "http://tempuri.org/RegisterUser";
    public final static String METHOD_NAME = "RegisterUser";
    public final static String NAME_SPACE = "http://tempuri.org/";
    public final static String URL = "http://tiffindabba.net/MyService.asmx";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.signup_fragment_layout,container,false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(!commonFunction.IsInternetAvailable(getActivity()))
        {
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        }

        nameEt=(EditText) view.findViewById(R.id.NameEt);
        addressEt=(EditText) view.findViewById(R.id.AddressEt);
        mobileEt=(EditText) view.findViewById(R.id.MobNoEt);
        emailEt=(EditText) view.findViewById(R.id.EmailEt);
        passwordEt=(EditText) view.findViewById(R.id.PasswordEt);
        confmPasswordEt=(EditText) view.findViewById(R.id.ConfmPasswordEt);
        signupBtn=(Button) view.findViewById(R.id.SignupBtn);
        clearBtn=(Button) view.findViewById(R.id.ClearBtn);
        loginTv=(TextView) view.findViewById(R.id.LoginTv);

        loginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right).replace(R.id.FragmentFl,new LoginFragment()).commit();

                TextView llogtv=(TextView) getActivity().findViewById(R.id.LoginTv);
                TextView lsignuptv=(TextView) getActivity().findViewById(R.id.SignupTv);

                llogtv.setTextColor(Color.parseColor("#000000"));
                lsignuptv.setTextColor(Color.parseColor("#ffffff"));
                llogtv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                llogtv.setBackgroundResource(R.drawable.shape_bc);
                lsignuptv.setBackgroundResource(0);

            }
        });


        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register();
            }
        });

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Clear();
            }
        });

    }

    private void Clear() {

        nameEt.setText("");
        addressEt.setText("");
        mobileEt.setText("");
        emailEt.setText("");
        passwordEt.setText("");
        confmPasswordEt.setText("");

    }

    private void Register() {

        if (!validatename(nameEt.getText().toString()))
        {
            nameEt.setError("Enter Valid Name");
            nameEt.requestFocus();
        }
        else if (!validateaddress(addressEt.getText().toString()))
        {
            addressEt.setError("Enter Valid Address");
            addressEt.requestFocus();
        }
        else if (!validatemobile(mobileEt.getText().toString()))
        {
            mobileEt.setError("Enter Valid Mobile No");
            mobileEt.requestFocus();
        }
        else if (!validateemail(emailEt.getText().toString()))
        {
            emailEt.setError("Enter Valid Email Id");
            emailEt.requestFocus();
        }
        else if (!validatepassword(passwordEt.getText().toString()))
        {
            passwordEt.setError("Enter Minimum 6 Character Password");
            passwordEt.requestFocus();
        }
        else if (!validateconfmpassword(confmPasswordEt.getText().toString()))
        {
            confmPasswordEt.setError("Password Does't Match");
            confmPasswordEt.requestFocus();
        }
        else
        {
           // Toast.makeText(getActivity(), "Perfect", Toast.LENGTH_SHORT).show();
            name=nameEt.getText().toString();
            address=addressEt.getText().toString();
            mobile=mobileEt.getText().toString();
            email=emailEt.getText().toString();
            password=passwordEt.getText().toString();

            new RegisterUser().execute();
        }
    }

    public class RegisterUser extends AsyncTask<String,Void,String>
    {
        String response;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog =new ProgressDialog(getActivity());
            progressDialog.setTitle("Loging In....");
            progressDialog.setMessage("Please Wait....");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            SoapObject request=new SoapObject(NAME_SPACE,METHOD_NAME);
            
            request.addProperty("Name",name);
            request.addProperty("Address",address);
            request.addProperty("Mobile",mobile);
            request.addProperty("EmailId",email);
            request.addProperty("Password",password);

            response=xmlParser.getServiceResultString(URL,SOAP_ACTION,request);

            return response;

        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            progressDialog.dismiss();

            if(s.equalsIgnoreCase("Registered"))
            {
                Toast.makeText(getActivity(), "You are registtered,Now you can Login", Toast.LENGTH_LONG).show();

                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right).replace(R.id.FragmentFl,new LoginFragment()).commit();

                TextView llogtv=(TextView) getActivity().findViewById(R.id.LoginTv);
                TextView lsignuptv=(TextView) getActivity().findViewById(R.id.SignupTv);

                llogtv.setTextColor(Color.parseColor("#000000"));
                lsignuptv.setTextColor(Color.parseColor("#ffffff"));
                llogtv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                llogtv.setBackgroundResource(R.drawable.shape_bc);
                lsignuptv.setBackgroundResource(0);
            }
            else if(s.equalsIgnoreCase("EmailId Exist"))
            {
                Toast.makeText(getActivity(), "Email Id Already Exist.", Toast.LENGTH_LONG).show();
                emailEt.setText("");
                emailEt.requestFocus();
            }

        }
    }

    private boolean validateconfmpassword(String confmpassword)
    {
        if (!confmpassword.matches("") && confmpassword.matches(passwordEt.getText().toString()))
        {
            return true;
        }
        else
        {
            return false;
        }

    }

    private boolean validatepassword(String password)
    {
        if (!password.matches("") && password.length()>5)
        {
            return true;
        }
        else
        {
            return false;
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

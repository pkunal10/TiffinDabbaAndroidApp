package com.tiffin_dabba.admin.tiffindabba.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tiffin_dabba.admin.tiffindabba.DrawerActivity;
import com.tiffin_dabba.admin.tiffindabba.ImportantClasses.CommonFunction;
import com.tiffin_dabba.admin.tiffindabba.ImportantClasses.XMLParser;
import com.tiffin_dabba.admin.tiffindabba.R;

import org.ksoap2.serialization.SoapObject;
import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ADMIN on 11-10-2017.
 */

public class MonthlyBookingFragment extends Fragment {

    Spinner mbCitySpinner, mbAreaSpinner;
    Button mbMonthlyBookBtn, mbTrialBookBtn;
    List<String> mbCityList, mbAreaList;
    SharedPreferences sp;
    String selectedCity;
    ProgressDialog progressDialog;
    CardView mbTiffinDetailsPnl;
    CommonFunction commonFunction=new CommonFunction();
    ArrayAdapter<String> mbCityAdapter, mbAreaAdapter;
    XMLParser xmlParser = new XMLParser();
    LinearLayout mbAlradyBookPnl, mbCanBookPnl;
    TextView mbAlreadyBookMsgTv;
    ImageView mbEatingIv;
    TextView mbDelAreTv;

    public final static String NAME_SPACE = "http://tempuri.org/";
    public final static String URL = "http://tiffindabba.net/MyService.asmx";

    public final static String SOAP_ACTION_FETCH_CITY = "http://tempuri.org/GetCityList";
    public final static String METHOD_NAME_FETCH_CITY = "GetCityList";

    public final static String SOAP_ACTION_FETCH_AREA = "http://tempuri.org/GetAreaList";
    public final static String METHOD_NAME_FETCH_AREA = "GetAreaList";

    public final static String SOAP_ACTION_FETCH_ADDRESS = "http://tempuri.org/GetUserAddress";
    public final static String METHOD_NAME_FETCH_ADDRESS = "GetUserAddress";

    public final static String SOAP_ACTION_FETCH_ST_END_DATE = "http://tempuri.org/GetStartEndDate";
    public final static String METHOD_NAME_FETCH_ST_END_DATE = "GetStartEndDate";

    public final static String SOAP_ACTION_MONTHLY_BOOK_ORDER = "http://tempuri.org/BookMonthlyOrder";
    public final static String METHOD_NAME_MONTHLY_BOOK_ORDER = "BookMonthlyOrder";

    public final static String SOAP_ACTION_MONTHLY_PANEL_VISIBLE = "http://tempuri.org/CheckPanelVisible";
    public final static String METHOD_NAME_MONTHLY_PANEL_VISIBLE = "CheckPanelVisible";

    public final static String SOAP_ACTION_MONTHLY_BOOK_ORDER_TRIAL = "http://tempuri.org/BookTrialOrder";
    public final static String METHOD_NAME_MONTHLY_BOOK_ORDER_TRIAL = "BookTrialOrder";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.monthly_booking_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sp = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);

        mbCitySpinner = (Spinner) view.findViewById(R.id.MbCitySpinner);
        mbAreaSpinner = (Spinner) view.findViewById(R.id.MbAreaSpinner);
        mbMonthlyBookBtn = (Button) view.findViewById(R.id.MbMonthlyBookBtn);
        mbTrialBookBtn = (Button) view.findViewById(R.id.MbTrialBookBtn);
        mbTiffinDetailsPnl = (CardView) view.findViewById(R.id.MbTiffinDetailsPnl);
        mbAlradyBookPnl = (LinearLayout) view.findViewById(R.id.MbAlreadyBookPnl);
        mbCanBookPnl = (LinearLayout) view.findViewById(R.id.MbCanBookPnl);
        mbAlreadyBookMsgTv = (TextView) view.findViewById(R.id.MbAlreadyBookMsgTv);
        mbEatingIv = (ImageView) view.findViewById(R.id.MbEatingIv);
        mbDelAreTv=(TextView) view.findViewById(R.id.MbDelAreaTv);

        new CheckPanelVisiblity().execute();

        mbCityList = new ArrayList<>();
        mbAreaList = new ArrayList<>();

        if (!commonFunction.IsInternetAvailable(getActivity())) {
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        }

        new FetchCity().execute();

        mbCitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(CartActivity.this, cityList.get(position), Toast.LENGTH_SHORT).show();

                if (position == 0) {
                    mbAreaList.clear();
                    mbDelAreTv.setVisibility(View.GONE);
                    mbAreaAdapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, mbAreaList);
                    mbAreaSpinner.setAdapter(mbAreaAdapter);
                    mbTiffinDetailsPnl.setVisibility(View.INVISIBLE);
                } else {

                    mbAreaList.clear();
                    mbDelAreTv.setVisibility(View.VISIBLE);
                    selectedCity = parent.getItemAtPosition(position).toString();
                    new AreaFetch().execute();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mbAreaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    mbTiffinDetailsPnl.setVisibility(View.INVISIBLE);
                } else {

                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("DelCity", mbCitySpinner.getSelectedItem().toString());
                    editor.putString("DelArea", mbAreaSpinner.getSelectedItem().toString());
                    editor.commit();
                    mbTiffinDetailsPnl.setVisibility(View.VISIBLE);

                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.panel_animation);
                    Animation animation_btn = AnimationUtils.loadAnimation(getContext(), R.anim.btn_animation);
                    mbTiffinDetailsPnl.setAnimation(animation);
                    mbTrialBookBtn.setAnimation(animation_btn);
                    mbMonthlyBookBtn.setAnimation(animation_btn);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mbMonthlyBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenPopupMonthlyBook();
            }
        });

        mbTrialBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenPopupTrialBook();
            }
        });
    }

    private void OpenPopupTrialBook() {

        View trialPopupView = LayoutInflater.from(getActivity()).inflate(R.layout.order_confirmation_layout_of_trialbook, null, false);

        ImageView mbTrialPopupCloseBtn;
        final Spinner mbTrialPopupBookForSpinner;
        final EditText mbTrialPopupAddressEt;
        Button mbTrialConfirmOrderBtn;
        TextView mbTrialPopupChangeAddressBtn;
        List<String> mbTrialPopupBookForList;
        ArrayAdapter<String> mbTrialPopupBookForAdapter;

        mbTrialPopupCloseBtn = (ImageView) trialPopupView.findViewById(R.id.MbTrialPopUpCloseBtn);
        mbTrialPopupBookForSpinner = (Spinner) trialPopupView.findViewById(R.id.MbTrialPopUpBookForSpinner);
        mbTrialPopupAddressEt = (EditText) trialPopupView.findViewById(R.id.MbTrialPopupAddressEt);
        mbTrialConfirmOrderBtn = (Button) trialPopupView.findViewById(R.id.MbTrialPopupConfirmOrderBtn);
        mbTrialPopupChangeAddressBtn = (TextView) trialPopupView.findViewById(R.id.MbTrialPopupChangeAddressBtn);

        mbTrialPopupBookForList = new ArrayList<>();
        mbTrialPopupBookForList.add(0, "Select Book For");
        mbTrialPopupBookForList.add(1, "Lunch");
        mbTrialPopupBookForList.add(2, "Dinner");

        AlertDialog.Builder buildertrialPopup = new AlertDialog.Builder(getActivity());
        buildertrialPopup.setView(trialPopupView);
        buildertrialPopup.setCancelable(false);
        final AlertDialog alertDialogtrialPopup = buildertrialPopup.create();
        alertDialogtrialPopup.show();

        mbTrialPopupBookForAdapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, mbTrialPopupBookForList);
        mbTrialPopupBookForSpinner.setAdapter(mbTrialPopupBookForAdapter);

        new FetchAddress(mbTrialPopupAddressEt).execute();

        mbTrialPopupChangeAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mbTrialPopupAddressEt.setEnabled(true);
            }
        });

        mbTrialPopupCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogtrialPopup.dismiss();
            }
        });

        mbTrialConfirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String trialBookFor = mbTrialPopupBookForSpinner.getSelectedItem().toString();
                final String UID = sp.getString("UserId", null);
                final String DelArea = mbAreaSpinner.getSelectedItem().toString();
                final String DelAdd = mbTrialPopupAddressEt.getText().toString();

                if (mbTrialPopupBookForSpinner.getSelectedItemPosition() == 0) {
                    Toast.makeText(getActivity(), "Select Book For.", Toast.LENGTH_SHORT).show();
                } else if (DelAdd.equalsIgnoreCase("")) {
                    Toast.makeText(getActivity(), "Enter Delivery Address.", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder builderConfirmation = new AlertDialog.Builder(getActivity());
                    builderConfirmation.setTitle("Confirmation");
                    builderConfirmation.setMessage("Are you sure you want to book this order ??");
                    builderConfirmation.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            new BookOrderTrial(trialBookFor, UID, DelArea, DelAdd).execute();

                            alertDialogtrialPopup.dismiss();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
                }

            }
        });
    }

    private void OpenPopupMonthlyBook() {

        View monthlyPopupView = LayoutInflater.from(getActivity()).inflate(R.layout.order_confirmation_layout_of_monthlybook, null, false);

        ImageView mbMonthlyPopupCloseBtn;
        final Spinner mbMonthlyPopupBookForSpinner, mbMonthlyPopupDaysSpinner;
        final TextView mbMonthlyPopupSTDate, mbMonthlyPopupEndDate, mbMonthlyChangeAddressBtn;
        final EditText mbMonthlyPopupAddressEt;
        Button mbMonthlyConfirmOrderBtn;
        List<String> mbMonthlyPopupBookForList, mbMonthlyPopupDaysList;
        ArrayAdapter<String> mbMonthlyPopupBookForAdapter, mbMonthlyPopupDaysAdapter;

        mbMonthlyPopupCloseBtn = (ImageView) monthlyPopupView.findViewById(R.id.MbMonthlyPopUpCloseBtn);
        mbMonthlyPopupBookForSpinner = (Spinner) monthlyPopupView.findViewById(R.id.MbMonthlyPopUpBookForSpinner);
        mbMonthlyPopupDaysSpinner = (Spinner) monthlyPopupView.findViewById(R.id.MbMonthlyPopUpDaysSpinner);
        mbMonthlyPopupSTDate = (TextView) monthlyPopupView.findViewById(R.id.MbMonthlyPopupSTDate);
        mbMonthlyPopupEndDate = (TextView) monthlyPopupView.findViewById(R.id.MbMonthlyPopupEndDate);
        mbMonthlyChangeAddressBtn = (TextView) monthlyPopupView.findViewById(R.id.MbMonthlyPopupChangeAddressBtn);
        mbMonthlyPopupAddressEt = (EditText) monthlyPopupView.findViewById(R.id.MbMonthlyPopupAddressEt);
        mbMonthlyConfirmOrderBtn = (Button) monthlyPopupView.findViewById(R.id.MbMonthlyPopupConfirmOrderBtn);

        mbMonthlyPopupDaysList = new ArrayList<>();
        mbMonthlyPopupBookForList = new ArrayList<>();

        mbMonthlyPopupDaysList.add(0, "Select No Of Days");
        mbMonthlyPopupDaysList.add(1, "15 Days");
        mbMonthlyPopupDaysList.add(2, "30 Days");

        mbMonthlyPopupBookForList.add(0, "Select Book For");
        mbMonthlyPopupBookForList.add(1, "Lunch");
        mbMonthlyPopupBookForList.add(2, "Dinner");
        mbMonthlyPopupBookForList.add(3, "Both");

        mbMonthlyPopupBookForAdapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, mbMonthlyPopupBookForList);
        mbMonthlyPopupBookForSpinner.setAdapter(mbMonthlyPopupBookForAdapter);

        mbMonthlyPopupDaysAdapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, mbMonthlyPopupDaysList);
        mbMonthlyPopupDaysSpinner.setAdapter(mbMonthlyPopupDaysAdapter);

        AlertDialog.Builder buildermonthlyPopup = new AlertDialog.Builder(getActivity());
        buildermonthlyPopup.setView(monthlyPopupView);
        buildermonthlyPopup.setCancelable(false);
        final AlertDialog alertDialogmonthlyPopup = buildermonthlyPopup.create();
        alertDialogmonthlyPopup.show();

        new FetchAddress(mbMonthlyPopupAddressEt).execute();

        mbMonthlyPopupDaysSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    mbMonthlyPopupSTDate.setText("");
                    mbMonthlyPopupEndDate.setText("");
                } else if (position == 1) {
                    new FetchSTEndDate("15", mbMonthlyPopupSTDate, mbMonthlyPopupEndDate).execute();
                } else if (position == 2) {
                    new FetchSTEndDate("30", mbMonthlyPopupSTDate, mbMonthlyPopupEndDate).execute();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mbMonthlyChangeAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mbMonthlyPopupAddressEt.setEnabled(true);
            }
        });

        mbMonthlyPopupCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogmonthlyPopup.dismiss();
            }
        });

        mbMonthlyConfirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mbMonthlyPopupBookForSpinner.getSelectedItemPosition() == 0) {
                    Toast.makeText(getActivity(), "Please Select Book For.", Toast.LENGTH_LONG).show();
                } else if (mbMonthlyPopupDaysSpinner.getSelectedItemPosition() == 0) {
                    Toast.makeText(getActivity(), "Please Select No Of Days.", Toast.LENGTH_LONG).show();
                } else if (mbMonthlyPopupAddressEt.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getActivity(), "Please Enter Delivery Address.", Toast.LENGTH_LONG).show();
                } else {
                    AlertDialog.Builder builderOrderConfirmation = new AlertDialog.Builder(getActivity());
                    builderOrderConfirmation.setMessage("Are you sure you want to book this order ??");
                    builderOrderConfirmation.setCancelable(false);

                    builderOrderConfirmation.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if (mbMonthlyPopupDaysSpinner.getSelectedItemPosition() == 1) {
                                String BookFor = (String) mbMonthlyPopupBookForSpinner.getSelectedItem();
                                String Uid = sp.getString("UserId", null);
                                String DelArea = (String) mbAreaSpinner.getSelectedItem();
                                String DelAddress = mbMonthlyPopupAddressEt.getText().toString();
                                String NoOfDays = "15";
                                String STDate = mbMonthlyPopupSTDate.getText().toString().substring(13);
                                String EndDate = mbMonthlyPopupEndDate.getText().toString().substring(11);

                                new BookOrder(BookFor, Uid, DelArea, DelAddress, NoOfDays, STDate, EndDate).execute();
                            }
                            if (mbMonthlyPopupDaysSpinner.getSelectedItemPosition() == 2) {
                                String BookFor = (String) mbMonthlyPopupBookForSpinner.getSelectedItem();
                                String Uid = sp.getString("UserId", null);
                                String DelArea = (String) mbAreaSpinner.getSelectedItem();
                                String DelAddress = mbMonthlyPopupAddressEt.getText().toString();
                                String NoOfDays = "30";
                                String STDate = mbMonthlyPopupSTDate.getText().toString().substring(13);
                                String EndDate = mbMonthlyPopupEndDate.getText().toString().substring(11);

                                new BookOrder(BookFor, Uid, DelArea, DelAddress, NoOfDays, STDate, EndDate).execute();
                            }


                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
                }
            }
        });

    }

    public class BookOrderTrial extends AsyncTask<String, Void, SoapObject> {
        ProgressDialog progressDialogBookOrderTrial;
        String trialBookFor, uid, delArea, delAdd;

        public BookOrderTrial(String trialBookFor, String uid, String delArea, String delAdd) {

            this.trialBookFor = trialBookFor;
            this.uid = uid;
            this.delArea = delArea;
            this.delAdd = delAdd;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialogBookOrderTrial = new ProgressDialog(getActivity());
            progressDialogBookOrderTrial.setTitle("Booking Order....");
            progressDialogBookOrderTrial.setMessage("Please Wait....");
            progressDialogBookOrderTrial.setCancelable(false);
            progressDialogBookOrderTrial.show();
        }


        @Override
        protected SoapObject doInBackground(String... params) {

            SoapObject request=new SoapObject(NAME_SPACE,METHOD_NAME_MONTHLY_BOOK_ORDER_TRIAL);

            request.addProperty("BookFor",trialBookFor);
            request.addProperty("UserId",uid);
            request.addProperty("DeliveryArea",delArea);
            request.addProperty("DeliveryAddress",delAdd);

            try {
                return xmlParser.getServiceResultSoapObj(URL,SOAP_ACTION_MONTHLY_BOOK_ORDER_TRIAL,request);
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

            progressDialogBookOrderTrial.dismiss();

            if(soapObject!=null)
            {
                String status=soapObject.getPropertyAsString(0);

                if(status.equalsIgnoreCase("Success"))
                {
                    Toast.makeText(getActivity(), "Order Is Booked.", Toast.LENGTH_LONG).show();
                }
                else if(status.equalsIgnoreCase("Fail"))
                {
                    String bookfr=soapObject.getPropertyAsString(1);
                    String time=soapObject.getPropertyAsString(2);

                    ShowErrMsg(bookfr,time);
                }
                else if(status.equalsIgnoreCase("Vacation"))
                {
                    AlertDialog.Builder builderErrTT=new AlertDialog.Builder(getActivity());
                    builderErrTT.setTitle("Vacation");
                    builderErrTT.setMessage("You can not book the order due to vacation....");
                    builderErrTT.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
                }
            }
        }
    }

    private void ShowErrMsg(String bookfr, String time) {

        AlertDialog.Builder builderErr=new AlertDialog.Builder(getActivity());
        builderErr.setTitle("Error....");
        builderErr.setMessage("You can't book tiffin for "+bookfr+" after "+time+"." );

        builderErr.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }

    public class BookOrder extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialogBookOrder;
        String bookFor, uid, delArea, delAddress, noOfDays, stDate, endDate;

        public BookOrder(String bookFor, String uid, String delArea, String delAddress, String noOfDays, String stDate, String endDate) {

            this.bookFor = bookFor;
            this.uid = uid;
            this.delArea = delArea;
            this.delAddress = delAddress;
            this.noOfDays = noOfDays;
            this.stDate = stDate;
            this.endDate = endDate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialogBookOrder = new ProgressDialog(getActivity());
            progressDialogBookOrder.setTitle("Booking Order....");
            progressDialogBookOrder.setMessage("Please Wait....");
            progressDialogBookOrder.setCancelable(false);
            progressDialogBookOrder.show();
        }

        @Override
        protected String doInBackground(String... params) {
            SoapObject request = new SoapObject(NAME_SPACE, METHOD_NAME_MONTHLY_BOOK_ORDER);

            request.addProperty("BookFor", bookFor);
            request.addProperty("UserId", uid);
            request.addProperty("DeliveryArea", delArea);
            request.addProperty("DeliveryAddress", delAddress);
            request.addProperty("NoOfDays", noOfDays);
            request.addProperty("StartDate", stDate);
            request.addProperty("EndDate", endDate);

            return xmlParser.getServiceResultString(URL, SOAP_ACTION_MONTHLY_BOOK_ORDER, request);

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            progressDialogBookOrder.dismiss();
            if (s.equalsIgnoreCase("Success")) {
                Toast.makeText(getActivity(), "Order Is Boked.", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getActivity(), DrawerActivity.class));
            }
            else if (s.equalsIgnoreCase("Vacation"))
            {
                AlertDialog.Builder builderErr=new AlertDialog.Builder(getActivity());
                builderErr.setTitle("Vacation");
                builderErr.setMessage("You can not book the order due to vacation....");
                builderErr.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
            }
        }
    }

    public class FetchSTEndDate extends AsyncTask<String, Void, SoapObject> {
        ProgressDialog progressDialogSTEndDate;
        String Days;
        TextView stTv, endTv;

        public FetchSTEndDate(String Days, TextView stTv, TextView endTv) {
            this.Days = Days;
            this.stTv = stTv;
            this.endTv = endTv;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialogSTEndDate = new ProgressDialog(getActivity());
            progressDialogSTEndDate.setTitle("Fetching Dates....");
            progressDialogSTEndDate.setMessage("Please Wait....");
            progressDialogSTEndDate.setCancelable(false);
            progressDialogSTEndDate.show();
        }

        @Override
        protected SoapObject doInBackground(String... params) {
            SoapObject request = new SoapObject(NAME_SPACE, METHOD_NAME_FETCH_ST_END_DATE);
            request.addProperty("Days", Days);

            try {
                return xmlParser.getServiceResultSoapObj(URL, SOAP_ACTION_FETCH_ST_END_DATE, request);
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
            progressDialogSTEndDate.dismiss();

            stTv.setText("Start Date:- " + soapObject.getPropertyAsString(0));
            endTv.setText("End Date:- " + soapObject.getPropertyAsString(1));
        }
    }


    public class FetchAddress extends AsyncTask<String, Void, String> {

        String UserId = sp.getString("UserId", null);
        EditText popupAddressEt;

        public FetchAddress(EditText popupAddressEt) {
            this.popupAddressEt = popupAddressEt;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Fetching Address....");
            progressDialog.setMessage("Please Wait....");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            SoapObject request = new SoapObject(NAME_SPACE, METHOD_NAME_FETCH_ADDRESS);

            request.addProperty("UserId", UserId);

            return xmlParser.getServiceResultString(URL, SOAP_ACTION_FETCH_ADDRESS, request);

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            progressDialog.dismiss();
            if (s != null && !s.equalsIgnoreCase(""))
                popupAddressEt.setText(s);

        }
    }

    public class FetchCity extends AsyncTask<String, Void, SoapObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Fetching Cities....");
            progressDialog.setMessage("Please Wait....");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected SoapObject doInBackground(String... params) {

            SoapObject request = new SoapObject(NAME_SPACE, METHOD_NAME_FETCH_CITY);

            try {
                return xmlParser.getServiceResultSoapObj(URL, SOAP_ACTION_FETCH_CITY, request);
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

            progressDialog.dismiss();
            if (soapObject != null) {
                for (int i = 0; i < soapObject.getPropertyCount(); i++) {
                    mbCityList.add(soapObject.getPropertyAsString(i));
                }
                mbCityList.remove(0);
                mbCityList.add(0, "Select City");

                mbCityAdapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, mbCityList);
                mbCitySpinner.setAdapter(mbCityAdapter);

                if (sp.contains("DelCity")) {
                    int position = mbCityList.indexOf(sp.getString("DelCity", null));
                    mbCitySpinner.setSelection(position);
//                    Toast.makeText(getActivity(), sp.getString("DelCity", null), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public class AreaFetch extends AsyncTask<String, Void, SoapObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Fetching Area....");
            progressDialog.setMessage("Please Wait....");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected SoapObject doInBackground(String... params) {

            SoapObject request = new SoapObject(NAME_SPACE, METHOD_NAME_FETCH_AREA);
            request.addProperty("City", selectedCity);

            try {
                return xmlParser.getServiceResultSoapObj(URL, SOAP_ACTION_FETCH_AREA, request);
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

            progressDialog.dismiss();
            if (soapObject != null) {
                for (int i = 0; i < soapObject.getPropertyCount(); i++) {
                    mbAreaList.add(soapObject.getPropertyAsString(i));
                }
                mbAreaList.add(0, "Select Area");
                mbAreaAdapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, mbAreaList);
                mbAreaSpinner.setAdapter(mbAreaAdapter);
                if (sp.contains("DelArea")) {

                    Integer areaposition = mbAreaList.indexOf(sp.getString("DelArea", null));
//                    Toast.makeText(getActivity(), areaposition.toString(), Toast.LENGTH_SHORT).show();
                    if (areaposition != -1)
                        mbAreaSpinner.setSelection(areaposition);
                }
            }
        }
    }

    public class CheckPanelVisiblity extends AsyncTask<String, Void, SoapObject> {
        ProgressDialog progressDialogPanelVisibility;
        String UID = sp.getString("UserId", null);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialogPanelVisibility = new ProgressDialog(getActivity());
            progressDialogPanelVisibility.setTitle("Loading....");
            progressDialogPanelVisibility.setMessage("Please Wait....");
            progressDialogPanelVisibility.setCancelable(false);
            progressDialogPanelVisibility.show();
        }

        @Override
        protected SoapObject doInBackground(String... params) {

            SoapObject request = new SoapObject(NAME_SPACE, METHOD_NAME_MONTHLY_PANEL_VISIBLE);
            request.addProperty("UserId", UID);

            try {
                return xmlParser.getServiceResultSoapObj(URL, SOAP_ACTION_MONTHLY_PANEL_VISIBLE, request);
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

            progressDialogPanelVisibility.dismiss();

            //Toast.makeText(getActivity(), soapObject.toString(), Toast.LENGTH_LONG).show();

            if (soapObject.getPropertyAsString(0).equalsIgnoreCase("CanBook")) {
                mbCanBookPnl.setVisibility(View.VISIBLE);
            } else if (soapObject.getPropertyAsString(0).equalsIgnoreCase("AlreadyBook")) {
                String noOfDay = soapObject.getPropertyAsString(1);
                String STDT = soapObject.getPropertyAsString(2);
                String ENDDT = soapObject.getPropertyAsString(3);
                String Name = soapObject.getPropertyAsString(4);

                mbAlradyBookPnl.setVisibility(View.VISIBLE);
//                mbEatingIv.setVisibility(View.VISIBLE);
                mbAlreadyBookMsgTv.setText("Dear " + Name + ", you have already booked tiffin for " + noOfDay + " Days that is from " + STDT + " to " + ENDDT + ".now you can book tiffin after " + ENDDT + ".");
            }

        }
    }
}

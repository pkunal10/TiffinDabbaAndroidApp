package com.tiffin_dabba.admin.tiffindabba.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.tiffin_dabba.admin.tiffindabba.CartActivity;
import com.tiffin_dabba.admin.tiffindabba.ImportantClasses.CommonFunction;
import com.tiffin_dabba.admin.tiffindabba.ImportantClasses.XMLParser;
import com.tiffin_dabba.admin.tiffindabba.Modals.SimpleTiffins;
import com.tiffin_dabba.admin.tiffindabba.R;

import org.ksoap2.serialization.SoapObject;
import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ADMIN on 20-09-2017.
 */

public class SimpleTiffinFragment extends Fragment {


    Spinner citySpinner, areaSpinner;
    ArrayAdapter<String> cityAdapter, areaAdapter;
    List<String> cityList, areaList;
    String selectedCity;
    List<SimpleTiffins> simpleTiffinsList;
    SharedPreferences sp;
    ProgressDialog progressDialog;
    CommonFunction commonFunction=new CommonFunction();
    List<String> bookForList;
    ArrayAdapter<String> bookForAdapter;
    Button stGoBtn;
    RecyclerView stRv;
    SimpleTiffinAdapter simpleTiffinAdapter;
    XMLParser xmlParser = new XMLParser();
    String HomeTiffinId = "";
    TextView stDelAreaTv;

    public final static String NAME_SPACE = "http://tempuri.org/";
    public final static String URL = "http://tiffindabba.net/MyService.asmx";

    public final static String SOAP_ACTION_FETCH_CITY = "http://tempuri.org/GetCityList";
    public final static String METHOD_NAME_FETCH_CITY = "GetCityList";

    public final static String SOAP_ACTION_FETCH_AREA = "http://tempuri.org/GetAreaList";
    public final static String METHOD_NAME_FETCH_AREA = "GetAreaList";

    public final static String SOAP_ACTION_FETCH_ST = "http://tempuri.org/FetchSimpleTiffinList";
    public final static String METHOD_NAME_FETCH_ST = "FetchSimpleTiffinList";

    public final static String SOAP_ACTION_FETCH_ST_BY_ID = "http://tempuri.org/GetSimpleTiffinDetailsByTiffinId";
    public final static String METHOD_NAME_FETCH_ST_BY_ID = "GetSimpleTiffinDetailsByTiffinId";

    public final static String SOAP_ACTION_FETCH_ADDRESS = "http://tempuri.org/GetUserAddress";
    public final static String METHOD_NAME_FETCH_ADDRESS = "GetUserAddress";

    public final static String SOAP_ACTION_BOOKORDER = "http://tempuri.org/BookSimpleTiffinOrder";
    public final static String METHOD_NAME_BOOKORDER = "BookSimpleTiffinOrder";

    public SimpleTiffinFragment() {

    }

    public SimpleTiffinFragment(String HomeTiffinId) {
        this.HomeTiffinId = HomeTiffinId;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.simple_tiffin_fragment_layout, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sp = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);

        citySpinner = (Spinner) view.findViewById(R.id.CitySpinner);
        areaSpinner = (Spinner) view.findViewById(R.id.AreaSpinner);
        stGoBtn = (Button) view.findViewById(R.id.STGoBtn);
        stRv = (RecyclerView) view.findViewById(R.id.STRv);
        stDelAreaTv=(TextView) view.findViewById(R.id.StDelAreaTv);


        cityList = new ArrayList<>();
        areaList = new ArrayList<>();
        bookForList = new ArrayList<>();
        simpleTiffinsList = new ArrayList<>();

        bookForList.add(0, "Select Book For");
        bookForList.add(1, "Lunch");
        bookForList.add(2, "Dinner");

        if (!commonFunction.IsInternetAvailable(getActivity())) {
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        }

        new FetchCity().execute();


        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(CartActivity.this, cityList.get(position), Toast.LENGTH_SHORT).show();

                if (position == 0) {
                    areaList.clear();
                    stDelAreaTv.setVisibility(View.GONE);
                    simpleTiffinsList.clear();
                    simpleTiffinAdapter = new SimpleTiffinAdapter(getActivity(), simpleTiffinsList);
                    stRv.setAdapter(simpleTiffinAdapter);
                    areaAdapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, areaList);
                    areaSpinner.setAdapter(areaAdapter);
                } else {

                    areaList.clear();
                    stDelAreaTv.setVisibility(View.VISIBLE);
                    selectedCity = parent.getItemAtPosition(position).toString();
                    new AreaFetch().execute();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        areaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    simpleTiffinsList.clear();
                    simpleTiffinAdapter = new SimpleTiffinAdapter(getActivity(), simpleTiffinsList);
                    stRv.setAdapter(simpleTiffinAdapter);
                }
                else
                {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("DelCity", citySpinner.getSelectedItem().toString());
                    editor.putString("DelArea", areaSpinner.getSelectedItem().toString());
                    editor.commit();

//                    if (!HomeTiffinId.equalsIgnoreCase("")) {
//                        OpenPopUp(HomeTiffinId);
//                        HomeTiffinId = "";
//                    } else {
                        simpleTiffinsList.clear();
                        new FetchSimpleTiffinList().execute();
                    //}
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        stGoBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (citySpinner.getSelectedItemPosition() == 0) {
//                    Toast.makeText(getActivity(), "Please Select City.", Toast.LENGTH_SHORT).show();
//                } else if (areaSpinner.getSelectedItemPosition() == 0) {
//                    Toast.makeText(getActivity(), "Please Select Area.", Toast.LENGTH_SHORT).show();
//                } else {
//
//                    SharedPreferences.Editor editor = sp.edit();
//                    editor.putString("DelCity", citySpinner.getSelectedItem().toString());
//                    editor.putString("DelArea", areaSpinner.getSelectedItem().toString());
//                    editor.commit();
//
//                    if (!HomeTiffinId.equalsIgnoreCase("")) {
//                        OpenPopUp(HomeTiffinId);
//                        HomeTiffinId = "";
//                    } else {
//                        simpleTiffinsList.clear();
//                        new FetchSimpleTiffinList().execute();
//                    }
//
//                }
//            }
//        });

    }

    public class FetchSimpleTiffinList extends AsyncTask<String, Void, SoapObject> {

        ProgressDialog progressDialogST;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialogST = new ProgressDialog(getActivity());
            progressDialogST.setTitle("Fetching Tiffins....");
            progressDialogST.setMessage("Please Wait....");
            progressDialogST.setCancelable(false);
            progressDialogST.show();
        }

        @Override
        protected SoapObject doInBackground(String... params) {

            SoapObject request = new SoapObject(NAME_SPACE, METHOD_NAME_FETCH_ST);
            request.addProperty("ProviderCity", selectedCity);

            try {
                return xmlParser.getServiceResultSoapObj(URL, SOAP_ACTION_FETCH_ST, request);
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(SoapObject s) {
            super.onPostExecute(s);

//            Toast.makeText(getActivity(), s.toString(), Toast.LENGTH_LONG).show();
            SetDatainListview(s);

            progressDialogST.dismiss();
        }
    }

    private void SetDatainListview(SoapObject s) {


        SoapObject chkSobj= (SoapObject) s.getProperty(0);

        if(chkSobj.getPropertyAsString("Status").equalsIgnoreCase("Fail"))
        {
            if(HomeTiffinId.equalsIgnoreCase(""))
            {
                Toast.makeText(getActivity(), "NO Tiffin Available.", Toast.LENGTH_LONG).show();
            }
            else
            {
//                Toast.makeText(getActivity(), "This Tiffin Not Available in this city.", Toast.LENGTH_LONG).show();
                AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                builder.setTitle("Error....");
                builder.setMessage("This tiffin is not available in this city.");
                builder.setCancelable(false);
                builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();

                HomeTiffinId="";
            }
        }
        else
        {
            if(!HomeTiffinId.equalsIgnoreCase(""))
            {
                OpenPopUp(HomeTiffinId);
                HomeTiffinId="";
            }

            for (int i = 0; i < s.getPropertyCount(); i++) {
                SoapObject sobj = (SoapObject) s.getProperty(i);

                if (sobj.getPropertyAsString("Status").equalsIgnoreCase("Success")) {
                    SimpleTiffins simpleTiffins = new SimpleTiffins();

                    simpleTiffins.setTiffinId(sobj.getPropertyAsString("TiffinId"));
                    simpleTiffins.setProviderCity(sobj.getPropertyAsString("ProviderCity"));
                    simpleTiffins.setProviderId(sobj.getPropertyAsString("ProviderId"));
                    simpleTiffins.setProviderName(sobj.getPropertyAsString("ProviderName"));
                    simpleTiffins.setSabji(sobj.getPropertyAsString("Sabji"));
                    simpleTiffins.setRoti(sobj.getPropertyAsString("Roti"));
                    simpleTiffins.setDal(sobj.getPropertyAsString("Dal"));
                    simpleTiffins.setChawal(sobj.getPropertyAsString("Chawal"));
                    simpleTiffins.setOther(sobj.getPropertyAsString("Other"));
                    simpleTiffins.setPrice(sobj.getPropertyAsString("Price"));
                    simpleTiffins.setTagline(sobj.getPropertyAsString("Tagline"));
                    simpleTiffins.setRecommend(sobj.getPropertyAsString("Recommend"));
                    simpleTiffins.setImage(sobj.getPropertyAsString("Image"));
                    simpleTiffins.setIsVacation(sobj.getPropertyAsString("IsVacation"));
                    simpleTiffins.setBtnTxt(sobj.getPropertyAsString("BtnTxt"));
                    // simpleTiffins.setAvailable(sobj.getPropertyAsString("Available"));

                    simpleTiffinsList.add(simpleTiffins);

                    simpleTiffinAdapter = new SimpleTiffinAdapter(getActivity(), simpleTiffinsList);
                    stRv.setAdapter(simpleTiffinAdapter);

                    LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
                    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    stRv.setLayoutManager(linearLayoutManager);
                }
            }
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
                    cityList.add(soapObject.getPropertyAsString(i));
                }
                cityList.remove(0);
                cityList.add(0, "Select City");

                cityAdapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, cityList);
                citySpinner.setAdapter(cityAdapter);

                if (sp.contains("DelCity")) {
                    int position = cityList.indexOf(sp.getString("DelCity", null));
                    citySpinner.setSelection(position);
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
                    areaList.add(soapObject.getPropertyAsString(i));
                }
                areaList.add(0, "Select Area");
                areaAdapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, areaList);
                areaSpinner.setAdapter(areaAdapter);

                if (sp.contains("DelArea")) {

                    Integer areaposition = areaList.indexOf(sp.getString("DelArea", null));
//                    Toast.makeText(getActivity(), areaposition.toString(), Toast.LENGTH_SHORT).show();
                    if (areaposition != -1)
                        areaSpinner.setSelection(areaposition);
                }
            }
        }
    }

    public class SimpleTiffinMainViewHolder extends RecyclerView.ViewHolder
    {
        ImageView stIv;
        TextView stRotiTv, stSabjiTv, stDalTv, stChawalTv, stOtherTv, stPriceTv;
        Button stbookBtn;

        public SimpleTiffinMainViewHolder(View convertView) {
            super(convertView);

            stIv = (ImageView) convertView.findViewById(R.id.STIv);
            stRotiTv = (TextView) convertView.findViewById(R.id.STRotiTv);
            stSabjiTv = (TextView) convertView.findViewById(R.id.STSabjiTv);
            stDalTv = (TextView) convertView.findViewById(R.id.STDalTv);
            stChawalTv = (TextView) convertView.findViewById(R.id.STChawalTv);
            stOtherTv = (TextView) convertView.findViewById(R.id.STOtherTv);
            stPriceTv = (TextView) convertView.findViewById(R.id.STPriceTv);
            stbookBtn = (Button) convertView.findViewById(R.id.StBookBtn);
        }
    }

    public class SimpleTiffinAdapter extends RecyclerView.Adapter<SimpleTiffinMainViewHolder>
    {
        Context context;
        List<SimpleTiffins> simpleTiffinsList;

        public SimpleTiffinAdapter(Context context, List<SimpleTiffins> simpleTiffinsList) {

            this.context = context;
            this.simpleTiffinsList = simpleTiffinsList;
        }

        @Override
        public SimpleTiffinMainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View viewST=LayoutInflater.from(context).inflate(R.layout.simple_tiffin_listview_row,parent,false);

            SimpleTiffinMainViewHolder viewHolder =new SimpleTiffinMainViewHolder(viewST);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(SimpleTiffinMainViewHolder holder, int position) {

            final SimpleTiffins simpleTiffins = simpleTiffinsList.get(position);

            String path = simpleTiffins.getImage().substring(1);
            Picasso.with(context).load("http://tiffindabba.net" + path).placeholder(R.mipmap.ic_launcher).fit().centerCrop().into(holder.stIv);

            holder.stSabjiTv.setText("Sabji:- "+simpleTiffins.getSabji());
            holder.stRotiTv.setText("Roti:- "+simpleTiffins.getRoti());
            holder.stDalTv.setText("Dal:- "+simpleTiffins.getDal());
            holder.stChawalTv.setText("Chawal:- "+simpleTiffins.getChawal());
            holder.stOtherTv.setText("Other:- "+simpleTiffins.getOther());
            holder.stPriceTv.setText("Price:- "+simpleTiffins.getPrice());

            holder.stSabjiTv.setVisibility(simpleTiffins.getSabji().equalsIgnoreCase("anyType{}") ? View.GONE : View.VISIBLE);
            holder.stRotiTv.setVisibility(simpleTiffins.getRoti().equalsIgnoreCase("anyType{}") ? View.GONE : View.VISIBLE);

            holder.stbookBtn.setEnabled(simpleTiffins.getIsVacation().equalsIgnoreCase("true")?false:true);
            holder.stbookBtn.setText(simpleTiffins.getBtnTxt().equalsIgnoreCase("anyType{}")?"Book":simpleTiffins.getBtnTxt());


            holder.stbookBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OpenPopUp(simpleTiffins.getTiffinId());
                }
            });

        }

        @Override
        public int getItemCount() {
            return simpleTiffinsList.size();
        }
    }

//    private class SimpleTiffinAdapter extends BaseAdapter {
//        Context context;
//        List<SimpleTiffins> simpleTiffinsList;
//
//        public SimpleTiffinAdapter(Context context, List<SimpleTiffins> simpleTiffinsList) {
//
//            this.context = context;
//            this.simpleTiffinsList = simpleTiffinsList;
//        }
//
//        @Override
//        public int getCount() {
//            return simpleTiffinsList.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return simpleTiffinsList.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//
//            ImageView stIv;
//            TextView stRotiTv, stSabjiTv, stDalTv, stChawalTv, stOtherTv, stPriceTv;
//            Button stbookBtn;
//
//            convertView = LayoutInflater.from(context).inflate(R.layout.simple_tiffin_listview_row, parent, false);
//
//            stIv = (ImageView) convertView.findViewById(R.id.STIv);
//            stRotiTv = (TextView) convertView.findViewById(R.id.STRotiTv);
//            stSabjiTv = (TextView) convertView.findViewById(R.id.STSabjiTv);
//            stDalTv = (TextView) convertView.findViewById(R.id.STDalTv);
//            stChawalTv = (TextView) convertView.findViewById(R.id.STChawalTv);
//            stOtherTv = (TextView) convertView.findViewById(R.id.STOtherTv);
//            stPriceTv = (TextView) convertView.findViewById(R.id.STPriceTv);
//            stbookBtn = (Button) convertView.findViewById(R.id.StBookBtn);
//
//            final SimpleTiffins simpleTiffins = simpleTiffinsList.get(position);
//
//            String path = simpleTiffins.getImage().substring(1);
//            Picasso.with(context).load("http://tiffindabba.net" + path).placeholder(R.mipmap.ic_launcher).fit().centerCrop().into(stIv);
//
//            stSabjiTv.append(simpleTiffins.getSabji());
//            stRotiTv.append(simpleTiffins.getRoti());
//            stDalTv.append(simpleTiffins.getDal());
//            stChawalTv.append(simpleTiffins.getChawal());
//            stOtherTv.append(simpleTiffins.getOther());
//            stPriceTv.append(simpleTiffins.getPrice());
//
//            stSabjiTv.setVisibility(simpleTiffins.getSabji().equalsIgnoreCase("anyType{}") ? View.GONE : View.VISIBLE);
//            stRotiTv.setVisibility(simpleTiffins.getRoti().equalsIgnoreCase("anyType{}") ? View.GONE : View.VISIBLE);
//
//
//            stbookBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    OpenPopUp(simpleTiffins.getTiffinId());
//                }
//            });
//
//            return convertView;
//        }
//    }

    private void OpenPopUp(final String tiffinId) {
        View popUpView = getActivity().getLayoutInflater().inflate(R.layout.order_confirmation_layout_of_simpletiffin, null, false);

        ImageView popCloseBtn;
        TextView popupSabjiTv, popupRotiTv, popupDalTv, popupChawalTv, popupOtherTv, popupPriceTv, popupChangeAddBtn;
        final EditText popupAddressEt;
        Button popupConfirmOrderBtn;
        final Spinner popupBookForSpinner;

        popupAddressEt = (EditText) popUpView.findViewById(R.id.STAddressEt);
        popupSabjiTv = (TextView) popUpView.findViewById(R.id.StPopUpSabjiTv);
        popupRotiTv = (TextView) popUpView.findViewById(R.id.StPopUpRotiTv);
        popupDalTv = (TextView) popUpView.findViewById(R.id.StPopUpDalTv);
        popupChawalTv = (TextView) popUpView.findViewById(R.id.StPopUpChawalTv);
        popupOtherTv = (TextView) popUpView.findViewById(R.id.StPopUpOtherTv);
        popupPriceTv = (TextView) popUpView.findViewById(R.id.StPopUpPriceTv);
        popupConfirmOrderBtn = (Button) popUpView.findViewById(R.id.STConfirmOrderBtn);
        popupBookForSpinner = (Spinner) popUpView.findViewById(R.id.STPopUpBookForSpinner);
        popupChangeAddBtn = (TextView) popUpView.findViewById(R.id.STPopupChangeAddressBtn);
        popCloseBtn = (ImageView) popUpView.findViewById(R.id.StPopUpCloseBtn);

        bookForAdapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, bookForList);
        popupBookForSpinner.setAdapter(bookForAdapter);


        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle("Order Confirmation");
        builder.setView(popUpView);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        new FetchTiffinDetails(tiffinId, popupSabjiTv, popupRotiTv, popupDalTv, popupChawalTv, popupOtherTv, popupPriceTv).execute();
        new FetchAddress(popupAddressEt).execute();

        popupChangeAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupAddressEt.setEnabled(true);
            }
        });

        popupConfirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String bookFor = (String) popupBookForSpinner.getSelectedItem();
                final String UId = sp.getString("UserId", null);
                final String DelArea = (String) areaSpinner.getSelectedItem();
                final String DelAddress = popupAddressEt.getText().toString();

                if (popupBookForSpinner.getSelectedItemPosition() == 0) {
                    Toast.makeText(getActivity(), "Select Book For.", Toast.LENGTH_SHORT).show();
                } else if (popupAddressEt.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getActivity(), "Enter Delivery Address.", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder builderConfirmation = new AlertDialog.Builder(getActivity());
                    builderConfirmation.setTitle("Confirmation");
                    builderConfirmation.setMessage("Are you sure you want to book this order ??");
                    builderConfirmation.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new BookOrder(tiffinId, bookFor, UId, DelArea, DelAddress).execute();
                            alertDialog.dismiss();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
                }
                //Toast.makeText(getActivity(), bookFor + " " + tiffinId + " " + UId + " " + DelArea + " " + DelAddress, Toast.LENGTH_LONG).show();
            }
        });

        popCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }

    public class BookOrder extends AsyncTask<String, Void, SoapObject> {
        ProgressDialog progressDialogBookOrder;
        String bookFor, uId, delArea, delAddress, tiffinId;

        public BookOrder(String tiffinId, String bookFor, String uId, String delArea, String delAddress) {

            this.bookFor = bookFor;
            this.uId = uId;
            this.delArea = delArea;
            this.delAddress = delAddress;
            this.tiffinId = tiffinId;
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
        protected SoapObject doInBackground(String... params) {

            SoapObject request = new SoapObject(NAME_SPACE, METHOD_NAME_BOOKORDER);

            request.addProperty("BookFor", bookFor);
            request.addProperty("TiffinId", tiffinId);
            request.addProperty("UserId", uId);
            request.addProperty("DeliveryArea", delArea);
            request.addProperty("DeliveryAddress", delAddress);

            try {
                return xmlParser.getServiceResultSoapObj(URL, SOAP_ACTION_BOOKORDER, request);
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

            progressDialogBookOrder.dismiss();

            if (soapObject != null) {
                String status = soapObject.getPropertyAsString(0);

                if (status.equalsIgnoreCase("Success")) {
                    Toast.makeText(getActivity(), "Order Is Booked.", Toast.LENGTH_LONG).show();
                } else if (status.equalsIgnoreCase("Fail")) {
                    String bookfr = soapObject.getPropertyAsString(1);
                    String time = soapObject.getPropertyAsString(2);

                    ShowErrMsg(bookfr, time);
                } else {
                    Toast.makeText(getActivity(), "Order Is Not Booked.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void ShowErrMsg(String bookfr, String time) {

        AlertDialog.Builder builderErr = new AlertDialog.Builder(getActivity());
        builderErr.setTitle("Error....");
        builderErr.setMessage("You can't book tiffin for " + bookfr + " after " + time + ".");

        builderErr.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
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

    public class FetchTiffinDetails extends AsyncTask<String, Void, SoapObject> {
        ProgressDialog progressDialogDetails;
        String tiffinId;
        TextView popupSabjiTv, popupRotiTv, popupDalTv, popupChawalTv, popupOtherTv, popupPriceTv;

        public FetchTiffinDetails(String tiffinId, TextView popupSabjiTv, TextView popupRotiTv, TextView popupDalTv, TextView popupChawalTv, TextView popupOtherTv, TextView popupPriceTv) {
            this.tiffinId = tiffinId;
            this.popupSabjiTv = popupSabjiTv;
            this.popupRotiTv = popupRotiTv;
            this.popupDalTv = popupDalTv;
            this.popupChawalTv = popupChawalTv;
            this.popupOtherTv = popupOtherTv;
            this.popupPriceTv = popupPriceTv;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            progressDialogDetails = new ProgressDialog(getActivity());
            progressDialogDetails.setTitle("Fetching Details....");
            progressDialogDetails.setMessage("Please Wait....");
            progressDialogDetails.setCancelable(false);
            progressDialogDetails.show();
        }

        @Override
        protected SoapObject doInBackground(String... params) {

            SoapObject request = new SoapObject(NAME_SPACE, METHOD_NAME_FETCH_ST_BY_ID);
            request.addProperty("TiffinId", tiffinId);

            try {
                return xmlParser.getServiceResultSoapObj(URL, SOAP_ACTION_FETCH_ST_BY_ID, request);
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

            progressDialogDetails.dismiss();

            popupSabjiTv.setVisibility(soapObject.getPropertyAsString("Sabji").equalsIgnoreCase("anyType{}") ? View.GONE : View.VISIBLE);
            popupRotiTv.setVisibility(soapObject.getPropertyAsString("Roti").equalsIgnoreCase("anyType{}") ? View.GONE : View.VISIBLE);

            popupSabjiTv.append(soapObject.getPropertyAsString("Sabji"));
            popupRotiTv.append(soapObject.getPropertyAsString("Roti"));
            popupDalTv.append(soapObject.getPropertyAsString("Dal"));
            popupChawalTv.append(soapObject.getPropertyAsString("Chawal"));
            popupOtherTv.append(soapObject.getPropertyAsString("Other"));
            popupPriceTv.append(soapObject.getPropertyAsString("Price"));
        }
    }
}

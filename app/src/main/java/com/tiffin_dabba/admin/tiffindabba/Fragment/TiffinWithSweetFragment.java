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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.tiffin_dabba.admin.tiffindabba.ImportantClasses.CommonFunction;
import com.tiffin_dabba.admin.tiffindabba.ImportantClasses.XMLParser;
import com.tiffin_dabba.admin.tiffindabba.Modals.SimpleTiffins;
import com.tiffin_dabba.admin.tiffindabba.Modals.TiffinWithSweets;
import com.tiffin_dabba.admin.tiffindabba.R;

import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ADMIN on 20-09-2017.
 */

public class TiffinWithSweetFragment extends Fragment {


    Spinner twsCitySpinner, twsAreaSpinner;
    ArrayAdapter<String> twsCityAdapter, twsAreaAdapter;
    List<String> twsCityList, twsAreaList;
    String selectedCity;
    List<TiffinWithSweets> tiffinWithSweetsList;
    SharedPreferences sp;
    ProgressDialog progressDialog;
    CommonFunction commonFunction=new CommonFunction();
    List<String> bookForList;
    ArrayAdapter<String> bookForAdapter;
    Button twsGoBtn;
    RecyclerView twsRv;
    TiffinWithSweetAdapter tiffinWithSweetAdapter;
    String HomeTiffinId = "";
    XMLParser xmlParser = new XMLParser();
    TextView twsDelAreTv;

    public final static String NAME_SPACE = "http://tempuri.org/";
    public final static String URL = "http://tiffindabba.net/MyService.asmx";

    public final static String SOAP_ACTION_FETCH_CITY = "http://tempuri.org/GetCityList";
    public final static String METHOD_NAME_FETCH_CITY = "GetCityList";

    public final static String SOAP_ACTION_FETCH_AREA = "http://tempuri.org/GetAreaList";
    public final static String METHOD_NAME_FETCH_AREA = "GetAreaList";

    public final static String SOAP_ACTION_FETCH_TWS = "http://tempuri.org/FetchTiffinWithSweetList";
    public final static String METHOD_NAME_FETCH_TWS = "FetchTiffinWithSweetList";

    public final static String SOAP_ACTION_FETCH_TWS_BY_ID = "http://tempuri.org/GetTiffinWithSweetDetailsByTiffinId";
    public final static String METHOD_NAME_FETCH_TWS_BY_ID = "GetTiffinWithSweetDetailsByTiffinId";

    public final static String SOAP_ACTION_FETCH_ADDRESS = "http://tempuri.org/GetUserAddress";
    public final static String METHOD_NAME_FETCH_ADDRESS = "GetUserAddress";

    public final static String SOAP_ACTION_BOOKORDER = "http://tempuri.org/BookTiffinWithSweetOrder";
    public final static String METHOD_NAME_BOOKORDER = "BookTiffinWithSweetOrder";

    public TiffinWithSweetFragment() {

    }

    public TiffinWithSweetFragment(String HomeTiffinId) {
        this.HomeTiffinId = HomeTiffinId;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tiffin_with_sweet_fragment_layout, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sp = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);

        twsCitySpinner = (Spinner) view.findViewById(R.id.TwsCitySpinner);
        twsAreaSpinner = (Spinner) view.findViewById(R.id.TwsAreaSpinner);
        twsGoBtn = (Button) view.findViewById(R.id.TwsGoBtn);
        twsRv = (RecyclerView) view.findViewById(R.id.TwsRv);
        twsDelAreTv=(TextView) view.findViewById(R.id.TwsDelAreaTv);


        twsCityList = new ArrayList<>();
        twsAreaList = new ArrayList<>();
        bookForList = new ArrayList<>();
        tiffinWithSweetsList = new ArrayList<>();

        bookForList.add(0, "Select Book For");
        bookForList.add(1, "Lunch");
        bookForList.add(2, "Dinner");

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        twsRv.setLayoutManager(linearLayoutManager);

        if (!commonFunction.IsInternetAvailable(getActivity())) {
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        }

        new FetchCity().execute();

        twsCitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(CartActivity.this, cityList.get(position), Toast.LENGTH_SHORT).show();

                if (position == 0) {
                    twsAreaList.clear();
                    twsDelAreTv.setVisibility(View.GONE);
                    tiffinWithSweetsList.clear();
                    tiffinWithSweetAdapter = new TiffinWithSweetAdapter(getActivity(), tiffinWithSweetsList);
                    twsRv.setAdapter(tiffinWithSweetAdapter);
                    twsAreaAdapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, twsAreaList);
                    twsAreaSpinner.setAdapter(twsAreaAdapter);
                } else {

                    twsAreaList.clear();
                    twsDelAreTv.setVisibility(View.VISIBLE);
                    selectedCity = parent.getItemAtPosition(position).toString();
                    new AreaFetch().execute();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        twsAreaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    tiffinWithSweetsList.clear();
                    tiffinWithSweetAdapter = new TiffinWithSweetAdapter(getActivity(), tiffinWithSweetsList);
                    twsRv.setAdapter(tiffinWithSweetAdapter);
                } else {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("DelCity", twsCitySpinner.getSelectedItem().toString());
                    editor.putString("DelArea", twsAreaSpinner.getSelectedItem().toString());
                    editor.commit();
//                    if (!HomeTiffinId.equalsIgnoreCase("")) {
//                        OpenPopUp(HomeTiffinId);
//                        HomeTiffinId = "";
//                    } else {
                        tiffinWithSweetsList.clear();
                        new FetchTiffinWithSweetList().execute();
                    //}
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        twsGoBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (twsCitySpinner.getSelectedItemPosition() == 0) {
//                    Toast.makeText(getActivity(), "Please Select City.", Toast.LENGTH_SHORT).show();
//                } else if (twsAreaSpinner.getSelectedItemPosition() == 0) {
//                    Toast.makeText(getActivity(), "Please Select Area.", Toast.LENGTH_SHORT).show();
//                } else {
//
//                    SharedPreferences.Editor editor = sp.edit();
//                    editor.putString("DelCity", twsCitySpinner.getSelectedItem().toString());
//                    editor.putString("DelArea", twsAreaSpinner.getSelectedItem().toString());
//                    editor.commit();
//                    if (!HomeTiffinId.equalsIgnoreCase("")) {
//                        OpenPopUp(HomeTiffinId);
//                        HomeTiffinId = "";
//                    } else {
//                        tiffinWithSweetsList.clear();
//                        new FetchTiffinWithSweetList().execute();
//                    }
//                }
//            }
//        });

    }

    public class FetchTiffinWithSweetList extends AsyncTask<String, Void, SoapObject> {

        ProgressDialog progressDialogTWS;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialogTWS = new ProgressDialog(getActivity());
            progressDialogTWS.setTitle("Fetching Tiffins....");
            progressDialogTWS.setMessage("Please Wait....");
            progressDialogTWS.setCancelable(false);
            progressDialogTWS.show();
        }

        @Override
        protected SoapObject doInBackground(String... params) {

            SoapObject request = new SoapObject(NAME_SPACE, METHOD_NAME_FETCH_TWS);
            request.addProperty("ProviderCity", selectedCity);

            try {
                return xmlParser.getServiceResultSoapObj(URL, SOAP_ACTION_FETCH_TWS, request);
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

            progressDialogTWS.dismiss();
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
        else {
            if(!HomeTiffinId.equalsIgnoreCase(""))
            {
                OpenPopUp(HomeTiffinId);
                HomeTiffinId="";
            }

            for (int i = 0; i < s.getPropertyCount(); i++) {
                SoapObject sobj = (SoapObject) s.getProperty(i);

                if (sobj.getPropertyAsString("Status").equalsIgnoreCase("Success")) {
                    TiffinWithSweets tiffinWithSweets = new TiffinWithSweets();

                    tiffinWithSweets.setTiffinId(sobj.getPropertyAsString("TiffinId"));
                    tiffinWithSweets.setProviderCity(sobj.getPropertyAsString("ProviderCity"));
                    tiffinWithSweets.setProviderId(sobj.getPropertyAsString("ProviderId"));
                    tiffinWithSweets.setProviderName(sobj.getPropertyAsString("ProviderName"));
                    tiffinWithSweets.setSabji(sobj.getPropertyAsString("Sabji"));
                    tiffinWithSweets.setRoti(sobj.getPropertyAsString("Roti"));
                    tiffinWithSweets.setDal(sobj.getPropertyAsString("Dal"));
                    tiffinWithSweets.setChawal(sobj.getPropertyAsString("Chawal"));
                    tiffinWithSweets.setSweet(sobj.getPropertyAsString("Sweet"));
                    tiffinWithSweets.setOther(sobj.getPropertyAsString("Other"));
                    tiffinWithSweets.setPrice(sobj.getPropertyAsString("Price"));
                    tiffinWithSweets.setTagline(sobj.getPropertyAsString("Tagline"));
                    tiffinWithSweets.setRecommend(sobj.getPropertyAsString("Recommend"));
                    tiffinWithSweets.setImage(sobj.getPropertyAsString("Image"));
                    tiffinWithSweets.setIsVacation(sobj.getPropertyAsString("IsVacation"));
                    tiffinWithSweets.setBtnTxt(sobj.getPropertyAsString("BtnTxt"));
                    // simpleTiffins.setAvailable(sobj.getPropertyAsString("Available"));

                    tiffinWithSweetsList.add(tiffinWithSweets);

                    tiffinWithSweetAdapter = new TiffinWithSweetAdapter(getActivity(), tiffinWithSweetsList);
                    twsRv.setAdapter(tiffinWithSweetAdapter);
                }
            }
        }


//        for (int i = 0; i < s.getPropertyCount(); i++) {
//            SoapObject sobj = (SoapObject) s.getProperty(i);
//
//            if (sobj.getPropertyAsString("Status").equalsIgnoreCase("Success")) {
//                TiffinWithSweets tiffinWithSweets = new TiffinWithSweets();
//
//                tiffinWithSweets.setTiffinId(sobj.getPropertyAsString("TiffinId"));
//                tiffinWithSweets.setProviderCity(sobj.getPropertyAsString("ProviderCity"));
//                tiffinWithSweets.setProviderId(sobj.getPropertyAsString("ProviderId"));
//                tiffinWithSweets.setProviderName(sobj.getPropertyAsString("ProviderName"));
//                tiffinWithSweets.setSabji(sobj.getPropertyAsString("Sabji"));
//                tiffinWithSweets.setRoti(sobj.getPropertyAsString("Roti"));
//                tiffinWithSweets.setDal(sobj.getPropertyAsString("Dal"));
//                tiffinWithSweets.setChawal(sobj.getPropertyAsString("Chawal"));
//                tiffinWithSweets.setSweet(sobj.getPropertyAsString("Sweet"));
//                tiffinWithSweets.setOther(sobj.getPropertyAsString("Other"));
//                tiffinWithSweets.setPrice(sobj.getPropertyAsString("Price"));
//                tiffinWithSweets.setTagline(sobj.getPropertyAsString("Tagline"));
//                tiffinWithSweets.setRecommend(sobj.getPropertyAsString("Recommend"));
//                tiffinWithSweets.setImage(sobj.getPropertyAsString("Image"));
//                // simpleTiffins.setAvailable(sobj.getPropertyAsString("Available"));
//
//                tiffinWithSweetsList.add(tiffinWithSweets);
//
//                tiffinWithSweetAdapter = new TiffinWithSweetAdapter(getActivity(), tiffinWithSweetsList);
//                twsRv.setAdapter(tiffinWithSweetAdapter);
//            } else {
//                Toast.makeText(getActivity(), "NO Tiffin Available.", Toast.LENGTH_LONG).show();
//            }
//        }

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
                    twsCityList.add(soapObject.getPropertyAsString(i));
                }
                twsCityList.remove(0);
                twsCityList.add(0, "Select City");

                twsCityAdapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, twsCityList);
                twsCitySpinner.setAdapter(twsCityAdapter);

                if (sp.contains("DelCity")) {
                    int position = twsCityList.indexOf(sp.getString("DelCity", null));
                    twsCitySpinner.setSelection(position);
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
                    twsAreaList.add(soapObject.getPropertyAsString(i));
                }
                twsAreaList.add(0, "Select Area");
                twsAreaAdapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, twsAreaList);
                twsAreaSpinner.setAdapter(twsAreaAdapter);
                if (sp.contains("DelArea")) {

                    Integer areaposition = twsAreaList.indexOf(sp.getString("DelArea", null));
//                    Toast.makeText(getActivity(), areaposition.toString(), Toast.LENGTH_SHORT).show();
                    if (areaposition != -1)
                        twsAreaSpinner.setSelection(areaposition);
                }
            }
        }
    }

    public class TiffinWithSweetMainViewHolder extends RecyclerView.ViewHolder
    {
        ImageView twsIv;
        TextView twsRotiTv, twsSabjiTv, twsDalTv, twsChawalTv, twsSweetTv, twsOtherTv, twsPriceTv;
        Button twsbookBtn;

        public TiffinWithSweetMainViewHolder(View convertView) {
            super(convertView);

            twsIv = (ImageView) convertView.findViewById(R.id.TwsIv);
            twsRotiTv = (TextView) convertView.findViewById(R.id.TwsRotiTv);
            twsSabjiTv = (TextView) convertView.findViewById(R.id.TwsSabjiTv);
            twsDalTv = (TextView) convertView.findViewById(R.id.TwsDalTv);
            twsChawalTv = (TextView) convertView.findViewById(R.id.TwsChawalTv);
            twsSweetTv = (TextView) convertView.findViewById(R.id.TwsSweetTv);
            twsOtherTv = (TextView) convertView.findViewById(R.id.TwsOtherTv);
            twsPriceTv = (TextView) convertView.findViewById(R.id.TwsPriceTv);
            twsbookBtn = (Button) convertView.findViewById(R.id.TwsBookBtn);

        }
    }

    public class TiffinWithSweetAdapter extends RecyclerView.Adapter<TiffinWithSweetMainViewHolder>
    {
        Context context;
        List<TiffinWithSweets> tiffinWithSweetsList;

        public TiffinWithSweetAdapter(Context context, List<TiffinWithSweets> tiffinWithSweetsList) {

            this.context = context;
            this.tiffinWithSweetsList = tiffinWithSweetsList;
        }
        @Override
        public TiffinWithSweetMainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View viewTWS=LayoutInflater.from(context).inflate(R.layout.tiffin_with_sweet_listview_row,parent,false);
            TiffinWithSweetMainViewHolder viewHolder=new TiffinWithSweetMainViewHolder(viewTWS);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(TiffinWithSweetMainViewHolder holder, int position) {

            final TiffinWithSweets tiffinWithSweets = tiffinWithSweetsList.get(position);

            String path = tiffinWithSweets.getImage().substring(1);
            Picasso.with(context).load("http://tiffindabba.net" + path).placeholder(R.mipmap.ic_launcher).fit().centerCrop().into(holder.twsIv);

            holder.twsSabjiTv.setText("Sabji:- "+tiffinWithSweets.getSabji());
            holder.twsRotiTv.setText("Roti:- "+tiffinWithSweets.getRoti());
            holder.twsDalTv.setText("Dal:- "+tiffinWithSweets.getDal());
            holder.twsChawalTv.setText("Chawal:- "+tiffinWithSweets.getChawal());
            holder.twsSweetTv.setText("Sweet:- "+tiffinWithSweets.getSweet());
            holder.twsOtherTv.setText("Other:- "+tiffinWithSweets.getOther());
            holder.twsPriceTv.setText("Price:- "+tiffinWithSweets.getPrice());

            holder.twsSabjiTv.setVisibility(tiffinWithSweets.getSabji().equalsIgnoreCase("anyType{}") ? View.GONE : View.VISIBLE);
            holder.twsRotiTv.setVisibility(tiffinWithSweets.getRoti().equalsIgnoreCase("anyType{}") ? View.GONE : View.VISIBLE);

            holder.twsbookBtn.setEnabled(tiffinWithSweets.getIsVacation().equalsIgnoreCase("true")?false:true);
            holder.twsbookBtn.setText(tiffinWithSweets.getBtnTxt().equalsIgnoreCase("anyType{}")?"Book":tiffinWithSweets.getBtnTxt());


            holder.twsbookBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OpenPopUp(tiffinWithSweets.getTiffinId());
                }
            });

        }

        @Override
        public int getItemCount() {
            return tiffinWithSweetsList.size();
        }
    }

//    private class TiffinWithSweetAdapter extends BaseAdapter {
//        Context context;
//        List<TiffinWithSweets> tiffinWithSweetsList;
//
//        public TiffinWithSweetAdapter(Context context, List<TiffinWithSweets> tiffinWithSweetsList) {
//
//            this.context = context;
//            this.tiffinWithSweetsList = tiffinWithSweetsList;
//        }
//
//        @Override
//        public int getCount() {
//            return tiffinWithSweetsList.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return tiffinWithSweetsList.get(position);
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
//            ImageView twsIv;
//            TextView twsRotiTv, twsSabjiTv, twsDalTv, twsChawalTv, twsSweetTv, twsOtherTv, twsPriceTv;
//            Button twsbookBtn;
//
//            convertView = LayoutInflater.from(context).inflate(R.layout.tiffin_with_sweet_listview_row, parent, false);
//
//            twsIv = (ImageView) convertView.findViewById(R.id.TwsIv);
//            twsRotiTv = (TextView) convertView.findViewById(R.id.TwsRotiTv);
//            twsSabjiTv = (TextView) convertView.findViewById(R.id.TwsSabjiTv);
//            twsDalTv = (TextView) convertView.findViewById(R.id.TwsDalTv);
//            twsChawalTv = (TextView) convertView.findViewById(R.id.TwsChawalTv);
//            twsSweetTv = (TextView) convertView.findViewById(R.id.TwsSweetTv);
//            twsOtherTv = (TextView) convertView.findViewById(R.id.TwsOtherTv);
//            twsPriceTv = (TextView) convertView.findViewById(R.id.TwsPriceTv);
//            twsbookBtn = (Button) convertView.findViewById(R.id.TwsBookBtn);
//
//            final TiffinWithSweets tiffinWithSweets = tiffinWithSweetsList.get(position);
//
//            String path = tiffinWithSweets.getImage().substring(1);
//            Picasso.with(context).load("http://tiffindabba.net" + path).placeholder(R.mipmap.ic_launcher).fit().centerCrop().into(twsIv);
//
//            twsSabjiTv.append(tiffinWithSweets.getSabji());
//            twsRotiTv.append(tiffinWithSweets.getRoti());
//            twsDalTv.append(tiffinWithSweets.getDal());
//            twsChawalTv.append(tiffinWithSweets.getChawal());
//            twsSweetTv.append(tiffinWithSweets.getSweet());
//            twsOtherTv.append(tiffinWithSweets.getOther());
//            twsPriceTv.append(tiffinWithSweets.getPrice());
//
//            twsSabjiTv.setVisibility(tiffinWithSweets.getSabji().equalsIgnoreCase("anyType{}") ? View.GONE : View.VISIBLE);
//            twsRotiTv.setVisibility(tiffinWithSweets.getRoti().equalsIgnoreCase("anyType{}") ? View.GONE : View.VISIBLE);
//
//
//            twsbookBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    OpenPopUp(tiffinWithSweets.getTiffinId());
//                }
//            });
//
//            return convertView;
//        }
//    }

    private void OpenPopUp(final String tiffinId) {
        View popUpView = getActivity().getLayoutInflater().inflate(R.layout.order_confirmation_layout_of_tiffin_with_sweet, null, false);

        ImageView popCloseBtn;
        TextView popupSabjiTv, popupRotiTv, popupDalTv, popupChawalTv, popupSweetTv, popupOtherTv, popupPriceTv, popupChangeAddBtn;
        final EditText popupAddressEt;
        Button popupConfirmOrderBtn;
        final Spinner popupBookForSpinner;

        popupAddressEt = (EditText) popUpView.findViewById(R.id.TwsAddressEt);
        popupSabjiTv = (TextView) popUpView.findViewById(R.id.TwsPopUpSabjiTv);
        popupRotiTv = (TextView) popUpView.findViewById(R.id.TwsPopUpRotiTv);
        popupDalTv = (TextView) popUpView.findViewById(R.id.TwsPopUpDalTv);
        popupChawalTv = (TextView) popUpView.findViewById(R.id.TwsPopUpChawalTv);
        popupSweetTv = (TextView) popUpView.findViewById(R.id.TwsPopUpSweetTv);
        popupOtherTv = (TextView) popUpView.findViewById(R.id.TwsPopUpOtherTv);
        popupPriceTv = (TextView) popUpView.findViewById(R.id.TwsPopUpPriceTv);
        popupConfirmOrderBtn = (Button) popUpView.findViewById(R.id.TwsConfirmOrderBtn);
        popupBookForSpinner = (Spinner) popUpView.findViewById(R.id.TwsPopUpBookForSpinner);
        popupChangeAddBtn = (TextView) popUpView.findViewById(R.id.TwsPopupChangeAddressBtn);
        popCloseBtn = (ImageView) popUpView.findViewById(R.id.TwsPopUpCloseBtn);

        bookForAdapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, bookForList);
        popupBookForSpinner.setAdapter(bookForAdapter);


        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle("Order Confirmation");
        builder.setView(popUpView);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        new FetchTiffinDetails(tiffinId, popupSabjiTv, popupRotiTv, popupDalTv, popupChawalTv, popupSweetTv, popupOtherTv, popupPriceTv).execute();
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
                final String DelArea = (String) twsAreaSpinner.getSelectedItem();
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
        TextView popupSabjiTv, popupRotiTv, popupDalTv, popupChawalTv, popSweetTv, popupOtherTv, popupPriceTv;

        public FetchTiffinDetails(String tiffinId, TextView popupSabjiTv, TextView popupRotiTv, TextView popupDalTv, TextView popupChawalTv, TextView popupSweetTv, TextView popupOtherTv, TextView popupPriceTv) {
            this.tiffinId = tiffinId;
            this.popupSabjiTv = popupSabjiTv;
            this.popupRotiTv = popupRotiTv;
            this.popupDalTv = popupDalTv;
            this.popupChawalTv = popupChawalTv;
            this.popSweetTv = popupSweetTv;
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

            SoapObject request = new SoapObject(NAME_SPACE, METHOD_NAME_FETCH_TWS_BY_ID);
            request.addProperty("TiffinId", tiffinId);

            try {
                return xmlParser.getServiceResultSoapObj(URL, SOAP_ACTION_FETCH_TWS_BY_ID, request);
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
            popSweetTv.append(soapObject.getPropertyAsString("Sweet"));
            popupOtherTv.append(soapObject.getPropertyAsString("Other"));
            popupPriceTv.append(soapObject.getPropertyAsString("Price"));
        }
    }
}

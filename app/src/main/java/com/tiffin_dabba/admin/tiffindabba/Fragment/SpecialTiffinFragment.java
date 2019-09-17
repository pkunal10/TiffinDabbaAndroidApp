package com.tiffin_dabba.admin.tiffindabba.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.tiffin_dabba.admin.tiffindabba.ImportantClasses.CommonFunction;
import com.tiffin_dabba.admin.tiffindabba.ImportantClasses.XMLParser;
import com.tiffin_dabba.admin.tiffindabba.Modals.SpecialTiffins;
import com.tiffin_dabba.admin.tiffindabba.Modals.TiffinWithSweets;
import com.tiffin_dabba.admin.tiffindabba.R;

import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.tiffin_dabba.admin.tiffindabba.Fragment.TiffinWithSweetFragment.SOAP_ACTION_FETCH_TWS;

/**
 * Created by ADMIN on 21-10-2017.
 */

public class SpecialTiffinFragment extends Fragment {

    RecyclerView sptRv;
    TextView sptDelAreaTv;
    Spinner sptCitySpinner, sptAreaSpinner;
    ArrayAdapter<String> sptCityAdapter, sptAreaAdapter;
    List<String> sptCityList, sptAreaList;
    List<SpecialTiffins> specialTiffinsList;
    String HomeTiffinId = "";
    CommonFunction commonFunction=new CommonFunction();
    XMLParser xmlParser = new XMLParser();
    SpecialTiffinAdapter specialTiffinAdapter;
    SharedPreferences sp;

    public final static String NAME_SPACE = "http://tempuri.org/";
    public final static String URL = "http://tiffindabba.net/MyService.asmx";

    public final static String SOAP_ACTION_FETCH_CITY = "http://tempuri.org/GetCityList";
    public final static String METHOD_NAME_FETCH_CITY = "GetCityList";

    public final static String SOAP_ACTION_FETCH_AREA = "http://tempuri.org/GetAreaList";
    public final static String METHOD_NAME_FETCH_AREA = "GetAreaList";

    public final static String SOAP_ACTION_FETCH_SPT = "http://tempuri.org/GetSpecialTiffinList";
    public final static String METHOD_NAME_FETCH_SPT = "GetSpecialTiffinList";

    public final static String SOAP_ACTION_FETCH_SPT_BY_ID = "http://tempuri.org/GetSpecialTiffinById";
    public final static String METHOD_NAME_FETCH_SPT_BY_ID = "GetSpecialTiffinById";

    public final static String SOAP_ACTION_FETCH_ADDRESS = "http://tempuri.org/GetUserAddress";
    public final static String METHOD_NAME_FETCH_ADDRESS = "GetUserAddress";

    public final static String SOAP_ACTION_BOOK_ORDER = "http://tempuri.org/BookSpecialTiffinOrder";
    public final static String METHOD_NAME_BOOK_ORDER = "BookSpecialTiffinOrder";


    public SpecialTiffinFragment() {
    }

    public SpecialTiffinFragment(String homeTiffinId) {
        HomeTiffinId = homeTiffinId;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.special_tiffin_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sptRv = (RecyclerView) view.findViewById(R.id.SPTRv);
        sptDelAreaTv = (TextView) view.findViewById(R.id.SptDelAreaTv);
        sptCitySpinner = (Spinner) view.findViewById(R.id.SPTCitySpinner);
        sptAreaSpinner = (Spinner) view.findViewById(R.id.SPTAreaSpinner);

        sptCityList = new ArrayList<>();
        sptAreaList = new ArrayList<>();
        specialTiffinsList = new ArrayList<>();
        sp = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);

//        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
//        sptRv.setLayoutManager(mLayoutManager);
//        sptRv.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
//        sptRv.setItemAnimator(new DefaultItemAnimator());

        LinearLayoutManager linearLayoutManagerimageRv = new LinearLayoutManager(getActivity());
        linearLayoutManagerimageRv.setOrientation(LinearLayoutManager.VERTICAL);
        sptRv.setLayoutManager(linearLayoutManagerimageRv);

        if (!commonFunction.IsInternetAvailable(getActivity())) {
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        }

        new FetchCity().execute();

        sptCitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(CartActivity.this, cityList.get(position), Toast.LENGTH_SHORT).show();

                if (position == 0) {
                    sptAreaList.clear();
                    sptDelAreaTv.setVisibility(View.GONE);
                    specialTiffinsList.clear();
                    specialTiffinAdapter = new SpecialTiffinAdapter(getActivity(), specialTiffinsList);
                    sptRv.setAdapter(specialTiffinAdapter);
                    sptAreaAdapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, sptAreaList);
                    sptAreaSpinner.setAdapter(sptAreaAdapter);
                } else {

                    sptAreaList.clear();
                    sptDelAreaTv.setVisibility(View.VISIBLE);
                    String selectedCity = parent.getItemAtPosition(position).toString();
                    new AreaFetch(selectedCity).execute();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sptAreaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    specialTiffinsList.clear();
                    specialTiffinAdapter = new SpecialTiffinAdapter(getActivity(), specialTiffinsList);
                    sptRv.setAdapter(specialTiffinAdapter);
                } else {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("DelCity", sptCitySpinner.getSelectedItem().toString());
                    editor.putString("DelArea", sptAreaSpinner.getSelectedItem().toString());
                    editor.commit();
//                    if (!HomeTiffinId.equalsIgnoreCase("")) {
//                        OpenPopup(HomeTiffinId);
//                        HomeTiffinId = "";
//                    } else {
                        specialTiffinsList.clear();
                        new FetchSpecialTiffinList().execute();
                    //}
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public class FetchSpecialTiffinList extends AsyncTask<String, Void, SoapObject> {

        ProgressDialog progressDialogTiffinList;
        String city = sptCitySpinner.getSelectedItem().toString();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialogTiffinList = new ProgressDialog(getActivity());
            progressDialogTiffinList.setTitle("Fetching Tiffins....");
            progressDialogTiffinList.setMessage("Please Wait....");
            progressDialogTiffinList.setCancelable(false);
            progressDialogTiffinList.show();
        }

        @Override
        protected SoapObject doInBackground(String... params) {

            SoapObject request = new SoapObject(NAME_SPACE, METHOD_NAME_FETCH_SPT);
            request.addProperty("ProviderCity", city);

            try {
                return xmlParser.getServiceResultSoapObj(URL, SOAP_ACTION_FETCH_SPT, request);
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
            SetDatainResview(s);

            progressDialogTiffinList.dismiss();
        }
    }

    private void SetDatainResview(SoapObject s) {


        SoapObject chkSobj= (SoapObject) s.getProperty(0);

        if(chkSobj.getPropertyAsString("Status").equalsIgnoreCase("NoTiffin"))
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
        else{

            if(!HomeTiffinId.equalsIgnoreCase(""))
            {
                OpenPopup(HomeTiffinId);
                HomeTiffinId="";
            }

            for (int i = 0; i < s.getPropertyCount(); i++) {
                SoapObject sobj = (SoapObject) s.getProperty(i);

                if (sobj.getPropertyAsString("Status").equalsIgnoreCase("Success")) {
                    SpecialTiffins specialTiffins = new SpecialTiffins();

                    specialTiffins.setTiffinId(sobj.getPropertyAsString("TiffinId"));
                    specialTiffins.setProviderCity(sobj.getPropertyAsString("ProviderCity"));
                    specialTiffins.setProviderId(sobj.getPropertyAsString("ProviderId"));
                    specialTiffins.setTiffinName((sobj.getPropertyAsString("TiffinName")));
                    specialTiffins.setDescription(sobj.getPropertyAsString("Description"));
                    specialTiffins.setPrice(sobj.getPropertyAsString("Price"));
                    specialTiffins.setStartTime(sobj.getPropertyAsString("StartTime"));
                    specialTiffins.setEndTime(sobj.getPropertyAsString("EndTime"));
                    specialTiffins.setImage(sobj.getPropertyAsString("Image"));
                    specialTiffins.setIsVacation(sobj.getPropertyAsString("IsVacation"));
                    specialTiffins.setBtnTxt(sobj.getPropertyAsString("BtnTxt"));

                    specialTiffinsList.add(specialTiffins);

                    specialTiffinAdapter = new SpecialTiffinAdapter(getActivity(), specialTiffinsList);
                    sptRv.setAdapter(specialTiffinAdapter);
                }
            }
        }

//        for (int i = 0; i < s.getPropertyCount(); i++) {
//            SoapObject sobj = (SoapObject) s.getProperty(i);
//
//            if (sobj.getPropertyAsString("Status").equalsIgnoreCase("Success")) {
//                SpecialTiffins specialTiffins = new SpecialTiffins();
//
//                specialTiffins.setTiffinId(sobj.getPropertyAsString("TiffinId"));
//                specialTiffins.setProviderCity(sobj.getPropertyAsString("ProviderCity"));
//                specialTiffins.setProviderId(sobj.getPropertyAsString("ProviderId"));
//                specialTiffins.setTiffinName((sobj.getPropertyAsString("TiffinName")));
//                specialTiffins.setDescription(sobj.getPropertyAsString("Description"));
//                specialTiffins.setPrice(sobj.getPropertyAsString("Price"));
//                specialTiffins.setStartTime(sobj.getPropertyAsString("StartTime"));
//                specialTiffins.setEndTime(sobj.getPropertyAsString("EndTime"));
//                specialTiffins.setImage(sobj.getPropertyAsString("Image"));
//
//                specialTiffinsList.add(specialTiffins);
//
//                specialTiffinAdapter = new SpecialTiffinAdapter(getActivity(), specialTiffinsList);
//                sptRv.setAdapter(specialTiffinAdapter);
//            } else {
//                Toast.makeText(getActivity(), "NO Tiffin Available.", Toast.LENGTH_LONG).show();
//            }
//        }

    }

    public class FetchCity extends AsyncTask<String, Void, SoapObject> {
        ProgressDialog progressDialogCity;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialogCity = new ProgressDialog(getActivity());
            progressDialogCity.setTitle("Fetching Cities....");
            progressDialogCity.setMessage("Please Wait....");
            progressDialogCity.setCancelable(false);
            progressDialogCity.show();
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

            progressDialogCity.dismiss();
            if (soapObject != null) {
                for (int i = 0; i < soapObject.getPropertyCount(); i++) {
                    sptCityList.add(soapObject.getPropertyAsString(i));
                }
                sptCityList.remove(0);
                sptCityList.add(0, "Select City");

                sptCityAdapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, sptCityList);
                sptCitySpinner.setAdapter(sptCityAdapter);

                if (sp.contains("DelCity")) {
                    int position = sptCityList.indexOf(sp.getString("DelCity", null));
                    sptCitySpinner.setSelection(position);
//                   Toast.makeText(getActivity(), sp.getString("DelCity", null), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public class AreaFetch extends AsyncTask<String, Void, SoapObject> {

        ProgressDialog progressDialogArea;
        String selectedCity;

        public AreaFetch(String selectedCity) {
            this.selectedCity = selectedCity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialogArea = new ProgressDialog(getActivity());
            progressDialogArea.setTitle("Fetching Area....");
            progressDialogArea.setMessage("Please Wait....");
            progressDialogArea.setCancelable(false);
            //progressDialogArea.show();
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

            progressDialogArea.dismiss();
            if (soapObject != null) {
                for (int i = 0; i < soapObject.getPropertyCount(); i++) {
                    sptAreaList.add(soapObject.getPropertyAsString(i));
                }
                sptAreaList.add(0, "Select Area");
                sptAreaAdapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, sptAreaList);
                sptAreaSpinner.setAdapter(sptAreaAdapter);
                if (sp.contains("DelArea")) {

                    Integer areaposition = sptAreaList.indexOf(sp.getString("DelArea", null));
//                    Toast.makeText(getActivity(), areaposition.toString(), Toast.LENGTH_SHORT).show();
                    if (areaposition != -1)
                        sptAreaSpinner.setSelection(areaposition);
                }
            }
        }
    }

    public class SpecialTiffinViewHolder extends RecyclerView.ViewHolder {
        ImageView sptIv;
        TextView sptTimeTv, sptNameTv, sptDescriptionTv, sptPriceTv;
        Button sptBookBtn;

        public SpecialTiffinViewHolder(View itemView) {
            super(itemView);

            sptIv = (ImageView) itemView.findViewById(R.id.SPTIv);
            sptTimeTv = (TextView) itemView.findViewById(R.id.SPTBookTimeTv);
            sptNameTv = (TextView) itemView.findViewById(R.id.SPTNameTv);
            sptDescriptionTv = (TextView) itemView.findViewById(R.id.SPTDescriptionTv);
            sptPriceTv = (TextView) itemView.findViewById(R.id.SPTPriceTv);
            sptBookBtn = (Button) itemView.findViewById(R.id.SPTBookBtn);
        }
    }

    public class SpecialTiffinAdapter extends RecyclerView.Adapter<SpecialTiffinViewHolder> {
        Context context;
        List<SpecialTiffins> specialTiffinsList;

        public SpecialTiffinAdapter(Context context, List<SpecialTiffins> specialTiffinsList) {
            this.context = context;
            this.specialTiffinsList = specialTiffinsList;
        }

        @Override
        public SpecialTiffinViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(context).inflate(R.layout.special_tiffin_list_row, parent, false);
            SpecialTiffinViewHolder specialTiffinViewHolder = new SpecialTiffinViewHolder(view);
            return specialTiffinViewHolder;
        }

        @Override
        public void onBindViewHolder(SpecialTiffinViewHolder holder, int position) {

            final SpecialTiffins specialTiffins = specialTiffinsList.get(position);

            String path = specialTiffins.getImage().substring(1);

            Picasso.with(context).load("http://tiffindabba.net" + path).fit().centerCrop().placeholder(R.mipmap.ic_launcher).into(holder.sptIv);
            holder.sptNameTv.setText("Tiffin Name:- " + specialTiffins.getTiffinName());
            holder.sptTimeTv.append("Booking Time:- " + specialTiffins.getStartTime() + " to " + specialTiffins.getEndTime());
            holder.sptDescriptionTv.setText("Description:- " + specialTiffins.getDescription());
            holder.sptPriceTv.setText("Price:- " + specialTiffins.getPrice());

            holder.sptBookBtn.setEnabled(specialTiffins.getIsVacation().equalsIgnoreCase("true")?false:true);
            holder.sptBookBtn.setText(specialTiffins.getBtnTxt().equalsIgnoreCase("anyType{}")?"Book":specialTiffins.getBtnTxt());

            holder.sptBookBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OpenPopup(specialTiffins.getTiffinId());
                }
            });
        }

        @Override
        public int getItemCount() {
            return specialTiffinsList.size();
        }
    }

    private void OpenPopup(final String tiffinId) {

        ImageView sptPopupCloseBtn;
        TextView sptPopupTiffinNameTv, sptPopupDescriptionTv, sptPopupPriceTv, sptPopupChangeAddBtn;
        final EditText sptPopupAddressEt;
        Button sptPopupOdrConfmBtn;

        View sptOdrConfmPopup = LayoutInflater.from(getActivity()).inflate(R.layout.order_confirmation_layout_of_special_tiffin, null);

        sptPopupCloseBtn = (ImageView) sptOdrConfmPopup.findViewById(R.id.SPTPopUpCloseBtn);
        sptPopupAddressEt = (EditText) sptOdrConfmPopup.findViewById(R.id.SPTAddressEt);
        sptPopupTiffinNameTv = (TextView) sptOdrConfmPopup.findViewById(R.id.SPTPopupTiffinNameTv);
        sptPopupDescriptionTv = (TextView) sptOdrConfmPopup.findViewById(R.id.SPTPopupDescriptionTv);
        sptPopupPriceTv = (TextView) sptOdrConfmPopup.findViewById(R.id.SPTPopupPriceTv);
        sptPopupChangeAddBtn = (TextView) sptOdrConfmPopup.findViewById(R.id.SPTPopupChangeAddressBtn);
        sptPopupOdrConfmBtn = (Button) sptOdrConfmPopup.findViewById(R.id.SPTConfirmOrderBtn);


        AlertDialog.Builder builderSPT = new AlertDialog.Builder(getActivity());
        builderSPT.setView(sptOdrConfmPopup);
        builderSPT.setCancelable(false);

        final AlertDialog alertDialogSPT = builderSPT.create();
        alertDialogSPT.show();

        new FetchSpecialTiffinById(tiffinId, sptPopupTiffinNameTv, sptPopupDescriptionTv, sptPopupPriceTv).execute();
        new FetchAddress(sptPopupAddressEt).execute();

        sptPopupCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogSPT.dismiss();
            }
        });

        sptPopupChangeAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sptPopupAddressEt.setEnabled(true);
            }
        });

        sptPopupOdrConfmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Are you sure you want to book this order ??");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (sptPopupAddressEt.getText().toString().equalsIgnoreCase("")) {
                            Toast.makeText(getActivity(), "Please Enter Delivery Address", Toast.LENGTH_SHORT).show();
                            sptPopupAddressEt.requestFocus();
                        } else {
                            String UserId = sp.getString("UserId", null);
                            String DelCity = sptCitySpinner.getSelectedItem().toString();
                            String DelArea = sptAreaSpinner.getSelectedItem().toString();
                            String DelAddress = sptPopupAddressEt.getText().toString();

                            new BookOrder(UserId, tiffinId, DelCity, DelArea, DelAddress).execute();
                            alertDialogSPT.dismiss();
                        }

                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
            }
        });

    }

    public class BookOrder extends AsyncTask<String, Void, SoapObject> {
        ProgressDialog progressDialogBookOrder;
        String userId, tiffinId, delCity, delArea, delAddress;

        public BookOrder(String userId, String tiffinId, String delCity, String delArea, String delAddress) {

            this.userId = userId;
            this.tiffinId = tiffinId;
            this.delCity = delCity;
            this.delArea = delArea;
            this.delAddress = delAddress;

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

            SoapObject request = new SoapObject(NAME_SPACE, METHOD_NAME_BOOK_ORDER);

            request.addProperty("UserId", userId);
            request.addProperty("TiffinId", tiffinId);
            request.addProperty("DelCity", delCity);
            request.addProperty("DelArea", delArea);
            request.addProperty("DelAddress", delAddress);

            try {
                return xmlParser.getServiceResultSoapObj(URL, SOAP_ACTION_BOOK_ORDER, request);
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
                if (soapObject.getPropertyAsString(0).equalsIgnoreCase("Success")) {
                    Toast.makeText(getActivity(), "Your Order Is Booked.", Toast.LENGTH_LONG).show();
                } else if (soapObject.getPropertyAsString(0).equalsIgnoreCase("CantBook")) {
                    String StTime = soapObject.getPropertyAsString(1);
                    String ENDTime = soapObject.getPropertyAsString(2);

                    ShowErr(StTime, ENDTime);
                }
            }
        }
    }

    private void ShowErr(String stTime, String endTime) {

        AlertDialog.Builder buildererr = new AlertDialog.Builder(getActivity());
        buildererr.setTitle("Error....");
        buildererr.setMessage("You can book this tiffin from " + stTime + " to " + endTime + ".");
        buildererr.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }

    public class FetchSpecialTiffinById extends AsyncTask<String, Void, SoapObject> {
        ProgressDialog progressDialogDetails;
        String tiffinId;
        TextView sptPopupTiffinNameTv, sptPopupDescriptionTv, sptPopupPriceTv;

        public FetchSpecialTiffinById(String tiffinId, TextView sptPopupTiffinNameTv, TextView sptPopupDescriptionTv, TextView sptPopupPriceTv) {

            this.tiffinId = tiffinId;
            this.sptPopupTiffinNameTv = sptPopupTiffinNameTv;
            this.sptPopupDescriptionTv = sptPopupDescriptionTv;
            this.sptPopupPriceTv = sptPopupPriceTv;

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
            SoapObject request = new SoapObject(NAME_SPACE, METHOD_NAME_FETCH_SPT_BY_ID);
            request.addProperty("TiffinId", tiffinId);

            try {
                return xmlParser.getServiceResultSoapObj(URL, SOAP_ACTION_FETCH_SPT_BY_ID, request);
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

            sptPopupTiffinNameTv.setText("Tiffin Name:- " + soapObject.getPropertyAsString("TiffinName"));
            sptPopupDescriptionTv.setText("Description:- " + soapObject.getPropertyAsString("Description"));
            sptPopupPriceTv.setText("Price:- " + soapObject.getPropertyAsString("Price"));
        }
    }

    public class FetchAddress extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialogAddress;
        String UserId = sp.getString("UserId", null);
        EditText popupAddressEt;

        public FetchAddress(EditText popupAddressEt) {
            this.popupAddressEt = popupAddressEt;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialogAddress = new ProgressDialog(getActivity());
            progressDialogAddress.setTitle("Fetching Address....");
            progressDialogAddress.setMessage("Please Wait....");
            progressDialogAddress.setCancelable(false);
            progressDialogAddress.show();
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

            progressDialogAddress.dismiss();
            if (s != null && !s.equalsIgnoreCase(""))
                popupAddressEt.setText(s);

        }
    }
}

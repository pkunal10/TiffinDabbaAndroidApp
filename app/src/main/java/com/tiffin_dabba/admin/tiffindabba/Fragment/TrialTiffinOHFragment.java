package com.tiffin_dabba.admin.tiffindabba.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.tiffin_dabba.admin.tiffindabba.ImportantClasses.CommonFunction;
import com.tiffin_dabba.admin.tiffindabba.ImportantClasses.TrialTiffins;
import com.tiffin_dabba.admin.tiffindabba.ImportantClasses.XMLParser;
import com.tiffin_dabba.admin.tiffindabba.Modals.SimpleTiffins;
import com.tiffin_dabba.admin.tiffindabba.R;

import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ADMIN on 14-10-2017.
 */

public class TrialTiffinOHFragment extends Fragment {

    SharedPreferences sp;
    XMLParser xmlParser=new XMLParser();
    LinearLayout tTOHNoOdrPnl;
    CommonFunction commonFunction=new CommonFunction();
    List<TrialTiffins> TTOHList=new ArrayList<>();
    RecyclerView ttOHRv;
    TrialTiffinAdapter trialTiffinAdapter;

    public final static String NAME_SPACE = "http://tempuri.org/";
    public final static String URL = "http://tiffindabba.net/MyService.asmx";

    public final static String SOAP_ACTION_FETCH_TTOH = "http://tempuri.org/GetTrialTiffinOrderHistory";
    public final static String METHOD_NAME_FETCH_TTOH = "GetTrialTiffinOrderHistory";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.trial_tiffin_oh_fragment_layout,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sp=getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);

        ttOHRv=(RecyclerView) view.findViewById(R.id.TTOHRv);
        tTOHNoOdrPnl=(LinearLayout) view.findViewById(R.id.TTOHNoOderPnl);

        if (!commonFunction.IsInternetAvailable(getActivity())) {
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        }

        new GetTTOrderHistory().execute();

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ttOHRv.setLayoutManager(linearLayoutManager);
    }
    public class GetTTOrderHistory extends AsyncTask<String,Void,SoapObject>
    {
        String UID=sp.getString("UserId",null);
        ProgressDialog progressDialogTTOH;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialogTTOH=new ProgressDialog(getActivity());
            progressDialogTTOH.setTitle("Fetching Order History....");
            progressDialogTTOH.setMessage("Please Wait....");
            progressDialogTTOH.setCancelable(false);
            progressDialogTTOH.show();
        }

        @Override
        protected SoapObject doInBackground(String... params) {

            SoapObject request=new SoapObject(NAME_SPACE,METHOD_NAME_FETCH_TTOH);

            request.addProperty("UserId",UID);

            try {
                return xmlParser.getServiceResultSoapObj(URL,SOAP_ACTION_FETCH_TTOH,request);
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

            progressDialogTTOH.dismiss();

            if(soapObject!=null){
                SoapObject sobj= (SoapObject) soapObject.getProperty(0);
                if(sobj.getPropertyAsString("Status").equalsIgnoreCase("Success"))
                {
                    FillDataInListView(soapObject);
                    trialTiffinAdapter=new TrialTiffinAdapter(getActivity(),TTOHList);
                    ttOHRv.setAdapter(trialTiffinAdapter);
                }
                else if(sobj.getPropertyAsString("Status").equalsIgnoreCase("NoOrder"))
                {
                    tTOHNoOdrPnl.setVisibility(View.VISIBLE);
                }
            }
        }
    }
    private void FillDataInListView(SoapObject soapObject) {

        TTOHList.clear();
        for (int i=0;i<soapObject.getPropertyCount();i++)
        {
            SoapObject sobj= (SoapObject) soapObject.getProperty(i);
            TrialTiffins trialTiffins=new TrialTiffins();

            trialTiffins.setBookDate(sobj.getPropertyAsString("BookDate"));
            trialTiffins.setBookFor(sobj.getPropertyAsString("BookFor"));
            trialTiffins.setPrice(sobj.getPropertyAsString("Price"));
            trialTiffins.setImage(sobj.getPropertyAsString("Image"));
            trialTiffins.setOrderStatus(sobj.getPropertyAsString("OrderStatus"));

            TTOHList.add(trialTiffins);
        }
    }

    public class TrialTiffinOHViewHolder extends RecyclerView.ViewHolder
    {
        ImageView tTOHIv;
        TextView tTOHBookForTv,tTOHBookDateTv,tTOHOdrStatus,tTOHPriceTv;
        public TrialTiffinOHViewHolder(View convertView) {
            super(convertView);

            tTOHIv= (ImageView) convertView.findViewById(R.id.TTOHIv);
            tTOHBookForTv=(TextView) convertView.findViewById(R.id.TTOHBookForTv);
            tTOHBookDateTv=(TextView) convertView.findViewById(R.id.TTOHBookDateTv);
            tTOHOdrStatus=(TextView) convertView.findViewById(R.id.TTOHOderStatusTv);
            tTOHPriceTv=(TextView) convertView.findViewById(R.id.TTOHPriceTv);
        }
    }

    public class TrialTiffinAdapter extends RecyclerView.Adapter<TrialTiffinOHViewHolder>
    {
        Context context;
        List<TrialTiffins> ttohList;

        public TrialTiffinAdapter(Context context, List<TrialTiffins> ttohList) {

            this.context=context;
            this.ttohList=ttohList;

        }

        @Override
        public TrialTiffinOHViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View viewTTOH=LayoutInflater.from(context).inflate(R.layout.ttoh_listview_row,parent,false);

            TrialTiffinOHViewHolder viewHolder =new TrialTiffinOHViewHolder (viewTTOH);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(TrialTiffinOHViewHolder holder, int position) {

            TrialTiffins trialTiffins=ttohList.get(position);

            String path=trialTiffins.getImage().substring(1);

            Picasso.with(context).load("http://tiffindabba.net" + path).placeholder(R.drawable.shortcut_icon).fit().centerCrop().into(holder.tTOHIv);
            holder.tTOHBookForTv.setText("Book For:- "+trialTiffins.getBookFor());
            holder.tTOHBookDateTv.setText("Date:- "+trialTiffins.getBookDate());
            holder.tTOHOdrStatus.setText("Status:- "+trialTiffins.getOrderStatus());
            holder.tTOHPriceTv.setText("Price:- "+trialTiffins.getPrice());

        }

        @Override
        public int getItemCount() {
            return ttohList.size();
        }
    }

//    private class TrialTiffinAdapter extends BaseAdapter
//    {
//        Context context;
//        List<TrialTiffins> ttohList;
//
//        public TrialTiffinAdapter(Context context, List<TrialTiffins> ttohList) {
//
//            this.context=context;
//            this.ttohList=ttohList;
//
//        }
//
//        @Override
//        public int getCount() {
//            return ttohList.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return ttohList.get(position);
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
//            ImageView tTOHIv;
//            TextView tTOHBookForTv,tTOHBookDateTv,tTOHOdrStatus,tTOHPriceTv;
//
//            convertView=LayoutInflater.from(context).inflate(R.layout.ttoh_listview_row,parent,false);
//
//            tTOHIv= (ImageView) convertView.findViewById(R.id.TTOHIv);
//            tTOHBookForTv=(TextView) convertView.findViewById(R.id.TTOHBookForTv);
//            tTOHBookDateTv=(TextView) convertView.findViewById(R.id.TTOHBookDateTv);
//            tTOHOdrStatus=(TextView) convertView.findViewById(R.id.TTOHOderStatusTv);
//            tTOHPriceTv=(TextView) convertView.findViewById(R.id.TTOHPriceTv);
//
//            TrialTiffins trialTiffins=ttohList.get(position);
//
//            String path=trialTiffins.getImage().substring(1);
//
//            Picasso.with(context).load("http://tiffindabba.net" + path).placeholder(R.drawable.shortcut_icon).fit().centerCrop().into(tTOHIv);
//            tTOHBookForTv.append(trialTiffins.getBookFor());
//            tTOHBookDateTv.append(trialTiffins.getBookDate());
//            tTOHOdrStatus.append(trialTiffins.getOrderStatus());
//            tTOHPriceTv.append(trialTiffins.getPrice());
//
//            return convertView;
//        }
//    }
}

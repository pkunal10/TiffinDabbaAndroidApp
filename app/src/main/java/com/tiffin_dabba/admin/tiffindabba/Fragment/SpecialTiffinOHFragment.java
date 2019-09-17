package com.tiffin_dabba.admin.tiffindabba.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
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
import com.tiffin_dabba.admin.tiffindabba.ImportantClasses.XMLParser;
import com.tiffin_dabba.admin.tiffindabba.Modals.SimpleTiffins;
import com.tiffin_dabba.admin.tiffindabba.Modals.SpecialTiffins;
import com.tiffin_dabba.admin.tiffindabba.R;

import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ADMIN on 14-10-2017.
 */

public class SpecialTiffinOHFragment extends Fragment {

    SharedPreferences sp;
    XMLParser xmlParser=new XMLParser();
    LinearLayout sPTOHNoOdrPnl;
    CommonFunction commonFunction=new CommonFunction();
    List<SpecialTiffins> SPTOHList=new ArrayList<>();
    RecyclerView sptOHRv;
    SpecialTiffinOHAdapter specialTiffinOHAdapter;

    public final static String NAME_SPACE = "http://tempuri.org/";
    public final static String URL = "http://tiffindabba.net/MyService.asmx";

    public final static String SOAP_ACTION_FETCH_SPTOH = "http://tempuri.org/GetSpecialTiffinOrderHistory";
    public final static String METHOD_NAME_FETCH_SPTOH = "GetSpecialTiffinOrderHistory";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.special_tiffin_oh_fragment_layout,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sp=getActivity().getSharedPreferences("pref",Context.MODE_PRIVATE);

        sptOHRv=(RecyclerView) view.findViewById(R.id.SPTOHRv);
        sPTOHNoOdrPnl=(LinearLayout) view.findViewById(R.id.SPTOHNoOderPnl);


        if (!commonFunction.IsInternetAvailable(getActivity())) {
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        }

        new GetSPTOrderHistory().execute();


        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        sptOHRv.setLayoutManager(linearLayoutManager);
    }


    public class GetSPTOrderHistory extends AsyncTask<String,Void,SoapObject>
    {
        String UID=sp.getString("UserId",null);
        ProgressDialog progressDialogSPTOH;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialogSPTOH=new ProgressDialog(getActivity());
            progressDialogSPTOH.setTitle("Fetching Order History....");
            progressDialogSPTOH.setMessage("Please Wait....");
            progressDialogSPTOH.setCancelable(false);
            progressDialogSPTOH.show();
        }

        @Override
        protected SoapObject doInBackground(String... params) {

            SoapObject request=new SoapObject(NAME_SPACE,METHOD_NAME_FETCH_SPTOH);

            request.addProperty("UserId",UID);

            try {
                return xmlParser.getServiceResultSoapObj(URL,SOAP_ACTION_FETCH_SPTOH,request);
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

            progressDialogSPTOH.dismiss();

            if(soapObject!=null){
                SoapObject sobj= (SoapObject) soapObject.getProperty(0);
                if(sobj.getPropertyAsString("Status").equalsIgnoreCase("Success"))
                {
                    FillDataInRecyclerView(soapObject);
                    specialTiffinOHAdapter=new SpecialTiffinOHAdapter(getActivity(),SPTOHList);
                    sptOHRv.setAdapter(specialTiffinOHAdapter);
                }
                else if(sobj.getPropertyAsString("Status").equalsIgnoreCase("NoOrder"))
                {
                    sPTOHNoOdrPnl.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void FillDataInRecyclerView(SoapObject soapObject) {

        SPTOHList.clear();
        for (int i=0;i<soapObject.getPropertyCount();i++)
        {
            SoapObject sobj= (SoapObject) soapObject.getProperty(i);
            SpecialTiffins specialTiffins = new SpecialTiffins();

            specialTiffins.setTiffinName((sobj.getPropertyAsString("TiffinName")));
            specialTiffins.setDescription(sobj.getPropertyAsString("Description"));
            specialTiffins.setPrice(sobj.getPropertyAsString("Price"));
            specialTiffins.setImage(sobj.getPropertyAsString("Image"));
            specialTiffins.setBookDate(sobj.getPropertyAsString("BookDate"));
            specialTiffins.setBookTime(sobj.getPropertyAsString("BookTime"));
            specialTiffins.setOrderStatus(sobj.getPropertyAsString("OrderStatus"));

            SPTOHList.add(specialTiffins);
        }
    }


    public class SpecialTiffinOHViewHolder extends RecyclerView.ViewHolder
    {
        ImageView sptOHIv;
        TextView sptOHTimeTv,sptOHBookDateTv,sptOHOdrStatusTv,sptOHNameTv,sptOHDescriptionTv,sptOHPriceTv;

        public SpecialTiffinOHViewHolder(View itemView) {
            super(itemView);

            sptOHIv=(ImageView) itemView.findViewById(R.id.SPTOHIv);
            sptOHNameTv=(TextView) itemView.findViewById(R.id.SPTOHNameTv);
            sptOHDescriptionTv=(TextView) itemView.findViewById(R.id.SPTOHDescriptionTv);
            sptOHPriceTv=(TextView) itemView.findViewById(R.id.SPTOHPriceTv);
            sptOHBookDateTv=(TextView) itemView.findViewById(R.id.SPTOHBookDateTv);
            sptOHTimeTv=(TextView) itemView.findViewById(R.id.SPTOHBookTimeTv);
            sptOHOdrStatusTv=(TextView) itemView.findViewById(R.id.SPTOHOderStatusTv);

        }
    }

    private class SpecialTiffinOHAdapter extends RecyclerView.Adapter<SpecialTiffinOHViewHolder>
    {
        Context context;
        List<SpecialTiffins> sptohList;

        public SpecialTiffinOHAdapter(Context context, List<SpecialTiffins> sptohList) {

            this.context=context;
            this.sptohList=sptohList;

        }


        @Override
        public SpecialTiffinOHViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View viewSPTOH=LayoutInflater.from(context).inflate(R.layout.sptoh_resview_row,parent,false);

            SpecialTiffinOHViewHolder viewHolder=new SpecialTiffinOHViewHolder(viewSPTOH);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(SpecialTiffinOHViewHolder holder, int position) {

            SpecialTiffins specialTiffins=sptohList.get(position);

            String path=specialTiffins.getImage().substring(1);

            Picasso.with(context).load("http://tiffindabba.net"+path).placeholder(R.mipmap.ic_launcher).fit().centerCrop().into(holder.sptOHIv);
            holder.sptOHTimeTv.setText("Time:- "+specialTiffins.getBookTime());
            holder.sptOHBookDateTv.setText("Date:- "+specialTiffins.getBookDate());
            holder.sptOHOdrStatusTv.setText("Status:- "+specialTiffins.getOrderStatus());
            holder.sptOHNameTv.setText("Tiffin Name:- "+specialTiffins.getTiffinName());
            holder.sptOHDescriptionTv.setText("Description:- "+specialTiffins.getDescription());
            holder.sptOHPriceTv.setText("Price:- "+specialTiffins.getPrice());

        }

        @Override
        public int getItemCount() {
            return sptohList.size();
        }
    }
}

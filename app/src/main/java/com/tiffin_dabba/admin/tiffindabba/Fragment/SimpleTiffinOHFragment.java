package com.tiffin_dabba.admin.tiffindabba.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
import com.tiffin_dabba.admin.tiffindabba.R;

import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ADMIN on 14-10-2017.
 */

public class SimpleTiffinOHFragment extends Fragment {

    SharedPreferences sp;
    XMLParser xmlParser=new XMLParser();
    LinearLayout sTOHNoOdrPnl;
    CommonFunction commonFunction=new CommonFunction();
    List<SimpleTiffins> STOHList=new ArrayList<>();
    RecyclerView stOHRv;
    SimpleTiffinOHAdapter simpleTiffinOHAdapter;

    public final static String NAME_SPACE = "http://tempuri.org/";
    public final static String URL = "http://tiffindabba.net/MyService.asmx";

    public final static String SOAP_ACTION_FETCH_STOH = "http://tempuri.org/GetSimpleTiffinOrderHistory";
    public final static String METHOD_NAME_FETCH_STOH = "GetSimpleTiffinOrderHistory";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.simple_tiffin_oh_fragment_layout,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sp=getActivity().getSharedPreferences("pref",Context.MODE_PRIVATE);

        stOHRv=(RecyclerView) view.findViewById(R.id.STOHRv);
        sTOHNoOdrPnl=(LinearLayout) view.findViewById(R.id.STOHNoOderPnl);


        if (!commonFunction.IsInternetAvailable(getActivity())) {
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        }

        new GetSTOrderHistory().execute();



        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        stOHRv.setLayoutManager(linearLayoutManager);
    }


    public class GetSTOrderHistory extends AsyncTask<String,Void,SoapObject>
    {
        String UID=sp.getString("UserId",null);
        ProgressDialog progressDialogSTOH;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialogSTOH=new ProgressDialog(getActivity());
            progressDialogSTOH.setTitle("Fetching Order History....");
            progressDialogSTOH.setMessage("Please Wait....");
            progressDialogSTOH.setCancelable(false);
            progressDialogSTOH.show();
        }

        @Override
        protected SoapObject doInBackground(String... params) {

            SoapObject request=new SoapObject(NAME_SPACE,METHOD_NAME_FETCH_STOH);

            request.addProperty("UserId",UID);

            try {
                return xmlParser.getServiceResultSoapObj(URL,SOAP_ACTION_FETCH_STOH,request);
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

            progressDialogSTOH.dismiss();

            if(soapObject!=null){
                SoapObject sobj= (SoapObject) soapObject.getProperty(0);
                if(sobj.getPropertyAsString("Status").equalsIgnoreCase("Success"))
                {
                    FillDataInListView(soapObject);
                    simpleTiffinOHAdapter=new SimpleTiffinOHAdapter(getActivity(),STOHList);
                    stOHRv.setAdapter(simpleTiffinOHAdapter);
                }
                else if(sobj.getPropertyAsString("Status").equalsIgnoreCase("NoOrder"))
                {
                    sTOHNoOdrPnl.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void FillDataInListView(SoapObject soapObject) {

        STOHList.clear();
        for (int i=0;i<soapObject.getPropertyCount();i++)
        {
            SoapObject sobj= (SoapObject) soapObject.getProperty(i);
            SimpleTiffins simpleTiffins=new SimpleTiffins();

            simpleTiffins.setBookDate(sobj.getPropertyAsString("BookDate"));
            simpleTiffins.setBookFor(sobj.getPropertyAsString("BookFor"));
            simpleTiffins.setSabji(sobj.getPropertyAsString("Sabji"));
            simpleTiffins.setRoti(sobj.getPropertyAsString("Roti"));
            simpleTiffins.setDal(sobj.getPropertyAsString("Dal"));
            simpleTiffins.setChawal(sobj.getPropertyAsString("Chawal"));
            simpleTiffins.setOther(sobj.getPropertyAsString("Other"));
            simpleTiffins.setImage(sobj.getPropertyAsString("Image"));
            simpleTiffins.setPrice(sobj.getPropertyAsString("Price"));
            simpleTiffins.setOrderStatus(sobj.getPropertyAsString("OrderStatus"));

            STOHList.add(simpleTiffins);
        }
    }

    public class SimpleTiffinOHViewHolder extends RecyclerView.ViewHolder
    {
        ImageView sTOHIv;
        TextView sTOHBookForTv,sTOHBookDateTv,sTOHOdrStatus,sTOHSabjiTv,sTOHRotiTv,sTOHDalTv,sTOHChawalTv,sTOHOtherTv,sTOHPriceTv;
        public SimpleTiffinOHViewHolder(View convertView) {
            super(convertView);

            sTOHIv= (ImageView) convertView.findViewById(R.id.STOHIv);
            sTOHBookForTv=(TextView) convertView.findViewById(R.id.STOHBookForTv);
            sTOHBookDateTv=(TextView) convertView.findViewById(R.id.STOHBookDateTv);
            sTOHOdrStatus=(TextView) convertView.findViewById(R.id.STOHOderStatusTv);
            sTOHSabjiTv=(TextView) convertView.findViewById(R.id.STOHSabjiTv);
            sTOHRotiTv=(TextView) convertView.findViewById(R.id.STOHRotiTv);
            sTOHDalTv=(TextView) convertView.findViewById(R.id.STOHDalTv);
            sTOHChawalTv=(TextView) convertView.findViewById(R.id.STOHChawalTv);
            sTOHOtherTv=(TextView) convertView.findViewById(R.id.STOHOtherTv);
            sTOHPriceTv=(TextView) convertView.findViewById(R.id.STOHPriceTv);

        }
    }

    public class SimpleTiffinOHAdapter extends RecyclerView.Adapter<SimpleTiffinOHViewHolder>
    {
        Context context;
        List<SimpleTiffins> stohList;

        public SimpleTiffinOHAdapter(Context context, List<SimpleTiffins> stohList) {

            this.context=context;
            this.stohList=stohList;

        }
        @Override
        public SimpleTiffinOHViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View viewSTOH=LayoutInflater.from(context).inflate(R.layout.stoh_listview_row,parent,false);

            SimpleTiffinOHViewHolder viewHolder =new SimpleTiffinOHViewHolder(viewSTOH);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(SimpleTiffinOHViewHolder holder, int position) {

            SimpleTiffins simpleTiffins=stohList.get(position);

            String path=simpleTiffins.getImage().substring(1);

            Picasso.with(context).load("http://tiffindabba.net" + path).placeholder(R.mipmap.ic_launcher).fit().centerCrop().into(holder.sTOHIv);
            holder.sTOHBookForTv.setText("Book For:- "+simpleTiffins.getBookFor());
            holder.sTOHBookDateTv.setText("Date:- "+simpleTiffins.getBookDate());
            holder.sTOHOdrStatus.setText("Status:- "+simpleTiffins.getOrderStatus());
            holder.sTOHSabjiTv.setText("Sabji:- "+simpleTiffins.getSabji());
            holder.sTOHRotiTv.setText("Roti:- "+simpleTiffins.getRoti());
            holder.sTOHDalTv.setText("Dal:- "+simpleTiffins.getDal());
            holder.sTOHChawalTv.setText("Chawal:- "+simpleTiffins.getChawal());
            holder.sTOHOtherTv.setText("Other:- "+simpleTiffins.getOther());
           holder.sTOHPriceTv.setText("Price:- "+simpleTiffins.getPrice());

            holder.sTOHSabjiTv.setVisibility(simpleTiffins.getSabji().equalsIgnoreCase("anyType{}") ? View.GONE : View.VISIBLE);
            holder.sTOHRotiTv.setVisibility(simpleTiffins.getRoti().equalsIgnoreCase("anyType{}") ? View.GONE : View.VISIBLE);

        }

        @Override
        public int getItemCount() {
            return stohList.size();
        }
    }

//    private class SimpleTiffinOHAdapter extends BaseAdapter
//    {
//        Context context;
//        List<SimpleTiffins> stohList;
//
//        public SimpleTiffinOHAdapter(Context context, List<SimpleTiffins> stohList) {
//
//            this.context=context;
//            this.stohList=stohList;
//
//        }
//
//        @Override
//        public int getCount() {
//            return stohList.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return stohList.get(position);
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
//            ImageView sTOHIv;
//            TextView sTOHBookForTv,sTOHBookDateTv,sTOHOdrStatus,sTOHSabjiTv,sTOHRotiTv,sTOHDalTv,sTOHChawalTv,sTOHOtherTv,sTOHPriceTv;
//
//            convertView=LayoutInflater.from(context).inflate(R.layout.stoh_listview_row,parent,false);
//
//            sTOHIv= (ImageView) convertView.findViewById(R.id.STOHIv);
//            sTOHBookForTv=(TextView) convertView.findViewById(R.id.STOHBookForTv);
//            sTOHBookDateTv=(TextView) convertView.findViewById(R.id.STOHBookDateTv);
//            sTOHOdrStatus=(TextView) convertView.findViewById(R.id.STOHOderStatusTv);
//            sTOHSabjiTv=(TextView) convertView.findViewById(R.id.STOHSabjiTv);
//            sTOHRotiTv=(TextView) convertView.findViewById(R.id.STOHRotiTv);
//            sTOHDalTv=(TextView) convertView.findViewById(R.id.STOHDalTv);
//            sTOHChawalTv=(TextView) convertView.findViewById(R.id.STOHChawalTv);
//            sTOHOtherTv=(TextView) convertView.findViewById(R.id.STOHOtherTv);
//            sTOHPriceTv=(TextView) convertView.findViewById(R.id.STOHPriceTv);
//
//            SimpleTiffins simpleTiffins=stohList.get(position);
//
//            String path=simpleTiffins.getImage().substring(1);
//
//            Picasso.with(context).load("http://tiffindabba.net" + path).placeholder(R.mipmap.ic_launcher).fit().centerCrop().into(sTOHIv);
//            sTOHBookForTv.append(simpleTiffins.getBookFor());
//            sTOHBookDateTv.append(simpleTiffins.getBookDate());
//            sTOHOdrStatus.append(simpleTiffins.getOrderStatus());
//            sTOHSabjiTv.append(simpleTiffins.getSabji());
//            sTOHRotiTv.append(simpleTiffins.getRoti());
//            sTOHDalTv.append(simpleTiffins.getDal());
//            sTOHChawalTv.append(simpleTiffins.getChawal());
//            sTOHOtherTv.append(simpleTiffins.getOther());
//            //sTOHPriceTv.append(simpleTiffins.getPrice());
//
//            sTOHSabjiTv.setVisibility(simpleTiffins.getSabji().equalsIgnoreCase("anyType{}") ? View.GONE : View.VISIBLE);
//            sTOHRotiTv.setVisibility(simpleTiffins.getRoti().equalsIgnoreCase("anyType{}") ? View.GONE : View.VISIBLE);
//
////            sTOHOdrStatus.setTextColor((ColorStateList) (simpleTiffins.getOrderStatus().equalsIgnoreCase("Delivered")? Color.parseColor("#388E3C"):simpleTiffins.getOrderStatus().equalsIgnoreCase("Cancelled")?Color.parseColor("#FFF70F0F"):""));
//
//
//            return convertView;
//        }
//    }
}

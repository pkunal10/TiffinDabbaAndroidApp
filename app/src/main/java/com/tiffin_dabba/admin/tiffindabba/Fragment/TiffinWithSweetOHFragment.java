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
 * Created by ADMIN on 14-10-2017.
 */

public class TiffinWithSweetOHFragment extends Fragment {

    SharedPreferences sp;
    XMLParser xmlParser=new XMLParser();
    LinearLayout tWSOHNoOdrPnl;
    CommonFunction commonFunction=new CommonFunction();
    List<TiffinWithSweets> TWSOHList=new ArrayList<>();
    TiffinWithSweetOHAdapter tiffinWithSweetOHAdapter;
    RecyclerView twsOHRv;

    public final static String NAME_SPACE = "http://tempuri.org/";
    public final static String URL = "http://tiffindabba.net/MyService.asmx";

    public final static String SOAP_ACTION_FETCH_TWSOH = "http://tempuri.org/GetTiffinWithSweetOrderHistory";
    public final static String METHOD_NAME_FETCH_TWSOH = "GetTiffinWithSweetOrderHistory";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tiffin_with_sweet_oh_fragment_layout,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sp=getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);

        twsOHRv=(RecyclerView) view.findViewById(R.id.TWSOHRv);
        tWSOHNoOdrPnl=(LinearLayout) view.findViewById(R.id.TWSOHNoOderPnl);

        if (!commonFunction.IsInternetAvailable(getActivity())) {
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        }

        new GetTWSOrderHistory().execute();


    }

    public class GetTWSOrderHistory extends AsyncTask<String,Void,SoapObject>
    {
        String UID=sp.getString("UserId",null);
        ProgressDialog progressDialogTWSOH;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialogTWSOH=new ProgressDialog(getActivity());
            progressDialogTWSOH.setTitle("Fetching Order History....");
            progressDialogTWSOH.setMessage("Please Wait....");
            progressDialogTWSOH.setCancelable(false);
            progressDialogTWSOH.show();
        }

        @Override
        protected SoapObject doInBackground(String... params) {

            SoapObject request=new SoapObject(NAME_SPACE,METHOD_NAME_FETCH_TWSOH);

            request.addProperty("UserId",UID);

            try {
                return xmlParser.getServiceResultSoapObj(URL,SOAP_ACTION_FETCH_TWSOH,request);
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

            progressDialogTWSOH.dismiss();

            if(soapObject!=null){
                SoapObject sobj= (SoapObject) soapObject.getProperty(0);
                if(sobj.getPropertyAsString("Status").equalsIgnoreCase("Success"))
                {
                    FillDataInListView(soapObject);
                }
                else if(sobj.getPropertyAsString("Status").equalsIgnoreCase("NoOrder"))
                {
                    tWSOHNoOdrPnl.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void FillDataInListView(SoapObject soapObject) {

        TWSOHList.clear();
        for (int i=0;i<soapObject.getPropertyCount();i++)
        {
            SoapObject sobj= (SoapObject) soapObject.getProperty(i);
            TiffinWithSweets tiffinWithSweets=new TiffinWithSweets();

            tiffinWithSweets.setBookDate(sobj.getPropertyAsString("BookDate"));
            tiffinWithSweets.setBookFor(sobj.getPropertyAsString("BookFor"));
            tiffinWithSweets.setSabji(sobj.getPropertyAsString("Sabji"));
            tiffinWithSweets.setRoti(sobj.getPropertyAsString("Roti"));
            tiffinWithSweets.setDal(sobj.getPropertyAsString("Dal"));
            tiffinWithSweets.setChawal(sobj.getPropertyAsString("Chawal"));
            tiffinWithSweets.setSweet(sobj.getPropertyAsString("Sweet"));
            tiffinWithSweets.setOther(sobj.getPropertyAsString("Other"));
            tiffinWithSweets.setImage(sobj.getPropertyAsString("Image"));
            tiffinWithSweets.setOrderStatus(sobj.getPropertyAsString("OrderStatus"));

            TWSOHList.add(tiffinWithSweets);

            tiffinWithSweetOHAdapter=new TiffinWithSweetOHAdapter(getActivity(),TWSOHList);
            twsOHRv.setAdapter(tiffinWithSweetOHAdapter);

            LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            twsOHRv.setLayoutManager(linearLayoutManager);
        }
    }

    public class TiffinWithSweetOHViewHolder extends RecyclerView.ViewHolder
    {
        ImageView tWSOHIv;
        TextView tWSOHBookForTv,tWSOHBookDateTv,tWSOHOdrStatus,tWSOHSabjiTv,tWSOHRotiTv,tWSOHDalTv,tWSOHChawalTv,tWSOHOtherTv,tWSOHPriceTv,tWSOHSweetTv;
        public TiffinWithSweetOHViewHolder(View convertView) {
            super(convertView);

            tWSOHIv= (ImageView) convertView.findViewById(R.id.TWSOHIv);
            tWSOHBookForTv=(TextView) convertView.findViewById(R.id.TWSOHBookForTv);
            tWSOHBookDateTv=(TextView) convertView.findViewById(R.id.TWSOHBookDateTv);
            tWSOHOdrStatus=(TextView) convertView.findViewById(R.id.TWSOHOderStatusTv);
            tWSOHSabjiTv=(TextView) convertView.findViewById(R.id.TWSOHSabjiTv);
            tWSOHRotiTv=(TextView) convertView.findViewById(R.id.TWSOHRotiTv);
            tWSOHDalTv=(TextView) convertView.findViewById(R.id.TWSOHDalTv);
            tWSOHChawalTv=(TextView) convertView.findViewById(R.id.TWSOHChawalTv);
            tWSOHOtherTv=(TextView) convertView.findViewById(R.id.TWSOHOtherTv);
            tWSOHPriceTv=(TextView) convertView.findViewById(R.id.TWSOHPriceTv);
            tWSOHSweetTv=(TextView) convertView.findViewById(R.id.TWSOHSweetTv);
        }
    }

    public class TiffinWithSweetOHAdapter extends RecyclerView.Adapter<TiffinWithSweetOHViewHolder>
    {
        Context context;
        List<TiffinWithSweets> twsohList;

        public TiffinWithSweetOHAdapter(Context context, List<TiffinWithSweets> twsohList) {

            this.context=context;
            this.twsohList=twsohList;

        }

        @Override
        public TiffinWithSweetOHViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View viewTWSOH=LayoutInflater.from(context).inflate(R.layout.twsoh_listview_row,parent,false);

            TiffinWithSweetOHViewHolder viewHolder =new TiffinWithSweetOHViewHolder(viewTWSOH);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(TiffinWithSweetOHViewHolder holder, int position) {

            TiffinWithSweets tiffinWithSweets=twsohList.get(position);

            String path=tiffinWithSweets.getImage().substring(1);

            Picasso.with(context).load("http://tiffindabba.net" + path).placeholder(R.mipmap.ic_launcher).fit().centerCrop().into(holder.tWSOHIv);
            holder.tWSOHBookForTv.setText("Book For:- "+tiffinWithSweets.getBookFor());
            holder.tWSOHBookDateTv.setText("Date:- "+tiffinWithSweets.getBookDate());
            holder.tWSOHOdrStatus.setText("Status:- "+tiffinWithSweets.getOrderStatus());
            holder.tWSOHSabjiTv.setText("Sabji:- "+tiffinWithSweets.getSabji());
            holder.tWSOHRotiTv.setText("Roti:- "+tiffinWithSweets.getRoti());
            holder.tWSOHDalTv.setText("Dal:- "+tiffinWithSweets.getDal());
            holder.tWSOHChawalTv.setText("Chawal:- "+tiffinWithSweets.getChawal());
            holder.tWSOHOtherTv.setText("Other:- "+tiffinWithSweets.getOther());
            //  tWSOHPriceTv.setText("Price:- "+tiffinWithSweets.getPrice());
            holder.tWSOHSweetTv.setText("Sweet:- "+tiffinWithSweets.getSweet());

            holder.tWSOHSabjiTv.setVisibility(tiffinWithSweets.getSabji().equalsIgnoreCase("anyType{}") ? View.GONE : View.VISIBLE);
            holder.tWSOHRotiTv.setVisibility(tiffinWithSweets.getRoti().equalsIgnoreCase("anyType{}") ? View.GONE : View.VISIBLE);

        }

        @Override
        public int getItemCount() {
            return twsohList.size();
        }
    }

//    private class TiffinWithSweetOHAdapter extends BaseAdapter
//    {
//        Context context;
//        List<TiffinWithSweets> twsohList;
//
//        public TiffinWithSweetOHAdapter(Context context, List<TiffinWithSweets> twsohList) {
//
//            this.context=context;
//            this.twsohList=twsohList;
//
//        }
//
//        @Override
//        public int getCount() {
//            return twsohList.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return twsohList.get(position);
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
//            ImageView tWSOHIv;
//            TextView tWSOHBookForTv,tWSOHBookDateTv,tWSOHOdrStatus,tWSOHSabjiTv,tWSOHRotiTv,tWSOHDalTv,tWSOHChawalTv,tWSOHOtherTv,tWSOHPriceTv,tWSOHSweetTv;
//
//            convertView=LayoutInflater.from(context).inflate(R.layout.twsoh_listview_row,parent,false);
//
//            tWSOHIv= (ImageView) convertView.findViewById(R.id.TWSOHIv);
//            tWSOHBookForTv=(TextView) convertView.findViewById(R.id.TWSOHBookForTv);
//            tWSOHBookDateTv=(TextView) convertView.findViewById(R.id.TWSOHBookDateTv);
//            tWSOHOdrStatus=(TextView) convertView.findViewById(R.id.TWSOHOderStatusTv);
//            tWSOHSabjiTv=(TextView) convertView.findViewById(R.id.TWSOHSabjiTv);
//            tWSOHRotiTv=(TextView) convertView.findViewById(R.id.TWSOHRotiTv);
//            tWSOHDalTv=(TextView) convertView.findViewById(R.id.TWSOHDalTv);
//            tWSOHChawalTv=(TextView) convertView.findViewById(R.id.TWSOHChawalTv);
//            tWSOHOtherTv=(TextView) convertView.findViewById(R.id.TWSOHOtherTv);
//            tWSOHPriceTv=(TextView) convertView.findViewById(R.id.TWSOHPriceTv);
//            tWSOHSweetTv=(TextView) convertView.findViewById(R.id.TWSOHSweetTv);
//
//            TiffinWithSweets tiffinWithSweets=twsohList.get(position);
//
//            String path=tiffinWithSweets.getImage().substring(1);
//
//            Picasso.with(context).load("http://tiffindabba.net" + path).placeholder(R.mipmap.ic_launcher).fit().centerCrop().into(tWSOHIv);
//            tWSOHBookForTv.append(tiffinWithSweets.getBookFor());
//            tWSOHBookDateTv.append(tiffinWithSweets.getBookDate());
//            tWSOHOdrStatus.append(tiffinWithSweets.getOrderStatus());
//            tWSOHSabjiTv.append(tiffinWithSweets.getSabji());
//            tWSOHRotiTv.append(tiffinWithSweets.getRoti());
//            tWSOHDalTv.append(tiffinWithSweets.getDal());
//            tWSOHChawalTv.append(tiffinWithSweets.getChawal());
//            tWSOHOtherTv.append(tiffinWithSweets.getOther());
//          //  tWSOHPriceTv.append(tiffinWithSweets.getPrice());
//            tWSOHSweetTv.append(tiffinWithSweets.getSweet());
//
//            tWSOHSabjiTv.setVisibility(tiffinWithSweets.getSabji().equalsIgnoreCase("anyType{}") ? View.GONE : View.VISIBLE);
//            tWSOHRotiTv.setVisibility(tiffinWithSweets.getRoti().equalsIgnoreCase("anyType{}") ? View.GONE : View.VISIBLE);
//
////            sTOHOdrStatus.setTextColor((ColorStateList) (simpleTiffins.getOrderStatus().equalsIgnoreCase("Delivered")? Color.parseColor("#388E3C"):simpleTiffins.getOrderStatus().equalsIgnoreCase("Cancelled")?Color.parseColor("#FFF70F0F"):""));
//
//
//            return convertView;
//        }
//    }
}

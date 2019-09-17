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
import com.tiffin_dabba.admin.tiffindabba.Modals.FastFoodProduct;
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

public class FastFoodOHFragment extends Fragment {

    SharedPreferences sp;
    XMLParser xmlParser=new XMLParser();
    LinearLayout fFOHNoOdrPnl;
    CommonFunction commonFunction=new CommonFunction();
    List<FastFoodProduct> FFOHList=new ArrayList<>();
    RecyclerView ffOHRv;
    FastFoodOHAdapter fastFoodOHAdapter;

    public final static String NAME_SPACE = "http://tempuri.org/";
    public final static String URL = "http://tiffindabba.net/MyService.asmx";

    public final static String SOAP_ACTION_FETCH_FFOH = "http://tempuri.org/GetFastFoodOrderHistory";
    public final static String METHOD_NAME_FETCH_FFOH = "GetFastFoodOrderHistory";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fast_food_oh_fragment_layout,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        sp=getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);

        ffOHRv=(RecyclerView) view.findViewById(R.id.FFOHRv);
        fFOHNoOdrPnl=(LinearLayout) view.findViewById(R.id.FFOHNoOderPnl);

        if (!commonFunction.IsInternetAvailable(getActivity())) {
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        }

        new GetFFOrderHistory().execute();

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ffOHRv.setLayoutManager(linearLayoutManager);
    }

    public class GetFFOrderHistory extends AsyncTask<String,Void,SoapObject>
    {
        String UID=sp.getString("UserId",null);
        ProgressDialog progressDialogFFOH;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialogFFOH=new ProgressDialog(getActivity());
            progressDialogFFOH.setTitle("Fetching Order History....");
            progressDialogFFOH.setMessage("Please Wait....");
            progressDialogFFOH.setCancelable(false);
            progressDialogFFOH.show();
        }

        @Override
        protected SoapObject doInBackground(String... params) {

            SoapObject request=new SoapObject(NAME_SPACE,METHOD_NAME_FETCH_FFOH);

            request.addProperty("UserId",UID);

            try {
                return xmlParser.getServiceResultSoapObj(URL,SOAP_ACTION_FETCH_FFOH,request);
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

            progressDialogFFOH.dismiss();

            if(soapObject!=null){
                SoapObject sobj= (SoapObject) soapObject.getProperty(0);
                if(sobj.getPropertyAsString("Status").equalsIgnoreCase("Success"))
                {
                    FillDataInListView(soapObject);
                    fastFoodOHAdapter=new FastFoodOHAdapter(getActivity(),FFOHList);
                    ffOHRv.setAdapter(fastFoodOHAdapter);
                }
                else if(sobj.getPropertyAsString("Status").equalsIgnoreCase("NoOrder"))
                {
                    fFOHNoOdrPnl.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void FillDataInListView(SoapObject soapObject) {

        FFOHList.clear();
        for (int i=0;i<soapObject.getPropertyCount();i++)
        {
            SoapObject sobj= (SoapObject) soapObject.getProperty(i);
            FastFoodProduct fastFoodProduct=new FastFoodProduct();

            fastFoodProduct.setBookDate(sobj.getPropertyAsString("BookDate"));
            fastFoodProduct.setProductName(sobj.getPropertyAsString("ProductName"));
            fastFoodProduct.setQuantity(sobj.getPropertyAsString("Quntity"));
            fastFoodProduct.setTotalPrice(sobj.getPropertyAsString("TotalPrice"));
            fastFoodProduct.setImage(sobj.getPropertyAsString("Image"));
            fastFoodProduct.setOrderStatus(sobj.getPropertyAsString("OrderStatus"));

            FFOHList.add(fastFoodProduct);
        }
    }

    public class FastFoodOHViewHolder extends RecyclerView.ViewHolder
    {
        ImageView fFOHIv;
        TextView fFOHBookDateTv,fFOHProductNameTv,fFOHQtyTv,fFOHTotPriceTv,fFOHOrderStatusTv;

        public FastFoodOHViewHolder(View convertView) {
            super(convertView);

            fFOHIv= (ImageView) convertView.findViewById(R.id.FFOHIv);
            fFOHBookDateTv=(TextView) convertView.findViewById(R.id.FFOHBookDateTv);
            fFOHProductNameTv=(TextView) convertView.findViewById(R.id.FFOHNameTv);
            fFOHQtyTv=(TextView) convertView.findViewById(R.id.FFOHQtyTv);
            fFOHTotPriceTv=(TextView) convertView.findViewById(R.id.FFOHPriceTv);
            fFOHOrderStatusTv=(TextView) convertView.findViewById(R.id.FFOHOdrStatusTv);
        }
    }

    public class FastFoodOHAdapter extends RecyclerView.Adapter<FastFoodOHViewHolder>
    {
        Context context;
        List<FastFoodProduct> ffohList;

        public FastFoodOHAdapter(Context context, List<FastFoodProduct> ffohList) {

            this.context=context;
            this.ffohList=ffohList;

        }
        @Override
        public FastFoodOHViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View viewFFOH=LayoutInflater.from(context).inflate(R.layout.ffoh_listview_row,parent,false);

            FastFoodOHViewHolder viewHolder=new FastFoodOHViewHolder(viewFFOH);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(FastFoodOHViewHolder holder, int position) {
            FastFoodProduct fastFoodProduct=ffohList.get(position);

            String path=fastFoodProduct.getImage().substring(1);

            Picasso.with(context).load("http://tiffindabba.net" + path).placeholder(R.mipmap.ic_launcher).fit().centerCrop().into(holder.fFOHIv);
            holder.fFOHBookDateTv.setText("Book Date:- "+fastFoodProduct.getBookDate());
            holder.fFOHProductNameTv.setText("Name:- "+fastFoodProduct.getProductName());
            holder.fFOHQtyTv.setText("Quantity:- "+fastFoodProduct.getQuantity());
            holder.fFOHTotPriceTv.setText("Total Price:- "+fastFoodProduct.getTotalPrice());
            holder.fFOHOrderStatusTv.setText("Status:- "+fastFoodProduct.getOrderStatus());
        }

        @Override
        public int getItemCount() {
            return ffohList.size();
        }
    }

//    private class FastFoodOHAdapter extends BaseAdapter
//    {
//        Context context;
//        List<FastFoodProduct> ffohList;
//
//        public FastFoodOHAdapter(Context context, List<FastFoodProduct> ffohList) {
//
//            this.context=context;
//            this.ffohList=ffohList;
//
//        }
//
//        @Override
//        public int getCount() {
//            return ffohList.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return ffohList.get(position);
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
//            ImageView fFOHIv;
//            TextView fFOHBookDateTv,fFOHProductNameTv,fFOHQtyTv,fFOHTotPriceTv,fFOHOrderStatusTv;
//
//            convertView=LayoutInflater.from(context).inflate(R.layout.ffoh_listview_row,parent,false);
//
//            fFOHIv= (ImageView) convertView.findViewById(R.id.FFOHIv);
//            fFOHBookDateTv=(TextView) convertView.findViewById(R.id.FFOHBookDateTv);
//            fFOHProductNameTv=(TextView) convertView.findViewById(R.id.FFOHNameTv);
//            fFOHQtyTv=(TextView) convertView.findViewById(R.id.FFOHQtyTv);
//            fFOHTotPriceTv=(TextView) convertView.findViewById(R.id.FFOHPriceTv);
//            fFOHOrderStatusTv=(TextView) convertView.findViewById(R.id.FFOHOdrStatusTv);
//
//            FastFoodProduct fastFoodProduct=ffohList.get(position);
//
//            String path=fastFoodProduct.getImage().substring(1);
//
//            Picasso.with(context).load("http://tiffindabba.net" + path).placeholder(R.mipmap.ic_launcher).fit().centerCrop().into(fFOHIv);
//            fFOHBookDateTv.append(fastFoodProduct.getBookDate());
//            fFOHProductNameTv.append(fastFoodProduct.getProductName());
//            fFOHQtyTv.append(fastFoodProduct.getQuantity());
//            fFOHTotPriceTv.append(fastFoodProduct.getTotalPrice());
//            fFOHOrderStatusTv.append(fastFoodProduct.getOrderStatus());
//
//            return convertView;
//        }
//    }
}

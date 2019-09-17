package com.tiffin_dabba.admin.tiffindabba.Fragment;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.tiffin_dabba.admin.tiffindabba.DrawerActivity;
import com.tiffin_dabba.admin.tiffindabba.ImportantClasses.CommonFunction;
import com.tiffin_dabba.admin.tiffindabba.ImportantClasses.XMLParser;
import com.tiffin_dabba.admin.tiffindabba.Modals.FastFoodProduct;
import com.tiffin_dabba.admin.tiffindabba.R;

import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by ADMIN on 19-09-2017.
 */


public class FastFoodProductFragment extends Fragment {

    GridView fastfoodProductGv;
    TextView resTv, categoryNameTv;
    ArrayList<FastFoodProduct> fastFoodProductList = new ArrayList<>();
    FastFoodAdapter fastFoodAdapter;
    CommonFunction commonFunction=new CommonFunction();
    String fastfoodCategoryId, fastfoodCategoryName,fastfoodNotiProductId;
    XMLParser xmlParser = new XMLParser();
    ProgressDialog progressDialog;
    ImageView backBtn;
    Button addCartBtn;
    Set<String> productSet=new HashSet<>();
    SharedPreferences sp;

    public final static String SOAP_ACTION = "http://tempuri.org/GetFastFoodProducts";
    public final static String METHOD_NAME = "GetFastFoodProducts";
    public final static String NAME_SPACE = "http://tempuri.org/";
    public final static String URL = "http://tiffindabba.net/MyService.asmx";

    @SuppressLint("ValidFragment")
    public FastFoodProductFragment(String fastfoodCategoryIdAct, String fastfoodCategoryName) {
        fastfoodCategoryId = fastfoodCategoryIdAct;
        this.fastfoodCategoryName = fastfoodCategoryName;
        //this.fastfoodNotiProductId=fastfoodNotiProductId;
    }

    public FastFoodProductFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fastfood_product_fragment_layout, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fastfoodProductGv = (GridView) view.findViewById(R.id.FastFoodProductGv);
        resTv = (TextView) view.findViewById(R.id.FastFoodProductResTv);
//        categoryNameTv=(TextView)view.findViewById(R.id.CategoryNameTv);
//        backBtn=(ImageView) view.findViewById(R.id.BackArrowBtn);

        //categoryNameTv.setText(fastfoodCategoryName);

        sp = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
       // Toast.makeText(getActivity(), fastfoodNotiProductId, Toast.LENGTH_SHORT).show();

//        if(productSet.size()==0)
//        {
//            productSet=new HashSet<>();
//        }

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(fastfoodCategoryName);

        if (!commonFunction.IsInternetAvailable(getActivity())) {
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        }

        new GetFastFoodProduct().execute();

        fastFoodAdapter = new FastFoodAdapter(getActivity(), fastFoodProductList);
        fastfoodProductGv.setAdapter(fastFoodAdapter);

//        backBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getActivity().finish();
//            }
//        });

    }

    public class GetFastFoodProduct extends AsyncTask<String, Void, SoapObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Fetching Products....");
            progressDialog.setMessage("Please Wait....");
            progressDialog.setCancelable(false);
            progressDialog.show();


        }

        @Override
        protected SoapObject doInBackground(String... params) {

            SoapObject response = null;

            SoapObject request = new SoapObject(NAME_SPACE, METHOD_NAME);

            request.addProperty("CategoryId", fastfoodCategoryId);

            try {
                response = xmlParser.getServiceResultSoapObj(URL, SOAP_ACTION, request);
                //  Log.d("ServerResult", response.toString());
            } catch (XmlPullParserException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();

            }

            return response;

        }


        @Override
        protected void onPostExecute(SoapObject soapObject) {
            super.onPostExecute(soapObject);

            progressDialog.dismiss();

            if (soapObject != null) {

                resTv.setText(soapObject.toString());
                SetDataInGridView(soapObject);
            }
        }
    }

    private void SetDataInGridView(SoapObject soapObject) {

        for (int i = 0; i < soapObject.getPropertyCount(); i++) {
            SoapObject sobj = (SoapObject) soapObject.getProperty(i);

            if (sobj.getPropertyAsString("Status").equalsIgnoreCase("Success")) {
                FastFoodProduct fastFoodProduct = new FastFoodProduct();

                fastFoodProduct.setProductId(sobj.getPropertyAsString("ProductId"));
                fastFoodProduct.setCategoryId(sobj.getPropertyAsString("CategoryId"));
                fastFoodProduct.setProviderCity(sobj.getPropertyAsString("ProviderCity"));
                fastFoodProduct.setProviderId(sobj.getPropertyAsString("ProviderId"));
                fastFoodProduct.setProductName(sobj.getPropertyAsString("ProductName"));
                fastFoodProduct.setDescription(sobj.getPropertyAsString("Description"));
                fastFoodProduct.setPrice(sobj.getPropertyAsString("Price"));
                fastFoodProduct.setStartTime(sobj.getPropertyAsString("StartTime"));
                fastFoodProduct.setEndTime(sobj.getPropertyAsString("EndTime"));
                fastFoodProduct.setImage(sobj.getPropertyAsString("Image"));
                fastFoodProduct.setCanBook(sobj.getPropertyAsString("CanBook"));
                fastFoodProduct.setDiscountedPrice(sobj.getPropertyAsString("DiscountedPrice"));
                //fastFoodProduct.setImage(R.mipmap.ic_launcher + "");

                fastFoodProductList.add(fastFoodProduct);

//                if(!fastfoodNotiProductId.equalsIgnoreCase(""))
//                {
//                    Integer pos= fastFoodProductList.indexOf(fastfoodNotiProductId);
//                    Toast.makeText(getActivity(), fastfoodNotiProductId+"   "+pos.toString(), Toast.LENGTH_SHORT).show();
//                }

            } else {
                Toast.makeText(getActivity(), "No Products Available For " + categoryNameTv.getText(), Toast.LENGTH_LONG).show();
                //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.DrawerFragmentsContainer,new FastFoodCategoryFragment()).commit();
                getActivity().finish();
            }
        }

    }

    private class FastFoodAdapter extends BaseAdapter {

        Context context;
        ArrayList<FastFoodProduct> fastFoodProductList;

        public FastFoodAdapter(Context context, ArrayList<FastFoodProduct> fastFoodProductList) {

            this.context = context;
            this.fastFoodProductList = fastFoodProductList;
        }

        @Override
        public int getCount() {
            return fastFoodProductList.size();
        }

        @Override
        public Object getItem(int position) {
            return fastFoodProductList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            convertView = LayoutInflater.from(context).inflate(R.layout.fastfood_product_gridview_layout, parent, false);
            ImageView imageView;
            TextView productNameTv, productDescriptionTv, productPriceTv,bookTimeTv,productOldPriceTv,productPerDisTv;

            productNameTv = (TextView) convertView.findViewById(R.id.ProductNameTv);
            productDescriptionTv = (TextView) convertView.findViewById(R.id.ProductDescriptionTv);
            productPriceTv = (TextView) convertView.findViewById(R.id.ProductPriceTv);
            addCartBtn = (Button) convertView.findViewById(R.id.AddCartBtn);
            bookTimeTv=(TextView)convertView.findViewById(R.id.BookTimeTv);
            productOldPriceTv=(TextView)convertView.findViewById(R.id.ProductOldPriceTv);
            productPerDisTv=(TextView)convertView.findViewById(R.id.ProductPerDisTv);

            final FastFoodProduct fastFoodProduct = fastFoodProductList.get(position);

            Integer per=0;
            if(!fastFoodProduct.getDiscountedPrice().equalsIgnoreCase("anyType{}"))
            {
                per=(100-((Integer.parseInt(fastFoodProduct.getDiscountedPrice())*100)/Integer.parseInt(fastFoodProduct.getPrice())));
            }

            imageView = (ImageView) convertView.findViewById(R.id.Image);
            productNameTv.setText(fastFoodProduct.getProductName());
            // productDescriptionTv.setText(fastFoodProduct.getDescription());
            //productPriceTv.setText("Price:-  " + fastFoodProduct.getPrice());
            bookTimeTv.append(fastFoodProduct.getStartTime()+" to "+fastFoodProduct.getEndTime());
            // imageView.setImageResource(Integer.parseInt(fastFoodProduct.getImage()));

            productPriceTv.setText(fastFoodProduct.getDiscountedPrice().equalsIgnoreCase("anyType{}")?"Price:-  " + fastFoodProduct.getPrice():"Price:-  " + fastFoodProduct.getDiscountedPrice());
            productOldPriceTv.setText(!fastFoodProduct.getDiscountedPrice().equalsIgnoreCase("anyType{}")?fastFoodProduct.getPrice()+" Rs.":"");
            productOldPriceTv.setPaintFlags(productOldPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            productPerDisTv.setText(!fastFoodProduct.getDiscountedPrice().equalsIgnoreCase("anyType{}")?"("+per+" % Off)":"");
            productDescriptionTv.setVisibility(fastFoodProduct.getDescription().equalsIgnoreCase("anyType{}")?View.GONE:View.VISIBLE);
            productDescriptionTv.setText(!fastFoodProduct.getDescription().equalsIgnoreCase("anyType{}")?fastFoodProduct.getDescription():"");

            String path = fastFoodProduct.getImage().substring(1);

            Picasso.with(context).load("http://tiffindabba.net" + path).placeholder(R.mipmap.ic_launcher).fit().centerCrop().into(imageView);

            addCartBtn.setEnabled(fastFoodProduct.getCanBook().equalsIgnoreCase("false")?false:true);

            addCartBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String ProductId = fastFoodProduct.getProductId();

                    AddProductInCart(ProductId);

                }
            });

            return convertView;
        }
    }

    private void AddProductInCart(String productId) {

        if(sp.contains("ProductSet"))
        {
            Set<String> pSet=new HashSet<>();
            pSet=sp.getStringSet("ProductSet",null);
            List<String> plist=new ArrayList<>(pSet);
            if(pSet.contains(productId))
            {
                Toast.makeText(getActivity(), "Product Already Added In Cart", Toast.LENGTH_LONG).show();
            }
            else
            {

                productSet.add(productId);
                for(int i=0;i<plist.size();i++)
                {
                    productSet.add(plist.get(i));
                }
                SharedPreferences.Editor editor = sp.edit();
               // productSet.add(productId);
                editor.putStringSet("ProductSet",productSet);
                editor.commit();

                Toast.makeText(getActivity(), "Product Added In Cart", Toast.LENGTH_LONG).show();
            }
        }
        else
        {

            SharedPreferences.Editor editor = sp.edit();
            productSet.add(productId);
            editor.putStringSet("ProductSet",productSet);
            editor.commit();

            Toast.makeText(getActivity(), "Product Added In Cart", Toast.LENGTH_LONG).show();
        }
    }
}

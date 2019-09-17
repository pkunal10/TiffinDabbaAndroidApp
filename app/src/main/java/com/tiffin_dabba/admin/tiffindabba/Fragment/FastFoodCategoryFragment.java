package com.tiffin_dabba.admin.tiffindabba.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.tiffin_dabba.admin.tiffindabba.ImportantClasses.CommonFunction;
import com.tiffin_dabba.admin.tiffindabba.ImportantClasses.XMLParser;
import com.tiffin_dabba.admin.tiffindabba.Modals.FastFoodCategory;
import com.tiffin_dabba.admin.tiffindabba.ProductListingActivity;
import com.tiffin_dabba.admin.tiffindabba.R;

import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by ADMIN on 18-09-2017.
 */

public class FastFoodCategoryFragment extends Fragment {

    TextView resTv;
    GridView gridView;
    XMLParser xmlParser=new XMLParser();
    ArrayList<FastFoodCategory> fastFoodCategoryList=new ArrayList<>();
    FastFoodCategoryAdapter fastFoodCategoryAdapter;
    CommonFunction commonFunction=new CommonFunction();
    ProgressDialog progressDialog;

    public final static String SOAP_ACTION = "http://tempuri.org/GetFastFoodCategory";
    public final static String METHOD_NAME = "GetFastFoodCategory";
    public final static String NAME_SPACE = "http://tempuri.org/";
    public final static String URL = "http://tiffindabba.net/MyService.asmx";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fastfood_category_fragment_layout,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {


        gridView=(GridView) view.findViewById(R.id.GridView);
        resTv=(TextView)view.findViewById(R.id.ResTv);

        if (!commonFunction.IsInternetAvailable(getActivity())) {
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        }

        new FasfFoodCategoryFetch().execute();

        fastFoodCategoryAdapter=new FastFoodCategoryAdapter(getActivity(),fastFoodCategoryList);
        gridView.setAdapter(fastFoodCategoryAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FastFoodCategory fastFoodCategory=fastFoodCategoryList.get(position);

                Bundle bundle=new Bundle();
                bundle.putString("ProductType","FastFood");
                bundle.putString("CategoryId",fastFoodCategory.getCategoryId()+"");
                bundle.putString("CategoryName",fastFoodCategory.getName());
               // bundle.putString("NotiProductId","");
                Intent intent=new Intent(getActivity(), ProductListingActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);

               // Toast.makeText(getActivity(),fastFoodCategory.getCategoryId()+"" , Toast.LENGTH_LONG).show();
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    public class FasfFoodCategoryFetch extends AsyncTask<String,Void,SoapObject>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setCancelable(false);
            progressDialog.setTitle("Fetching FastFood Category....");
            progressDialog.setMessage("Please Wait....");
            progressDialog.show();
        }

        @Override
        protected SoapObject doInBackground(String... params) {
            SoapObject response = null;

            SoapObject request=new SoapObject(NAME_SPACE,METHOD_NAME);

            try {
                response=xmlParser.getServiceResultSoapObj(URL,SOAP_ACTION,request);
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return response;
        }


        @Override
        protected void onPostExecute(SoapObject s) {
            super.onPostExecute(s);
            progressDialog.dismiss();

            resTv.setText(s.toString());
            if(s!=null)
            {
                ListDataInGridview(s);
            }
        }
    }

    private void ListDataInGridview(SoapObject s) {

        for (int i=0;i<s.getPropertyCount();i++)
        {
            SoapObject sobj= (SoapObject) s.getProperty(i);

            FastFoodCategory fastFoodCategory=new FastFoodCategory();
            fastFoodCategory.setCategoryId(sobj.getPropertyAsString("CategoryId"));
            fastFoodCategory.setName(sobj.getPropertyAsString("CategoryName"));
            fastFoodCategory.setImage(sobj.getPropertyAsString("Image"));
            fastFoodCategory.setNoOfDishes(sobj.getPropertyAsString("NoOfDishes"));

            fastFoodCategoryList.add(fastFoodCategory);
        }

    }

    private class FastFoodCategoryAdapter extends BaseAdapter {

        ImageView imageView;
        TextView fastfoodnameTv,noOfDishesTv;

        Context context;
        ArrayList<FastFoodCategory> fastFoodCategoryList;

        public FastFoodCategoryAdapter(Context context, ArrayList<FastFoodCategory> fastFoodCategoryList) {

            this.context=context;
            this.fastFoodCategoryList=fastFoodCategoryList;

        }

        @Override
        public int getCount() {
            return fastFoodCategoryList.size();
        }

        @Override
        public Object getItem(int position) {
            return fastFoodCategoryList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView=LayoutInflater.from(context).inflate(R.layout.fastfood_category_gridview_row,parent,false);

            imageView=(ImageView) convertView.findViewById(R.id.Image);
            fastfoodnameTv=(TextView) convertView.findViewById(R.id.CategoryNameTv);
            noOfDishesTv=(TextView) convertView.findViewById(R.id.NoOfDishesTv);

            FastFoodCategory fastFoodCategory=fastFoodCategoryList.get(position);

            String path=fastFoodCategory.getImage().substring(1);

            Picasso.with(context).load("http://tiffindabba.net"+path).placeholder(R.mipmap.ic_launcher).fit().centerCrop().into(imageView);
            fastfoodnameTv.setText(fastFoodCategory.getName());
            noOfDishesTv.setText("("+fastFoodCategory.getNoOfDishes()+" Dishes)");


            return convertView;
        }
    }
}

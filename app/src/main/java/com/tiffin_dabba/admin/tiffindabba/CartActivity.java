package com.tiffin_dabba.admin.tiffindabba;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.tiffin_dabba.admin.tiffindabba.ImportantClasses.XMLParser;
import com.tiffin_dabba.admin.tiffindabba.Modals.FastFoodProduct;

import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CartActivity extends AppCompatActivity {


    List<FastFoodProduct> fastFoodProductList = new ArrayList<>();
    ListView cartLv;
    CartAdapter cartAdapter;
    SharedPreferences sp;
    Set<String> productSet = new HashSet<>();
    List<String> productList, cityList, areaList;
    String ProductId, UserId, selectedCity;
    TextView cartFinalTotalTv, cartDelChargeTv, cartGrandTotalTv, resTv, cartChangeAddressBtn, cartFixedChangeAddressBtn, cartFixedAddressTv;
    XMLParser xmlParser = new XMLParser();
    int ChargeLimit, DeliveryCharge;
    Button cartCheckoutBtn, cartAddressBtn, cartSelectAddressBtn,startShopBtn;
    Spinner cartCitySpinner, cartAreaSpinner;
    ArrayAdapter<String> cityAdapter, areaAdapter;
    EditText cartAddressEt;
    LinearLayout emptyCartLl;
    SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss");

    ProgressDialog progressDialog;
    Handler handler = new Handler();

    public final static String SOAP_ACTION = "http://tempuri.org/GetCart";
    public final static String METHOD_NAME = "GetCart";
    public final static String NAME_SPACE = "http://tempuri.org/";
    public final static String URL = "http://tiffindabba.net/MyService.asmx";

    public final static String SOAP_ACTION_DEL_CHARGE = "http://tempuri.org/GetDeliveryCharges";
    public final static String METHOD_NAME_DEL_CHARGE = "GetDeliveryCharges";

    public final static String SOAP_ACTION_ORDERID = "http://tempuri.org/GetOrderId";
    public final static String METHOD_NAME_ORDERID = "GetOrderId";

    public final static String SOAP_ACTION_FETCH_CITY = "http://tempuri.org/GetCityList";
    public final static String METHOD_NAME_FETCH_CITY = "GetCityList";

    public final static String SOAP_ACTION_FETCH_ADDRESS = "http://tempuri.org/GetUserAddress";
    public final static String METHOD_NAME_FETCH_ADDRESS = "GetUserAddress";

    public final static String SOAP_ACTION_FETCH_AREA = "http://tempuri.org/GetAreaList";
    public final static String METHOD_NAME_FETCH_AREA = "GetAreaList";

    public final static String SOAP_ACTION_SAVE_ORDER = "http://tempuri.org/SaveFastFoodOrder";
    public final static String METHOD_NAME_SAVE_ORDER = "SaveFastFoodOrder";

    public final static String SOAP_ACTION_SEND_MSG = "http://tempuri.org/SendMsgsOfFastFoodOrder";
    public final static String METHOD_NAME_SEND_MSG = "SendMsgsOfFastFoodOrder";

    public final static String SOAP_ACTION_CHECK_VACATION = "http://tempuri.org/CheckVacation";
    public final static String METHOD_NAME_CHECK_VACATION = "CheckVacation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cartLv = (ListView) findViewById(R.id.CartLv);
        cartFinalTotalTv = (TextView) findViewById(R.id.CartFinalTotalTv);
        cartDelChargeTv = (TextView) findViewById(R.id.CartDelChargeTv);
        cartGrandTotalTv = (TextView) findViewById(R.id.CartGrandTotalTv);
        cartCheckoutBtn = (Button) findViewById(R.id.CartCheckoutBtn);
        cartAddressBtn = (Button) findViewById(R.id.CartAddressBtn);
        resTv = (TextView) findViewById(R.id.ResTv);
        cartFixedAddressTv = (TextView) findViewById(R.id.CartFixedAddressTv);
        cartFixedChangeAddressBtn = (TextView) findViewById(R.id.CartFixedChangeAddressBtn);
        emptyCartLl=(LinearLayout)findViewById(R.id.EmptyCartLl);
        startShopBtn=(Button) findViewById(R.id.StartShopBtn);


        cityList = new ArrayList<>();
        areaList = new ArrayList<>();

        getSupportActionBar().setTitle("Cart");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sp = getSharedPreferences("pref", MODE_PRIVATE);

        productSet = sp.getStringSet("ProductSet", null);
        if (productSet != null)
            productList = new ArrayList<>(productSet);

        CheckEmptyCart();

        new GetDeliveryCharge().execute();
        new CheckVacation(cartCheckoutBtn).execute();


//        cartAdapter=new CartAdapter(CartActivity.this,fastFoodProductList);
//        cartLv.setAdapter(cartAdapter);



        cartAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OpenPopUp();

            }
        });


        cartFixedChangeAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenPopUp();
            }
        });

        startShopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CartActivity.this,DrawerActivity.class));
            }
        });

    }

    public class CheckVacation extends AsyncTask<String,Void,String>
    {
        Button cartCheckoutBtn;
        public CheckVacation(Button cartCheckoutBtn) {

            this.cartCheckoutBtn=cartCheckoutBtn;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            SoapObject request=new SoapObject(NAME_SPACE,METHOD_NAME_CHECK_VACATION);

            return  xmlParser.getServiceResultString(URL,SOAP_ACTION_CHECK_VACATION,request);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s.equalsIgnoreCase("Vacation"))
            {
                cartCheckoutBtn.setEnabled(false);
                cartCheckoutBtn.setText("Vacation Running");
                cartCheckoutBtn.setBackgroundColor(Color.parseColor("#FF0000"));
            }

        }
    }

    private void CheckEmptyCart() {

        if (!sp.contains("ProductSet")) {
//            Toast.makeText(this, "Cart Is Empty", Toast.LENGTH_LONG).show();
            emptyCartLl.setVisibility(View.VISIBLE);
            cartAddressBtn.setVisibility(View.GONE);
            cartCheckoutBtn.setEnabled(false);

        } else if (productList.size() == 0) {
            //Toast.makeText(this, "Cart Is Empty", Toast.LENGTH_LONG).show();
            emptyCartLl.setVisibility(View.VISIBLE);
            cartAddressBtn.setVisibility(View.GONE);
            cartCheckoutBtn.setEnabled(false);
        } else {
            for (int i = 0; i < productList.size(); i++) {
                ProductId = productList.get(i);
                new GetCart(ProductId).execute();
            }
        }
    }

    private void OpenPopUp() {

        ImageView popCloseBtn;

        View AddressView = getLayoutInflater().inflate(R.layout.address_for_ff_order_layout, null);

        cartCitySpinner = (Spinner) AddressView.findViewById(R.id.CartCitySpinner);
        cartAreaSpinner = (Spinner) AddressView.findViewById(R.id.CartAreaSpinner);
        cartAddressEt = (EditText) AddressView.findViewById(R.id.CartAddressEt);
        cartChangeAddressBtn = (TextView) AddressView.findViewById(R.id.CartChangeAddressBtn);
        cartSelectAddressBtn = (Button) AddressView.findViewById(R.id.CartSelectAddressBtn);
        popCloseBtn=(ImageView) AddressView.findViewById(R.id.CartPopUpCloseBtn);
        final TextView cartPopupDelAreaTv=(TextView) AddressView.findViewById(R.id.CartPopupDelAreaTv);

        cartAddressEt.setEnabled(false);

        final AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
//        builder.setTitle("Delivery Address Details");
        builder.setView(AddressView);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        //cityList.remove(1);
        UserId = sp.getString("UserId", null);
        new FetchAddress(cartAddressEt).execute();
        new FetchCity(cartCitySpinner).execute();

//                cartAddressEt.setText(Address);


        cartCitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(CartActivity.this, cityList.get(position), Toast.LENGTH_SHORT).show();

                if (position == 0) {
                    areaList.clear();
                    cartPopupDelAreaTv.setVisibility(View.GONE);
                    areaAdapter = new ArrayAdapter<String>(CartActivity.this, R.layout.support_simple_spinner_dropdown_item, areaList);
                    cartAreaSpinner.setAdapter(areaAdapter);
                } else {

                    areaList.clear();
                    cartPopupDelAreaTv.setVisibility(View.VISIBLE);
                    selectedCity = parent.getItemAtPosition(position).toString();
                    new AreaFetch().execute();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        cartChangeAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartAddressEt.setEnabled(true);
            }
        });


        cartSelectAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cartCitySpinner.getSelectedItemPosition() == 0) {
                    Toast.makeText(CartActivity.this, "Please Select City.", Toast.LENGTH_LONG).show();
                }
                else if (cartAreaSpinner.getSelectedItemPosition() == 0) {
                    Toast.makeText(CartActivity.this, "Please Select Area.", Toast.LENGTH_LONG).show();
                }
                else if (cartAddressEt.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(CartActivity.this, "Please Enter Address.", Toast.LENGTH_LONG).show();
                } else {
                    //Toast.makeText(CartActivity.this, "Perfect", Toast.LENGTH_SHORT).show();

                    String city = cityList.get(cartCitySpinner.getSelectedItemPosition());
                    String area = areaList.get(cartAreaSpinner.getSelectedItemPosition());
                    String add = cartAddressEt.getText().toString();

                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("DelCity", cartCitySpinner.getSelectedItem().toString());
                    editor.putString("DelArea", cartAreaSpinner.getSelectedItem().toString());
                    editor.commit();

                    alertDialog.dismiss();
                    CalculateFinalTotal();
                    ShowChectoutBtn(city, area, add);

                }
            }
        });

        popCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }

    private void ShowChectoutBtn(final String city, final String area, final String add) {

        cartFixedChangeAddressBtn.setVisibility(View.VISIBLE);
        cartFixedAddressTv.setVisibility(View.VISIBLE);
        cartFixedAddressTv.setText(add);
        cartCheckoutBtn.setVisibility(View.VISIBLE);
        cartAddressBtn.setVisibility(View.GONE);

        Animation animation= AnimationUtils.loadAnimation(CartActivity.this,R.anim.bounce);
        cartCheckoutBtn.setAnimation(animation);

        cartCheckoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                builder.setTitle("Confirm Order...");
                builder.setMessage("Are You Sure You Want To Book This Order??");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean canBook = CheckOut();
                        if (canBook == true) {
                            //Toast.makeText(CartActivity.this, "Can Book", Toast.LENGTH_SHORT).show();
                            BookOrder(city, area, add);

                        }
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.setCancelable(false);
                builder.show();
            }
        });

    }

    private void BookOrder(String city, String area, String add) {


        new FetchOrderId(city, area, add,sdf.format(Calendar.getInstance().getTime())).execute();

    }

    private boolean CheckOut() {


        for (int i = 0; i < cartAdapter.getCount(); i++) {
            View view = cartAdapter.getView(i, null, null);

            TextView canBookTv = (TextView) view.findViewById(R.id.CartCanBookTv);
            TextView productNamrTv = (TextView) view.findViewById(R.id.CartProductNameTv);

            if (canBookTv.getText().toString().equalsIgnoreCase("false")) {
                Toast.makeText(CartActivity.this, "You Can't Book " + productNamrTv.getText().toString() + " In This Time.", Toast.LENGTH_LONG).show();
                return false;
            }

        }
        return true;

    }

    public class FetchAddress extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialogAddress;
        EditText cartAddressEt;

        public FetchAddress(EditText cartAddressEt) {

            this.cartAddressEt = cartAddressEt;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialogAddress = new ProgressDialog(CartActivity.this);
            progressDialogAddress.setTitle("Please Wait....");
            progressDialogAddress.setMessage("Fetching Address....");
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
                cartAddressEt.setText(s);

        }
    }

    public class FetchCity extends AsyncTask<String, Void, SoapObject> {
        Spinner cartCitySpinner;
        public FetchCity(Spinner cartCitySpinner) {
            this.cartCitySpinner=cartCitySpinner;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(CartActivity.this);
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
                cityList.clear();
                for (int i = 0; i < soapObject.getPropertyCount(); i++) {
                    cityList.add(soapObject.getPropertyAsString(i));
                }
                cityList.remove(0);
                cityList.add(0, "Select City");
                cityAdapter = new ArrayAdapter<String>(CartActivity.this, R.layout.support_simple_spinner_dropdown_item, cityList);
                cartCitySpinner.setAdapter(cityAdapter);

                if (sp.contains("DelCity")) {
                    int position = cityList.indexOf(sp.getString("DelCity", null));
                    cartCitySpinner.setSelection(position);
//                    Toast.makeText(getActivity(), sp.getString("DelCity", null), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public class AreaFetch extends AsyncTask<String, Void, SoapObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(CartActivity.this);
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
                //areaList.remove(0);
                // cityList.add(0, "Select City");
                areaList.add(0, "Select Area");
                areaAdapter = new ArrayAdapter<String>(CartActivity.this, R.layout.support_simple_spinner_dropdown_item, areaList);
                cartAreaSpinner.setAdapter(areaAdapter);
                if (sp.contains("DelArea")) {

                    Integer areaposition = areaList.indexOf(sp.getString("DelArea", null));
//                    Toast.makeText(getActivity(), areaposition.toString(), Toast.LENGTH_SHORT).show();
                    if (areaposition != -1)
                        cartAreaSpinner.setSelection(areaposition);
                }
            }
        }
    }

    public class SaveOrder extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialogSaveOrder;
        String orderId, city, area, add, userId, productId, qty, unitPrice, totPrice, providerId,time;

        public SaveOrder(String time, String orderId, String city, String area, String add, String userId, String productId, String qty, String unitPrice, String totPrice, String providerId) {

            this.orderId = orderId;
            this.city = city;
            this.add = add;
            this.area = area;
            this.userId = userId;
            this.productId = productId;
            this.qty = qty;
            this.unitPrice = unitPrice;
            this.totPrice = totPrice;
            this.providerId = providerId;
            this.time=time;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialogSaveOrder = new ProgressDialog(CartActivity.this);
            progressDialogSaveOrder.setTitle("Booking Order....");
            progressDialogSaveOrder.setMessage("Please Wait....");
            progressDialogSaveOrder.setCancelable(false);
           // progressDialog.show();

        }


        @Override
        protected String doInBackground(String... params) {

            SoapObject request = new SoapObject(NAME_SPACE, METHOD_NAME_SAVE_ORDER);

            request.addProperty("OrderId", orderId);
            request.addProperty("Time",time);
            request.addProperty("City", city);
            request.addProperty("Area", area);
            request.addProperty("Address", add);
            request.addProperty("UserId", UserId);
            request.addProperty("ProductId", productId);
            request.addProperty("Qty", qty);
            request.addProperty("UnitPrice", unitPrice);
            request.addProperty("TotalPrice", totPrice);
            request.addProperty("ProviderId", providerId);


            return xmlParser.getServiceResultString(URL, SOAP_ACTION_SAVE_ORDER, request);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

//            if(s.equalsIgnoreCase("Vacation"))
//            {
//
//                AlertDialog.Builder builderErr=new AlertDialog.Builder(CartActivity.this);
//                builderErr.setTitle("Vacation");
//                builderErr.setMessage("You can not book the order due to vacation....");
//                builderErr.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                }).show();
//
//            }
            progressDialogSaveOrder.dismiss();
        }
    }


    public class FetchOrderId extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialogFetchOrderId;
        String city, area, add,time;

        public FetchOrderId(String city, String area, String add, String time) {
            this.city = city;
            this.area = area;
            this.add = add;
            this.time=time;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialogFetchOrderId = new ProgressDialog(CartActivity.this);
            progressDialogFetchOrderId.setTitle("Booking Order....");
            progressDialogFetchOrderId.setMessage("Please Wait....");
            progressDialogFetchOrderId.setCancelable(false);
            progressDialogFetchOrderId.show();


        }


        @Override
        protected String doInBackground(String... params) {

            SoapObject request = new SoapObject(NAME_SPACE, METHOD_NAME_ORDERID);

            return xmlParser.getServiceResultString(URL, SOAP_ACTION_ORDERID, request);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            progressDialogFetchOrderId.dismiss();

            for (int i = 0; i < cartLv.getCount(); i++) {
                String productId, qty, unitPrice, totPrice, providerId;

                View view = cartLv.getChildAt(i);
                TextView productIdTv = (TextView) view.findViewById(R.id.CartProductIdTv);
                TextView quntityTv = (TextView) view.findViewById(R.id.CartQtyTv);
                TextView unitPriceTv = (TextView) view.findViewById(R.id.CartPriceTv);
                TextView totalPriceTv = (TextView) view.findViewById(R.id.CartTotalPriceTv);
                TextView providerIdTv = (TextView) view.findViewById(R.id.CartProviderIdTv);

                UserId = sp.getString("UserId", null);
                productId = productIdTv.getText().toString();
                qty = quntityTv.getText().toString();
                unitPrice = unitPriceTv.getText().toString();
                totPrice = totalPriceTv.getText().toString();
                providerId = providerIdTv.getText().toString();

                //Toast.makeText(CartActivity.this,sdf.format(Calendar.getInstance().getTime()) , Toast.LENGTH_LONG).show();

                new SaveOrder(time,s,city, area, add, UserId, productId, qty, unitPrice, totPrice, providerId).execute();
            }

            new SendMsg(s).execute();
            Toast.makeText(CartActivity.this, "Yor Order Is Booked.", Toast.LENGTH_LONG).show();
            SharedPreferences.Editor editorOrder = sp.edit();
            editorOrder.remove("ProductSet");
            editorOrder.commit();

            startActivity(new Intent(CartActivity.this,CartActivity.class));

        }


        //  Toast.makeText(CartActivity.this,OrderId, Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.drawer, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.OpMenuLogOut) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Logging Out....");
            progressDialog.setMessage("Please Wait....");
            progressDialog.setCancelable(false);
            progressDialog.show();

            SharedPreferences.Editor editor = sp.edit();
            editor.remove("UserId");
            editor.commit();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                    startActivity(new Intent(CartActivity.this, LoginSignupActivity.class));
                    finish();
                }
            }, 1500);
        } else if (id == R.id.OpMenuCart) {
            startActivity(new Intent(CartActivity.this, CartActivity.class));
        }
        else if(id==R.id.OpMenuViewProfile)
        {
            startActivity(new Intent(CartActivity.this,ViewProfileActivity.class));
        }
        else if(id==android.R.id.home)
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public class GetDeliveryCharge extends AsyncTask<String, Void, SoapObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected SoapObject doInBackground(String... params) {

            SoapObject response = null;
            SoapObject request = new SoapObject(NAME_SPACE, METHOD_NAME_DEL_CHARGE);

            try {
                response = xmlParser.getServiceResultSoapObj(URL, SOAP_ACTION_DEL_CHARGE, request);
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response;
        }


        @Override
        protected void onPostExecute(SoapObject soapObject) {

            //resTv.setText(soapObject.toString());

            SoapObject s = (SoapObject) soapObject.getProperty(0);

            String charge_limit = s.getPropertyAsString("ChargeLimit");
            String del_charge = s.getPropertyAsString("DeliveryCharge");

            ChargeLimit = Integer.parseInt(charge_limit);
            DeliveryCharge = Integer.parseInt(del_charge);
            //Toast.makeText(CartActivity.this, soapObject.toString(), Toast.LENGTH_LONG).show();

            super.onPostExecute(soapObject);
        }
    }

    public class GetCart extends AsyncTask<String, Void, SoapObject> {
        SoapObject response;
        String PId;
        ProgressDialog progressDialogCart;

        public GetCart(String productId) {
            this.PId = productId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialogCart=new ProgressDialog(CartActivity.this);
            progressDialogCart.setTitle("Preparing Cart....");
            progressDialogCart.setMessage("Please Wait....");
            progressDialogCart.setCancelable(false);
            progressDialogCart.show();
        }


        @Override
        protected SoapObject doInBackground(String... params) {

            SoapObject request = new SoapObject(NAME_SPACE, METHOD_NAME);
            request.addProperty("ProductId", PId);

            try {
                response = (xmlParser.getServiceResultSoapObj(URL, SOAP_ACTION, request));
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

            progressDialogCart.dismiss();
            PrepareCart(s);

          //  Toast.makeText(CartActivity.this, s.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void PrepareCart(SoapObject s) {

        FastFoodProduct fastFoodProduct = new FastFoodProduct();

        fastFoodProduct.setProductId(s.getPropertyAsString("ProductId"));
        fastFoodProduct.setCategoryId(s.getPropertyAsString("CategoryId"));
        fastFoodProduct.setProviderCity(s.getPropertyAsString("ProviderCity"));
        fastFoodProduct.setProviderId(s.getPropertyAsString("ProviderId"));
        fastFoodProduct.setProductName(s.getPropertyAsString("ProductName"));
        fastFoodProduct.setDescription(s.getPropertyAsString("Description"));
        fastFoodProduct.setPrice(s.getPropertyAsString("Price"));
        fastFoodProduct.setStartTime(s.getPropertyAsString("StartTime"));
        fastFoodProduct.setEndTime(s.getPropertyAsString("EndTime"));
        fastFoodProduct.setImage(s.getPropertyAsString("Image"));
        fastFoodProduct.setCanBook(s.getPropertyAsString("CanBook"));
        fastFoodProduct.setDiscountedPrice(s.getPropertyAsString("DiscountedPrice"));
        //fastFoodProduct.setImage(R.mipmap.ic_launcher + "");

        // Toast.makeText(this, fastFoodProduct.getProductName(), Toast.LENGTH_SHORT).show();

        fastFoodProductList.add(fastFoodProduct);

        cartAdapter = new CartAdapter(CartActivity.this, fastFoodProductList);
        cartLv.setAdapter(cartAdapter);

        CalculateFinalTotal();

    }

    private class CartAdapter extends BaseAdapter {
        Context context;
        List<FastFoodProduct> fastFoodProductList;

        public CartAdapter(Context context, List<FastFoodProduct> fastFoodProductList) {

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

            ImageView cartProductIv;
            final TextView cartOldPriceTv,cartPerDisTv,cartProductNameTv, cartPriceTv, cartTotalPriceTv, cartRemoveBtn, cartQtyTv, cartDecreaseBtn, cartIncreaseBtn, cartCanBookTv, cartProductIdTv, cartProviderIdTv,cartBookTimeTv,productDescriptionTv;

            convertView = LayoutInflater.from(CartActivity.this).inflate(R.layout.cart_listview_row, parent, false);

            cartProductIv = (ImageView) convertView.findViewById(R.id.CartProductIv);
            cartProductNameTv = (TextView) convertView.findViewById(R.id.CartProductNameTv);
            cartPriceTv = (TextView) convertView.findViewById(R.id.CartPriceTv);
            cartTotalPriceTv = (TextView) convertView.findViewById(R.id.CartTotalPriceTv);
            cartQtyTv = (TextView) convertView.findViewById(R.id.CartQtyTv);
            productDescriptionTv = (TextView) convertView.findViewById(R.id.CartProductDescriptionTv);
            cartRemoveBtn = (TextView) convertView.findViewById(R.id.CartRemoveBtn);
            cartIncreaseBtn = (TextView) convertView.findViewById(R.id.CartQtyIncreaseBtn);
            cartDecreaseBtn = (TextView) convertView.findViewById(R.id.CartQtyDecreaseBtn);
            cartCanBookTv = (TextView) convertView.findViewById(R.id.CartCanBookTv);
            cartProductIdTv = (TextView) convertView.findViewById(R.id.CartProductIdTv);
            cartProviderIdTv = (TextView) convertView.findViewById(R.id.CartProviderIdTv);
            cartBookTimeTv=(TextView) convertView.findViewById(R.id.CartBookTimeTv);
            cartOldPriceTv=(TextView) convertView.findViewById(R.id.CartOldPriceTv);
            cartPerDisTv=(TextView) convertView.findViewById(R.id.CartPerDisTv);


            final FastFoodProduct fastFoodProduct = fastFoodProductList.get(position);

            Integer per=0;
            if(!fastFoodProduct.getDiscountedPrice().equalsIgnoreCase("anyType{}"))
            {
                per=(100-((Integer.parseInt(fastFoodProduct.getDiscountedPrice())*100)/Integer.parseInt(fastFoodProduct.getPrice())));
            }

            String path = fastFoodProduct.getImage().substring(1);
            //int price=Integer.parseInt(fastFoodProduct.getPrice());

            Picasso.with(CartActivity.this).load("http://tiffindabba.net/" + path).placeholder(R.mipmap.ic_launcher).fit().centerCrop().into(cartProductIv);
            cartProductNameTv.setText(fastFoodProduct.getProductName());
           // cartPriceTv.setText(fastFoodProduct.getPrice());
            //cartTotalPriceTv.setText(fastFoodProduct.getPrice());
            cartCanBookTv.setText(fastFoodProduct.getCanBook());
            cartProductIdTv.setText(fastFoodProduct.getProductId());
            cartProviderIdTv.setText(fastFoodProduct.getProviderId());
            cartBookTimeTv.append(fastFoodProduct.getStartTime()+" to "+fastFoodProduct.getEndTime());


            cartTotalPriceTv.setText(fastFoodProduct.getDiscountedPrice().equalsIgnoreCase("anyType{}")?fastFoodProduct.getPrice():fastFoodProduct.getDiscountedPrice());
            cartPriceTv.setText(fastFoodProduct.getDiscountedPrice().equalsIgnoreCase("anyType{}")?fastFoodProduct.getPrice():fastFoodProduct.getDiscountedPrice());
            cartOldPriceTv.setText(!fastFoodProduct.getDiscountedPrice().equalsIgnoreCase("anyType{}")?fastFoodProduct.getPrice()+" Rs.":"");
            cartOldPriceTv.setPaintFlags(cartOldPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            cartPerDisTv.setText(!fastFoodProduct.getDiscountedPrice().equalsIgnoreCase("anyType{}")?"("+per+" % Off)":"");
            productDescriptionTv.setVisibility(fastFoodProduct.getDescription().equalsIgnoreCase("anyType{}")?View.GONE:View.VISIBLE);
            productDescriptionTv.setText(!fastFoodProduct.getDescription().equalsIgnoreCase("anyType{}")?fastFoodProduct.getDescription():"");

            cartRemoveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fastFoodProductList.remove(position);
                    cartAdapter.notifyDataSetChanged();
                    Set<String> set = new HashSet<String>();
                    set = sp.getStringSet("ProductSet", null);
                    set.remove(fastFoodProduct.getProductId());
                    SharedPreferences.Editor editor = sp.edit();
                    editor.remove("ProductSet");
                    editor.putStringSet("ProductSet", set);
                    editor.commit();

                    productList = new ArrayList<>(productSet);
                    if (!sp.contains("ProductSet")) {
                        emptyCartLl.setVisibility(View.VISIBLE);
                        cartAddressBtn.setVisibility(View.GONE);
                        cartCheckoutBtn.setEnabled(false);
                        //Toast.makeText(CartActivity.this, "Cart Is Empty", Toast.LENGTH_LONG).show();
                        cartCheckoutBtn.setEnabled(false);
                    } else if (productList.size() == 0) {
                        emptyCartLl.setVisibility(View.VISIBLE);
                        cartAddressBtn.setVisibility(View.GONE);
                        cartCheckoutBtn.setEnabled(false);
                        //Toast.makeText(CartActivity.this, "Cart Is Empty", Toast.LENGTH_LONG).show();
                        cartCheckoutBtn.setEnabled(false);
                    }

                    //CheckEmptyCart();

                    CalculateFinalTotal();

                }
            });

            cartIncreaseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //IncreaseQty(cartQtyTv.getText().toString());
                    int qty = Integer.parseInt(cartQtyTv.getText().toString()) + 1;
                    int price = Integer.parseInt(cartPriceTv.getText().toString());
                    cartQtyTv.setText(qty + "");
                    cartTotalPriceTv.setText(price * qty + "");
                    //cartAdapter.notifyDataSetChanged();
                    //cartAdapter.notifyDataSetChanged();
                    CalculateFinalTotalIn(cartPriceTv.getText().toString());
                }
            });

            cartDecreaseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //  DecreaseQty(cartQtyTv.getText().toString());
                    if (cartQtyTv.getText().toString().equalsIgnoreCase("1")) {
                        Toast.makeText(context, "Quantity Can Not be Zero.", Toast.LENGTH_SHORT).show();
                    } else {
                        int price = Integer.parseInt(cartPriceTv.getText().toString());
                        int qty = Integer.parseInt(cartQtyTv.getText().toString()) - 1;
                        cartQtyTv.setText(qty + "");
                        cartTotalPriceTv.setText(price * qty + "");
                        // cartAdapter.notifyDataSetChanged();
                        //cartAdapter.notifyDataSetChanged();
                        CalculateFinalTotalDe(cartPriceTv.getText().toString());
                    }
                }
            });

            return convertView;
        }
    }

    private void CalculateFinalTotalDe(String s) {

        int oldTotal = Integer.parseInt(cartFinalTotalTv.getText().toString().substring(9));
        int price = Integer.parseInt(s);
        int finaltot = oldTotal - price;
        // Toast.makeText(this, cartFinalTotalTv.getText().toString()+" &  "+s, Toast.LENGTH_LONG).show();
        cartFinalTotalTv.setText("Total:-  " + finaltot);

        if (finaltot < ChargeLimit && finaltot > 0) {
            cartDelChargeTv.setText("Delivery Charge:-  " + DeliveryCharge);
            int grand_tot = finaltot + DeliveryCharge;
            cartGrandTotalTv.setText("Final Total:-  " + grand_tot);
        } else {
            cartDelChargeTv.setText("Delivery Charge:-  " + 0);
            int grand_tot = finaltot + 0;
            cartGrandTotalTv.setText("Final Total:-  " + grand_tot);
        }


    }

    private void CalculateFinalTotalIn(String s) {

        int oldTotal = Integer.parseInt(cartFinalTotalTv.getText().toString().substring(9));
        int price = Integer.parseInt(s);
        int finaltot = oldTotal + price;

        cartFinalTotalTv.setText("Total:-  " + finaltot);

        if (finaltot < ChargeLimit && finaltot > 0) {
            cartDelChargeTv.setText("Delivery Charge:-  " + DeliveryCharge);
            int grand_tot = finaltot + DeliveryCharge;
            cartGrandTotalTv.setText("Final Total:-  " + grand_tot);
        } else {
            cartDelChargeTv.setText("Delivery Charge:-  " + 0);
            int grand_tot = finaltot + 0;
            cartGrandTotalTv.setText("Final Total:-  " + grand_tot);
        }
    }

    private void CalculateFinalTotal() {

        int finalTotal = 0;

        for (int i = 0; i < cartAdapter.getCount(); i++) {
            View v = cartAdapter.getView(i, null, null);
            TextView tv = (TextView) v.findViewById(R.id.CartTotalPriceTv);
            int price = Integer.parseInt(tv.getText().toString());
            finalTotal += price;
        }

        cartFinalTotalTv.setText("Total:-  " + finalTotal);

        if (finalTotal < ChargeLimit && finalTotal > 0) {
            cartDelChargeTv.setText("Delivery Charge:-  " + DeliveryCharge);
            int grand_tot = finalTotal + DeliveryCharge;
            cartGrandTotalTv.setText("Final Total:-  " + grand_tot);
        } else {
            cartDelChargeTv.setText("Delivery Charge:-  " + 0);
            int grand_tot = finalTotal + 0;
            cartGrandTotalTv.setText("Final Total:-  " + grand_tot);
        }

    }

    private class SendMsg extends AsyncTask<String, Void, String> {
        String oid;

        public SendMsg(String s) {
            this.oid = s;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(String... params) {

            SoapObject request = new SoapObject(NAME_SPACE, METHOD_NAME_SEND_MSG);

            request.addProperty("OrderId", oid);


            return xmlParser.getServiceResultString(URL, SOAP_ACTION_SEND_MSG, request);
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            cartDelChargeTv.setText(s);
        }
    }
}

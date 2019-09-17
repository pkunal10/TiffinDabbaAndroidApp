package com.tiffin_dabba.admin.tiffindabba.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tiffin_dabba.admin.tiffindabba.ImportantClasses.CommonFunction;
import com.tiffin_dabba.admin.tiffindabba.ImportantClasses.RoundedCornerTransformation;
import com.tiffin_dabba.admin.tiffindabba.ImportantClasses.XMLParser;
import com.tiffin_dabba.admin.tiffindabba.Modals.FastFoodProduct;
import com.tiffin_dabba.admin.tiffindabba.Modals.SimpleTiffins;
import com.tiffin_dabba.admin.tiffindabba.Modals.SpecialTiffins;
import com.tiffin_dabba.admin.tiffindabba.Modals.TiffinWithSweets;
import com.tiffin_dabba.admin.tiffindabba.R;

import org.ksoap2.serialization.SoapObject;
import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



/**
 * Created by ADMIN on 25-09-2017.
 */

public class HomeFragment extends Fragment {

    LinearLayout ffProductPnl, stProductPnl, twsProductPnl, sptProductPnl;
    TextView lunchTimeTv, dinnerTv, ffTimeTv;
    RecyclerView ffRv, stRv, twsRv, imageRv, sptRv;
    List<FastFoodProduct> ffList;
    List<SimpleTiffins> stList;
    List<TiffinWithSweets> twsList;
    List<SpecialTiffins> sptList;
    CommonFunction commonFunction=new CommonFunction();
    List<String> imageList;
    FastFoodAdapter fastFoodAdapter;
    SimpleTiffinAdapter simpleTiffinAdapter;
    TiffinWithSweetAdapter tiffinWithSweetAdapter;
    ImageAdapter imageAdapter;
    SpecialTiffinHomeAdapter specialTiffinHomeAdapter;
    SharedPreferences sp;
    Set<String> productSet = new HashSet<>();
    XMLParser xmlParser = new XMLParser();

    public final static String NAME_SPACE = "http://tempuri.org/";
    public final static String URL = "http://tiffindabba.net/MyService.asmx";

    public final static String SOAP_ACTION_FETCH_DATA = "http://tempuri.org/GetDataForAppHome";
    public final static String METHOD_NAME_FETCH_DATA = "GetDataForAppHome";

    public final static String SOAP_ACTION_SAVE_TOKEN = "http://tempuri.org/SaveDeviceToken";
    public final static String METHOD_NAME_SAVE_TOKEN = "SaveDeviceToken";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_fragment_layout, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ffProductPnl = (LinearLayout) view.findViewById(R.id.FFProductPnl);
        stProductPnl = (LinearLayout) view.findViewById(R.id.STProductPnl);
        twsProductPnl = (LinearLayout) view.findViewById(R.id.TWSProductPnl);
        sptProductPnl = (LinearLayout) view.findViewById(R.id.SPTProductPnl);
        lunchTimeTv = (TextView) view.findViewById(R.id.LunchTimeTv);
        dinnerTv = (TextView) view.findViewById(R.id.DinnerTimeTv);
        ffTimeTv = (TextView) view.findViewById(R.id.FFTimeTv);
        ffRv = (RecyclerView) view.findViewById(R.id.FFRv);
        stRv = (RecyclerView) view.findViewById(R.id.STRv);
        sptRv = (RecyclerView) view.findViewById(R.id.SPTHomeRv);
        twsRv = (RecyclerView) view.findViewById(R.id.TWSRv);
        imageRv = (RecyclerView) view.findViewById(R.id.ImageRv);


        ffList = new ArrayList<>();
        stList = new ArrayList<>();
        twsList = new ArrayList<>();
        sptList=new ArrayList<>();
        imageList = new ArrayList<>();
        sp = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);

        if (!commonFunction.IsInternetAvailable(getActivity())) {
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        }

        new FetchHomeData().execute();


        LinearLayoutManager linearLayoutManagerffRv = new LinearLayoutManager(getActivity());
        linearLayoutManagerffRv.setOrientation(LinearLayoutManager.HORIZONTAL);
        ffRv.setLayoutManager(linearLayoutManagerffRv);

        LinearLayoutManager linearLayoutManagerstRv = new LinearLayoutManager(getActivity());
        linearLayoutManagerstRv.setOrientation(LinearLayoutManager.HORIZONTAL);
        stRv.setLayoutManager(linearLayoutManagerstRv);

        LinearLayoutManager linearLayoutManagertwsRv = new LinearLayoutManager(getActivity());
        linearLayoutManagertwsRv.setOrientation(LinearLayoutManager.HORIZONTAL);
        twsRv.setLayoutManager(linearLayoutManagertwsRv);

        LinearLayoutManager linearLayoutManagerimageRv = new LinearLayoutManager(getActivity());
        linearLayoutManagerimageRv.setOrientation(LinearLayoutManager.HORIZONTAL);
        imageRv.setLayoutManager(linearLayoutManagerimageRv);

        LinearLayoutManager linearLayoutManagersptRv = new LinearLayoutManager(getActivity());
        linearLayoutManagersptRv.setOrientation(LinearLayoutManager.HORIZONTAL);
        sptRv.setLayoutManager(linearLayoutManagersptRv);

        String UserId=sp.getString("UserId",null);
        String Token=sp.getString("token",null);

       // Toast.makeText(getActivity(), Token, Toast.LENGTH_SHORT).show();

        new SaveDeviceToken(UserId,Token).execute();

    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView imageRvIv;

        public ImageViewHolder(View itemView) {
            super(itemView);

            imageRvIv = (ImageView) itemView.findViewById(R.id.ImageRvIv);
        }
    }

    public class ImageAdapter extends RecyclerView.Adapter<ImageViewHolder> {
        Context context;
        List<String> imageList;

        public ImageAdapter(Context context, List<String> imageList) {
            this.context = context;
            this.imageList = imageList;
        }

        @Override
        public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View viewImage = LayoutInflater.from(context).inflate(R.layout.image_recycle_row, parent, false);

            ImageViewHolder holder = new ImageViewHolder(viewImage);

            return holder;
        }

        @Override
        public void onBindViewHolder(ImageViewHolder holder, int position) {

            String path = imageList.get(position).substring(1);

            Picasso.with(context).load("http://tiffindabba.net" + path).placeholder(R.mipmap.ic_launcher).fit().centerCrop().into(holder.imageRvIv);

        }

        @Override
        public int getItemCount() {
            return imageList.size();
        }
    }

    public class TiffinWithSweetViewHolder extends RecyclerView.ViewHolder {
        ImageView twsRvIv;
        TextView twsRvSabjiTv, twsRvRotiTv, twsRvDalTv, twsRvChawalTv, twsRvSweetTv, twsRvOtherTv, twsRvPriceTv;
        Button twsRvBookBtn;

        public TiffinWithSweetViewHolder(View itemView) {
            super(itemView);

            twsRvIv = (ImageView) itemView.findViewById(R.id.TwsRvIv);
            twsRvSabjiTv = (TextView) itemView.findViewById(R.id.TwsRvSabjiTv);
            twsRvRotiTv = (TextView) itemView.findViewById(R.id.TwsRvRotiTv);
            twsRvDalTv = (TextView) itemView.findViewById(R.id.TwsRvDalTv);
            twsRvChawalTv = (TextView) itemView.findViewById(R.id.TwsRvChawalTv);
            twsRvSweetTv = (TextView) itemView.findViewById(R.id.TwsRvSweetTv);
            twsRvOtherTv = (TextView) itemView.findViewById(R.id.TwsRvOtherTv);
            twsRvPriceTv = (TextView) itemView.findViewById(R.id.TwsRvPriceTv);
            twsRvBookBtn = (Button) itemView.findViewById(R.id.TwsRvBookBtn);
        }
    }

    public class TiffinWithSweetAdapter extends RecyclerView.Adapter<TiffinWithSweetViewHolder> {
        Context context;
        List<TiffinWithSweets> twsList;

        public TiffinWithSweetAdapter(Context context, List<TiffinWithSweets> twsList) {

            this.context = context;
            this.twsList = twsList;
        }

        @Override
        public TiffinWithSweetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View viewTWS = LayoutInflater.from(context).inflate(R.layout.tiffin_with_sweet_recycle_row, parent, false);

            TiffinWithSweetViewHolder holder = new TiffinWithSweetViewHolder(viewTWS);

            return holder;
        }

        @Override
        public void onBindViewHolder(TiffinWithSweetViewHolder holder, int position) {

            final TiffinWithSweets tiffinWithSweets = twsList.get(position);

            String path = tiffinWithSweets.getImage().substring(1);

            Picasso.with(context).load("http://tiffindabba.net" + path).placeholder(R.mipmap.ic_launcher).fit().centerCrop().into(holder.twsRvIv);
            holder.twsRvSabjiTv.setText("Sabji:- " + tiffinWithSweets.getSabji());
            holder.twsRvRotiTv.setText("Roti:- " + tiffinWithSweets.getRoti());
            holder.twsRvDalTv.setText("Dal:- " + tiffinWithSweets.getDal());
            holder.twsRvChawalTv.setText("Chawal:- " + tiffinWithSweets.getChawal());
            holder.twsRvSweetTv.setText("Sweet:- " + tiffinWithSweets.getSweet());
            holder.twsRvOtherTv.setText("Other:- " + tiffinWithSweets.getOther());
            holder.twsRvPriceTv.setText("Price:- " + tiffinWithSweets.getPrice());

            holder.twsRvSabjiTv.setVisibility(tiffinWithSweets.getSabji().equalsIgnoreCase("anyType{}") ? View.GONE : View.VISIBLE);
            holder.twsRvRotiTv.setVisibility(tiffinWithSweets.getRoti().equalsIgnoreCase("anyType{}") ? View.GONE : View.VISIBLE);

            holder.twsRvBookBtn.setEnabled(tiffinWithSweets.getIsVacation().equalsIgnoreCase("true")?false:true);
            holder.twsRvBookBtn.setText(tiffinWithSweets.getBtnTxt().equalsIgnoreCase("anyType{}")?"Book":tiffinWithSweets.getBtnTxt());



            holder.twsRvBookBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String TWSTiffinId = tiffinWithSweets.getTiffinId();

                    //Toast.makeText(context, STTiffinId, Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.DrawerFragmentsContainer, new TiffinWithSweetFragment(TWSTiffinId)).commit();
                }
            });
        }

        @Override
        public int getItemCount() {
            return twsList.size();
        }
    }

    public class SimpleTiffinViewHolder extends RecyclerView.ViewHolder {
        ImageView stRvIv;
        TextView stRvSabjiTv, stRvRotiTv, stRvDalTv, stRvChawalTv, stRvOtherTv, stRvPriceTv;
        Button stRvBookBtn;

        public SimpleTiffinViewHolder(View itemView) {
            super(itemView);

            stRvIv = (ImageView) itemView.findViewById(R.id.STRvIv);
            stRvSabjiTv = (TextView) itemView.findViewById(R.id.STRvSabjiTv);
            stRvRotiTv = (TextView) itemView.findViewById(R.id.STRvRotiTv);
            stRvDalTv = (TextView) itemView.findViewById(R.id.STRvDalTv);
            stRvChawalTv = (TextView) itemView.findViewById(R.id.STRvChawalTv);
            stRvOtherTv = (TextView) itemView.findViewById(R.id.STRvOtherTv);
            stRvPriceTv = (TextView) itemView.findViewById(R.id.STRvPriceTv);
            stRvBookBtn = (Button) itemView.findViewById(R.id.STRvBookBtn);
        }
    }

    public class SimpleTiffinAdapter extends RecyclerView.Adapter<SimpleTiffinViewHolder> {
        Context context;
        List<SimpleTiffins> stList;

        public SimpleTiffinAdapter(Context context, List<SimpleTiffins> stList) {
            this.context = context;
            this.stList = stList;
        }

        @Override
        public SimpleTiffinViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View viewST = LayoutInflater.from(context).inflate(R.layout.simple_tiffin_recycle_row, parent, false);

            SimpleTiffinViewHolder holder = new SimpleTiffinViewHolder(viewST);

            return holder;
        }

        @Override
        public void onBindViewHolder(SimpleTiffinViewHolder holder, int position) {

            final SimpleTiffins simpleTiffins = stList.get(position);

            String path = simpleTiffins.getImage().substring(1);

            Picasso.with(context).load("http://tiffindabba.net" + path).placeholder(R.mipmap.ic_launcher).fit().centerCrop().into(holder.stRvIv);
            holder.stRvSabjiTv.setText("Sabji:- " + simpleTiffins.getSabji());
            holder.stRvRotiTv.setText("Roti:- " + simpleTiffins.getRoti());
            holder.stRvDalTv.setText("Dal:- " + simpleTiffins.getDal());
            holder.stRvChawalTv.setText("Chawal:- " + simpleTiffins.getChawal());
            holder.stRvOtherTv.setText("Other:- " + simpleTiffins.getOther());
            holder.stRvPriceTv.setText("Price:- " + simpleTiffins.getPrice());

            holder.stRvSabjiTv.setVisibility(simpleTiffins.getSabji().equalsIgnoreCase("anyType{}") ? View.GONE : View.VISIBLE);
            holder.stRvRotiTv.setVisibility(simpleTiffins.getRoti().equalsIgnoreCase("anyType{}") ? View.GONE : View.VISIBLE);

            holder.stRvBookBtn.setEnabled(simpleTiffins.getIsVacation().equalsIgnoreCase("true")?false:true);
            holder.stRvBookBtn.setText(simpleTiffins.getBtnTxt().equalsIgnoreCase("anyType{}")?"Book":simpleTiffins.getBtnTxt());

            holder.stRvBookBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String STTiffinId = simpleTiffins.getTiffinId();

                    //Toast.makeText(context, STTiffinId, Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.DrawerFragmentsContainer, new SimpleTiffinFragment(STTiffinId)).commit();
//                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.DrawerFragmentsContainer,new TiffinBookFragment("ST",STTiffinId)).commit();
                }
            });
        }

        @Override
        public int getItemCount() {
            return stList.size();
        }
    }

    public class FastFoodViewHolder extends RecyclerView.ViewHolder {

        ImageView ffRvIv;
        TextView ffRvProductNameTv, ffRvPriceTv, ffRvBookTimeTv,ffRvOldPriceTv,ffRvPerDisTv,ffRvDescriptionTv;//,ffRvProductIdTv;
        Button ffRvAddCartBtn;

        public FastFoodViewHolder(View itemView) {
            super(itemView);

            ffRvIv = (ImageView) itemView.findViewById(R.id.FFRvImage);
            ffRvBookTimeTv = (TextView) itemView.findViewById(R.id.FFRvBookTimeTv);
            ffRvProductNameTv = (TextView) itemView.findViewById(R.id.FFRvProductNameTv);
            //ffRvProductIdTv=(TextView) itemView.findViewById(R.id.FFRvProductId);
            ffRvPriceTv = (TextView) itemView.findViewById(R.id.FFRvProductPriceTv);
            ffRvDescriptionTv = (TextView) itemView.findViewById(R.id.FFRvProductDescriptionTv);
            ffRvOldPriceTv = (TextView) itemView.findViewById(R.id.FFRvProductOldPriceTv);
            ffRvPerDisTv = (TextView) itemView.findViewById(R.id.FFRvProductPerDisTv);
            ffRvAddCartBtn = (Button) itemView.findViewById(R.id.FFRvAddCartBtn);

        }
    }


    private class FastFoodAdapter extends RecyclerView.Adapter<FastFoodViewHolder> {
        Context context;
        List<FastFoodProduct> ffList;

        public FastFoodAdapter(Context context, List<FastFoodProduct> ffList) {
            this.ffList = ffList;
            this.context = context;
        }

        @Override
        public FastFoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View viewFF = LayoutInflater.from(context).inflate(R.layout.fastfood_recycle_row, parent, false);

            FastFoodViewHolder fastFoodViewHolder = new FastFoodViewHolder(viewFF);

            return fastFoodViewHolder;
        }

        @Override
        public void onBindViewHolder(FastFoodViewHolder holder, int position) {

            final FastFoodProduct fastFoodProduct = ffList.get(position);

            Integer per=0;
            if(!fastFoodProduct.getDiscountedPrice().equalsIgnoreCase("anyType{}"))
            {
                per=(100-((Integer.parseInt(fastFoodProduct.getDiscountedPrice())*100)/Integer.parseInt(fastFoodProduct.getPrice())));
            }

            String path = fastFoodProduct.getImage().substring(1);

            holder.ffRvBookTimeTv.setText("");
            Picasso.with(context).load("http://tiffindabba.net" + path).placeholder(R.mipmap.ic_launcher).fit().centerCrop().into(holder.ffRvIv);
            holder.ffRvBookTimeTv.setText("Booking Time:- " + fastFoodProduct.getStartTime() + " to " + fastFoodProduct.getEndTime());
            holder.ffRvProductNameTv.setText(fastFoodProduct.getProductName());
            //holder.ffRvPriceTv.setText("Price :-  " + fastFoodProduct.getPrice());
            //holder.ffRvProductIdTv.setText(fastFoodProduct.getProductId());
            holder.ffRvAddCartBtn.setEnabled(fastFoodProduct.getCanBook().equalsIgnoreCase("false") ? false : true);

            holder.ffRvPriceTv.setText(fastFoodProduct.getDiscountedPrice().equalsIgnoreCase("anyType{}")?"Price:-  " + fastFoodProduct.getPrice():"Price:-  " + fastFoodProduct.getDiscountedPrice());
            holder.ffRvOldPriceTv.setText(!fastFoodProduct.getDiscountedPrice().equalsIgnoreCase("anyType{}")?fastFoodProduct.getPrice()+" Rs.":"");
            holder.ffRvOldPriceTv.setPaintFlags(holder.ffRvOldPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.ffRvPerDisTv.setText(!fastFoodProduct.getDiscountedPrice().equalsIgnoreCase("anyType{}")?"("+per+" % Off)":"");
            holder.ffRvDescriptionTv.setVisibility(fastFoodProduct.getDescription().equalsIgnoreCase("anyType{}")?View.GONE:View.VISIBLE);
            holder.ffRvDescriptionTv.setText(!fastFoodProduct.getDescription().equalsIgnoreCase("anyType{}")?fastFoodProduct.getDescription():"");

            holder.ffRvAddCartBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String ProductId = fastFoodProduct.getProductId();

                    AddProductInCart(ProductId);
                }
            });
        }

        @Override
        public int getItemCount() {
            return ffList.size();
        }
    }


    public class SpecialTiffinHomeViewHolder extends RecyclerView.ViewHolder {
        ImageView sptIv;
        TextView sptTimeTv, sptNameTv, sptDescriptionTv, sptPriceTv;
        Button sptBookBtn;

        public SpecialTiffinHomeViewHolder(View itemView) {
            super(itemView);

            sptIv = (ImageView) itemView.findViewById(R.id.SPTIv);
            sptTimeTv = (TextView) itemView.findViewById(R.id.SPTBookTimeTv);
            sptNameTv = (TextView) itemView.findViewById(R.id.SPTNameTv);
            sptDescriptionTv = (TextView) itemView.findViewById(R.id.SPTDescriptionTv);
            sptPriceTv = (TextView) itemView.findViewById(R.id.SPTPriceTv);
            sptBookBtn = (Button) itemView.findViewById(R.id.SPTBookBtn);
        }
    }

    public class SpecialTiffinHomeAdapter extends RecyclerView.Adapter<SpecialTiffinHomeViewHolder> {
        Context context;
        List<SpecialTiffins> specialTiffinsList;

        public SpecialTiffinHomeAdapter(Context context, List<SpecialTiffins> specialTiffinsList) {
            this.context = context;
            this.specialTiffinsList = specialTiffinsList;
        }

        @Override
        public SpecialTiffinHomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(context).inflate(R.layout.special_tiffin_home_recycle_row, parent, false);
            SpecialTiffinHomeViewHolder specialTiffinHomeViewHolder = new SpecialTiffinHomeViewHolder(view);
            return specialTiffinHomeViewHolder;
        }

        @Override
        public void onBindViewHolder(SpecialTiffinHomeViewHolder holder, int position) {

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
                    String SPTTiffinId = specialTiffins.getTiffinId();

                    //Toast.makeText(context, STTiffinId, Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.DrawerFragmentsContainer, new SpecialTiffinFragment(SPTTiffinId)).commit();
//                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.DrawerFragmentsContainer,new TiffinBookFragment("ST",STTiffinId)).commit();
                }
            });
        }

        @Override
        public int getItemCount() {
            return specialTiffinsList.size();
        }
    }


    public class FetchHomeData extends AsyncTask<String, Void, SoapObject> {
        ProgressDialog progressDialogHomeData;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialogHomeData = new ProgressDialog(getActivity());
            progressDialogHomeData.setTitle("Please Wait....");
            progressDialogHomeData.setMessage("Fetching Data....");
            progressDialogHomeData.setCancelable(false);
            progressDialogHomeData.show();
        }

        @Override
        protected SoapObject doInBackground(String... params) {

            SoapObject request = new SoapObject(NAME_SPACE, METHOD_NAME_FETCH_DATA);

            try {
                return xmlParser.getServiceResultSoapObj(URL, SOAP_ACTION_FETCH_DATA, request);
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

            progressDialogHomeData.dismiss();
            if (soapObject != null) {
                SoapObject timeSobj = (SoapObject) soapObject.getProperty("OrderTimmings");
                DisplayOdrTime(timeSobj);

                SoapObject ffSobj = (SoapObject) soapObject.getProperty("FastFoodList");
                ShowFFDataInRv(ffSobj);

                SoapObject stSobj = (SoapObject) soapObject.getProperty("SimpleTiffinList");
                ShowSTDataInRv(stSobj);

                SoapObject twsSobj = (SoapObject) soapObject.getProperty("TiffinWithSweetsList");
                ShowTWSDataInRv(twsSobj);

                SoapObject imageSobj = (SoapObject) soapObject.getProperty("Images");
                ShowImages(imageSobj);

                SoapObject sptSobj= (SoapObject) soapObject.getProperty("SpecialTiffinList");
                ShowSPTDataInRv(sptSobj);
            }
        }
    }

    private void ShowSPTDataInRv(SoapObject sptSobj) {

        SoapObject sobj = (SoapObject) sptSobj.getProperty(0);
        if (sobj.getPropertyAsString("Status").equalsIgnoreCase("NOSPT")) {
            sptProductPnl.setVisibility(View.GONE);
        } else {
            for (int i = 0; i < sptSobj.getPropertyCount(); i++) {
                SoapObject SobjSuccess = (SoapObject) sptSobj.getProperty(i);
                SpecialTiffins specialTiffins = new SpecialTiffins();

                specialTiffins.setTiffinId(SobjSuccess.getPropertyAsString("TiffinId"));
                specialTiffins.setProviderCity(SobjSuccess.getPropertyAsString("ProviderCity"));
                specialTiffins.setProviderId(SobjSuccess.getPropertyAsString("ProviderId"));
                specialTiffins.setTiffinName((SobjSuccess.getPropertyAsString("TiffinName")));
                specialTiffins.setDescription(SobjSuccess.getPropertyAsString("Description"));
                specialTiffins.setPrice(SobjSuccess.getPropertyAsString("Price"));
                specialTiffins.setStartTime(SobjSuccess.getPropertyAsString("StartTime"));
                specialTiffins.setEndTime(SobjSuccess.getPropertyAsString("EndTime"));
                specialTiffins.setImage(SobjSuccess.getPropertyAsString("Image"));
                specialTiffins.setIsVacation(sobj.getPropertyAsString("IsVacation"));
                specialTiffins.setBtnTxt(sobj.getPropertyAsString("BtnTxt"));

                sptList.add(specialTiffins);

                specialTiffinHomeAdapter=new SpecialTiffinHomeAdapter(getActivity(),sptList);
                sptRv.setAdapter(specialTiffinHomeAdapter);

            }
        }

    }

    private void ShowImages(SoapObject imageSobj) {

        for (int i = 0; i < imageSobj.getPropertyCount(); i++) {

            imageList.add(imageSobj.getPropertyAsString(i));
            imageAdapter = new ImageAdapter(getActivity(), imageList);
            imageRv.setAdapter(imageAdapter);
        }
    }

    private void ShowTWSDataInRv(SoapObject twsSobj) {

        SoapObject sobj = (SoapObject) twsSobj.getProperty(0);
        if (sobj.getPropertyAsString("Status").equalsIgnoreCase("NOTWS")) {
            twsProductPnl.setVisibility(View.GONE);
        } else {
            for (int i = 0; i < twsSobj.getPropertyCount(); i++) {
                SoapObject SobjSuccess = (SoapObject) twsSobj.getProperty(i);
                TiffinWithSweets tiffinWithSweets = new TiffinWithSweets();

                tiffinWithSweets.setTiffinId(SobjSuccess.getPropertyAsString("TiffinId"));
                tiffinWithSweets.setProviderCity(SobjSuccess.getPropertyAsString("ProviderCity"));
                tiffinWithSweets.setProviderId(SobjSuccess.getPropertyAsString("ProviderId"));
                tiffinWithSweets.setProviderName(SobjSuccess.getPropertyAsString("ProviderName"));
                tiffinWithSweets.setSabji(SobjSuccess.getPropertyAsString("Sabji"));
                tiffinWithSweets.setRoti(SobjSuccess.getPropertyAsString("Roti"));
                tiffinWithSweets.setDal(SobjSuccess.getPropertyAsString("Dal"));
                tiffinWithSweets.setChawal(SobjSuccess.getPropertyAsString("Chawal"));
                tiffinWithSweets.setSweet(SobjSuccess.getPropertyAsString("Sweet"));
                tiffinWithSweets.setOther(SobjSuccess.getPropertyAsString("Other"));
                tiffinWithSweets.setPrice(SobjSuccess.getPropertyAsString("Price"));
                tiffinWithSweets.setTagline(SobjSuccess.getPropertyAsString("Tagline"));
                tiffinWithSweets.setRecommend(SobjSuccess.getPropertyAsString("Recommend"));
                tiffinWithSweets.setImage(SobjSuccess.getPropertyAsString("Image"));
                tiffinWithSweets.setIsVacation(sobj.getPropertyAsString("IsVacation"));
                tiffinWithSweets.setBtnTxt(sobj.getPropertyAsString("BtnTxt"));
                // simpleTiffins.setAvailable(sobj.getPropertyAsString("Available"));

                twsList.add(tiffinWithSweets);

                tiffinWithSweetAdapter = new TiffinWithSweetAdapter(getActivity(), twsList);
                twsRv.setAdapter(tiffinWithSweetAdapter);
            }
        }

    }

    private void ShowSTDataInRv(SoapObject stSobj) {

        SoapObject sobj = (SoapObject) stSobj.getProperty(0);
        if (sobj.getPropertyAsString("Status").equalsIgnoreCase("NOST")) {
            stProductPnl.setVisibility(View.GONE);
        } else {
            for (int i = 0; i < stSobj.getPropertyCount(); i++) {
                SoapObject SobjSuccess = (SoapObject) stSobj.getProperty(i);
                SimpleTiffins simpleTiffins = new SimpleTiffins();

                simpleTiffins.setTiffinId(SobjSuccess.getPropertyAsString("TiffinId"));
                simpleTiffins.setProviderCity(SobjSuccess.getPropertyAsString("ProviderCity"));
                simpleTiffins.setProviderId(SobjSuccess.getPropertyAsString("ProviderId"));
                simpleTiffins.setProviderName(SobjSuccess.getPropertyAsString("ProviderName"));
                simpleTiffins.setSabji(SobjSuccess.getPropertyAsString("Sabji"));
                simpleTiffins.setRoti(SobjSuccess.getPropertyAsString("Roti"));
                simpleTiffins.setDal(SobjSuccess.getPropertyAsString("Dal"));
                simpleTiffins.setChawal(SobjSuccess.getPropertyAsString("Chawal"));
                simpleTiffins.setOther(SobjSuccess.getPropertyAsString("Other"));
                simpleTiffins.setPrice(SobjSuccess.getPropertyAsString("Price"));
                simpleTiffins.setTagline(SobjSuccess.getPropertyAsString("Tagline"));
                simpleTiffins.setRecommend(SobjSuccess.getPropertyAsString("Recommend"));
                simpleTiffins.setImage(SobjSuccess.getPropertyAsString("Image"));
                simpleTiffins.setIsVacation(sobj.getPropertyAsString("IsVacation"));
                simpleTiffins.setBtnTxt(sobj.getPropertyAsString("BtnTxt"));
                // simpleTiffins.setAvailable(sobj.getPropertyAsString("Available"));

                stList.add(simpleTiffins);

                simpleTiffinAdapter = new SimpleTiffinAdapter(getActivity(), stList);
                stRv.setAdapter(simpleTiffinAdapter);
            }
        }
    }

    private void ShowFFDataInRv(SoapObject ffSobj) {
        SoapObject sobj = (SoapObject) ffSobj.getProperty(0);
        if (sobj.getPropertyAsString("Status").equalsIgnoreCase("NOFF")) {
            ffProductPnl.setVisibility(View.GONE);
        } else {
            for (int i = 0; i < ffSobj.getPropertyCount(); i++) {
                SoapObject SobjSuccess = (SoapObject) ffSobj.getProperty(i);
                FastFoodProduct fastFoodProduct = new FastFoodProduct();

                fastFoodProduct.setProductId(SobjSuccess.getPropertyAsString("ProductId"));
                fastFoodProduct.setCategoryId(SobjSuccess.getPropertyAsString("CategoryId"));
                fastFoodProduct.setProviderCity(SobjSuccess.getPropertyAsString("ProviderCity"));
                fastFoodProduct.setProviderId(SobjSuccess.getPropertyAsString("ProviderId"));
                fastFoodProduct.setProductName(SobjSuccess.getPropertyAsString("ProductName"));
                fastFoodProduct.setDescription(SobjSuccess.getPropertyAsString("Description"));
                fastFoodProduct.setPrice(SobjSuccess.getPropertyAsString("Price"));
                fastFoodProduct.setStartTime(SobjSuccess.getPropertyAsString("StartTime"));
                fastFoodProduct.setEndTime(SobjSuccess.getPropertyAsString("EndTime"));
                fastFoodProduct.setImage(SobjSuccess.getPropertyAsString("Image"));
                fastFoodProduct.setCanBook(SobjSuccess.getPropertyAsString("CanBook"));
                fastFoodProduct.setDiscountedPrice(SobjSuccess.getPropertyAsString("DiscountedPrice"));

                ffList.add(fastFoodProduct);

                fastFoodAdapter = new FastFoodAdapter(getActivity(), ffList);
                ffRv.setAdapter(fastFoodAdapter);
            }
        }
    }

    private void DisplayOdrTime(SoapObject timeSobj) {

        String LunchTime = timeSobj.getPropertyAsString(0);
        String DinnerTime = timeSobj.getPropertyAsString(1);

        lunchTimeTv.append("Same day before " + LunchTime + ".");
        dinnerTv.append("Same day before " + DinnerTime + ".");
    }

    private void AddProductInCart(String productId) {

        if (sp.contains("ProductSet")) {
            Set<String> pSet = new HashSet<>();
            pSet = sp.getStringSet("ProductSet", null);
            List<String> plist = new ArrayList<>(pSet);
            if (pSet.contains(productId)) {
                Toast.makeText(getActivity(), "Product Already Added In Cart", Toast.LENGTH_LONG).show();
            } else {

                productSet.add(productId);
                for (int i = 0; i < plist.size(); i++) {
                    productSet.add(plist.get(i));
                }
                SharedPreferences.Editor editor = sp.edit();
                // productSet.add(productId);
                editor.putStringSet("ProductSet", productSet);
                editor.commit();

                Toast.makeText(getActivity(), "Product Added In Cart", Toast.LENGTH_LONG).show();
            }
        } else {

            SharedPreferences.Editor editor = sp.edit();
            productSet.add(productId);
            editor.putStringSet("ProductSet", productSet);
            editor.commit();

            Toast.makeText(getActivity(), "Product Added In Cart", Toast.LENGTH_LONG).show();
        }
    }

    public class SaveDeviceToken extends AsyncTask<String,Void,String>
    {
        String userId,token;
        public SaveDeviceToken(String userId, String token) {

            this.userId=userId;
            this.token=token;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            SoapObject request=new SoapObject(NAME_SPACE,METHOD_NAME_SAVE_TOKEN);

            request.addProperty("UserId",userId);
            request.addProperty("DeviceToken",token);

            return xmlParser.getServiceResultString(URL,SOAP_ACTION_SAVE_TOKEN,request);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
}

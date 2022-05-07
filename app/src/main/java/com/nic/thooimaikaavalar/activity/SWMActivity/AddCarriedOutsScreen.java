package com.nic.thooimaikaavalar.activity.SWMActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nic.thooimaikaavalar.Interface.AdapterCameraIntent;
import com.nic.thooimaikaavalar.R;
import com.nic.thooimaikaavalar.activity.NewMainPage;
import com.nic.thooimaikaavalar.adapter.CarriedOutWastDumpAdapter;
import com.nic.thooimaikaavalar.adapter.ClearedWasteDumbAdapter;
import com.nic.thooimaikaavalar.adapter.CommonAdapter;
import com.nic.thooimaikaavalar.adapter.WastedumpDetailsAdapter;
import com.nic.thooimaikaavalar.api.Api;
import com.nic.thooimaikaavalar.api.ApiService;
import com.nic.thooimaikaavalar.api.ServerResponse;
import com.nic.thooimaikaavalar.constant.AppConstant;
import com.nic.thooimaikaavalar.dataBase.DBHelper;
import com.nic.thooimaikaavalar.dataBase.dbData;
import com.nic.thooimaikaavalar.databinding.ActivityAddCarriedOutsScreenBinding;
import com.nic.thooimaikaavalar.databinding.ActivitySwmMasterDetailsViewBinding;
import com.nic.thooimaikaavalar.model.RealTimeMonitoringSystem;
import com.nic.thooimaikaavalar.session.PrefManager;
import com.nic.thooimaikaavalar.support.MyLocationListener;
import com.nic.thooimaikaavalar.utils.CameraUtils;
import com.nic.thooimaikaavalar.utils.UrlGenerator;
import com.nic.thooimaikaavalar.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;

public class AddCarriedOutsScreen extends AppCompatActivity implements  Api.ServerResponseListener {
    ActivityAddCarriedOutsScreenBinding carriedOutsScreenBinding;
    public com.nic.thooimaikaavalar.dataBase.dbData dbData = new dbData(this);
    private PrefManager prefManager;
    private SQLiteDatabase db;
    public DBHelper dbHelper;


    String swm_infra_details_id ="";
    String no_of_thooimai_kaavalars_allocated ="";
    String no_of_thooimai_kaavalars_working ="";
    String whether_community_compost_pit_available_in_panchayat ="";
    String whether_vermi_compost_pit_available_in_panchayat ="";
    String any_integrated_nuesery_devlp_near_swm_facility ="";
    String is_plastic_connected_to_waste_management_unit ="";

    ArrayList<RealTimeMonitoringSystem> carriedOutDateList;
    private String choose_date_string="";
    CarriedOutWastDumpAdapter carriedOutWastDumpAdapter;
    ClearedWasteDumbAdapter clearedWasteDumbAdapter;

    ArrayList<RealTimeMonitoringSystem> carriedOutListDetails;
    ArrayList<RealTimeMonitoringSystem> viewClearedWasteDumpList;

    public static final int MEDIA_TYPE_IMAGE = 1;

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 2500;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static String imageStoragePath;
    public static final int BITMAP_SAMPLE_SIZE = 8;

    LocationManager mlocManager = null;
    LocationListener mlocListener;
    Double offlatTextValue, offlongTextValue;

    AdapterCameraIntent adapterCameraIntent;
    int clicked_position;

    String total_quantity_of_waste="";
    String quantity_of_bio_degradable_waste="";
    String total_quantity_of_compost_generated_from_community="";
    String total_quantity_of_compost_generated_from_vermi="";
    String quantity_of_compost_sold = "";
    String total_revenue_generated = "";
    String toast_flag="";


    String quantity_of_compostable_waste_recycle="";
    String total_recycle_plastic_waste_revenue_generated="";
    String total_plastic_waste="";
    String total_plastic_waste_revenue_generated="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        carriedOutsScreenBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_carried_outs_screen);
        carriedOutsScreenBinding.setActivity(this);

        prefManager = new PrefManager(this);
        Utils.setLocale("ta",this);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        dbData.open();
        getIntentData();
        loadCarriedOutDateList();
        carriedOutsScreenBinding.backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        carriedOutsScreenBinding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidation();
            }
        });

        /*int gettableCountWasteDumpTable = dbData.gettableCountWasteDumpTable();
        if(gettableCountWasteDumpTable==0){
            getOnlineWasteDumpList();
        }
        else {
            //Utils.showAlert(AddCarriedOutsScreen.this,"First Upload Your WastDump Data");
            Toasty.error(AddCarriedOutsScreen.this,"First Upload Your WastDump Data",Toasty.LENGTH_LONG);
            onBackPressed();
        }

*/
        carriedOutListDetails = new ArrayList<>();
        //carriedOutWastDumpAdapter = new CarriedOutWastDumpAdapter(carriedOutListDetails,this,"Server",dbData);

        carriedOutsScreenBinding.chooseDateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0){
                    choose_date_string = carriedOutDateList.get(position).getCarried_out_date();
                    carriedOutsScreenBinding.totalQuantityOfWaste.setText("");
                    carriedOutsScreenBinding.quantityOfBioDegradableWaste.setText("");
                    carriedOutsScreenBinding.totalQuantityOfCompostGeneratedFromCommunity.setText("");
                    carriedOutsScreenBinding.totalQuantityOfCompostGeneratedFromVermi.setText("");
                    carriedOutsScreenBinding.quantityOfCompostSold.setText("");
                    carriedOutsScreenBinding.totalRevenueGenerated.setText("");

                    carriedOutsScreenBinding.quantityOfCompostableWasteRecycle.setText("");
                    carriedOutsScreenBinding.totalRecyclePlasticWasteRevenueGenerated.setText("");
                    carriedOutsScreenBinding.totalPlasticWaste.setText("");
                    carriedOutsScreenBinding.totalPlasticWasteRevenueGenerated.setText("");

                    carriedOutsScreenBinding.wasteDumpRecycler.setAdapter(null);
                    getOnlineWasteDumpList();
                }
                else {
                    choose_date_string = "";
                    carriedOutsScreenBinding.totalQuantityOfWaste.setText("");
                    carriedOutsScreenBinding.quantityOfBioDegradableWaste.setText("");
                    carriedOutsScreenBinding.totalQuantityOfCompostGeneratedFromCommunity.setText("");
                    carriedOutsScreenBinding.totalQuantityOfCompostGeneratedFromVermi.setText("");
                    carriedOutsScreenBinding.quantityOfCompostSold.setText("");
                    carriedOutsScreenBinding.totalRevenueGenerated.setText("");

                    carriedOutsScreenBinding.quantityOfCompostableWasteRecycle.setText("");
                    carriedOutsScreenBinding.totalRecyclePlasticWasteRevenueGenerated.setText("");
                    carriedOutsScreenBinding.totalPlasticWaste.setText("");
                    carriedOutsScreenBinding.totalPlasticWasteRevenueGenerated.setText("");

                    carriedOutsScreenBinding.wasteDumpRecycler.setAdapter(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        carriedOutsScreenBinding.wasteDumpImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewClearedWasteDumpList.size()>0){
                    carriedOutsScreenBinding.clearWasteDumpRecyclerLayout.setVisibility(View.VISIBLE);
                    carriedOutsScreenBinding.carriedOutDetailsLayout.setVisibility(View.GONE);
                    clearedWasteDumbAdapter = new ClearedWasteDumbAdapter(viewClearedWasteDumpList,AddCarriedOutsScreen.this,"Server",dbData);
                    adapterCameraIntent =carriedOutWastDumpAdapter;
                    carriedOutsScreenBinding.clearWasteDumpRecycler.setAdapter(clearedWasteDumbAdapter);

                }
                else {
                    carriedOutsScreenBinding.clearWasteDumpRecyclerLayout.setVisibility(View.GONE);
                    carriedOutsScreenBinding.carriedOutDetailsLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    public void getIntentData(){
        initialiseRecyclerView();
        swm_infra_details_id = getIntent().getStringExtra("swm_infra_details_id");
        no_of_thooimai_kaavalars_allocated = getIntent().getStringExtra("no_of_thooimai_kaavalars_allocated");
        no_of_thooimai_kaavalars_working = getIntent().getStringExtra("no_of_thooimai_kaavalars_working");
        whether_community_compost_pit_available_in_panchayat = getIntent().getStringExtra("whether_community_compost_pit_available_in_panchayat");
        whether_vermi_compost_pit_available_in_panchayat = getIntent().getStringExtra("whether_vermi_compost_pit_available_in_panchayat");
        any_integrated_nuesery_devlp_near_swm_facility = getIntent().getStringExtra("any_integrated_nuesery_devlp_near_swm_facility");
        is_plastic_connected_to_waste_management_unit = getIntent().getStringExtra("is_plastic_connected_to_waste_management_unit");
        carriedOutsScreenBinding.wasteDumpImg.setVisibility(View.GONE);
        carriedOutsScreenBinding.clearWasteDumpRecyclerLayout.setVisibility(View.GONE);
        carriedOutsScreenBinding.carriedOutDetailsLayout.setVisibility(View.VISIBLE);

        if(is_plastic_connected_to_waste_management_unit.equals("Y")){
            carriedOutsScreenBinding.plasticConnectedLayout.setVisibility(View.VISIBLE);
        }
        else {
            carriedOutsScreenBinding.plasticConnectedLayout.setVisibility(View.GONE);
        }
    }
    private void initialiseRecyclerView() {
        carriedOutsScreenBinding.wasteDumpRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getApplicationContext(),DividerItemDecoration.VERTICAL);
        carriedOutsScreenBinding.wasteDumpRecycler.addItemDecoration(itemDecoration);

        carriedOutsScreenBinding.clearWasteDumpRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        RecyclerView.ItemDecoration itemDecoration1 = new DividerItemDecoration(getApplicationContext(),DividerItemDecoration.VERTICAL);
        carriedOutsScreenBinding.clearWasteDumpRecycler.addItemDecoration(itemDecoration1);
    }

    public void loadCarriedOutDateList(){
        carriedOutDateList = new ArrayList<>();
        RealTimeMonitoringSystem carriedOutItem1 = new RealTimeMonitoringSystem();
        carriedOutItem1.setCarried_out_date(getResources().getString(R.string.select_date));
        carriedOutDateList.add(carriedOutItem1);
        JSONArray jArray = (JSONArray)prefManager.get_carried_out_date_listJson();
        for (int i =0; i<jArray.length();i++){
            RealTimeMonitoringSystem carriedOutItem = new RealTimeMonitoringSystem();
            try {
                carriedOutItem.setCarried_out_date(jArray.getString(i));
                carriedOutDateList.add(carriedOutItem);
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
        if (carriedOutDateList.size()>0){
            carriedOutsScreenBinding.chooseDateSpinner.setAdapter(new CommonAdapter(AddCarriedOutsScreen.this,carriedOutDateList,"carriedOutDateList"));
        }
        else {
            carriedOutsScreenBinding.chooseDateSpinner.setAdapter(null);

        }

    }

    public void getOnlineWasteDumpList(){
        try {
            new ApiService(this).makeJSONObjectRequest("OnlineWasteDump", Api.Method.POST, UrlGenerator.getWorkListUrl(), OnlinegetOnlineWasteDumpListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public JSONObject OnlinegetOnlineWasteDumpListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), OnlinegetOnlineWasteDumpListNormalJsonParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("OnlineWasteDump", "" + dataSet);
        return dataSet;
    }
    public  JSONObject OnlinegetOnlineWasteDumpListNormalJsonParams() throws JSONException {
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, "swm_activity_carried_out_view");
        dataSet.put("swm_infra_details_id", swm_infra_details_id);
        dataSet.put("date_entry_for",choose_date_string);
        Log.d("OnlineWasteDump", "" + dataSet);
        return dataSet;
    }

    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        try {
            String urlType = serverResponse.getApi();
            JSONObject responseObj = serverResponse.getJsonResponse();

            if ("OnlineWasteDump".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new fetchOnlineWasteDumpTask().execute(jsonObject);
                }
                Log.d("OnlineWasteDump", "" + responseDecryptedBlockKey);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnError(VolleyError volleyError) {

    }

    public class fetchOnlineWasteDumpTask extends AsyncTask<JSONObject, Void, ArrayList<RealTimeMonitoringSystem>> {
        String total_quantity_of_waste="";
        String quantity_of_bio_degradable_waste="";
        String total_quantity_of_compost_generated_from_community="";
        String total_quantity_of_compost_generated_from_vermi="";
        String quantity_of_compost_sold = "";
        String total_revenue_generated = "";
        String date_entry_for = "";
        String action_taken_on_waste_dump_seen_in_panchayat = "";

        String amount_of_compostable_waste_sent_for_recycling_in_kg="";
        String amount_of_compostable_waste_sent_for_recycling_revenue_in_rs="";
        String amount_of_plastic_waste_sent_to_pwm_unit_in_kg="";
        String amount_of_plastic_waste_sent_to_pwm_unit_revenue_in_rs="";

        @Override
        protected ArrayList<RealTimeMonitoringSystem> doInBackground(JSONObject... params) {

            ArrayList<RealTimeMonitoringSystem> carriedOutList = new ArrayList<>();
            viewClearedWasteDumpList= new ArrayList<>();
            if (params.length > 0) {
                JSONArray jsonArray = new JSONArray();
                JSONArray jsonArray1 = new JSONArray();
                try {
                    jsonArray1 = params[0].getJSONArray("CARRIED_OUT_DETAILS");
                    jsonArray = params[0].getJSONArray("WASTE_DUMP_PHOTOS");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    if(jsonArray1.length()>0){
                        for (int i=0;i<jsonArray1.length();i++){
                            total_quantity_of_waste= (jsonArray1.getJSONObject(i).getString("tot_qty_of_waste_collected_kg"));
                            quantity_of_bio_degradable_waste= (jsonArray1.getJSONObject(i).getString("qty_of_biodegradable_waste_collected_kg"));
                            total_quantity_of_compost_generated_from_community= (jsonArray1.getJSONObject(i).getString("tot_qty_compost_gen_from_community_compost_pit_kg"));
                            total_quantity_of_compost_generated_from_vermi= (jsonArray1.getJSONObject(i).getString("tot_qty_of_compost_gen_from_vermicomposting_unit_kg"));
                            quantity_of_compost_sold= (jsonArray1.getJSONObject(i).getString("sale_qty_of_compost_kg"));
                            total_revenue_generated= (jsonArray1.getJSONObject(i).getString("sale_revenue_generated_in_rupees"));
                            date_entry_for= (jsonArray1.getJSONObject(i).getString("date_entry_for"));
                            action_taken_on_waste_dump_seen_in_panchayat= (jsonArray1.getJSONObject(i).getString("waste_dump_seen_in_panchayat"));

                            amount_of_compostable_waste_sent_for_recycling_in_kg=(jsonArray1.getJSONObject(i).getString("amount_of_compostable_waste_sent_for_recycling_in_kg"));
                            amount_of_compostable_waste_sent_for_recycling_revenue_in_rs=(jsonArray1.getJSONObject(i).getString("amount_of_compostable_waste_sent_for_recycling_revenue_in_rs"));
                            amount_of_plastic_waste_sent_to_pwm_unit_in_kg=(jsonArray1.getJSONObject(i).getString("amount_of_plastic_waste_sent_to_pwm_unit_in_kg"));
                            amount_of_plastic_waste_sent_to_pwm_unit_revenue_in_rs=(jsonArray1.getJSONObject(i).getString("amount_of_plastic_waste_sent_to_pwm_unit_revenue_in_rs"));

                        }
                    }
                    else {
                        total_quantity_of_waste="";
                        quantity_of_bio_degradable_waste="";
                        total_quantity_of_compost_generated_from_community="";
                        total_quantity_of_compost_generated_from_vermi="";
                        quantity_of_compost_sold = "";
                        total_revenue_generated = "";
                        date_entry_for = "";

                        amount_of_compostable_waste_sent_for_recycling_in_kg="";
                        amount_of_compostable_waste_sent_for_recycling_revenue_in_rs="";
                        amount_of_plastic_waste_sent_to_pwm_unit_in_kg="";
                        amount_of_plastic_waste_sent_to_pwm_unit_revenue_in_rs="";
                    }

                }
                catch (JSONException e){

                }


                for (int i = 0; i < jsonArray.length(); i++) {
                    RealTimeMonitoringSystem carriedOutItems = new RealTimeMonitoringSystem();
                    RealTimeMonitoringSystem carriedOutItems1 = new RealTimeMonitoringSystem();
                    try {
                        carriedOutItems.setDistictCode(jsonArray.getJSONObject(i).getString("dcode"));
                        carriedOutItems.setBlockCode(jsonArray.getJSONObject(i).getString("bcode"));
                        carriedOutItems.setPvCode(jsonArray.getJSONObject(i).getString("pvcode"));
                        carriedOutItems.setSwm_infra_details_id(jsonArray.getJSONObject(i).getString("swm_infra_details_id"));
                        carriedOutItems.setSwm_waste_dump_photos_id(jsonArray.getJSONObject(i).getString("swm_waste_dump_photos_id"));
                        carriedOutItems.setIs_there_any_waste_dump(jsonArray.getJSONObject(i).getString("is_photo_of_waste_dump"));
                        carriedOutItems.setIs_photo_of_waste_dump_after_action(jsonArray.getJSONObject(i).getString("is_photo_of_waste_dump_after_action"));
                        if(jsonArray.getJSONObject(i).getString("is_photo_of_waste_dump").equals("Y")){
                            byte[] decodedString = Base64.decode(jsonArray.getJSONObject(i).getString("before_taken_image"), Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            carriedOutItems.setBefore_taken_image(decodedByte);
                        }
                        /*if(jsonArray.getJSONObject(i).getString("is_photo_of_waste_dump_after_action").equals("Y")){
                            byte[] decodedString = Base64.decode(jsonArray.getJSONObject(i).getString("after_taken_image"), Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            carriedOutItems.setAfter_taken_image(decodedByte);
                        }*/
                        carriedOutItems.setAfter_taken_image_lat("");
                        carriedOutItems.setAfter_taken_image_long("");

                        carriedOutList.add(carriedOutItems);

                        ///This for Cleared WasteDUmpList ***///
                        carriedOutItems1.setDistictCode(jsonArray.getJSONObject(i).getString("dcode"));
                        carriedOutItems1.setBlockCode(jsonArray.getJSONObject(i).getString("bcode"));
                        carriedOutItems1.setPvCode(jsonArray.getJSONObject(i).getString("pvcode"));
                        carriedOutItems1.setSwm_infra_details_id(jsonArray.getJSONObject(i).getString("swm_infra_details_id"));
                        carriedOutItems1.setSwm_waste_dump_photos_id(jsonArray.getJSONObject(i).getString("swm_waste_dump_photos_id"));
                        carriedOutItems1.setIs_there_any_waste_dump(jsonArray.getJSONObject(i).getString("is_photo_of_waste_dump"));
                        carriedOutItems1.setIs_photo_of_waste_dump_after_action(jsonArray.getJSONObject(i).getString("is_photo_of_waste_dump_after_action"));
                        if(jsonArray.getJSONObject(i).getString("is_photo_of_waste_dump").equals("Y")){
                            byte[] decodedString = Base64.decode(jsonArray.getJSONObject(i).getString("before_taken_image"), Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            carriedOutItems1.setBefore_taken_image(decodedByte);
                        }
                        if(jsonArray.getJSONObject(i).getString("is_photo_of_waste_dump_after_action").equals("Y")){
                            byte[] decodedString = Base64.decode(jsonArray.getJSONObject(i).getString("after_taken_image"), Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            carriedOutItems1.setAfter_taken_image(decodedByte);
                        }
                        carriedOutItems1.setAfter_taken_image_lat("");
                        carriedOutItems1.setAfter_taken_image_long("");
                        /////*******///////////
                        viewClearedWasteDumpList.add(carriedOutItems1);

                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }
            }

            return carriedOutList;
        }

        @Override
        protected void onPostExecute(ArrayList<RealTimeMonitoringSystem> carriedOutList) {
            super.onPostExecute(carriedOutList);
            if(carriedOutList.size()>0){
                carriedOutListDetails = new ArrayList<>();
                carriedOutListDetails.addAll(carriedOutList);
                for(int i= carriedOutListDetails.size()-1;i >= 0;i--){
                    if(carriedOutListDetails.get(i).getIs_photo_of_waste_dump_after_action().equals("Y")){
                        carriedOutListDetails.remove(i);
                    }
                }
                if(carriedOutListDetails.size()>0){
                    carriedOutWastDumpAdapter = new CarriedOutWastDumpAdapter(carriedOutListDetails,AddCarriedOutsScreen.this,"Server",dbData);
                    adapterCameraIntent =carriedOutWastDumpAdapter;
                    carriedOutsScreenBinding.wasteDumpRecycler.setAdapter(carriedOutWastDumpAdapter);
                    carriedOutsScreenBinding.wasteDumpRecycler.setVisibility(View.VISIBLE);
                }
                else {
                    carriedOutsScreenBinding.wasteDumpRecycler.setVisibility(View.GONE);
                }


            }
            else {
                carriedOutsScreenBinding.wasteDumpRecycler.setVisibility(View.GONE);
            }
            /*try {
                carriedOutsScreenBinding.chooseDateSpinner.setSelection(getSpinnerIndex(carriedOutsScreenBinding.chooseDateSpinner,date_entry_for));
            }
            catch (Exception e){

            }*/
            carriedOutsScreenBinding.totalQuantityOfWaste.setText(total_quantity_of_waste);
            carriedOutsScreenBinding.quantityOfBioDegradableWaste.setText(quantity_of_bio_degradable_waste);
            carriedOutsScreenBinding.totalQuantityOfCompostGeneratedFromCommunity.setText(total_quantity_of_compost_generated_from_community);
            carriedOutsScreenBinding.totalQuantityOfCompostGeneratedFromVermi.setText(total_quantity_of_compost_generated_from_vermi);
            carriedOutsScreenBinding.quantityOfCompostSold.setText(quantity_of_compost_sold);
            carriedOutsScreenBinding.totalRevenueGenerated.setText(total_revenue_generated);

            carriedOutsScreenBinding.quantityOfCompostableWasteRecycle.setText(amount_of_compostable_waste_sent_for_recycling_in_kg);
            carriedOutsScreenBinding.totalRecyclePlasticWasteRevenueGenerated.setText(amount_of_compostable_waste_sent_for_recycling_revenue_in_rs);
            carriedOutsScreenBinding.totalPlasticWaste.setText(amount_of_plastic_waste_sent_to_pwm_unit_in_kg);
            carriedOutsScreenBinding.totalPlasticWasteRevenueGenerated.setText(amount_of_plastic_waste_sent_to_pwm_unit_revenue_in_rs);


            if(viewClearedWasteDumpList.size()>0){
                carriedOutsScreenBinding.wasteDumpImg.setVisibility(View.VISIBLE);
            }
            else {
                carriedOutsScreenBinding.wasteDumpImg.setVisibility(View.GONE);
            }
        }
    }

    private int getSpinnerIndex(Spinner spinner, String myString){

        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
    }
    private void captureImage() {
        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {

            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);

        }
        else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            File file = CameraUtils.getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (file != null) {
                imageStoragePath = file.getAbsolutePath();
            }

            Uri fileUri = CameraUtils.getOutputMediaFileUri(this, file);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

            // start the image capture Intent
            startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
        }
        if (MyLocationListener.latitude > 0) {
            offlatTextValue = MyLocationListener.latitude;
            offlongTextValue = MyLocationListener.longitude;
        }
    }

    public void getLatLong(int pos) {
        clicked_position = pos;
        mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new MyLocationListener();
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAltitudeRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        criteria.setBearingRequired(false);

        //API level 9 and up
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
        Integer gpsFreqInMillis = 1000;
        Integer gpsFreqInDistance = 1;


        // permission was granted, yay! Do the
        // location-related task you need to do.
        if (ContextCompat.checkSelfPermission(AddCarriedOutsScreen.this,
                ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            //Request location updates:
            //mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
            mlocManager.requestLocationUpdates(gpsFreqInMillis, gpsFreqInDistance, criteria, mlocListener, null);

        }

        if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(AddCarriedOutsScreen.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AddCarriedOutsScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    requestPermissions(new String[]{CAMERA, ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            } else {
                if (ActivityCompat.checkSelfPermission(AddCarriedOutsScreen.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AddCarriedOutsScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AddCarriedOutsScreen.this, new String[]{ACCESS_FINE_LOCATION}, 1);

                }
            }
            if (MyLocationListener.latitude > 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (CameraUtils.checkPermissions(AddCarriedOutsScreen.this)) {
                        captureImage();
                    } else {
                        requestCameraPermission(MEDIA_TYPE_IMAGE);
                    }
//                            checkPermissionForCamera();
                } else {
                    captureImage();
                }
            } else {
                Utils.showAlert(AddCarriedOutsScreen.this, getResources().getString(R.string.satellite));
            }
        } else {
            Utils.showAlert(AddCarriedOutsScreen.this, getResources().getString(R.string.gps_is_not_turned_on));
        }
    }

    private void requestCameraPermission(final int type) {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                            if (type == MEDIA_TYPE_IMAGE) {
                                // capture picture
                                captureImage();
                            } else {
//                                captureVideo();
                            }

                        } else if (report.isAnyPermissionPermanentlyDenied()) {
                            showPermissionsAlert();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }


    private void showPermissionsAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.permission_required))
                .setMessage(getResources().getString(R.string.camera_need_permission))
                .setPositiveButton(getResources().getString(R.string.goto_settings), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        CameraUtils.openSettings(AddCarriedOutsScreen.this);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    public void previewCapturedImage() {
        try {
            // hide video preview
            Bitmap bitmap = CameraUtils.optimizeBitmap(BITMAP_SAMPLE_SIZE, imageStoragePath);
            ExifInterface ei = null;
            try {
                ei = new ExifInterface(imageStoragePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            Bitmap rotatedBitmap = null;
            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;
            }

        } catch (NullPointerException e) {
            e.printStackTrace();

        }
    }
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(adapterCameraIntent!=null){
                        adapterCameraIntent.OnIntentListener(data,resultCode,clicked_position,String.valueOf(offlatTextValue),String.valueOf(offlongTextValue));
                    }

                }
                else {
                    // Refreshing the gallery
                    CameraUtils.refreshGallery(getApplicationContext(), imageStoragePath);

                    // successfully captured the image
                    // display it in image view
                    previewCapturedImage();
                }
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.user_canceled_image_capture), Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.sorry_failed_to_capture), Toast.LENGTH_SHORT)
                        .show();
            }
        }
        else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Refreshing the gallery
                CameraUtils.refreshGallery(getApplicationContext(), imageStoragePath);

                // video successfully recorded
                // preview the recorded video
//                previewVideo();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.user_canceled_video), Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.sorry_failed_capture_video), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    public void checkValidation(){
        total_quantity_of_waste = carriedOutsScreenBinding.totalQuantityOfWaste.getText().toString();
        quantity_of_bio_degradable_waste = carriedOutsScreenBinding.quantityOfBioDegradableWaste.getText().toString();
        total_quantity_of_compost_generated_from_community = carriedOutsScreenBinding.totalQuantityOfCompostGeneratedFromCommunity.getText().toString();
        total_quantity_of_compost_generated_from_vermi = carriedOutsScreenBinding.totalQuantityOfCompostGeneratedFromVermi.getText().toString();
        quantity_of_compost_sold = carriedOutsScreenBinding.quantityOfCompostSold.getText().toString();
        total_revenue_generated = carriedOutsScreenBinding.totalRevenueGenerated.getText().toString();

        quantity_of_compostable_waste_recycle=carriedOutsScreenBinding.quantityOfCompostableWasteRecycle.getText().toString();
        total_recycle_plastic_waste_revenue_generated=carriedOutsScreenBinding.totalRecyclePlasticWasteRevenueGenerated.getText().toString();
        total_plastic_waste=carriedOutsScreenBinding.totalPlasticWaste.getText().toString();
        total_plastic_waste_revenue_generated=carriedOutsScreenBinding.totalPlasticWasteRevenueGenerated.getText().toString();


        if(!choose_date_string.equals("")){
            if(!total_quantity_of_waste.equals("")){
                if(!quantity_of_bio_degradable_waste.equals("")){
                    if(!total_quantity_of_compost_generated_from_community.equals("")){
                        if (!total_quantity_of_compost_generated_from_vermi.equals("")){
                            if(!quantity_of_compost_sold.equals("")){
                                if(!total_revenue_generated.equals("")){
                                    if (is_plastic_connected_to_waste_management_unit.equals("Y")){
                                        if(!quantity_of_compostable_waste_recycle.equals("")){
                                            if(!total_recycle_plastic_waste_revenue_generated.equals("")){
                                                if(!total_plastic_waste.equals("")){
                                                    if(!total_plastic_waste_revenue_generated.equals("")){
                                                        adapterItemValuesCheck();
                                                    }
                                                    else {
                                                        Utils.showAlert(AddCarriedOutsScreen.this,getResources().getString(R.string.revenue_rs));
                                                    }
                                                }
                                                else {
                                                    Utils.showAlert(AddCarriedOutsScreen.this,getResources().getString(R.string.plastic_waste_unit));
                                                }
                                            }
                                            else {
                                                Utils.showAlert(AddCarriedOutsScreen.this,getResources().getString(R.string.revenue_rs));
                                            }
                                        }
                                        else {
                                            Utils.showAlert(AddCarriedOutsScreen.this,getResources().getString(R.string.compostable_waste_recycle_text));
                                        }
                                    }
                                    else {
                                        adapterItemValuesCheck();
                                    }

                                }
                                else {
                                    Utils.showAlert(AddCarriedOutsScreen.this,getResources().getString(R.string.total_revenue_generated_in_lakhs));

                                }

                            }
                            else {
                                Utils.showAlert(AddCarriedOutsScreen.this,getResources().getString(R.string.total_quantity_of_compost_sold_in_kg));

                            }

                        }
                        else {
                            Utils.showAlert(AddCarriedOutsScreen.this,getResources().getString(R.string.total_quantity_of_compost_generated_from_the_vermi_composting_compost_pit_in_kg));
                        }
                    }
                    else {
                        Utils.showAlert(AddCarriedOutsScreen.this,getResources().getString(R.string.total_quantity_of_compost_generated_from_the_community_compost_pit_in_kg));
                    }
                }
                else {
                    Utils.showAlert(AddCarriedOutsScreen.this,getResources().getString(R.string.total_quantity_of_bio_degradable_waste_collected_in_kg));
                }
            }
            else {
                Utils.showAlert(AddCarriedOutsScreen.this,getResources().getString(R.string.total_quantity_of_waste_collected_in_the_panchayat_in_kg));
            }
        }
        else {
            Utils.showAlert(AddCarriedOutsScreen.this,getResources().getString(R.string.select_date));
        }
    }

    public void adapterItemValuesCheck(){
        dbData.open();
        ArrayList <RealTimeMonitoringSystem> carriedOutAdapterList = new ArrayList<>();
        boolean all_is_correct= false;
        if(carriedOutListDetails.size()>0){
            carriedOutAdapterList = carriedOutWastDumpAdapter.getTheCarriedList();
            for (int j=0;j<carriedOutAdapterList.size();j++){
                if(!carriedOutAdapterList.get(j).getIs_photo_of_waste_dump_after_action().equals("")) {
                    if (carriedOutAdapterList.get(j).getIs_photo_of_waste_dump_after_action().equals("Y")) {
                        if (carriedOutAdapterList.get(j).getAfter_taken_image() != null) {
                            all_is_correct = true;
                        } else {
                            all_is_correct = false;
                            break;
                        }
                    }
                    else {
                        all_is_correct = true;
                    }
                }
                else {
                    all_is_correct = false;
                    break;
                }
            }
            if(all_is_correct){
                long insert_updated_id =0;
                int count = 0;
                long rowInserted = 0;
                try {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("swm_infra_details_id",swm_infra_details_id);
                    contentValues.put("dcode",prefManager.getDistrictCode());
                    contentValues.put("bcode",prefManager.getBlockCode());
                    contentValues.put("pvcode",prefManager.getPvCode());
                    contentValues.put("date_entry_for",choose_date_string);
                    contentValues.put("total_quantity_of_waste",total_quantity_of_waste);
                    contentValues.put("quantity_of_bio_degradable_waste",quantity_of_bio_degradable_waste);
                    contentValues.put("total_quantity_of_compost_generated_from_community",total_quantity_of_compost_generated_from_community);
                    contentValues.put("total_quantity_of_compost_generated_from_vermi",total_quantity_of_compost_generated_from_vermi);
                    contentValues.put("quantity_of_compost_sold",quantity_of_compost_sold);
                    contentValues.put("total_revenue_generated",total_revenue_generated);

                    contentValues.put("amount_of_compostable_waste_sent_for_recycling_in_kg",quantity_of_compostable_waste_recycle);
                    contentValues.put("amount_of_compostable_waste_sent_for_recycling_revenue_in_rs",total_recycle_plastic_waste_revenue_generated);
                    contentValues.put("amount_of_plastic_waste_sent_to_pwm_unit_in_kg",total_plastic_waste);
                    contentValues.put("amount_of_plastic_waste_sent_to_pwm_unit_revenue_in_rs",total_plastic_waste_revenue_generated);

                    if(dbData.gettableCountCarriedOutTable("",swm_infra_details_id,choose_date_string)>0){
                        insert_updated_id = db.update(DBHelper.SWM_CARRIED_OUT_DETAILS,contentValues,null,null);
                        if (insert_updated_id>0){
                            Toasty.success(AddCarriedOutsScreen.this,getResources().getString(R.string.updated_success),Toasty.LENGTH_SHORT);
                        }

                    }
                    else {
                        insert_updated_id = db.insert(DBHelper.SWM_CARRIED_OUT_DETAILS,null,contentValues);
                        if (insert_updated_id>0){
                            Toasty.success(AddCarriedOutsScreen.this,getResources().getString(R.string.inserted_success),Toasty.LENGTH_SHORT);

                        }
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                if(dbData.getParticularCarriedOutPhotosList((int) insert_updated_id,prefManager.getPvCode(),swm_infra_details_id,choose_date_string).size()>0){
                    long delete_id = db.delete(DBHelper.SWM_CARRIED_OUT_PHOTOS_DETAILS,null,null);
                }
                for(int j=0;j<carriedOutAdapterList.size();j++){
                    byte[] imageInByte = new byte[0];
                    count = count+1;
                    ContentValues values =  new ContentValues();
                    try {
                        values.put("carried_out_details_id",insert_updated_id);
                        values.put("swm_infra_details_id",carriedOutAdapterList.get(j).getSwm_infra_details_id());
                        values.put("swm_waste_dump_photos_id",carriedOutAdapterList.get(j).getSwm_waste_dump_photos_id());
                        values.put("dcode",prefManager.getDistrictCode());
                        values.put("bcode",prefManager.getBlockCode());
                        values.put("pvcode",prefManager.getPvCode());
                        values.put("date_entry_for",choose_date_string);
                        values.put("is_photo_of_waste_dump_after_action",carriedOutAdapterList.get(j).getIs_photo_of_waste_dump_after_action());
                        if(carriedOutAdapterList.get(j).getIs_photo_of_waste_dump_after_action().equals("Y")){
                            Bitmap bitmap = carriedOutAdapterList.get(j).getAfter_taken_image();
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            imageInByte = baos.toByteArray();
                            values.put("after_taken_image_lat",carriedOutAdapterList.get(j).getAfter_taken_image_lat());
                            values.put("after_taken_image_long",carriedOutAdapterList.get(j).getAfter_taken_image_long());
                            values.put("after_taken_image",imageInByte);
                        }

                            rowInserted = db.insert(DBHelper.SWM_CARRIED_OUT_PHOTOS_DETAILS, null, values);
                            if (count == carriedOutAdapterList.size()) {
                                if (rowInserted > 0) {
                                    toast_flag = "Inserted";
                                    Toasty.success(AddCarriedOutsScreen.this, getResources().getString(R.string.inserted_success), Toasty.LENGTH_SHORT);
                                    onBackPressed();
                                }
                            }

                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            else {
                //Utils.showAlert(AddCarriedOutsScreen.this,"Please Choose This ");
                Utils.showAlert(AddCarriedOutsScreen.this,getResources().getString(R.string.please_enter_all_the_details));
            }
        }
        else {

            long insert_updated_id =0;
            try {
                ContentValues contentValues = new ContentValues();
                contentValues.put("swm_infra_details_id",swm_infra_details_id);
                contentValues.put("dcode",prefManager.getDistrictCode());
                contentValues.put("bcode",prefManager.getBlockCode());
                contentValues.put("pvcode",prefManager.getPvCode());
                contentValues.put("date_entry_for",choose_date_string);
                contentValues.put("total_quantity_of_waste",total_quantity_of_waste);
                contentValues.put("quantity_of_bio_degradable_waste",quantity_of_bio_degradable_waste);
                contentValues.put("total_quantity_of_compost_generated_from_community",total_quantity_of_compost_generated_from_community);
                contentValues.put("total_quantity_of_compost_generated_from_vermi",total_quantity_of_compost_generated_from_vermi);
                contentValues.put("quantity_of_compost_sold",quantity_of_compost_sold);
                contentValues.put("total_revenue_generated",total_revenue_generated);

                contentValues.put("amount_of_compostable_waste_sent_for_recycling_in_kg",quantity_of_compostable_waste_recycle);
                contentValues.put("amount_of_compostable_waste_sent_for_recycling_revenue_in_rs",total_recycle_plastic_waste_revenue_generated);
                contentValues.put("amount_of_plastic_waste_sent_to_pwm_unit_in_kg",total_plastic_waste);
                contentValues.put("amount_of_plastic_waste_sent_to_pwm_unit_revenue_in_rs",total_plastic_waste_revenue_generated);

                if(dbData.gettableCountCarriedOutTable("",swm_infra_details_id,choose_date_string)>0){
                    insert_updated_id = db.update(DBHelper.SWM_CARRIED_OUT_DETAILS,contentValues,null,null);
                    if (insert_updated_id>0){
                        toast_flag = "Updated";
                        Toasty.success(AddCarriedOutsScreen.this,getResources().getString(R.string.updated_success),Toasty.LENGTH_SHORT);
                        onBackPressed();
                    }

                }
                else {
                    insert_updated_id = db.insert(DBHelper.SWM_CARRIED_OUT_DETAILS,null,contentValues);
                    if (insert_updated_id>0){
                        toast_flag = "Inserted";
                        Toasty.success(AddCarriedOutsScreen.this,getResources().getString(R.string.inserted_success),Toasty.LENGTH_SHORT);
                        onBackPressed();
                    }
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }


    }

    @Override
    public void onBackPressed() {
        if(carriedOutsScreenBinding.clearWasteDumpRecyclerLayout.getVisibility()==View.VISIBLE){
            carriedOutsScreenBinding.clearWasteDumpRecyclerLayout.setVisibility(View.GONE);
            carriedOutsScreenBinding.carriedOutDetailsLayout.setVisibility(View.VISIBLE);
        }
        else {
            if(toast_flag.equals("Inserted")){
                Toasty.success(AddCarriedOutsScreen.this,getResources().getString(R.string.inserted_success),Toasty.LENGTH_SHORT);
            }
            else if(toast_flag.equals("Updated")) {
                Toasty.success(AddCarriedOutsScreen.this,getResources().getString(R.string.updated_success),Toasty.LENGTH_SHORT);
            }
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
        }
    }
}

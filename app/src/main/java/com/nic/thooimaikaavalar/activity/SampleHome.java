package com.nic.thooimaikaavalar.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;

import com.android.volley.VolleyError;
import com.nic.thooimaikaavalar.R;
import com.nic.thooimaikaavalar.api.Api;
import com.nic.thooimaikaavalar.api.ApiService;
import com.nic.thooimaikaavalar.api.ServerResponse;
import com.nic.thooimaikaavalar.constant.AppConstant;
import com.nic.thooimaikaavalar.dataBase.dbData;
import com.nic.thooimaikaavalar.databinding.SampleHomeScreenBinding;
import com.nic.thooimaikaavalar.dialog.MyDialog;
import com.nic.thooimaikaavalar.model.RealTimeMonitoringSystem;
import com.nic.thooimaikaavalar.session.PrefManager;
import com.nic.thooimaikaavalar.support.ProgressHUD;
import com.nic.thooimaikaavalar.utils.UrlGenerator;
import com.nic.thooimaikaavalar.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;

public class SampleHome extends AppCompatActivity implements Api.ServerResponseListener, View.OnClickListener, MyDialog.myOnClickListener {
    private SampleHomeScreenBinding homeScreenBinding;
    private PrefManager prefManager;
    public com.nic.thooimaikaavalar.dataBase.dbData dbData = new dbData(this);
    private String isHome;
    JSONObject dataset = new JSONObject();
    private ProgressHUD progressHUD;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeScreenBinding = DataBindingUtil.setContentView(this, R.layout.sample_home_screen);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        prefManager = new PrefManager(this);
        Utils.setLocale("ta",this);
        dbData.open();
        homeScreenBinding.tvName.setText(prefManager.getDesignation());
        homeScreenBinding.designation.setText(prefManager.getDesignation());
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            isHome = bundle.getString("Home");
        }
        if (Utils.isOnline() && !isHome.equalsIgnoreCase("Home")) {
            fetchAllResponseFromApi();
        }
        syncButtonVisibility();

        showMenuLayout();
        initialUINew();
        timerConcept();
    }

    public void timerConcept(){
        try {
            CountDownTimer newtimer = new CountDownTimer(1000000000, 1000) {

                public void onTick(long millisUntilFinished) {
                    long date =System.currentTimeMillis();
                    SimpleDateFormat datetimeformat =new SimpleDateFormat("E dd-MM-yyyy\n hh:mm:ss a");
                    try {
                        String datetimetext =datetimeformat.format(date);
                        homeScreenBinding.timer.setText(datetimetext);
                    }
                    catch (Exception e){

                    }
                }
                public void onFinish() {

                }
            };
            newtimer.start();
        }
        catch (Exception e){

        }



    }

    public void fetchAllResponseFromApi() {
        ///////////// ************** Now Not Need ********//////
        //getSchemeList();
        /*//        getDistrictList();
//        getBlockList();
        getStageList();
        getAdditionalWorkStageList();
        getFinYearList();*/

        /////////////////**************** //////
        getVillageList();
        getHabList();
        get_swm_capacity_of_mcc_in_tones();
        get_swm_no_of_thooimai_kaavalars();
        get_swm_photographs_of_mcc_components();
        get_swm_water_supply_availability();
        get_swm_asset_type();
        get_no_of_waste_dump_photos();
        get_carried_out_date_list();

    }

/*
    public void syncButtonVisibility() {
        dbData.open();
        ArrayList<RealTimeMonitoringSystem> workImageCount = dbData.getSavedWorkImage("","","","","");

        if (workImageCount.size() > 0) {
            homeScreenBinding.sync.setVisibility(View.VISIBLE);
            homeScreenBinding.pendingCount.setText(String.valueOf(workImageCount.size()));
        }else {
            homeScreenBinding.sync.setVisibility(View.GONE);
            homeScreenBinding.pendingCount.setText("NIL");
        }
    }
*/

    public void syncButtonVisibility() {
        dbData.open();
        ArrayList<RealTimeMonitoringSystem> getAllBasicDetails =  dbData.getAllBasicDetails();
        ArrayList<RealTimeMonitoringSystem> getTooimaiKaavalrCount =  dbData.getAllThooimaikaavalarListLocalAll();
        ArrayList<RealTimeMonitoringSystem> getComponentImageCount =  dbData.getAllComponentsPendingScreen();
        ArrayList<RealTimeMonitoringSystem> getWasteCollectedDetailsCount =  dbData.getParticularWasteCollectedSaveTableRow("","All");
        ArrayList<RealTimeMonitoringSystem> getSwmMasterDetailsCount =  dbData.getAllSWMMasterDetails();
        int gettableCountAssetDetailsTable =  dbData.gettableCountAssetDetailsTable();
        int gettableCountWasteDumpTable =  dbData.gettableCountWasteDumpTable();
        int gettableCountCarriedOutTable =  dbData.gettableCountCarriedOutTable("All","","");


        if(getAllBasicDetails.size()>0||getTooimaiKaavalrCount.size()>0||getComponentImageCount.size()>0|| getWasteCollectedDetailsCount.size()>0
                || getSwmMasterDetailsCount.size()>0|| gettableCountAssetDetailsTable > 0||gettableCountWasteDumpTable>0||gettableCountCarriedOutTable>0){
            homeScreenBinding.sync.setVisibility(View.VISIBLE);
            homeScreenBinding.syncCountLayout.setVisibility(View.VISIBLE);

            homeScreenBinding.sync1.setVisibility(View.VISIBLE);
            /*homeScreenBinding.marqueeTextLayout.setVisibility(View.VISIBLE);
            homeScreenBinding.marqueeText.setText("Data's Stored in Local...");
            homeScreenBinding.marqueeText.setSelected(true);*/


            try {
                int count = getAllBasicDetails.size()+getTooimaiKaavalrCount.size()+getComponentImageCount.size()+getWasteCollectedDetailsCount.size()+
                        getSwmMasterDetailsCount.size()+gettableCountAssetDetailsTable;
                homeScreenBinding.pendingCount.setText("true");
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }else {
            homeScreenBinding.sync.setVisibility(View.GONE);
            homeScreenBinding.sync1.setVisibility(View.GONE);
            homeScreenBinding.syncCountLayout.setVisibility(View.GONE);
            //homeScreenBinding.marqueeTextLayout.setVisibility(View.GONE);

            homeScreenBinding.pendingCount.setText("NIL");

        }


    }
    public void openPendingScreen() {
        homeScreenBinding.drawerLayout.closeDrawer(Gravity.LEFT);
        Intent intent = new Intent(this, NewPendingScreenActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    public void showMenuLayout(){
        homeScreenBinding.tvName1.setText(prefManager.getDesignation());
        homeScreenBinding.tvName1.setTextColor(getResources().getColor(R.color.white));
        homeScreenBinding.designation1.setText(prefManager.getBlockName()+" : "+prefManager.getDesignation());
        homeScreenBinding.designation1.setTextColor(getResources().getColor(R.color.white));
        getTimeMange();
    }


    public void getVillageList() {
        try {
            new ApiService(this).makeJSONObjectRequest("VillageList", Api.Method.POST, UrlGenerator.getServicesListUrl(), villageListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void getHabList() {
        try {
            new ApiService(this).makeJSONObjectRequest("HabitationList", Api.Method.POST, UrlGenerator.getServicesListUrl(), habitationListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void get_swm_capacity_of_mcc_in_tones() {
        try {
            new ApiService(this).makeJSONObjectRequest("swm_capacity_of_mcc_in_tones", Api.Method.POST, UrlGenerator.getWorkListUrl(), swm_capacity_of_mcc_in_tonesParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void get_swm_no_of_thooimai_kaavalars() {
        try {
            new ApiService(this).makeJSONObjectRequest("swm_no_of_thooimai_kaavalars", Api.Method.POST, UrlGenerator.getWorkListUrl(), swm_no_of_thooimai_kaavalarsParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void get_swm_photographs_of_mcc_components() {
        try {
            new ApiService(this).makeJSONObjectRequest("swm_photographs_of_mcc_components", Api.Method.POST, UrlGenerator.getWorkListUrl(), swm_photographs_of_mcc_componentsParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void get_swm_water_supply_availability() {
        try {
            new ApiService(this).makeJSONObjectRequest("swm_water_supply_availability", Api.Method.POST, UrlGenerator.getWorkListUrl(), swm_water_supply_availabilityParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void get_swm_asset_type() {
        try {
            new ApiService(this).makeJSONObjectRequest("swm_asset_type", Api.Method.POST, UrlGenerator.getWorkListUrl(), swm_asset_typeParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void get_no_of_waste_dump_photos() {
        try {
            new ApiService(this).makeJSONObjectRequest("no_of_waste_dump_photos", Api.Method.POST, UrlGenerator.getWorkListUrl(), no_of_waste_dump_photosParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void get_carried_out_date_list() {
        try {
            new ApiService(this).makeJSONObjectRequest("carried_out_date_listParams", Api.Method.POST, UrlGenerator.getWorkListUrl(), carried_out_date_listParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject villageListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.villageListDistrictBlockWiseJsonParams(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("villageListDistrictWise", "" + authKey);
        return dataSet;
    }
    public JSONObject habitationListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.HabitationListDistrictBlockVillageWiseJsonParams(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("HabitationList", "" + authKey);
        return dataSet;
    }
    public JSONObject swm_capacity_of_mcc_in_tonesParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.swm_capacity_of_mcc_in_tonesParamsJsonParams(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("swm_capacity_of", "" + dataSet);
        return dataSet;
    }
    public JSONObject swm_no_of_thooimai_kaavalarsParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.swm_no_of_thooimai_kaavalarsParamsJsonParams(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("swm_no_of_thooimai", "" + dataSet);
        return dataSet;
    }
    public JSONObject swm_photographs_of_mcc_componentsParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.swm_photographs_of_mcc_componentsJsonParams(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("swm_photographs_o", "" + dataSet);
        return dataSet;
    }
    public JSONObject swm_water_supply_availabilityParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.swm_water_supply_availabilityParamsJsonParams(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("swm_water_supply", "" + dataSet);
        return dataSet;
    }
    public JSONObject swm_asset_typeParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.swm_asset_typeJsonParams(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("swm_asset_type", "" + dataSet);
        return dataSet;
    }
    public JSONObject no_of_waste_dump_photosParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.no_of_waste_dump_photosJsonParams(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("no_of_waste_dump_photos", "" + dataSet);
        return dataSet;
    }
    public JSONObject carried_out_date_listParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.carried_out_date_listParams(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("carried_out_date_listParams", "" + dataSet);
        return dataSet;
    }

    ////////////***************** Now Not Need
    public void getDistrictList() {
        try {
            new ApiService(this).makeJSONObjectRequest("DistrictList", Api.Method.POST, UrlGenerator.getServicesListUrl(), districtListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void getBlockList() {
        try {
            new ApiService(this).makeJSONObjectRequest("BlockList", Api.Method.POST, UrlGenerator.getServicesListUrl(), blockListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void getFinYearList() {
        try {
            new ApiService(this).makeJSONObjectRequest("FinYearList", Api.Method.POST, UrlGenerator.getServicesListUrl(), finyearListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void getStageList() {
        try {
            new ApiService(this).makeJSONObjectRequest("StageList", Api.Method.POST, UrlGenerator.getServicesListUrl(), stageListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void getAdditionalWorkStageList() {
        try {
            new ApiService(this).makeJSONObjectRequest("AdditionalWorkStageList", Api.Method.POST, UrlGenerator.getServicesListUrl(), additionalstageListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void getSchemeList() {
        try {
            new ApiService(this).makeJSONObjectRequest("SchemeList", Api.Method.POST, UrlGenerator.getWorkListUrl(), schemeListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject districtListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.districtListJsonParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("districtList", "" + authKey);
        return dataSet;
    }
    public JSONObject blockListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.blockListDistrictWiseJsonParams(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("blockListDistrictWise", "" + authKey);
        return dataSet;
    }
    public JSONObject finyearListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.schemeFinyearListJsonParams(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("finYearList", "" + authKey);
        return dataSet;
    }
    public JSONObject stageListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.stageListJsonParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("StageList", "" + authKey);
        return dataSet;
    }
    public JSONObject additionalstageListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.additionalstageListJsonParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("AdditionalStageList", "" + authKey);
        return dataSet;
    }
    public JSONObject schemeListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.schemeListBlockWiseJsonParams(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("schemeList", "" + dataSet);
        return dataSet;
    }

    /////////////***************** //////////



    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        try {
            String urlType = serverResponse.getApi();
            JSONObject responseObj = serverResponse.getJsonResponse();

            /////////////// ********** Now Not Need/////////////
            if ("BlockList".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new InsertBlockTask().execute(jsonObject);
                }
                Log.d("BlockList", "" + responseDecryptedBlockKey);
            }
            if ("DistrictList".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new InsertDistrictTask().execute(jsonObject);
                }
                Log.d("DistrictList", "" + responseDecryptedBlockKey);
            }
            if ("SchemeList".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedSchemeKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedSchemeKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new InsertSchemeTask().execute(jsonObject);
                }
                Log.d("SchemeList", "" + responseDecryptedSchemeKey);
            }
            if ("FinYearList".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedSchemeKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedSchemeKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new InsertFinYearTask().execute(jsonObject);
                }
                Log.d("FinYear", "" + responseDecryptedSchemeKey);
            }
            if ("StageList".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedKey);
                Log.d("StageList", "" + responseDecryptedKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new InsertStageTask().execute(jsonObject);
                }

            }
            if ("AdditionalWorkStageList".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new InsertAdditionalStageTask().execute(jsonObject);
                }
                Log.d("AdditionalWorkStageList", "" + responseDecryptedKey);
            }

            ///////////// *********************///////////////

            if ("VillageList".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new InsertVillageTask().execute(jsonObject);
                }
                Log.d("VillageList", "" + responseDecryptedBlockKey);
            }
            if ("HabitationList".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new InsertHabTask().execute(jsonObject);
                }
                Log.d("HabitationList", "" + responseDecryptedBlockKey);
            }

            if ("swm_capacity_of_mcc_in_tones".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new Insertswm_capacity_of_mcc_in_tonesTask().execute(jsonObject);
                }
                Log.d("swm_capacity_of_mcc_in_tones", "" + responseDecryptedBlockKey);
            }
            if ("swm_no_of_thooimai_kaavalars".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new Insertswm_no_of_thooimai_kaavalarsTask().execute(jsonObject);
                }
                Log.d("swm_no_of_thooimai_kaavalars", "" + responseDecryptedBlockKey);
            }
            if ("swm_photographs_of_mcc_components".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new Insertswm_photographs_of_mcc_componentsTask().execute(jsonObject);
                }
                Log.d("swm_photographs_of_mcc_components", "" + responseDecryptedBlockKey);
            }
            if ("swm_water_supply_availability".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new Insertswm_water_supply_availabilityTask().execute(jsonObject);
                }
                Log.d("swm_water_supply_availability", "" + responseDecryptedBlockKey);
            }
            if ("swm_asset_type".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new Insertswm_asset_type().execute(jsonObject);
                }
                Log.d("swm_asset_type", "" + responseDecryptedBlockKey);
            }
            if ("no_of_waste_dump_photos".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    prefManager.setKEY_no_of_waste_dump_photos(jsonObject.getString("NO_OF_PHOTOS"));
                }
                Log.d("no_of_waste_dump_photos", "" + responseDecryptedBlockKey);
            }
            if ("carried_out_date_listParams".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    prefManager.set_carried_out_date_listJson(jsonObject.getJSONArray("DATE_LIST"));
                }
                Log.d("carried_out_date_listParams", "" + responseDecryptedBlockKey);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    public class InsertVillageTask extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {
            dbData.open();
            ArrayList<RealTimeMonitoringSystem> villagelist_count = dbData.getAll_Village();
            if (villagelist_count.size() <= 0) {
                if (params.length > 0) {
                    JSONArray jsonArray = new JSONArray();
                    try {
                        jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        RealTimeMonitoringSystem villageListValue = new RealTimeMonitoringSystem();
                        try {
                            villageListValue.setDistictCode(jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_CODE));
                            villageListValue.setBlockCode(jsonArray.getJSONObject(i).getString(AppConstant.BLOCK_CODE));
                            villageListValue.setPvCode(jsonArray.getJSONObject(i).getString(AppConstant.PV_CODE));
                            villageListValue.setPvName(jsonArray.getJSONObject(i).getString(AppConstant.PV_NAME));

                            dbData.insertVillage(villageListValue);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

            }
            return null;
        }

    }
    public class InsertHabTask extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {
            dbData.open();
            ArrayList<RealTimeMonitoringSystem> hablist_count = dbData.getAll_Habitation(prefManager.getDistrictCode(),prefManager.getBlockCode());
            if (hablist_count.size() <= 0) {
                if (params.length > 0) {
                    JSONArray jsonArray = new JSONArray();
                    try {
                        jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        RealTimeMonitoringSystem habListValue = new RealTimeMonitoringSystem();
                        try {
                            habListValue.setDistictCode(jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_CODE));
                            habListValue.setBlockCode(jsonArray.getJSONObject(i).getString(AppConstant.BLOCK_CODE));
                            habListValue.setPvCode(jsonArray.getJSONObject(i).getString(AppConstant.PV_CODE));
                            habListValue.setHabCode(jsonArray.getJSONObject(i).getString(AppConstant.HABB_CODE));
                            habListValue.setHabitationName(jsonArray.getJSONObject(i).getString(AppConstant.HABITATION_NAME));
                            habListValue.setHabitationNameTa(jsonArray.getJSONObject(i).getString(AppConstant.HABITATION_NAME_TA));

                            dbData.insertHabitation(habListValue);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

            }
            return null;


        }
    }


    public class Insertswm_capacity_of_mcc_in_tonesTask extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {
            dbData.open();
            ArrayList<RealTimeMonitoringSystem> capacity_count = dbData.getAll_capacity_of_mcc();
            if (capacity_count.size() <= 0) {
                if (params.length > 0) {
                    JSONArray jsonArray = new JSONArray();
                    try {
                        jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        RealTimeMonitoringSystem capacityList = new RealTimeMonitoringSystem();
                        try {
                            capacityList.setKEY_ID(jsonArray.getJSONObject(i).getString(AppConstant.KEY_ID));
                            capacityList.setKEY_capacity_of_mcc_name(jsonArray.getJSONObject(i).getString(AppConstant.KEY_capacity_of_mcc_name));


                            dbData.Insertswm_capacity_of_mcc_in_tonesTask(capacityList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

            }
            return null;
        }

    }
    public class Insertswm_no_of_thooimai_kaavalarsTask extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {
            dbData.open();
            ArrayList<RealTimeMonitoringSystem> thooimai_kavalars_count = dbData.getAll_thooimai_kaavalars();
            if (thooimai_kavalars_count.size() <= 0) {
                if (params.length > 0) {
                    JSONArray jsonArray = new JSONArray();
                    try {
                        jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        RealTimeMonitoringSystem thooimaiKaavalarList = new RealTimeMonitoringSystem();
                        try {
                            thooimaiKaavalarList.setKEY_ID(jsonArray.getJSONObject(i).getString(AppConstant.KEY_ID));
                            thooimaiKaavalarList.setKEY_thooimai_kaavalars_name(jsonArray.getJSONObject(i).getString(AppConstant.KEY_thooimai_kaavalars_name));


                            dbData.Insertswm_no_of_thooimai_kaavalarsTask(thooimaiKaavalarList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

            }
            return null;
        }

    }
    public class Insertswm_photographs_of_mcc_componentsTask extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {
            dbData.open();
            ArrayList<RealTimeMonitoringSystem> photograph_count = dbData.getAll_photographs_of_mcc();
            if (photograph_count.size() <= 0) {
                if (params.length > 0) {
                    JSONArray jsonArray = new JSONArray();
                    try {
                        jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        RealTimeMonitoringSystem photoGraphList = new RealTimeMonitoringSystem();
                        try {
                            photoGraphList.setKEY_ID(jsonArray.getJSONObject(i).getString("component_id"));
                            photoGraphList.setKEY_photographs_name(jsonArray.getJSONObject(i).getString("component_name"));
                            photoGraphList.setKEY_photographs_name_ta(jsonArray.getJSONObject(i).getString("component_name_ta"));

                            dbData.Insertswm_photographs_of_mcc_componentsTask(photoGraphList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

            }
            return null;
        }

    }
    public class Insertswm_water_supply_availabilityTask extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {
            dbData.open();
            ArrayList<RealTimeMonitoringSystem> water_supply_count = dbData.getAll_water_supply_availability();
            if (water_supply_count.size() <= 0) {
                if (params.length > 0) {
                    JSONArray jsonArray = new JSONArray();
                    try {
                        jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        RealTimeMonitoringSystem waterSupplyList = new RealTimeMonitoringSystem();
                        try {
                            waterSupplyList.setKEY_ID(jsonArray.getJSONObject(i).getString(AppConstant.KEY_ID));
                            waterSupplyList.setKEY_water_supply_availability_name(jsonArray.getJSONObject(i).getString(AppConstant.KEY_water_supply_availability_name));
                            waterSupplyList.setKEY_water_supply_availability_name_ta(jsonArray.getJSONObject(i).getString(AppConstant.KEY_water_supply_availability_name_ta));


                            dbData.Insertswm_water_supply_availabilityTask(waterSupplyList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

            }
            return null;
        }

    }
    public class Insertswm_asset_type extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {
            dbData.open();
            ArrayList<RealTimeMonitoringSystem> water_supply_count = dbData.getAll_swm_asset_type();
            if (water_supply_count.size() <= 0) {
                if (params.length > 0) {
                    JSONArray jsonArray = new JSONArray();
                    try {
                        jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        RealTimeMonitoringSystem waterSupplyList = new RealTimeMonitoringSystem();
                        try {
                            waterSupplyList.setSwm_asset_type_id(jsonArray.getJSONObject(i).getString("swm_asset_type_id"));
                            waterSupplyList.setAsset_type_name(jsonArray.getJSONObject(i).getString("asset_type_name"));
                            waterSupplyList.setAsset_type_name_ta(jsonArray.getJSONObject(i).getString("asset_type_name_ta"));
                            waterSupplyList.setNo_of_photos(jsonArray.getJSONObject(i).getString("no_of_photos"));
                            waterSupplyList.setIs_this_others(jsonArray.getJSONObject(i).getString("is_this_others"));


                            dbData.Insertswm_asset_type(waterSupplyList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

            }
            return null;
        }

    }


    /////////////////// *********************** Now Not Need  //////////////////////
    public class InsertSchemeTask extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {

            dbData.open();
            ArrayList<RealTimeMonitoringSystem> scheme_count = dbData.getAll_Scheme();
            if (scheme_count.size() <= 0) {
                if (params.length > 0) {
                    JSONArray jsonArray = new JSONArray();
                    try {
                        jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        RealTimeMonitoringSystem schemeList = new RealTimeMonitoringSystem();
                        try {
                            schemeList.setSchemeSequentialID(jsonArray.getJSONObject(i).getInt(AppConstant.KEY_SCHEME_SEQUENTIAL_ID));
                            schemeList.setSchemeName(jsonArray.getJSONObject(i).getString(AppConstant.KEY_SCHEME_NAME));
                            schemeList.setFinancialYear(jsonArray.getJSONObject(i).getString(AppConstant.FINANCIAL_YEAR));
                            schemeList.setAdditional_data_status(jsonArray.getJSONObject(i).getString("thittam_app_additional_details_required"));

                            dbData.insertScheme(schemeList);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return null;
        }
    }
    public class InsertFinYearTask extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {
            ArrayList<RealTimeMonitoringSystem> finYear_count = dbData.getAll_FinYear();
            if (finYear_count.size() <= 0) {
                if (params.length > 0) {
                    JSONArray jsonArray = new JSONArray();
                    try {
                        jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        RealTimeMonitoringSystem finYear = new RealTimeMonitoringSystem();
                        try {
                            finYear.setFinancialYear(jsonArray.getJSONObject(i).getString(AppConstant.FINANCIAL_YEAR));
                            dbData.insertFinYear(finYear);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return null;
        }

    }
    public class InsertStageTask extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {
            ArrayList<RealTimeMonitoringSystem> stage_count = dbData.getAll_Stage();
            if (stage_count.size() <= 0) {
                if (params.length > 0) {
                    JSONArray jsonArray = new JSONArray();
                    try {
                        jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        RealTimeMonitoringSystem stage = new RealTimeMonitoringSystem();
                        try {
                            stage.setWorkGroupID(jsonArray.getJSONObject(i).getString(AppConstant.WORK_GROUP_ID));
                            stage.setWorkTypeID(jsonArray.getJSONObject(i).getString(AppConstant.WORK_TYPE_ID));
                            stage.setWorkStageOrder(jsonArray.getJSONObject(i).getString(AppConstant.WORK_STAGE_ORDER));
                            stage.setWorkStageCode(jsonArray.getJSONObject(i).getString(AppConstant.WORK_STAGE_CODE));
                            stage.setWorkStageName(jsonArray.getJSONObject(i).getString(AppConstant.WORK_SATGE_NAME));
                            stage.setAdditional_data_status(jsonArray.getJSONObject(i).getString("thittam_app_additional_details_required"));

                            dbData.insertStage(stage);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (progressHUD != null) {
                progressHUD.cancel();
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressHUD = ProgressHUD.show(SampleHome.this, "Downloading", true, false, null);
        }
    }
    public class InsertAdditionalStageTask extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {
            ArrayList<RealTimeMonitoringSystem> stage_count = dbData.getAdditionalStage();
            if (stage_count.size() <= 0) {
                if (params.length > 0) {
                    JSONArray jsonArray = new JSONArray();
                    try {
                        jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        RealTimeMonitoringSystem stage = new RealTimeMonitoringSystem();
                        try {
                            stage.setWorkTypeCode(jsonArray.getJSONObject(i).getString(AppConstant.WORK_TYPE_CODE));
                            stage.setWorkStageOrder(jsonArray.getJSONObject(i).getString(AppConstant.WORK_STAGE_ORDER));
                            stage.setWorkStageCode(jsonArray.getJSONObject(i).getString(AppConstant.WORK_STAGE_CODE));
                            stage.setWorkStageName(jsonArray.getJSONObject(i).getString(AppConstant.WORK_SATGE_NAME));
                            stage.setWorkTypeFlagLe(jsonArray.getJSONObject(i).getString(AppConstant.CD_TYPE_FLAG));

                            dbData.insertAdditionalStage(stage);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return null;
        }
    }
    public class InsertDistrictTask extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {
            dbData.open();
            ArrayList<RealTimeMonitoringSystem> districtlist_count = dbData.getAll_District();
            if (districtlist_count.size() <= 0) {
                if (params.length > 0) {
                    JSONArray jsonArray = new JSONArray();
                    try {
                        jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        RealTimeMonitoringSystem districtListValue = new RealTimeMonitoringSystem();
                        try {
                            districtListValue.setDistictCode(jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_CODE));
                            districtListValue.setDistrictName(jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_NAME));

                            dbData.insertDistrict(districtListValue);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

            }
            return null;
        }

    }
    public class InsertBlockTask extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {
            dbData.open();
            ArrayList<RealTimeMonitoringSystem> blocklist_count = dbData.getAll_Block();
            if (blocklist_count.size() <= 0) {
                if (params.length > 0) {
                    JSONArray jsonArray = new JSONArray();
                    try {
                        jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        RealTimeMonitoringSystem blocktListValue = new RealTimeMonitoringSystem();
                        try {
                            blocktListValue.setDistictCode(jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_CODE));
                            blocktListValue.setBlockCode(jsonArray.getJSONObject(i).getString(AppConstant.BLOCK_CODE));
                            blocktListValue.setBlockName(jsonArray.getJSONObject(i).getString(AppConstant.BLOCK_NAME));

                            dbData.insertBlock(blocktListValue);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

            }
            return null;
        }

    }
    ////////////////////////// *********************** //////////////

    @Override
    public void OnError(VolleyError volleyError) {

    }

    public void viewVillageList() {
        homeScreenBinding.drawerLayout.closeDrawer(Gravity.LEFT);
        /*Intent intent = new Intent(this, VillageListScreen.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);*/
        Intent intent = new Intent(this, NewMainPage.class);
        intent.putExtra(AppConstant.PV_CODE,prefManager.getPvCode());
        intent.putExtra("mcc_id","");
        intent.putExtra("mcc_name","");
        intent.putExtra("capacity_id","");
        intent.putExtra("capacity_name","");
        intent.putExtra("water_supply_availability_id","");
        intent.putExtra("water_supply_availability_name","");
        intent.putExtra("water_supply_availability_other","");
        intent.putExtra("center_image","");
        intent.putExtra("date_of_commencement","");
        intent.putExtra("latitude","");
        intent.putExtra("longtitude","");
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
    public void viewMCCBasicDetailsList() {
        homeScreenBinding.drawerLayout.closeDrawer(Gravity.LEFT);
        Intent intent = new Intent(this, ViewAndEditMCCDetaila.class);
        intent.putExtra("Entry","No");
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
    public void entryMCCBasicDetailsList() {
        homeScreenBinding.drawerLayout.closeDrawer(Gravity.LEFT);
        Intent intent = new Intent(this, ViewAndEditMCCDetaila.class);
        intent.putExtra("Entry","Yes");
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
    public void viewSWMDetailsList() {
        homeScreenBinding.drawerLayout.closeDrawer(Gravity.LEFT);
        Intent intent = new Intent(this,NewDashborad.class);
        intent.putExtra("Layout","SWM");
        intent.putExtra("village_name",prefManager.getKeyPvNameTa());
        intent.putExtra("pvcode",prefManager.getPvCode());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }


    public void closeApplication() {
        new MyDialog(this).exitDialog(this, getResources().getString(R.string.you_want_to_log_out), "Logout");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                new MyDialog(this).exitDialog(this, getResources().getString(R.string.you_want_to_exit), "Exit");
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onButtonClick(AlertDialog alertDialog, String type) {
        alertDialog.dismiss();
        /*if ("Exit".equalsIgnoreCase(type)) {
            onBackPressed();
        }
        else {

            Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("EXIT", false);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
        }*/
        Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("EXIT", false);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }
    public void logout() {
        homeScreenBinding.drawerLayout.closeDrawer(Gravity.LEFT);
        dbData.open();
        ArrayList<RealTimeMonitoringSystem> getAllBasicDetails =  dbData.getAllBasicDetails();
        ArrayList<RealTimeMonitoringSystem> getTooimaiKaavalrCount =  dbData.getAllThooimaikaavalarListLocalAll();
        ArrayList<RealTimeMonitoringSystem> getComponentImageCount =  dbData.getAllComponentsPendingScreen();
        ArrayList<RealTimeMonitoringSystem> getWasteCollectedCount =  dbData.getParticularWasteCollectedSaveTableRow("","All");
        ArrayList<RealTimeMonitoringSystem> getSwmMasterDetailsCount =  dbData.getAllSWMMasterDetails();
        int gettableCountAssetDetailsTable =  dbData.gettableCountAssetDetailsTable();
        int gettableCountWasteDumpTable =  dbData.gettableCountWasteDumpTable();
        int gettableCountCarriedOutTable =  dbData.gettableCountCarriedOutTable("","","");
        ArrayList<RealTimeMonitoringSystem> activityCount = dbData.getSavedWorkImage("","","","","");
        if (!Utils.isOnline()) {
            Utils.showAlert(this, getResources().getString(R.string.logging_out_loss_data));
        } else {
            if (!(getAllBasicDetails.size() > 0 || getTooimaiKaavalrCount.size()>0 || getComponentImageCount.size()>0 || getWasteCollectedCount.size()>0
                    || getSwmMasterDetailsCount.size()>0 || gettableCountAssetDetailsTable>0||gettableCountWasteDumpTable>0||gettableCountCarriedOutTable>0)) {
                closeApplication();
            }else{
                Utils.showAlert(this,getResources().getString(R.string.sync_all_the_data_before_logout));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        syncButtonVisibility();
    }

    public void getTimeMange(){
        homeScreenBinding.timeImageText.setTextColor(getResources().getColor(R.color.white));
        try {
            DateTimeFormatter dtf = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                System.out.println(dtf.format(now));


            }
        }
        catch (Exception e){
        }

        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if(timeOfDay >= 0 && timeOfDay < 12){
            //Toast.makeText(this, "Good Morning", Toast.LENGTH_SHORT).show();
            homeScreenBinding.timeImage.setImageResource(R.drawable.good_morning_image);
            homeScreenBinding.timeImageText.setText("Good Morning");
        }else if(timeOfDay >= 12 && timeOfDay < 16){
            //Toast.makeText(this, "Good Afternoon", Toast.LENGTH_SHORT).show();
            homeScreenBinding.timeImage.setImageResource(R.drawable.good_after_noon_image);
            homeScreenBinding.timeImageText.setText("Good Afternoon");
        }else if(timeOfDay >= 16 && timeOfDay < 21){
            //Toast.makeText(this, "Good Evening", Toast.LENGTH_SHORT).show();
            homeScreenBinding.timeImage.setImageResource(R.drawable.good_evening_image);
            homeScreenBinding.timeImageText.setText("Good Evening");
        }
        else if(timeOfDay >= 21 && timeOfDay < 24){
            //Toast.makeText(this, "Good Night", Toast.LENGTH_SHORT).show();
            homeScreenBinding.timeImage.setImageResource(R.drawable.good_night_image);
            homeScreenBinding.timeImageText.setText("Good Night");
        }
    }

    public void openMenuDrawer(){
        if(homeScreenBinding.drawerLayout.isDrawerOpen(Gravity.LEFT)){
            homeScreenBinding.drawerLayout.closeDrawer(Gravity.LEFT);
        }else{
            homeScreenBinding.drawerLayout.openDrawer(Gravity.LEFT);
        }
    }

    public void initialUINew(){
        homeScreenBinding.title.setText("Micro Composting Center");
        homeScreenBinding.district.setText("District"+" : "+prefManager.getDistrictName());
        homeScreenBinding.block.setText("Block"+" : "+prefManager.getBlockName());
        homeScreenBinding.finYear.setText("Fin Year"+" : "+"2021-2022");
    }

}

package com.nic.thooimaikaavalar.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupMenu;

import com.android.volley.VolleyError;
import com.nic.thooimaikaavalar.R;
import com.nic.thooimaikaavalar.adapter.AssetsUploadAdapter;
import com.nic.thooimaikaavalar.adapter.BasicDetailsAdapter;
import com.nic.thooimaikaavalar.adapter.ComponentPhotoAdapter;
import com.nic.thooimaikaavalar.adapter.SwmMasterDetailsAdapter;
import com.nic.thooimaikaavalar.adapter.ThooimaiKaavarListLocalAdapter;
import com.nic.thooimaikaavalar.adapter.WasteCollectedAdapter;
import com.nic.thooimaikaavalar.api.Api;
import com.nic.thooimaikaavalar.api.ApiService;
import com.nic.thooimaikaavalar.api.ServerResponse;
import com.nic.thooimaikaavalar.constant.AppConstant;
import com.nic.thooimaikaavalar.dataBase.DBHelper;
import com.nic.thooimaikaavalar.dataBase.dbData;
import com.nic.thooimaikaavalar.databinding.ActivityNewPendingScreenBinding;
import com.nic.thooimaikaavalar.model.RealTimeMonitoringSystem;
import com.nic.thooimaikaavalar.session.PrefManager;
import com.nic.thooimaikaavalar.utils.UrlGenerator;
import com.nic.thooimaikaavalar.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NewPendingScreenActivity extends AppCompatActivity implements Api.ServerResponseListener{
    private PrefManager prefManager;
    public com.nic.thooimaikaavalar.dataBase.dbData dbData = new dbData(this);
    public  DBHelper dbHelper;
    public  SQLiteDatabase db;
    ActivityNewPendingScreenBinding activity_new_pending_screen;
    BasicDetailsAdapter basicDetailsAdapter;
    ThooimaiKaavarListLocalAdapter thooimaiKaavarListLocalAdapter;
    ComponentPhotoAdapter componentPhotoAdapter;
    WasteCollectedAdapter wasteCollectedAdapter;
    SwmMasterDetailsAdapter swmMasterDetailsAdapter;
    AssetsUploadAdapter assetsUploadAdapter;
    String key_id="";
    String type="";
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        activity_new_pending_screen = DataBindingUtil.setContentView(this, R.layout.activity_new_pending_screen);
        activity_new_pending_screen.setActivity(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        prefManager = new PrefManager(this);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Utils.setLocale("ta",this);
        Utils.setupUI(activity_new_pending_screen.parentLayout,this);

        getBasicList();
        activity_new_pending_screen.listName.setText(getResources().getString(R.string.basic_details_of_mcc));
        activity_new_pending_screen.backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        activity_new_pending_screen.menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(NewPendingScreenActivity.this, activity_new_pending_screen.menuIcon);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.pending_adapter_menu, popup.getMenu());
                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        //
                        // Toast.makeText(NewPendingScreenActivity.this,"You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                        switch (item.getItemId()){
                            case R.id.basic_recycler:

                                getBasicList();
                                activity_new_pending_screen.listName.setText(getResources().getString(R.string.basic_details_of_mcc));
                                break;
                            case R.id.thooimai_kavalar_recyler:
                                getThooimaiKaavalrList();
                                activity_new_pending_screen.listName.setText(getResources().getString(R.string.mcc_thooimai_kaavalar_details));
                                break;
                            case R.id.component_photo_recyler:
                                getComponentsPhotoList();
                                activity_new_pending_screen.listName.setText(getResources().getString(R.string.mcc_components_details));
                                break;
                            case R.id.waste_collected_from:
                                getWasteCollectedList();
                                activity_new_pending_screen.listName.setText(getResources().getString(R.string.waste_collected_details));
                                break;
                            case R.id.swm_master_details:
                                getSwmMasterList();
                                activity_new_pending_screen.listName.setText(getResources().getString(R.string.infrastructure_details));
                                break;
                            case R.id.assets_details:
                                getInfraAssets();
                                activity_new_pending_screen.listName.setText(getResources().getString(R.string.infrastructure_assets));
                                break;
                            case R.id.waste_dump_details:
                                getWasteDump();
                                activity_new_pending_screen.listName.setText(getResources().getString(R.string.wastdump_details));
                                break;
                            case R.id.carried_out_details:
                                getCarriedOut();
                                activity_new_pending_screen.listName.setText(getResources().getString(R.string.activity_carried_out));
                                break;
                        }
                        return true;
                    }
                });

                popup.show();//showing popup menu

            }
        });

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.basic_recycler).setIcon(R.drawable.ic_upload_icon);
        return super.onPrepareOptionsMenu(menu);
    }

    public void getBasicList() {
        dbData.open();
        ArrayList<RealTimeMonitoringSystem> getAllBasicDetails =  dbData.getAllBasicDetails();

        if(getAllBasicDetails.size()>0){

            activity_new_pending_screen.noDataIcon.setVisibility(View.GONE);
            activity_new_pending_screen.basicRecycler.setVisibility(View.VISIBLE);
            activity_new_pending_screen.basicRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            basicDetailsAdapter = new BasicDetailsAdapter(getAllBasicDetails,this);
            activity_new_pending_screen.basicRecycler.setAdapter(basicDetailsAdapter);
        }
        else {
            activity_new_pending_screen.noDataIcon.setVisibility(View.VISIBLE);
            activity_new_pending_screen.basicRecycler.setVisibility(View.GONE);
        }

    }
    public void getThooimaiKaavalrList() {
        dbData.open();
        ArrayList<RealTimeMonitoringSystem> getAllThooimaikaavalarListLocal =  dbData.getAllThooimaikaavalarListLocalAll();

        if(getAllThooimaikaavalarListLocal.size()>0){
            activity_new_pending_screen.noDataIcon.setVisibility(View.GONE);
            activity_new_pending_screen.basicRecycler.setVisibility(View.VISIBLE);
            activity_new_pending_screen.basicRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            thooimaiKaavarListLocalAdapter = new ThooimaiKaavarListLocalAdapter(getAllThooimaikaavalarListLocal,this,dbData);
            activity_new_pending_screen.basicRecycler.setAdapter(thooimaiKaavarListLocalAdapter);
        }
        else {
            activity_new_pending_screen.noDataIcon.setVisibility(View.VISIBLE);
            activity_new_pending_screen.basicRecycler.setVisibility(View.GONE);
        }

    }
    public void getComponentsPhotoList() {
        dbData.open();
        ArrayList<RealTimeMonitoringSystem> getAllThooimaikaavalarListLocal =  dbData.getAllComponentsPendingScreen();

        if(getAllThooimaikaavalarListLocal.size()>0){
            activity_new_pending_screen.noDataIcon.setVisibility(View.GONE);
            activity_new_pending_screen.basicRecycler.setVisibility(View.VISIBLE);
            activity_new_pending_screen.basicRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            componentPhotoAdapter = new ComponentPhotoAdapter(getAllThooimaikaavalarListLocal,this,dbData);
            activity_new_pending_screen.basicRecycler.setAdapter(componentPhotoAdapter);
        }
        else {
            activity_new_pending_screen.noDataIcon.setVisibility(View.VISIBLE);
            activity_new_pending_screen.basicRecycler.setVisibility(View.GONE);
        }

    }
    public void getWasteCollectedList() {
        dbData.open();
        ArrayList<RealTimeMonitoringSystem> getAllThooimaikaavalarListLocal =  dbData.getParticularWasteCollectedSaveTableRow("","All");

        if(getAllThooimaikaavalarListLocal.size()>0){
            activity_new_pending_screen.noDataIcon.setVisibility(View.GONE);
            activity_new_pending_screen.basicRecycler.setVisibility(View.VISIBLE);
            activity_new_pending_screen.basicRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            wasteCollectedAdapter = new WasteCollectedAdapter(getAllThooimaikaavalarListLocal,this,dbData);
            activity_new_pending_screen.basicRecycler.setAdapter(wasteCollectedAdapter);
        }
        else {
            activity_new_pending_screen.noDataIcon.setVisibility(View.VISIBLE);
            activity_new_pending_screen.basicRecycler.setVisibility(View.GONE);
        }

    }
    public void getSwmMasterList() {
        dbData.open();
        ArrayList<RealTimeMonitoringSystem> getAllSwmMasterDetails =  dbData.getAllSWMMasterDetails();

        if(getAllSwmMasterDetails.size()>0){
            activity_new_pending_screen.noDataIcon.setVisibility(View.GONE);
            activity_new_pending_screen.basicRecycler.setVisibility(View.VISIBLE);
            activity_new_pending_screen.basicRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            swmMasterDetailsAdapter = new SwmMasterDetailsAdapter(getAllSwmMasterDetails,this,dbData,"Local","");
            activity_new_pending_screen.basicRecycler.setAdapter(swmMasterDetailsAdapter);
        }
        else {
            activity_new_pending_screen.noDataIcon.setVisibility(View.VISIBLE);
            activity_new_pending_screen.basicRecycler.setVisibility(View.GONE);
        }

    }
    public void getInfraAssets() {
        dbData.open();
        int getAllSwmMasterDetails =  dbData.gettableCountAssetDetailsTable();
        if(getAllSwmMasterDetails>0){
            activity_new_pending_screen.noDataIcon.setVisibility(View.GONE);
            activity_new_pending_screen.basicRecycler.setVisibility(View.VISIBLE);
            activity_new_pending_screen.basicRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            assetsUploadAdapter = new AssetsUploadAdapter(this,dbData,prefManager,"Assets");
            activity_new_pending_screen.basicRecycler.setAdapter(assetsUploadAdapter);
        }
        else {
            activity_new_pending_screen.noDataIcon.setVisibility(View.VISIBLE);
            activity_new_pending_screen.basicRecycler.setVisibility(View.GONE);
        }



    }
    public void getWasteDump() {
        dbData.open();
        int getAllSwmMasterDetails =  dbData.gettableCountWasteDumpTable();
        if(getAllSwmMasterDetails>0){
            activity_new_pending_screen.noDataIcon.setVisibility(View.GONE);
            activity_new_pending_screen.basicRecycler.setVisibility(View.VISIBLE);
            activity_new_pending_screen.basicRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            assetsUploadAdapter = new AssetsUploadAdapter(this,dbData,prefManager,"Waste_Dump");
            activity_new_pending_screen.basicRecycler.setAdapter(assetsUploadAdapter);
        }
        else {
            activity_new_pending_screen.noDataIcon.setVisibility(View.VISIBLE);
            activity_new_pending_screen.basicRecycler.setVisibility(View.GONE);
        }



    }
    public void getCarriedOut() {
        dbData.open();
        int getCarriedOutDetails =  dbData.gettableCountCarriedOutTable("All","","");
        if(getCarriedOutDetails>0){
            activity_new_pending_screen.noDataIcon.setVisibility(View.GONE);
            activity_new_pending_screen.basicRecycler.setVisibility(View.VISIBLE);
            activity_new_pending_screen.basicRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            assetsUploadAdapter = new AssetsUploadAdapter(this,dbData,prefManager,"Carried_Out");
            activity_new_pending_screen.basicRecycler.setAdapter(assetsUploadAdapter);
        }
        else {
            activity_new_pending_screen.noDataIcon.setVisibility(View.VISIBLE);
            activity_new_pending_screen.basicRecycler.setVisibility(View.GONE);
        }



    }

    public void SyncData(JSONObject jsonObject,String key_id_n,String type_){
        key_id =key_id_n;
        type =type_;
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), jsonObject.toString());
        JSONObject dataSet = new JSONObject();
        try {
            dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
            dataSet.put(AppConstant.DATA_CONTENT, authKey);
            Log.d("EncryptedDetails", "" + dataSet);
            Log.d("JSONDetails", "" + jsonObject);
            try {
                new ApiService(this).makeJSONObjectRequest("SaveDetails", Api.Method.POST, UrlGenerator.getWorkListUrl(), dataSet, "not cache", this);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        catch (JSONException e){

        }


    }
    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        String urlType = serverResponse.getApi();
        try {
            JSONObject responseObj = serverResponse.getJsonResponse();
            if ("SaveDetails".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {

                    Utils.showAlert(this,jsonObject.getString("MESSAGE"));
                    if(type.equalsIgnoreCase("thooimai")){
                        int sdsm = db.delete(DBHelper.THOOIMAI_KAAVALARS_DETAIL_OF_MCC_SAVE_LOCAL, "mcc_id = ? ", new String[]{key_id});
                        getThooimaiKaavalrList();
                        thooimaiKaavarListLocalAdapter.notifyDataSetChanged();

                    }
                    else if(type.equalsIgnoreCase("basic")){
                        int sdsm = db.delete(DBHelper.BASIC_DETAILS_OF_MCC_SAVE, "id = ? ", new String[]{key_id});
                        getBasicList();
                        basicDetailsAdapter.notifyDataSetChanged();

                    }
                    else if(type.equalsIgnoreCase("photos")){
                        int sdsm = db.delete(DBHelper.COMPOST_TUB_IMAGE_TABLE, "mcc_id = ? ", new String[]{key_id});
                        getComponentsPhotoList();
                        componentPhotoAdapter.notifyDataSetChanged();

                    }
                    else if(type.equalsIgnoreCase("waste_collected")){
                        int sdsm = db.delete(DBHelper.WASTE_COLLECTED_SAVE_TABLE, "mcc_id = ? ", new String[]{key_id});
                        getWasteCollectedList();
                        wasteCollectedAdapter.notifyDataSetChanged();

                    }
                    else if(type.equalsIgnoreCase("SWM_Master")){
                        int sdsm = db.delete(DBHelper.SWM_MASTER_DETAILS_TABLE, "id = ? ", new String[]{key_id});
                        getSwmMasterList();
                        swmMasterDetailsAdapter.notifyDataSetChanged();

                    }
                    else if(type.equalsIgnoreCase("Assets_Details")){
                        int sdsm = db.delete(DBHelper.SWM_ASSET_DETAILS_TABLE,null,null);
                        int sdsm1 = db.delete(DBHelper.SWM_ASSET_PHOTOS_TABLE,null,null);
                        getInfraAssets();
                        assetsUploadAdapter.notifyDataSetChanged();

                    }
                    else if(type.equalsIgnoreCase("Waste_Dump")){
                        int sdsm = db.delete(DBHelper.SWM_WASTE_DUMP_PHOTOS_DETAILS,null,null);
                        getWasteDump();
                        assetsUploadAdapter.notifyDataSetChanged();

                    }
                    else if(type.equalsIgnoreCase("Carried_Out")){
                        int sdsm = db.delete(DBHelper.SWM_CARRIED_OUT_DETAILS,null,null);
                        int sdsm1 = db.delete(DBHelper.SWM_CARRIED_OUT_PHOTOS_DETAILS,null,null);
                        getCarriedOut();
                        assetsUploadAdapter.notifyDataSetChanged();

                    }

                }
                else if(jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("FAIL")){
                    Utils.showAlert(this,jsonObject.getString("MESSAGE"));
                }
                Log.d("SaveDetails", "" + responseDecryptedBlockKey);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Utils.showAlert(this,"Something Wrong!");
        }
    }

    @Override
    public void OnError(VolleyError volleyError) {
        Utils.showAlert(this,getResources().getString(R.string.no_response_from_server));
    }

    public void deletePending(String type) {
        if(type.equals("Assets_Details")) {
            int sdsm = db.delete(DBHelper.SWM_ASSET_DETAILS_TABLE,null,null);
            int sdsm1 = db.delete(DBHelper.SWM_ASSET_PHOTOS_TABLE,null,null);
            getInfraAssets();
            assetsUploadAdapter.notifyDataSetChanged();
        }
        else if(type.equals("Waste_Dump")) {
            int sdsm = db.delete(DBHelper.SWM_WASTE_DUMP_PHOTOS_DETAILS,null,null);
            getWasteDump();
            assetsUploadAdapter.notifyDataSetChanged();
        }
        else if(type.equalsIgnoreCase("Carried_Out")){
            int sdsm = db.delete(DBHelper.SWM_CARRIED_OUT_DETAILS,null,null);
            int sdsm1 = db.delete(DBHelper.SWM_CARRIED_OUT_PHOTOS_DETAILS,null,null);
            getCarriedOut();
            assetsUploadAdapter.notifyDataSetChanged();

        }
    }

}

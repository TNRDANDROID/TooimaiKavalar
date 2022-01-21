package com.nic.thooimaikaavalar.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupMenu;

import com.android.volley.VolleyError;
import com.nic.thooimaikaavalar.R;
import com.nic.thooimaikaavalar.adapter.BasicDetailsAdapter;
import com.nic.thooimaikaavalar.adapter.ComponentPhotoAdapter;
import com.nic.thooimaikaavalar.adapter.ThooimaiKaavarListLocalAdapter;
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
    public static DBHelper dbHelper;
    public static SQLiteDatabase db;
    ActivityNewPendingScreenBinding activity_new_pending_screen;
    BasicDetailsAdapter basicDetailsAdapter;
    ThooimaiKaavarListLocalAdapter thooimaiKaavarListLocalAdapter;
    ComponentPhotoAdapter componentPhotoAdapter;
    String key_id="";
    String type="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        activity_new_pending_screen = DataBindingUtil.setContentView(this, R.layout.activity_new_pending_screen);
        activity_new_pending_screen.setActivity(this);
        prefManager = new PrefManager(this);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        getBasicList();
        activity_new_pending_screen.listName.setText("Basic Details of MCC");
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
                                activity_new_pending_screen.listName.setText("Basic Details of MCC");
                                break;
                            case R.id.thooimai_kavalar_recyler:
                                getThooimaiKaavalrList();
                                activity_new_pending_screen.listName.setText("MCC Thooimai Kaavalar Details");
                                break;
                            case R.id.component_photo_recyler:
                                getComponentsPhotoList();
                                activity_new_pending_screen.listName.setText("MCC Component Details");
                                break;
                        }
                        return true;
                    }
                });

                popup.show();//showing popup menu

            }
        });

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

                    Utils.showAlert(this,"Successfully Saved");
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

                }
                else if(jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("FAIL")){
                    Utils.showAlert(this,jsonObject.getString("MESSAGE"));
                }
                Log.d("SaveDetails", "" + responseDecryptedBlockKey);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnError(VolleyError volleyError) {

    }
}

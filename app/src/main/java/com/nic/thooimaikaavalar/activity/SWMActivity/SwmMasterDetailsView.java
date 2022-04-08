package com.nic.thooimaikaavalar.activity.SWMActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.VolleyError;
import com.nic.thooimaikaavalar.R;
import com.nic.thooimaikaavalar.adapter.SwmMasterDetailsAdapter;
import com.nic.thooimaikaavalar.api.Api;
import com.nic.thooimaikaavalar.api.ApiService;
import com.nic.thooimaikaavalar.api.ServerResponse;
import com.nic.thooimaikaavalar.constant.AppConstant;
import com.nic.thooimaikaavalar.dataBase.DBHelper;
import com.nic.thooimaikaavalar.dataBase.dbData;
import com.nic.thooimaikaavalar.databinding.ActivitySwmMasterDetailsViewBinding;
import com.nic.thooimaikaavalar.model.RealTimeMonitoringSystem;
import com.nic.thooimaikaavalar.session.PrefManager;
import com.nic.thooimaikaavalar.support.ProgressHUD;
import com.nic.thooimaikaavalar.utils.UrlGenerator;
import com.nic.thooimaikaavalar.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SwmMasterDetailsView extends AppCompatActivity implements Api.ServerResponseListener{

    ActivitySwmMasterDetailsViewBinding activitySwmMasterDetailsViewBinding;
    public com.nic.thooimaikaavalar.dataBase.dbData dbData = new dbData(this);
    private PrefManager prefManager;
    private SQLiteDatabase db;
    public DBHelper dbHelper;
    String pv_code;
    ArrayList<RealTimeMonitoringSystem> villageList;
    ArrayList<RealTimeMonitoringSystem> swmMasterDetailsServerList;
    private ProgressHUD progressHUD;

    SwmMasterDetailsAdapter swmMasterDetailsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activitySwmMasterDetailsViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_swm_master_details_view);
        activitySwmMasterDetailsViewBinding.setActivity(this);

        prefManager = new PrefManager(this);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        dbData.open();

        activitySwmMasterDetailsViewBinding.swmMasterDetailsRecyler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        activitySwmMasterDetailsViewBinding.mccReclclerRl.setVisibility(View.GONE);
        activitySwmMasterDetailsViewBinding.noDataText.setVisibility(View.GONE);
        activitySwmMasterDetailsViewBinding.swmMasterDetailsRecyler.setVisibility(View.GONE);
        activitySwmMasterDetailsViewBinding.previewImageLayout.setVisibility(View.GONE);



        activitySwmMasterDetailsViewBinding.backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(activitySwmMasterDetailsViewBinding.previewImageLayout.getVisibility()==View.VISIBLE){
                    activitySwmMasterDetailsViewBinding.previewImageLayout.setVisibility(View.GONE);
                }
                else {
                    finish();
                }

            }
        });
        activitySwmMasterDetailsViewBinding.closeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activitySwmMasterDetailsViewBinding.previewImageLayout.setVisibility(View.GONE);
            }
        });

        pv_code = prefManager.getPvCode();
        if(Utils.isOnline()){
            int gettableCountWasteDumpTable = dbData.gettableCountWasteDumpTable();
            if(gettableCountWasteDumpTable>0){
                new fetchSwmDetailsFromServer().execute();
            }
            else {
                getSwmMasterList();
            }

        }
        else {
            new fetchSwmDetailsFromServer().execute();
        }

        activitySwmMasterDetailsViewBinding.backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
            }
        });

    }

    public void getSwmMasterList() {
        try {
            new ApiService(this).makeJSONObjectRequest("SWMList", Api.Method.POST, UrlGenerator.getWorkListUrl(), SwmMasteJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject SwmMasteJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), SwmMasteNormalJsonParams(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("encrypt", "" + dataSet);
        return dataSet;
    }

    public  JSONObject SwmMasteNormalJsonParams(Activity activity) throws JSONException {
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, "details_of_swm_infra_view");
        dataSet.put(AppConstant.PV_CODE, pv_code);
        Log.d("normal", "" + dataSet);
        return dataSet;
    }

    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        try {
            String urlType = serverResponse.getApi();
            JSONObject responseObj = serverResponse.getJsonResponse();

            if ("SWMList".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedSchemeKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedSchemeKey);
                Log.d("Query",responseDecryptedSchemeKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new InsertSWMListFromServerTask().execute(jsonObject);


                }
                else if(jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("NO_RECORD")){
                    Utils.showAlert(this,getResources().getString(R.string.no_record_found));
                    activitySwmMasterDetailsViewBinding.mccReclclerRl.setVisibility(View.VISIBLE);
                    activitySwmMasterDetailsViewBinding.noDataText.setVisibility(View.VISIBLE);
                    activitySwmMasterDetailsViewBinding.swmMasterDetailsRecyler.setVisibility(View.GONE);
                }

                Log.d("SWMList", "" + responseDecryptedSchemeKey);
            }
            /*if ("DeleteDetails".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedSchemeKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedSchemeKey);
                Log.d("Query",responseDecryptedSchemeKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    //Utils.showAlert(this,jsonObject.getString("MESSAGE"));
                    Utils.showAlert(this,getResources().getString(R.string.successfully_deleted));
                    int sdsm = db.delete(DBHelper.THOOIMAI_KAAVALARS_DETAIL_OF_MCC_SAVE_LOCAL, "mcc_id = ? ", new String[]{delete_mcc_id});
                    int sds = db.delete(DBHelper.BASIC_DETAILS_OF_MCC_SAVE, "mcc_id = ? ", new String[]{delete_mcc_id});
                    int sd = db.delete(DBHelper.COMPOST_TUB_IMAGE_TABLE, "mcc_id = ? ", new String[]{delete_mcc_id});
                    int sd1 = db.delete(DBHelper.WASTE_COLLECTED_SAVE_TABLE, "mcc_id = ? ", new String[]{delete_mcc_id});
                    getBasicMccList();

                } else if(jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("NO_RECORD")){
                    Utils.showAlert(this,jsonObject.getString("MESSAGE"));
                }

                Log.d("MCCList", "" + responseDecryptedSchemeKey);
            }*/
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnError(VolleyError volleyError) {

    }

    public class fetchSwmDetailsFromServer extends AsyncTask<Void, Void,ArrayList<RealTimeMonitoringSystem>> {
        @Override
        protected ArrayList<RealTimeMonitoringSystem> doInBackground(Void... params) {
            dbData.open();
            swmMasterDetailsServerList = new ArrayList<>();
            swmMasterDetailsServerList.addAll(dbData.getAllSWMDetailsFromServer(pv_code));
            Log.d("getAllSWMFromServer", String.valueOf(swmMasterDetailsServerList.size()));
            return swmMasterDetailsServerList;
        }

        @Override
        protected void onPostExecute(ArrayList<RealTimeMonitoringSystem> villageList) {
            super.onPostExecute(villageList);
            if(villageList.size()>0){
                activitySwmMasterDetailsViewBinding.swmMasterDetailsRecyler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                activitySwmMasterDetailsViewBinding.mccReclclerRl.setVisibility(View.VISIBLE);
                activitySwmMasterDetailsViewBinding.noDataText.setVisibility(View.GONE);
                activitySwmMasterDetailsViewBinding.swmMasterDetailsRecyler.setVisibility(View.VISIBLE);
                swmMasterDetailsAdapter = new SwmMasterDetailsAdapter(swmMasterDetailsServerList,SwmMasterDetailsView.this,dbData,"Server");
                activitySwmMasterDetailsViewBinding.swmMasterDetailsRecyler.setAdapter(swmMasterDetailsAdapter);
            }
            else {
                activitySwmMasterDetailsViewBinding.mccReclclerRl.setVisibility(View.VISIBLE);
                activitySwmMasterDetailsViewBinding.noDataText.setVisibility(View.VISIBLE);
                activitySwmMasterDetailsViewBinding.swmMasterDetailsRecyler.setVisibility(View.GONE);

            }
        }
    }

    public class InsertSWMListFromServerTask extends AsyncTask<JSONObject, Void, Void> {

        private  boolean running = true;



        @Override
        protected Void doInBackground(JSONObject... params) {

            dbData.open();

            if (params.length > 0) {
                String where="pvcode = ?";

                db.delete(DBHelper.SWM_MASTER_DETAILS_SERVER_TABLE, where, new String[]{pv_code}) ;

                JSONArray jsonArray = new JSONArray();

                try {
                    jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < jsonArray.length(); i++) {
                    RealTimeMonitoringSystem swmMasterDetailsList = new RealTimeMonitoringSystem();
                    try {

                        swmMasterDetailsList.setSwm_infra_details_id(jsonArray.getJSONObject(i).getString("swm_infra_details_id"));
                        swmMasterDetailsList.setDistictCode(jsonArray.getJSONObject(i).getString("dcode"));
                        swmMasterDetailsList.setBlockCode(jsonArray.getJSONObject(i).getString("bcode"));
                        swmMasterDetailsList.setPvCode(jsonArray.getJSONObject(i).getString("pvcode"));
                        swmMasterDetailsList.setPvName(prefManager.getKeyPvNameTa());
                        swmMasterDetailsList.setNo_of_thooimai_kaavalars_allocated(jsonArray.getJSONObject(i).getString("no_of_thooimai_kaavalars_allocated"));
                        swmMasterDetailsList.setNo_of_thooimai_kaavalars_working(jsonArray.getJSONObject(i).getString("no_of_thooimai_kaavalars_working"));
                        swmMasterDetailsList.setWhether_community_compost_pit_available_in_panchayat(jsonArray.getJSONObject(i).getString("whether_community_compost_pit_available_in_panchayat"));
                        swmMasterDetailsList.setWhether_vermi_compost_pit_available_in_panchayat(jsonArray.getJSONObject(i).getString("whether_vermi_compost_pit_available_in_panchayat"));
                        swmMasterDetailsList.setAny_integrated_nuesery_devlp_near_swm_facility(jsonArray.getJSONObject(i).getString("any_integrated_nuesery_devlp_near_swm_facility"));
                        swmMasterDetailsList.setIs_there_any_waste_dump(jsonArray.getJSONObject(i).getString("any_waste_dump_seen_in_the_panchayat"));
                        //swmMasterDetailsList.setIs_there_any_waste_dump("");


                        dbData.insertSwmMasterDetailsFromServer(swmMasterDetailsList);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            // }
            return null;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressHUD = ProgressHUD.show(SwmMasterDetailsView.this, "Downloading", true, false, null);

        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(progressHUD!=null){
                progressHUD.cancel();
                new fetchSwmDetailsFromServer().execute();
            }
        }
    }

}

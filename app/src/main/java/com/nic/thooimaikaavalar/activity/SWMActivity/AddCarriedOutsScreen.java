package com.nic.thooimaikaavalar.activity.SWMActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.android.volley.VolleyError;
import com.nic.thooimaikaavalar.R;
import com.nic.thooimaikaavalar.adapter.CarriedOutWastDumpAdapter;
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
import com.nic.thooimaikaavalar.utils.UrlGenerator;
import com.nic.thooimaikaavalar.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

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

    ArrayList<RealTimeMonitoringSystem> carriedOutDateList;
    private String choose_date_string="";
    CarriedOutWastDumpAdapter carriedOutWastDumpAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        carriedOutsScreenBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_carried_outs_screen);
        carriedOutsScreenBinding.setActivity(this);

        prefManager = new PrefManager(this);
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

        int gettableCountWasteDumpTable = dbData.gettableCountWasteDumpTable();
        if(gettableCountWasteDumpTable<0){
            getOnlineWasteDumpList();
        }


        carriedOutsScreenBinding.chooseDateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0){
                    choose_date_string = carriedOutDateList.get(position).getCarried_out_date();
                }
                else {
                    choose_date_string = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    public void getIntentData(){
        swm_infra_details_id = getIntent().getStringExtra("swm_infra_details_id");
        no_of_thooimai_kaavalars_allocated = getIntent().getStringExtra("no_of_thooimai_kaavalars_allocated");
        no_of_thooimai_kaavalars_working = getIntent().getStringExtra("no_of_thooimai_kaavalars_working");
        whether_community_compost_pit_available_in_panchayat = getIntent().getStringExtra("whether_community_compost_pit_available_in_panchayat");
        whether_vermi_compost_pit_available_in_panchayat = getIntent().getStringExtra("whether_vermi_compost_pit_available_in_panchayat");
        any_integrated_nuesery_devlp_near_swm_facility = getIntent().getStringExtra("any_integrated_nuesery_devlp_near_swm_facility");
    }

    public void loadCarriedOutDateList(){
        carriedOutDateList = new ArrayList<>();
        RealTimeMonitoringSystem carriedOutItem1 = new RealTimeMonitoringSystem();
        carriedOutItem1.setCarried_out_date("Select Date");
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
        dataSet.put(AppConstant.KEY_SERVICE_ID, "details_of_swm_waste_dump_view");
        dataSet.put("swm_infra_details_id", swm_infra_details_id);
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

        @Override
        protected ArrayList<RealTimeMonitoringSystem> doInBackground(JSONObject... params) {

            ArrayList<RealTimeMonitoringSystem> assetList = new ArrayList<>();
            if (params.length > 0) {
                JSONArray jsonArray = new JSONArray();
                try {
                    jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                for (int i = 0; i < jsonArray.length(); i++) {
                    RealTimeMonitoringSystem assetsListItem = new RealTimeMonitoringSystem();
                    try {
                        assetsListItem.setDistictCode(jsonArray.getJSONObject(i).getString("dcode"));
                        assetsListItem.setBlockCode(jsonArray.getJSONObject(i).getString("bcode"));
                        assetsListItem.setPvCode(jsonArray.getJSONObject(i).getString("pvcode"));
                        assetsListItem.setSwm_infra_details_id(jsonArray.getJSONObject(i).getString("swm_infra_details_id"));
                        assetsListItem.setSwm_waste_dump_photos_id(jsonArray.getJSONObject(i).getString("swm_waste_dump_photos_id"));
                        assetsListItem.setIs_there_any_waste_dump(jsonArray.getJSONObject(i).getString("is_photo_of_waste_dump"));
                        byte[] decodedString = Base64.decode(jsonArray.getJSONObject(i).getString(AppConstant.KEY_IMAGE), Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        assetsListItem.setImage(decodedByte);
                        assetList.add(assetsListItem);

                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }
            }

            return assetList;
        }

        @Override
        protected void onPostExecute(ArrayList<RealTimeMonitoringSystem> wasteDumpList) {
            super.onPostExecute(wasteDumpList);
            if(wasteDumpList.size()>0){
                carriedOutWastDumpAdapter = new CarriedOutWastDumpAdapter(wasteDumpList,AddCarriedOutsScreen.this,"Server",dbData);
                carriedOutsScreenBinding.wasteDumpRecycler.setAdapter(carriedOutWastDumpAdapter);
                carriedOutsScreenBinding.wasteDumpRecycler.setVisibility(View.VISIBLE);
            }
            else {
                carriedOutsScreenBinding.wasteDumpRecycler.setVisibility(View.GONE);
            }
        }
    }
}

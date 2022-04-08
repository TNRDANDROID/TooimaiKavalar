package com.nic.thooimaikaavalar.activity.SWMActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.bumptech.glide.util.Util;
import com.nic.thooimaikaavalar.R;
import com.nic.thooimaikaavalar.activity.VillageListScreen;
import com.nic.thooimaikaavalar.adapter.CommonAdapter;
import com.nic.thooimaikaavalar.adapter.VillageListAdapter;
import com.nic.thooimaikaavalar.dataBase.DBHelper;
import com.nic.thooimaikaavalar.dataBase.dbData;
import com.nic.thooimaikaavalar.databinding.ActivityMasterFormSwmEntryBinding;
import com.nic.thooimaikaavalar.model.RealTimeMonitoringSystem;
import com.nic.thooimaikaavalar.session.PrefManager;
import com.nic.thooimaikaavalar.utils.Utils;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class MasterFormSwmEntry extends AppCompatActivity {

    ActivityMasterFormSwmEntryBinding activityMasterFormSwmEntryBinding;
    private PrefManager prefManager;
    private SQLiteDatabase db;
    public DBHelper dbHelper;
    public com.nic.thooimaikaavalar.dataBase.dbData dbData = new dbData(this);

    ArrayList<RealTimeMonitoringSystem> villageList;

    String pvcode="";
    String pvname="";
    String compostPitAvailableYes_No="";
    String vermiCompostFacilityAvailableYes_No="";
    String nurseryDevelopedAvailableYes_No="";

    String swm_infra_details_id="";

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_form_swm_entry);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        activityMasterFormSwmEntryBinding = DataBindingUtil.setContentView(this, R.layout.activity_master_form_swm_entry);
        activityMasterFormSwmEntryBinding.setActivity(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        prefManager = new PrefManager(this);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //new loadVillageSpinner().execute();

        activityMasterFormSwmEntryBinding.compostPitAvailableRadioGroup.clearCheck();
        activityMasterFormSwmEntryBinding.vermiCompostFacilityAvailableRadioGroup.clearCheck();
        activityMasterFormSwmEntryBinding.nurseryDevelopedAvailableRadioGroup.clearCheck();

        /*activityMasterFormSwmEntryBinding.villageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0){
                    pvcode = villageList.get(position).getPvCode();
                    pvname = villageList.get(position).getPvName();
                }
                else {
                    pvcode="";
                    pvname="";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/
        activityMasterFormSwmEntryBinding.compostPitAvailableYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    compostPitAvailableYes_No="Y";
                }
            }
        });
        activityMasterFormSwmEntryBinding.compostPitAvailableNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    compostPitAvailableYes_No="N";
                }
            }
        });
        activityMasterFormSwmEntryBinding.vermiCompostFacilityAvailableYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    vermiCompostFacilityAvailableYes_No="Y";
                }
            }
        });
        activityMasterFormSwmEntryBinding.vermiCompostFacilityAvailableNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    vermiCompostFacilityAvailableYes_No="N";
                }
            }
        });
        activityMasterFormSwmEntryBinding.nurseryDevelopedAvailableYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    nurseryDevelopedAvailableYes_No="Y";
                }
            }
        });
        activityMasterFormSwmEntryBinding.nurseryDevelopedAvailableNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    nurseryDevelopedAvailableYes_No="N";
                }
            }
        });

        activityMasterFormSwmEntryBinding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidationForm();
            }
        });

        activityMasterFormSwmEntryBinding.backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
            }
        });


    }

    @SuppressLint("StaticFieldLeak")
    public class loadVillageSpinner extends AsyncTask<Void, Void,ArrayList<RealTimeMonitoringSystem>> {
        @Override
        protected ArrayList<RealTimeMonitoringSystem> doInBackground(Void... params) {
            dbData.open();
            villageList = new ArrayList<>();
            RealTimeMonitoringSystem realTimeMonitoringSystem = new RealTimeMonitoringSystem();
            realTimeMonitoringSystem.setPvCode("0");
            realTimeMonitoringSystem.setPvName(getResources().getString(R.string.select_village));
            villageList.add(realTimeMonitoringSystem);
            villageList = dbData.getAll_Village();
            Log.d("VILLAGE_COUNT", String.valueOf(villageList.size()));
            return villageList;
        }

        @Override
        protected void onPostExecute(ArrayList<RealTimeMonitoringSystem> villageList) {
            super.onPostExecute(villageList);
            if(villageList.size()>0){
                activityMasterFormSwmEntryBinding.villageSpinner.setAdapter(new CommonAdapter(MasterFormSwmEntry.this,villageList,""));

            }
            else {
                activityMasterFormSwmEntryBinding.villageSpinner.setAdapter(null);
            }
        }
    }

    public void checkValidationForm(){
        String no_of_thooimai_kavalars_alocated= activityMasterFormSwmEntryBinding.noOfThooimaiKaavalarsAllocated.getText().toString();
        String no_of_thooimai_kavalars_working= activityMasterFormSwmEntryBinding.noOfThooimaiKaavalarsWorking.getText().toString();
        boolean greater_then_flag=false;
        int no_of_thooimai_kavalars_alocated_count = Integer.parseInt(no_of_thooimai_kavalars_alocated);
        int no_of_thooimai_kavalars_working_count = Integer.parseInt(no_of_thooimai_kavalars_working);
        if(no_of_thooimai_kavalars_alocated_count>=no_of_thooimai_kavalars_working_count){
            greater_then_flag = true;
        }
        else {
            greater_then_flag =false;
        }
            if(!no_of_thooimai_kavalars_alocated.equals("")){
                if(!no_of_thooimai_kavalars_working.equals("")){
                    if(greater_then_flag){
                        if(!compostPitAvailableYes_No.equals("")){
                            if(!vermiCompostFacilityAvailableYes_No.equals("")){
                                if(!nurseryDevelopedAvailableYes_No.equals("")){
                                    saveMasterFormLocally();
                                }
                                else {
                                    Utils.showAlert(MasterFormSwmEntry.this,getResources().getString(R.string.is_there_any_integrated_nursery_developed_near_swm_facility));
                                }
                            }
                            else {
                                Utils.showAlert(MasterFormSwmEntry.this,getResources().getString(R.string.whether_community_vermi_compost_facility_available_in_the_panchayat));

                            }
                        }
                        else {
                            Utils.showAlert(MasterFormSwmEntry.this,getResources().getString(R.string.whether_community_compost_pit_available_in_the_panchayat));

                        }
                    }
                    else {
                        Utils.showAlert(MasterFormSwmEntry.this,getResources().getString(R.string.enter_no_of_thooimai_kaavalars_working));
                    }
                }
                else {
                    Utils.showAlert(MasterFormSwmEntry.this,getResources().getString(R.string.enter_no_of_thooimai_kaavalars_working));

                }
            }
            else {
                Utils.showAlert(MasterFormSwmEntry.this,getResources().getString(R.string.enter_no_of_thooimai_kaavalars_allocated));
            }

    }
    public void saveMasterFormLocally(){
        String no_of_thooimai_kaavalars_allocated= activityMasterFormSwmEntryBinding.noOfThooimaiKaavalarsAllocated.getText().toString();
        String no_of_thooimai_kaavalars_working= activityMasterFormSwmEntryBinding.noOfThooimaiKaavalarsWorking.getText().toString();
        long id = 0;
        String whereClause = "";
        String[] whereArgs = null;
        try {
            ContentValues values = new ContentValues();

            values.put("swm_infra_details_id",swm_infra_details_id);
            values.put("dcode",prefManager.getDistrictCode());
            values.put("bcode",prefManager.getBlockCode());
            values.put("pvcode",prefManager.getPvCode());
            values.put("pvname",prefManager.getKeyPvNameTa());
            values.put("no_of_thooimai_kaavalars_allocated",no_of_thooimai_kaavalars_allocated);
            values.put("no_of_thooimai_kaavalars_working",no_of_thooimai_kaavalars_working);
            values.put("whether_community_compost_pit_available_in_panchayat",compostPitAvailableYes_No);
            values.put("whether_vermi_compost_pit_available_in_panchayat",vermiCompostFacilityAvailableYes_No);
            values.put("any_integrated_nuesery_devlp_near_swm_facility",nurseryDevelopedAvailableYes_No);

            whereClause = "swm_infra_details_id = ? and pvcode = ?";
            whereArgs = new String[]{swm_infra_details_id,pvcode};
            dbData.open();

            if(swm_infra_details_id.equals("")) {
                id = db.insert(DBHelper.SWM_MASTER_DETAILS_TABLE, null, values);
                if(id > 0){
                    Toasty.success(this, getResources().getString(R.string.inserted_success), Toast.LENGTH_LONG, true).show();
                    onBackPressed();
                }

            }
            else if(!swm_infra_details_id.equals("")) {
                ArrayList<RealTimeMonitoringSystem> imageOffline = dbData.getParticularVillageSWMMasterDetails(swm_infra_details_id,pvcode);
                if(imageOffline.size()>0){
                    id = db.update(DBHelper.SWM_MASTER_DETAILS_TABLE, values, whereClause, whereArgs);
                    if(id > 0){
                        Toasty.success(this, getResources().getString(R.string.updated_success), Toast.LENGTH_LONG, true).show();
                        onBackPressed();
                    }

                }
                else {
                    id = db.insert(DBHelper.SWM_MASTER_DETAILS_TABLE, null, values);
                    if(id > 0){
                        Toasty.success(this, getResources().getString(R.string.inserted_success), Toast.LENGTH_LONG, true).show();
                        onBackPressed();
                    }

                }

            }
        }
        catch (Exception e){

        }
    }
}

package com.nic.thooimaikaavalar.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.nic.thooimaikaavalar.R;
import com.nic.thooimaikaavalar.adapter.ThooimaiKaavalrEnterDetailsAdapter;
import com.nic.thooimaikaavalar.dataBase.DBHelper;
import com.nic.thooimaikaavalar.dataBase.dbData;
import com.nic.thooimaikaavalar.databinding.ActivityAddThooimaiKaavalarDetailsBinding;
import com.nic.thooimaikaavalar.model.RealTimeMonitoringSystem;
import com.nic.thooimaikaavalar.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class AddThooimaiKaavalarDetails extends AppCompatActivity {

    ActivityAddThooimaiKaavalarDetailsBinding activityAddThooimaiKaavalarDetailsBinding;
    public com.nic.thooimaikaavalar.dataBase.dbData dbData = new dbData(this);
    int count;
    Intent intent;
    ThooimaiKaavalrEnterDetailsAdapter thooimaiKaavalrEnterDetailsAdapter;

    EditText name,mobile_number,date_of_engage,date_of_training;
    String thooimai_kaavalar_id="";
    ArrayList<RealTimeMonitoringSystem> thooimaiKaavalrList;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        activityAddThooimaiKaavalarDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_thooimai_kaavalar_details);
        activityAddThooimaiKaavalarDetailsBinding.setActivity(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Utils.setLocale("ta",this);

        count =getIntent().getIntExtra("count",0);

        initializeRecycler();

        activityAddThooimaiKaavalarDetailsBinding.backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        activityAddThooimaiKaavalarDetailsBinding.addBtnLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkItems();
            }
        });

    }

    public void initializeRecycler(){
        activityAddThooimaiKaavalarDetailsBinding.addRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        thooimaiKaavalrList = new ArrayList<>();
        for(int i=0;i<count;i++){
            RealTimeMonitoringSystem realTimeMonitoringSystem = new RealTimeMonitoringSystem();
            realTimeMonitoringSystem.setName_of_the_thooimai_kaavalars("");
            realTimeMonitoringSystem.setMobile_no("");
            realTimeMonitoringSystem.setDate_of_engagement("");
            realTimeMonitoringSystem.setDate_of_training_given("");
            thooimaiKaavalrList.add(realTimeMonitoringSystem);
        }
        thooimaiKaavalrEnterDetailsAdapter = new ThooimaiKaavalrEnterDetailsAdapter(count,this,thooimaiKaavalrList);
        activityAddThooimaiKaavalarDetailsBinding.addRecycler.setAdapter(thooimaiKaavalrEnterDetailsAdapter);

    }

    public void checkItems() {
        dbData.open();
        /*activityAddThooimaiKaavalarDetailsBinding.addRecycler.getChildCount();
        //int childCount = activityAddThooimaiKaavalarDetailsBinding.addRecycler.getChildCount();
        int childCount = thooimaiKaavalrEnterDetailsAdapter.getAllList().size();*/
        ArrayList<RealTimeMonitoringSystem> thooimai_kaavalar_list = new ArrayList<>(thooimaiKaavalrEnterDetailsAdapter.getAllList());
        int count=0;
        try {
            if (thooimai_kaavalar_list.size() > 0) {
                if (checkCondition()) {
                    for (int i = 0; i < thooimai_kaavalar_list.size(); i++) {
                        count = count + 1;
                        /*View vv = activityAddThooimaiKaavalarDetailsBinding.addRecycler.getChildAt(i);
                        mobile_number = vv.findViewById(R.id.mobile_number);
                        name = vv.findViewById(R.id.name);
                        date_of_engage = vv.findViewById(R.id.date_of_engage);
                        date_of_training = vv.findViewById(R.id.date_of_training);*/

                        String name_new = thooimai_kaavalar_list.get(i).getName_of_the_thooimai_kaavalars();
                        String mobile_number_1 = thooimai_kaavalar_list.get(i).getMobile_no();
                        String date_of_engage_1 = thooimai_kaavalar_list.get(i).getDate_of_engagement();
                        String date_of_training_1 =thooimai_kaavalar_list.get(i).getDate_of_training_given();
                        String mcc_id = getIntent().getStringExtra("mcc_id");

                        RealTimeMonitoringSystem realTimeMonitoringSystem = new RealTimeMonitoringSystem();
                        realTimeMonitoringSystem.setMcc_id(mcc_id);
                        realTimeMonitoringSystem.setName_of_the_thooimai_kaavalars(name_new);
                        realTimeMonitoringSystem.setMobile_no(mobile_number_1);
                        realTimeMonitoringSystem.setDate_of_engagement(date_of_engage_1);
                        realTimeMonitoringSystem.setDate_of_training_given(date_of_training_1);
                        realTimeMonitoringSystem.setThooimai_kaavalar_id("");

                        dbData.insertThooimaiKaavaleDetailsLocal(realTimeMonitoringSystem);

                        if (thooimai_kaavalar_list.size() == count) {
                                onBackPressed();
                                Toast.makeText(this, getResources().getString(R.string.inserted_success), Toast.LENGTH_SHORT).show();
                            }

                    }
                    //onBackPressed();
                }
                else {
                    Utils.showAlert(this,getResources().getString(R.string.please_enter_all_the_details));
                }
            }



        } catch (Exception e) {
        }

    }

    public boolean checkCondition(){
        /*activityAddThooimaiKaavalarDetailsBinding.addRecycler.getChildCount();
        int childCount = activityAddThooimaiKaavalarDetailsBinding.addRecycler.getChildCount();*/
        ArrayList<RealTimeMonitoringSystem> thooimai_kaavalar_list = new ArrayList<>(thooimaiKaavalrEnterDetailsAdapter.getAllList());

        try {
            JSONArray imageJson = new JSONArray();
            if (thooimai_kaavalar_list.size() > 0) {
                for (int i = 0; i < thooimai_kaavalar_list.size(); i++) {
                    JSONObject jsonObject = new JSONObject();
                    /*View vv = activityAddThooimaiKaavalarDetailsBinding.addRecycler.getChildAt(i);
                    mobile_number = vv.findViewById(R.id.mobile_number);
                    name = vv.findViewById(R.id.name);
                    date_of_engage = vv.findViewById(R.id.date_of_engage);
                    date_of_training = vv.findViewById(R.id.date_of_training);
*/
                    String name_new = thooimai_kaavalar_list.get(i).getName_of_the_thooimai_kaavalars();
                    String mobile_number_1 = thooimai_kaavalar_list.get(i).getMobile_no();
                    String date_of_engage_1 = thooimai_kaavalar_list.get(i).getDate_of_engagement();
                    String date_of_training_1 =thooimai_kaavalar_list.get(i).getDate_of_training_given();

                    jsonObject.put("name", name_new);
                    jsonObject.put("mobile_number", mobile_number_1);
                    jsonObject.put("date_of_engage", date_of_engage_1);
                    jsonObject.put("date_of_training", date_of_training_1);
                    /*jsonObject.put("deleted","N");*/
                    imageJson.put(jsonObject);
                  /*  if(!name.getText().toString().equals("")){
                        if(!mobile_number.getText().toString().equals("")&& Utils.isValidMobile(mobile_number.getText().toString())){
                            if(!date_of_engage.getText().toString().equals("")){
                                if(!date_of_training.getText().toString().equals("")){
                                    return true;
                                }
                                else {
                                    Utils.showAlert(this,getResources().getString(R.string.please_enter_date_of_training));
                                    date_of_training.requestFocus();
                                    return false;
                                }
                            }
                            else {
                                Utils.showAlert(this,getResources().getString(R.string.please_enter_data_of_engage));
                                date_of_engage.requestFocus();
                                return false;
                            }
                        }
                        else {

                            Utils.showAlert(this,getResources().getString(R.string.please_enter_valid_mobile_number));
                            mobile_number.requestFocus();
                            return false;
                        }
                    }
                    else {
                        Utils.showAlert(this,getResources().getString(R.string.please_enter_name));
                        name.requestFocus();
                        return false;
                    }*/

                }
            }
            try {
                boolean flag =false;
                JSONObject jsonObject1=new JSONObject();
                for (int i=0;i<imageJson.length();i++){
                    jsonObject1=imageJson.getJSONObject(i);
                    if(!jsonObject1.getString("name").equals("")&&!jsonObject1.getString("mobile_number").equals("")
                            && Utils.isValidMobilenew(jsonObject1.getString("mobile_number"))
                            &&!jsonObject1.getString("date_of_engage").equals("")&&!jsonObject1.getString("date_of_training").equals("")){
                        flag=true;
                    }
                    else {
                        flag=false;
                        break;
                    }
                }
                if (flag){
                    return true;
                }
                else {
                    return false;

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }



        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

}

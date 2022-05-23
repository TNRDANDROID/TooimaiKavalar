package com.nic.thooimaikaavalar.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nic.thooimaikaavalar.R;
import com.nic.thooimaikaavalar.activity.SWMActivity.MasterFormSwmEntry;
import com.nic.thooimaikaavalar.activity.SWMActivity.SwmMasterDetailsView;
import com.nic.thooimaikaavalar.databinding.ActivityNewDashboradBinding;
import com.nic.thooimaikaavalar.session.PrefManager;
import com.nic.thooimaikaavalar.utils.Utils;

import java.text.ParseException;
import java.util.Date;

public class NewDashborad extends AppCompatActivity {

    ActivityNewDashboradBinding activityNewDashboradBinding;
    PrefManager prefManager;
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        activityNewDashboradBinding = DataBindingUtil.setContentView(this, R.layout.activity_new_dashborad);
        activityNewDashboradBinding.setActivity(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Utils.setLocale("ta",this);
        prefManager  = new PrefManager(this);
        activityNewDashboradBinding.btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlert();
            }
        });
        activityNewDashboradBinding.btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoeditThooimaiKavalarActivity();
            }
        });
        activityNewDashboradBinding.btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoEditViewEditComponentsActivity();
            }
        });
        activityNewDashboradBinding.btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoViewWasteCollectedDetailsActivity();
            }
        });
        activityNewDashboradBinding.swmAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoAddSwm();
            }
        });
        activityNewDashboradBinding.swmViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoViewSwm("Infrastructure");
            }
        });
        activityNewDashboradBinding.swmCarriedOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoViewSwm("Carried_Out");
            }
        });
        activityNewDashboradBinding.swmPwmUnitSaleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoViewSwm("PWMUnitSale");
            }
        });

        activityNewDashboradBinding.backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        initializeUi();
    }

    public void initializeUi(){
        if(getIntent().getStringExtra("Layout").equals("SWM")){
            activityNewDashboradBinding.villageName.setText(getResources().getString(R.string.village)+" : "+getIntent().getStringExtra("village_name"));
            activityNewDashboradBinding.villageName.setVisibility(View.VISIBLE);
            activityNewDashboradBinding.dateOfCommencement.setVisibility(View.GONE);
            activityNewDashboradBinding.dateOfCommencement.setVisibility(View.GONE);
            activityNewDashboradBinding.swmDetailsLayout.setVisibility(View.VISIBLE);
            activityNewDashboradBinding.mccDetailsLayout.setVisibility(View.GONE);
            if(prefManager.getis_pwm().equals("Y")){
                activityNewDashboradBinding.swmPwmUnitSaleBtn.setVisibility(View.VISIBLE);
            }
            else {
                activityNewDashboradBinding.swmPwmUnitSaleBtn.setVisibility(View.GONE);
            }

        }
        else {
            activityNewDashboradBinding.swmDetailsLayout.setVisibility(View.GONE);
            activityNewDashboradBinding.mccDetailsLayout.setVisibility(View.VISIBLE);
            activityNewDashboradBinding.villageName.setVisibility(View.VISIBLE);
            activityNewDashboradBinding.dateOfCommencement.setVisibility(View.VISIBLE);
            activityNewDashboradBinding.dateOfCommencement.setVisibility(View.VISIBLE);
            String date_of_commencement= dateFormate(getIntent().getStringExtra("date_of_commencement"),"yes");
            activityNewDashboradBinding.villageName.setText(getResources().getString(R.string.village)+" : "+getIntent().getStringExtra("village_name"));
            activityNewDashboradBinding.mccName.setText(getResources().getString(R.string.mcc_name)+" : "+getIntent().getStringExtra("mcc_name"));
            activityNewDashboradBinding.dateOfCommencement.setText(getResources().getString(R.string.date_of_commencement)+" : "+date_of_commencement);
        }

    }

    public  void showAlert(){
        try {
            final Dialog dialog = new Dialog(NewDashborad.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.no_of_count_dialog);



            ImageView close_icon = (ImageView) dialog.findViewById(R.id.close_icon);
            TextView add_text = (TextView) dialog.findViewById(R.id.add);
            TextView minus_text = (TextView) dialog.findViewById(R.id.minus);
            TextView count_text = (TextView) dialog.findViewById(R.id.number);
            Button okBtn = (Button) dialog.findViewById(R.id.btn_ok);
            Button cancelBtn = (Button) dialog.findViewById(R.id.btn_cancel);

            close_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            add_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int count = Integer.parseInt(count_text.getText().toString());
                    if(count<5){
                        count =count+1;
                        count_text.setText(""+count);
                    }
                }
            });
            minus_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int count = Integer.parseInt(count_text.getText().toString());
                    if(count>1){
                        count =count-1;
                        count_text.setText(""+count);
                    }
                }
            });

            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            okBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent =new Intent(NewDashborad.this,AddThooimaiKaavalarDetails.class);
                    intent.putExtra("count",Integer.parseInt(count_text.getText().toString()));
                    intent.putExtra("mcc_id",getIntent().getStringExtra("mcc_id"));

                    startActivity(intent);
                    dialog.dismiss();
                }
            });


            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void gotoeditThooimaiKavalarActivity(){
        Intent intent =new Intent(NewDashborad.this,NewThooimaiKavalarEdit.class);
        intent.putExtra("mcc_id",getIntent().getStringExtra("mcc_id"));
        startActivity(intent);
    }
    public void gotoEditViewEditComponentsActivity(){
        Intent intent =new Intent(NewDashborad.this,ViewTakeEditComponentsPhots.class);
        intent.putExtra("mcc_id",getIntent().getStringExtra("mcc_id"));
        intent.putExtra("village_name",getIntent().getStringExtra("village_name"));
        intent.putExtra("mcc_name",getIntent().getStringExtra("mcc_name"));
        intent.putExtra("date_of_commencement",getIntent().getStringExtra("date_of_commencement"));
        startActivity(intent);
    }
    public void gotoViewWasteCollectedDetailsActivity(){
        Intent intent =new Intent(NewDashborad.this,ViewWasteCollectedDetails.class);
        intent.putExtra("mcc_id",getIntent().getStringExtra("mcc_id"));
        intent.putExtra("village_name",getIntent().getStringExtra("village_name"));
        intent.putExtra("pvcode",getIntent().getStringExtra("pvcode"));
        intent.putExtra("mcc_name",getIntent().getStringExtra("mcc_name"));
        intent.putExtra("date_of_commencement",getIntent().getStringExtra("date_of_commencement"));
        startActivity(intent);
    }

    public void gotoAddSwm(){
        Intent intent =new Intent(NewDashborad.this, MasterFormSwmEntry.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
    }
    public void gotoViewSwm(String flag){
        Intent intent =new Intent(NewDashborad.this, SwmMasterDetailsView.class);
        intent.putExtra("Flag",flag);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
    }

    public  String dateFormate( String strDate,String type ){
        try {
            SimpleDateFormat sdfSource =null;
            if(type.equals("")) {
                // create SimpleDateFormat object with source string date format
                sdfSource = new SimpleDateFormat(
                        "dd-M-yyyy");
            }
            else {
                sdfSource = new SimpleDateFormat(
                        "yyyy-mm-dd");
            }

            // parse the string into Date object
            Date date = sdfSource.parse(strDate);

            // create SimpleDateFormat object with desired date format
            SimpleDateFormat sdfDestination = new SimpleDateFormat(
                    "dd-MM-yyyy");

            // parse the date into another format
            strDate = sdfDestination.format(date);

           /* System.out
                    .println("Date is converted from yyyy-MM-dd'T'hh:mm:ss'.000Z' format to dd/MM/yyyy, ha");
            System.out.println("Converted date is : " + strDate.toLowerCase());
*/
        } catch (ParseException pe) {
            System.out.println("Parse Exception : " + pe);
        }
        return strDate;
    }

}

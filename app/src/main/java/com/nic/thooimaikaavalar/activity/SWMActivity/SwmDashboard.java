package com.nic.thooimaikaavalar.activity.SWMActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.nic.thooimaikaavalar.R;
import com.nic.thooimaikaavalar.adapter.ComponentsAdapter;
import com.nic.thooimaikaavalar.dataBase.DBHelper;
import com.nic.thooimaikaavalar.dataBase.dbData;
import com.nic.thooimaikaavalar.databinding.ActivitySwmDashboardBinding;
import com.nic.thooimaikaavalar.model.RealTimeMonitoringSystem;
import com.nic.thooimaikaavalar.session.PrefManager;
import com.nic.thooimaikaavalar.utils.Utils;

import java.util.ArrayList;

public class SwmDashboard extends AppCompatActivity {

    ActivitySwmDashboardBinding activitySwmDashboardBinding;

    public com.nic.thooimaikaavalar.dataBase.dbData dbData = new dbData(this);
    private PrefManager prefManager;
    private SQLiteDatabase db;
    public DBHelper dbHelper;
    String flag_type="";
    private ShimmerRecyclerView recyclerView;
    ArrayList<RealTimeMonitoringSystem> swmAssetsList;
    ComponentsAdapter swmAssetsAdapter;
    String assets_id ="";
    String assets_name ="";
    String swm_infra_details_id ="";
    String no_of_photos ="";
    String is_this_others ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activitySwmDashboardBinding = DataBindingUtil.setContentView(this, R.layout.activity_swm_dashboard);
        activitySwmDashboardBinding.setActivity(this);

        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        prefManager = new PrefManager(this);
        Utils.setLocale("ta",this);
        activitySwmDashboardBinding.view2.setVisibility(View.GONE);
        activitySwmDashboardBinding.view1.setVisibility(View.VISIBLE);
        activitySwmDashboardBinding.tv2.setTextColor(getResources().getColor(R.color.black));
        activitySwmDashboardBinding.tv1.setTextColor(getResources().getColor(R.color.white));
        flag_type="Add_Details";
        activitySwmDashboardBinding.addDetailsRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activitySwmDashboardBinding.view2.setVisibility(View.GONE);
                activitySwmDashboardBinding.view1.setVisibility(View.VISIBLE);
                activitySwmDashboardBinding.tv2.setTextColor(getResources().getColor(R.color.black));
                activitySwmDashboardBinding.tv1.setTextColor(getResources().getColor(R.color.white));
                flag_type="Add_Details";
            }
        });
        activitySwmDashboardBinding.viewDetailsRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activitySwmDashboardBinding.view2.setVisibility(View.VISIBLE);
                activitySwmDashboardBinding.view1.setVisibility(View.GONE);
                activitySwmDashboardBinding.tv2.setTextColor(getResources().getColor(R.color.white));
                activitySwmDashboardBinding.tv1.setTextColor(getResources().getColor(R.color.black));
                flag_type="View_Details";
            }
        });
        initAssetsRecycler();

        activitySwmDashboardBinding.backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
            }
        });

    }

    private void initAssetsRecycler() {
        recyclerView = activitySwmDashboardBinding.swmAssetRecycler;
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new fetchSwmAssets().execute();
            }
        },1000);
    }
    public class fetchSwmAssets extends AsyncTask<Void, Void,ArrayList<RealTimeMonitoringSystem>> {
        @Override
        protected ArrayList<RealTimeMonitoringSystem> doInBackground(Void... params) {
            dbData.open();
            ArrayList<RealTimeMonitoringSystem> capacityList = new ArrayList<>();
            capacityList = dbData.getAll_swm_asset_type();
            Log.d("swm_asset_type_COUNT", String.valueOf(capacityList.size()));
            return capacityList;
        }

        @Override
        protected void onPostExecute(ArrayList<RealTimeMonitoringSystem> capacityList) {
            super.onPostExecute(capacityList);
            swmAssetsList = new ArrayList<>();
            swmAssetsList.addAll(capacityList);

            swmAssetsAdapter = new ComponentsAdapter(capacityList,SwmDashboard.this,"SWM");
            recyclerView.setAdapter(swmAssetsAdapter);
            recyclerView.showShimmerAdapter();
            recyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadCards();
                }
            }, 1000);
        }
    }
    private void loadCards() {

        recyclerView.hideShimmerAdapter();
        //initNoOfThooimaiKaavalarRecyler();

    }

    public void assetsAdapterItemClicked(int position){
        assets_id = swmAssetsList.get(position).getSwm_asset_type_id();
        assets_name = swmAssetsList.get(position).getAsset_type_name_ta();
        swm_infra_details_id = getIntent().getStringExtra("swm_infra_details_id");
        is_this_others = swmAssetsList.get(position).getIs_this_others();
        no_of_photos = swmAssetsList.get(position).getNo_of_photos();
        if(flag_type.equals("Add_Details")){

            Intent intent = new Intent(SwmDashboard.this,SwmAddAssetDetails.class);
            intent.putExtra("OnOffType","Offline");
            intent.putExtra("assets_id",assets_id);
            intent.putExtra("swm_infra_details_id",swm_infra_details_id);
            intent.putExtra("assets_name",assets_name);
            intent.putExtra("is_this_others",is_this_others);
            intent.putExtra("no_of_photos",no_of_photos);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
        }
        else {
            Intent intent = new Intent(SwmDashboard.this,SwmAddAssetDetails.class);
            intent.putExtra("OnOffType","Online");
            intent.putExtra("assets_id",assets_id);
            intent.putExtra("swm_infra_details_id",swm_infra_details_id);
            intent.putExtra("assets_name",assets_name);

            startActivity(intent);
            overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
        }

    }

}

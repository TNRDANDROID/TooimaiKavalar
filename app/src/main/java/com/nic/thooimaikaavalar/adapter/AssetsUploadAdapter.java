package com.nic.thooimaikaavalar.adapter;

import android.app.Dialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nic.thooimaikaavalar.R;
import com.nic.thooimaikaavalar.activity.NewPendingScreenActivity;
import com.nic.thooimaikaavalar.constant.AppConstant;
import com.nic.thooimaikaavalar.dataBase.DBHelper;
import com.nic.thooimaikaavalar.databinding.AssetsUploadRecyclerItemBinding;
import com.nic.thooimaikaavalar.model.RealTimeMonitoringSystem;
import com.nic.thooimaikaavalar.session.PrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class AssetsUploadAdapter extends RecyclerView.Adapter<AssetsUploadAdapter.MyViewHolder> {
    private LayoutInflater layoutInflater;
    Context context;
    private com.nic.thooimaikaavalar.dataBase.dbData dbData;
    public DBHelper dbHelper;
    public SQLiteDatabase db;
    PrefManager prefManager;
    String type;

    public AssetsUploadAdapter(Context context,com.nic.thooimaikaavalar.dataBase.dbData dbData,PrefManager prefManager,String type) {
        this.context = context;
        this.dbData = dbData;
        this.prefManager = prefManager;
        this.type = type;
        try {
            dbHelper = new DBHelper(context);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @NonNull
    @Override
    public AssetsUploadAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        AssetsUploadRecyclerItemBinding assetsUploadRecyclerItemBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.assets_upload_recycler_item, viewGroup, false);
        return new MyViewHolder(assetsUploadRecyclerItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull AssetsUploadAdapter.MyViewHolder holder, int position) {

        holder.assetsUploadRecyclerItemBinding.villageName.setText(prefManager.getKeyPvNameTa());

        holder.assetsUploadRecyclerItemBinding.deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_and_delete_alert("delete");
            }
        });
        holder.assetsUploadRecyclerItemBinding.uploadIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_and_delete_alert("save");
            }
        });
        holder.assetsUploadRecyclerItemBinding.logoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private AssetsUploadRecyclerItemBinding assetsUploadRecyclerItemBinding;

        public MyViewHolder(AssetsUploadRecyclerItemBinding Binding) {
            super(Binding.getRoot());
            assetsUploadRecyclerItemBinding = Binding;
        }
    }

    public void save_and_delete_alert(String save_delete){
        try {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.alert_dialog);

            TextView text = (TextView) dialog.findViewById(R.id.tv_message);
            if(save_delete.equals("save")) {
                text.setText("Do You Want to Upload?");
            }
            else if(save_delete.equals("delete")){
                text.setText("Do You Want to Delete?");
            }

            Button yesButton = (Button) dialog.findViewById(R.id.btn_ok);
            Button noButton = (Button) dialog.findViewById(R.id.btn_cancel);
            noButton.setVisibility(View.VISIBLE);
            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(save_delete.equals("save")) {
                        if(type.equals("Waste_Dump")){
                            uploadWasteDump();
                        }
                        else {
                            uploadAssets();
                        }

                        dialog.dismiss();
                    }
                    else if(save_delete.equals("delete")) {
                        dialog.dismiss();
                    }
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void uploadWasteDump() {
        dbData.open();
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();
        JSONArray waste_dump_photos = new JSONArray();
        JSONArray swm_waste_dump = new JSONArray();

        try {
            ArrayList<RealTimeMonitoringSystem> wasteDumpDetails = dbData.getWasteDumpPhotosList("All","",prefManager.getPvCode());
            for(int j = 0; j < 1; j++) {
                jsonObject = new JSONObject();
                jsonObject.put("swm_infra_details_id",wasteDumpDetails.get(0).getSwm_infra_details_id());
                jsonObject.put("any_waste_dump_seen_in_the_panchayat",wasteDumpDetails.get(0).getIs_there_any_waste_dump());
                swm_waste_dump.put(jsonObject);
                for (int i = 0; i < wasteDumpDetails.size(); i++) {
                    JSONObject waste_dumpJson = new JSONObject();
                    if (wasteDumpDetails.get(i).getIs_there_any_waste_dump().equals("Y")) {
                        waste_dumpJson.put("photo_lat", wasteDumpDetails.get(i).getLatitude());
                        waste_dumpJson.put("photo_long", wasteDumpDetails.get(i).getLongitude());
                        waste_dumpJson.put("image", BitMapToString(wasteDumpDetails.get(i).getImage()));
                        waste_dump_photos.put(waste_dumpJson);
                    }
                }
                if(jsonObject.getString("any_waste_dump_seen_in_the_panchayat").equals("Y")){
                    jsonObject.put("waste_dump_photos",waste_dump_photos);
                    swm_waste_dump.put(jsonObject);
                }

            }
            try {
                for(int k=0;k<swm_waste_dump.length();k++){
                    for(int l=k+1;l<swm_waste_dump.length();l++){
                        if (swm_waste_dump.get(k) == swm_waste_dump.get(l)) {
                            swm_waste_dump.remove(l);
                            l--;
                        }
                    }
                }
            }
            catch (JSONException e){
                e.printStackTrace();
            }
            jsonObject1.put(AppConstant.KEY_SERVICE_ID,"swm_waste_dump_save");
            jsonObject1.put("swm_waste_dump",swm_waste_dump);

            Log.d("json",""+jsonObject1);
            ((NewPendingScreenActivity)context).SyncData(jsonObject1, "","Waste_Dump");

        }
        catch (JSONException e){
            e.printStackTrace();
        }

    }

    public void uploadAssets() {
        dbData.open();
        JSONObject jsonObject = new JSONObject();
        JSONObject asset_type_details_List_jsonObject = new JSONObject();
        JSONObject asset_type_details_List_jsonObject1 = new JSONObject();
        JSONObject asset_type_details_photos_jsonObject1 = new JSONObject();
        JSONArray infra_assets = new JSONArray();
        JSONArray infra_assets1 = new JSONArray();
        JSONArray infra_asset_details = new JSONArray();
        JSONArray infra_asset_photos = new JSONArray();
        ArrayList<RealTimeMonitoringSystem> asset_typeList = new ArrayList<>();
        ArrayList<RealTimeMonitoringSystem> asset_type_details_List = new ArrayList<>();
        ArrayList<RealTimeMonitoringSystem> asset_type_details_List1 = new ArrayList<>();
        ArrayList<RealTimeMonitoringSystem> asset_type_details_photos_List = new ArrayList<>();
        asset_typeList = dbData.getAll_swm_asset_type();
        try {
            for (int i = 0; i < asset_typeList.size() ; i++ ){

                asset_type_details_List = new ArrayList<>();
                asset_type_details_List = dbData.getParticularAssetsList(prefManager.getPvCode(),asset_typeList.get(i).getSwm_asset_type_id(),"","");
                if(asset_type_details_List.size()>0) {
                    for (int j = 0; j < 1; j++) {
                        infra_asset_details = new JSONArray();
                        asset_type_details_List_jsonObject = new JSONObject();
                        asset_type_details_List_jsonObject.put("swm_infra_details_id", asset_type_details_List.get(0).getSwm_infra_details_id());
                        asset_type_details_List_jsonObject.put("swm_asset_type_id", asset_type_details_List.get(0).getSwm_asset_type_id());
                        infra_assets.put(asset_type_details_List_jsonObject);
                        asset_type_details_List1 = new ArrayList<>();
                        asset_type_details_List1 = dbData.getParticularAssetsList(prefManager.getPvCode(), asset_type_details_List.get(0).getSwm_asset_type_id(), asset_type_details_List.get(0).getSwm_infra_details_id(), "All");
                            for (int k = 0; k < asset_type_details_List1.size(); k++) {
                                infra_asset_photos = new JSONArray();
                                asset_type_details_List_jsonObject1 = new JSONObject();
                                asset_type_details_List_jsonObject1.put("if_asset_others_name", asset_type_details_List1.get(k).getOthers_name());
                                asset_type_details_List_jsonObject1.put("is_functional", asset_type_details_List1.get(k).getIs_functional());
                                infra_asset_details.put(asset_type_details_List_jsonObject1);
                                asset_type_details_photos_List = new ArrayList<>();
                                asset_type_details_photos_List = dbData.getParticularAssetsPhotosList(asset_type_details_List1.get(k).getId()
                                        , prefManager.getPvCode(), asset_type_details_List1.get(k).getSwm_asset_type_id(), asset_type_details_List1.get(k).getSwm_infra_details_id());

                                    for (int l = 0; l < asset_type_details_photos_List.size(); l++) {
                                        asset_type_details_photos_jsonObject1 = new JSONObject();
                                        asset_type_details_photos_jsonObject1.put("photo_geo_lat", asset_type_details_photos_List.get(l).getLatitude());
                                        asset_type_details_photos_jsonObject1.put("photo_geo_long", asset_type_details_photos_List.get(l).getLongitude());
                                        asset_type_details_photos_jsonObject1.put("image", BitMapToString(asset_type_details_photos_List.get(l).getImage()));
                                        infra_asset_photos.put(asset_type_details_photos_jsonObject1);
                                    }
                                asset_type_details_List_jsonObject1.put("infra_asset_photos",infra_asset_photos);
                                infra_asset_details.put(asset_type_details_List_jsonObject1);
                            }
                        try {
                            for(int k=0;k<infra_asset_details.length();k++){
                                for(int l=k+1;l<infra_asset_details.length();l++){
                                    if (infra_asset_details.get(k) == infra_asset_details.get(l)) {
                                        infra_asset_details.remove(l);
                                        l--;
                                    }
                                }
                            }
                        }
                        catch (JSONException e){
                            e.printStackTrace();
                        }
                        asset_type_details_List_jsonObject.put("infra_asset_details",infra_asset_details);
                        infra_assets.put(asset_type_details_List_jsonObject);
                    }

                }

            }

            try {
                try {
                    for(int k=0;k<infra_assets.length();k++){
                        for(int l=k+1;l<infra_assets.length();l++){
                            if (infra_assets.get(k) == infra_assets.get(l)) {
                                infra_assets.remove(l);
                                l--;
                            }
                        }
                    }
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
                jsonObject.put(AppConstant.KEY_SERVICE_ID,"swm_infra_assets_save");
                jsonObject.put("infra_assets",infra_assets);

                Log.d("json",""+jsonObject);
                ((NewPendingScreenActivity)context).SyncData(jsonObject, "","Assets_Details");



            }
            catch (JSONException e){
                e.printStackTrace();
            }


        }
        catch (JSONException e){
            e.printStackTrace();
        }

    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

}

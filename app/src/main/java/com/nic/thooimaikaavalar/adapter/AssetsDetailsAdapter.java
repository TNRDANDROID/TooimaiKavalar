package com.nic.thooimaikaavalar.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
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
import com.nic.thooimaikaavalar.activity.FullImageActivity;
import com.nic.thooimaikaavalar.activity.SWMActivity.Add_ViewWasteDumpDetails;
import com.nic.thooimaikaavalar.activity.SWMActivity.SwmAddAssetDetails;
import com.nic.thooimaikaavalar.constant.AppConstant;
import com.nic.thooimaikaavalar.dataBase.DBHelper;
import com.nic.thooimaikaavalar.databinding.AssetsRecyclerItemBinding;
import com.nic.thooimaikaavalar.databinding.SwmMasterBasicDetailsAdapterBinding;
import com.nic.thooimaikaavalar.model.RealTimeMonitoringSystem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class AssetsDetailsAdapter extends RecyclerView.Adapter<AssetsDetailsAdapter.MyViewHolder> {
    private LayoutInflater layoutInflater;
    private List<RealTimeMonitoringSystem> assetDetailsList;
    static JSONObject dataset = new JSONObject();
    Context context;
    String type;
    private com.nic.thooimaikaavalar.dataBase.dbData dbData;
    public DBHelper dbHelper;
    public SQLiteDatabase db;

    public AssetsDetailsAdapter(List<RealTimeMonitoringSystem> assetDetailsList, Context context, String type, com.nic.thooimaikaavalar.dataBase.dbData dbData) {
        this.assetDetailsList = assetDetailsList;
        this.context = context;
        this.type = type;
        this.dbData = dbData;
        try {
            dbHelper = new DBHelper(context);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @NonNull
    @Override
    public AssetsDetailsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        AssetsRecyclerItemBinding assetsRecyclerItemBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.assets_recycler_item, viewGroup, false);
        return new MyViewHolder(assetsRecyclerItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull AssetsDetailsAdapter.MyViewHolder holder, int position) {

        holder.assetsRecyclerItemBinding.previewImageLayout.setVisibility(View.GONE);
            if(assetDetailsList.get(position).getOthers_name().equals("")){
                holder.assetsRecyclerItemBinding.assetName.setText(assetDetailsList.get(position).getAsset_type_name_ta());
            }
            else {
                holder.assetsRecyclerItemBinding.assetName.setText(assetDetailsList.get(position).getAsset_type_name_ta()+
                " ( "+assetDetailsList.get(position).getOthers_name()+" ) ");
            }

            if(assetDetailsList.get(position).getIs_functional().equals("Y")){
                holder.assetsRecyclerItemBinding.ans.setText(context.getResources().getString(R.string.yes));
                holder.assetsRecyclerItemBinding.ans.setTextColor(Color.GREEN);
                holder.assetsRecyclerItemBinding.status.setText(context.getResources().getString(R.string.yes));
                holder.assetsRecyclerItemBinding.status.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.circlegreen));
            }
            else {
                holder.assetsRecyclerItemBinding.ans.setText(context.getResources().getString(R.string.no));
                holder.assetsRecyclerItemBinding.ans.setTextColor(Color.RED);
                holder.assetsRecyclerItemBinding.status.setText(context.getResources().getString(R.string.no));
                holder.assetsRecyclerItemBinding.status.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.curved_red_bg));
            }




        holder.assetsRecyclerItemBinding.imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FullImageActivity.class);
                intent.putExtra("Activity","Assets");
                if(type.equals("Local")){
                    intent.putExtra("OnOffType","Offline");
                    intent.putExtra("asset_image_id",(assetDetailsList.get(position).getId()));
                    intent.putExtra("swm_asset_type_id",(assetDetailsList.get(position).getSwm_asset_type_id()));
                    intent.putExtra("swm_infra_details_id",(assetDetailsList.get(position).getSwm_infra_details_id()));
                    intent.putExtra("pvcoe",(assetDetailsList.get(position).getPvCode()));
                }
                else {
                    intent.putExtra("OnOffType","Online");
                    intent.putExtra("pvcode",(assetDetailsList.get(position).getPvCode()));
                    intent.putExtra("swm_infra_details_id",(assetDetailsList.get(position).getSwm_infra_details_id()));
                    intent.putExtra("swm_asset_type_id",(assetDetailsList.get(position).getSwm_asset_type_id()));
                    intent.putExtra("swm_infra_assets_id",(assetDetailsList.get(position).getSwm_infra_assets_id()));
                    intent.putExtra("swm_infra_asset_detail_id",(assetDetailsList.get(position).getSwm_infra_asset_detail_id()));

                }

                context.startActivity(intent);
            }
        });
        holder.assetsRecyclerItemBinding.deleteIc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_and_delete_alert(position,"delete");
            }
        });

    }

    @Override
    public int getItemCount() {
        return assetDetailsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private AssetsRecyclerItemBinding assetsRecyclerItemBinding;

        public MyViewHolder(AssetsRecyclerItemBinding Binding) {
            super(Binding.getRoot());
            assetsRecyclerItemBinding = Binding;
        }
    }

    public void deletePending(int position) {
        if(type.equals("Local")) {
            String key_id = String.valueOf(assetDetailsList.get(position).getId());

            int sdsm = db.delete(DBHelper.SWM_ASSET_PHOTOS_TABLE, "id = ? ", new String[]{key_id});
            int sdsm1 = db.delete(DBHelper.SWM_ASSET_DETAILS_TABLE, "id = ? ", new String[]{key_id});
            assetDetailsList.remove(position);
            notifyItemRemoved(position);
            notifyItemChanged(position, assetDetailsList.size());
            Log.d("sdsm", String.valueOf(sdsm));
        }
        else {
            deleteServerData(position);
        }
    }

    public void save_and_delete_alert(int position,String save_delete){
        try {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.alert_dialog);

            TextView text = (TextView) dialog.findViewById(R.id.tv_message);
            if(save_delete.equals("save")) {
                text.setText(context.getResources().getString(R.string.do_you_wnat_to_upload));
            }
            else if(save_delete.equals("delete")){
                text.setText(context.getResources().getString(R.string.do_you_wnat_to_delete));
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
                        dialog.dismiss();
                    }
                    else if(save_delete.equals("delete")) {
                        deletePending(position);
                        dialog.dismiss();
                    }
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void deleteServerData(int position) {
        String swm_infra_details_id= assetDetailsList.get(position).getSwm_infra_details_id();
        String swm_asset_type_id=assetDetailsList.get(position).getSwm_asset_type_id();
        String swm_infra_assets_id=assetDetailsList.get(position).getSwm_infra_assets_id();
        String swm_infra_asset_detail_id= assetDetailsList.get(position).getSwm_infra_asset_detail_id();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(AppConstant.KEY_SERVICE_ID,"swm_infra_assets_delete");
            jsonObject.put("swm_infra_details_id",swm_infra_details_id);
            jsonObject.put("swm_asset_type_id",swm_asset_type_id);
            jsonObject.put("swm_infra_assets_id",swm_infra_assets_id);
            jsonObject.put("swm_infra_asset_detail_id",swm_infra_asset_detail_id);

            ((SwmAddAssetDetails)context).DeleteAssetData(jsonObject);

        }
        catch (JSONException e){
            e.printStackTrace();
        }

    }


}

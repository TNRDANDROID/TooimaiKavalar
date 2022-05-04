package com.nic.thooimaikaavalar.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.nic.thooimaikaavalar.activity.SWMActivity.AddCarriedOutsScreen;
import com.nic.thooimaikaavalar.activity.SWMActivity.Add_ViewWasteDumpDetails;
import com.nic.thooimaikaavalar.activity.SWMActivity.SwmDashboard;
import com.nic.thooimaikaavalar.constant.AppConstant;
import com.nic.thooimaikaavalar.dataBase.DBHelper;
import com.nic.thooimaikaavalar.dataBase.dbData;
import com.nic.thooimaikaavalar.databinding.SwmMasterBasicDetailsAdapterBinding;
import com.nic.thooimaikaavalar.model.RealTimeMonitoringSystem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class SwmMasterDetailsAdapter extends RecyclerView.Adapter<SwmMasterDetailsAdapter.MyViewHolder> {
    private LayoutInflater layoutInflater;
    private List<RealTimeMonitoringSystem> basicDetailsList;
    Context context;
    int pos=-1;
    String type;
    String which_flag;
    private com.nic.thooimaikaavalar.dataBase.dbData dbData;
    public  DBHelper dbHelper;
    public SQLiteDatabase db;

    public SwmMasterDetailsAdapter(List<RealTimeMonitoringSystem> capacityList, Context context,dbData dbData,String type,String which_flag) {
        this.basicDetailsList = capacityList;
        this.context = context;
        this.type=type;
        this.dbData=dbData;
        this.which_flag=which_flag;

        try {
            dbHelper = new DBHelper(context);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public SwmMasterDetailsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        SwmMasterBasicDetailsAdapterBinding swmMasterBasicDetailsAdapterBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.swm_master_basic_details_adapter, viewGroup, false);
        return new MyViewHolder(swmMasterBasicDetailsAdapterBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final SwmMasterDetailsAdapter.MyViewHolder holder, final int position) {
        holder.swmMasterBasicDetailsAdapterBinding.pvName.setText(basicDetailsList.get(position).getPvName());
        holder.swmMasterBasicDetailsAdapterBinding.workersAllocated.setText(context.getResources().getString(R.string.workers_allocated)+" "+basicDetailsList.get(position).getNo_of_thooimai_kaavalars_allocated());
        holder.swmMasterBasicDetailsAdapterBinding.workersWorking.setText(context.getResources().getString(R.string.workers_working)+" "+basicDetailsList.get(position).getNo_of_thooimai_kaavalars_working());
        if(basicDetailsList.get(position).getWhether_community_compost_pit_available_in_panchayat().equals("Y")){
            holder.swmMasterBasicDetailsAdapterBinding.wccPit.setText(context.getResources().getString(R.string.wcc_pit)+" "+context.getResources().getString(R.string.yes));
        }
        else {
            holder.swmMasterBasicDetailsAdapterBinding.wccPit.setText(context.getResources().getString(R.string.wcc_pit)+" "+context.getResources().getString(R.string.no));
        }
        if(basicDetailsList.get(position).getWhether_vermi_compost_pit_available_in_panchayat().equals("Y")){
            holder.swmMasterBasicDetailsAdapterBinding.wvcPit.setText(context.getResources().getString(R.string.wvc_pit)+" "+context.getResources().getString(R.string.yes));

        }
        else {
            holder.swmMasterBasicDetailsAdapterBinding.wvcPit.setText(context.getResources().getString(R.string.wvc_pit)+" "+context.getResources().getString(R.string.no));

        }
        if(basicDetailsList.get(position).getAny_integrated_nuesery_devlp_near_swm_facility().equals("Y")){
            holder.swmMasterBasicDetailsAdapterBinding.nurseryPit.setText(context.getResources().getString(R.string.nursery_developed)+" "+context.getResources().getString(R.string.yes));
        }
        else {
            holder.swmMasterBasicDetailsAdapterBinding.nurseryPit.setText(context.getResources().getString(R.string.nursery_developed)+" "+context.getResources().getString(R.string.no));
        }



        if(type.equals("Local")) {
            holder.swmMasterBasicDetailsAdapterBinding.uploadIcon.setVisibility(View.VISIBLE);
            holder.swmMasterBasicDetailsAdapterBinding.deleteIcon.setVisibility(View.VISIBLE);
            holder.swmMasterBasicDetailsAdapterBinding.uploadIcon.setImageResource(R.drawable.ic_upload_icon);
            holder.swmMasterBasicDetailsAdapterBinding.deleteIcon.setImageResource(R.drawable.ic_delete_black_24dp);
            holder.swmMasterBasicDetailsAdapterBinding.serverSideLayout.setVisibility(View.GONE);
        }
        else {
            if(which_flag.equals("Carried_Out")){
                holder.swmMasterBasicDetailsAdapterBinding.linearLayout.setVisibility(View.GONE);
                holder.swmMasterBasicDetailsAdapterBinding.carriedOutLayout.setVisibility(View.VISIBLE);
            }
            else {
                holder.swmMasterBasicDetailsAdapterBinding.linearLayout.setVisibility(View.VISIBLE);
                holder.swmMasterBasicDetailsAdapterBinding.view2.setVisibility(View.GONE);
                holder.swmMasterBasicDetailsAdapterBinding.carriedOutLayout.setVisibility(View.GONE);
            }
            holder.swmMasterBasicDetailsAdapterBinding.uploadIcon.setVisibility(View.GONE);
            holder.swmMasterBasicDetailsAdapterBinding.deleteIcon.setVisibility(View.GONE);
            holder.swmMasterBasicDetailsAdapterBinding.uploadIcon.setImageResource(R.drawable.ic_visibility_grey_900_24dp);
            holder.swmMasterBasicDetailsAdapterBinding.deleteIcon.setImageResource(R.drawable.ic_mode_edit_black_24dp);
            holder.swmMasterBasicDetailsAdapterBinding.serverSideLayout.setVisibility(View.VISIBLE);
        }

        holder.swmMasterBasicDetailsAdapterBinding.deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(type.equals("Local")) {
                    save_and_delete_alert(position, "delete");
                }
                else {
                    save_and_delete_alert(position,"edit");
                }

            }
        });
        holder.swmMasterBasicDetailsAdapterBinding.uploadIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(type.equals("Local")) {
                    save_and_delete_alert(position, "save");
                }

            }
        });
        holder.swmMasterBasicDetailsAdapterBinding.assetLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoSwmDashBoard(position);
            }
        });
        holder.swmMasterBasicDetailsAdapterBinding.wasteDumpLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoWasteDumb(position);
            }
        });
        holder.swmMasterBasicDetailsAdapterBinding.carriedOutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoCarriedOut(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return basicDetailsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private SwmMasterBasicDetailsAdapterBinding swmMasterBasicDetailsAdapterBinding;

        public MyViewHolder(SwmMasterBasicDetailsAdapterBinding Binding) {
            super(Binding.getRoot());
            swmMasterBasicDetailsAdapterBinding = Binding;
        }
    }

    public Bitmap stringtoBitmap(String encodedImage){
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    public void gotoSwmDashBoard(int pos){
        Intent intent = new Intent(context, SwmDashboard.class);
        intent.putExtra("swm_infra_details_id",basicDetailsList.get(pos).getSwm_infra_details_id());
        context.startActivity(intent);

    }
    public void gotoWasteDumb(int pos){
        Intent intent = new Intent(context, Add_ViewWasteDumpDetails.class);
        intent.putExtra("swm_infra_details_id",basicDetailsList.get(pos).getSwm_infra_details_id());
        intent.putExtra("no_of_thooimai_kaavalars_allocated",basicDetailsList.get(pos).getNo_of_thooimai_kaavalars_allocated());
        intent.putExtra("no_of_thooimai_kaavalars_working",basicDetailsList.get(pos).getNo_of_thooimai_kaavalars_working());
        intent.putExtra("whether_community_compost_pit_available_in_panchayat",basicDetailsList.get(pos).getWhether_community_compost_pit_available_in_panchayat());
        intent.putExtra("whether_vermi_compost_pit_available_in_panchayat",basicDetailsList.get(pos).getWhether_vermi_compost_pit_available_in_panchayat());
        intent.putExtra("any_integrated_nuesery_devlp_near_swm_facility",basicDetailsList.get(pos).getAny_integrated_nuesery_devlp_near_swm_facility());
        intent.putExtra("is_plastic_connected_to_waste_management_unit",basicDetailsList.get(pos).getIs_plastic_connected_to_waste_management_unit());
        context.startActivity(intent);

    }
    public void gotoCarriedOut(int pos){
        Intent intent = new Intent(context, AddCarriedOutsScreen.class);
        intent.putExtra("swm_infra_details_id",basicDetailsList.get(pos).getSwm_infra_details_id());
        intent.putExtra("no_of_thooimai_kaavalars_allocated",basicDetailsList.get(pos).getNo_of_thooimai_kaavalars_allocated());
        intent.putExtra("no_of_thooimai_kaavalars_working",basicDetailsList.get(pos).getNo_of_thooimai_kaavalars_working());
        intent.putExtra("whether_community_compost_pit_available_in_panchayat",basicDetailsList.get(pos).getWhether_community_compost_pit_available_in_panchayat());
        intent.putExtra("whether_vermi_compost_pit_available_in_panchayat",basicDetailsList.get(pos).getWhether_vermi_compost_pit_available_in_panchayat());
        intent.putExtra("any_integrated_nuesery_devlp_near_swm_facility",basicDetailsList.get(pos).getAny_integrated_nuesery_devlp_near_swm_facility());
        intent.putExtra("is_plastic_connected_to_waste_management_unit",basicDetailsList.get(pos).getIs_plastic_connected_to_waste_management_unit());
        context.startActivity(intent);

    }

    public void deletePending(int position) {
        if(type.equals("Local")) {
            String key_id = String.valueOf(basicDetailsList.get(position).getId());

            int sdsm = db.delete(DBHelper.SWM_MASTER_DETAILS_TABLE, "id = ? ", new String[]{key_id});
            basicDetailsList.remove(position);
            notifyItemRemoved(position);
            notifyItemChanged(position, basicDetailsList.size());
            Log.d("sdsm", String.valueOf(sdsm));
        }
        else {

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
            else if(save_delete.equals("edit")){
                text.setText(context.getResources().getString(R.string.do_you_want_to_edit));
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
                        uploadPending(position);
                        dialog.dismiss();
                    }
                    else if(save_delete.equals("delete")) {
                        deletePending(position);
                        dialog.dismiss();
                    }
                    else if(save_delete.equals("edit")) {
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
    public void uploadPending(int position) {
        dbData.open();

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(AppConstant.KEY_SERVICE_ID,"details_of_swm_infra_save");
            if(basicDetailsList.get(position).getPvCode().equals("")){
                jsonObject.put("swm_infra_details_id","");
            }
            else {
                jsonObject.put("swm_infra_details_id",basicDetailsList.get(position).getSwm_infra_details_id());
            }
            jsonObject.put("pvcode",basicDetailsList.get(position).getPvCode());
            jsonObject.put("dcode",basicDetailsList.get(position).getDistictCode());
            jsonObject.put("bcode",basicDetailsList.get(position).getBlockCode());
            jsonObject.put("no_of_thooimai_kaavalars_allocated",basicDetailsList.get(position).getNo_of_thooimai_kaavalars_allocated());
            jsonObject.put("no_of_thooimai_kaavalars_working",basicDetailsList.get(position).getNo_of_thooimai_kaavalars_working());
            jsonObject.put("whether_community_compost_pit_available_in_panchayat",basicDetailsList.get(position).getWhether_community_compost_pit_available_in_panchayat());
            jsonObject.put("whether_vermi_compost_pit_available_in_panchayat",basicDetailsList.get(position).getWhether_vermi_compost_pit_available_in_panchayat());
            jsonObject.put("any_integrated_nuesery_devlp_near_swm_facility",basicDetailsList.get(position).getAny_integrated_nuesery_devlp_near_swm_facility());
            jsonObject.put("is_plastic_connected_to_waste_management_unit",basicDetailsList.get(position).getIs_plastic_connected_to_waste_management_unit());

            ((NewPendingScreenActivity)context).SyncData(jsonObject, String.valueOf(basicDetailsList.get(position).getId()),"SWM_Master");

        }
        catch (JSONException e){
            e.printStackTrace();
        }

    }

}


package com.nic.thooimaikaavalar.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nic.thooimaikaavalar.Interface.AdapterCameraIntent;
import com.nic.thooimaikaavalar.R;
import com.nic.thooimaikaavalar.activity.SWMActivity.AddCarriedOutsScreen;
import com.nic.thooimaikaavalar.dataBase.DBHelper;
import com.nic.thooimaikaavalar.databinding.CarriedOutActionAdapterItemsBinding;
import com.nic.thooimaikaavalar.model.RealTimeMonitoringSystem;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CarriedOutWastDumpAdapter extends RecyclerView.Adapter<CarriedOutWastDumpAdapter.MyViewHolder> implements AdapterCameraIntent {
    private LayoutInflater layoutInflater;
    private List<RealTimeMonitoringSystem> wasteDumpList;
    static JSONObject dataset = new JSONObject();
    Context context;
    String type;
    private com.nic.thooimaikaavalar.dataBase.dbData dbData;
    public DBHelper dbHelper;
    public SQLiteDatabase db;

    public CarriedOutWastDumpAdapter(List<RealTimeMonitoringSystem> assetDetailsList, Context context, String type, com.nic.thooimaikaavalar.dataBase.dbData dbData) {
        this.wasteDumpList = assetDetailsList;
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
    public CarriedOutWastDumpAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        CarriedOutActionAdapterItemsBinding carriedOutActionAdapterItemsBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.carried_out_action_adapter_items, viewGroup, false);
        return new MyViewHolder(carriedOutActionAdapterItemsBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CarriedOutWastDumpAdapter.MyViewHolder holder, int position) {

            holder.carriedOutActionAdapterItemsBinding.assetName.setVisibility(View.GONE);
            holder.carriedOutActionAdapterItemsBinding.previewImageLayout.setVisibility(View.VISIBLE);
            holder.carriedOutActionAdapterItemsBinding.status.setText("Yes");
            holder.carriedOutActionAdapterItemsBinding.status.setTextColor(context.getResources().getColor(R.color.account_status_green_color));

            holder.carriedOutActionAdapterItemsBinding.afterImageLat.setText(wasteDumpList.get(position).getAfter_taken_image_lat());
            holder.carriedOutActionAdapterItemsBinding.afterImageLong.setText(wasteDumpList.get(position).getAfter_taken_image_long());

            if(wasteDumpList.get(position).getIs_there_any_waste_dump().equals("Y")){
                holder.carriedOutActionAdapterItemsBinding.beforeImageView.setImageBitmap(wasteDumpList.get(position).getBefore_taken_image());
            }
            else {
                holder.carriedOutActionAdapterItemsBinding.beforeImageView.setImageBitmap(null);
            }
            if(wasteDumpList.get(position).getIs_photo_of_waste_dump_after_action().equals("Y")){
                holder.carriedOutActionAdapterItemsBinding.functionalYes.setChecked(true);
                holder.carriedOutActionAdapterItemsBinding.afterImageLayout.setVisibility(View.VISIBLE);
                holder.carriedOutActionAdapterItemsBinding.afterImageView.setImageBitmap(wasteDumpList.get(position).getAfter_taken_image());
            }
            else if(wasteDumpList.get(position).getIs_photo_of_waste_dump_after_action().equals("")){
                holder.carriedOutActionAdapterItemsBinding.radioGroup.clearCheck();
                holder.carriedOutActionAdapterItemsBinding.afterImageLayout.setVisibility(View.GONE);
                holder.carriedOutActionAdapterItemsBinding.afterImageView.setImageBitmap(null);
                holder.carriedOutActionAdapterItemsBinding.afterImageView.setImageResource(R.drawable.capture_image);
            }
            else {
                holder.carriedOutActionAdapterItemsBinding.functionalNo.setChecked(true);
                holder.carriedOutActionAdapterItemsBinding.afterImageLayout.setVisibility(View.GONE);
                holder.carriedOutActionAdapterItemsBinding.afterImageView.setImageBitmap(null);
                holder.carriedOutActionAdapterItemsBinding.afterImageView.setImageResource(R.drawable.capture_image);
            }

            holder.carriedOutActionAdapterItemsBinding.functionalNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        wasteDumpList.get(position).setIs_photo_of_waste_dump_after_action("N");
                        holder.carriedOutActionAdapterItemsBinding.afterImageLayout.setVisibility(View.GONE);
                        holder.carriedOutActionAdapterItemsBinding.afterImageView.setImageBitmap(null);
                        holder.carriedOutActionAdapterItemsBinding.afterImageView.setImageResource(R.drawable.capture_image);
                        //notifyItemChanged(position);
                    }
                }
            });
            holder.carriedOutActionAdapterItemsBinding.functionalYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        holder.carriedOutActionAdapterItemsBinding.afterImageLayout.setVisibility(View.VISIBLE);
                        holder.carriedOutActionAdapterItemsBinding.afterImageView.setImageBitmap(null);
                        holder.carriedOutActionAdapterItemsBinding.afterImageView.setImageResource(R.drawable.capture_image);
                    }
                }
            });



            holder.carriedOutActionAdapterItemsBinding.afterImageLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((AddCarriedOutsScreen)context).getLatLong(position);
                }
            });

    }

    @Override
    public int getItemCount() {
        return wasteDumpList.size();
    }

    @Override
    public void OnIntentListener(Intent data, int result_code, int position,String after_image_lat,String after_image_long) {
       if(data!=null){
           Bitmap photo= (Bitmap) data.getExtras().get("data");
           RealTimeMonitoringSystem realTimeMonitoringSystem = new RealTimeMonitoringSystem();
           realTimeMonitoringSystem.setDistictCode(wasteDumpList.get(position).getDistictCode());
           realTimeMonitoringSystem.setBlockCode(wasteDumpList.get(position).getBlockCode());
           realTimeMonitoringSystem.setPvCode(wasteDumpList.get(position).getPvCode());
           realTimeMonitoringSystem.setSwm_infra_details_id(wasteDumpList.get(position).getSwm_infra_details_id());
           realTimeMonitoringSystem.setSwm_waste_dump_photos_id(wasteDumpList.get(position).getSwm_waste_dump_photos_id());
           realTimeMonitoringSystem.setIs_there_any_waste_dump("Y");
           realTimeMonitoringSystem.setBefore_taken_image(wasteDumpList.get(position).getBefore_taken_image());
           realTimeMonitoringSystem.setIs_photo_of_waste_dump_after_action("Y");
           realTimeMonitoringSystem.setAfter_taken_image(photo);
           realTimeMonitoringSystem.setAfter_taken_image_lat(after_image_lat);
           realTimeMonitoringSystem.setAfter_taken_image_long(after_image_long);
           wasteDumpList.set(position,realTimeMonitoringSystem);
           notifyItemChanged(position);

       }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CarriedOutActionAdapterItemsBinding carriedOutActionAdapterItemsBinding;

        public MyViewHolder(CarriedOutActionAdapterItemsBinding Binding) {
            super(Binding.getRoot());
            carriedOutActionAdapterItemsBinding = Binding;
        }
    }


    public ArrayList<RealTimeMonitoringSystem> getTheCarriedList(){
        ArrayList<RealTimeMonitoringSystem> arrayList = new ArrayList<>();
        arrayList = (ArrayList<RealTimeMonitoringSystem>) wasteDumpList;
        return arrayList;
    }


}



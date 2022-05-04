package com.nic.thooimaikaavalar.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nic.thooimaikaavalar.ImageZoom.ImageMatrixTouchHandler;
import com.nic.thooimaikaavalar.Interface.AdapterCameraIntent;
import com.nic.thooimaikaavalar.R;
import com.nic.thooimaikaavalar.activity.SWMActivity.AddCarriedOutsScreen;
import com.nic.thooimaikaavalar.dataBase.DBHelper;
import com.nic.thooimaikaavalar.databinding.CarriedOutActionAdapterItemsBinding;
import com.nic.thooimaikaavalar.model.RealTimeMonitoringSystem;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ClearedWasteDumbAdapter extends RecyclerView.Adapter<ClearedWasteDumbAdapter.MyViewHolder> {
    private LayoutInflater layoutInflater;
    private List<RealTimeMonitoringSystem> wasteDumpList;
    static JSONObject dataset = new JSONObject();
    Context context;
    String type;
    private com.nic.thooimaikaavalar.dataBase.dbData dbData;
    public DBHelper dbHelper;
    public SQLiteDatabase db;

    public ClearedWasteDumbAdapter(List<RealTimeMonitoringSystem> assetDetailsList, Context context, String type, com.nic.thooimaikaavalar.dataBase.dbData dbData) {
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
    public ClearedWasteDumbAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        CarriedOutActionAdapterItemsBinding carriedOutActionAdapterItemsBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.carried_out_action_adapter_items, viewGroup, false);
        return new MyViewHolder(carriedOutActionAdapterItemsBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ClearedWasteDumbAdapter.MyViewHolder holder, int position) {

        holder.carriedOutActionAdapterItemsBinding.assetName.setVisibility(View.GONE);
        holder.carriedOutActionAdapterItemsBinding.previewImageLayout.setVisibility(View.VISIBLE);
        holder.carriedOutActionAdapterItemsBinding.status.setText(context.getResources().getString(R.string.yes));
        holder.carriedOutActionAdapterItemsBinding.status.setTextColor(context.getResources().getColor(R.color.account_status_green_color));

        holder.carriedOutActionAdapterItemsBinding.afterImageLat.setText(wasteDumpList.get(position).getAfter_taken_image_lat());
        holder.carriedOutActionAdapterItemsBinding.afterImageLong.setText(wasteDumpList.get(position).getAfter_taken_image_long());

        holder.carriedOutActionAdapterItemsBinding.functionalYes.setEnabled(false);
        holder.carriedOutActionAdapterItemsBinding.functionalNo.setEnabled(false);
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

        holder.carriedOutActionAdapterItemsBinding.afterImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExpandedImage(holder.carriedOutActionAdapterItemsBinding.afterImageView.getDrawable());
            }
        });
        holder.carriedOutActionAdapterItemsBinding.beforeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExpandedImage(holder.carriedOutActionAdapterItemsBinding.beforeImageView.getDrawable());
            }
        });


    }

    @Override
    public int getItemCount() {
        return wasteDumpList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CarriedOutActionAdapterItemsBinding carriedOutActionAdapterItemsBinding;

        public MyViewHolder(CarriedOutActionAdapterItemsBinding Binding) {
            super(Binding.getRoot());
            carriedOutActionAdapterItemsBinding = Binding;
        }
    }




    private void ExpandedImage(Drawable profile) {
        try {
            //We need to get the instance of the LayoutInflater, use the context of this activity
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //Inflate the view from a predefined XML layout
            View ImagePopupLayout = inflater.inflate(R.layout.image_fullscreen_preview, null);

            ImageView zoomImage = (ImageView) ImagePopupLayout.findViewById(R.id.image_preview);
            zoomImage.setImageDrawable(profile);

            ImageMatrixTouchHandler imageMatrixTouchHandler = new ImageMatrixTouchHandler(context);
            zoomImage.setOnTouchListener(imageMatrixTouchHandler);
//            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext, R.style.MyDialogTheme);
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
            dialogBuilder.setView(ImagePopupLayout);

            final AlertDialog alert = dialogBuilder.create();
            alert.getWindow().getAttributes().windowAnimations = R.style.dialog_animation_zoomInOut;
            alert.show();
            alert.getWindow().setBackgroundDrawableResource(R.color.full_transparent);
            alert.setCanceledOnTouchOutside(true);

            zoomImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}




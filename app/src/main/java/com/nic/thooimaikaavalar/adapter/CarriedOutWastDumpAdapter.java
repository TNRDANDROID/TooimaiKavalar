package com.nic.thooimaikaavalar.adapter;

import android.app.Dialog;
import android.content.Context;
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
import com.nic.thooimaikaavalar.activity.SWMActivity.Add_ViewWasteDumpDetails;
import com.nic.thooimaikaavalar.constant.AppConstant;
import com.nic.thooimaikaavalar.dataBase.DBHelper;
import com.nic.thooimaikaavalar.databinding.AssetsRecyclerItemBinding;
import com.nic.thooimaikaavalar.model.RealTimeMonitoringSystem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class CarriedOutWastDumpAdapter extends RecyclerView.Adapter<CarriedOutWastDumpAdapter.MyViewHolder> {
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
        AssetsRecyclerItemBinding assetsRecyclerItemBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.assets_recycler_item, viewGroup, false);
        return new MyViewHolder(assetsRecyclerItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CarriedOutWastDumpAdapter.MyViewHolder holder, int position) {


        if(type.equals("Local")){
            holder.assetsRecyclerItemBinding.assetName.setVisibility(View.GONE);
            holder.assetsRecyclerItemBinding.ques.setText("Is There Any Waste Dump?");
            holder.assetsRecyclerItemBinding.previewImageLayout.setVisibility(View.GONE);

            if(wasteDumpList.get(position).getIs_there_any_waste_dump().equals("Y")){
                holder.assetsRecyclerItemBinding.ans.setText("Yes");
                holder.assetsRecyclerItemBinding.ans.setTextColor(Color.GREEN);
                holder.assetsRecyclerItemBinding.status.setText("Yes");
                holder.assetsRecyclerItemBinding.status.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.circlegreen));
            }
            else {
                holder.assetsRecyclerItemBinding.ans.setText("NO");
                holder.assetsRecyclerItemBinding.ans.setTextColor(Color.RED);
                holder.assetsRecyclerItemBinding.status.setText("No");
                holder.assetsRecyclerItemBinding.status.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.curved_red_bg));
            }
        }
        else {
            holder.assetsRecyclerItemBinding.assetName.setVisibility(View.GONE);
            holder.assetsRecyclerItemBinding.ques.setText("Is There Any Waste Dump?");
            holder.assetsRecyclerItemBinding.previewImageLayout.setVisibility(View.VISIBLE);

            holder.assetsRecyclerItemBinding.ans.setText("Yes");
            holder.assetsRecyclerItemBinding.ans.setTextColor(Color.GREEN);
            holder.assetsRecyclerItemBinding.status.setText("Yes");
            holder.assetsRecyclerItemBinding.status.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.circlegreen));

            holder.assetsRecyclerItemBinding.srcImageView.setImageBitmap(wasteDumpList.get(position).getImage());

        }



        holder.assetsRecyclerItemBinding.imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        return wasteDumpList.size();
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
            String key_id = String.valueOf(wasteDumpList.get(position).getId());
            int sdsm = db.delete(DBHelper.SWM_WASTE_DUMP_PHOTOS_DETAILS, "id = ? ", new String[]{key_id});
            int count = dbData.gettableCountWasteDumpTable();
            if(count==0){
            }
            wasteDumpList.remove(position);
            notifyItemRemoved(position);
            notifyItemChanged(position, wasteDumpList.size());
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
                        dialog.dismiss();
                    }
                    else if(save_delete.equals("delete")) {
                        //deletePending(position);
                        dialog.dismiss();
                    }
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}



package com.nic.thooimaikaavalar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nic.thooimaikaavalar.R;
import com.nic.thooimaikaavalar.activity.NewMainPage;
import com.nic.thooimaikaavalar.activity.SWMActivity.SwmDashboard;
import com.nic.thooimaikaavalar.activity.ViewTakeEditComponentsPhots;
import com.nic.thooimaikaavalar.databinding.CapacityRecylerAdapterBinding;
import com.nic.thooimaikaavalar.databinding.ComponentsAdapterBinding;
import com.nic.thooimaikaavalar.model.RealTimeMonitoringSystem;

import java.util.List;

public class ComponentsAdapter extends RecyclerView.Adapter<ComponentsAdapter.MyViewHolder> {
    private LayoutInflater layoutInflater;
    private List<RealTimeMonitoringSystem> capacityList;
    Context context;
    int pos=-1;
    String type;

    public ComponentsAdapter(List<RealTimeMonitoringSystem> capacityList, Context context,String type) {
        this.capacityList = capacityList;
        this.context = context;
        this.type=type;
    }

    @NonNull
    @Override
    public ComponentsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        ComponentsAdapterBinding capacityRecylerAdapterBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.components_adapter, viewGroup, false);
        return new MyViewHolder(capacityRecylerAdapterBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ComponentsAdapter.MyViewHolder holder, final int position) {

        if(type.equals("SWM")){
            holder.capacityRecylerAdapterBinding.valueTv.setText(capacityList.get(position).getAsset_type_name_ta());
        }
        else {
            holder.capacityRecylerAdapterBinding.valueTv.setText(capacityList.get(position).getKEY_photographs_name());
        }


        if(position==pos){
            holder.capacityRecylerAdapterBinding.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimary));

        }
        else {
            holder.capacityRecylerAdapterBinding.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.grey_2));

        }

        holder.capacityRecylerAdapterBinding.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pos=position;
                notifyDataSetChanged();
                if(type.equals("SWM")){
                    ((SwmDashboard)context).assetsAdapterItemClicked(position);
                }
                else {
                    ((ViewTakeEditComponentsPhots) context).componentsAdapterItemClicked(pos);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return capacityList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private ComponentsAdapterBinding capacityRecylerAdapterBinding;

        public MyViewHolder(ComponentsAdapterBinding Binding) {
            super(Binding.getRoot());
            capacityRecylerAdapterBinding = Binding;
        }
    }
}

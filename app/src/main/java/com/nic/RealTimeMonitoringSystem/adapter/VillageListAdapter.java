package com.nic.RealTimeMonitoringSystem.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.databinding.DataBindingUtil;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.nic.RealTimeMonitoringSystem.R;
import com.nic.RealTimeMonitoringSystem.activity.WorkListScreen;
import com.nic.RealTimeMonitoringSystem.constant.AppConstant;
import com.nic.RealTimeMonitoringSystem.databinding.AdapterVillageListBinding;
import com.nic.RealTimeMonitoringSystem.model.RealTimeMonitoringSystem;

import java.util.ArrayList;
import java.util.List;

public class VillageListAdapter extends PagedListAdapter<RealTimeMonitoringSystem,VillageListAdapter.MyViewHolder> implements Filterable {
    private List<RealTimeMonitoringSystem> villageListValues;
    private List<RealTimeMonitoringSystem> villageValuesFiltered;
   private String letter;
   private Context context;
   private ColorGenerator generator = ColorGenerator.MATERIAL;

    private LayoutInflater layoutInflater;
    private static DiffUtil.ItemCallback<RealTimeMonitoringSystem> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<RealTimeMonitoringSystem>() {
                @Override
                public boolean areItemsTheSame(RealTimeMonitoringSystem oldItem, RealTimeMonitoringSystem newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @SuppressLint("DiffUtilEquals")
                @Override
                public boolean areContentsTheSame(RealTimeMonitoringSystem oldItem, RealTimeMonitoringSystem newItem) {
                    return oldItem.equals(newItem);
                }
            };
    public VillageListAdapter(Context context, List<RealTimeMonitoringSystem> villageListValues) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.villageListValues = villageListValues;
        this.villageValuesFiltered = villageListValues;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private AdapterVillageListBinding adapterVillageListBinding;

        public MyViewHolder(AdapterVillageListBinding Binding) {
            super(Binding.getRoot());
            adapterVillageListBinding = Binding;
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        AdapterVillageListBinding adapterVillageListBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.adapter_village_list, viewGroup, false);
        return new MyViewHolder(adapterVillageListBinding);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.adapterVillageListBinding.villageName.setText(villageValuesFiltered.get(position).getPvName());

        letter = String.valueOf(villageValuesFiltered.get(position).getPvName().charAt(0));

        TextDrawable drawable = TextDrawable.builder()
                .buildRound(letter, generator.getRandomColor());

        holder.adapterVillageListBinding.villageFirstLetter.setImageDrawable(drawable);
        holder.adapterVillageListBinding.villageCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHousingWorks(position);
            }
        });
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    villageValuesFiltered = villageListValues;
                } else {
                    List<RealTimeMonitoringSystem> filteredList = new ArrayList<>();
                    for (RealTimeMonitoringSystem row : villageListValues) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getPvName().toLowerCase().contains(charString.toLowerCase()) || row.getPvName().toLowerCase().contains(charString.toUpperCase())) {
                            filteredList.add(row);
                        }
                    }

                    villageValuesFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = villageValuesFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                villageValuesFiltered = (ArrayList<RealTimeMonitoringSystem>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public void viewHousingWorks(int pos) {
        Activity activity = (Activity) context;
        Intent intent = new Intent(activity, WorkListScreen.class);
        intent.putExtra(AppConstant.PV_CODE,villageValuesFiltered.get(pos).getPvCode());
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @Override
    public int getItemCount() {
        return villageValuesFiltered == null ? 0 : villageValuesFiltered.size();
    }
}

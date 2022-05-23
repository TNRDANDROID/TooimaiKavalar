package com.nic.thooimaikaavalar.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nic.thooimaikaavalar.Interface.MultipleSelection;
import com.nic.thooimaikaavalar.R;

import com.nic.thooimaikaavalar.activity.PWMUNIT.PwmUnitSale;
import com.nic.thooimaikaavalar.databinding.PwmUnitListItemViewBinding;
import com.nic.thooimaikaavalar.model.RealTimeMonitoringSystem;


import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

import java.util.Locale;

public class PwmUnitSaleAdapter extends RecyclerView.Adapter<PwmUnitSaleAdapter.MyViewHolder> {
    private LayoutInflater layoutInflater;
    private ArrayList<RealTimeMonitoringSystem> pwmUnitSaleList;
    Context context;
    int pos=-1;
    MultipleSelection multipleSelection;

    public PwmUnitSaleAdapter(Context context,ArrayList<RealTimeMonitoringSystem> pwmUnitSaleList,MultipleSelection multipleSelection) {
        this.pwmUnitSaleList = pwmUnitSaleList;
        this.multipleSelection = multipleSelection;
        this.context = context;
    }

    @NonNull
    @Override
    public PwmUnitSaleAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        PwmUnitListItemViewBinding pwmUnitListItemViewBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.pwm_unit_list_item_view, viewGroup, false);
        return new MyViewHolder(pwmUnitListItemViewBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final PwmUnitSaleAdapter.MyViewHolder holder, final int position) {

        if(pwmUnitSaleList.get(position).getCheck_flag().equalsIgnoreCase("Y")){
            holder.pwmUnitListItemViewBinding.selectIcon.setImageResource(R.drawable.ic_check_with_tick);
            holder.pwmUnitListItemViewBinding.selectIcon.setTag("ic_check_with_tick");

        }
        else {
            holder.pwmUnitListItemViewBinding.selectIcon.setImageResource(R.drawable.ic_oval);
            holder.pwmUnitListItemViewBinding.selectIcon.setTag("ic_oval");
        }
        holder.pwmUnitListItemViewBinding.date.setText(pwmUnitSaleList.get(position).getDate_entry_for());
        holder.pwmUnitListItemViewBinding.quantity.setText(pwmUnitSaleList.get(position).getAmount_of_plastic_waste_sent_to_pwm_unit_in_kg());
        holder.pwmUnitListItemViewBinding.amount.setText(indianMoney(pwmUnitSaleList.get(position).getAmount_of_plastic_waste_sent_to_pwm_unit_revenue_in_rs()));
        holder.pwmUnitListItemViewBinding.checkLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((holder.pwmUnitListItemViewBinding.selectIcon.getTag().equals("ic_oval")))
                {
                        holder.pwmUnitListItemViewBinding.selectIcon.setImageResource(R.drawable.ic_check_with_tick);
                        pwmUnitSaleList.get(position).setCheck_flag("Y");
                        holder.pwmUnitListItemViewBinding.selectIcon.setTag("ic_check_with_tick");
                        multipleSelection.onClickedItems(pwmUnitSaleList);
                        //((PwmUnitSale)context).onItemCLickedViews(pwmUnitSaleList);
                }
                else {
                    holder.pwmUnitListItemViewBinding.selectIcon.setImageResource(R.drawable.ic_oval);
                    holder.pwmUnitListItemViewBinding.selectIcon.setTag("ic_oval");
                    pwmUnitSaleList.get(position).setCheck_flag("N");
                    multipleSelection.onClickedItems(pwmUnitSaleList);
                    //((PwmUnitSale)context).onItemCLickedViews(pwmUnitSaleList);

                }
            }
        });
        holder.pwmUnitListItemViewBinding.pdfIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((PwmUnitSale)context).listItemPdfIconClickAction(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return pwmUnitSaleList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private PwmUnitListItemViewBinding pwmUnitListItemViewBinding;

        public MyViewHolder(PwmUnitListItemViewBinding Binding) {
            super(Binding.getRoot());
            pwmUnitListItemViewBinding = Binding;
        }
    }

    public String indianMoney(String amount){
        String amount_value="";
        try{
            Locale locale = new Locale("en","IN");
            DecimalFormat decimalFormat = (DecimalFormat) DecimalFormat.getCurrencyInstance(locale);
            DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance(locale);
            dfs.setCurrencySymbol("\u20B9");
            decimalFormat.setDecimalFormatSymbols(dfs);
            System.out.println(decimalFormat.format(Integer.parseInt(amount)));
            amount_value = decimalFormat.format(Integer.parseInt(amount));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return amount_value;
    }
}

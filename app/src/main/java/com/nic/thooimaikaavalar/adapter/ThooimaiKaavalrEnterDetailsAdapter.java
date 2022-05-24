package com.nic.thooimaikaavalar.adapter;

import android.app.DatePickerDialog;
import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nic.thooimaikaavalar.R;
import com.nic.thooimaikaavalar.activity.NewMainPage;
import com.nic.thooimaikaavalar.databinding.NoOfThooimaiKaavalarAdapterBinding;
import com.nic.thooimaikaavalar.databinding.ThooimaiKavaalarDetailsAddLayoutAdapterBinding;
import com.nic.thooimaikaavalar.model.RealTimeMonitoringSystem;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ThooimaiKaavalrEnterDetailsAdapter extends RecyclerView.Adapter<ThooimaiKaavalrEnterDetailsAdapter.MyViewHolder> {
    private LayoutInflater layoutInflater;
    int counts;
    int Pos=-1;
    Context context;
    ArrayList<RealTimeMonitoringSystem> thooimaiKaavalarList;

    public ThooimaiKaavalrEnterDetailsAdapter(int counts, Context context,ArrayList<RealTimeMonitoringSystem> thooimaiKaavalarList) {
        this.counts =counts;
        this.context = context;
        this.thooimaiKaavalarList = thooimaiKaavalarList;
    }

    @NonNull
    @Override
    public ThooimaiKaavalrEnterDetailsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        ThooimaiKavaalarDetailsAddLayoutAdapterBinding thooimaiKavaalarDetailsAddLayoutAdapterBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.thooimai_kavaalar_details_add_layout_adapter, viewGroup, false);
        return new MyViewHolder(thooimaiKavaalarDetailsAddLayoutAdapterBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ThooimaiKaavalrEnterDetailsAdapter.MyViewHolder holder, final int position) {

        holder.thooimaiKavaalarDetailsAddLayoutAdapterBinding.dateOfEngage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                String date = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                                //dateFormate(date);
                                holder.thooimaiKavaalarDetailsAddLayoutAdapterBinding.dateOfEngage.setText(dateFormate(date,""));
                                thooimaiKaavalarList.get(position).setDate_of_engagement(holder.thooimaiKavaalarDetailsAddLayoutAdapterBinding.dateOfEngage.getText().toString());

                            }
                        }, year, month, day);
                datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
                datePickerDialog.show();
            }
        });
        holder.thooimaiKavaalarDetailsAddLayoutAdapterBinding.dateOfTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                String date = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                                //dateFormate(date);
                                holder.thooimaiKavaalarDetailsAddLayoutAdapterBinding.dateOfTraining.setText(dateFormate(date,""));
                                thooimaiKaavalarList.get(position).setDate_of_training_given(holder.thooimaiKavaalarDetailsAddLayoutAdapterBinding.dateOfTraining.getText().toString());

                            }
                        }, year, month, day);
                datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
                datePickerDialog.show();
            }
        });
        holder.thooimaiKavaalarDetailsAddLayoutAdapterBinding.name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                thooimaiKaavalarList.get(position).setName_of_the_thooimai_kaavalars(holder.thooimaiKavaalarDetailsAddLayoutAdapterBinding.name.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        holder.thooimaiKavaalarDetailsAddLayoutAdapterBinding.mobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                thooimaiKaavalarList.get(position).setMobile_no(holder.thooimaiKavaalarDetailsAddLayoutAdapterBinding.mobileNumber.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        holder.thooimaiKavaalarDetailsAddLayoutAdapterBinding.name.setText(""+thooimaiKaavalarList.get(position).getName_of_the_thooimai_kaavalars());
        holder.thooimaiKavaalarDetailsAddLayoutAdapterBinding.mobileNumber.setText(""+thooimaiKaavalarList.get(position).getMobile_no());
        holder.thooimaiKavaalarDetailsAddLayoutAdapterBinding.dateOfEngage.setText(""+thooimaiKaavalarList.get(position).getDate_of_engagement());
        holder.thooimaiKavaalarDetailsAddLayoutAdapterBinding.dateOfTraining.setText(""+thooimaiKaavalarList.get(position).getDate_of_training_given());
    }

    @Override
    public int getItemCount() {
        return thooimaiKaavalarList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private ThooimaiKavaalarDetailsAddLayoutAdapterBinding thooimaiKavaalarDetailsAddLayoutAdapterBinding;

        public MyViewHolder(ThooimaiKavaalarDetailsAddLayoutAdapterBinding Binding) {
            super(Binding.getRoot());
            thooimaiKavaalarDetailsAddLayoutAdapterBinding = Binding;
        }
    }

    public  String dateFormate( String strDate,String type ){
        try {
            SimpleDateFormat sdfSource =null;
            if(type.equals("")) {
                // create SimpleDateFormat object with source string date format
                sdfSource = new SimpleDateFormat(
                        "dd-M-yyyy");
            }
            else {
                sdfSource = new SimpleDateFormat(
                        "yyyy-mm-dd");
            }

            // parse the string into Date object
            Date date = sdfSource.parse(strDate);

            // create SimpleDateFormat object with desired date format
            SimpleDateFormat sdfDestination = new SimpleDateFormat(
                    "dd-MM-yyyy");

            // parse the date into another format
            strDate = sdfDestination.format(date);

           /* System.out
                    .println("Date is converted from yyyy-MM-dd'T'hh:mm:ss'.000Z' format to dd/MM/yyyy, ha");
            System.out.println("Converted date is : " + strDate.toLowerCase());
*/
        }
        catch (ParseException pe) {
            System.out.println("Parse Exception : " + pe);
        }
        return strDate;
    }

    public ArrayList<RealTimeMonitoringSystem> getAllList(){
        return thooimaiKaavalarList;
    }


}

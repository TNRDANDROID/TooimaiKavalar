package com.nic.thooimaikaavalar.activity.PWMUNIT;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.nic.thooimaikaavalar.Interface.MultipleSelection;
import com.nic.thooimaikaavalar.R;
import com.nic.thooimaikaavalar.activity.SWMActivity.AddCarriedOutsScreen;
import com.nic.thooimaikaavalar.activity.ViewWasteCollectedDetails;
import com.nic.thooimaikaavalar.adapter.PwmUnitSaleAdapter;
import com.nic.thooimaikaavalar.adapter.WasteCollectedAdapterServer;
import com.nic.thooimaikaavalar.api.Api;
import com.nic.thooimaikaavalar.api.ApiService;
import com.nic.thooimaikaavalar.api.ServerResponse;
import com.nic.thooimaikaavalar.constant.AppConstant;
import com.nic.thooimaikaavalar.databinding.ActivityPwmUnitSaleBinding;
import com.nic.thooimaikaavalar.model.RealTimeMonitoringSystem;
import com.nic.thooimaikaavalar.session.PrefManager;
import com.nic.thooimaikaavalar.utils.CameraUtils;
import com.nic.thooimaikaavalar.utils.DateInterface;
import com.nic.thooimaikaavalar.utils.UrlGenerator;
import com.nic.thooimaikaavalar.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class PwmUnitSale extends AppCompatActivity implements Api.ServerResponseListener, DateInterface, MultipleSelection {
    ActivityPwmUnitSaleBinding pwmUnitSaleBinding;
    PrefManager prefManager;
    String swm_infra_details_id;
    String fromDate,toDate;
    ArrayList<RealTimeMonitoringSystem> pwmUnitList;
    ArrayList<RealTimeMonitoringSystem> clickedList;
    PwmUnitSaleAdapter pwmUnitSaleAdapter;
    ArrayList<Integer> swm_activity_carried_out_id_list;


    //////**************  **********File Selection PDF
    private static final int FILE_SELECT_CODE = 0;
    private static final int MY_REQUEST_CODE_PERMISSION = 1000;
    Dialog dialog;
    int pageNumber;
    Uri uri;
    File myFile;
    String displayName = "";
    private String fileString = "";
    private byte[] bytes;
    private String uriString;
    private String fileSize = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        pwmUnitSaleBinding = DataBindingUtil.setContentView(this, R.layout.activity_pwm_unit_sale);
        pwmUnitSaleBinding.setActivity(this);

        prefManager = new PrefManager(this);
        Utils.setLocale("ta",this);
        swm_infra_details_id = getIntent().getStringExtra("swm_infra_details_id");

        pwmUnitSaleBinding.date.setText(Utils.getCurrentDate()+" to "+Utils.getCurrentDate());
        if(Utils.isOnline()){
            get_details_of_pwm_unit_sale_view("Today");
        }
        else {
            onBackPressed();
        }

        pwmUnitSaleBinding.selectedItemValueVisibleLayout.setVisibility(View.GONE);
        pwmUnitSaleBinding.pdfIcon.setVisibility(View.GONE);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getApplicationContext(),DividerItemDecoration.VERTICAL);
        pwmUnitSaleBinding.pwmCollectionRecycler.addItemDecoration(itemDecoration);
        pwmUnitSaleBinding.pwmCollectionRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        pwmUnitSaleBinding.chooseDateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.showDatePickerDialog(PwmUnitSale.this);
            }
        });
        pwmUnitSaleBinding.backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        pwmUnitSaleBinding.pdfImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPermissions()){
                    showFileChooser();
                }
            }
        });
        pwmUnitSaleBinding.pdfIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPermissions()){
                    viewPdf(fileString);

                }
            }
        });
        pwmUnitSaleBinding.submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(!pwmUnitSaleBinding.quantityText.getText().toString().equals("")){
                    if(!pwmUnitSaleBinding.amountText.getText().toString().equals("")) {
                        if(!pwmUnitSaleBinding.agencyNameText.getText().toString().equals("")){
                            if(!fileString.equals("")){
                                if(Utils.isOnline()){
                                    plastic_waste_management_unit_save();
                                }
                                else {
                                    Utils.showAlert(PwmUnitSale.this,getResources().getString(R.string.no_internet));
                                }
                            }
                            else {
                                Utils.showAlert(PwmUnitSale.this,"Please Attach Your Cheque");
                            }
                        }
                        else {
                            Utils.showAlert(PwmUnitSale.this,"Please Enter Agency Name");
                        }
                    }
                   else {
                        Utils.showAlert(PwmUnitSale.this,"Please Enter Amount");
                   }
               }
               else {
                   Utils.showAlert(PwmUnitSale.this,"Please Choose the List");
               }
            }
        });
    }
    public void get_details_of_pwm_unit_sale_view(String type) {
        try {
            new ApiService(this).makeJSONObjectRequest("plastic_waste_management_unit_list", Api.Method.POST, UrlGenerator.getWorkListUrl(), details_of_pwm_unit_sale_viewParams(type), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject details_of_pwm_unit_sale_viewParams(String type) throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), details_of_pwm_unit_sale_viewJsonParams(type).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("JSONRequest", "" + authKey);
        return dataSet;
    }

    private JSONObject details_of_pwm_unit_sale_viewJsonParams(String type) {
        JSONObject dataSet = new JSONObject();
        try {

            dataSet.put(AppConstant.KEY_SERVICE_ID, "plastic_waste_management_unit_list");
            dataSet.put("swm_infra_details_id", swm_infra_details_id);
            if(type.equals("Today")){
                dataSet.put("from_date",Utils.getCurrentDate());
                dataSet.put("to_date",Utils.getCurrentDate());
            }
            else {
                //dataSet.put("date",activity_view_waste_collected_details.dateOfSave.getText().toString());
                dataSet.put("from_date",fromDate);
                dataSet.put("to_date",toDate);
            }

        }catch (JSONException e){
            e.printStackTrace();
        }
        Log.d("JSONRequest",dataSet.toString());
        return dataSet;
    }
    @SuppressLint("CheckResult")
    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        try {
            String urlType = serverResponse.getApi();
            JSONObject responseObj = serverResponse.getJsonResponse();

            if ("plastic_waste_management_unit_list".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new Insert_plastic_waste_management_unit_list().execute(jsonObject);
                }
                else {
                    pwmUnitSaleBinding.pwmCollectionRecycler.setVisibility(View.GONE);
                }
                Log.d("plastic_waste_management_unit_list", "" + responseDecryptedBlockKey);
            }
            if ("plastic_waste_management_unit_save".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    Toasty.success(this, jsonObject.getString("MESSAGE"), Toast.LENGTH_LONG, true).show();
                    get_details_of_pwm_unit_sale_view("");
                }
                else {
                    Toasty.error(this, jsonObject.getString("MESSAGE"), Toast.LENGTH_LONG, true).show();
                }
                Log.d("plastic_waste_management_unit_list", "" + responseDecryptedBlockKey);
            }
            if ("get_carried_out_receipt".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    JSONArray jsonArray = jsonObject.getJSONArray("JSON_DATA");
                    String fileString = jsonArray.getJSONObject(0).getString("receipt_file");
                    viewPdf(fileString);
                }
                Log.d("get_carried_out_receipt", "" + responseDecryptedBlockKey);
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnError(VolleyError volleyError) {

    }

    @Override
    public void onClickedItems(ArrayList<RealTimeMonitoringSystem> pwmuniSelectList) {
        clickedList = new ArrayList<>();
        double quantity =0;
        swm_activity_carried_out_id_list = new ArrayList<>();

        for(int i=0;i<pwmuniSelectList.size();i++){
            if(pwmuniSelectList.get(i).getCheck_flag().equalsIgnoreCase("Y")){
                RealTimeMonitoringSystem pwm_unit_sale_list_item = new RealTimeMonitoringSystem();
                pwm_unit_sale_list_item.setSwm_activity_carried_out_id(pwmuniSelectList.get(i).getSwm_activity_carried_out_id());
                pwm_unit_sale_list_item.setSwm_infra_details_id(pwmuniSelectList.get(i).getSwm_infra_details_id());
                pwm_unit_sale_list_item.setDate_entry_for(pwmuniSelectList.get(i).getDate_entry_for());
                pwm_unit_sale_list_item.setAmount_of_plastic_waste_sent_to_pwm_unit_in_kg(pwmuniSelectList.get(i).getAmount_of_plastic_waste_sent_to_pwm_unit_in_kg());
                pwm_unit_sale_list_item.setAmount_of_plastic_waste_sent_to_pwm_unit_revenue_in_rs(pwmuniSelectList.get(i).getAmount_of_plastic_waste_sent_to_pwm_unit_revenue_in_rs());
                pwm_unit_sale_list_item.setCheck_flag("N");
                clickedList.add(pwm_unit_sale_list_item);
            }
        }
        if(clickedList.size()>0){
            for(int i=0;i<clickedList.size();i++){
                try {
                quantity =  quantity+Double.parseDouble(clickedList.get(i).getAmount_of_plastic_waste_sent_to_pwm_unit_in_kg());
                swm_activity_carried_out_id_list.add(clickedList.get(i).getSwm_activity_carried_out_id());
            }
            catch (NumberFormatException e){
                e.printStackTrace();
            }

            }
            pwmUnitSaleBinding.quantityText.setText(""+quantity);
            pwmUnitSaleBinding.selectedItemValueVisibleLayout.setVisibility(View.VISIBLE);

        }
        else {
            pwmUnitSaleBinding.selectedItemValueVisibleLayout.setVisibility(View.GONE);
        }
    }

    public void listItemPdfIconClickAction(int position){
        if(Utils.isOnline()){
            get_carried_out_receipt(pwmUnitList.get(position).getSwm_activity_carried_out_id());
        }
        else {
            Utils.showAlert(PwmUnitSale.this,getResources().getString(R.string.no_internet));
        }

    }

    public void plastic_waste_management_unit_save() {
        try {
            new ApiService(this).makeJSONObjectRequest("plastic_waste_management_unit_save", Api.Method.POST, UrlGenerator.getWorkListUrl(), plastic_waste_management_unit_save_encrypt_Params(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public JSONObject plastic_waste_management_unit_save_encrypt_Params() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), plastic_waste_management_unit_save_normal_Params().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("JSONRequest", "" + authKey);
        return dataSet;
    }
    public JSONObject plastic_waste_management_unit_save_normal_Params() throws JSONException {
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, "plastic_waste_management_unit_save");
        dataSet.put("swm_infra_details_id", swm_infra_details_id);
        dataSet.put("swm_activity_carried_out_id", swm_activity_carried_out_id_list.toArray());
        dataSet.put("sale_in_kg", pwmUnitSaleBinding.quantityText.getText().toString());
        dataSet.put("sale_amount", pwmUnitSaleBinding.amountText.getText().toString());
        dataSet.put("agency_name", pwmUnitSaleBinding.agencyNameText.getText().toString());
        dataSet.put("cheque_file", fileString);
        Log.d("JSONRequest", "" + dataSet);
        return dataSet;
    }

    public class Insert_plastic_waste_management_unit_list extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {
            if (params.length > 0) {
                JSONArray jsonArray = new JSONArray();
                try {
                    jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pwmUnitList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    RealTimeMonitoringSystem pwm_unit_sale_list_item = new RealTimeMonitoringSystem();
                    try {
                        pwm_unit_sale_list_item.setSwm_activity_carried_out_id(jsonArray.getJSONObject(i).getInt("swm_activity_carried_out_id"));
                        pwm_unit_sale_list_item.setSwm_infra_details_id(jsonArray.getJSONObject(i).getString("swm_infra_details_id"));
                        pwm_unit_sale_list_item.setDate_entry_for(jsonArray.getJSONObject(i).getString("date_entry_for"));
                        pwm_unit_sale_list_item.setAmount_of_plastic_waste_sent_to_pwm_unit_in_kg(jsonArray.getJSONObject(i).getString("amount_of_plastic_waste_sent_to_pwm_unit_in_kg"));
                        pwm_unit_sale_list_item.setAmount_of_plastic_waste_sent_to_pwm_unit_revenue_in_rs(jsonArray.getJSONObject(i).getString("amount_of_plastic_waste_sent_to_pwm_unit_revenue_in_rs"));
                        pwm_unit_sale_list_item.setCheck_flag("N");

                        pwmUnitList.add(pwm_unit_sale_list_item);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(pwmUnitList.size()>0){
                pwmUnitSaleBinding.pwmCollectionRecycler.setVisibility(View.VISIBLE);
                pwmUnitSaleAdapter = new PwmUnitSaleAdapter(PwmUnitSale.this,pwmUnitList,PwmUnitSale.this::onClickedItems);
                pwmUnitSaleBinding.pwmCollectionRecycler.setAdapter(pwmUnitSaleAdapter);
            }
            else {
                pwmUnitSaleBinding.pwmCollectionRecycler.setVisibility(View.GONE);
            }
        }
    }

    public void get_carried_out_receipt(int swm_activity_carried_out_id) {
        try {
            new ApiService(this).makeJSONObjectRequest("get_carried_out_receipt", Api.Method.POST, UrlGenerator.getWorkListUrl(), get_carried_out_receipt_encrypt_Params(swm_activity_carried_out_id), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public JSONObject get_carried_out_receipt_encrypt_Params(int swm_activity_carried_out_id) throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), get_carried_out_receipt_normal_Params(swm_activity_carried_out_id).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("JSONRequest", "" + authKey);
        return dataSet;
    }
    public JSONObject get_carried_out_receipt_normal_Params(int swm_activity_carried_out_id) throws JSONException {
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, "get_carried_out_receipt");
        dataSet.put("swm_infra_details_id", swm_infra_details_id);
        dataSet.put("swm_activity_carried_out_id", swm_activity_carried_out_id);
        Log.d("JSONRequest", "" + dataSet);
        return dataSet;
    }

    @Override
    public void getDate(String dateValue) {
        String[] separated = dateValue.split(":");
        fromDate = separated[0]; // this will contain "Fruit"
        toDate = separated[1];
        pwmUnitSaleBinding.date.setText(fromDate+" to "+toDate);
//        getInCompleteActivityList();
        if(Utils.isOnline()) {
            get_details_of_pwm_unit_sale_view("");
        }
        else {
            Utils.showAlert(PwmUnitSale.this,getResources().getString(R.string.no_internet));
        }
    }

    //////******* PDF Activity
    //************/
    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("*/*");
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select file to upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install file manager",
                    Toast.LENGTH_SHORT).show();
        }
    }
    private boolean checkPermissions() {
        String[] permissions = new String[] {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE

        };
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p:permissions) {
            result = ContextCompat.checkSelfPermission(PwmUnitSale.this,p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_REQUEST_CODE_PERMISSION);
            return false;
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //
        switch (requestCode) {
            case MY_REQUEST_CODE_PERMISSION: {

                // Note: If request is cancelled, the result arrays are empty.
                // Permissions granted (CALL_PHONE).
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.i( "LOG_TAG","Permission granted");
                    Toast.makeText(this.getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                    showFileChooser();

//                    this.doBrowseFile();
                }
                // Cancelled or denied.
                else {
                    Log.i("LOG_TAG","Permission denied");
                    Toast.makeText(this.getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }
    public void viewPdf(final String DocumentString) {
        dialog = new Dialog(this,R.style.AppTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.pdf_view_layout);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.dimAmount = 0.7f;
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();

        final PDFView pdfView = (PDFView) dialog.findViewById(R.id.documentViewer);
        final TextView pageNum = (TextView) dialog.findViewById(R.id.pageNum);
        final TextView title = (TextView) dialog.findViewById(R.id.title);

        pageNumber = 0;
        if (DocumentString != null && !DocumentString.equals("")) {
            byte[] decodedString = new byte[0];
            try {
                //byte[] name = java.util.Base64.getEncoder().encode(fileString.getBytes());
                decodedString = Base64.decode(DocumentString/*traders.get(position).getDocument().toString()*/, Base64.DEFAULT);
                System.out.println(new String(decodedString));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            pdfView.fromBytes(decodedString).
                    onPageChange(new OnPageChangeListener() {
                        @Override
                        public void onPageChanged(int page, int pageCount) {
                            pageNumber = page;
//                            setTitle(String.format("%s %s / %s", "PDF", page + 1, pageCount));
                            pageNum.setText(pageNumber + 1 + "/" + pageCount);
                        }
                    }).defaultPage(pageNumber).swipeHorizontal(true).enableDoubletap(true).load();

        }else {
            Utils.showAlert(this,"No Record Found!");
        }


        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        super.onActivityResult(requestCode, resultCode, data);
         if(requestCode == FILE_SELECT_CODE){
            if (resultCode == RESULT_OK) {
                // Get the Uri of the selected file
                uri = data.getData();
                String uriString = uri.toString();
                myFile = new File(uriString);
                String path = myFile.getAbsolutePath();
                Log.d("uri", uriString);
                Log.d("myFile", myFile.toString());
                Log.d("path", path);
                ConvertToString(uri);
                if (uriString.startsWith("content://")) {
                    Cursor cursor = null;
                    try {
                        cursor = this.getContentResolver().query(uri, null, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                            int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                            long fileSizeInBytes = cursor.getLong(sizeIndex);

                            ConvertToString(uri);
                            Log.d("fileString>>", fileString);
                            pwmUnitSaleBinding.pdfIcon.setVisibility(View.VISIBLE);
                        }
                    } finally {
                        cursor.close();
                    }
                } else if (uriString.startsWith("file://")) {
                    displayName = myFile.getName();
                    Log.d("displayName", displayName);

                    pwmUnitSaleBinding.pdfIcon.setVisibility(View.VISIBLE);
                }
            }
        }
    }
    public void ConvertToString(Uri uri){
        uriString = uri.toString();
        Log.d("data", "onActivityResult: uri"+uriString);
        //            myFile = new File(uriString);
        //            ret = myFile.getAbsolutePath();
        //Fpath.setText(ret);
        try {
            InputStream in = getContentResolver().openInputStream(uri);

            bytes=getBytes(in);
            Log.d("data", "onActivityResult: bytes size="+bytes.length);
            String cnt_size;

            double size_kb = getFileSize(displayName) /1024;
            double size_mb = size_kb / 1024;
            double size_gb = size_mb / 1024 ;

            if (size_gb > 0){
                cnt_size = size_gb + " GB";
            }else if(size_mb > 0){
                cnt_size = size_mb + " MB";
            }else{
                cnt_size = size_kb + " KB";
            }
            Log.d("data", "onActivityResult: Base64string="+Base64.encodeToString(bytes,Base64.DEFAULT));
            fileString = Base64.encodeToString(bytes,Base64.DEFAULT);
            System.out.println("Base64>>"+Base64.encodeToString(bytes,Base64.DEFAULT));
            System.out.println("Base64fileString>>"+fileString);
            fileSize=cnt_size;

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.d("error", "onActivityResult: " + e.toString());
        }
    }
    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
    String getFilePath(Context cntx, Uri uri) {
        Cursor cursor = null;
        try {
            String[] arr = { MediaStore.Images.Media.DATA };
            cursor = cntx.getContentResolver().query(uri,  arr, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    public static long getFileSize(String filename) {
        File file = new File(filename);
        if (!file.exists() || !file.isFile()) {
            System.out.println("File doesn\'t exist");
            return -1;
        }
        return file.length();
    }

 }

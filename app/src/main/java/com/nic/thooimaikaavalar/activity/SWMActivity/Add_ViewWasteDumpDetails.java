package com.nic.thooimaikaavalar.activity.SWMActivity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nic.thooimaikaavalar.R;
import com.nic.thooimaikaavalar.adapter.AssetsDetailsAdapter;
import com.nic.thooimaikaavalar.adapter.WastedumpDetailsAdapter;
import com.nic.thooimaikaavalar.api.Api;
import com.nic.thooimaikaavalar.api.ApiService;
import com.nic.thooimaikaavalar.api.ServerResponse;
import com.nic.thooimaikaavalar.constant.AppConstant;
import com.nic.thooimaikaavalar.dataBase.DBHelper;
import com.nic.thooimaikaavalar.dataBase.dbData;
import com.nic.thooimaikaavalar.databinding.ActivityAddViewWasteDumpDetailsBinding;
import com.nic.thooimaikaavalar.model.RealTimeMonitoringSystem;
import com.nic.thooimaikaavalar.session.PrefManager;
import com.nic.thooimaikaavalar.support.MyLocationListener;
import com.nic.thooimaikaavalar.support.ProgressHUD;
import com.nic.thooimaikaavalar.utils.CameraUtils;
import com.nic.thooimaikaavalar.utils.FontCache;
import com.nic.thooimaikaavalar.utils.UrlGenerator;
import com.nic.thooimaikaavalar.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;

public class Add_ViewWasteDumpDetails extends AppCompatActivity implements Api.ServerResponseListener{

    ActivityAddViewWasteDumpDetailsBinding wasteDumpDetailsBinding;
    public com.nic.thooimaikaavalar.dataBase.dbData dbData = new dbData(this);
    private PrefManager prefManager;
    private SQLiteDatabase db;
    public DBHelper dbHelper;

    private AlertDialog alert;
    String is_there_any_waste_dump ="";
    String swm_infra_details_id ="";
    String no_of_thooimai_kaavalars_allocated ="";
    String no_of_thooimai_kaavalars_working ="";
    String whether_community_compost_pit_available_in_panchayat ="";
    String whether_vermi_compost_pit_available_in_panchayat ="";
    String any_integrated_nuesery_devlp_near_swm_facility ="";

    ///Image With Description
    ImageView imageView, image_view_preview;
    TextView latitude_text, longtitude_text;
    EditText myEditTextView;
    private List<View> viewArrayList = new ArrayList<>();
    Double offlatTextValue, offlongTextValue;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 2500;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static String imageStoragePath;
    public static final int BITMAP_SAMPLE_SIZE = 8;
    LocationManager mlocManager = null;
    LocationListener mlocListener;

    WastedumpDetailsAdapter wastedumpDetailsAdapter;

    Uri photoURI;
    File photoFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wasteDumpDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_add__view_waste_dump_details);
        wasteDumpDetailsBinding.setActivity(this);

        prefManager = new PrefManager(this);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
            dbData.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        getIntentData();
        wasteDumpDetailsBinding.wasteDumpAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getTables_there_any_waste_dump = dbData.getTables_there_any_waste_dump();
                if(getTables_there_any_waste_dump.equals("")){
                    addStructureView();
                }
                else if(getTables_there_any_waste_dump.equals("Y")){
                    imageWithDescription(getTables_there_any_waste_dump,wasteDumpDetailsBinding.scrollView);
                }
                else if(getTables_there_any_waste_dump.equals("N")){
                    Utils.showAlert(Add_ViewWasteDumpDetails.this,"You Can't take photos Please delete old record");
                }

            }
        });
        wasteDumpDetailsBinding.wasteDumpViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int gettableCountWasteDumpTable = dbData.gettableCountWasteDumpTable();
                if(gettableCountWasteDumpTable>0){
                    getOfflineWatseDumpList();
                }
                else {
                    getOnlineWasteDumpList();
                }

            }
        });

        wasteDumpDetailsBinding.backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
            }
        });
    }

    public void getIntentData(){
        initialiseRecyclerView();
        wasteDumpDetailsBinding.scrollView.setVisibility(View.VISIBLE);
        wasteDumpDetailsBinding.wasteDumpRecyclerLayout.setVisibility(View.GONE);
        swm_infra_details_id = getIntent().getStringExtra("swm_infra_details_id");
        no_of_thooimai_kaavalars_allocated = getIntent().getStringExtra("no_of_thooimai_kaavalars_allocated");
        no_of_thooimai_kaavalars_working = getIntent().getStringExtra("no_of_thooimai_kaavalars_working");
        whether_community_compost_pit_available_in_panchayat = getIntent().getStringExtra("whether_community_compost_pit_available_in_panchayat");
        whether_vermi_compost_pit_available_in_panchayat = getIntent().getStringExtra("whether_vermi_compost_pit_available_in_panchayat");
        any_integrated_nuesery_devlp_near_swm_facility = getIntent().getStringExtra("any_integrated_nuesery_devlp_near_swm_facility");
    }
    private void initialiseRecyclerView() {
        wasteDumpDetailsBinding.wasteDumpRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getApplicationContext(),DividerItemDecoration.VERTICAL);
        wasteDumpDetailsBinding.wasteDumpRecycler.addItemDecoration(itemDecoration);
    }

    public void addStructureView(){

        try {
            //We need to get the instance of the LayoutInflater, use the context of this activity
            final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //Inflate the view from a predefined XML layout
            final View view = inflater.inflate(R.layout.dialog_for_add_asset_details, null);

            ImageView close_icon = view.findViewById(R.id.close_icon);
            RadioButton yes_radio = view.findViewById(R.id.functional_yes);
            RadioButton no_radio = view.findViewById(R.id.functional_no);
            Button submit_btn_layout = view.findViewById(R.id.submit_btn_layout);
            RelativeLayout capture_layout = view.findViewById(R.id.capture_layout);
            RelativeLayout others_name_layout = view.findViewById(R.id.others_name_layout);
            TextView asset_count_name = view.findViewById(R.id.asset_count_name);
            TextView is_functional_tv = view.findViewById(R.id.is_functional_tv);
            EditText others_name = view.findViewById(R.id.others_name);

            is_functional_tv.setText(getResources().getString(R.string.is_there_any_waste_dump_seen));

            capture_layout.setVisibility(View.GONE);
            submit_btn_layout.setVisibility(View.GONE);
            others_name_layout.setVisibility(View.GONE);


            close_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();
                }
            });
            yes_radio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        is_there_any_waste_dump ="Y";
                        capture_layout.setVisibility(View.VISIBLE);
                        submit_btn_layout.setVisibility(View.GONE);
                    }
                }
            });
            no_radio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        is_there_any_waste_dump ="N";
                        capture_layout.setVisibility(View.GONE);
                        submit_btn_layout.setVisibility(View.VISIBLE);
                    }
                }
            });


            submit_btn_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!is_there_any_waste_dump.equals("")){
                        try {
                            ContentValues values = new ContentValues();
                            values.put("swm_infra_details_id",swm_infra_details_id);
                            values.put("dcode",prefManager.getDistrictCode());
                            values.put("bcode",prefManager.getBlockCode());
                            values.put("pvcode",prefManager.getPvCode());
                            values.put("is_there_any_waste_dump", is_there_any_waste_dump);

                            long insert_id = db.insert(DBHelper.SWM_WASTE_DUMP_PHOTOS_DETAILS,null,values);
                            if(insert_id>0){
                                ContentValues contentValues = new ContentValues();
                                contentValues.put("swm_infra_details_id",swm_infra_details_id);
                                contentValues.put("dcode",prefManager.getDistrictCode());
                                contentValues.put("bcode",prefManager.getBlockCode());
                                contentValues.put("pvcode",prefManager.getPvCode());
                                contentValues.put("pvname",prefManager.getKeyPvNameTa());
                                contentValues.put("is_there_any_waste_dump", is_there_any_waste_dump);
                                contentValues.put("no_of_thooimai_kaavalars_allocated", no_of_thooimai_kaavalars_allocated);
                                contentValues.put("no_of_thooimai_kaavalars_working", no_of_thooimai_kaavalars_working);
                                contentValues.put("whether_community_compost_pit_available_in_panchayat", whether_community_compost_pit_available_in_panchayat);
                                contentValues.put("whether_vermi_compost_pit_available_in_panchayat", whether_vermi_compost_pit_available_in_panchayat);
                                contentValues.put("any_integrated_nuesery_devlp_near_swm_facility", any_integrated_nuesery_devlp_near_swm_facility);
                                long update_id = db.update(DBHelper.SWM_MASTER_DETAILS_SERVER_TABLE, contentValues, "swm_infra_details_id" + "= ?", new String[] {swm_infra_details_id});
                                Toasty.success(Add_ViewWasteDumpDetails.this, "Success", Toast.LENGTH_LONG, true).show();
                                alert.dismiss();
                            }
                            else {
                                Toasty.error(Add_ViewWasteDumpDetails.this, "Fail", Toast.LENGTH_LONG, true).show();
                                alert.dismiss();
                            }
                        }
                        catch (Exception e){
                            alert.dismiss();
                        }
                    }
                    else {
                        Utils.showAlert(Add_ViewWasteDumpDetails.this,"Please Choose is_there_any_waste_dump");
                    }

                }
            });

            capture_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                            if(!is_there_any_waste_dump.equals("")){
                                try {
                                    imageWithDescription(is_there_any_waste_dump,wasteDumpDetailsBinding.scrollView);
                                    alert.dismiss();
                                }
                                catch (Exception e){
                                    alert.dismiss();
                                }
                            }
                            else {
                                Utils.showAlert(Add_ViewWasteDumpDetails.this,"Please Choose is_there_any_waste_dump");
                            }


                }
            });

            androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
            dialogBuilder.setView(view);
            alert = dialogBuilder.create();
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(alert.getWindow().getAttributes());
            lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.CENTER;
            lp.windowAnimations = R.style.DialogAnimation;
            alert.getWindow().setAttributes(lp);
            alert.show();
            alert.setCanceledOnTouchOutside(true);
            alert.setCancelable(true);
            alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void imageWithDescription(final String type, final ScrollView scrollView) {

        final Dialog dialog = new Dialog(this,R.style.AppTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_photo);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.dimAmount = 0.7f;
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();


        final LinearLayout mobileNumberLayout = (LinearLayout) dialog.findViewById(R.id.mobile_number_layout);
        TextView cancel = (TextView) dialog.findViewById(R.id.close);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button done = (Button) dialog.findViewById(R.id.btn_save_inspection);
        done.setGravity(Gravity.CENTER);
        done.setVisibility(View.VISIBLE);
        done.setTypeface(FontCache.getInstance(this).getFont(FontCache.Font.HEAVY));

        done.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                JSONArray imageJson = new JSONArray();
                long rowInserted=0;
                int childCount = mobileNumberLayout.getChildCount();
                int count = 0;
                if (childCount > 0) {
                    for (int i = 0; i < childCount; i++) {
                        JSONArray imageArray = new JSONArray();

                        View vv = mobileNumberLayout.getChildAt(i);
                        imageView = vv.findViewById(R.id.image_view);
                        myEditTextView = vv.findViewById(R.id.description);
                        latitude_text = vv.findViewById(R.id.latitude);
                        longtitude_text = vv.findViewById(R.id.longtitude);


                        if(imageView.getDrawable()!=null){
                            //if(!myEditTextView.getText().toString().equals("")){
                                count = count+1;
                                byte[] imageInByte = new byte[0];
                                String image_str = "";
                                String description="";
                                try {
                                    description = myEditTextView.getText().toString();
                                    Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                    imageInByte = baos.toByteArray();


                                } catch (Exception e) {
                                    Utils.showAlert(Add_ViewWasteDumpDetails.this, getResources().getString(R.string.at_least_capture_one_photo));
                                }

                                if (MyLocationListener.latitude > 0) {
                                    offlatTextValue = MyLocationListener.latitude;
                                    offlongTextValue =MyLocationListener.longitude;
                                }

                                ContentValues imageValue = new ContentValues();
                                imageValue.put("swm_infra_details_id", swm_infra_details_id);
                                imageValue.put("dcode", prefManager.getDistrictCode());
                                imageValue.put("bcode", prefManager.getBlockCode());
                                imageValue.put("pvcode", prefManager.getPvCode());
                                imageValue.put("is_there_any_waste_dump", type);
                                imageValue.put("lattitude", latitude_text.getText().toString());
                                imageValue.put("longtitude", longtitude_text.getText().toString());
                                imageValue.put("image", imageInByte);

                                rowInserted = db.insert(DBHelper.SWM_WASTE_DUMP_PHOTOS_DETAILS, null, imageValue);

                                if(count==childCount){
                                    if(rowInserted>0){
                                        ContentValues contentValues = new ContentValues();
                                        contentValues.put("swm_infra_details_id",swm_infra_details_id);
                                        contentValues.put("dcode",prefManager.getDistrictCode());
                                        contentValues.put("bcode",prefManager.getBlockCode());
                                        contentValues.put("pvcode",prefManager.getPvCode());
                                        contentValues.put("pvname",prefManager.getKeyPvNameTa());
                                        contentValues.put("is_there_any_waste_dump", type);
                                        contentValues.put("no_of_thooimai_kaavalars_allocated", no_of_thooimai_kaavalars_allocated);
                                        contentValues.put("no_of_thooimai_kaavalars_working", no_of_thooimai_kaavalars_working);
                                        contentValues.put("whether_community_compost_pit_available_in_panchayat", whether_community_compost_pit_available_in_panchayat);
                                        contentValues.put("whether_vermi_compost_pit_available_in_panchayat", whether_vermi_compost_pit_available_in_panchayat);
                                        contentValues.put("any_integrated_nuesery_devlp_near_swm_facility", any_integrated_nuesery_devlp_near_swm_facility);
                                        long update_id = db.update(DBHelper.SWM_MASTER_DETAILS_SERVER_TABLE, contentValues, "swm_infra_details_id" + "= ?", new String[] {swm_infra_details_id});

                                        Toasty.success(Add_ViewWasteDumpDetails.this,getResources().getString(R.string.inserted_success),Toasty.LENGTH_SHORT);
                                        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                                        dialog.dismiss();
                                    }

                                }

                            /*}

                            else {
                                Utils.showAlert(Add_ViewWasteDumpDetails.this,getResources().getString(R.string.please_enter_description));
                            }*/
                        }
                        else {
                            Utils.showAlert(Add_ViewWasteDumpDetails.this,getResources().getString(R.string.please_capture_image));
                        }
                    }
                }

                focusOnView(scrollView);

            }
        });
        Button btnAddMobile = (Button) dialog.findViewById(R.id.btn_add);
        btnAddMobile.setTypeface(FontCache.getInstance(this).getFont(FontCache.Font.MEDIUM));
        viewArrayList.clear();
        btnAddMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewArrayList.size() < Integer.parseInt(prefManager.getKEY_no_of_waste_dump_photos())) {
                    if (imageView.getDrawable() != null && viewArrayList.size() > 0) {
                        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                        updateView(Add_ViewWasteDumpDetails.this, mobileNumberLayout, "", type);
                    } else {
                        Utils.showAlert(Add_ViewWasteDumpDetails.this, getResources().getString(R.string.please_capture_image));
                    }
                }
                else {
                    Utils.showAlert(Add_ViewWasteDumpDetails.this, getResources().getString(R.string.maximum_three_photos));

                }
            }
        });
        updateView(this, mobileNumberLayout, "", type);

    }
    private final void focusOnView(final ScrollView your_scrollview) {
        your_scrollview.post(new Runnable() {
            @Override
            public void run() {
                your_scrollview.fullScroll(View.FOCUS_DOWN);

            }
        });
    }

    //Method for update single view based on email or mobile type
    public View updateView(final Activity activity, final LinearLayout emailOrMobileLayout, final String values, final String type) {
        final View hiddenInfo = activity.getLayoutInflater().inflate(R.layout.image_with_description, emailOrMobileLayout, false);
        final ImageView imageView_close = (ImageView) hiddenInfo.findViewById(R.id.imageView_close);
        imageView = (ImageView) hiddenInfo.findViewById(R.id.image_view);
        image_view_preview = (ImageView) hiddenInfo.findViewById(R.id.image_view_preview);
        myEditTextView = (EditText) hiddenInfo.findViewById(R.id.description);
        latitude_text = hiddenInfo.findViewById(R.id.latitude);
        longtitude_text = hiddenInfo.findViewById(R.id.longtitude);

        imageView_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    imageView.setVisibility(View.VISIBLE);
                    if (viewArrayList.size() != 1) {
                        ((LinearLayout) hiddenInfo.getParent()).removeView(hiddenInfo);
                        viewArrayList.remove(hiddenInfo);
                    }

                } catch (IndexOutOfBoundsException a) {
                    a.printStackTrace();
                }
            }
        });
        image_view_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLatLong();

            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLatLong();
            }
        });
        emailOrMobileLayout.addView(hiddenInfo);

        View vv = emailOrMobileLayout.getChildAt(viewArrayList.size());
        EditText myEditTextView1 = (EditText) vv.findViewById(R.id.description);
        //myEditTextView1.setSelection(myEditTextView1.length());
        myEditTextView1.requestFocus();
        viewArrayList.add(hiddenInfo);
        return hiddenInfo;
    }

    public void getLatLong() {
        mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new MyLocationListener();
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAltitudeRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        criteria.setBearingRequired(false);

        //API level 9 and up
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
        Integer gpsFreqInMillis = 1000;
        Integer gpsFreqInDistance = 1;


        // permission was granted, yay! Do the
        // location-related task you need to do.
        if (ContextCompat.checkSelfPermission(Add_ViewWasteDumpDetails.this,
                ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            //Request location updates:
            //mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
            mlocManager.requestLocationUpdates(gpsFreqInMillis, gpsFreqInDistance, criteria, mlocListener, null);

        }

        if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(Add_ViewWasteDumpDetails.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Add_ViewWasteDumpDetails.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    requestPermissions(new String[]{CAMERA, ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            } else {
                if (ActivityCompat.checkSelfPermission(Add_ViewWasteDumpDetails.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Add_ViewWasteDumpDetails.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Add_ViewWasteDumpDetails.this, new String[]{ACCESS_FINE_LOCATION}, 1);

                }
            }
            if (MyLocationListener.latitude > 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (CameraUtils.checkPermissions(Add_ViewWasteDumpDetails.this)) {
                        captureImage();
                    } else {
                        requestCameraPermission(MEDIA_TYPE_IMAGE);
                    }
//                            checkPermissionForCamera();
                } else {
                    captureImage();
                }
            } else {
                Utils.showAlert(Add_ViewWasteDumpDetails.this, getResources().getString(R.string.satellite));
            }
        } else {
            Utils.showAlert(Add_ViewWasteDumpDetails.this, getResources().getString(R.string.gps_is_not_turned_on));
        }
    }

    private void requestCameraPermission(final int type) {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                            if (type == MEDIA_TYPE_IMAGE) {
                                // capture picture
                                captureImage();
                            } else {
//                                captureVideo();
                            }

                        } else if (report.isAnyPermissionPermanentlyDenied()) {
                            showPermissionsAlert();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }


    private void showPermissionsAlert() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.permission_required))
                .setMessage(getResources().getString(R.string.camera_need_permission))
                .setPositiveButton(getResources().getString(R.string.goto_settings), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        CameraUtils.openSettings(Add_ViewWasteDumpDetails.this);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    public void previewCapturedImage() {
        try {
            // hide video preview
            Bitmap bitmap = CameraUtils.optimizeBitmap(BITMAP_SAMPLE_SIZE, imageStoragePath);
            image_view_preview.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            ExifInterface ei = null;
            try {
                ei = new ExifInterface(imageStoragePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            Bitmap rotatedBitmap = null;
            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;
            }
            imageView.setImageBitmap(rotatedBitmap);
            latitude_text.setText(""+offlatTextValue);
            longtitude_text.setText(""+offlongTextValue);
//            cameraScreenBinding.imageView.showImage((getImageUri(rotatedBitmap)));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Bitmap photo= (Bitmap) data.getExtras().get("data");
                    imageView.setImageBitmap(photo);
                    image_view_preview.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                    latitude_text.setText(""+offlatTextValue);
                    longtitude_text.setText(""+offlongTextValue);
                }
                else {
                    // Refreshing the gallery
                    CameraUtils.refreshGallery(getApplicationContext(), imageStoragePath);

                    // successfully captured the image
                    // display it in image view
                    previewCapturedImage();
                }
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.user_canceled_image_capture), Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.sorry_failed_to_capture), Toast.LENGTH_SHORT)
                        .show();
            }
        } else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Refreshing the gallery
                CameraUtils.refreshGallery(getApplicationContext(), imageStoragePath);

                // video successfully recorded
                // preview the recorded video
//                previewVideo();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.user_canceled_video), Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.sorry_failed_capture_video), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    private void captureImage() {
        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {

            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);

        }
        else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            File file = CameraUtils.getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (file != null) {
                imageStoragePath = file.getAbsolutePath();
            }

            Uri fileUri = CameraUtils.getOutputMediaFileUri(this, file);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

            // start the image capture Intent
            startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
        }
        if (MyLocationListener.latitude > 0) {
            offlatTextValue = MyLocationListener.latitude;
            offlongTextValue = MyLocationListener.longitude;
        }
    }

    public void getOfflineWatseDumpList(){
        try {

            dbData.open();
            ArrayList<RealTimeMonitoringSystem> wasteDumpList = new ArrayList<>();
            wasteDumpList = dbData.getWasteDumpPhotosList("",prefManager.getPvCode(),swm_infra_details_id);
            if(wasteDumpList.size()>0){
                wastedumpDetailsAdapter = new WastedumpDetailsAdapter(wasteDumpList,Add_ViewWasteDumpDetails.this,"Local",dbData,swm_infra_details_id);
                wasteDumpDetailsBinding.wasteDumpRecycler.setAdapter(wastedumpDetailsAdapter);
                wasteDumpDetailsBinding.wasteDumpRecycler.setVisibility(View.VISIBLE);
                wasteDumpDetailsBinding.scrollView.setVisibility(View.GONE);
                wasteDumpDetailsBinding.wasteDumpRecyclerLayout.setVisibility(View.VISIBLE);
            }
            else {
                wasteDumpDetailsBinding.wasteDumpRecycler.setVisibility(View.GONE);
            }

        }
        catch (Exception e){

        }
    }

    public void getOnlineWasteDumpList(){
        try {
            new ApiService(this).makeJSONObjectRequest("OnlineWasteDump", Api.Method.POST, UrlGenerator.getWorkListUrl(), OnlinegetOnlineWasteDumpListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public JSONObject OnlinegetOnlineWasteDumpListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), OnlinegetOnlineWasteDumpListNormalJsonParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("OnlineWasteDump", "" + dataSet);
        return dataSet;
    }
    public  JSONObject OnlinegetOnlineWasteDumpListNormalJsonParams() throws JSONException {
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, "details_of_swm_waste_dump_view");
        dataSet.put("swm_infra_details_id", swm_infra_details_id);
        Log.d("OnlineWasteDump", "" + dataSet);
        return dataSet;
    }

    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        try {
            String urlType = serverResponse.getApi();
            JSONObject responseObj = serverResponse.getJsonResponse();


            if ("OnlineWasteDump".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new fetchOnlineWasteDumpTask().execute(jsonObject);
                }
                else {
                    getSwmMasterList();
                    wasteDumpDetailsBinding.wasteDumpRecycler.setVisibility(View.GONE);
                    Toasty.success(Add_ViewWasteDumpDetails.this,jsonObject.getString("MESSAGE"),Toasty.LENGTH_SHORT);
                }
                Log.d("OnlineWasteDump", "" + responseDecryptedBlockKey);
            }

            if ("DeleteWasteDump".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    getOnlineWasteDumpList();
                    Toasty.success(Add_ViewWasteDumpDetails.this,jsonObject.getString("MESSAGE"),Toasty.LENGTH_SHORT);
                }

                Log.d("DeleteWasteDump", "" + responseDecryptedBlockKey);
            }

            if ("SWMList".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedSchemeKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedSchemeKey);
                Log.d("Query",responseDecryptedSchemeKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new InsertSWMListFromServerTask().execute(jsonObject);
                }

                Log.d("SWMList", "" + responseDecryptedSchemeKey);
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnError(VolleyError volleyError) {

    }

    public class fetchOnlineWasteDumpTask extends AsyncTask<JSONObject, Void, ArrayList<RealTimeMonitoringSystem>> {

        @Override
        protected ArrayList<RealTimeMonitoringSystem> doInBackground(JSONObject... params) {

            ArrayList<RealTimeMonitoringSystem> assetList = new ArrayList<>();
            if (params.length > 0) {
                JSONArray jsonArray = new JSONArray();
                try {
                    jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                for (int i = 0; i < jsonArray.length(); i++) {
                    RealTimeMonitoringSystem assetsListItem = new RealTimeMonitoringSystem();
                    try {
                        assetsListItem.setDistictCode(jsonArray.getJSONObject(i).getString("dcode"));
                        assetsListItem.setBlockCode(jsonArray.getJSONObject(i).getString("bcode"));
                        assetsListItem.setPvCode(jsonArray.getJSONObject(i).getString("pvcode"));
                        assetsListItem.setSwm_infra_details_id(jsonArray.getJSONObject(i).getString("swm_infra_details_id"));
                        assetsListItem.setSwm_waste_dump_photos_id(jsonArray.getJSONObject(i).getString("swm_waste_dump_photos_id"));
                        assetsListItem.setIs_there_any_waste_dump(jsonArray.getJSONObject(i).getString("is_photo_of_waste_dump"));
                        byte[] decodedString = Base64.decode(jsonArray.getJSONObject(i).getString(AppConstant.KEY_IMAGE), Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        assetsListItem.setImage(decodedByte);
                        assetList.add(assetsListItem);

                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }
            }

            return assetList;
        }

        @Override
        protected void onPostExecute(ArrayList<RealTimeMonitoringSystem> wasteDumpList) {
            super.onPostExecute(wasteDumpList);
            if(wasteDumpList.size()>0){
                wastedumpDetailsAdapter = new WastedumpDetailsAdapter(wasteDumpList,Add_ViewWasteDumpDetails.this,"Server",dbData,swm_infra_details_id);
                wasteDumpDetailsBinding.wasteDumpRecycler.setAdapter(wastedumpDetailsAdapter);
                wasteDumpDetailsBinding.wasteDumpRecycler.setVisibility(View.VISIBLE);
                wasteDumpDetailsBinding.scrollView.setVisibility(View.GONE);
                wasteDumpDetailsBinding.wasteDumpRecyclerLayout.setVisibility(View.VISIBLE);
            }
            else {
                wasteDumpDetailsBinding.wasteDumpRecycler.setVisibility(View.GONE);
            }
        }
    }

    public void DeleteWasteDump(JSONObject jsonObject){
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), jsonObject.toString());
        JSONObject dataSet = new JSONObject();
        try {
            dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
            dataSet.put(AppConstant.DATA_CONTENT, authKey);
            Log.d("EncryptedDetails", "" + dataSet);
            Log.d("JSONDetails", "" + jsonObject);
            try {
                new ApiService(this).makeJSONObjectRequest("DeleteWasteDump", Api.Method.POST, UrlGenerator.getWorkListUrl(), dataSet, "not cache", this);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        catch (JSONException e){

        }


    }

    @Override
    public void onBackPressed() {
        if(wasteDumpDetailsBinding.wasteDumpRecyclerLayout.getVisibility()==View.VISIBLE){
            wasteDumpDetailsBinding.wasteDumpRecyclerLayout.setVisibility(View.GONE);
            wasteDumpDetailsBinding.scrollView.setVisibility(View.VISIBLE);
        }
        else {
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
        }
    }

    public class InsertSWMListFromServerTask extends AsyncTask<JSONObject, Void, Void> {

        private  boolean running = true;

        @Override
        protected Void doInBackground(JSONObject... params) {

            dbData.open();

            if (params.length > 0) {
                String where="pvcode = ?";

                db.delete(DBHelper.SWM_MASTER_DETAILS_SERVER_TABLE, where, new String[]{prefManager.getPvCode()}) ;

                JSONArray jsonArray = new JSONArray();

                try {
                    jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < jsonArray.length(); i++) {
                    RealTimeMonitoringSystem swmMasterDetailsList = new RealTimeMonitoringSystem();
                    try {

                        swmMasterDetailsList.setSwm_infra_details_id(jsonArray.getJSONObject(i).getString("swm_infra_details_id"));
                        swmMasterDetailsList.setDistictCode(jsonArray.getJSONObject(i).getString("dcode"));
                        swmMasterDetailsList.setBlockCode(jsonArray.getJSONObject(i).getString("bcode"));
                        swmMasterDetailsList.setPvCode(jsonArray.getJSONObject(i).getString("pvcode"));
                        swmMasterDetailsList.setPvName(prefManager.getKeyPvNameTa());
                        swmMasterDetailsList.setNo_of_thooimai_kaavalars_allocated(jsonArray.getJSONObject(i).getString("no_of_thooimai_kaavalars_allocated"));
                        swmMasterDetailsList.setNo_of_thooimai_kaavalars_working(jsonArray.getJSONObject(i).getString("no_of_thooimai_kaavalars_working"));
                        swmMasterDetailsList.setWhether_community_compost_pit_available_in_panchayat(jsonArray.getJSONObject(i).getString("whether_community_compost_pit_available_in_panchayat"));
                        swmMasterDetailsList.setWhether_vermi_compost_pit_available_in_panchayat(jsonArray.getJSONObject(i).getString("whether_vermi_compost_pit_available_in_panchayat"));
                        swmMasterDetailsList.setAny_integrated_nuesery_devlp_near_swm_facility(jsonArray.getJSONObject(i).getString("any_integrated_nuesery_devlp_near_swm_facility"));
                        swmMasterDetailsList.setIs_there_any_waste_dump(jsonArray.getJSONObject(i).getString("any_waste_dump_seen_in_the_panchayat"));
                        //swmMasterDetailsList.setIs_there_any_waste_dump("");

                        dbData.insertSwmMasterDetailsFromServer(swmMasterDetailsList);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            // }
            return null;
        }


    }

    public void getSwmMasterList() {
        try {
            new ApiService(this).makeJSONObjectRequest("SWMList", Api.Method.POST, UrlGenerator.getWorkListUrl(), SwmMasteJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject SwmMasteJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), SwmMasteNormalJsonParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("encrypt", "" + dataSet);
        return dataSet;
    }

    public  JSONObject SwmMasteNormalJsonParams() throws JSONException {
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, "details_of_swm_infra_view");
        dataSet.put(AppConstant.PV_CODE, prefManager.getPvCode());
        Log.d("normal", "" + dataSet);
        return dataSet;
    }

}

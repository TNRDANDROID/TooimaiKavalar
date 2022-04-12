package com.nic.thooimaikaavalar.Interface;

import android.content.Intent;

public interface AdapterCameraIntent {

    public void OnIntentListener(Intent data,int result_code,int position,String after_image_lat,String after_image_long);
}

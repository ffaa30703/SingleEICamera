package com.example.myapplication.cameraController;

import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.util.Log;

import com.example.myapplication.CameraActivity;

import java.util.List;

public class CameraController {
    private Camera camera;

    public static  int CAMERA_WIDTH = 640;
    public static  int CAMERA_HEIGHT = 480;

    CameraActivity activity;

    public void openCamera() {
        if (camera != null) {
            camera.release();
        }
        try{
            camera = Camera.open(1);
        }catch (Exception e){
            e.printStackTrace();
//            camera = Camera.open(1);
        }

    }



    public void startPreview(SurfaceTexture surface, PreviewCallback callback, byte[] previewCallback) {
        try {
            if (camera != null) {
                initParam(camera);
                //REQUIRED:API10
                camera.setPreviewTexture(surface);
                camera.addCallbackBuffer(previewCallback);
                camera.setPreviewCallbackWithBuffer(callback);
                camera.startPreview();
            } else {
                Log.d(getClass().getSimpleName(), "No camera");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeCamera() {
        try {
            if (camera != null) {
                camera.stopPreview();
                camera.setPreviewCallback(null);
                camera.release();
                camera = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initParam(Camera camera) {
        if (camera != null) {
            //        List<Camera.Size> pictureSize = camera.getParameters().getSupportedPictureSizes();
            List<Camera.Size> previewSize = camera.getParameters().getSupportedPreviewSizes();
            Camera.Parameters params = camera.getParameters();

            CameraActivity cameraActivity= new CameraActivity();
            activity=cameraActivity.getInstance();


            params.setPreviewSize(CAMERA_WIDTH, CAMERA_HEIGHT);
            params.setPictureSize(CAMERA_WIDTH, CAMERA_HEIGHT);
            camera.setParameters(params);
        }
    }


}

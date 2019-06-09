package com.example.myapplication.cameraController;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;

import com.example.myapplication.CameraActivity;
import com.example.myapplication.R;
import com.wf.*;

import java.util.Iterator;
import java.util.Set;

public class MainSurfaceView extends TextureView implements TextureView.SurfaceTextureListener, Camera.PreviewCallback {
    private CameraController cameraController = new CameraController();

    private String name = "";
    private boolean enroll = false;
    CameraActivity cameraActivity;






    private UnexpectedTerminationHelper mUnexpectedTerminationHelper = new UnexpectedTerminationHelper();







    private class UnexpectedTerminationHelper {
        private Thread mThread;
        private Thread.UncaughtExceptionHandler mOldUncaughtExceptionHandler = null;
        private Thread.UncaughtExceptionHandler mUncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) { // gets called on the same (main) thread
                cameraController.closeCamera(); // TODO: write appropriate code here
                if(mOldUncaughtExceptionHandler != null) {
                    // it displays the "force close" dialog
                    mOldUncaughtExceptionHandler.uncaughtException(thread, ex);
                }
            }
        };
        void init() {
            mThread = Thread.currentThread();
            mOldUncaughtExceptionHandler = mThread.getUncaughtExceptionHandler();
            mThread.setUncaughtExceptionHandler(mUncaughtExceptionHandler);
        }
        void fini() {
            mThread.setUncaughtExceptionHandler(mOldUncaughtExceptionHandler);
            mOldUncaughtExceptionHandler = null;
            mThread = null;
        }
    }





    public MainSurfaceView(Context context) {
        this(context, null);
    }

    public MainSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
        /*if (cameraMirror) {
            setScaleX(-1);
        }*/
    }

    public void setDrawActivity(CameraActivity activity){
        this.cameraActivity = activity;
    }

    public void setEnrolledName(String name,boolean enroll){
        this.name = name;
        this.enroll = enroll;
    }

    private boolean cameraFront = true;
    private boolean cameraMirror = false;
    private boolean cameraVertical = false;

    private void init(Context context, AttributeSet attrs) {
        setSurfaceTextureListener(this);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MainSurfaceView);
            cameraFront = a.getBoolean(R.styleable.MainSurfaceView_msv_cameraFront, cameraFront);
            cameraMirror = a.getBoolean(R.styleable.MainSurfaceView_msv_cameraMirror, cameraMirror);
            cameraVertical = a.getBoolean(R.styleable.MainSurfaceView_msv_cameraVertical, cameraVertical);
            a.recycle();
        }
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (cameraController != null) {
            mUnexpectedTerminationHelper.init();
            cameraController.openCamera();
            cameraPreviewBuffer = new byte[(int) (CameraController.CAMERA_HEIGHT*CameraController.CAMERA_WIDTH * 1.5)];//1.5 for yuv image
            cameraController.startPreview(surface, this,cameraPreviewBuffer);
            wffrsinglecamapp.setState(1);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (cameraController != null) {
            wffrsinglecamapp.releaseEngine();
            cameraController.closeCamera();
            mUnexpectedTerminationHelper.fini();
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }


    private byte[] cameraPreviewBuffer;


    /*************************************************************/
    @Override
    public void onPreviewFrame(final byte[] data, final Camera camera) {
      /*  if (cameraFront) {
            CameraDataQueue.getInstance().putF(data, SystemClock.uptimeMillis());
        } else {
            CameraDataQueue.getInstance().putB(data, SystemClock.uptimeMillis());
        }

        long startTime = System.currentTimeMillis();

*/
        //  final byte[][] imagesBufferArray = CameraDataQueue.getInstance().getDualCameraPreview();
        // long endTime = System.currentTimeMillis();
        //     Log.i("SDSD","TIME TAKEN EXECUTION : "+(endTime-startTime)+"ms");

      /*  if (imagesBufferArray!=null && camera1Activity!=null){

            //For normal withour rotation
             wffrdualcamapp.startExecution(imagesBufferArray[0], imagesBufferArray[1],CameraController.CAMERA_WIDTH,CameraController.CAMERA_HEIGHT,name);

            *//* To rotat eby 270 degrees *//*
         *//*  imagesBufferArray[0] = wffrjni.rotateImage(imagesBufferArray[0],CameraController.CAMERA_WIDTH,CameraController.CAMERA_HEIGHT,1,270);
            imagesBufferArray[1] = wffrjni.rotateImage(imagesBufferArray[1],CameraController.CAMERA_WIDTH,CameraController.CAMERA_HEIGHT,0,270);
            wffrdualcamapp.startExecution(imagesBufferArray[0], imagesBufferArray[1],CameraController.CAMERA_HEIGHT,CameraController.CAMERA_WIDTH,name);
            *//*
           // wffrdualcamapp.startExecution(imagesBufferArray[0], imagesBufferArray[1],CameraController.CAMERA_WIDTH,CameraController.CAMERA_HEIGHT,name);

            camera1Activity.drawOutput(wffrdualcamapp.getFaceCoordinates(), CameraController.CAMERA_WIDTH,CameraController.CAMERA_HEIGHT, enroll);
          //  camera1Activity.drawOutput(wffrdualcamapp.getFaceCoordinates(), CameraController.CAMERA_HEIGHT,CameraController.CAMERA_WIDTH, enroll);
        }*/


        try {
            long startTime = System.currentTimeMillis();
            //System.out.println("Time on Preview Method");
            Camera.Parameters parameters = camera.getParameters();
            final int frameWidth = parameters.getPreviewSize().width;
            final int frameHeight = parameters.getPreviewSize().height;

            Log.i("Thread ", "run:thread acrtive count out " + Thread.activeCount());


            AsyncTask.execute(new Runnable() {
                                  @Override
                                  public void run() {
                                      if (wffrsinglecamapp.getState() != 0) {
                                          wffrsinglecamapp.startExecution(data, frameWidth, frameHeight, name);
                                      }
                                      cameraActivity.drawOutput(wffrsinglecamapp.getFaceCoordinates(),
                                              frameWidth, frameHeight, enroll);

                                  }


                              });
            Log.i("Thread ", "run:thread acrtive coundsct  " + Thread.activeCount());
            Set<Thread> threadSet = Thread.getAllStackTraces().keySet();


            for (Thread thread : threadSet) {
           //     Log.i("threadset", "onPreviewFrame: "+thread.getState());
           //     Log.i("threadset", "onPreviewFrame: "+thread.getStackTrace());
            }

            //activity.drawOutput(array, frameWidth, frameHeight);
            //System.out.println("Time before Camera CallBack");
            //System.out.println("Time After Camera CallBack");
        } catch (Exception e) {
            e.printStackTrace();
        }
        camera.addCallbackBuffer(cameraPreviewBuffer);



    }


}

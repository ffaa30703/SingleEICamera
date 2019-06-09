package com.example.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.myapplication.Utils.AssetFileManager;
import com.example.myapplication.Utils.CameraDetectView;
import com.example.myapplication.Utils.SettingActivity;
import com.wf.*;
import com.example.myapplication.cameraController.MainSurfaceView;

import java.util.ArrayList;
import java.util.Arrays;

public class CameraActivity extends AppCompatActivity {

    MainSurfaceView mainSurfaceView;


    CameraDetectView CLRCameraDetectView;
    ToggleButton enroll_button;
    String name;
    String impFilesPath;
    boolean enroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mainSurfaceView= findViewById(R.id.textureview);
        wffrjni.EnableImageSaveForDebugging(1);

        CLRCameraDetectView = (CameraDetectView) findViewById(R.id.cameraDetect1);
        copyAssets();
        mainSurfaceView.setDrawActivity(CameraActivity.this);
        enroll_button = (ToggleButton) findViewById(R.id.enroll_button);
        enroll_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enroll_button.setChecked(false);
                enterNameDialogBox();
            }
        });
        wffrsinglecamapp.finish_state = 1;


        findViewById(R.id.setting_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showPopup(v);
                    }
                });
            }
        });
    }


    public  CameraActivity getInstance(){
        return this;
    }


    private void enterNameDialogBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CameraActivity.this);
        builder.setCancelable(true);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_name_dialog_2, null);
        Rect displayRectangle = new Rect();
        Window window = this.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        view.setMinimumWidth((int)(displayRectangle.width() * 0.5f));
        view.setMinimumHeight((int)(displayRectangle.height() * 0.3f));
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        final EditText firstName = (EditText) view.findViewById(R.id.enter_name);
        builder.setView(view);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //also check when firstname is not empty firstname should always be there!!
                if (firstName.getText().length() > 0 ) {
                    initEnroll(firstName.getText().toString());

                } else {
                    Toast.makeText(CameraActivity.this, "Field Can't be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.show();
        Window windowDialog = alertDialog.getWindow();
        WindowManager.LayoutParams wlp = windowDialog.getAttributes();
        wlp.width = (int)(displayRectangle.width() * 0.50f);
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        windowDialog.setAttributes(wlp);
    }


    private void initEnroll(String firstName){
        Toast.makeText(CameraActivity.this, "Hello " + firstName, Toast.LENGTH_SHORT).show();
        enroll_button.setChecked(true);
        enroll_button.setEnabled(false);
        name = firstName.toString();
        CLRCameraDetectView.isEnrolling(true);
        mainSurfaceView.setEnrolledName(firstName,true);
        wffrsinglecamapp.setState(2);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                wffrsinglecamapp.setState(1);
                name = "";
                enroll_button.setChecked(false);
                enroll_button.setEnabled(true);
                CLRCameraDetectView.isEnrolling(false);
                mainSurfaceView.setEnrolledName("",false);

            }
        },12000);
    }

    private void copyAssets(){
        if(!new AssetFileManager(this).checkFilesExist()){
            (new AssetFileManager(this)).copyFilesFromAssets();//copies files from assests to cache folder
        }
        impFilesPath = (new AssetFileManager(CameraActivity.this).getFilePath()) + "/";
        wffrjni.SetRecognitionThreshold(wffrjni.GetRecognitionThreshold());

        wffrsinglecamapp.setAssetPath(impFilesPath);
    }

    public void drawOutput(final int facesArray[][], final int imageWidth, final int imageHeight, final boolean enroll) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (wffrsinglecamapp.finish_state==-1){
                    finish();
                }

                float confidenceValuesCamera[] = wffrsinglecamapp.getConfidence();//get Confidence Array from NDK
                String nameValuesCamera[] = wffrsinglecamapp.getNames();//get name Array from NDK


                Log.i("DSDS","NAMES : "+ Arrays.toString(nameValuesCamera));
                ArrayList<Integer> leftCornerValues = new ArrayList<>();
                ArrayList<Integer> topCornerValues = new ArrayList<>();
                ArrayList<Integer> rightCornerValues = new ArrayList<>();
                ArrayList<Integer> bottomCornerValues = new ArrayList<>();
                ArrayList<String> nameList = new ArrayList<>();
                ArrayList<Float> confidenceValList = new ArrayList<>();
                if(facesArray!=null){
                    for (int i = 0; i < facesArray.length; i++) {//Run for all the faces detected
                        double perReduce = 0.1;
                        int redWidth = (int) (facesArray[i][2] * perReduce);
                        int redHeight = (int) (facesArray[i][3] * perReduce);

                        int leftCornerValue = (facesArray[i][0] + redWidth);// - (facesArray[i][2] ));//XOriginal - rectWidth
                        int topCornerValue = (facesArray[i][1] + redHeight);//- (facesArray[i][3] ));//yOriginal- rectHeight
                        int rightCornerValue = (facesArray[i][0] + (facesArray[i][2]) - redWidth);//XOriginal + rectWidth
                        int bottomCornerValue = (facesArray[i][1] + (facesArray[i][3]) - redHeight);//yOriginal + rectHeight
                        //System.out.println("LeftCorner: " + leftCornerValue + " i: " + i);
                        leftCornerValues.add(i, leftCornerValue);
                        topCornerValues.add(i, topCornerValue);
                        rightCornerValues.add(i, rightCornerValue);
                        bottomCornerValues.add(i, bottomCornerValue);
                        if (nameValuesCamera.length!=0){
                            nameList.add(i, nameValuesCamera[i]);
                        }
                        if (confidenceValuesCamera.length!=0){
                            confidenceValList.add(i, confidenceValuesCamera[i]);
                        }
                    }


                    CLRCameraDetectView.setRotationValue(0);
                    CLRCameraDetectView.setFrontRotationValue(0);
                    // flipcamera.setRotation(0);

                    long timeLeft = wffrsinglecamapp.getTimeLeft();
                    CLRCameraDetectView.setTimeLeft(timeLeft);
                    //System.out.println("Time of YUV Image After and before rendereing rect");
                    CLRCameraDetectView.setVisibility(View.VISIBLE);

                    CLRCameraDetectView.setRectValuesArray(leftCornerValues, topCornerValues, rightCornerValues, bottomCornerValues);

                    CLRCameraDetectView.setValuesArray(nameList, confidenceValList);

                    float scaleY = (float) CLRCameraDetectView.getHeight() / (float) imageHeight;
                    float scaleX = (float) CLRCameraDetectView.getWidth() / (float) imageWidth;
                    CLRCameraDetectView.setScaleValues(scaleX,scaleY);


                }
                CLRCameraDetectView.invalidate();
            }
        });
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.main_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

//                    case R.id.database:
//                        Intent i1 = new Intent(HomeActivity.this, AppListDialog.class);
//                        startActivity(i1);
//                        return true;
                    case R.id.settings:
                        Intent i2 = new Intent(CameraActivity.this, SettingActivity.class);
                        startActivity(i2);
                        return true;
                    case R.id.aboutus:
                      //  showAboutusDialog();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }

  /*  private void showAboutusDialog(){
        Dialog dialog = new Dialog(CameraActivity.this,R.style.AlertDialogCustom);
        // Include dialog.xml file
        dialog.setContentView(R.layout.about_us_dialog);
        // Set dialog title
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        dialog.setCancelable(true);
    }
*/
    @Override
    protected void onDestroy() {
        super.onDestroy();


    }
}

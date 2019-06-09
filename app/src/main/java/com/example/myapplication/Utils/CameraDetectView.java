package com.example.myapplication.Utils;

/**
 * Created by abhinav on 5/17/18.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.wf.wffrjni;

import java.util.ArrayList;

/**
 * Created by Abhinav on 10/11/2017.
 */

public class CameraDetectView extends View {
    ArrayList<Integer> leftCornerValues;
    ArrayList<Integer> topCornerValues;
    ArrayList<Integer> rightCornerValues;
    ArrayList<Integer> bottomCornerValues;
    ArrayList<String> nameList;
    ArrayList<Float> confidenceValList;

    long timeLeft = 0;


    int rotation = 0;
    int frontRotation = 0;
    boolean enroll;
    int angle;
    public boolean isDebugMode = true;



    public void setDebugMode(boolean flag){
        this.isDebugMode = flag;
    }

    public CameraDetectView(Context context) {
        super(context);
    }

    public CameraDetectView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CameraDetectView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public int cameraHeight,cameraWidth;
    public void setCameraParam(int cameraWidth,int cameraHeight){
        this.cameraHeight = cameraHeight;
        this.cameraWidth = cameraWidth;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        long startDraw = System.currentTimeMillis();
        super.onDraw(canvas);
//        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        Paint paint = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);

        paint.setTextSize(25);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(0xff20960c);
//        paint.setColor(0xff30cd16);
        paint.setStrokeWidth(8);
        canvas.save();
        paint.setAlpha(255);
        paint.setStrokeWidth(16);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(new Rect(0,0,canvas.getWidth(),canvas.getHeight()),paint);

        paint.setStrokeWidth(8);
        paint.setStyle(Paint.Style.FILL);
        if (isDebugMode){
            canvas.drawText("Resolution : "+cameraHeight+" x "+cameraWidth, 150,50,paint);
            paint.setTextSize(35);
            canvas.drawText(START_EXECUTION_TIME_LOG, 150,75,paint);
            canvas.drawText(BEYOND_START_EXECUTION_TIME_LOG, 150,100,paint);
        }

        drawTime(canvas,paint);

        paint.setTextSize(25);
        //canvas.drawColor(0x4D000000);
        // canvas.rotate(rotation, canvas.getWidth() / 2, canvas.getHeight() / 2);
        //int bigger = 80;
        int textPosX = 0;
        int textPosY = 0;
        if (leftCornerValues==null){
            return;
        }
        for (int iValue = 0; iValue < leftCornerValues.size(); iValue++) {
            paint.setStyle(Paint.Style.STROKE);
            leftCornerValues.set(iValue,(int)(leftCornerValues.get(iValue)*ScaleX));
            rightCornerValues.set(iValue,(int)(rightCornerValues.get(iValue)*ScaleX));
            topCornerValues.set(iValue,(int)(topCornerValues.get(iValue)*ScaleY));
            bottomCornerValues.set(iValue,(int)(bottomCornerValues.get(iValue)*ScaleY));
            int leftV = leftCornerValues.get(iValue);
            int rightV = rightCornerValues.get(iValue);
            int topV = topCornerValues.get(iValue);
            int bottomV = bottomCornerValues.get(iValue);
                   /* int leftV = (l+1000) * vWidth/2000;
                    int topV  = (t+1000) * vHeight/2000;
                    int rightV = (r+1000) * vWidth/2000;
                    int bottomV = (b+1000) * vHeight/2000;*/
            if (rotation == -90) {
                bottomV = canvas.getHeight() - topCornerValues.get(iValue);
                topV = canvas.getHeight() - bottomCornerValues.get(iValue);
                // System.out.println("LeftV: " + leftV + " :Right: " + rightV + " top: " + topV);
            } else if (rotation == 90) {
                // bigger+=100;
                leftV = canvas.getWidth() - rightCornerValues.get(iValue);
                rightV = canvas.getWidth() - leftCornerValues.get(iValue);
            }
            else if(rotation == 180){
                bottomV = canvas.getHeight() - topCornerValues.get(iValue);
                topV = canvas.getHeight() - bottomCornerValues.get(iValue);
            }
            else if(frontRotation == 180){
                bottomV = canvas.getHeight() - topCornerValues.get(iValue);
                topV = canvas.getHeight() - bottomCornerValues.get(iValue);
            }
            else if (frontRotation == 90) {
                bottomV = canvas.getHeight() - topCornerValues.get(iValue);
                topV = canvas.getHeight() - bottomCornerValues.get(iValue);
                leftV = canvas.getWidth() - rightCornerValues.get(iValue);
                rightV = canvas.getWidth() - leftCornerValues.get(iValue);
            }
            if (enroll) {

                if (confidenceValList.size()!=0 && confidenceValList.get(iValue)==-1){
                    paint.setColor(0xffFF0000);
                }else {
                    paint.setColor(0xff1a8cff);
                }

                drawGenericRectangle(paint, iValue, canvas);
            } else {

                if (confidenceValList.size()!=0 && confidenceValList.get(iValue) > wffrjni.GetRecognitionThreshold()) {

                    paint.setColor(0xff20960c);
                    drawStyle1Rectangle(leftV,rightV,topV,bottomV,paint,canvas);

                    canvas.rotate(frontRotation,((leftV+rightV)/2),((topV+bottomV)/2));
                    canvas.rotate(rotation,((leftV+rightV)/2),((topV+bottomV)/2));
                    if (nameList.size()>0){
                        canvas.drawText(nameList.get(iValue), (leftV + rightV) / 2, (bottomV + 75), paint);
                    }
//                    canvas.drawText("Authenticated", (leftV + rightV) / 2, (bottomV + 75), paint);
                    canvas.rotate(-frontRotation,((leftV+rightV)/2),((topV+bottomV)/2));
                    canvas.rotate(-rotation,((leftV+rightV)/2),((topV+bottomV)/2));
                } else {

                    if (confidenceValList.get(iValue)== 0){
                        paint.setColor(0xffFFFF00);
                    }
                    else if(confidenceValList.get(iValue)==-1){
                        paint.setColor(0xffFF0000);
                    }
//                    paint.setColor(0xffFF0000);
                    paint.setStyle(Paint.Style.FILL);
                    paint.setTextSize(30);
                    canvas.rotate(frontRotation,((leftV+rightV)/2),((topV+bottomV)/2));
                    canvas.rotate(rotation,((leftV+rightV)/2),((topV+bottomV)/2));
                    canvas.drawText("Confidence: " + Math.round(confidenceValList.get(iValue) * 10.0) / 10.0 + "%", (leftV + rightV) / 2, (bottomV + 75), paint);
                    canvas.rotate(-frontRotation,((leftV+rightV)/2),((topV+bottomV)/2));
                    canvas.rotate(-rotation,((leftV+rightV)/2),((topV+bottomV)/2));
                    paint.setStyle(Paint.Style.STROKE);
                    drawStyle1Rectangle(leftV,rightV,topV,bottomV,paint,canvas);
//                    canvas.drawRect(leftV, topV,
//                            rightV, bottomV, paint);
//                    drawRectangle(leftV,rightV,topV,bottomV,paint, canvas);

                }
            }

            long endDraw = System.currentTimeMillis();
            if (isDebugMode){
                paint.setTextSize(35);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawText("Drawing Time : "+(endDraw-startDraw)+" ms",150,150,paint);
            }
        }
        canvas.restore();
    }

    protected void drawStyle1Rectangle(int leftV,int rightV,int topV,int bottomV,Paint paint,Canvas canvas){

        int bigger = 0;//(int)(0.1f*(rightV-leftV)) ;
        int bigger2 = bigger + 12;
        paint.setStyle(Paint.Style.FILL);
        paint.setAlpha(30);
        paint.setStyle(Paint.Style.STROKE);

        paint.setAlpha(255);
        int lineEnd = (rightV-leftV) / 5;
        int dist=(rightV-leftV)/12;
        int distPlus=(int)dist*1/2;
        int cornerOffset = 1;
        //TopLeft
        canvas.drawLine(leftV-cornerOffset, topV, leftV + lineEnd, topV, paint);
        canvas.drawLine(leftV, topV, leftV, topV + lineEnd, paint);
//                    //TopRight
        canvas.drawLine(rightV - lineEnd, topV, rightV+cornerOffset, topV, paint);
        canvas.drawLine(rightV, topV, rightV, topV + lineEnd, paint);
//                    //BottomLeft
        canvas.drawLine(leftV-cornerOffset, bottomV, leftV + lineEnd+cornerOffset, bottomV, paint);
        canvas.drawLine(leftV, bottomV - lineEnd, leftV, bottomV, paint);
//                    //BottomRight
        canvas.drawLine(rightV - lineEnd, bottomV, rightV+cornerOffset, bottomV, paint);
        canvas.drawLine(rightV, bottomV - lineEnd, rightV, bottomV, paint);

        paint.setStrokeWidth(2);
        float fac = 1.5f;
        //TopLine
        canvas.drawLine(leftV+(int)(fac*lineEnd), topV, rightV-(int)(fac*lineEnd), topV, paint);
        //LeftLine
        canvas.drawLine(leftV, topV+(int)(fac*lineEnd), leftV,bottomV-(int)(fac*lineEnd), paint);
        //BottomLine
        canvas.drawLine(leftV+(int)(fac*lineEnd), bottomV, rightV-(int)(fac*lineEnd), bottomV, paint);
        //RightLine
        canvas.drawLine(rightV, topV+(int)(fac*lineEnd), rightV, bottomV-(int)(fac*lineEnd), paint);
        paint.setStrokeWidth(8);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(30);
        canvas.save();
    }

    public String START_EXECUTION_TIME_LOG= "";
    public void showStartExecutionLog(String log){
        START_EXECUTION_TIME_LOG = log;
    }
    public String BEYOND_START_EXECUTION_TIME_LOG= "";
    public void showBeyondStartExecutionLog(String log){
        BEYOND_START_EXECUTION_TIME_LOG = log;
    }



    boolean isPopupSshowing = false;
    private void showAuthenticatedPopup(String name){
        if(!isPopupSshowing){
            isPopupSshowing = true;
            final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext()).
                    setTitle("Recognition Status").setMessage(name +" Recognised !!");
            final AlertDialog alert = dialog.create();
            alert.show();
            final Handler handler  = new Handler();
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (alert.isShowing()) {
                        alert.dismiss();
                    }
                }
            };

            alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    handler.removeCallbacks(runnable);
                    isPopupSshowing = false;
                }
            });

            handler.postDelayed(runnable, 2000);
        }
    }

    public void drawRectangle(int leftV,int rightV,int topV,int bottomV,Paint paint,Canvas canvas){

        paint.setAlpha(30);
        canvas.drawRect(leftV , topV ,rightV, bottomV, paint);
        //canvas.drawRect(leftV + bigger2, topV + bigger2, rightV - bigger2, bottomV - bigger2, paint);
        paint.setStyle(Paint.Style.STROKE);
        int no_grids=12;
        int dist=(rightV-leftV)/no_grids;
        paint.setStrokeWidth(3);
        paint.setStrokeWidth(7);
        paint.setAlpha(255);
        // paint.setStrokeWidth(12);
        int lineEnd = (rightV-leftV) / 5;// 80;//rightV - leftV / 2;
        int distPlus=(int)dist*1/2;
        //Left
        canvas.drawLine(leftV, topV, leftV + lineEnd, topV, paint);
        canvas.drawLine(leftV, topV, leftV, topV + lineEnd, paint);
        //Right
        canvas.drawLine(rightV - lineEnd, topV, rightV, topV, paint);
        canvas.drawLine(rightV, topV, rightV, topV + lineEnd, paint);
        //BottomLeft
        canvas.drawLine(leftV, bottomV, leftV + lineEnd, bottomV, paint);
        canvas.drawLine(leftV, bottomV - lineEnd, leftV, bottomV, paint);
        //BottomRight
        canvas.drawLine(rightV - lineEnd, bottomV, rightV, bottomV, paint);
        canvas.drawLine(rightV, bottomV - lineEnd, rightV, bottomV, paint);

        canvas.drawLine((rightV+leftV)/2,topV+distPlus,(rightV+leftV)/2, topV-distPlus, paint);
        canvas.drawLine(leftV+distPlus,(topV+bottomV)/2,leftV-distPlus,(topV+bottomV)/2, paint);
        canvas.drawLine((rightV+leftV)/2,bottomV+distPlus,(rightV+leftV)/2, bottomV-distPlus, paint);
        canvas.drawLine(rightV+distPlus,(topV+bottomV)/2,rightV-distPlus,(topV+bottomV)/2, paint);
        // System.out.println("Time after rendering rectangle");
        paint.setStyle(Paint.Style.FILL);

        paint.setTextSize(getPX(18));
    }
    public int getPX(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, getResources().getDisplayMetrics());
    }

    public void drawGenericRectangle(Paint paint, int iValue, Canvas canvas) {

        int leftV = leftCornerValues.get(iValue);
        int rightV = rightCornerValues.get(iValue);
        int topV = topCornerValues.get(iValue);
        int bottomV = bottomCornerValues.get(iValue);
        if (rotation == -90) {
            bottomV = canvas.getHeight() - topCornerValues.get(iValue);
            topV = canvas.getHeight() - bottomCornerValues.get(iValue);
            // System.out.println("LeftV: " + leftV + " :Right: " + rightV + " top: " + topV);
        } else if (rotation == 90) {
            // bigger+=100;
            leftV = canvas.getWidth() - rightCornerValues.get(iValue);
            rightV = canvas.getWidth() - leftCornerValues.get(iValue);
        }
        else if(rotation == 180){
            bottomV = canvas.getHeight() - topCornerValues.get(iValue);
            topV = canvas.getHeight() - bottomCornerValues.get(iValue);
        }
        else if(frontRotation == 180){
            bottomV = canvas.getHeight() - topCornerValues.get(iValue);
            topV = canvas.getHeight() - bottomCornerValues.get(iValue);
        }
        else if (frontRotation == 90) {
            bottomV = canvas.getHeight() - topCornerValues.get(iValue);
            topV = canvas.getHeight() - bottomCornerValues.get(iValue);
            leftV = canvas.getWidth() - rightCornerValues.get(iValue);
            rightV = canvas.getWidth() - leftCornerValues.get(iValue);
        }

        canvas.drawRect(leftV, topV,
                rightV, bottomV, paint);
        if (!enroll) {
            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(30);
            canvas.drawText("Confidence: " + Math.round(confidenceValList.get(iValue) * 10.0) / 10.0 + "%", (leftV + rightV) / 2, (bottomV + 75), paint);
        }
    }

    public void setRectValuesArray(ArrayList<Integer> leftCornerValues, ArrayList<Integer> topCornerValues,
                                   ArrayList<Integer> rightCornerValues, ArrayList<Integer> bottomCornerValues) {
        this.leftCornerValues = leftCornerValues;
        this.topCornerValues = topCornerValues;
        this.rightCornerValues = rightCornerValues;
        this.bottomCornerValues = bottomCornerValues;
//        Log.d("ValuesRect ", "leftList " + leftCornerValues + " topList " + topCornerValues +
//                " rightList " + rightCornerValues + " bottomList " + bottomCornerValues);
    }



    public void drawTime(Canvas canvas, Paint paint) {
        paint.setTextSize(getPX(16));
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xff1a8cff);
        canvas.drawText("Time Left : " + timeLeft, 150,200, paint);
        paint.setColor(0xff20960c);
    }

    public void setTimeLeft(long time){
        this.timeLeft = time;
    }

    public void setRotationValue(int rotation) {
        this.rotation = rotation;

    }

    public void isEnrolling(boolean enroll) {
        this.enroll = enroll;

    }

    public void setFrontRotationValue(int frontRotation) {
        this.frontRotation = frontRotation;

    }

    public float ScaleX =1f,ScaleY=1f;

    public void setScaleValues(float ScaleX,float ScaleY){
        this.ScaleX = ScaleX;
        this.ScaleY = ScaleY;
    }

    public void setValuesArray(ArrayList<String> nameList, ArrayList<Float> confidenceValList) {
        this.nameList = nameList;
        this.confidenceValList = confidenceValList;
    }

}


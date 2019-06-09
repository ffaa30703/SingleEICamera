package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.Utils.AssetFileManager;
import com.example.myapplication.Utils.SettingActivity;
import com.wf.wffrjni;
import com.wf.wffrsinglecamapp;

import java.io.File;

public class CheckActivity extends AppCompatActivity {
    private static final String TO_ENCROLL_DIR_PATH = "/mnt/sdcard/face";
    private static final String TO_RECONIGE_DIR_PATH = "/mnt/sdcard/recognize";
    private int faceCount;


    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, CheckActivity.class));
    }

    private Button btEncroll;
    private TextView tvFaceCount;
    private boolean inEncroll = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);
        btEncroll = findViewById(R.id.bt_encroll);
        tvFaceCount = findViewById(R.id.tv_facecount);


        String impFilesPath = (new AssetFileManager(CheckActivity.this).getFilePath()) + "/";
        wffrjni.SetRecognitionThreshold(wffrjni.GetRecognitionThreshold());
        wffrsinglecamapp.setAssetPath(impFilesPath);

    }

    public void encrollFromFile(View view) {
        String encrollFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "encroll";
//        btEncroll.setEnabled(false);
        inRecognize=false;
        if (inEncroll) {
            inEncroll = false;
        } else {
            inEncroll = true;
            btEncroll.setText("STOP");

            new Thread(new EncrollRunnable(faceCount)).start();

        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        faceCount = getEncrollFace();
        tvFaceCount.setText(faceCount + "");
    }


    @Override
    protected void onStop() {
        super.onStop();
        inEncroll = false;
        inRecognize=false;
    }

    public int getEncrollFace() {
        wffrsinglecamapp.releaseEngine();
        Log.i("SDSD", "INIT : " + wffrsinglecamapp.getDatabase());
        String[] names = (String[]) wffrsinglecamapp.getDatabaseNames();
        return names == null ? 0 : names.length;
    }

    public void toSetting(View view) {
        Intent i2 = new Intent(CheckActivity.this, SettingActivity.class);
        startActivity(i2);
    }


    class EncrollRunnable implements Runnable {
        int faceCount;

        public EncrollRunnable(int faceCount) {
            this.faceCount = faceCount;
        }

        @Override
        public void run() {
//            int index = Integer.parseInt(String.valueOf(etIndex.getText()));
//            int count = Integer.parseInt(String.valueOf(etCount.getText()));


            File dir = new File(TO_ENCROLL_DIR_PATH);
            if (dir == null || !dir.exists()) {
                return;
            }
            File[] imgfs = dir.listFiles();
            if (imgfs == null || imgfs.length == 0) return;
            int amount = 0;
            for (int i = faceCount; i < imgfs.length && inEncroll; i++) {
                File f = imgfs[i];
                final String filename = f.getName();
                if (!filename.endsWith(".jpg") || !f.exists())
                    continue;
                String enName = filename.substring(0, filename.indexOf('.'));
                boolean rst = false;
                try {
                    rst = 0 == wffrsinglecamapp.runEnrollFromJpegFile(f.getAbsolutePath(), enName);
                } catch (Exception e) {
                    Log.e("---------", "run: ------------------");

                }

                if (rst)
                    amount++;
                final int finalI = faceCount + amount;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvFaceCount.setText(finalI + "");
                    }
                });

            }
            wffrsinglecamapp.releaseEngine();
            final int[] databaseRecords = wffrsinglecamapp.getDatabaseRecords();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btEncroll.setEnabled(true);
                    btEncroll.setText("录入");
//                    Toast.makeText(EncrollActivity.this,""+databaseRecords.length,Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    public void startCheck(View view) {
        inEncroll=false;
        Thread thread=new Thread(new RecognizeRunnable());
        thread.start();

    }

    boolean inRecognize = false;

    class RecognizeRunnable implements Runnable {


        @Override
        public void run() {
            inRecognize = true;
            File dir = new File(TO_RECONIGE_DIR_PATH);
            if (dir == null || !dir.exists()) {
                return;
            }
            int count=0;
            File[] imgfs = dir.listFiles();
            if (imgfs == null || imgfs.length == 0) return;
            int amount = 0;
            for (int i = 0; i < imgfs.length && inRecognize; i++) {
                File f = imgfs[i];
                final String filename = f.getName();
                if (!filename.endsWith(".jpg") || !f.exists())
                    continue;
                String enName = filename.substring(0, filename.indexOf('.'));
                boolean rst = false;
                Log.d("--", "run: "+enName);
                try {
                    rst = 0 == wffrsinglecamapp.runRecognizeFromJpegFile(f.getAbsolutePath());

                } catch (Exception e) {
                    Log.e("---------", "run: ------------------");

                }


                if (rst) {
                    wffrsinglecamapp.getFaceCoordinates();
                    String[] names = wffrsinglecamapp.getNames();
                    String name = "";
                    if (names != null) {
                        for (String st : names) {
                            if (!TextUtils.isEmpty(st)&&st.startsWith(enName.trim().substring(0,enName.indexOf('_')))) {
                                count++;
                                Log.d("recognize------", "name: " + st+" == "+enName+"  "+count);
                                break;
                            }
                        }
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });

            }

        }
    }

}

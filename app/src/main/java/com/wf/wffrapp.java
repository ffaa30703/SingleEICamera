package com.wf;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.Semaphore;

public class wffrapp {

    private static final int AssetError = 1;
    private static final int RecordError = 2;
    private static final int InitializeError = 3;
    public static int frInitialized = 0;
    public static int enrollTime = 10000;
    public static int recognizeTime = 100000;
    public static int recognitionSpoofing = 0;
    public static int enrollSpoofing = 0;
    public static int currentState = 0;
    public static long startTime;
    public static int timeRemaining = 0;
    static String assetPath = "";
    static int Process_Running_Error = 50;
    static Object[] DBnames;
    static int[] records;
    private static int state = 0;
    private static int[][] faceCoordinates;
    private static String[] names;
    private static float[] confidence;
    static Semaphore semaphore = new Semaphore(1);


    public static int getState() {
        return state;
    }

    public static void setState(int value)  {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        state = value;

        semaphore.release();

    }

    public static void setAssetPath(String path) {
        assetPath = path;
    }

    public static int getTimeLeft() {
        return timeRemaining;
    }

    public static int startExecution(byte[] cameraData, int frameWidth, int frameHeight, String name)  {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (currentState == 1 && state == 2) {
            System.out.println("WFFRJNI: Recognizing already running, stopping the current process and release resources.");
            System.out.println("WFFRJNI: Release");
            wffrjni.Release();
            frInitialized = 0;

        }
        if (currentState == 2 && state == 1) {
            System.out.println("WFFRJNI: Enrolling already running, stopping the current process and release resources.");
            System.out.println("WFFRJNI: Release");
            wffrjni.Release();
            frInitialized = 0;
        }


        if (assetPath != null && !assetPath.equals("")) {
            if (state == 0) {
                if (frInitialized == 1) {
                    System.out.println("WFFRJNI: Release");
                    wffrjni.Release();
                    frInitialized = 0;
                }
            } else if (state == 2) {
                String lastName = "";
                if (frInitialized == 0) {
                    int init = wffrjni.initialize(assetPath, frameWidth, frameHeight, frameWidth, 1, enrollSpoofing);
                    if (init != 0) {
                        System.out.println("WFFRJNI: Init Error: " + init);
                        semaphore.release();
                        return 3;
                    }

                    System.out.println("WFFRJNI: Enroll Init");
                    if (name != null && name.contains(" ")) {
                        lastName = name.substring(name.lastIndexOf(' '));
                        name = name.substring(0, name.lastIndexOf(' '));
                    }
                    int addRec = wffrjni.addRecord(name, lastName);
                    if (addRec != 0) {
                        System.out.println("WFFRJNI: Adding Record Error: " + addRec);
                        semaphore.release();
                        return 2;
                    }

                    frInitialized = 1;
                    startTime = System.currentTimeMillis();
                }

//                faceCoordinates = wffrjni.enroll(cameraData ,frameWidth, frameHeight);
                faceCoordinates = wffrjni.enrollSingleCamSpoof(cameraData ,frameWidth, frameHeight);
                names = wffrjni.nameValues();
                confidence = wffrjni.confidenceValues();
                long currentSeconds = System.currentTimeMillis();
                long elapsedTime = currentSeconds - startTime;


                if (elapsedTime > (long) enrollTime) {
                    timeRemaining = 0;
                    wffrjni.Release();
                    frInitialized = 0;
                    state = 0;
                } else {
                    timeRemaining = (enrollTime / 1000) - (int) (elapsedTime / 1000);
                }
            } else if (state == 1) {
                long timeMillis1 = System.currentTimeMillis();
                if (frInitialized == 0) {
                    int init = wffrjni.initialize(assetPath, frameWidth, frameHeight, frameWidth, 0, recognitionSpoofing);
                    if (init != 0) {
                        System.out.println("WFFRJNI: Init Recognize Error: " + init);
                        semaphore.release();
                        return 3;
                    }

                    System.out.println("WFFRJNI: Recognize Init");
                    frInitialized = 1;
                    startTime = timeMillis1;
                }

//                faceCoordinates = wffrjni.recognize(cameraData, frameWidth, frameHeight);
                faceCoordinates = wffrjni.recognizeSingleCamSpoof(cameraData, frameWidth, frameHeight);
//                SaveRotated(cameraData,frameWidth,frameHeight,"orignal.jpg");
//                byte[] rotated = wffrjni.rotateImage(cameraData ,frameWidth,frameHeight ,270);
//                byte[] rotated_YUV = new byte[(int)(rotated.length*1.5)];
//                for(int i = 0;i<rotated.length;i++){
//                    rotated_YUV[i] = rotated[i];
//                }
//                for(int i = rotated.length;i<rotated_YUV.length;i++){
//                    rotated_YUV[i] = (byte)(128);
//                }
                //SaveRotated(rotated_YUV,frameHeight,frameWidth,"rotated.jpg");
                names = wffrjni.nameValues();
                confidence = wffrjni.confidenceValues();
                long elapsedTime1 = timeMillis1 - startTime;


                if (elapsedTime1 > (long) recognizeTime) {
                    System.out.println("WFFRJNI: Release");
                    wffrjni.Release();
                    frInitialized = 0;
                    state = 0;
                    timeRemaining = 0;
                } else {
                    timeRemaining = (recognizeTime / 1000 - (int) elapsedTime1 / 1000);
                }
            }
            currentState = state;
            semaphore.release();
            return 0;
        } else {
            semaphore.release();
            return 1;
        }
    }




    public static int verifyImageForEnrollFromJpegBuffer(byte[] jpegBuffer) {

        try {
            semaphore.acquire();
            if (currentState > 0 && frInitialized == 1) {
                System.out.println("WFFRJNI: Video mode already running, stopping the current process and release resources.");
                System.out.println("WFFRJNI: Release");
                wffrjni.Release();
                frInitialized = 0;
                currentState = 0;
                state = 0;
            }


            if (assetPath != null && !assetPath.equals("")) {

                int init = wffrjni.initialize(assetPath, 0, 0, 0, 0, 0);
                if (init != 0) {
                    System.out.println("WFFRJNI: Init Recognize Error: " + init);
                    semaphore.release();
                    return 3;
                }

                System.out.println("WFFRJNI: Recognize Init Done");
                faceCoordinates = wffrjni.VerifyImageForEnrollJpegBuffer(jpegBuffer, jpegBuffer.length);
                names = wffrjni.nameValues();
                confidence = wffrjni.confidenceValues();
                wffrjni.Release();

                semaphore.release();
                return 0;
            } else {
                semaphore.release();
                return 1;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return 1;
        }



    }



    public static void SaveRotated(byte[] frame, int width, int height, String filename){


        Log.i("JUJU",filename+" : "+frame.length);
        File photo=new File(Environment.getExternalStorageDirectory(), filename);
        YuvImage yuvImage = new YuvImage(frame, ImageFormat.NV21, width, height, null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        yuvImage.compressToJpeg(new Rect(0, 0, width, height), 80, out);
        byte[] imageBytes = out.toByteArray();

        if (photo.exists()) {
            photo.delete();
        }

        try {
            FileOutputStream fos=new FileOutputStream(photo.getPath());

            fos.write(imageBytes);
            fos.close();
        }
        catch (java.io.IOException e) {
            Log.e("PictureDemo", "Exception in photoCallback", e);
        }


    }

    /** Force stop recognition/enroll process and release engine instance**/
    public static int stopExecution()
    {
        try {
            semaphore.acquire();
            if (frInitialized == 1)
            {
                wffrjni.Release();
                frInitialized = 0;
                state = 0;
            }

            semaphore.release();
            return 0;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return 1;
        }
    }


    public static int getDatabase() {
        try {
            semaphore.acquire();

            if ((getState() == 0) && (frInitialized == 0)) {
                int init = wffrjni.initialize(assetPath, 0, 0, 0, 0, 0);
                if (init != 0) {
                    System.out.println("WFFRJNI: Init DB Error: " + init);
                    semaphore.release();
                    return 3;
                }

                System.out.println("WFFRJNI: DB Init");

                DBnames = wffrjni.getDbNameList();

                if (getDatabaseNames() != null)
                    records = wffrjni.getDbRecordList(getDatabaseNames().length);

                wffrjni.Release();
                state = 0;
                frInitialized = 0;

                semaphore.release();
                return 0;
            } else {
                semaphore.release();
                return Process_Running_Error;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return 1;
        }
    }


    public static int[][] getFaceCoordinates() {

        return faceCoordinates;
    }

    public static String[] getNames() {
        return names;
    }

    public static float[] getConfidence() {
        return confidence;
    }

    public static Object[] getDatabaseNames() {


        return DBnames;
    }

    public static int[] getDatabaseRecords() {

        return records;
    }

    public static int deletePerson(int recordID) {

        try {
            semaphore.acquire();

            if ((getState() == 0) && (frInitialized == 0)) {
                int init = wffrjni.initialize(assetPath, 0, 0, 0, 0, 0);
                System.out.println("Init: " + init);
                if (init != 0) {
                    System.out.println("WFFRJNI: Init DB Error: " + init);
                    semaphore.release();
                    return 3;
                }
                int val = wffrjni.DeletePersonFromDb(recordID);
                System.out.println("Val: " + val);
                wffrjni.Release();
                state = 0;
                frInitialized = 0;

                semaphore.release();
                return val;
            } else {
                semaphore.release();
                return Process_Running_Error;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return 1;
        }
    }

    public static int deletePersonbyName(String name) {

        try {
            semaphore.acquire();
            if ((getState() == 0) && (frInitialized == 0)) {
                int init = wffrjni.initialize(assetPath, 0, 0, 0, 0, 0);
                System.out.println("Init: " + init);
                if (init != 0) {
                    System.out.println("WFFRJNI: Init DB Error: " + init);
                    semaphore.release();
                    return 3;
                }
                String firstname = name;
                String lastname = "";
                System.out.println("WFFRJNI: Enroll Init");
                if (name != null && name.contains(" ")) {
                    lastname = name.substring(name.lastIndexOf(' '));
                    firstname = name.substring(0, name.lastIndexOf(' '));
                }
                int val = wffrjni.DeletePersonByNameFromDb(firstname,lastname);
                System.out.println("Val: " + val);
                wffrjni.Release();
                state = 0;
                frInitialized = 0;

                semaphore.release();
                return val;
            } else {
                semaphore.release();
                return Process_Running_Error;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return 1;
        }
    }

    public static int ExtractPersonByName(String name) {

        try {
            semaphore.acquire();
            if ((getState() == 0) && (frInitialized == 0)) {
                int init = wffrjni.initialize(assetPath, 0, 0, 0, 0, 0);
                System.out.println("Init: " + init);
                if (init != 0) {
                    System.out.println("WFFRJNI: Init DB Error: " + init);
                    semaphore.release();
                    return 3;
                }
                String firstname = name;
                String lastname = "";
                System.out.println("WFFRJNI: Enroll Init");
                if (name != null && name.contains(" ")) {
                    lastname = name.substring(name.lastIndexOf(' ')+1);
                    firstname = name.substring(0, name.lastIndexOf(' '));
                }
                Log.i("SDSD","FIRST NAME LENGTH : "+firstname.length());
                Log.i("SDSD","LAST NAME LENGTH : "+lastname.length());
                int val = wffrjni.ExtractPersonByName(firstname,lastname);
                System.out.println("Val: " + val);
                wffrjni.Release();
                state = 0;
                frInitialized = 0;

                semaphore.release();
                return val;
            } else {
                semaphore.release();
                return Process_Running_Error;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return 1;
        }
    }

    public static int deleteDatabase() {
        try {
            semaphore.acquire();
            if ((getState() == 0) && (frInitialized == 0)) {
                int init = wffrjni.initialize(assetPath, 0, 0, 0, 0, 0);
                if (init != 0) {
                    System.out.println("WFFRJNI: Init DB Error: " + init);
                    semaphore.release();
                    return 3;
                }
                int val = wffrjni.DeleteDatabase();

                wffrjni.Release();
                state = 0;
                frInitialized = 0;

                semaphore.release();
                return val;
            } else {

                semaphore.release();
                return Process_Running_Error;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return 1;
        }
    }


    public static int latest_pid;

    public static int getLatest_pid() {
        return latest_pid;
    }

    public static void setLatest_pid(int latest_pid) {
        wffrapp.latest_pid = latest_pid;
    }

    public static int runEnrollFromJpegFile(String imageFileName, String name) {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (currentState > 0) {
//            System.out.println("WFFRJNI: Video mode already running, stopping the current process and release resources.");
//            System.out.println("WFFRJNI: Release");
            wffrjni.Release();
            frInitialized = 0;
            currentState = 0;
        }

        if (assetPath != null && !assetPath.equals("")) {

            setLatest_pid(-1);
            int init = wffrjni.initialize(assetPath, 0, 0, 0, 1,0);
            if (init != 0) {
//                System.out.println("WFFRJNI: Init Error: " + init);
                semaphore.release();
                return 3;
            }
            String firstName = name;
            String lastName = "";

//            System.out.println("WFFRJNI: Enroll Init");
            if (name != null && name.contains(" ")) {
                lastName = name.substring(name.lastIndexOf(' '));
                firstName = name.substring(0, name.lastIndexOf(' '));

            }
            int addRec = wffrjni.addRecord(firstName, lastName);
            if (addRec != 0) {
//                System.out.println("WFFRJNI: Adding Record Error: " + addRec);
                semaphore.release();
                return 2;
            }
            setLatest_pid(wffrjni.getLastAddedRecord());

            faceCoordinates = wffrjni.enrollFromImageFile(imageFileName);
            names = wffrjni.nameValues();
            confidence = wffrjni.confidenceValues();

            wffrjni.Release();

            semaphore.release();

            if (faceCoordinates.length>0 && confidence.length>0 && confidence[0]==0){
                return 0;
            }
            else {
                return 1;
            }

        } else {
            semaphore.release();
            return 1;
        }

    }




    public static int runRecognizeFromJpegFile(String imageFileName) {

        try {
            semaphore.acquire();
            if (currentState > 0 && frInitialized == 1) {
                System.out.println("WFFRJNI: Video mode already running, stopping the current process and release resources.");
                System.out.println("WFFRJNI: Release");
                wffrjni.Release();
                frInitialized = 0;
                currentState = 0;
                state = 0;
            }


            if (assetPath != null && !assetPath.equals("")) {

                int init = wffrjni.initialize(assetPath, 0, 0, 0, 0, 0);
                if (init != 0) {
                    System.out.println("WFFRJNI: Init Recognize Error: " + init);
                    semaphore.release();
                    return 3;
                }

                System.out.println("WFFRJNI: Recognize Init");
                faceCoordinates = wffrjni.recognizeFromImageFile(imageFileName);
                names = wffrjni.nameValues();
                confidence = wffrjni.confidenceValues();

                wffrjni.Release();

                semaphore.release();
                return 0;
            } else {
                semaphore.release();
                return 1;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return 1;
        }



    }

    public static int updateFromPCDB()
    {
        try {
            semaphore.acquire();
            if (currentState > 0 && frInitialized == 1) {
                System.out.println("WFFRJNI: Video mode already running, stopping the current process and release resources.");
                System.out.println("WFFRJNI: Release");
                wffrjni.Release();
                frInitialized = 0;
                currentState = 0;
                state = 0;
            }

            if (assetPath != null && !assetPath.equals("")) {

                int init = wffrjni.initialize(assetPath, 0, 0, 0, 0, 0);
                if (init != 0) {
                    System.out.println("WFFRJNI: Init Recognize Error: " + init);
                    semaphore.release();
                    return 3;
                }

                wffrjni.Release();

                semaphore.release();
                return 0;
            } else {
                semaphore.release();
                return 1;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return 1;
        }

    }

}
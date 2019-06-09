
package com.wf;

public class wffrjniID {
    static {
        System.loadLibrary("wffr");
        System.loadLibrary("wffrjniID");

    }

    public wffrjniID() {
    }

    private static native int VerifyLic(String path);

    private static native int initialize(String path, int spoofing);

    public static native int Release();

    private static native int[][] recognize(byte[] frameByteArray, int width, int height);

    private static native int[][] recognizeFromImageFile(String imageFileName);

    private static native int[][] recognizeFromJpegBuffer(byte[] jpegByteArray, int jpegByteArraySize);

    private static native float[] confidenceValues();

    private static native int enroll(byte[] frameByteArray, int width, int height);
   
    private static native int enrollFromImageFile(String imageFileName);

    private static native int enrollFromJpegBuffer(byte[] jpegByteArray, int jpegByteArraySize);

}

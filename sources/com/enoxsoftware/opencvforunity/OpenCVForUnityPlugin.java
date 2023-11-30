package com.enoxsoftware.opencvforunity;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.util.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class OpenCVForUnityPlugin {
    private static final String TAG = "OpenCVForUnityPlugin";

    public static String copyFileFromAssets(Activity activity, String fileName, String dir) {
        File dstFile = new File(activity.getDir(dir, 0), fileName);
        if (!dstFile.exists()) {
            File dstParentFile = dstFile.getParentFile();
            if (!dstParentFile.exists()) {
                dstParentFile.mkdirs();
            }
            copy(activity, fileName, dstFile, true);
        } else {
            AssetFileDescriptor afd = null;
            try {
                AssetFileDescriptor afd2 = activity.getResources().getAssets().openFd(fileName);
                long srcFileLength = afd2.getLength();
                afd2.close();
                if (srcFileLength != dstFile.length()) {
                    copy(activity, fileName, dstFile, false);
                }
                if (afd2 != null) {
                    try {
                        afd2.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            } catch (FileNotFoundException fnfex) {
                Log.e(TAG, "AssetFileDescriptor openFd(). Exception thrown: " + fnfex);
                copy(activity, fileName, dstFile, false);
                if (afd != null) {
                    try {
                        afd.close();
                    } catch (IOException ex2) {
                        ex2.printStackTrace();
                    }
                }
            } catch (Exception ex3) {
                Log.e(TAG, "Exception thrown: " + ex3);
                if (afd != null) {
                    try {
                        afd.close();
                    } catch (IOException ex4) {
                        ex4.printStackTrace();
                    }
                }
            } catch (Throwable th) {
                if (afd != null) {
                    try {
                        afd.close();
                    } catch (IOException ex5) {
                        ex5.printStackTrace();
                    }
                }
                throw th;
            }
        }
        return dstFile.getAbsolutePath();
    }

    /* JADX WARNING: Removed duplicated region for block: B:29:0x0044 A[SYNTHETIC, Splitter:B:29:0x0044] */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0049 A[Catch:{ IOException -> 0x004d }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static void copy(android.app.Activity r8, java.lang.String r9, java.io.File r10, boolean r11) {
        /*
            r4 = 0
            r5 = 0
            android.content.res.Resources r7 = r8.getResources()     // Catch:{ Exception -> 0x005b, all -> 0x0041 }
            android.content.res.AssetManager r0 = r7.getAssets()     // Catch:{ Exception -> 0x005b, all -> 0x0041 }
            java.io.InputStream r4 = r0.open(r9)     // Catch:{ Exception -> 0x005b, all -> 0x0041 }
            java.io.FileOutputStream r6 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x005b, all -> 0x0041 }
            r6.<init>(r10, r11)     // Catch:{ Exception -> 0x005b, all -> 0x0041 }
            r7 = 4096(0x1000, float:5.74E-42)
            byte[] r1 = new byte[r7]     // Catch:{ Exception -> 0x002f, all -> 0x0058 }
        L_0x0017:
            int r2 = r4.read(r1)     // Catch:{ Exception -> 0x002f, all -> 0x0058 }
            r7 = -1
            if (r2 != r7) goto L_0x002a
            if (r4 == 0) goto L_0x0023
            r4.close()     // Catch:{ IOException -> 0x0052 }
        L_0x0023:
            if (r6 == 0) goto L_0x0056
            r6.close()     // Catch:{ IOException -> 0x0052 }
            r5 = r6
        L_0x0029:
            return
        L_0x002a:
            r7 = 0
            r6.write(r1, r7, r2)     // Catch:{ Exception -> 0x002f, all -> 0x0058 }
            goto L_0x0017
        L_0x002f:
            r7 = move-exception
            r5 = r6
        L_0x0031:
            if (r4 == 0) goto L_0x0036
            r4.close()     // Catch:{ IOException -> 0x003c }
        L_0x0036:
            if (r5 == 0) goto L_0x0029
            r5.close()     // Catch:{ IOException -> 0x003c }
            goto L_0x0029
        L_0x003c:
            r3 = move-exception
            r3.printStackTrace()
            goto L_0x0029
        L_0x0041:
            r7 = move-exception
        L_0x0042:
            if (r4 == 0) goto L_0x0047
            r4.close()     // Catch:{ IOException -> 0x004d }
        L_0x0047:
            if (r5 == 0) goto L_0x004c
            r5.close()     // Catch:{ IOException -> 0x004d }
        L_0x004c:
            throw r7
        L_0x004d:
            r3 = move-exception
            r3.printStackTrace()
            goto L_0x004c
        L_0x0052:
            r3 = move-exception
            r3.printStackTrace()
        L_0x0056:
            r5 = r6
            goto L_0x0029
        L_0x0058:
            r7 = move-exception
            r5 = r6
            goto L_0x0042
        L_0x005b:
            r7 = move-exception
            goto L_0x0031
        */
        throw new UnsupportedOperationException("Method not decompiled: com.enoxsoftware.opencvforunity.OpenCVForUnityPlugin.copy(android.app.Activity, java.lang.String, java.io.File, boolean):void");
    }
}

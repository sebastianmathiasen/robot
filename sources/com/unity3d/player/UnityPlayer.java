package com.unity3d.player;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NativeActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.hardware.Camera;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Process;
import android.util.AttributeSet;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import com.unity3d.player.a;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class UnityPlayer extends FrameLayout implements a.C0000a {
    private static Lock D = new ReentrantLock();
    public static Activity currentActivity = null;
    private static boolean p;
    /* access modifiers changed from: private */
    public ProgressBar A = null;
    private Runnable B = new Runnable() {
        public final void run() {
            int k = UnityPlayer.this.nativeActivityIndicatorStyle();
            if (k >= 0) {
                if (UnityPlayer.this.A == null) {
                    ProgressBar unused = UnityPlayer.this.A = new ProgressBar(UnityPlayer.this.m, (AttributeSet) null, new int[]{16842874, 16843401, 16842873, 16843400}[k]);
                    UnityPlayer.this.A.setIndeterminate(true);
                    UnityPlayer.this.A.setLayoutParams(new FrameLayout.LayoutParams(-2, -2, 51));
                    UnityPlayer.this.addView(UnityPlayer.this.A);
                }
                UnityPlayer.this.A.setVisibility(0);
                UnityPlayer.this.bringChildToFront(UnityPlayer.this.A);
            }
        }
    };
    private Runnable C = new Runnable() {
        public final void run() {
            if (UnityPlayer.this.A != null) {
                UnityPlayer.this.A.setVisibility(8);
                UnityPlayer.this.removeView(UnityPlayer.this.A);
                ProgressBar unused = UnityPlayer.this.A = null;
            }
        }
    };
    b a = new b();
    q b = null;
    /* access modifiers changed from: private */
    public boolean c = false;
    private boolean d = false;
    private boolean e = true;
    private final i f;
    /* access modifiers changed from: private */
    public final r g;
    private boolean h = false;
    private t i = new t();
    private final ConcurrentLinkedQueue j = new ConcurrentLinkedQueue();
    private BroadcastReceiver k = null;
    private boolean l = false;
    /* access modifiers changed from: private */
    public ContextWrapper m;
    /* access modifiers changed from: private */
    public SurfaceView n;
    private WindowManager o;
    /* access modifiers changed from: private */
    public boolean q;
    private boolean r = true;
    private int s = 0;
    private int t = 0;
    private final p u;
    private String v = null;
    private NetworkInfo w = null;
    private Bundle x = new Bundle();
    private List y = new ArrayList();
    /* access modifiers changed from: private */
    public u z;

    /* renamed from: com.unity3d.player.UnityPlayer$3  reason: invalid class name */
    class AnonymousClass3 extends BroadcastReceiver {
        final /* synthetic */ UnityPlayer a;

        public void onReceive(Context context, Intent intent) {
            this.a.b();
        }
    }

    enum a {
        PAUSE,
        RESUME,
        QUIT,
        FOCUS_GAINED,
        FOCUS_LOST
    }

    private class b extends Thread {
        ArrayBlockingQueue a = new ArrayBlockingQueue(32);
        boolean b = false;

        b() {
        }

        private void a(a aVar) {
            try {
                this.a.put(aVar);
            } catch (InterruptedException e) {
                interrupt();
            }
        }

        public final void a() {
            a(a.QUIT);
        }

        public final void a(boolean z) {
            a(z ? a.FOCUS_GAINED : a.FOCUS_LOST);
        }

        public final void b() {
            a(a.RESUME);
        }

        public final void c() {
            a(a.PAUSE);
        }

        public final void run() {
            setName("UnityMain");
            while (true) {
                try {
                    a aVar = (a) this.a.take();
                    if (aVar != a.QUIT) {
                        if (aVar == a.RESUME) {
                            this.b = true;
                        } else if (aVar == a.PAUSE) {
                            this.b = false;
                            UnityPlayer.this.executeGLThreadJobs();
                        } else if (aVar == a.FOCUS_LOST && !this.b) {
                            UnityPlayer.this.executeGLThreadJobs();
                        }
                        if (this.b) {
                            while (true) {
                                UnityPlayer.this.executeGLThreadJobs();
                                if (!UnityPlayer.this.isFinishing() && !UnityPlayer.this.nativeRender()) {
                                    UnityPlayer.this.b();
                                }
                                if (this.a.peek() == null) {
                                    if (interrupted()) {
                                        break;
                                    }
                                } else {
                                    break;
                                }
                            }
                        }
                    } else {
                        return;
                    }
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }

    private abstract class c implements Runnable {
        private c() {
        }

        /* synthetic */ c(UnityPlayer unityPlayer, byte b) {
            this();
        }

        public abstract void a();

        public final void run() {
            if (!UnityPlayer.this.isFinishing()) {
                a();
            }
        }
    }

    static {
        new s().a();
        p = false;
        p = loadLibraryStatic("main");
    }

    public UnityPlayer(ContextWrapper contextWrapper) {
        super(contextWrapper);
        if (contextWrapper instanceof Activity) {
            currentActivity = (Activity) contextWrapper;
        }
        this.g = new r(this);
        this.m = contextWrapper;
        this.f = contextWrapper instanceof Activity ? new n(contextWrapper) : null;
        this.u = new p(contextWrapper, this);
        a();
        if (o.a) {
            o.h.a((View) this);
        }
        setFullscreen(true);
        a(this.m.getApplicationInfo());
        if (!t.c()) {
            AlertDialog create = new AlertDialog.Builder(this.m).setTitle("Failure to initialize!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    UnityPlayer.this.b();
                }
            }).setMessage("Your hardware does not support this application, sorry!").create();
            create.setCancelable(false);
            create.show();
            return;
        }
        nativeFile(this.m.getPackageCodePath());
        k();
        this.n = new SurfaceView(contextWrapper);
        this.n.getHolder().setFormat(2);
        this.n.getHolder().addCallback(new SurfaceHolder.Callback() {
            public final void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
                UnityPlayer.this.a(0, surfaceHolder.getSurface());
                UnityPlayer.this.a((c) new c() {
                    {
                        UnityPlayer unityPlayer = UnityPlayer.this;
                    }

                    public final void a() {
                        UnityPlayer.this.h();
                    }
                });
            }

            public final void surfaceCreated(SurfaceHolder surfaceHolder) {
                UnityPlayer.this.a(0, surfaceHolder.getSurface());
            }

            public final void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                UnityPlayer.this.a(0, (Surface) null);
            }
        });
        this.n.setFocusable(true);
        this.n.setFocusableInTouchMode(true);
        this.g.c(this.n);
        this.q = false;
        c();
        initJni(contextWrapper);
        nativeInitWWW(WWW.class);
        if (o.e) {
            o.k.a(this, this.m);
        }
        if (o.d) {
            o.j.a(this);
        }
        this.o = (WindowManager) this.m.getSystemService("window");
        this.m.setTheme(l());
        b(!getStatusBarHidden());
        this.a.start();
    }

    public static native void UnitySendMessage(String str, String str2, String str3);

    private static int a(boolean z2) {
        if (z2) {
            if (Build.VERSION.SDK_INT >= 21) {
                return 16974383;
            }
            return Build.VERSION.SDK_INT >= 14 ? 16973933 : 16973831;
        } else if (Build.VERSION.SDK_INT >= 21) {
            return 16974382;
        } else {
            return Build.VERSION.SDK_INT >= 14 ? 16973932 : 16973830;
        }
    }

    private static String a(String str) {
        byte[] bArr;
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            FileInputStream fileInputStream = new FileInputStream(str);
            long length = new File(str).length();
            fileInputStream.skip(length - Math.min(length, 65558));
            byte[] bArr2 = new byte[1024];
            for (int i2 = 0; i2 != -1; i2 = fileInputStream.read(bArr2)) {
                instance.update(bArr2, 0, i2);
            }
            bArr = instance.digest();
        } catch (FileNotFoundException e2) {
            bArr = null;
        } catch (IOException e3) {
            bArr = null;
        } catch (NoSuchAlgorithmException e4) {
            bArr = null;
        }
        if (bArr == null) {
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (byte b2 : bArr) {
            stringBuffer.append(Integer.toString((b2 & 255) + 256, 16).substring(1));
        }
        return stringBuffer.toString();
    }

    private void a() {
        try {
            File file = new File(this.m.getPackageCodePath(), "assets/bin/Data/settings.xml");
            InputStream fileInputStream = file.exists() ? new FileInputStream(file) : this.m.getAssets().open("bin/Data/settings.xml");
            XmlPullParserFactory newInstance = XmlPullParserFactory.newInstance();
            newInstance.setNamespaceAware(true);
            XmlPullParser newPullParser = newInstance.newPullParser();
            newPullParser.setInput(fileInputStream, (String) null);
            String str = null;
            String str2 = null;
            for (int eventType = newPullParser.getEventType(); eventType != 1; eventType = newPullParser.next()) {
                if (eventType == 2) {
                    str2 = newPullParser.getName();
                    String str3 = str;
                    for (int i2 = 0; i2 < newPullParser.getAttributeCount(); i2++) {
                        if (newPullParser.getAttributeName(i2).equalsIgnoreCase("name")) {
                            str3 = newPullParser.getAttributeValue(i2);
                        }
                    }
                    str = str3;
                } else if (eventType == 3) {
                    str2 = null;
                } else if (eventType == 4 && str != null) {
                    if (str2.equalsIgnoreCase("integer")) {
                        this.x.putInt(str, Integer.parseInt(newPullParser.getText()));
                    } else if (str2.equalsIgnoreCase("string")) {
                        this.x.putString(str, newPullParser.getText());
                    } else if (str2.equalsIgnoreCase("bool")) {
                        this.x.putBoolean(str, Boolean.parseBoolean(newPullParser.getText()));
                    } else if (str2.equalsIgnoreCase("float")) {
                        this.x.putFloat(str, Float.parseFloat(newPullParser.getText()));
                    }
                    str = null;
                }
            }
        } catch (Exception e2) {
            l.Log(6, "Unable to locate player settings. " + e2.getLocalizedMessage());
            b();
        }
    }

    /* access modifiers changed from: private */
    public void a(int i2, Surface surface) {
        if (!this.c) {
            b(0, surface);
        }
    }

    private static void a(ApplicationInfo applicationInfo) {
        if (p && NativeLoader.load(applicationInfo.nativeLibraryDir)) {
            t.a();
        }
    }

    /* access modifiers changed from: private */
    public void a(c cVar) {
        if (!isFinishing()) {
            c((Runnable) cVar);
        }
    }

    static void a(Runnable runnable) {
        new Thread(runnable).start();
    }

    private static String[] a(Context context) {
        String packageName = context.getPackageName();
        Vector vector = new Vector();
        try {
            int i2 = context.getPackageManager().getPackageInfo(packageName, 0).versionCode;
            if (Environment.getExternalStorageState().equals("mounted")) {
                File file = new File(Environment.getExternalStorageDirectory().toString() + "/Android/obb/" + packageName);
                if (file.exists()) {
                    if (i2 > 0) {
                        String str = file + File.separator + "main." + i2 + "." + packageName + ".obb";
                        if (new File(str).isFile()) {
                            vector.add(str);
                        }
                    }
                    if (i2 > 0) {
                        String str2 = file + File.separator + "patch." + i2 + "." + packageName + ".obb";
                        if (new File(str2).isFile()) {
                            vector.add(str2);
                        }
                    }
                }
            }
            String[] strArr = new String[vector.size()];
            vector.toArray(strArr);
            return strArr;
        } catch (PackageManager.NameNotFoundException e2) {
            return new String[0];
        }
    }

    /* access modifiers changed from: private */
    public void b() {
        if ((this.m instanceof Activity) && !((Activity) this.m).isFinishing()) {
            ((Activity) this.m).finish();
        }
    }

    private void b(boolean z2) {
        Window window = ((Activity) this.m).getWindow();
        if (z2) {
            window.clearFlags(1024);
        } else {
            window.setFlags(1024, 1024);
        }
    }

    private boolean b(int i2, Surface surface) {
        if (!t.c()) {
            return false;
        }
        nativeRecreateGfxState(i2, surface);
        return true;
    }

    private void c() {
        m mVar = new m((Activity) this.m);
        if (this.m instanceof NativeActivity) {
            boolean a2 = mVar.a();
            this.l = a2;
            nativeForwardEventsToDalvik(a2);
        }
    }

    private void c(Runnable runnable) {
        if (t.c()) {
            if (Thread.currentThread() == this.a) {
                runnable.run();
            } else {
                this.j.add(runnable);
            }
        }
    }

    private void d() {
        for (a c2 : this.y) {
            c2.c();
        }
    }

    private void e() {
        for (a aVar : this.y) {
            try {
                aVar.a((a.C0000a) this);
            } catch (Exception e2) {
                l.Log(6, "Unable to initialize camera: " + e2.getMessage());
                aVar.c();
            }
        }
    }

    /* access modifiers changed from: private */
    public void f() {
        nativeDone();
    }

    private void g() {
        if (this.i.e()) {
            if (this.z != null) {
                this.z.onResume();
                return;
            }
            this.i.c(true);
            e();
            this.a.b();
            this.u.e();
            this.v = null;
            this.w = null;
            if (t.c()) {
                k();
            }
            c((Runnable) new Runnable() {
                public final void run() {
                    UnityPlayer.this.nativeResume();
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void h() {
        float f2;
        if (this.m instanceof NativeActivity) {
            boolean z2 = o.f && this.e;
            if (getStatusBarHidden() || z2) {
                f2 = 0.0f;
            } else {
                Rect rect = new Rect();
                ((Activity) this.m).getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
                f2 = (float) rect.top;
            }
            nativeSetTouchDeltaY(f2);
        }
    }

    private static void i() {
        if (t.c()) {
            lockNativeAccess();
            if (!NativeLoader.unload()) {
                unlockNativeAccess();
                throw new UnsatisfiedLinkError("Unable to unload libraries from libmain.so");
            }
            t.b();
            unlockNativeAccess();
        }
    }

    private final native void initJni(Context context);

    private boolean j() {
        return this.m.getPackageManager().hasSystemFeature("android.hardware.camera") || this.m.getPackageManager().hasSystemFeature("android.hardware.camera.front");
    }

    private void k() {
        if (this.x.getBoolean("useObb")) {
            for (String str : a((Context) this.m)) {
                String a2 = a(str);
                if (this.x.getBoolean(a2)) {
                    nativeFile(str);
                }
                this.x.remove(a2);
            }
        }
    }

    private int l() {
        int i2;
        try {
            i2 = this.m.getApplicationInfo().theme;
        } catch (Exception e2) {
            l.Log(5, "Failed to obtain current theme, applying best theme available on device");
            i2 = 16973831;
        }
        return i2 == 16973831 ? a(getStatusBarHidden()) : i2;
    }

    protected static boolean loadLibraryStatic(String str) {
        try {
            System.loadLibrary(str);
            return true;
        } catch (UnsatisfiedLinkError e2) {
            l.Log(6, "Unable to find " + str);
            return false;
        } catch (Exception e3) {
            l.Log(6, "Unknown error " + e3);
            return false;
        }
    }

    protected static void lockNativeAccess() {
        D.lock();
    }

    /* access modifiers changed from: private */
    public final native int nativeActivityIndicatorStyle();

    private final native void nativeDone();

    private final native void nativeFile(String str);

    /* access modifiers changed from: private */
    public final native void nativeFocusChanged(boolean z2);

    private final native void nativeInitWWW(Class cls);

    private final native boolean nativeInjectEvent(InputEvent inputEvent);

    /* access modifiers changed from: private */
    public final native boolean nativePause();

    private final native void nativeRecreateGfxState(int i2, Surface surface);

    /* access modifiers changed from: private */
    public final native boolean nativeRender();

    /* access modifiers changed from: private */
    public final native void nativeResume();

    private final native void nativeSetExtras(Bundle bundle);

    /* access modifiers changed from: private */
    public final native void nativeSetInputCanceled(boolean z2);

    /* access modifiers changed from: private */
    public final native void nativeSetInputString(String str);

    private final native void nativeSetTouchDeltaY(float f2);

    /* access modifiers changed from: private */
    public final native void nativeSoftInputClosed();

    /* access modifiers changed from: private */
    public final native void nativeVideoFrameCallback(int i2, byte[] bArr, int i3, int i4);

    protected static void unlockNativeAccess() {
        D.unlock();
    }

    /* access modifiers changed from: protected */
    public boolean Location_IsServiceEnabledByUser() {
        return this.u.a();
    }

    /* access modifiers changed from: protected */
    public void Location_SetDesiredAccuracy(float f2) {
        this.u.b(f2);
    }

    /* access modifiers changed from: protected */
    public void Location_SetDistanceFilter(float f2) {
        this.u.a(f2);
    }

    /* access modifiers changed from: protected */
    public void Location_StartUpdatingLocation() {
        this.u.b();
    }

    /* access modifiers changed from: protected */
    public void Location_StopUpdatingLocation() {
        this.u.c();
    }

    /* access modifiers changed from: package-private */
    public final void b(Runnable runnable) {
        if (this.m instanceof Activity) {
            ((Activity) this.m).runOnUiThread(runnable);
        } else {
            l.Log(5, "Not running Unity from an Activity; ignored...");
        }
    }

    /* access modifiers changed from: protected */
    public void closeCamera(int i2) {
        for (a aVar : this.y) {
            if (aVar.a() == i2) {
                aVar.c();
                this.y.remove(aVar);
                return;
            }
        }
    }

    public void configurationChanged(Configuration configuration) {
        if (this.n instanceof SurfaceView) {
            this.n.getHolder().setSizeFromLayout();
        }
        if (this.z != null) {
            this.z.updateVideoLayout();
        }
    }

    /* access modifiers changed from: protected */
    public void disableLogger() {
        l.a = true;
    }

    public boolean displayChanged(int i2, Surface surface) {
        if (i2 == 0) {
            this.c = surface != null;
            b((Runnable) new Runnable() {
                public final void run() {
                    if (UnityPlayer.this.c) {
                        UnityPlayer.this.g.d(UnityPlayer.this.n);
                    } else {
                        UnityPlayer.this.g.c(UnityPlayer.this.n);
                    }
                }
            });
        }
        return b(i2, surface);
    }

    /* access modifiers changed from: protected */
    public void executeGLThreadJobs() {
        while (true) {
            Runnable runnable = (Runnable) this.j.poll();
            if (runnable != null) {
                runnable.run();
            } else {
                return;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void forwardMotionEventToDalvik(long j2, long j3, int i2, int i3, int[] iArr, float[] fArr, int i4, float f2, float f3, int i5, int i6, int i7, int i8, int i9, long[] jArr, float[] fArr2) {
        this.f.a(j2, j3, i2, i3, iArr, fArr, i4, f2, f3, i5, i6, i7, i8, i9, jArr, fArr2);
    }

    /* access modifiers changed from: protected */
    public int getCameraOrientation(int i2) {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(i2, cameraInfo);
        return cameraInfo.orientation;
    }

    /* access modifiers changed from: protected */
    public int getNumCameras() {
        if (!j()) {
            return 0;
        }
        return Camera.getNumberOfCameras();
    }

    public Bundle getSettings() {
        return this.x;
    }

    /* access modifiers changed from: protected */
    public int getSplashMode() {
        return this.x.getInt("splash_mode");
    }

    /* access modifiers changed from: protected */
    public boolean getStatusBarHidden() {
        return this.x.getBoolean("hide_status_bar", true);
    }

    public View getView() {
        return this;
    }

    /* access modifiers changed from: protected */
    public void hideSoftInput() {
        final AnonymousClass6 r0 = new Runnable() {
            public final void run() {
                if (UnityPlayer.this.b != null) {
                    UnityPlayer.this.b.dismiss();
                    UnityPlayer.this.b = null;
                }
            }
        };
        if (o.g) {
            a((c) new c() {
                public final void a() {
                    UnityPlayer.this.b(r0);
                }
            });
        } else {
            b((Runnable) r0);
        }
    }

    /* access modifiers changed from: protected */
    public void hideVideoPlayer() {
        b((Runnable) new Runnable() {
            public final void run() {
                if (UnityPlayer.this.z != null) {
                    UnityPlayer.this.g.c(UnityPlayer.this.n);
                    UnityPlayer.this.removeView(UnityPlayer.this.z);
                    u unused = UnityPlayer.this.z = null;
                    UnityPlayer.this.resume();
                }
            }
        });
    }

    public void init(int i2, boolean z2) {
    }

    /* access modifiers changed from: protected */
    public int[] initCamera(int i2, int i3, int i4, int i5) {
        a aVar = new a(i2, i3, i4, i5);
        try {
            aVar.a((a.C0000a) this);
            this.y.add(aVar);
            Camera.Size b2 = aVar.b();
            return new int[]{b2.width, b2.height};
        } catch (Exception e2) {
            l.Log(6, "Unable to initialize camera: " + e2.getMessage());
            aVar.c();
            return null;
        }
    }

    public boolean injectEvent(InputEvent inputEvent) {
        return nativeInjectEvent(inputEvent);
    }

    /* access modifiers changed from: protected */
    public boolean installPresentationDisplay(int i2) {
        if (o.e) {
            return o.k.a(this, this.m, i2);
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean isCameraFrontFacing(int i2) {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(i2, cameraInfo);
        return cameraInfo.facing == 1;
    }

    /* access modifiers changed from: protected */
    public boolean isFinishing() {
        if (!this.q) {
            boolean z2 = (this.m instanceof Activity) && ((Activity) this.m).isFinishing();
            this.q = z2;
            if (!z2) {
                return false;
            }
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public void kill() {
        Process.killProcess(Process.myPid());
    }

    /* access modifiers changed from: protected */
    public boolean loadLibrary(String str) {
        return loadLibraryStatic(str);
    }

    /* access modifiers changed from: protected */
    public final native void nativeAddVSyncTime(long j2);

    /* access modifiers changed from: package-private */
    public final native void nativeForwardEventsToDalvik(boolean z2);

    /* access modifiers changed from: protected */
    public native void nativeSetLocation(float f2, float f3, float f4, float f5, double d2, float f6);

    /* access modifiers changed from: protected */
    public native void nativeSetLocationStatus(int i2);

    public void onCameraFrame(a aVar, byte[] bArr) {
        final int a2 = aVar.a();
        final Camera.Size b2 = aVar.b();
        final byte[] bArr2 = bArr;
        final a aVar2 = aVar;
        a((c) new c() {
            public final void a() {
                UnityPlayer.this.nativeVideoFrameCallback(a2, bArr2, b2.width, b2.height);
                aVar2.a(bArr2);
            }
        });
    }

    public boolean onGenericMotionEvent(MotionEvent motionEvent) {
        return injectEvent(motionEvent);
    }

    public boolean onKeyDown(int i2, KeyEvent keyEvent) {
        return injectEvent(keyEvent);
    }

    public boolean onKeyMultiple(int i2, int i3, KeyEvent keyEvent) {
        return injectEvent(keyEvent);
    }

    public boolean onKeyUp(int i2, KeyEvent keyEvent) {
        return injectEvent(keyEvent);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        return injectEvent(motionEvent);
    }

    public void pause() {
        if (this.z != null) {
            this.z.onPause();
            return;
        }
        reportSoftInputStr((String) null, 1, true);
        if (this.i.f()) {
            if (t.c()) {
                final Semaphore semaphore = new Semaphore(0);
                if (isFinishing()) {
                    c((Runnable) new Runnable() {
                        public final void run() {
                            UnityPlayer.this.f();
                            semaphore.release();
                        }
                    });
                } else {
                    c((Runnable) new Runnable() {
                        public final void run() {
                            if (UnityPlayer.this.nativePause()) {
                                boolean unused = UnityPlayer.this.q = true;
                                UnityPlayer.this.f();
                                semaphore.release(2);
                                return;
                            }
                            semaphore.release();
                        }
                    });
                }
                try {
                    if (!semaphore.tryAcquire(4, TimeUnit.SECONDS)) {
                        l.Log(5, "Timeout while trying to pause the Unity Engine.");
                    }
                } catch (InterruptedException e2) {
                    l.Log(5, "UI thread got interrupted while trying to pause the Unity Engine.");
                }
                if (semaphore.drainPermits() > 0) {
                    quit();
                }
            }
            this.i.c(false);
            this.i.b(true);
            d();
            this.a.c();
            this.u.d();
        }
    }

    public void quit() {
        this.q = true;
        if (!this.i.d()) {
            pause();
        }
        this.a.a();
        try {
            this.a.join(4000);
        } catch (InterruptedException e2) {
            this.a.interrupt();
        }
        if (this.k != null) {
            this.m.unregisterReceiver(this.k);
        }
        this.k = null;
        if (t.c()) {
            removeAllViews();
        }
        if (o.e) {
            o.k.a(this.m);
        }
        if (o.d) {
            o.j.a();
        }
        kill();
        i();
    }

    /* access modifiers changed from: protected */
    public void reportSoftInputStr(final String str, final int i2, final boolean z2) {
        if (i2 == 1) {
            hideSoftInput();
        }
        a((c) new c() {
            public final void a() {
                if (z2) {
                    UnityPlayer.this.nativeSetInputCanceled(true);
                } else if (str != null) {
                    UnityPlayer.this.nativeSetInputString(str);
                }
                if (i2 == 1) {
                    UnityPlayer.this.nativeSoftInputClosed();
                }
            }
        });
    }

    public void resume() {
        if (o.a) {
            o.h.b(this);
        }
        this.i.b(false);
        g();
    }

    /* access modifiers changed from: protected */
    public void setFullscreen(final boolean z2) {
        this.e = z2;
        if (o.a) {
            b((Runnable) new Runnable() {
                public final void run() {
                    o.h.a(UnityPlayer.this, z2);
                }
            });
        }
    }

    /* access modifiers changed from: protected */
    public void setSoftInputStr(final String str) {
        b((Runnable) new Runnable() {
            public final void run() {
                if (UnityPlayer.this.b != null && str != null) {
                    UnityPlayer.this.b.a(str);
                }
            }
        });
    }

    /* access modifiers changed from: protected */
    public void showSoftInput(String str, int i2, boolean z2, boolean z3, boolean z4, boolean z5, String str2) {
        final String str3 = str;
        final int i3 = i2;
        final boolean z6 = z2;
        final boolean z7 = z3;
        final boolean z8 = z4;
        final boolean z9 = z5;
        final String str4 = str2;
        b((Runnable) new Runnable() {
            public final void run() {
                UnityPlayer.this.b = new q(UnityPlayer.this.m, this, str3, i3, z6, z7, z8, str4);
                UnityPlayer.this.b.show();
            }
        });
    }

    /* access modifiers changed from: protected */
    public void showVideoPlayer(String str, int i2, int i3, int i4, boolean z2, int i5, int i6) {
        final String str2 = str;
        final int i7 = i2;
        final int i8 = i3;
        final int i9 = i4;
        final boolean z3 = z2;
        final int i10 = i5;
        final int i11 = i6;
        b((Runnable) new Runnable() {
            public final void run() {
                if (UnityPlayer.this.z == null) {
                    UnityPlayer.this.pause();
                    u unused = UnityPlayer.this.z = new u(UnityPlayer.this, UnityPlayer.this.m, str2, i7, i8, i9, z3, (long) i10, (long) i11);
                    UnityPlayer.this.addView(UnityPlayer.this.z);
                    UnityPlayer.this.z.requestFocus();
                    UnityPlayer.this.g.d(UnityPlayer.this.n);
                }
            }
        });
    }

    /* access modifiers changed from: protected */
    public void startActivityIndicator() {
        b(this.B);
    }

    /* access modifiers changed from: protected */
    public void stopActivityIndicator() {
        b(this.C);
    }

    public void windowFocusChanged(final boolean z2) {
        this.i.a(z2);
        if (z2 && this.b != null) {
            reportSoftInputStr((String) null, 1, false);
        }
        if (o.a && z2) {
            o.h.b(this);
        }
        c((Runnable) new Runnable() {
            public final void run() {
                UnityPlayer.this.nativeFocusChanged(z2);
            }
        });
        this.a.a(z2);
        g();
    }
}

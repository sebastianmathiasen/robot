package com.unity3d.player;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.MediaController;
import java.io.FileInputStream;
import java.io.IOException;

public final class u extends FrameLayout implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnVideoSizeChangedListener, SurfaceHolder.Callback, MediaController.MediaPlayerControl {
    private static boolean a = false;
    /* access modifiers changed from: private */
    public final UnityPlayer b;
    private final Context c;
    private final SurfaceView d;
    private final SurfaceHolder e;
    private final String f;
    private final int g;
    private final int h;
    private final boolean i;
    private final long j;
    private final long k;
    private final FrameLayout l;
    private final Display m;
    private int n;
    private int o;
    private int p;
    private int q;
    private MediaPlayer r;
    private MediaController s;
    private boolean t = false;
    private boolean u = false;
    private int v = 0;
    private boolean w = false;
    private int x = 0;
    private boolean y;

    protected u(UnityPlayer unityPlayer, Context context, String str, int i2, int i3, int i4, boolean z, long j2, long j3) {
        super(context);
        this.b = unityPlayer;
        this.c = context;
        this.l = this;
        this.d = new SurfaceView(context);
        this.e = this.d.getHolder();
        this.e.addCallback(this);
        this.e.setType(3);
        this.l.setBackgroundColor(i2);
        this.l.addView(this.d);
        this.m = ((WindowManager) this.c.getSystemService("window")).getDefaultDisplay();
        this.f = str;
        this.g = i3;
        this.h = i4;
        this.i = z;
        this.j = j2;
        this.k = j3;
        if (a) {
            a("fileName: " + this.f);
        }
        if (a) {
            a("backgroundColor: " + i2);
        }
        if (a) {
            a("controlMode: " + this.g);
        }
        if (a) {
            a("scalingMode: " + this.h);
        }
        if (a) {
            a("isURL: " + this.i);
        }
        if (a) {
            a("videoOffset: " + this.j);
        }
        if (a) {
            a("videoLength: " + this.k);
        }
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.y = true;
    }

    private void a() {
        doCleanUp();
        try {
            this.r = new MediaPlayer();
            if (this.i) {
                this.r.setDataSource(this.c, Uri.parse(this.f));
            } else if (this.k != 0) {
                FileInputStream fileInputStream = new FileInputStream(this.f);
                this.r.setDataSource(fileInputStream.getFD(), this.j, this.k);
                fileInputStream.close();
            } else {
                try {
                    AssetFileDescriptor openFd = getResources().getAssets().openFd(this.f);
                    this.r.setDataSource(openFd.getFileDescriptor(), openFd.getStartOffset(), openFd.getLength());
                    openFd.close();
                } catch (IOException e2) {
                    FileInputStream fileInputStream2 = new FileInputStream(this.f);
                    this.r.setDataSource(fileInputStream2.getFD());
                    fileInputStream2.close();
                }
            }
            this.r.setDisplay(this.e);
            this.r.setScreenOnWhilePlaying(true);
            this.r.setOnBufferingUpdateListener(this);
            this.r.setOnCompletionListener(this);
            this.r.setOnPreparedListener(this);
            this.r.setOnVideoSizeChangedListener(this);
            this.r.setAudioStreamType(3);
            this.r.prepare();
            if (this.g == 0 || this.g == 1) {
                this.s = new MediaController(this.c);
                this.s.setMediaPlayer(this);
                this.s.setAnchorView(this);
                this.s.setEnabled(true);
                this.s.show();
            }
        } catch (Exception e3) {
            if (a) {
                a("error: " + e3.getMessage() + e3);
            }
            onDestroy();
        }
    }

    private static void a(String str) {
        Log.v("Video", "VideoPlayer: " + str);
    }

    private void b() {
        if (!isPlaying()) {
            if (a) {
                a("startVideoPlayback");
            }
            updateVideoLayout();
            if (!this.w) {
                start();
            }
        }
    }

    public final boolean canPause() {
        return true;
    }

    public final boolean canSeekBackward() {
        return true;
    }

    public final boolean canSeekForward() {
        return true;
    }

    /* access modifiers changed from: protected */
    public final void doCleanUp() {
        if (this.r != null) {
            this.r.release();
            this.r = null;
        }
        this.p = 0;
        this.q = 0;
        this.u = false;
        this.t = false;
    }

    public final int getBufferPercentage() {
        if (this.i) {
            return this.v;
        }
        return 100;
    }

    public final int getCurrentPosition() {
        if (this.r == null) {
            return 0;
        }
        return this.r.getCurrentPosition();
    }

    public final int getDuration() {
        if (this.r == null) {
            return 0;
        }
        return this.r.getDuration();
    }

    public final boolean isPlaying() {
        boolean z = this.u && this.t;
        return this.r == null ? !z : this.r.isPlaying() || !z;
    }

    public final void onBufferingUpdate(MediaPlayer mediaPlayer, int i2) {
        if (a) {
            a("onBufferingUpdate percent:" + i2);
        }
        this.v = i2;
    }

    public final void onCompletion(MediaPlayer mediaPlayer) {
        if (a) {
            a("onCompletion called");
        }
        onDestroy();
    }

    public final void onControllerHide() {
    }

    /* access modifiers changed from: protected */
    public final void onDestroy() {
        onPause();
        doCleanUp();
        UnityPlayer.a((Runnable) new Runnable() {
            public final void run() {
                u.this.b.hideVideoPlayer();
            }
        });
    }

    public final boolean onKeyDown(int i2, KeyEvent keyEvent) {
        if (i2 != 4 && (this.g != 2 || i2 == 0 || keyEvent.isSystem())) {
            return this.s != null ? this.s.onKeyDown(i2, keyEvent) : super.onKeyDown(i2, keyEvent);
        }
        onDestroy();
        return true;
    }

    /* access modifiers changed from: protected */
    public final void onPause() {
        if (a) {
            a("onPause called");
        }
        if (!this.w) {
            pause();
            this.w = false;
        }
        if (this.r != null) {
            this.x = this.r.getCurrentPosition();
        }
        this.y = false;
    }

    public final void onPrepared(MediaPlayer mediaPlayer) {
        if (a) {
            a("onPrepared called");
        }
        this.u = true;
        if (this.u && this.t) {
            b();
        }
    }

    /* access modifiers changed from: protected */
    public final void onResume() {
        if (a) {
            a("onResume called");
        }
        if (!this.y && !this.w) {
            start();
        }
        this.y = true;
    }

    public final boolean onTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction() & 255;
        if (this.g != 2 || action != 0) {
            return this.s != null ? this.s.onTouchEvent(motionEvent) : super.onTouchEvent(motionEvent);
        }
        onDestroy();
        return true;
    }

    public final void onVideoSizeChanged(MediaPlayer mediaPlayer, int i2, int i3) {
        if (a) {
            a("onVideoSizeChanged called " + i2 + "x" + i3);
        }
        if (i2 != 0 && i3 != 0) {
            this.t = true;
            this.p = i2;
            this.q = i3;
            if (this.u && this.t) {
                b();
            }
        } else if (a) {
            a("invalid video width(" + i2 + ") or height(" + i3 + ")");
        }
    }

    public final void pause() {
        if (this.r != null) {
            this.r.pause();
            this.w = true;
        }
    }

    public final void seekTo(int i2) {
        if (this.r != null) {
            this.r.seekTo(i2);
        }
    }

    public final void start() {
        if (this.r != null) {
            this.r.start();
            this.w = false;
        }
    }

    public final void surfaceChanged(SurfaceHolder surfaceHolder, int i2, int i3, int i4) {
        if (a) {
            a("surfaceChanged called " + i2 + " " + i3 + "x" + i4);
        }
        if (this.n != i3 || this.o != i4) {
            this.n = i3;
            this.o = i4;
            updateVideoLayout();
        }
    }

    public final void surfaceCreated(SurfaceHolder surfaceHolder) {
        if (a) {
            a("surfaceCreated called");
        }
        a();
        seekTo(this.x);
    }

    public final void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (a) {
            a("surfaceDestroyed called");
        }
        doCleanUp();
    }

    /* access modifiers changed from: protected */
    public final void updateVideoLayout() {
        if (a) {
            a("updateVideoLayout");
        }
        if (this.n == 0 || this.o == 0) {
            WindowManager windowManager = (WindowManager) this.c.getSystemService("window");
            this.n = windowManager.getDefaultDisplay().getWidth();
            this.o = windowManager.getDefaultDisplay().getHeight();
        }
        int i2 = this.n;
        int i3 = this.o;
        float f2 = ((float) this.p) / ((float) this.q);
        float f3 = ((float) this.n) / ((float) this.o);
        if (this.h == 1) {
            if (f3 <= f2) {
                i3 = (int) (((float) this.n) / f2);
            } else {
                i2 = (int) (((float) this.o) * f2);
            }
        } else if (this.h == 2) {
            if (f3 >= f2) {
                i3 = (int) (((float) this.n) / f2);
            } else {
                i2 = (int) (((float) this.o) * f2);
            }
        } else if (this.h == 0) {
            i2 = this.p;
            i3 = this.q;
        }
        if (a) {
            a("frameWidth = " + i2 + "; frameHeight = " + i3);
        }
        this.l.updateViewLayout(this.d, new FrameLayout.LayoutParams(i2, i3, 17));
    }
}

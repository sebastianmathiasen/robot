package com.unity3d.player;

import android.app.Presentation;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public final class j implements g {
    /* access modifiers changed from: private */
    public Object a = new Object[0];
    /* access modifiers changed from: private */
    public Presentation b;
    private DisplayManager.DisplayListener c;

    public final void a(Context context) {
        DisplayManager displayManager;
        if (this.c != null && (displayManager = (DisplayManager) context.getSystemService("display")) != null) {
            displayManager.unregisterDisplayListener(this.c);
        }
    }

    public final void a(final UnityPlayer unityPlayer, Context context) {
        DisplayManager displayManager = (DisplayManager) context.getSystemService("display");
        if (displayManager != null) {
            displayManager.registerDisplayListener(new DisplayManager.DisplayListener() {
                public final void onDisplayAdded(int i) {
                    unityPlayer.displayChanged(-1, (Surface) null);
                }

                public final void onDisplayChanged(int i) {
                    unityPlayer.displayChanged(-1, (Surface) null);
                }

                public final void onDisplayRemoved(int i) {
                    unityPlayer.displayChanged(-1, (Surface) null);
                }
            }, (Handler) null);
        }
    }

    public final boolean a(final UnityPlayer unityPlayer, final Context context, int i) {
        Display display;
        synchronized (this.a) {
            if (this.b != null && this.b.isShowing() && (display = this.b.getDisplay()) != null && display.getDisplayId() == i) {
                return true;
            }
            DisplayManager displayManager = (DisplayManager) context.getSystemService("display");
            if (displayManager == null) {
                return false;
            }
            final Display display2 = displayManager.getDisplay(i);
            if (display2 == null) {
                return false;
            }
            unityPlayer.b((Runnable) new Runnable() {
                public final void run() {
                    synchronized (j.this.a) {
                        if (j.this.b != null) {
                            j.this.b.dismiss();
                        }
                        Presentation unused = j.this.b = new Presentation(context, display2) {
                            /* access modifiers changed from: protected */
                            public final void onCreate(Bundle bundle) {
                                SurfaceView surfaceView = new SurfaceView(context);
                                surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                                    public final void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
                                        unityPlayer.displayChanged(1, surfaceHolder.getSurface());
                                    }

                                    public final void surfaceCreated(SurfaceHolder surfaceHolder) {
                                        unityPlayer.displayChanged(1, surfaceHolder.getSurface());
                                    }

                                    public final void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                                        unityPlayer.displayChanged(1, (Surface) null);
                                    }
                                });
                                setContentView(surfaceView);
                            }

                            public final void onDisplayRemoved() {
                                dismiss();
                                synchronized (j.this.a) {
                                    Presentation unused = j.this.b = null;
                                }
                            }
                        };
                        j.this.b.show();
                    }
                }
            });
            return true;
        }
    }
}

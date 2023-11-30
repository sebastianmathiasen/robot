package com.unity3d.player;

import android.app.NativeActivity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class UnityPlayerNativeActivity extends NativeActivity {
    protected UnityPlayer mUnityPlayer;

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        return keyEvent.getAction() == 2 ? this.mUnityPlayer.injectEvent(keyEvent) : super.dispatchKeyEvent(keyEvent);
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.mUnityPlayer.configurationChanged(configuration);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        requestWindowFeature(1);
        super.onCreate(bundle);
        getWindow().takeSurface((SurfaceHolder.Callback2) null);
        getWindow().setFormat(2);
        this.mUnityPlayer = new UnityPlayer(this);
        setContentView(this.mUnityPlayer);
        this.mUnityPlayer.requestFocus();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        this.mUnityPlayer.quit();
        super.onDestroy();
    }

    public boolean onGenericMotionEvent(MotionEvent motionEvent) {
        return this.mUnityPlayer.injectEvent(motionEvent);
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        return this.mUnityPlayer.injectEvent(keyEvent);
    }

    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        return this.mUnityPlayer.injectEvent(keyEvent);
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        this.mUnityPlayer.pause();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        this.mUnityPlayer.resume();
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        return this.mUnityPlayer.injectEvent(motionEvent);
    }

    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        this.mUnityPlayer.windowFocusChanged(z);
    }
}

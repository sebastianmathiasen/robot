package com.vishnu.speech;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;
import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends UnityPlayerActivity implements RecognitionListener {
    private static String confidence = "";
    private static String speechResult = "";
    private String LOG_TAG = "SpeakNow";
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private Activity _activity;
    private Intent intent;
    BroadcastReceiver mBroadcastReceiver;
    /* access modifiers changed from: private */
    public Intent recognizerIntent;
    /* access modifiers changed from: private */
    public SpeechRecognizer speech = null;

    public static String getSpeechResult() {
        return speechResult;
    }

    public void setSpeechResult(String speechResult2) {
        speechResult = speechResult2;
    }

    public static String getConfidence() {
        return confidence;
    }

    public void setConfidence(String confidence2) {
        confidence = confidence2;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this._activity = UnityPlayer.currentActivity;
        this.recognizerIntent = new Intent("android.speech.action.RECOGNIZE_SPEECH");
        this.recognizerIntent.putExtra("android.speech.extra.LANGUAGE_PREFERENCE", "en");
        this.recognizerIntent.putExtra("calling_package", getPackageName());
        this.recognizerIntent.putExtra("android.speech.extra.LANGUAGE_MODEL", "web_search");
        this.recognizerIntent.putExtra("android.speech.extra.MAX_RESULTS", 3);
        this.intent = new Intent("android.speech.action.RECOGNIZE_SPEECH");
        this.intent.putExtra("android.speech.extra.LANGUAGE_MODEL", "free_form");
        this.intent.putExtra("android.speech.extra.LANGUAGE", Locale.getDefault());
        this.intent.putExtra("android.speech.extra.PROMPT", "Say something…");
    }

    public void startSpeech() {
        try {
            runOnUiThread(new Runnable() {
                public void run() {
                    MainActivity.this.speech.startListening(MainActivity.this.recognizerIntent);
                }
            });
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), "Sorry! Your device doesn't support speech input", 0).show();
        }
    }

    public void startSpeechPopup() {
        try {
            startActivityForResult(this.intent, 100);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), "Sorry! Your device doesn't support speech input", 0).show();
        }
    }

    public void makeToast(final String msg) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this.getApplicationContext(), msg, 0).show();
            }
        });
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100:
                if (resultCode == -1 && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra("android.speech.extra.RESULTS");
                    confidence = String.valueOf(data.getFloatArrayExtra("android.speech.extra.CONFIDENCE_SCORES")[0]);
                    speechResult = result.get(0);
                    return;
                }
                return;
            default:
                return;
        }
    }

    public void onResume() {
        super.onResume();
        Log.i(this.LOG_TAG, "onresume called");
        this.speech = SpeechRecognizer.createSpeechRecognizer(this);
        this.speech.setRecognitionListener(this);
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        Log.i(this.LOG_TAG, "onpause called");
        this.speech.destroy();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        this.speech.destroy();
        Log.i(this.LOG_TAG, "ondestroy called");
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        Log.i(this.LOG_TAG, "onstop called");
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void onReadyForSpeech(Bundle params) {
    }

    public void onBeginningOfSpeech() {
    }

    public void onRmsChanged(float rmsdB) {
    }

    public void onBufferReceived(byte[] buffer) {
    }

    public void onEndOfSpeech() {
    }

    public void onError(int error) {
        speechResult = getErrorText(error);
        this.speech.cancel();
    }

    public void onResults(Bundle results) {
        speechResult = results.getStringArrayList("results_recognition").get(0);
        confidence = String.valueOf(results.getFloatArray("confidence_scores")[0]);
    }

    public void onPartialResults(Bundle partialResults) {
    }

    public void onEvent(int eventType, Bundle params) {
    }

    public String getErrorText(int errorCode) {
        switch (errorCode) {
            case 1:
                return "Network timeout";
            case 2:
                return "Network error";
            case 3:
                return "Audio recording error";
            case 4:
                return "error from server";
            case 5:
                return "Client side error";
            case 6:
                return "No speech input";
            case 7:
                return "No match";
            case 8:
                return "Service busy..Please Try Again";
            case 9:
                return "Insufficient permissions";
            default:
                return "Didn't understand, please try again.";
        }
    }
}

package com.frecre.plugin;

import android.app.Activity;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class EasyTTSUtil {
    /* access modifiers changed from: private */
    public static String nowEngineName = "";
    /* access modifiers changed from: private */
    public static TextToSpeech textToSpeech;

    public static void initialize(Activity activity, final String local) {
        if (textToSpeech == null) {
            textToSpeech = new TextToSpeech(activity, new TextToSpeech.OnInitListener() {
                public void onInit(int status) {
                    if (EasyTTSUtil.textToSpeech != null) {
                        EasyTTSUtil.setLocal(local);
                        EasyTTSUtil.nowEngineName = EasyTTSUtil.textToSpeech.getDefaultEngine();
                    }
                }
            });
        } else {
            setLocal(local);
        }
    }

    /* access modifiers changed from: private */
    public static void setLocal(String local) {
        String[] localStr = local.split("-");
        try {
            textToSpeech.setLanguage(new Locale(localStr[0], localStr[1]));
        } catch (Exception e) {
            Log.d("EasyTTSUtil", e.toString());
        }
    }

    public static void initialize(Activity activity, final String local, final String engineName) {
        if (textToSpeech == null || !nowEngineName.equals(engineName)) {
            textToSpeech = new TextToSpeech(activity, new TextToSpeech.OnInitListener() {
                public void onInit(int status) {
                    EasyTTSUtil.nowEngineName = engineName;
                    EasyTTSUtil.setLocal(local);
                }
            }, engineName);
        } else {
            setLocal(local);
        }
    }

    public static void speechAdd(Activity activity, String text) {
        speech(activity, text, 1);
    }

    public static void speechFrush(Activity activity, String text) {
        speech(activity, text, 0);
    }

    public static void stop(Activity activity) {
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
    }

    public static void shotDown() {
        if (textToSpeech != null) {
            textToSpeech.shutdown();
        }
    }

    public static void openTTSSetting(Activity activity) {
        Intent intent = new Intent();
        intent.setAction("com.android.settings.TTS_SETTINGS");
        intent.setFlags(268435456);
        activity.startActivity(intent);
    }

    private static void speech(Activity activity, String text, int queueMode) {
        if (textToSpeech == null) {
            initialize(activity, "en-US");
        } else if (textToSpeech.speak(text, queueMode, (HashMap) null) < 0) {
            Log.e("EasyTextToSpeech ", "TextToSpeech is not available");
        }
    }

    public static String getDefaultEngineName() {
        if (textToSpeech == null) {
            return null;
        }
        String textStr = "";
        String enginePkg = textToSpeech.getDefaultEngine();
        try {
            for (TextToSpeech.EngineInfo inf : textToSpeech.getEngines()) {
                if (inf.name.equals(enginePkg)) {
                    textStr = inf.label;
                }
            }
            return textStr;
        } catch (Exception e) {
            return enginePkg;
        }
    }

    public static String getDefaultEnginePkg() {
        if (textToSpeech == null) {
            return null;
        }
        String textStr = "";
        String enginePkg = textToSpeech.getDefaultEngine();
        try {
            for (TextToSpeech.EngineInfo inf : textToSpeech.getEngines()) {
                if (inf.name.equals(enginePkg)) {
                    textStr = inf.name;
                }
            }
            return textStr;
        } catch (Exception e) {
            return enginePkg;
        }
    }

    public static String[] getEnginePkgArray() {
        String[] enginePkgArray = null;
        if (textToSpeech != null) {
            try {
                List<TextToSpeech.EngineInfo> infoList = textToSpeech.getEngines();
                enginePkgArray = new String[infoList.size()];
                for (int i = 0; i < infoList.size(); i++) {
                    enginePkgArray[i] = infoList.get(i).name;
                }
            } catch (Exception e) {
            }
        }
        return enginePkgArray;
    }

    public static String[] getEngineNameArray() {
        String[] engineNameArray = null;
        if (textToSpeech != null) {
            try {
                List<TextToSpeech.EngineInfo> infoList = textToSpeech.getEngines();
                engineNameArray = new String[infoList.size()];
                for (int i = 0; i < infoList.size(); i++) {
                    engineNameArray[i] = infoList.get(i).label;
                }
            } catch (Exception e) {
                Log.e("EasyTTSUtil", e.toString());
            }
        }
        return engineNameArray;
    }

    public static boolean IsEnable(Activity activity, String local) {
        if (textToSpeech == null) {
            Log.e("EasyTextToSpeech", "isEnable need to call after done initialize");
            initialize(activity, local);
            return false;
        }
        String[] localStr = local.split("-");
        if (textToSpeech.isLanguageAvailable(new Locale(localStr[0], localStr[1])) >= 0) {
            return true;
        }
        return false;
    }
}

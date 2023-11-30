package com.unity3d.player;

import android.os.Build;

public final class o {
    static final boolean a = (Build.VERSION.SDK_INT >= 11);
    static final boolean b = (Build.VERSION.SDK_INT >= 12);
    static final boolean c = (Build.VERSION.SDK_INT >= 14);
    static final boolean d = (Build.VERSION.SDK_INT >= 16);
    static final boolean e = (Build.VERSION.SDK_INT >= 17);
    static final boolean f = (Build.VERSION.SDK_INT >= 19);
    static final boolean g;
    static final f h = (a ? new d() : null);
    static final e i = (b ? new c() : null);
    static final h j = (d ? new k() : null);
    static final g k;

    static {
        j jVar = null;
        boolean z = true;
        if (Build.VERSION.SDK_INT < 21) {
            z = false;
        }
        g = z;
        if (e) {
            jVar = new j();
        }
        k = jVar;
    }
}

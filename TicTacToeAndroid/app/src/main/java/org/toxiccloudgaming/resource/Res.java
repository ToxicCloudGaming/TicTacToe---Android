package org.toxiccloudgaming.resource;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;

public abstract class Res {

    public static String getString(Activity activity, String res) {
        int id = activity.getResources().getIdentifier(res, "string", activity.getPackageName());
        return activity.getString(id);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static int getColor(Activity activity, String res) {
        int id = activity.getResources().getIdentifier(res, "color", activity.getPackageName());
        return activity.getColor(id);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static Drawable getDrawable(Activity activity, String res) {
        int id = activity.getResources().getIdentifier(res, "drawable", activity.getPackageName());
        return activity.getDrawable(id);
    }
}

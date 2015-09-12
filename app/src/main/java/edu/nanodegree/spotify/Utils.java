package edu.nanodegree.spotify;

import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Display;
import android.view.WindowManager;

import java.util.List;

import kaaes.spotify.webapi.android.models.Image;

public final class Utils {

    public static int getSizeIndex(List<Image> lista) {
        return getSizeIndex(lista, 200);
    }

    public static int getSizeIndex(List<Image> lista, int desiredSize) {
        int currentDistance = 10000;
        int result = -1;

        for (int i=0;  lista.size() > i; i++) {
            int distance = Math.abs(lista.get(i).width - desiredSize);
            if (distance <= currentDistance) {
                result = i;
                currentDistance = distance;
            }
        }
        return result;
    }

    public static int getScreenWidth(Context context) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            return 600;
        }
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        return width;
    }

    // Following
    // http://stackoverflow.com/questions/4238921/detect-whether-there-is-an-internet-connection-available-on-android
    public static boolean isNetworkAvailable(Context c) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
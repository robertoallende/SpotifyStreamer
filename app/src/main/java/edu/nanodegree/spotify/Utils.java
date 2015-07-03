package edu.nanodegree.spotify;

import java.util.List;

import kaaes.spotify.webapi.android.models.Image;

public final class Utils {

    private Utils() {
    }

    public static int getSizeIndex(List<Image> lista) {
        int desiredSize = 200;
        int currentDistance = 1000;
        int result = -1;

        for (int i=0;  lista.size() > i; i++) {
            int distance = Math.abs(lista.get(0).width - desiredSize);
            if (distance <= currentDistance) {
                result = i;
                currentDistance = distance;
            }
        }
        return result;
    }
}

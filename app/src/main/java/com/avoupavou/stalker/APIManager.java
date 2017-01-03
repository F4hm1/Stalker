package com.avoupavou.stalker;

import android.content.Context;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;


/**
 * Created by DiVouz on 12/29/2016.
 */

public class ApiManager {
    private static boolean isPlacesAPIInit =false;
    private static boolean isAwarenessAPIInit =false;
    private static GoogleApiClient awarenessApiClient;
    private static GoogleApiClient placesApiClient;

    public ApiManager() {

    }

    public static GoogleApiClient getAwarenessApiClient(Context context) {
        if(!isAwarenessAPIInit){
            awarenessApiClient = new GoogleApiClient
                    .Builder(context)
                    .addApi(Awareness.API)
                    .build();
            awarenessApiClient.connect();
            isAwarenessAPIInit = true;
        }
        return awarenessApiClient;
    }

    public static GoogleApiClient getPlacesApiClient(Context context) {
        if(!isPlacesAPIInit){
            placesApiClient = new GoogleApiClient
                    .Builder(context)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    //.enableAutoManage(, this)
                    .build();
        }
        return placesApiClient;
    }
}

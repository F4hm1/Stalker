package com.avoupavou.stalker.activities;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.avoupavou.stalker.R;
import com.avoupavou.stalker.fragments.AuthenticateFragment;
import com.avoupavou.stalker.fragments.PlaceFragment;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, PlaceFragment.OnFragmentInteractionListener, AuthenticateFragment.OnFragmentInteractionListener {
    final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 123;
    GoogleApiClient client;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());

        setContentView(R.layout.activity_main);

        client = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(Awareness.API)
                .build();
        client.connect();

        fragmentManager = getFragmentManager();

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    public void goToFacebook(View view) {
        fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = new AuthenticateFragment();
        fragmentTransaction.add(fragment, "facebook_frag_tag");
        fragmentTransaction.commit();
    }

    public void startLocationFragment() {
        fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = new PlaceFragment();
        fragmentTransaction.replace(R.id.welcome_fragment_id, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {
        googleMap.getUiSettings().setAllGesturesEnabled(false);


        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return;
        }
        googleMap.setMaxZoomPreference(20.0f);
        googleMap.setMinZoomPreference(10.0f);
        final LatLng[] currentLocation = new LatLng[1];

        Awareness.SnapshotApi.getLocation(client)
                .setResultCallback(new ResultCallback<LocationResult>() {
                    @Override
                    public void onResult(@NonNull LocationResult locationResult) {
                        if (!locationResult.getStatus().isSuccess()) {
                            Log.e("", "Could not get location.");
                            return;
                        }
                        Location location = locationResult.getLocation();
                        currentLocation[0] = new LatLng(location.getLatitude(), location.getLongitude());
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation[0], 12.0f));
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(16.0f), 3000, null);
                    }
                });
    }

    @Override
    public void onFragmentInteraction(String name) {
        Log.d("MainActivity", name);
        Intent toPeopleIntent = new Intent(getApplicationContext(), PeopleListActivity.class);
        toPeopleIntent.putExtra("placeName", name);
        startActivity(toPeopleIntent);
    }

    @Override
    public void onFragmentInteraction() {
        Log.d("MainActivity", "Atuhedication ok");
        startLocationFragment();
    }
}

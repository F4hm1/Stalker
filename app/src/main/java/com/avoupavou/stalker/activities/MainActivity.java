package com.avoupavou.stalker.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.avoupavou.stalker.R;
import com.avoupavou.stalker.fragments.AuthenticateFragment;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends AppCompatActivity {
    private boolean facebookAuthenticated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());

        setContentView(R.layout.activity_main);

        GoogleApiClient client = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(Awareness.API)
                .build();
        client.connect();
    }

    public void goToFacebook(View view) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = new AuthenticateFragment();
        fragmentTransaction.add(fragment, "facebook_frag_tag");
        fragmentTransaction.commit();
    }

}

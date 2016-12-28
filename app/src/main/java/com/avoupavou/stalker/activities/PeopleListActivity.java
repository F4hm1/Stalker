package com.avoupavou.stalker.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.avoupavou.stalker.R;
import com.avoupavou.stalker.RecyclerItemClickListener;
import com.avoupavou.stalker.people.PeopleListAdapter;
import com.avoupavou.stalker.people.Person;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PeopleListActivity extends AppCompatActivity {


    private String TAG = "PeopleActivity";

    private String dataKey;

    private FirebaseAuth fireBaseAuth;
    private String placeName;
    private DatabaseReference mPlaceReference;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener childEventListener;


    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<Person> myDataset;
    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_list);
        Intent locationIntent = getIntent();
        placeName = locationIntent.getStringExtra("placeName");

        myDataset = new ArrayList<>();

        listItemListenerInit();

        Log.d(TAG, placeName);

        fireBaseAuth = FirebaseAuth.getInstance();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mPlaceReference = mDatabaseReference.child(placeName);

        childEventListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if (dataSnapshot.getKey() != dataKey) {
                    Person person = dataSnapshot.getValue(Person.class);
                    person.setName(person.getName());
                    myDataset.add(person);
                    mAdapter.notifyItemInserted(myDataset.size() - 1);
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Person person = dataSnapshot.getValue(Person.class);
                int n = findPerson(person.getName());
                if (dataSnapshot.getKey() != dataKey && n >= 0) {
                    myDataset.remove(n);
                    mAdapter.notifyItemRemoved(n);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mPlaceReference.addChildEventListener(childEventListener);


    }


    public void listItemListenerInit() {
        mRecyclerView = (RecyclerView) findViewById(R.id.people_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        myDataset = new ArrayList<>();
        mAdapter = new PeopleListAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Person person = myDataset.get(position);
                        String id = person.getId();
                        String url = "https://www.facebook.com/ " + id;
                        Intent intent;
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://facewebmodal/f?href=" + url));
                        //intent = new  Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/"+id));
                        startActivity(intent);
                    }
                })
        );
    }

    public void facebookGraphRequest() {
        if (!FacebookSdk.isInitialized()) {
            FacebookSdk.sdkInitialize(getApplicationContext());
            AppEventsLogger.activateApp(getApplication());
        }
        GraphRequest request = GraphRequest.newMeRequest(
                //loginResult.getAccessToken(),
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.v(TAG, response.toString());

                        // Application code
                        try {
                            if (object == null) return;
                            Person person = new Person();
                            String name = object.getString("name");
                            String gender = object.getString("gender"); // 01/31/1980 format
                            String id = object.getString("id");
                            Log.d(TAG, "Name: " + name + ". Gender: " + gender);
                            person.setName(name);
                            person.setId(id);
                            person.setLink("http://www.facebook.com/" + id);

                            dataKey = mPlaceReference.push().getKey();
                            mPlaceReference.child(dataKey).setValue(person);


                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,birthday");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPlaceReference.child(dataKey).removeValue();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlaceReference.removeEventListener(childEventListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        facebookGraphRequest();
    }

    public int findPerson(String ref) {
        for (int i = 0; i < myDataset.size(); i++) {
            if (myDataset.get(i).getName().equals(ref)) {
                return i;
            }
        }
        return -1;
    }

}

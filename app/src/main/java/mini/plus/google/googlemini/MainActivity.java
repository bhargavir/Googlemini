package mini.plus.google.googlemini;

import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<People.LoadPeopleResult> {

    // Declaring Your View and Variables
    Toolbar toolbar;
    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[] = {"Profile", "Orgs"};
    int Numboftabs = 2;

    // GoogleAPIClient
    private static final int RC_SIGN_IN = 0;
    private GoogleApiClient mGoogleApiClient;
    private boolean mIntentInProgress;

    List<String> userIds = new ArrayList<String>();
    HashMap hm = new HashMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .build();

        // Creating The Toolbar and setting it as the Toolbar for the activity
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle(Html.fromHtml("<font color='#ffffff'>Google+ Mini</font>"));
        setSupportActionBar(toolbar);

        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), Titles, Numboftabs);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_signout) {
            if (mGoogleApiClient.isConnected()) {
                Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                mGoogleApiClient.disconnect();
                mGoogleApiClient.connect();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(getApplicationContext(), "Connected.", Toast.LENGTH_SHORT).show();
        try {
            userIds.clear();
            //Tab1
            getProfileInformation();
            //Tab2
            getOrgInformation();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getProfileInformation() {
        Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
        Person.Image personPicture = currentPerson.getImage();
        String personName = currentPerson.getDisplayName();
        List<Person.Organizations> personOrganizations = currentPerson.getOrganizations();
        String personBirthday = currentPerson.getBirthday();
        String personAboutMe = currentPerson.getAboutMe();
        String personLocation = currentPerson.getCurrentLocation();
        int personGender = currentPerson.getGender();
        int personRelationshipStatus = currentPerson.getRelationshipStatus();

        String organizationsWorkedIn = "";
        String personGenderString = "";
        String personRelationshipStatusString = "";
        if (!personOrganizations.isEmpty()) {
            for (int i = 0; i < personOrganizations.size(); ++i) {
                String temp = personOrganizations.get(i).getName();
                organizationsWorkedIn += temp;
                if ((i != personOrganizations.size() - 1)) {
                    organizationsWorkedIn += ", ";
                }
            }
        }
        if (personGender == 0) {
            personGenderString = "Gender: Male";
        } else if (personGender == 1) {
            personGenderString = "Gender: Female";
        } else if (personGender == 2) {
            personGenderString = "Gender: Other";
        }
        if (personRelationshipStatus == 0) {
            personRelationshipStatusString = "Relationship Status: Single";
        } else if (personRelationshipStatus == 1) {
            personRelationshipStatusString = "Relationship Status: In a Relationship";
        } else if (personRelationshipStatus == 2) {
            personRelationshipStatusString = "Relationship Status: Engaged";
        } else if (personRelationshipStatus == 3) {
            personRelationshipStatusString = "Relationship Status: Married";
        } else if (personRelationshipStatus == 4) {
            personRelationshipStatusString = "Relationship Status: It's complicated";
        } else if (personRelationshipStatus == 5) {
            personRelationshipStatusString = "Relationship Status: In an open relationship";
        } else if (personRelationshipStatus == 6) {
            personRelationshipStatusString = "Relationship Status: Widowed";
        } else if (personRelationshipStatus == 7) {
            personRelationshipStatusString = "Relationship Status: In a domestic partnership";
        } else if (personRelationshipStatus == 8) {
            personRelationshipStatusString = "Relationship Status: In a civil union";
        }

        ImageView profilePicture = (ImageView) findViewById(R.id.profilePicture);
        TextView nameText = (TextView) findViewById(R.id.name);
        TextView birthdayText = (TextView) findViewById(R.id.birthday);
        TextView locationText = (TextView) findViewById(R.id.location);
        TextView organizationsText = (TextView) findViewById(R.id.organizations);
        TextView aboutMeText = (TextView) findViewById(R.id.aboutme);
        TextView genderText = (TextView) findViewById(R.id.gender);
        TextView relationshipStatusText = (TextView) findViewById(R.id.relationshipStatus);

        new DownloadImageTask(profilePicture).execute(personPicture.getUrl() + "0");
        nameText.setText(personName);
        nameText.setTypeface(null, Typeface.BOLD);
        nameText.setPaintFlags(nameText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        birthdayText.setText("Born on " + personBirthday);
        genderText.setText(personGenderString);
        relationshipStatusText.setText(personRelationshipStatusString);
        locationText.setText("Lives in " + personLocation);
        organizationsText.setText("Has worked at " + organizationsWorkedIn);
        aboutMeText.setText(personAboutMe);
    }

    private void getOrgInformation() {
        Plus.PeopleApi.loadVisible(mGoogleApiClient, null).setResultCallback(this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!mIntentInProgress && result.hasResolution()) {
            try {
                mIntentInProgress = true;
                startIntentSenderForResult(result.getResolution().getIntentSender(),
                        RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onResult(People.LoadPeopleResult peopleData) {
        Log.d("ERROR TAG", "result.getStatus():" + peopleData.getStatus());
        if (peopleData.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {
            PersonBuffer personBuffer = peopleData.getPersonBuffer();
            try {
                int count = personBuffer.getCount();
                for (int i = 0; i < count; i++) {
                    Log.d("ORG1", "Display name: " + personBuffer.get(i).getDisplayName());
                    userIds.add(personBuffer.get(i).getDisplayName());
                    hm.put(personBuffer.get(i).getDisplayName(), personBuffer.get(i).getId());
                }
                Collections.sort(userIds);
                ArrayAdapter adapter = new ArrayAdapter<String>(this,
                        R.layout.activity_listview, userIds);
                ListView listView = (ListView) findViewById(R.id.listView);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String value = (String) parent.getItemAtPosition(position);
                        Set set = hm.entrySet();
                        Iterator i = set.iterator();
                        while (i.hasNext()) {
                            Map.Entry me = (Map.Entry) i.next();
                            if (value.equals(me.getKey())) {
                                //Toast.makeText(getApplicationContext(), value + " " + me.getValue(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("message/rfc822");
                                try {
                                    startActivity(Intent.createChooser(intent, "Send mail"));
                                } catch (android.content.ActivityNotFoundException ex) {
                                    Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                });
            } finally {
                //notify();
                personBuffer.close();
            }
        } else {
            Log.e("ORG1", "Error requesting visible circles: " + peopleData.getStatus());
        }

    }
}


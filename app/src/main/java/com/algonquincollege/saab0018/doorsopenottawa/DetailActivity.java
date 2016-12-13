package com.algonquincollege.saab0018.doorsopenottawa;


import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.algonquincollege.saab0018.doorsopenottawa.utils.HttpManager;
import com.algonquincollege.saab0018.doorsopenottawa.utils.HttpMethod;
import com.algonquincollege.saab0018.doorsopenottawa.utils.RequestPackage;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.Locale;



/**
 * Created by mattsaab and alve0024 on 2016-11-17.
 */

public class DetailActivity extends FragmentActivity implements OnMapReadyCallback  {

    public static final String IMAGE_BASE_URL = "https://doors-open-ottawa-hurdleg.mybluemix.net/";
    public static final String REST_URI = "https://doors-open-ottawa-hurdleg.mybluemix.net/buildings";

    private TextView detailBuildingName;
    private TextView detailBuildingDescription;
    private TextView detailOpenHours;
    private TextView detailAddress;

    private GoogleMap mMap;
    private Geocoder mGeocoder;
    private Integer bID;

    float cameraZoom = 15.0f;

    // Locate and pin locationName to the map.
    private void pin( String locationName ) {
        try {
            Address address = mGeocoder.getFromLocationName(locationName, 1).get(0);
            LatLng ll = new LatLng( address.getLatitude(), address.getLongitude() );
            mMap.addMarker( new MarkerOptions().position(ll).title(locationName) );
            mMap.moveCamera( CameraUpdateFactory.newLatLng(ll) );
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, cameraZoom));
            Toast.makeText(this, "Pinned: " + locationName, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Not found: " + locationName, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_main);
        mGeocoder = new Geocoder( this, Locale.CANADA_FRENCH );
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Create a reference to the view
        detailBuildingName = (TextView) findViewById( R.id.buildingName );
        detailBuildingDescription = (TextView) findViewById( R.id.buildingDescription );
        detailOpenHours = (TextView) findViewById(R.id.openHours);
        detailAddress = (TextView) findViewById(R.id.address);
        // Get the bundle of extras that was sent to this activity
        intentBuildingInfo();

    }

    private void intentBuildingInfo() {
        Bundle bundle = getIntent().getExtras();
        if ( bundle != null ) {
            String nameFromDetailActivity = bundle.getString( "detailBuildingName");
            String descriptionFromDetailActivity = bundle.getString( "detailBuildingDescription" );
            String openHoursFromDetailActivity = bundle.getString("detailBuildingOpenHours");
            String addressFromDetailActivity = bundle.getString("detailBuildingAddress");

            // Update the view
            detailBuildingName.setText(nameFromDetailActivity );
            detailBuildingDescription.setText(descriptionFromDetailActivity);
            detailOpenHours.setText(openHoursFromDetailActivity);
            detailAddress.setText("Address:\t\t" +  addressFromDetailActivity);
        }

    }

    private void deletePlanet(String uri) {
        // Get the bundle of extras that was sent to this activity
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            bID = bundle.getInt("buildingID");

            RequestPackage pkg = new RequestPackage();
            pkg.setMethod(HttpMethod.DELETE);
            // DELETE the building with Id
            pkg.setUri(uri + "/" + bID);
            DoTask deleteTask = new DoTask();
            deleteTask.execute(pkg);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.details_menu, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.delete) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            deletePlanet( REST_URI);
            return true;
        }




        return super.onOptionsItemSelected(item);
    }
        @Override
        public void onMapReady (GoogleMap googleMap){
            mMap = googleMap;

            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                String addressFromDetailActivity = bundle.getString("detailBuildingAddress");
                DetailActivity.this.pin(addressFromDetailActivity);
            }
        }


    private class DoTask extends AsyncTask<RequestPackage, String, String> {


        @Override
        protected String doInBackground(RequestPackage ... params) {

            String content = HttpManager.getNewData(params[0]);
            return content;
        }

        @Override
        protected void onPostExecute(String result) {


            if (result == null) {
                Toast.makeText(DetailActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            }
        }
    }


}


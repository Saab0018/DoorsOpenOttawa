package com.algonquincollege.saab0018.doorsopenottawa;


import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;
import android.widget.Toast;
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

    private TextView detailBuildingName;
    private TextView detailBuildingDescription;
    private TextView detailOpenHours;
    private TextView detailAddress;

    private GoogleMap mMap;
    private Geocoder mGeocoder;

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


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Bundle bundle = getIntent().getExtras();
        if ( bundle != null ) {
            String addressFromDetailActivity = bundle.getString("detailBuildingAddress");
            DetailActivity.this.pin(addressFromDetailActivity);
        }
    }
}


package com.example.tejas.track;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String currentLocation, destLocation;
    double doubleCurrentLatitude, doubleCurrentLongitude, doubleDestLatitude, doubleDestLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        String busID = getIntent().getExtras().getString("BusID");
        currentLocation = getIntent().getExtras().getString("current");
        Toast.makeText(this, "Current: "+currentLocation, Toast.LENGTH_LONG).show();

        String[] array1 = currentLocation.split(",");
        String currentLatitude = array1[0].trim();
        String currentLongitude = array1[1].trim();

        doubleCurrentLatitude = Double.parseDouble(currentLatitude);
        doubleCurrentLongitude = Double.parseDouble(currentLongitude);

        destLocation = getIntent().getExtras().getString("destination");
        Toast.makeText(this, "Destination: "+destLocation, Toast.LENGTH_LONG).show();

        String[] array = destLocation.split(",");
        String destinationLatitude = array[0].trim();
        String destinationLongitude = array[1].trim();

        doubleDestLatitude = Double.parseDouble(destinationLatitude);
        doubleDestLongitude = Double.parseDouble(destinationLongitude);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng currentLatLang = new LatLng(doubleCurrentLatitude, doubleCurrentLongitude);
        LatLng destLatLang = new LatLng(doubleDestLatitude, doubleDestLongitude);
        mMap.addMarker(new MarkerOptions().position(currentLatLang).title("Your bus"));
        mMap.addMarker(new MarkerOptions().position(destLatLang).title("Destination"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLang, 14.0f));
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
    }
}

package com.example.tejas.track;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

public class ClientActivity extends AppCompatActivity implements LocationListener{

    String currentValue = null, destinationValue = null;
    Button getLocationBtn;
    FirebaseDatabase database;
    DatabaseReference myRef;
    EditText busID_edit_text;
    TextView distance, textViewDestination;
    String busID = "";
    LocationManager mLocationManager;
    double doubleCurrentLatitude, doubleCurrentLongitude, doubleDestLatitude, doubleDestLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        database = FirebaseDatabase.getInstance();
        busID_edit_text = (EditText) findViewById(R.id.bus_id_edit_text);
        distance = (TextView) findViewById(R.id.textView_distance);
        textViewDestination = (TextView)findViewById(R.id.textView_destination);
        getLocationBtn = (Button) findViewById(R.id.get_location_btn);

        mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        try {
            getLocationBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    busID = busID_edit_text.getText().toString().trim();

                    myRef = database.getReference().child(busID);

                    myRef.child("current").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            currentValue = dataSnapshot.getValue(String.class);
                            TextView locationViewer = (TextView) findViewById(R.id.location_viewer);
                            locationViewer.setText("Bus coordinates:"+currentValue);

                            String[] array = currentValue.split(",");
                            String latitude = array[0].trim();
                            String longitude = array[1].trim();

                            doubleCurrentLatitude = Double.parseDouble(latitude);
                            doubleCurrentLongitude = Double.parseDouble(longitude);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    myRef.child("destination").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            destinationValue = dataSnapshot.getValue(String.class);
                            String[] array = destinationValue.split(",");
                            String latitude = array[0].trim();
                            String longitude = array[1].trim();

                            doubleDestLatitude = Double.parseDouble(latitude);
                            doubleDestLongitude = Double.parseDouble(longitude);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    myRef.child("Dest").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String dest = dataSnapshot.getValue(String.class);
                            textViewDestination.setText("Destination: "+dest);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    Location myLocation = getLocation();
                    double doubleMyLatitude = myLocation.getLatitude();
                    double doubleMyLongitude = myLocation.getLongitude();

                    float[] results = new float[1];
                    Location.distanceBetween(doubleCurrentLatitude, doubleCurrentLongitude,
                            doubleMyLatitude, doubleMyLongitude, results);
                    distance.setText(new DecimalFormat("#.00").format(results[0]) + " meters");
                    //Toast.makeText(getApplicationContext(), "Distance: " + results[0] +"meters", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
        }

    }

    private Location getLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        Location locationGPS;
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60*1000, 10, this);
        locationGPS = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        Location locationNet;
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60*1000, 10, this);
        locationNet = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long GPSLocationTime = 0;
        if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if ( 0 < GPSLocationTime - NetLocationTime ) {
            return locationGPS;
        }
        else {
            return locationNet;
        }
    }

    public void openMaps(View view) {
        if (currentValue != null && destinationValue != null) {
            Intent mapsIntent = new Intent(ClientActivity.this, MapsActivity.class);
            Bundle extras = new Bundle();
            extras.putString("BusID", busID);
            extras.putString("current", currentValue);
            extras.putString("destination", destinationValue);
            mapsIntent.putExtras(extras);
            startActivity(mapsIntent);
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}

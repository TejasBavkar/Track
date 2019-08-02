package com.example.tejas.track;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.test.mock.MockPackageManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef;
    Button btnShowLocation,buttonSelectDestination;
    TextView location;
    EditText busID, source, destination;
    ToggleButton updates;

    Toolbar toolbar;

    int PLACE_PICKER_REQUEST = 1;

    private  static final int REQUEST_CODE_PERMISSION = 2;
    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;

    GPSTracker gps;

    String s_source, s_dest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        source = (EditText)findViewById(R.id.editText_source);
        destination = (EditText)findViewById(R.id.editText_destination);

        //s_source=source.getText().toString();
        //s_dest=destination.getText().toString();

        try{
            if(ActivityCompat.checkSelfPermission(this, mPermission) != MockPackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{mPermission}, REQUEST_CODE_PERMISSION);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        updates = (ToggleButton)findViewById(R.id.toggleButton_updates);
        updates.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                final Handler handler = new Handler();
                if (b) {
                    // The toggle is enabled
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            gps = new GPSTracker(getApplicationContext());
                            location = (TextView)findViewById(R.id.location);
                            busID = (EditText) findViewById(R.id.bus_id_edit_text);
                            if(gps.canGetLocation){
                                double latitude = gps.getLatitude();
                                double longitude = gps.getLongitude();
                                location.setText(latitude+", "+longitude);
                                database = FirebaseDatabase.getInstance();
                                myRef = database.getReference().child(busID.getText().toString().trim());
                                myRef.child("current").setValue(latitude+","+longitude);
                            }
                            else{
                                gps.showSettingsAlert();
                            }
                            Toast.makeText(getApplicationContext(), "check", Toast.LENGTH_SHORT).show();
                            handler.postDelayed(this, 180000);
                        }
                    }, 180000);

                } else {
                    // The toggle is disabled
                    handler.removeCallbacksAndMessages(null);
                }
            }
        });


        btnShowLocation = (Button)findViewById(R.id.getLocation);
        btnShowLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gps = new GPSTracker(getApplicationContext());
                location = (TextView)findViewById(R.id.location);
                busID = (EditText) findViewById(R.id.bus_id_edit_text);
                if(gps.canGetLocation){
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                    location.setText(latitude+", "+longitude);
                    database = FirebaseDatabase.getInstance();
                    myRef = database.getReference().child(busID.getText().toString().trim());
                    myRef.child("current").setValue(latitude+","+longitude);
                    myRef.child("Source").setValue(source.getText().toString());
                    myRef.child("Dest").setValue(destination.getText().toString());
                }
                else{
                    gps.showSettingsAlert();
                }
            }
        });

        buttonSelectDestination = (Button)findViewById(R.id.selectDestination);
        buttonSelectDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(UserActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place =   PlacePicker.getPlace(data,this);
                Double latitude = place.getLatLng().latitude;
                Double longitude = place.getLatLng().longitude;
                String address = String.valueOf(latitude)+","+String.valueOf(longitude);
                database = FirebaseDatabase.getInstance();
                myRef = database.getReference().child(busID.getText().toString().trim());
                myRef.child("destination").setValue(address);
                Toast.makeText(this, address, Toast.LENGTH_LONG).show();
            }
        }
    }

    /*public void submit(View view){
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child(busID.getText().toString().trim());
        myRef.child("Source").setValue(source.getText());
        myRef.child("Dest").setValue(destination.getText());
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FirebaseAuth.getInstance().signOut();
        finish();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        return true;
    }
}

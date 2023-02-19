package com.example.cse110project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.util.Pair;

import android.content.SharedPreferences;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CompassActivity extends AppCompatActivity {
    private LocationService locationService;
    //private OrientationService orientationService;
    private final double OUR_LAT = 32.88129;
    private final double OUR_LONG = -117.23758;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        ImageView familyIcon = findViewById(R.id.blue_icon);
        ImageView friendIcon = findViewById(R.id.purple_icon);
        TextView homeLabel = findViewById(R.id.homeLabelDisplay);
        TextView familyLabel = findViewById(R.id.familyLabelDisplay);
        TextView friendLabel = findViewById(R.id.friendLabelDisplay);

        ImageView homeIcon = findViewById(R.id.red_icon);
        homeIcon.setVisibility(View.INVISIBLE);
        familyIcon.setVisibility(View.INVISIBLE);
        friendIcon.setVisibility(View.INVISIBLE);
        homeLabel.setVisibility(View.INVISIBLE);
        familyLabel.setVisibility(View.INVISIBLE);
        friendLabel.setVisibility(View.INVISIBLE);


        SharedPreferences preferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
        String homeLatLong = preferences.getString("mine", "");
        String friendLatLong = preferences.getString("family", "");
        String familyLatLong = preferences.getString("friend", "");

        System.out.println(homeLatLong);

        if(!homeLatLong.equals("")) {
            homeIcon.setVisibility(View.VISIBLE);
            homeLabel.setVisibility(View.VISIBLE);
        }
        else {
            homeIcon.setVisibility(View.INVISIBLE);
            homeLabel.setVisibility(View.INVISIBLE);
        }

        if(!friendLatLong.equals("")){
            friendIcon.setVisibility(View.VISIBLE);
            friendLabel.setVisibility(View.VISIBLE);
        }

        else {
            friendIcon.setVisibility(View.INVISIBLE);
            friendLabel.setVisibility(View.INVISIBLE);
        }

        if(!familyLatLong.equals("")){
            familyIcon.setVisibility(View.VISIBLE);
            familyLabel.setVisibility(View.VISIBLE);
        }

        else {
            familyIcon.setVisibility(View.INVISIBLE);
            familyLabel.setVisibility(View.INVISIBLE);
        }

        locationService = LocationService.singleton(this);
        this.reobserveLocation();

        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.compassLayout);
        TextView orientationView = (TextView) findViewById(R.id.orientation);
        RotateCompass.rotateCompass(this, this, layout, orientationView);
    }

    public void backToCoordinates(View view) {
        finish();
    }

    /**
     * Taken from Lab Demo 5
     * Basically rechecks for current location
     */
    public void reobserveLocation() {
        var locationData = locationService.getLocation();
        locationData.observe(this, this::onLocationChanged);
    }

    private void onLocationChanged(android.util.Pair<Double, Double> latLong) {
        SharedPreferences preferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
        String homeLatLong = preferences.getString("mine", "");
        String friendLatLong = preferences.getString("family", "");
        String familyLatLong = preferences.getString("friend", "");

        ImageView homeIcon = findViewById(R.id.red_icon);

        TextView locationText = findViewById(R.id.locationText);
        locationText.setText(Utilities.formatLocation(latLong.first, latLong.second));

        if (!homeLatLong.equals("")){
            String[] coordinates = Utilities.parseCoords(homeLatLong);
            double latitude = Utilities.stringToDouble(coordinates[0]);
            double longitude = Utilities.stringToDouble(coordinates[1]);
            double ourHomeAngle = Utilities.findAngle(latLong.first, latLong.second, latitude, longitude);

            ConstraintLayout.LayoutParams redLayoutParams = (ConstraintLayout.LayoutParams) homeIcon.getLayoutParams();
            redLayoutParams.circleAngle = (float) ourHomeAngle;
            homeIcon.setLayoutParams(redLayoutParams);
        }
    }
}
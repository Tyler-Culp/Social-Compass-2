package com.example.cse110project.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LiveData;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cse110project.R;
import com.example.cse110project.Utilities;
import com.example.cse110project.model.User;
import com.example.cse110project.model.UserAPI;
import com.example.cse110project.model.UserDao;
import com.example.cse110project.model.UserDatabase;
import com.example.cse110project.model.UserRepository;
import com.example.cse110project.service.ConstrainUserService;
import com.example.cse110project.service.LocationService;
import com.example.cse110project.service.RotateCompass;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

public class CompassActivity extends AppCompatActivity {
    TextView latLong;
    TextView public_uid;
    UserAPI api = new UserAPI();

    //TODO Implement using SharedPreferences Later
    int zoomLevel = 1;
    private LocationService locationService;
    private List<ImageView> circleViews = new ArrayList<>();
    private final List<ImageView> finalCircleSizes = new ArrayList<>();

    SharedPreferences prefs;
    //Hashtable<String, TextView> tableTextView;
    Hashtable<String, ConstrainUserService> tableTextView;

    Context context;
    UserDatabase db;
    UserDao dao;
    UserRepository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
        prefs = getSharedPreferences(Utilities.PREFERENCES_NAME, MODE_PRIVATE);
        TextView myText = new TextView(this);
        myText.setText("TextView number: 1");

        context = getApplicationContext();
        db = UserDatabase.provide(context);
        dao = db.getDao();
        repo = new UserRepository(dao);

//        for (int i = 0; i < 3; i++) {
//            TextView myText = new TextView(this);
//            myText.setText("TextView number: " + i);
//            layout.addView(myText);
//        }
        latLong = findViewById(R.id.userUIDTextView);
        latLong.setText("Some new text in the box");
        public_uid = findViewById(R.id.public_uid);
        if(Utilities.personalUser != null){
            public_uid.setText(Utilities.personalUser.public_code);
        }

        locationService = LocationService.singleton(this);

        if (Utilities.personalUser.private_code == null) {
            throw new IllegalStateException("personal UID can't be empty by the time we get to the Compass");
        }

        this.reobserveLocation();
        this.reobserveGPS();
        circleViews.add(findViewById(R.id.circleOne));
        circleViews.add(findViewById(R.id.circleTwo));
        circleViews.add(findViewById(R.id.circleThree));
        circleViews.add(findViewById(R.id.circleFour));

        Utilities.updateZoom(zoomLevel, circleViews);

//        var executor = Executors.newSingleThreadExecutor();
//        var future = executor.submit(this::reobserveLocation);
//        try {
//            var getFuture = future.get(1, TimeUnit.SECONDS);
//        } catch (ExecutionException e) {
//            throw new RuntimeException(e);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        } catch (TimeoutException e) {
//            throw new RuntimeException(e);
//        }

//        personalUIDTextView = findViewById(R.id.userUIDTextView);
//        SharedPreferences preferences = getSharedPreferences(Utilities.PREFERENCES_NAME, MODE_PRIVATE);
//        String personalPublicUID = preferences.getString(Utilities.USER_PUBLIC_UID, "");

//        personalUIDTextView.setText(personalPublicUID);

        // These were used to check that UIDs that I entered on the EnterFriendsActivity were actually
        // put into the Room database correctly and that I would be able to get them back out.
        // They worked correctly for me, if they break when you try to uncomment them make sure you add
        // 'My current location' on EnterFriendsActivity first

//        assert(dao.exists("My current location") == true);
//        dao.get("My current location").observe(this, user -> {
//            personalUIDTextView.setText(user.toJSON());
//        });
        tableTextView = new Hashtable<>();
        LiveData<List<User>> list = repo.getAllLocal();
        ConstraintLayout constraint = findViewById(R.id.compassLayout);
        list.observe(this, listUsers ->{
            for(User user : listUsers){
                if(!tableTextView.containsKey(user.public_code)){
                    TextView textView = new TextView(this);
                    textView.setText(user.label);
                    constraint.addView(textView);
                    ConstrainUserService constrainUserService = new ConstrainUserService(user.latitude, user.longitude, textView);
                    tableTextView.put(user.public_code, constrainUserService);
                    constrainUserService.constrainUser(Utilities.personalUser.latitude, Utilities.personalUser.longitude, user.latitude, user.longitude, zoomLevel);
                } else {
                    TextView textView = tableTextView.get(user.public_code).textView;
                    textView.setText(user.label);
                    tableTextView.get(user.public_code).constrainUser(Utilities.personalUser.latitude, Utilities.personalUser.longitude, user.latitude, user.longitude, zoomLevel);
                }
            }
        });

        list.observe(this, listUsers ->{
            for(User user : listUsers){
                LiveData<User> updatedUser = repo.getRemote(user.public_code);
                updatedUser.observe(this, updatedUsers -> {
                    if (Instant.parse(user.updated_at).compareTo(Instant.parse(updatedUsers.updated_at)) < 0) {
                        dao.upsert(updatedUsers);
                    }
                });
            }
        });

        //rotate compass
        TextView orientationView = (TextView) findViewById(R.id.orientation);
        RotateCompass.rotateCompass(this, this, constraint, orientationView);
    }

    public void enterFriendsBtnPressed(View view) {
        finish();
    }

    public Hashtable<String, ConstrainUserService> getTextViews(){
        return tableTextView;
    }

    public void reobserveGPS() {
        var gpsStatus = locationService.getStatus();
        Log.d("ReobserveGPS", "reobserveGPS: " + gpsStatus);
        gpsStatus.observe(this, this::onStatusChanged);
    }

    public void onStatusChanged(Boolean gpsStatus){
        if(gpsStatus){
            //TODO: Make a status light and a timer
        }
        else {
            //TODO: Enable status light and timer
        }
    }

    public void reobserveLocation() {
        var locationData = locationService.getLocation();
        locationData.observe(this, this::onLocationChanged);
    }

    public void onLocationChanged(Pair<Double, Double> latLong) {
        double newLat = latLong.first;
        double newLong = latLong.second;
        String updatedTime = Instant.now().toString();

        Utilities.personalUser.latitude = (float) newLat;
        Utilities.personalUser.longitude = (float) newLong;
        Utilities.personalUser.updated_at = updatedTime;

        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(Utilities.USER_LATITUDE, (float) newLat);
        editor.putFloat(Utilities.USER_LONGITUDE, (float) newLong);
        editor.putString(Utilities.UPDATED_AT, updatedTime);

        editor.apply();


        api.putUserAsync(Utilities.personalUser);
        this.latLong.setText("Latitude: " + newLat + ", longitude: "+ newLong);

//        var context = getApplicationContext();
//        var db = UserDatabase.provide(context);
//        var dao = db.getDao();
//
//        dao.upsert(Utilities.personalUser);


        // Loop through all of the friend UIDs we have and recompute the formula for getting their angles on the compass
        Enumeration<String> e = tableTextView.keys();

        while (e.hasMoreElements()) {
            String key = e.nextElement();
            TextView textView = tableTextView.get(key).textView;
        }

        LiveData<List<User>> list = repo.getAllLocal();
        list.observe(this, listUsers ->{
            for(User user : listUsers){
                TextView textView = tableTextView.get(user.public_code).textView;
                textView.setText(user.label);
                tableTextView.get(user.public_code).constrainUser(Utilities.personalUser.latitude, Utilities.personalUser.longitude, user.latitude, user.longitude, zoomLevel);
            }
        });
    }

    public void zoomIn(View view){
        Button zoomInBtn = findViewById(R.id.zoomInBtn);
        Button zoomOutBtn = findViewById(R.id.zoomOutBtn);
        System.out.println("Zoom Level : " + zoomLevel);

        if(zoomLevel == 3){
            zoomOutBtn.setAlpha(1f);
            zoomOutBtn.setEnabled(true);
        }
        zoomLevel--;
        if(zoomLevel < 0){
            System.out.println("Zoom Level : " + zoomLevel);
            zoomInBtn.setAlpha(0.5f);
            zoomInBtn.setEnabled(false);
            return;
        }

        Utilities.updateZoom(zoomLevel, circleViews);
        Enumeration<String> e = tableTextView.keys();

        while (e.hasMoreElements()) {
            String key = e.nextElement();
            ConstrainUserService textView = tableTextView.get(key);
            textView.constrainUser(Utilities.personalUser.latitude, Utilities.personalUser.longitude, zoomLevel);
        }
    }
    public void zoomOut(View view){
        Button zoomOutBtn = findViewById(R.id.zoomOutBtn);
        Button zoomInBtn = findViewById(R.id.zoomInBtn);

        if(zoomLevel == 0){
            zoomInBtn.setAlpha(1f);
            zoomInBtn.setEnabled(true);
        }
        if(zoomLevel + 1 >= circleViews.size()){
            zoomOutBtn.setAlpha(0.5f);
            zoomOutBtn.setEnabled(false);
            return;
        }
        zoomLevel++;
        Utilities.updateZoom(zoomLevel, circleViews);
        Enumeration<String> e = tableTextView.keys();

        while (e.hasMoreElements()) {
            String key = e.nextElement();
            ConstrainUserService textView = tableTextView.get(key);
            textView.constrainUser(Utilities.personalUser.latitude, Utilities.personalUser.longitude, zoomLevel);
        }
    }
}
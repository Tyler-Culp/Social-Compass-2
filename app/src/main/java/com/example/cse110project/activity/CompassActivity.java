package com.example.cse110project.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.cse110project.R;
import com.example.cse110project.Utilities;
import com.example.cse110project.model.UserDao;
import com.example.cse110project.model.UserDatabase;

public class CompassActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
        var context = getApplicationContext();
        var db = UserDatabase.provide(context);
        var dao = db.getDao();


        TextView personalUIDTextView = findViewById(R.id.userUIDTextView);
        SharedPreferences preferences = getSharedPreferences(Utilities.PREFERENCES_NAME, MODE_PRIVATE);
        String personalPublicUID = preferences.getString(Utilities.USER_PUBLIC_UID, "");

        if (personalPublicUID.equals("")) {
            throw new IllegalStateException("personal UID can't be empty by the time we get to the Compass");
        }
        personalUIDTextView.setText(personalPublicUID);

        // These were used to check that UIDs that I entered on the EnterFriendsActivity were actually
        // put into the Room database correctly and that I would be able to get them back out.
        // They worked correctly for me, if they break when you try to uncomment them make sure you add
        // 'My current location' on EnterFriendsActivity first

//        assert(dao.exists("My current location") == true);
//        dao.get("My current location").observe(this, user -> {
//            personalUIDTextView.setText(user.toJSON());
//        });
    }

    public void enterFriendsBtnPressed(View view) {
        finish();
    }
}
package com.example.cse110project;

import static org.junit.Assert.assertEquals;

import android.Manifest;
import android.app.Application;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.cse110project.activity.CompassActivity;
import com.example.cse110project.model.User;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

@RunWith(RobolectricTestRunner.class)
public class OrientationServiceTest {
    @Test
    public void test_orientation_service(){
        Application application = ApplicationProvider.getApplicationContext();
        ShadowApplication app = Shadows.shadowOf(application);
        app.grantPermissions(android.Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        Utilities.personalUser = new User("test","test","test",0,0);
        var scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.onActivity(activity -> {
            var mockDataSource = new MutableLiveData<Float>();
            var expected = 2.65386f;
            mockDataSource.setValue(expected);

            var orientationService = com.example.cse110project.OrientationService.singleton(activity);
            orientationService.setMockOrientationSource(mockDataSource);
            orientationService.getOrientation().observe(activity, orientation->{
                assertEquals(orientation,expected,0);
            });
        });
    }
}

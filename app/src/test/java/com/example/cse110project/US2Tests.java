package com.example.cse110project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.content.Context;
import android.util.Pair;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.cse110project.activity.CompassActivity;
import com.example.cse110project.activity.EnterFriendActivity;
import com.example.cse110project.model.User;
import com.example.cse110project.model.UserDao;
import com.example.cse110project.model.UserDatabase;
import com.example.cse110project.service.TimeService;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class US2Tests {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private UserDao dao;
    private UserDatabase db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, UserDatabase.class)
                .allowMainThreadQueries()
                .build();
        dao = db.getDao();
    }

    @After
    public void closeDb() throws IOException{
        db.close();
    }

    @Test
    public void testUpsertNewUser(){
        User user1 = new User("1", "abc", "friend1", 32, 114);
        User user2 = new User("2","abc", "friend2", 33, 114);

        long id1 = dao.upsert(user1);
        long id2 = dao.upsert(user2);

        assertNotEquals(id1,id2);
    }

    @Test
    public void testGet(){
        User user1 = new User("4","abc", "friend4", 33, 114);
        long id = dao.upsert(user1);

//        LiveData<User> user = dao.get("4");
//        user.observe(ApplicationProvider.getApplicationContext(), returnedUser->{
//            assertEquals("4",user1.public_code);
//            assertEquals(user1.label, returnedUser.label);
//            assertEquals(user1.latitude, returnedUser.latitude);
//            assertEquals(user1.longitude, returnedUser.longitude);
//        });

//        User user = dao.get("4");
//        assertEquals("4", user.public_code);
//        assertEquals(user1.label, user.label);
//        assertEquals(user1.latitude, user.latitude,0);
//        assertEquals(user1.longitude, user.longitude,0);
    }

    @Test
    public void testDelete(){
        User user1 = new User("5", "abc","friend5", 33, 114);
        long id = dao.upsert(user1);

//        user1 = dao.get("5");
//        int deletedItem = dao.delete(user1);
//        assertEquals(1, deletedItem);
//        assertNull(dao.get("5"));
    }

    @Test
    public void US5_test_GPS_Active() {
        Utilities.personalUser = new User("test", "test", "test", 0, 0);

        var scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.moveToState(Lifecycle.State.STARTED);

        scenario.onActivity(activity -> {
            var timeService = TimeService.singleton();
            var mockTime = new MutableLiveData<Long>();
            timeService.setMockTimeSource(mockTime);

            Pair<Double, Double> testLocation = new Pair<Double, Double>(1.0, 1.0);
            activity.onLocationChanged(testLocation);

            Long currentTime = System.currentTimeMillis();
            Long testTime = currentTime;
            mockTime.setValue(testTime);

            TextView gpsStatus = activity.findViewById(R.id.gpsStatus);
            assertEquals(4, gpsStatus.getVisibility());

            ImageView gpsActive = activity.findViewById(R.id.gpsActive);
            assertEquals(0, gpsActive.getVisibility());

            ImageView gpsInactive = activity.findViewById(R.id.gpsInactive);
            assertEquals(4, gpsInactive.getVisibility());
        });
    }
}

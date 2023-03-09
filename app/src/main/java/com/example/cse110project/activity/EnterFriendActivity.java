package com.example.cse110project.activity;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.example.cse110project.R;
import com.example.cse110project.Utilities;
import com.example.cse110project.model.EnterFriendViewModel;
import com.example.cse110project.model.User;
import com.example.cse110project.model.UserAPI;
import com.example.cse110project.model.UserAdapter;
import com.example.cse110project.model.UserDao;
import com.example.cse110project.model.UserDatabase;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class EnterFriendActivity extends AppCompatActivity {

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_friend);

        var viewModel = setUpViewModel();
        var adapter = setupAdapter(viewModel);

        setupViews(viewModel, adapter);
    }

    private void setupViews(EnterFriendViewModel viewModel, UserAdapter adapter) {
        setupRecycler(adapter);
        setupInput(viewModel);
    }

    private void setupInput(EnterFriendViewModel viewModel) {
        var input = (EditText) findViewById(R.id.enterUIDEditText);
        input.setOnEditorActionListener((view, actionId, event) -> {
            // If the event isn't "done" or "enter", do nothing.
            if (actionId != EditorInfo.IME_ACTION_DONE) {
                return false;
            }

            var public_uid = input.getText().toString();
            try {
                var user = viewModel.getOrCreateUser(public_uid);
                if (user == null) {
                    Utilities.showAlert(this, "This public UID doesn't exist!");
                    return false;
                }
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (TimeoutException e) {
                throw new RuntimeException(e);
            }

            return true;
        });
    }

    @SuppressLint("RestrictedApi")
    private void setupRecycler(UserAdapter adapter) {
        recyclerView = findViewById(R.id.recycler_main);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);
    }

    private UserAdapter setupAdapter(EnterFriendViewModel viewModel) {
        UserAdapter adapter = new UserAdapter();
        adapter.setHasStableIds(true);
        adapter.setOnUserDeleteClickedListener(user -> onUserDeleteClicked(user, viewModel));
        viewModel.getUsers().observe(this, adapter::setUsers);
        return adapter;
    }

    private void onUserDeleteClicked(User user, EnterFriendViewModel viewModel) {
        viewModel.delete(user);
    }

    private EnterFriendViewModel setUpViewModel() {
        return new ViewModelProvider(this).get(EnterFriendViewModel.class);
    }

//    public void onAddPressed(View view) throws ExecutionException, InterruptedException, TimeoutException {
//        EditText textBoxUid = findViewById(R.id.enterUIDEditText);
//        String uid = textBoxUid.getText().toString();
//        var user =
//
//
//        var context = getApplicationContext();
//        var db = UserDatabase.provide(context);
//        var dao = db.getDao();
//
//
//        dao.upsert(user);
//
//        textBoxUid.setText("");
//    }

    public void onNextPressed(View view) {
        Intent intent = new Intent(this, CompassActivity.class);
        startActivity(intent);
    }

    public void onBackPressed(View view) {
        Intent intent = new Intent(this, EnterNameActivity.class);
        startActivity(intent);
        finish();
    }
}
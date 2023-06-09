package com.example.cse110project.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.room.Upsert;

import java.util.List;
@Dao
public abstract class UserDao {
    @Upsert
    public abstract long upsert(User user);

    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE public_code = :public_code)")
    public abstract boolean exists(String public_code);

    @Query("SELECT * FROM users WHERE public_code = :public_code")
//    public abstract User get(String public_code);
    public abstract LiveData<User> get(String public_code);

    @Query("SELECT * FROM users ORDER BY public_code")
    public abstract LiveData<List<User>> getAll();

    @Delete
    public abstract int delete(User user);
}

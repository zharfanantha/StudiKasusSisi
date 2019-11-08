package com.example.studikasussisi.Repository.DB.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.studikasussisi.Repository.Model.User;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;

@Dao
public interface UserDao {
    @Query("SELECT * FROM User")
    Single<List<User>> getAllUser();

    @Query("SELECT * FROM User WHERE noKtp = :noKtp")
    Single<User> getUserById(String noKtp);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertUser(User... items);

    @Update
    Completable updateUser(User... items);

    @Delete
    Completable deleteUser(User item);
}

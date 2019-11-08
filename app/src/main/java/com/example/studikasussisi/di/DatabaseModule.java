package com.example.studikasussisi.di;

import android.app.Application;

import androidx.room.Room;

import com.example.studikasussisi.Repository.DB.AppDatabase;
import com.example.studikasussisi.Repository.DB.DAO.UserDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DatabaseModule {

    private static AppDatabase database;

    public DatabaseModule(Application application) {
        database = Room.databaseBuilder(application, AppDatabase.class, "bcafDB")
//                .addMigrations(AppDatabase.MIGRATION_1_2)
                .fallbackToDestructiveMigration()
                .build();
    }

    @Singleton
    @Provides
    static AppDatabase provideDatabase(){
        return database;
    }

    @Singleton
    @Provides
    static UserDao provideUserDao(AppDatabase database) {
        return database.userDao();
    }

}

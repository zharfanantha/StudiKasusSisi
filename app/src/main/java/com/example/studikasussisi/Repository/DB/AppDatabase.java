package com.example.studikasussisi.Repository.DB;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.studikasussisi.Repository.DB.DAO.UserDao;
import com.example.studikasussisi.Repository.Model.User;

@Database(entities = {User.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract UserDao userDao();

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE `TodoItem` "
                    + "ADD COLUMN todoStatus VARCHAR(20)");
            database.execSQL("UPDATE `TodoItem` "
                    + "SET todoStatus = `Pending`");
        }
    };
}

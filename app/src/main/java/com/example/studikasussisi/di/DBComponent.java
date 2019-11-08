package com.example.studikasussisi.di;

import com.example.studikasussisi.Repository.DB.AppDatabase;
import com.example.studikasussisi.Repository.DB.DAO.UserDao;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {DatabaseModule.class})
public interface DBComponent {

    AppDatabase appDatabase();
    UserDao userDao();

}

package com.example.studikasussisi.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.studikasussisi.R;
import com.example.studikasussisi.Repository.Model.HomeMenu;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends AndroidViewModel {


    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    private String[] titleList = {
            "Buat Data", "Lihat Data"
    };

    private int[] imgList = {
            R.drawable.user,
            R.drawable.user
    };

    private MutableLiveData<List<HomeMenu>> mHomeMenu = new MutableLiveData<>();
    private List<HomeMenu> listHm = new ArrayList<>();

    public LiveData<List<HomeMenu>> getmHomeMenu() {
        listHm = initialize();
        mHomeMenu.postValue(listHm);
        return mHomeMenu;
    }

    private List<HomeMenu> initialize() {
        List<HomeMenu> allList = new ArrayList<>();
        for (int i=0 ; i<imgList.length ; i++) {
            allList.add(new HomeMenu(titleList[i], imgList[i]));
        }
        return allList;
    }

}

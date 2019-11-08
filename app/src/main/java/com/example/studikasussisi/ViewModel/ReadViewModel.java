package com.example.studikasussisi.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.studikasussisi.Repository.DB.AppDatabase;
import com.example.studikasussisi.Repository.Model.User;
import com.example.studikasussisi.di.DBComponent;
import com.example.studikasussisi.di.DaggerDBComponent;
import com.example.studikasussisi.di.DatabaseModule;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ReadViewModel extends AndroidViewModel {
    @Inject
    AppDatabase db;

    public ReadViewModel(@NonNull Application application) {
        super(application);
        DBComponent component = DaggerDBComponent.builder()
                .databaseModule(new DatabaseModule(application)).build();
        db = component.appDatabase();
    }

    CompositeDisposable disposable = new CompositeDisposable();

    MutableLiveData<Boolean> isError = new MutableLiveData<>();
    MutableLiveData<String> errorMessage = new MutableLiveData<>();
    MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    MutableLiveData<List<User>> listUser = new MutableLiveData<>();

    public MutableLiveData<Boolean> getIsError() {
        return isError;
    }
    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }
    public MutableLiveData<List<User>> getListUser() {
        return listUser;
    }

    public void getAllUser() {
        isLoading.setValue(true);
        disposable.add(
                db.userDao()
                        .getAllUser()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<User>>() {
                            @Override
                            public void onSuccess(List<User> users) {
                                isLoading.setValue(false);
                                if (users.size() != 0) {
                                    Log.e("cek", "user "+users.get(0).getNama());
                                    listUser.setValue(users);
                                } else {
                                    listUser.setValue(null);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        })
        );
    }

}

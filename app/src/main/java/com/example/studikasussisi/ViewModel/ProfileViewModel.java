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
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ProfileViewModel extends AndroidViewModel {
    @Inject
    AppDatabase db;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        DBComponent component = DaggerDBComponent.builder()
                .databaseModule(new DatabaseModule(application)).build();
        db = component.appDatabase();
    }

    CompositeDisposable disposable = new CompositeDisposable();

    MutableLiveData<Boolean> isError = new MutableLiveData<>();
    MutableLiveData<String> errorMessage = new MutableLiveData<>();
    MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    MutableLiveData<Boolean> isUpdated = new MutableLiveData<>();
    MutableLiveData<Boolean> isDeleted = new MutableLiveData<>();
    MutableLiveData<User> userProfile = new MutableLiveData<>();

    public MutableLiveData<Boolean> getIsError() {
        return isError;
    }
    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }
    public MutableLiveData<User> getUserProfile() {
        return userProfile;
    }

    public MutableLiveData<Boolean> getIsUpdated() {
        return isUpdated;
    }

    public MutableLiveData<Boolean> getIsDeleted() {
        return isDeleted;
    }

    public void getUser(String noKtp) {
        isLoading.setValue(true);
        disposable.add(
                db.userDao()
                .getUserById(noKtp)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<User>() {
                            @Override
                            public void onSuccess(User users) {
                                userProfile.setValue(users);
                                isLoading.setValue(false);
                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        })
        );
    }

    public void updateProfile(User user) {
        isLoading.setValue(true);
        disposable.add(
                db.userDao()
                        .updateUser(user)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableCompletableObserver() {
                            @Override
                            public void onComplete() {
                                isLoading.setValue(false);
                                isUpdated.setValue(true);
                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        })
        );
    }

    public void deleteProfile(User user) {
        isLoading.setValue(true);
        disposable.add(
                db.userDao()
                        .deleteUser(user)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableCompletableObserver() {
                            @Override
                            public void onComplete() {
                                isLoading.setValue(false);
                                isDeleted.setValue(true);
                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        })
        );
    }

}

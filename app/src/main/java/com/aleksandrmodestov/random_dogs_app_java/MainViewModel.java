package com.aleksandrmodestov.random_dogs_app_java;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.net.UnknownHostException;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainViewModel extends AndroidViewModel {

    private static final String TAG = "MainViewModel";

    private MutableLiveData<Dog> dogLiveData = new MutableLiveData();
    private MutableLiveData<Boolean> shouldDisplayProgressBar = new MutableLiveData<>();
    private MutableLiveData<Boolean> shouldDisplayErrorToast = new MutableLiveData<>();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ApiService apiService = ApiFactory.getApiService();

    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Dog> getDog() {
        return dogLiveData;
    }

    public LiveData<Boolean> getShouldDisplayProgressBar() {
        return shouldDisplayProgressBar;
    }

    public LiveData<Boolean> getShouldDisplayErrorToast() {
        return shouldDisplayErrorToast;
    }

    protected void loadDogImage() {
        Disposable disposable = loadDoImageRx()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Throwable {
                        shouldDisplayErrorToast.setValue(false);
                        shouldDisplayProgressBar.setValue(true);
                    }
                })
                .doAfterTerminate(new Action() {
                    @Override
                    public void run() throws Throwable {
                        shouldDisplayProgressBar.setValue(false);
                    }
                })
                .subscribe(new Consumer<Dog>() {
                    @Override
                    public void accept(Dog dog) throws Throwable {
                        dogLiveData.setValue(dog);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        if (throwable instanceof UnknownHostException) {
                            shouldDisplayErrorToast.setValue(true);
                        }
                        Log.d(TAG, throwable.toString());
                    }
                });
        compositeDisposable.add(disposable);
    }

    private Single<Dog> loadDoImageRx() {
        return apiService.loadDog();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}


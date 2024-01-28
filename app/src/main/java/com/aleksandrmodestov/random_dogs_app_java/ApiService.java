package com.aleksandrmodestov.random_dogs_app_java;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;

public interface ApiService {

    @GET("breeds/image/random")
    Single<Dog> loadDog();
}

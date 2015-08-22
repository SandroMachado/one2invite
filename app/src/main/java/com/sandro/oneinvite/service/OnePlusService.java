package com.sandro.oneinvite.service;

import com.sandro.oneinvite.model.Result;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface OnePlusService {

    @GET("/index.php?r=share/view")
    void getUserRank(@Query("kid") String kid, Callback<Result> callback);

}

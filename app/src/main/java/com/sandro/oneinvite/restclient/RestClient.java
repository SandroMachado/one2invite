package com.sandro.oneinvite.restclient;

import com.sandro.oneinvite.service.OnePlusService;

import retrofit.RestAdapter;

public class RestClient {

    private RestAdapter restAdapter;

    public RestClient() {
        this.restAdapter = new RestAdapter.Builder()
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .setEndpoint("https://invites.oneplus.net")
            .build();
    }

    public OnePlusService getOnePlusService() {
        return restAdapter.create(OnePlusService.class);
    }

}

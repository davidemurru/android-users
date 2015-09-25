package me.nootify.users.service;

/**
 * API Class
 */

import ly.apps.android.rest.client.Callback;
import ly.apps.android.rest.client.annotations.GET;
import ly.apps.android.rest.client.annotations.Path;
import ly.apps.android.rest.client.annotations.RestService;

@RestService
public interface IRestAPI {

    // API for retrieve the users data.
    @GET("/{path}")
    void getList(@Path("path") String path, Callback<ServiceResponse> response);
}
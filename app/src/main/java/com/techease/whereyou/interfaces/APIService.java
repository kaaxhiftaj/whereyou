package com.techease.whereyou.interfaces;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by k.zahid on 5/14/18.
 */

public interface APIService {


    @Headers({
            "Content-Type:application/json"
    })
    @POST("send")
    Call<JSONObject> sendNotification(@Body JSONObject notification);


}
package com.example.apprendiendo

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap


interface WebAPIService {
    @GET("getApplication.php")
    fun getApplications(): Call<JsonArray>

    @GET("getCourse.php")
    fun getCourses(@Query(QUERY_PARAM_QUERY) query: String): Call<JsonArray>

    @GET("getSteps.php")
    fun getSteps(@Query(QUERY_PARAM_STEP) query: String): Call<JsonArray>

    companion object {
        private const val QUERY_PARAM_QUERY = "application_name"
        private const val QUERY_PARAM_STEP = "course_name"
    }
}
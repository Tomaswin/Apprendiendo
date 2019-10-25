package com.example.apprendiendo

import android.content.Context
import android.util.Log
import android.widget.ListView
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RestClientCall : InterfaceClient{
    lateinit var retrofit1: Retrofit
    private lateinit var restClient: WebAPIService
    override fun create() {
        retrofit1 = Retrofit.Builder()
            .baseUrl("https://shanatova.000webhostapp.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        restClient = retrofit1.create(WebAPIService::class.java)
    }

    override fun getApplications(listView: ListView, context: Context){
        var call = restClient.getApplications()
        call.enqueue(object : Callback<JsonArray> {
            override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                Log.e("JSON", t.toString())            }

            override fun onResponse(call: Call<JsonArray>, response: Response<JsonArray>) {
                Log.i("JSON",response.body().toString())
                val recipeList = ApplicationModel.getRecipesFromFile(response.body().toString(), context)
                val adapter = ApplicationAdapter(context, recipeList)
                listView.adapter = adapter
            }
        })
    }

    override fun getCourses(application: String,listView: ListView, context: Context) {
        var call = restClient.getCourses(application)
        call.enqueue(object : Callback<JsonArray> {
            override fun onResponse(call: Call<JsonArray>, response: Response<JsonArray>) {
                Log.i("JSON",response.body().toString())
                val recipeList = CourseModel.getRecipesFromFile(response.body().toString(), context)
                val adapter = CourseAdapter(context, recipeList)
                listView.adapter = adapter
            }

            override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                Log.e("JSON", t.toString())
            }
        })
    }
}
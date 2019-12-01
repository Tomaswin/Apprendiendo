package com.example.apprendiendo

import android.content.Context
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class ApplicationModel(
    val title: String,
    val description: String,
    val image: String,
    val course_count: String){
    //val imageUrl: String) {

    companion object {

        fun getRecipesFromFile(filename: String, context: Context): ArrayList<ApplicationModel> {
            val recipeList = ArrayList<ApplicationModel>()

            try {
                // Load data
                val recipes = JSONArray(filename)
                // Get Recipe objects from data
                (0 until recipes.length()).mapTo(recipeList) {
                    ApplicationModel(recipes.getJSONObject(it).getString("title"),
                        recipes.getJSONObject(it).getString("subtitle"),
                        recipes.getJSONObject(it).getString("image"),
                        //recipes.getJSONObject(it).getString("url"),
                        recipes.getJSONObject(it).getString("count_course"))
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return recipeList
        }

    }
}
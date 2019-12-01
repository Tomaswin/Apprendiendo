package com.example.apprendiendo

import android.content.Context
import org.json.JSONArray
import org.json.JSONException

class CourseModel(
    val title: String,
    val description: String,
    val image: String){
    //val course_count: String {

    companion object {

        fun getRecipesFromFile(filename: String, context: Context): ArrayList<CourseModel> {
            val recipeList = ArrayList<CourseModel>()

            try {
                // Load data
                val recipes = JSONArray(filename)
                // Get Recipe objects from data
                (0 until recipes.length()).mapTo(recipeList) {
                    CourseModel(recipes.getJSONObject(it).getString("title"),
                        recipes.getJSONObject(it).getString("subtitle"),
                        recipes.getJSONObject(it).getString("image"))
                        //recipes.getJSONObject(it).getString("course_count"))
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return recipeList
        }
    }
}
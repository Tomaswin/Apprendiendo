package com.example.apprendiendo

import android.content.Context
import org.json.JSONArray
import org.json.JSONException

class StepsModel(
    val text: String,
    val image: String,
    val positionX: String,
    val positionY: String){

    companion object {

        fun getRecipesFromFile(filename: String, context: Context): ArrayList<StepsModel> {
            val recipeList = ArrayList<StepsModel>()

            try {
                // Load data
                val recipes = JSONArray(filename)
                // Get Recipe objects from data
                (0 until recipes.length()).mapTo(recipeList) {
                    StepsModel(recipes.getJSONObject(it).getString("text"),
                        recipes.getJSONObject(it).getString("image"),
                        recipes.getJSONObject(it).getString("xPosition"),
                        recipes.getJSONObject(it).getString("yPosition"))
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return recipeList
        }
    }
}
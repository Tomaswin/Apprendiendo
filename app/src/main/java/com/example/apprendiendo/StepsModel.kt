package com.example.apprendiendo

import android.content.Context
import org.json.JSONArray
import org.json.JSONException

class StepsModel(
    val text: String,
    val image: String,
    val positionX: Int,
    val positionY: Int,
    val sizeX: Int,
    val sizeY: Int){

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
                        recipes.getJSONObject(it).getInt("xPosition"),
                        recipes.getJSONObject(it).getInt("yPosition"),
                        recipes.getJSONObject(it).getInt("xsize"),
                        recipes.getJSONObject(it).getInt("ysize"))
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return recipeList
        }
    }
}
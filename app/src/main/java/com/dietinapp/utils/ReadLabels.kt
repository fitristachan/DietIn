package com.dietinapp.utils

import android.content.Context
import com.dietinapp.model.Ingredient
import com.dietinapp.model.Recipe
import org.json.JSONObject

fun readRecipesFromJson(context: Context): List<Recipe> {
    val jsonString = context.assets.open("ingredients.json").bufferedReader().use {
        it.readText()
    }

    val jsonObject = JSONObject(jsonString)
    val dataArray = jsonObject.optJSONArray("data")

    val recipesList = mutableListOf<Recipe>()

    dataArray?.let { recipesArray ->
        for (i in 0 until recipesArray.length()) {
            val recipeObject = recipesArray.optJSONObject(i)

            val id = recipeObject?.optInt("id") ?: -1
            val name = recipeObject?.optString("name") ?: ""

            val status = if (recipeObject?.optString("status") == "low"){
                "Rendah Lektin"
            } else if (recipeObject?.optString("status") == "high"){
                "Tinggi Lektin"
            } else {
                "Belum Diketahui"
            }

//            val status = recipeObject?.optString("status") ?: ""

            val ingredientsList = mutableListOf<Ingredient>()
            val ingredientsArray = recipeObject?.optJSONArray("ingredients")

            ingredientsArray?.let { ingArray ->
                for (j in 0 until ingArray.length()) {
                    val ingredientObject = ingArray.optJSONObject(j)
                    val ingredientName = ingredientObject?.optString("name") ?: ""

                    val ingredientStatus = if (ingredientObject?.optString("status") == "low"){
                        "Rendah Lektin"
                    } else if (ingredientObject?.optString("status") == "high"){
                        "Tinggi Lektin"
                    } else {
                        "Belum Diketahui"
                    }

                    val ingredient = Ingredient(ingredientName, ingredientStatus)
                    ingredientsList.add(ingredient)
                }
            }

            val recipe = Recipe(id, name, status, ingredientsList)
            recipesList.add(recipe)
        }
    }

    return recipesList
}
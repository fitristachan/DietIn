package com.dietinapp.model

import com.dietinapp.retrofit.response.IngredientsItem

data class Recipe(
    val id: Int,
    val name: String,
    val status: Boolean,
    val ingredients: List<IngredientsItem>
)

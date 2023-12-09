package com.dietinapp.model


data class Ingredient(
    val ingredientName: String,
    val ingredientLectineStatus: Boolean
)

data class Recipe(
    val id: Int,
    val name: String,
    val status: Boolean,
    val ingredients: List<Ingredient>
)

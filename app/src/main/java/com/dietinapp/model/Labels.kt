package com.dietinapp.model


data class Ingredient(
    val name: String,
    val status: String
)

data class Recipe(
    val id: Int,
    val name: String,
    val status: String,
    val ingredients: List<Ingredient>
)

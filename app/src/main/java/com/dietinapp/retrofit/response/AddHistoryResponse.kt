package com.dietinapp.retrofit.response

import com.google.gson.annotations.SerializedName

data class AddHistoryResponse(

	@field:SerializedName("data")
	val data: Data,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Int
)

data class Data(

	@field:SerializedName("foodName")
	val foodName: String,

	@field:SerializedName("foodPhoto")
	val foodPhoto: String,

	@field:SerializedName("lectineStatus")
	val lectineStatus: Boolean,

	@field:SerializedName("ingredients")
	val ingredients: List<IngredientsItem>,

	@field:SerializedName("userId")
	val userId: String
)

data class IngredientsItem(

	@field:SerializedName("ingredientName")
	val ingredientName: String,

	@field:SerializedName("ingredientLectineStatus")
	val ingredientLectineStatus: Boolean
)

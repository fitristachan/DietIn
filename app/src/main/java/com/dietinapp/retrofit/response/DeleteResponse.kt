package com.dietinapp.retrofit.response

import com.google.gson.annotations.SerializedName

data class DeleteResponse(

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Int
)
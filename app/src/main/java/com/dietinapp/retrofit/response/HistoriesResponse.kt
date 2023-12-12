package com.dietinapp.retrofit.response

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class HistoriesResponse(

	@field:SerializedName("datas")
	val datas: List<HistoryItem>,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Int
)


@Entity(tableName = "histories")
data class HistoryItem(

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("foodName")
	val foodName: String,

	@field:SerializedName("foodPhoto")
	val foodPhoto: String,

	@field:SerializedName("lectineStatus")
	val lectineStatus: Boolean,

	@PrimaryKey
	@field:SerializedName("id")
	val id: String
)

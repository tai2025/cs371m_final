package edu.utap.exerciseapp.api

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.core.text.clearSpans
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Nutrition(
    @SerializedName("foods")
    val foods: List<Food>
): Serializable

data class RetNut(
    val name: String,
    val cat: String,
    val protein: Double,
    val fat: Double,
    val carb: Double,
    val cal: Double
): Serializable

data class Food(
    @SerializedName("description")
    val description: String,
    @SerializedName("foodCategory")
    val foodCategory: String,
    @SerializedName("foodNutrients")
    val foodNutrients: List<FoodNutrient>,
): Serializable

data class FoodNutrient(
    @SerializedName("nutrientId")
    val nutrientId: Int,
    @SerializedName("nutrientName")
    val nutrientName: String,
    @SerializedName("unitName")
    val unitName: String,
    @SerializedName("value")
    val value: Double,
): Serializable

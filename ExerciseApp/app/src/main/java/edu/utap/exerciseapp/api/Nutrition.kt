package edu.utap.exerciseapp.api

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class RetNut(
    val name: String,
    val company: String?,
    val cat: String,
    val protein: Double,
    val fat: Double,
    val carb: Double,
    val cal: Double
): Serializable

data class Nutrition(
    @SerializedName("totalHits") val totalHits: Int,
    @SerializedName("currentPage") val currentPage: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("pageList") val pageList: List<Int>,
    @SerializedName("foodSearchCriteria") val foodSearchCriteria: FoodSearchCriteria,
    @SerializedName("foods") val foods: List<Food>
) : Serializable

data class FoodSearchCriteria(
    @SerializedName("query") val query: String,
    @SerializedName("generalSearchInput") val generalSearchInput: String,
    @SerializedName("pageNumber") val pageNumber: Int,
    @SerializedName("numberOfResultsPerPage") val numberOfResultsPerPage: Int,
    @SerializedName("pageSize") val pageSize: Int,
    @SerializedName("requireAllWords") val requireAllWords: Boolean
) : Serializable

data class Food(
    @SerializedName("fdcId") val fdcId: Long,
    @SerializedName("description") val description: String,
    @SerializedName("dataType") val dataType: String,
    @SerializedName("gtinUpc") val gtinUpc: String,
    @SerializedName("publishedDate") val publishedDate: String,
    @SerializedName("brandOwner") val brandOwner: String,
    @SerializedName("brandName") val brandName: String,
    @SerializedName("ingredients") val ingredients: String,
    @SerializedName("marketCountry") val marketCountry: String,
    @SerializedName("foodCategory") val foodCategory: String,
    @SerializedName("modifiedDate") val modifiedDate: String,
    @SerializedName("dataSource") val dataSource: String,
    @SerializedName("packageWeight") val packageWeight: String,
    @SerializedName("servingSizeUnit") val servingSizeUnit: String,
    @SerializedName("servingSize") val servingSize: Double,
    @SerializedName("householdServingFullText") val householdServingFullText: String,
    @SerializedName("tradeChannels") val tradeChannels: List<String>,
    @SerializedName("allHighlightFields") val allHighlightFields: String,
    @SerializedName("score") val score: Double,
    @SerializedName("microbes") val microbes: List<Any>,
    @SerializedName("foodNutrients") val foodNutrients: List<FoodNutrient>,
    @SerializedName("finalFoodInputFoods") val finalFoodInputFoods: List<Any>,
    @SerializedName("foodMeasures") val foodMeasures: List<Any>,
    @SerializedName("foodAttributes") val foodAttributes: List<Any>,
    @SerializedName("foodAttributeTypes") val foodAttributeTypes: List<Any>,
    @SerializedName("foodVersionIds") val foodVersionIds: List<Any>
) : Serializable

data class FoodNutrient(
    @SerializedName("nutrientId") val nutrientId: Int,
    @SerializedName("nutrientName") val nutrientName: String,
    @SerializedName("nutrientNumber") val nutrientNumber: String,
    @SerializedName("unitName") val unitName: String,
    @SerializedName("derivationCode") val derivationCode: String,
    @SerializedName("derivationDescription") val derivationDescription: String,
    @SerializedName("derivationId") val derivationId: Int,
    @SerializedName("value") val value: Double,
    @SerializedName("foodNutrientSourceId") val foodNutrientSourceId: Int,
    @SerializedName("foodNutrientSourceCode") val foodNutrientSourceCode: String,
    @SerializedName("foodNutrientSourceDescription") val foodNutrientSourceDescription: String,
    @SerializedName("rank") val rank: Int,
    @SerializedName("indentLevel") val indentLevel: Int,
    @SerializedName("foodNutrientId") val foodNutrientId: Long
) : Serializable

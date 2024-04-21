package edu.utap.exerciseapp.api

import android.text.SpannableString
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call

class NutritionRepository(private val nutritionAPI: NutritionApi) {
    private val API_KEY = "NxO4HOL31FWr0r9egAwvfn4NbhSAYMOrQPqB3a5J"

    fun getFoods() {

    }

    // NB: This is for our testing.
    private val gson : Gson = GsonBuilder().registerTypeAdapter(
            SpannableString::class.java, NutritionApi.SpannableDeserializer()
        ).create()

    private fun unpackPosts(response: Call<Nutrition>): List<RetNut> {
        // XXX Write me.
        val list = mutableListOf<RetNut>()
        response
            .foods?.forEach {
            val name = it.description
            val cat = it.foodCategory
            val protein = it.foodNutrients.first { it.nutrientId == 1003 }
            val fat = it.foodNutrients.first { it.nutrientId == 1004 }
            val carb = it.foodNutrients.first { it.nutrientId == 1005 }
            val cal = it.foodNutrients.first { it.nutrientId == 1008 }
            list.add(RetNut(name, cat, protein.value, fat.value, carb.value, cal.value))
        }

        return list
    }

    suspend fun getSearch(search_key: String): List<RetNut> {
        val response : NutritionApi.NutritionResponse?
        response = nutritionAPI.getSearch(API_KEY, search_key)
        Log.d("debug response", "${response.data}")
        return unpackPosts(response)
    }
}

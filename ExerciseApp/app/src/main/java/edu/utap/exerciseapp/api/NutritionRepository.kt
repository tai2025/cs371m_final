package edu.utap.exerciseapp.api

import android.text.SpannableString
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import edu.utap.exerciseapp.MainActivity

class NutritionRepository(private val redditApi: NutritionApi) {
    // NB: This is for our testing.
    private val gson : Gson = GsonBuilder().registerTypeAdapter(
            SpannableString::class.java, NutritionApi.SpannableDeserializer()
        ).create()

    private fun unpackPosts(response: NutritionApi.ListingResponse): List<Nutrition> {
        // XXX Write me.
        val list = mutableListOf<Nutrition>()
        response.data.children.forEach {
            list.add(it.data)
        }
        return list
    }

    suspend fun getPosts(subreddit: String): List<Nutrition> {
        val response : NutritionApi.ListingResponse?
        response = redditApi.getPosts(subreddit)
        return unpackPosts(response)
//        if (MainActivity.globalDebug) {
//            response = gson.fromJson(
//                MainActivity.jsonAww100,
//                NutritionApi.ListingResponse::class.java)
//        } else {
//            // XXX Write me.
//            response = redditApi.getPosts(subreddit)
//        }
//        return unpackPosts(response!!)
    }

    suspend fun getSubreddits(): List<Nutrition> {
        val response : NutritionApi.ListingResponse?
        response = redditApi.getSubs()
        return unpackPosts(response)
//        if (MainActivity.globalDebug) {
//            response = gson.fromJson(
//                MainActivity.subreddit1,
//                NutritionApi.ListingResponse::class.java)
//        } else {
//            // XXX Write me.
//            response = redditApi.getSubs()
//        }
//        return unpackPosts(response!!)
    }
}

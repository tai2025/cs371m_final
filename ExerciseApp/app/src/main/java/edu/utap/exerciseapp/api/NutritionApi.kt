package edu.utap.exerciseapp.api

import android.text.SpannableString
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.lang.reflect.Type


interface NutritionApi {
    // XXX Write me, two function prototypes with Retrofit annotations
    // @GET contains a string appended to the base URL
    // the string is called a path name
    // You can add a parameter to the path name like this
     @GET("/r/{subreddit}/hot.json")
     suspend fun getPosts(
        @Path("subreddit") subreddit: String,
        @Query("limit") limit : Int = 100) : ListingResponse
    // The reddit api docs are here: https://www.reddit.com/dev/api/#GET_hot

     @GET("/subreddits/popular.json")
     suspend fun getSubs(
         @Query("limit") limit : Int = 100
     ) : ListingResponse


    // NB: Everything below here is fine, no need to change it

    // https://www.reddit.com/dev/api/#listings
    class ListingResponse(val data: ListingData)

    class ListingData(
        val children: List<RedditChildrenResponse>,
        val after: String?,
        val before: String?
    )
    data class RedditChildrenResponse(val data: Nutrition)

    // This class allows Retrofit to parse items in our model of type
    // SpannableString.  Note, given the amount of "work" we do to
    // enable this behavior, one can argue that Retrofit is a bit...."simple."
    class SpannableDeserializer : JsonDeserializer<SpannableString> {
        // @Throws(JsonParseException::class)
        override fun deserialize(
            json: JsonElement,
            typeOfT: Type,
            context: JsonDeserializationContext
        ): SpannableString {
            return SpannableString(json.asString)
        }
    }

    companion object {
        // Tell Gson to use our SpannableString deserializer
        private fun buildGsonConverterFactory(): GsonConverterFactory {
            val gsonBuilder = GsonBuilder().registerTypeAdapter(
                SpannableString::class.java, SpannableDeserializer()
            )
            return GsonConverterFactory.create(gsonBuilder.create())
        }
        // Keep the base URL simple
        //private const val BASE_URL = "https://www.reddit.com/"
        var httpurl = HttpUrl.Builder()
            .scheme("https")
            .host("www.reddit.com")
            .build()
        fun create(): NutritionApi = create(httpurl)
        private fun create(httpUrl: HttpUrl): NutritionApi {
            val client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    // Enable basic HTTP logging to help with debugging.
                    this.level = HttpLoggingInterceptor.Level.BASIC
                })
                .build()
            return Retrofit.Builder()
                .baseUrl(httpUrl)
                .client(client)
                .addConverterFactory(buildGsonConverterFactory())
                .build()
                .create(NutritionApi::class.java)
        }
    }
}
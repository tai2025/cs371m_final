package edu.utap.exerciseapp.model

import com.google.gson.annotations.SerializedName
import edu.utap.exerciseapp.api.RetNut
import java.io.Serializable

data class FoodModel (
    @SerializedName("list")
    var list : MutableList<RetNut> = mutableListOf(),
    @SerializedName("name")
    var name : String = ""
) : Serializable {
    fun remove(nut : RetNut) {
        list.remove(nut)
    }
}
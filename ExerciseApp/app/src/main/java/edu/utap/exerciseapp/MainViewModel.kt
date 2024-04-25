package edu.utap.exerciseapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.utap.exerciseapp.api.Nutrition
import edu.utap.exerciseapp.api.NutritionApi
import edu.utap.exerciseapp.api.NutritionRepository
import edu.utap.exerciseapp.api.RetNut
import edu.utap.exerciseapp.glide.Glide
import edu.utap.exerciseapp.model.FoodModel
import edu.utap.exerciseapp.model.UserModel
import edu.utap.exerciseapp.model.WorkoutEntry
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainViewModel : ViewModel() {
    // It is a real bummer that we need to put this here, but we do because
    // it is computed elsewhere, then we launch the camera activity
    // At that point our fragment can be destroyed, which means this has to be
    // remembered and restored.  Instead, we put it in the viewModel where we
    // know it will persist (and we can persist it)
    private var curUser = MutableLiveData<UserModel>()
    private var progList = MutableLiveData<List<WorkoutEntry>>()

    private var queried = MutableLiveData<Boolean>(false)

    private var nutAPI = NutritionApi.create()
    private var nutRepo = NutritionRepository(nutAPI)
    private var foods = MutableLiveData<List<RetNut>>()
    private var uid = MutableLiveData<String>()
    private var foodsList = MutableLiveData<MutableList<FoodModel>>(emptyList<FoodModel>().toMutableList())

    fun newFoodList(name : String) {
        foodsList.value!!.add(FoodModel(emptyList<RetNut>().toMutableList(), name))
    }

    fun addToList(pos : Int, item : RetNut) {
        foodsList.value!![pos].list.add(item)
    }

    fun removeFromList(name : String, item : RetNut) {
        foodsList.value!!.forEach {
            if(it.name.equals(name)){
                it.remove(item)
            }
        }
    }

    fun removeList(pos : Int) {
        foodsList.value!!.removeAt(pos)
    }

    fun getListOfNames(): MutableList<String> {
        val list = emptyList<String>().toMutableList()
        foodsList.value!!.forEach {
            list.add(it.name)
        }
        return list
    }

    fun observeFoodList(): MutableLiveData<MutableList<FoodModel>> {
        return foodsList
    }

    fun getList(): List<FoodModel> {
        if(foodsList.value == null){
            return listOf<FoodModel>()
        } else {
            return foodsList.value!!
        }
    }


    fun setUid(u : String) {
        viewModelScope.launch {
            uid.setValue(u)
        }
    }

    fun getUID(): MutableLiveData<String> {
        return uid
    }



    fun emptyFoods(){
        foods.postValue(emptyList())
    }

    fun searchFood(search : String){
        viewModelScope.launch {
            nutRepo.getFoods(search, object :
                Callback<Nutrition> {
                override fun onFailure(call: Call<Nutrition>, t: Throwable) { Log.d("-------", "Error fetching foods", t) }
                override fun onResponse(call: Call<Nutrition>,
                                        response: Response<Nutrition>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            Log.d("-------", "foods: $it")
                            val list = mutableListOf<RetNut>()
                            it.foods.forEach {food ->
                                val name = food.description
                                val cat = food.foodCategory
                                val company = food.brandOwner
                                val protein: Double = food.foodNutrients.firstOrNull  {id ->
                                    id.nutrientId == 1003 }?.value ?: -1.0
                                val fat: Double = food.foodNutrients.firstOrNull  {id ->
                                    id.nutrientId == 1004 }?.value ?: -1.0
                                val carb: Double = food.foodNutrients.firstOrNull  { id ->
                                    id.nutrientId == 1005 }?.value ?: -1.0
                                val cal: Double = food.foodNutrients.firstOrNull  { id ->
                                    id.nutrientId == 1008 }?.value ?: -1.0
                                val nut = RetNut(name, company, cat, protein, fat, carb, cal)
                                if(nut != null){
                                    list.add(nut)
                                }
                            }
                            foods.postValue(list)
                            Log.d("-------", "$list")
                        }
                    } else Log.e("-------", "Failed to fetch foods: ${response.errorBody()?.string()}")
                }
            })
        }
    }

    fun observeFoods(): MutableLiveData<List<RetNut>> {
        return foods
    }

    fun setQueried(b : Boolean) {
        queried.postValue(b)
    }

    fun isQueried() : LiveData<Boolean> {
        return queried
    }

    fun setCurUser(u: UserModel) {
        curUser.postValue(u)
    }
    fun observeCurUser() : LiveData<UserModel> {
        return curUser
    }
    fun getCurUser() : LiveData<UserModel> {
        return curUser
    }

    fun addToProgList(we : WorkoutEntry) {
        val list = mutableListOf<WorkoutEntry>()
        progList.value?.let { list.addAll(it.toList()) }
        list.add(we)
        progList.setValue(list.toList())
        Log.d("proglistviewmodel", "${progList.value}")
    }

    fun removeFromProgList(i: Int) {
        val list = mutableListOf<WorkoutEntry>()
        progList.value?.let { list.addAll(it.toList()) }
        list.removeAt(i)
        progList.setValue(list.toList())
        Log.d("proglistviewmodel", "${progList.value}")
    }

    fun updateProgList(i: Int, we: WorkoutEntry) {
        val list = mutableListOf<WorkoutEntry>()
        progList.value?.let { list.addAll(it.toList()) }
        list.removeAt(i)
        list.add(i, we)
        progList.setValue(list.toList())
    }

    fun setProgList(list : List<WorkoutEntry>) {
        progList.postValue(list)
    }

    fun getProgList(): List<WorkoutEntry> {
        if (progList.value == null) {
            return listOf<WorkoutEntry>()
        }
        return progList.value!!
    }


    private var pictureUUID = ""
    // Only call this from TakePictureWrapper
    fun takePictureUUID(uuid: String) {
        pictureUUID = uuid
    }
    var pictureNameByUser = "" // String provided by the user

    // Track current authenticated user
    private var currentAuthUser = invalidUser



    // MainActivity gets updates on this via live data and informs view model
    fun setCurrentAuthUser(user: User) {
        currentAuthUser = user
    }
}

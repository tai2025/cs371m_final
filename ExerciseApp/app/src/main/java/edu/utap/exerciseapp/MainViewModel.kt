package edu.utap.exerciseapp

import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.utap.exerciseapp.api.NutritionApi
import edu.utap.exerciseapp.api.NutritionRepository
import edu.utap.exerciseapp.api.RetNut
import edu.utap.exerciseapp.glide.Glide
import edu.utap.exerciseapp.model.PhotoMeta
import edu.utap.exerciseapp.model.UserModel
import edu.utap.exerciseapp.model.WorkoutEntry
import edu.utap.exerciseapp.view.TakePictureWrapper
import kotlinx.coroutines.launch

enum class SortColumn {
    TITLE,
    SIZE
}
data class SortInfo(val sortColumn: SortColumn, val ascending: Boolean)
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

    fun setUid(u : String) {
        viewModelScope.launch {
            uid.setValue(u)
        }
    }

    fun getUID(): MutableLiveData<String> {
        return uid
    }

    fun searchFood(search : String){
        viewModelScope.launch {
            foods.postValue(nutRepo.getSearch(search))
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
        progList.postValue(list.toList())
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
    // LiveData for entire note list, all images
    private var photoMetaList = MutableLiveData<List<PhotoMeta>>()
    private var sortInfo = MutableLiveData(
        SortInfo(SortColumn.TITLE, true))
    // Track current authenticated user
    private var currentAuthUser = invalidUser
    // Firestore state
    private val storage = Storage()
    // Database access
    private val dbHelp = ViewModelDBHelper()


    /////////////////////////////////////////////////////////////
    // Notes, memory cache and database interaction
    fun fetchPhotoMeta(resultListener:()->Unit) {
        dbHelp.fetchPhotoMeta(sortInfo.value!!) {
            photoMetaList.postValue(it)
            resultListener.invoke()
        }
    }
    fun observePhotoMeta(): LiveData<List<PhotoMeta>> {
        return photoMetaList
    }
    fun observeSortInfo(): LiveData<SortInfo> {
        return sortInfo
    }

    fun sortInfoClick(sortColumn: SortColumn,
                      resultListener: () -> Unit) {
        // XXX User has changed sort info
    }

    // MainActivity gets updates on this via live data and informs view model
    fun setCurrentAuthUser(user: User) {
        currentAuthUser = user
    }
    fun removePhotoAt(position: Int) {
        // XXX Deletion requires two different operations.  What are they?
    }

    // Get a note from the memory cache
    fun getPhotoMeta(position: Int) : PhotoMeta {
        val note = photoMetaList.value?.get(position)
        return note!!
    }

    private fun createPhotoMeta(pictureTitle: String, uuid : String,
                                byteSize : Long) {
        val currentUser = currentAuthUser
        val photoMeta = PhotoMeta(
            ownerName = currentUser.name,
            ownerUid = currentUser.uid,
            uuid = uuid,
            byteSize = byteSize,
            pictureTitle = pictureTitle,
        )
        dbHelp.createPhotoMeta(sortInfo.value!!, photoMeta) {
            photoMetaList.postValue(it)
        }
    }

    /////////////////////////////////////////////////////////////
    // We can't just schedule the file upload and return.
    // The problem is that our previous picture uploads can still be pending.
    // So a note can have a pictureFileName that does not refer to an existing file.
    // That violates referential integrity, which we really like in our db (and programming
    // model).
    // So we do not add the pictureFileName to the note until the picture finishes uploading.
    // That means a user won't see their picture updates immediately, they have to
    // wait for some interaction with the server.
    // You could imagine dealing with this somehow using local files while waiting for
    // a server interaction, but that seems error prone.
    // Freezing the app during an upload also seems bad.
    fun pictureSuccess() {
        val photoFile = TakePictureWrapper.fileNameToFile(pictureUUID)
        // XXX Write me while preserving referential integrity
    }
    fun pictureFailure() {
        // Note, the camera intent will only create the file if the user hits accept
        // so I've never seen this called
        pictureUUID = ""
        pictureNameByUser = ""
    }

    fun glideFetch(uuid: String, imageView: ImageView) {
        Glide.fetch(storage.uuid2StorageReference(uuid),
            imageView)
    }
}

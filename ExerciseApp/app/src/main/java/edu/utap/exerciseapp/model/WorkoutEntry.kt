package edu.utap.exerciseapp.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

class WorkoutEntry() {
    val list = mutableListOf<Workout>()
    fun addToList(workout: Workout) {
        list.add(workout)
    }
    class Workout() {
        var Exercise: String = ""
        var Set: String = ""
        var Rep: String = ""
        var Weight: String = ""
        var Note: String = ""
        var RPE: String = ""
    }
}
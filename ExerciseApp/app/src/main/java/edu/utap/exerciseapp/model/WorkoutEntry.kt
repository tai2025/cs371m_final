package edu.utap.exerciseapp.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class WorkoutEntry() {
    val list = mutableListOf<Workout>()
    private var entryNum: Int = 0

    private var localDate: LocalDate = LocalDate.MIN

    fun WorkoutEntry(){
    }


    fun setLocalDate(date: String) {
        localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyy"))
    }
    fun getLocalDate(): LocalDate{
        return localDate
    }
    fun getEntryNum(): Int{
        return entryNum
    }
    fun setEntryNum(num: Int) {
        entryNum = num
    }

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
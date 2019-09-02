package com.wmontgom85.personsreponativekotlin.repo

import android.content.Context
import androidx.room.RoomDatabase
import androidx.room.Database
import androidx.room.Room
import com.wmontgom85.personsreponativekotlin.model.Person

@Database(entities = [
    Person::class
], version = 1)
abstract class DBHelper : RoomDatabase() {
    abstract fun personDao(): PersonDao

    companion object {
        private var INSTANCE: DBHelper? = null

        fun getInstance(context: Context): DBHelper? {
            if (INSTANCE == null) {
                synchronized(DBHelper::class) {
                    INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            DBHelper::class.java, "persons.db"
                    ).build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }

    fun cleanDB() {
        personDao().deleteAll()
    }
}
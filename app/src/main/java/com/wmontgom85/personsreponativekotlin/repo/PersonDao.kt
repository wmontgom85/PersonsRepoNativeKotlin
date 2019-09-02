package com.wmontgom85.personsreponativekotlin.repo

import androidx.room.*
import com.wmontgom85.personsreponativekotlin.model.Person

@Dao
public interface PersonDao {
    @Query("SELECT * FROM Person")
    fun getPeople(): List<Person>

    @Query("SELECT * FROM Person WHERE id = :id")
    fun getPerson(id: Long) : Person

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(person: Person) : Long

    @Query("DELETE FROM Person WHERE id = :pId")
    fun delete(pId: Int)

    @Delete
    fun delete(person: Person)

    @Update
    fun update(person: Person) : Int

    @Query("DELETE FROM Person")
    fun deleteAll()
}
package com.example.momoanalytics

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TransactionDao {
    @Query("SELECT * FROM `transaction` WHERE type IN (:transactionTypes) AND time >= :from AND  time <= :to")
    fun getAll(transactionTypes: IntArray, from: Long = 0, to: Long = Long.MAX_VALUE): List<Transaction>

    @Query("SELECT * FROM `transaction` WHERE (subject_first_name LIKE :name || '%' OR subject_last_name LIKE :name || '%') AND (time >= :from AND  time <= :to)")
    fun findBySubjectName(name: String, from: Long = 0, to: Long = Long.MAX_VALUE): List<Transaction>

    @Query("SELECT * FROM `transaction` WHERE uid = :id")
    fun findById(id : Int): Transaction

    @Query("DELETE FROM `transaction`")
    fun deleteAll()

    @Insert
    fun insertAll(vararg users: Transaction)

    @Delete
    fun delete(user: Transaction)
}
package com.futureglories.momoanalitika.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.futureglories.momoanalitika.data.Transaction
import com.futureglories.momoanalitika.data.TransactionDao

@Database(entities = arrayOf(Transaction::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}

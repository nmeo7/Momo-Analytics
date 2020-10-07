package com.example.momoanalytics

import android.content.Context
import android.util.Log
import androidx.room.Room
import java.lang.Exception
import java.util.concurrent.Executor

class DataPersistence (val context: Context, private val executor: Executor): Executor {

    companion object {
        lateinit var db: AppDatabase
        lateinit var transactionDao: TransactionDao
    }

    init {
        db = Room.databaseBuilder(
            context,
            AppDatabase::class.java, "journal6"
        ).build()
        transactionDao = db.transactionDao()
    }

    fun resetDatabase ()
    {
        execute {
            val parser: MomoParser = MomoParser(context)
            val transactions = parser.retrieveNowPlaying()

            transactionDao.deleteAll()

            for (tr in transactions)
                if (tr != null)
                    try {
                        transactionDao.insertAll(tr)
                    }
                    catch (e: Exception)
                    {
                        Log.i("AAA TAG", e.toString())
                    }
        }
    }

    fun retrieve(name: String, from: Long = 0, to: Long = Long.MAX_VALUE, callback: (List<Transaction>) -> Unit)
    {
        execute {
            val ret = transactionDao.findBySubjectName(name, from, to)

            for (transaction in ret)
                Log.i("AAAB", transaction.toString())

            callback (ret)
        }
    }

    fun retrieve(id: Int)
    {
        execute {
            val ret = transactionDao.findById(id)
        }
    }

    fun retrieveAll (from: Long, to: Long, callback: (List<Transaction>) -> Unit)
    {
        execute {
            val ret = transactionDao.getAll(intArrayOf(0, 1, 2, 3, 4, 5, 6), from, to)
            // for (transaction in ret)
                // Log.i("AAA", transaction.toString())
            callback (ret)
        }
    }

    fun retrieveByTypes (types: IntArray, from: Long, to: Long)
    {
        execute {
            val ret = transactionDao.getAll(types, from, to)
        }
    }

    override fun execute(r: Runnable?) {
        Thread(r).start()
    }
}
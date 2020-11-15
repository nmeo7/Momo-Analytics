package com.futureglories.momoanalitika.activities

import android.R.attr.defaultValue
import android.R.attr.key
import android.content.Context
import android.content.Intent.getIntent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.futureglories.momoanalitika.R
import com.futureglories.momoanalitika.data.Transaction


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class SummaryFragment : Fragment() {

    private val TAG = "SUMMARY FRAGMENT"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_summary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = this.arguments
        if (bundle != null) {
            Log.i(TAG, "debug 1")
            val transactions = bundle.getSerializable("data") as List<Transaction>

            transactions.forEach {
                    transaction -> Log.i(TAG, "data here ${transaction.subject_last_name}")
            }
        }

        Log.i(TAG, "debug 2")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}
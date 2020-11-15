package com.futureglories.momoanalitika.activities

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.futureglories.momoanalitika.R
import com.futureglories.momoanalitika.data.Transaction
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.util.ArrayList

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class TimeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_time, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = this.arguments
        var chart = view.findViewById<LineChart>(R.id.line_chart);
        val values: ArrayList<Entry> = ArrayList()
        var i = 0

        if (bundle != null) {
            val transactions = bundle.getSerializable("data") as List<Transaction>

            transactions.forEach {
                    transaction -> values.add(
                        Entry(
                            i.toFloat() ,
                            transaction.balance!!.toFloat()
                        )
                    )
                i++
            }
        }

        val set1 : LineDataSet = LineDataSet(values, "DataSet 1")
        val data : LineData = LineData(set1)
        chart.data = data
        chart.invalidate()
    }
}
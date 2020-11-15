package com.futureglories.momoanalitika.activities

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import com.futureglories.momoanalitika.R
import com.futureglories.momoanalitika.data.Transaction
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import java.util.ArrayList

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class DistributionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_distribution, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val chart = view.findViewById<PieChart>(R.id.pie_chart)
        val values: ArrayList<PieEntry> = ArrayList()
        val bundle = this.arguments
        var i = 0

        val mapp = mutableMapOf( "other" to 0.toFloat() )

        if (bundle != null) {
            val transactions = bundle.getSerializable("data") as List<Transaction>

            transactions.forEach {
                val name = it.subject_first_name + it.subject_last_name
                mapp.putIfAbsent(name, 0.toFloat())
                mapp[name]?.plus(it.amount!!.toFloat())
            }

            mapp.forEach {
                    transaction -> values.add(
                        PieEntry(
                            transaction.value,
                            i.toFloat()
                        )
                    )
                    i++
            }
        }

        val set1 = PieDataSet (values, "DataSet 1")
        val data =  PieData(set1)
        chart.data = data
        chart.invalidate()
    }
}
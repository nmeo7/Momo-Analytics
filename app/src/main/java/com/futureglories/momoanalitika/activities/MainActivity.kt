package com.futureglories.momoanalitika.activities


import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Cartesian
import com.anychart.core.cartesian.series.Line
import com.anychart.data.Mapping
import com.anychart.data.Set
import com.anychart.enums.Anchor
import com.anychart.enums.MarkerType
import com.anychart.enums.TooltipPositionMode
import com.anychart.graphics.vector.Stroke
import com.futureglories.momoanalitika.R
import com.futureglories.momoanalitika.data.DataPersistence
import com.futureglories.momoanalitika.data.Transaction
import java.util.*
import java.util.concurrent.Executor

// 1. intercept sms,
// 2. save all the data to internal memory - ok
// 3. display the data on a listbox
// 4. implement some graphs
// 5. implement all the fancy stuff
// 6. refresh, reset, etc.
// 7. refactor code


class MainActivity : AppCompatActivity() {

    fun drawchart (transaction: List<Transaction>)
    {
        val anyChartView = findViewById<AnyChartView>(R.id.any_chart_view)
        anyChartView.setProgressBar(findViewById(R.id.progress_bar))

        val cartesian: Cartesian = AnyChart.line()

        cartesian.animation(true)

        cartesian.padding(10.0, 20.0, 5.0, 20.0)

        cartesian.crosshair().enabled(true)
        cartesian.crosshair()
            .yLabel(true)
            .yStroke(null as Stroke?, null, null, null as String?, null as String?)

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT)

        // cartesian.title("Trend of Sales of the Most Popular Products of ACME Corp.")
        var st = ""

        cartesian.yAxis(0).title("Spendings")
        cartesian.xAxis(0).labels().padding(5.0, 5.0, 5.0, 5.0)

        val seriesData: MutableList<DataEntry> = ArrayList()

        var ii = 1
        var fee = 0
        var amount = 0

        for (tr: Transaction in transaction)
        {
            Log.i("AAAXX", tr.toString())
            ii++
            seriesData.add(
                CustomDataEntry(
                    ii.toString(),
                    tr.amount,
                    tr.fee,
                    tr.balance
                )
            )
            fee += tr.fee!!
        }

        cartesian.title("Total Fee: $fee")

        seriesData.add(
            CustomDataEntry(
                "1986",
                3.6,
                2.3,
                2.8
            )
        )
        seriesData.add(
            CustomDataEntry(
                "1987",
                7.1,
                4.0,
                4.1
            )
        )
        seriesData.add(
            CustomDataEntry(
                "1988",
                8.5,
                6.2,
                5.1
            )
        )
        seriesData.add(
            CustomDataEntry(
                "1989",
                9.2,
                11.8,
                6.5
            )
        )
        seriesData.add(
            CustomDataEntry(
                "1990",
                10.1,
                13.0,
                12.5
            )
        )

        val set = Set.instantiate()
        set.data(seriesData)
        set.data(seriesData)
        val series1Mapping: Mapping = set.mapAs("{ x: 'x', value: 'value' }")
        val series2Mapping: Mapping = set.mapAs("{ x: 'x', value: 'value2' }")
        val series3Mapping: Mapping = set.mapAs("{ x: 'x', value: 'value3' }")

        val series1: Line = cartesian.line(series1Mapping)
        series1.name("Amount")
        series1.hovered().markers().enabled(true)
        series1.hovered().markers()
            .type(MarkerType.CIRCLE)
            .size(4.0)
        series1.tooltip()
            .position("right")
            .anchor(Anchor.LEFT_CENTER)
            .offsetX(5.0)
            .offsetY(5.0)

        val series2: Line = cartesian.line(series2Mapping)
        series2.name("Fee")
        series2.hovered().markers().enabled(true)
        series2.hovered().markers()
            .type(MarkerType.CIRCLE)
            .size(4.0)
        series2.tooltip()
            .position("right")
            .anchor(Anchor.LEFT_CENTER)
            .offsetX(5.0)
            .offsetY(5.0)

        val series3: Line = cartesian.line(series3Mapping)
        series3.name("Balance")
        series3.hovered().markers().enabled(true)
        series3.hovered().markers()
            .type(MarkerType.CIRCLE)
            .size(4.0)
        series3.tooltip()
            .position("right")
            .anchor(Anchor.LEFT_CENTER)
            .offsetX(5.0)
            .offsetY(5.0)

        cartesian.legend().enabled(true)
        cartesian.legend().fontSize(13.0)
        cartesian.legend().padding(0.0, 0.0, 10.0, 0.0)

        anyChartView.setChart(cartesian)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_charts_common)

        val tr = LinkedList<Transaction>()

        // drawchart (tr)
        /*
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)*/


        // now testing this ussd code thing here.
        var UssdCode = "*131#"
        //we want to remove the last # from the ussd code as we need to encode it. so *555# becomes *555
        UssdCode = UssdCode.substring(0, UssdCode.length - 1)

        val UssdCodeNew = UssdCode + Uri.encode("#")

        //request for permission
        if (ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.CALL_PHONE),
                1
            )
        } else {
            // startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:$UssdCodeNew")))
        }

        if (ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.READ_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.READ_SMS),
                1
            )
        }

        if (ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.RECEIVE_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.RECEIVE_SMS),
                1
            )
        }

        val dataPersistence =
            DataPersistence(
                this,
                Executor {})
        // dataPersistence.resetDatabase ()
        // dataPersistence.retrieveAll(1550311629000, 1550512629000)
        // dataPersistence.retrieveAll(0, Long.MAX_VALUE)
        dataPersistence.retrieveAll(1596203697000, Long.MAX_VALUE)
        {
            runOnUiThread{
                drawchart(it)
            }
        }
        dataPersistence.retrieve("eli") {
            Log.i ("AAAX", "this is the response to the main thread")
        }
    }


    private class CustomDataEntry internal constructor(
        x: String?,
        value: Number?,
        value2: Number?,
        value3: Number?
    ) :
        ValueDataEntry(x, value) {
        init {
            setValue("value2", value2)
            setValue("value3", value3)
        }
    }
}
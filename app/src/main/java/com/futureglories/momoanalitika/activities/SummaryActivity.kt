package com.futureglories.momoanalitika.activities

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.fragment.app.FragmentActivity
import com.allattentionhere.fabulousfilter.AAH_FabulousFragment
import com.fastaccess.permission.base.callback.OnPermissionCallback
import com.futureglories.momoanalitika.R
import com.futureglories.momoanalitika.data.DataPersistence
import com.futureglories.momoanalitika.data.Transaction
import com.futureglories.momoanalitika.util.RequestPermissions
import com.gauravk.bubblenavigation.BubbleNavigationLinearView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import java.io.Serializable
import java.sql.Timestamp
import java.util.*
import java.util.concurrent.Executor


class SummaryActivity : FragmentActivity(), OnPermissionCallback, TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener,
    AAH_FabulousFragment.Callbacks
{

val TAG = "SUMMARY ACTIVITY"


    private val MULTI_PERMISSIONS = arrayOf(
        Manifest.permission.GET_ACCOUNTS,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    val fragmentManager = supportFragmentManager
    var records = listOf<Transaction>()

    fun refreshCurrentFragment () {
        Log.i(TAG, "refreshing fragment")
        val frg = fragmentManager.findFragmentById(R.id.summary_fragment)
        val ft = fragmentManager.beginTransaction()

        if (frg == null)
            return;

        ft.detach(frg)
        ft.attach(frg)
        ft.commit()

        Log.i(TAG, "fragment refreshed")
    }

    fun fetchEntries (from: Long, to: Long = Long.MAX_VALUE)
    {
        val dataPersistence =
            DataPersistence(
                this,
                Executor {})

        dataPersistence.retrieveAll(from, to)
        {
            records = it

            for (rec in records)
            {
                val st = Timestamp (rec.time!!).toLocaleString() + " " + rec.subject_first_name
                // Log.i(TAG, st)
            }

            // calculate the summaries here, and also keep the shits here.
            // maybe also force re render of the screens.
            refreshCurrentFragment ()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)
        // setSupportActionBar(findViewById(R.id.toolbar))

        var fab = findViewById<FloatingActionButton>(R.id.fab)

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {

            val dialogFrag: ContentFragment = ContentFragment.newInstance()
            dialogFrag.setParentFab(fab)
            dialogFrag.show(supportFragmentManager, dialogFrag.getTag())

            /*
            Log.i(TAG, "Trying this")

            var permissionHelper = PermissionHelper.getInstance(this)
            permissionHelper
                .setForceAccepting(false) // default is false. its here so you know that it exists.
                .request(MULTI_PERMISSIONS)*/


        }

        RequestPermissions.request(this);


        val dataPersistence =
            DataPersistence(
                this,
                Executor {})
        // dataPersistence.resetDatabase ()
        // dataPersistence.retrieveAll(1550311629000, 1550512629000)
        // dataPersistence.retrieveAll(0, Long.MAX_VALUE)
        dataPersistence.retrieveAll(1596203697000, Long.MAX_VALUE)
        {
            records = it
            runOnUiThread{
                // drawchart(it)
            }
        }
        dataPersistence.retrieve("eli") {
            records = it
            Log.i ("AAAX", "this is the response to the main thread")
        }

        findViewById<Button>(R.id.current_month).setOnClickListener {
            Log.d (TAG, Calendar.getInstance().toString())

            val c = Calendar.getInstance()

            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            // val day = c.get(Calendar.DAY_OF_MONTH)

            fetchEntries ( Timestamp ( year - 1900, month, 1, 0, 0, 0, 0 ).time );
        }

        findViewById<Button>(R.id.last_30_days).setOnClickListener {
            fetchEntries ( Calendar.getInstance().timeInMillis - 3600000 * 24 * 30.toLong() );
        }

        findViewById<Button>(R.id.custom_interval).setOnClickListener {
            val now: Calendar = Calendar.getInstance()
            val dpd =
                TimePickerDialog.newInstance(
                    { _: TimePickerDialog, h: Int, m: Int, s: Int ->

                    },
                    now.get(Calendar.HOUR_OF_DAY),
                    now.get(Calendar.MINUTE),
                    true
                )
            dpd.show(fragmentManager, "Datepickerdialog");
        }






        changeFragment()

        val navigation_view = findViewById<BubbleNavigationLinearView>(R.id.bottom_navigation_view_linear)
        navigation_view.setNavigationChangeListener { view , position ->
            //navigation changed, do something here
            Log.i(TAG, "We are here, yo! $position")

            val fragmentTransaction = fragmentManager.beginTransaction()

            val fragment = when (position) {
                1 -> ListFragment()
                2 -> DistributionFragment()
                3 -> TimeFragment()
                else -> SummaryFragment()
            }

            val bundle = Bundle()
            bundle.putSerializable("data", records as Serializable)
            fragment.arguments = bundle

            for (record in records)
                Log.i(TAG, record.subject_first_name.toString())

            fragmentTransaction.replace(R.id.summary_fragment, fragment)
            fragmentTransaction.commit()
        }


    }

    override fun onPermissionPreGranted(permissionsName: String) {
    }

    override fun onPermissionGranted(permissionName: Array<out String>) {
    }

    override fun onNoPermissionNeeded() {
    }

    override fun onPermissionReallyDeclined(permissionName: String) {
    }

    override fun onPermissionDeclined(permissionName: Array<out String>) {
    }

    override fun onPermissionNeedExplanation(permissionName: String) {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        val time =
            "You picked the following time: " + hourOfDay + "h" + minute + "m" + second
        Log.i(TAG, time)
    }

    override fun onDateSet(
        view: DatePickerDialog?,
        year: Int,
        monthOfYear: Int,
        dayOfMonth: Int
    ) {
        val date =
            "You picked the following date: " + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year
        Log.i(TAG, date)
    }

    fun changeFragment()
    {
        val fragment = FirstFragment()
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.summary_fragment, fragment)
        fragmentTransaction.commit()

    }

    override fun onResult(result: Any?) {
        Log.i(TAG, "Here is a callback, you happy about it?")
    }

}
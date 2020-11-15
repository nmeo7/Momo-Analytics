package com.futureglories.momoanalitika.activities

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.*

import androidx.appcompat.widget.AppCompatButton
import com.allattentionhere.fabulousfilter.AAH_FabulousFragment
import com.futureglories.momoanalitika.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import java.util.*
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog


class ContentFragment : AAH_FabulousFragment(), AdapterView.OnItemSelectedListener {

    override fun setupDialog(dialog: Dialog, style: Int) {
        val contentView: View = View.inflate(context, R.layout.filter_view, null)
        val rl_content = contentView.findViewById(R.id.rl_content) as RelativeLayout
        val ll_buttons = contentView.findViewById(R.id.goto_settings) as TextView

        contentView.findViewById<Button>(R.id.btn_close).setOnClickListener { closeFilter("closed") }

        //params to set
        setAnimationDuration(600) //optional; default 500ms
        setPeekHeight(300) // optional; default 400dp
        setCallbacks  (activity as Callbacks?) //optional; to get back result
        //setAnimationListener(activity as AnimationListener?) //optional; to get animation callbacks
        setViewgroupStatic(ll_buttons) // optional; layout to stick at bottom on slide
        //setViewPager(vp_types) //optional; if you use viewpager that has scrollview
        setViewMain(rl_content) //necessary; main bottomsheet view
        setMainContentView(contentView) // necessary; call at end before super
        super.setupDialog(dialog, style) //call super at last
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*
        view.findViewById<TextView>(R.id.date_from).setOnClickListener {
            dateTimePicker ()
        }
        view.findViewById<RangeSeekBar>(R.id.sb_range_1).setOnRangeChangedListener {

        } */

        val spinner: Spinner = view.findViewById(R.id.activities)

        ArrayAdapter.createFromResource(
            context!!,
            R.array.activities,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = this

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // view?.findViewById<TextView>(R.id.date_from)?.setOnClickListener {
            // dateTimePicker ()
        // }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
    }


    fun dateTimePicker ()
    {
        val now: Calendar = Calendar.getInstance()
        val dpd =
            TimePickerDialog.newInstance(
                { _: TimePickerDialog, h: Int, m: Int, s: Int ->

                },
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
            )
        dpd.show(childFragmentManager, "Datepickerdialog");
    }

    companion object {
        fun newInstance(): ContentFragment {
            return ContentFragment()
        }
    }
}
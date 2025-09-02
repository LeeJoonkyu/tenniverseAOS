package com.heejae.tenniverse.persentation.home.rent.common

import android.lecture.myapplication.util.DEBUG
import android.lecture.myapplication.util.name
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.heejae.tenniverse.R
import com.heejae.tenniverse.persentation.base.BaseActivity
import com.heejae.tenniverse.util.calendar.hour
import com.heejae.tenniverse.util.calendar.minute
import java.util.Calendar
import java.util.Locale

abstract class EventActivity<B : ViewDataBinding>(
    @LayoutRes val eventLayoutId: Int
) : BaseActivity<B>(eventLayoutId) {
    abstract val viewModel: RentCommonViewModel

    protected lateinit var datePicker: MaterialDatePicker<Long>
    protected lateinit var timePicker: MaterialTimePicker

    override fun initView() {
        initBinding()
        setPicker()
        eventInitView()
    }

    abstract fun initBinding()
    abstract fun eventInitView()

    private fun setPicker() {
        val validator = DateValidatorPointForward.now()

        timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(viewModel.calendar.value.hour())
            .setMinute(viewModel.calendar.value.minute())
            .setTheme(R.style.Widget_App_TimePicker_Clock)
            .setTitleText("")
            .build()
            .apply {
                addOnPositiveButtonClickListener {
                    viewModel.setDateModel(hour, minute)
                    DEBUG(this@EventActivity.name, "hour: $hour")
                }
            }

        datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("")
                .setCalendarConstraints(
                    CalendarConstraints.Builder()
                        .setValidator(validator).build()
                )
                .setTheme(R.style.ThemeOverlay_App_DatePicker)
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
                .apply {
                    addOnPositiveButtonClickListener { time ->
                        time?.let {
                            val calendar = Calendar.getInstance(Locale.getDefault()).apply {
                                timeInMillis = it
                            }
                            viewModel.setDateModel(calendar)
                        }
                    }
                }
    }
}
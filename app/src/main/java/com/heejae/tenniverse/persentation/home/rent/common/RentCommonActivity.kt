package com.heejae.tenniverse.persentation.home.rent.common

import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import com.heejae.tenniverse.R
import com.heejae.tenniverse.domain.model.CourtType
import com.heejae.tenniverse.domain.model.GameType

abstract class RentCommonActivity<B : ViewDataBinding>(
    @LayoutRes val rentLayoutId: Int
): EventActivity<B>(rentLayoutId) {

    override fun eventInitView() {
        setPopupMenuAdapter()
        rentInitView()
    }

    abstract fun rentInitView()

    abstract fun getGenderACTV(): AutoCompleteTextView
    abstract fun getCoatACTV(): AutoCompleteTextView

    private fun setPopupMenuAdapter() {
        val genderList = GameType.values().map { getString(it.kor) }
        val coatList = CourtType.values().map { getString(it.kor) }
        setPopupMenuAdapter(genderList, getGenderACTV())
        setPopupMenuAdapter(coatList, getCoatACTV())
    }

    private fun setPopupMenuAdapter(list: List<String>, view: AutoCompleteTextView) {
        view.apply {
            setDropDownBackgroundResource(R.color.white)
            setAdapter(
                ArrayAdapter(this@RentCommonActivity, R.layout.list_popup_window_item, list)
            )
        }
    }
}
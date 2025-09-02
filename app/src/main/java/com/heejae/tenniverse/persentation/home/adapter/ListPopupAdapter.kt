package com.heejae.tenniverse.persentation.home.adapter

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.content.ContextCompat
import androidx.core.graphics.translationMatrix
import com.heejae.tenniverse.R
import kotlin.math.roundToInt

class ListPopupAdapter(
    val context: Context,
    val view: View,
    private val items: List<String>,
    val onClick: (Int) -> Unit,
) {
    lateinit var listPopupWindow : ListPopupWindow

    init {
        initAdapter()
    }

    private fun initAdapter() {
        val newContext = ContextThemeWrapper(context, R.style.PopupMenuStyle)

        listPopupWindow = ListPopupWindow(newContext).apply {
            val adapter = ArrayAdapter(context, R.layout.list_popup_window_item, items)
            setAdapter(adapter)
            anchorView = view
            width = (100 * context.resources.displayMetrics.density).roundToInt()
            translationMatrix(200f, 0f)
            setBackgroundDrawable(ContextCompat.getDrawable(context, com.heejae.tenniverse.R.drawable.ic_custom_popup_background))
            setOnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
                // Respond to list popup window item click.
                onClick(position)
                // Dismiss popup.
                listPopupWindow.dismiss()
            }
        }
    }
}
package com.rangoli.digitalschool.core.utility

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.PopupWindow
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.SearchView
import com.rangoli.digitalschool.R


class CustomSearchSpinner @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = androidx.appcompat.R.attr.spinnerStyle
) : AppCompatSpinner(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "CustomSearchSpinner"
    }

    private var popupWindow: PopupWindow? = null
    private var listView: ListView? = null
    private var searchView: SearchView? = null
    private var adapter: ArrayAdapter<SpinnerModel>? = null
    private var customAdapter: CustomSpinnerAdapter<SpinnerModel>? = null
    private var items: List<SpinnerModel> = emptyList()

    init {
        initSpinner()
        setSpinnerBorder()
    }

    private fun setSpinnerBorder() {
        // Create beautiful curved border drawable
        val borderDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(Color.WHITE)
            setStroke(2, Color.parseColor("#CCCCCC"))
            cornerRadius = 16f  // More rounded corners
        }
        background = borderDrawable

        // Set padding
        setPadding(16, 12, 16, 12)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initSpinner() {
        setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                post {
                    createPopupWindow()
                    popupWindow?.showAsDropDown(this)
                }
            }
            true
        }
    }

    fun setAdapter(adapter: ArrayAdapter<SpinnerModel>?) {
        this.adapter = adapter
        if (adapter is CustomSpinnerAdapter<*>) {
            @Suppress("UNCHECKED_CAST")
            customAdapter = adapter as CustomSpinnerAdapter<SpinnerModel>
            items = customAdapter?.getItems() ?: emptyList()
        }
        super.setAdapter(adapter)
    }

    private fun createPopupWindow() {
        val popupView = View.inflate(context, R.layout.spinner_popup, null)

        searchView = popupView.findViewById(R.id.spinner_search_view)

        // Style SearchView
        val searchEditId = searchView?.context?.resources
            ?.getIdentifier("android:id/search_src_text", null, null)
        val searchEditText = searchEditId?.let { searchView?.findViewById<android.widget.EditText>(it) }

        searchView?.post {
            val searchEditText =
                searchView?.findViewById<android.widget.EditText>(
                    androidx.appcompat.R.id.search_src_text
                )

            searchEditText?.apply {
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                setTextColor(Color.BLACK)
                setHintTextColor(Color.parseColor("#999999"))
            }
        }




        listView = popupView.findViewById(R.id.spinner_list_view)

        if (customAdapter == null) {
            customAdapter = CustomSpinnerAdapter(context, items)
        }

        listView?.adapter = customAdapter

        // Popup height
        val popupHeight = resources.getDimensionPixelSize(R.dimen.popup_window_height)

        // Set popup width = spinner width
        val popupWidth = this.width
        Log.d(TAG, "Popup width = $popupWidth")

        popupWindow = PopupWindow(
            popupView,
            popupWidth,
            popupHeight,
            true
        ).apply {
            isOutsideTouchable = true
            elevation = 12f
            setBackgroundDrawable(android.graphics.drawable.ColorDrawable(Color.WHITE))
        }

        // Style search plate
        val plateId = searchView?.context?.resources
            ?.getIdentifier("android:id/search_plate", null, null)
        val plate = plateId?.let { searchView?.findViewById<View>(it) }
        plate?.apply {
            setBackgroundColor(Color.WHITE)
            setPadding(8, 4, 8, 4)
        }

        // Search filter logic
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false

            override fun onQueryTextChange(query: String?): Boolean {
                filterList(query ?: "")
                return true
            }
        })

        listView?.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = customAdapter?.getItem(position)
            val originalIndex = items.indexOf(selectedItem)

            if (originalIndex >= 0) setSelection(originalIndex)

            popupWindow?.dismiss()
            searchView?.setQuery("", false)
        }
    }

    private fun filterList(query: String) {
        val filtered = items.filter {
            it.name.contains(query, ignoreCase = true)
        }
        customAdapter?.updateItems(filtered)
    }
}
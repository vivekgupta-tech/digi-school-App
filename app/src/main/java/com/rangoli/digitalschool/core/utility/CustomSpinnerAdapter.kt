package com.rangoli.digitalschool.core.utility

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.rangoli.digitalschool.R

class CustomSpinnerAdapter<T : SpinnerModel>(
    context: Context,
    private var items: List<T>
) : ArrayAdapter<T>(context, 0, items) {

    private val TAG = "CustomSpinnerAdapter"

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.project_spinner_item_layout2, parent, false)

        val item = getItem(position)
        val text = view.findViewById<TextView>(R.id.projectNameTextview)
        item?.let {
            Log.d(TAG, "getView: ${it.name}")
            text.text = it.name
        }
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.project_spinner_item_layout2, parent, false)

        val item = getItem(position)
        val text = view.findViewById<TextView>(R.id.projectNameTextview)
        item?.let {
            Log.d(TAG, "getDropDownView: ${it.name}")
            text.text = it.name
        }
        return view
    }

    fun getItems(): List<T> = items

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): T = items[position]

    fun updateItems(filteredItems: List<T>) {
        items = filteredItems
        notifyDataSetChanged()
    }
}

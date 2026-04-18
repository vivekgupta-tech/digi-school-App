package com.rangoli.digitalschool.core.utility

import android.os.Parcel
import android.os.Parcelable

open class SpinnerModel(
    open val id: String,
    open val name: String
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<SpinnerModel> {
        override fun createFromParcel(parcel: Parcel): SpinnerModel {
            return SpinnerModel(parcel)
        }

        override fun newArray(size: Int): Array<SpinnerModel?> {
            return arrayOfNulls(size)
        }
    }
}
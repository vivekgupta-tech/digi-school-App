package com.rangoli.digitalschool.core.utility

import com.android.volley.AuthFailureError
import com.android.volley.NetworkError
import com.android.volley.NoConnectionError
import com.android.volley.ParseError
import com.android.volley.ServerError
import com.android.volley.TimeoutError
import com.android.volley.VolleyError


class MyException(message: String, cause: Throwable? = null, val errorCode: Int? = null) :
    Exception(message, cause) {

    companion object {

        fun fromVolleyError(error: VolleyError): MyException {
            return when (error) {
                is TimeoutError -> {
                    MyException("Request timed out. Please try again later.", error, 408)
                }
                is NoConnectionError -> {
                    MyException("No internet connection. Please check your network and try again.", error, 503)
                }

                is ParseError -> {
                    MyException("Failed to parse the response. Please try again later.", error, 400)
                }

                is NetworkError -> {
                    MyException("Ntwork error occurred. Please check your connection.", error, 503)
                }

                is ServerError -> {
                    val statusCode = error.networkResponse?.statusCode ?: 500
                    MyException(
                        "Server error occurred (Error code: $statusCode). Please try again later.",
                        error,
                        statusCode
                    )
                }

                is AuthFailureError -> {
                    MyException("Authentication failed. Please check your credentials.", error, 401)
                }

                else -> {
                    MyException(error.message ?: "An unexpected error occurred", error, 500)
                }
            }
        }

        fun fromJsonParseError(error: Throwable): MyException {
            return MyException("Failed to parse response", error, 400)
        }
    }
}
package com.rangoli.digitalschool.core.network

import android.content.Context
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class VolleyRequestManager(context: Context) {

    private val queue: RequestQueue =
        Volley.newRequestQueue(context.applicationContext)

    companion object {
        // Initial timeout per attempt (ms)
        private const val TIMEOUT_MS = 10_000

        // Total retry attempts after the first try
        private const val MAX_RETRIES = 5

        // Volley multiplies timeout by this after every retry (1f = no increase)
        private const val BACKOFF_MULTIPLIER = 1f
    }

    // ─────────────────────────────────────────
    // Retry policy helper — attach to every request
    // ─────────────────────────────────────────
    private fun retryPolicy() = DefaultRetryPolicy(
        TIMEOUT_MS,
        MAX_RETRIES,
        BACKOFF_MULTIPLIER
    )

    fun makeStringRequest(
        method: Int,
        url: String,
        params: HashMap<String, String> = HashMap(),
        success: (String) -> Unit,
        error: (VolleyError) -> Unit,
        tag: String? = ""
    ) {
        val request = object : StringRequest(
            method,
            url,
            { success(it) },
            { error(it) }
        ) {
            override fun getParams(): Map<String, String> = params
        }

        request.retryPolicy = retryPolicy()   // ✅ auto-retry on timeout
        request.tag = tag
        queue.add(request)
    }

    fun makeJsonObjectRequest(
        method: Int,
        url: String,
        jsonBody: JSONObject,
        success: (JSONObject) -> Unit,
        error: (VolleyError) -> Unit,
        tag: String
    ) {
        val request = object : JsonObjectRequest(
            method,
            url,
            jsonBody,
            { success(it) },
            { error(it) }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return hashMapOf(
                    "Content-Type" to "application/json",
                    "Accept"       to "application/json"
                )
            }
        }

        request.retryPolicy = retryPolicy()   // ✅ auto-retry on timeout
        request.tag = tag
        queue.add(request)
    }

    fun cancelRequests(tag: String) {
        queue.cancelAll(tag)
    }
}
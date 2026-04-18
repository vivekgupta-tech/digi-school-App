package com.rangoli.digitalschool.core.network

import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException
import java.nio.charset.Charset
import java.util.*

abstract class VolleyMultipartRequest(
    method: Int,
    url: String,
    private val listener: Response.Listener<NetworkResponse>,
    errorListener: Response.ErrorListener
) : Request<NetworkResponse>(method, url, errorListener) {

    private val boundary = "apiclient-${System.currentTimeMillis()}"
    private val mimeType = "multipart/form-data;boundary=$boundary"
    private val charset = "UTF-8"

    override fun getBodyContentType(): String = mimeType

    override fun getBody(): ByteArray {
        val bos = ByteArrayOutputStream()
        val dos = DataOutputStream(bos)

        try {
            // ---------- Text params ----------
            val params = params
            if (params != null && params.isNotEmpty()) {
                for ((key, value) in params) {
                    buildTextPart(dos, key, value)
                }
            }

            // ---------- File params ----------
            val data = getByteData()
            for ((key, dataPart) in data) {
                buildFilePart(dos, dataPart, key)
            }

            // ---------- End boundary ----------
            dos.writeBytes("--$boundary--\r\n")
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return bos.toByteArray()
    }

    override fun parseNetworkResponse(response: NetworkResponse): Response<NetworkResponse> {
        return Response.success(response, HttpHeaderParser.parseCacheHeaders(response))
    }

    override fun deliverResponse(response: NetworkResponse) {
        listener.onResponse(response)
    }

    protected abstract fun getByteData(): Map<String, DataPart>

    data class DataPart(
        val fileName: String,
        val content: ByteArray,
        val type: String
    )

    @Throws(IOException::class)
    private fun buildTextPart(
        dos: DataOutputStream,
        parameterName: String,
        parameterValue: String
    ) {
        dos.writeBytes("--$boundary\r\n")
        dos.writeBytes("Content-Disposition: form-data; name=\"$parameterName\"\r\n")
        dos.writeBytes("Content-Type: text/plain; charset=$charset\r\n\r\n")
        dos.writeBytes(parameterValue)
        dos.writeBytes("\r\n")
    }

    @Throws(IOException::class)
    private fun buildFilePart(
        dos: DataOutputStream,
        dataFile: DataPart,
        inputName: String
    ) {
        dos.writeBytes("--$boundary\r\n")
        dos.writeBytes(
            "Content-Disposition: form-data; name=\"$inputName\"; filename=\"${dataFile.fileName}\"\r\n"
        )
        dos.writeBytes("Content-Type: ${dataFile.type}\r\n\r\n")
        dos.write(dataFile.content)
        dos.writeBytes("\r\n")
    }
}

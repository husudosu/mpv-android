package `is`.xyz.streaming

import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class APIService {
    val client = OkHttpClient()
    val mediaType = "application/json; charset=utf-8".toMediaType()

    fun postJsonRequestAsync(
        url: String,
        jsonRequestPayload: JSONObject,
        onSuccess: (JSONObject) -> Unit,
        onFailure: (IOException) -> Unit
    ) {


        // Add default headers

        val requestBody = jsonRequestPayload.toString().toRequestBody(mediaType)
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .header("Accept", "application/json")
            .build()

        // Use .enqueue() instead of .execute() for async execution
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Trigger the failure callback passed into the function
                onFailure(e)
            }

            override fun onResponse(call: Call, response: Response) {
                // Use .use to ensure the response body is automatically closed
                response.use {
                    if (!response.isSuccessful) {
                        onFailure(IOException("Unexpected code $response"))
                        return
                    }

                    val responseBodyString = response.body?.string()
                    if (responseBodyString != null) {
                        try {
                            val jsonResponse = JSONObject(responseBodyString)
                            onSuccess(jsonResponse) // Trigger success callback
                        } catch (e: Exception) {
                            onFailure(IOException("Failed to parse JSON response", e))
                        }
                    } else {
                        onFailure(IOException("Response body was empty"))
                    }
                }
            }
        })
    }
}
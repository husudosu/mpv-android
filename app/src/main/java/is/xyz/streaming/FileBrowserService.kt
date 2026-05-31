package `is`.xyz.streaming

import android.util.Log
import `is`.xyz.mpv.FileBrowserEntryDto
import `is`.xyz.mpv.FileBrowserResultsDto
import okio.IOException
import org.json.JSONArray
import org.json.JSONObject

class FileBrowserService {

    val apiService: APIService = APIService()

    private fun mapFileBrowserEntries(entriesObj: JSONArray): List<FileBrowserEntryDto> {
        val resp: ArrayList<FileBrowserEntryDto> = ArrayList()
        for (i in 0 until entriesObj.length()) {
            val obj = entriesObj.getJSONObject(i)
            resp.add(
                FileBrowserEntryDto(
                    obj.getString("name"),
                    obj.getBoolean("is_dir"),
                    obj.getString("file_type"),
                    obj.getString("extension"),
                    obj.getString("created_at"),
                    obj.getString("modified_at"),
                    obj.getString("file_path")
                )
            )
        }
        return resp
    }

    fun getFileList(
        path: String,
        onSuccess: (FileBrowserResultsDto) -> Unit,
        onFailure: (IOException) -> Unit
    ) {
        val requestObj = JSONObject()
        requestObj.put("directory", path)
        Log.i("API", "Getting file list")

        // FIXME: Get URL from config.
        apiService.postJsonRequestAsync(
            "http://192.168.88.6:8000/filebrowser/list",
            requestObj,
            { responseObj ->
                Log.i("API", "API request success, mapping")
                val resultObj = FileBrowserResultsDto(
                    responseObj.getString("name"),
                    responseObj.getString("full_path"),
                    responseObj.getString("parent"),
                    mapFileBrowserEntries(responseObj.getJSONArray("entries"))
                )
                Log.i("API", "Mapping done callback")
                onSuccess(resultObj)
            },
            onFailure =  { errorObj ->
                Log.e("API","Failure during API request:")
                Log.e("API", errorObj.message.toString())
                onFailure(errorObj)
            }
        )
    }
}
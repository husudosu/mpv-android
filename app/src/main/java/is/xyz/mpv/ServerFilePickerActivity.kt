package `is`.xyz.mpv

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.net.Uri
import `is`.xyz.streaming.APIMediaPlaybackService
import `is`.xyz.streaming.FileBrowserService
import `is`.xyz.streaming.Settings


class ServerFilePickerActivity : AppCompatActivity() {

    val fileBrowserService: FileBrowserService = FileBrowserService()

    var currentResponseFromAPI: FileBrowserResultsDto? = null

    var hasParent: Boolean = false

    val apiMediaPlaybackService : APIMediaPlaybackService = APIMediaPlaybackService()



    private fun getFileListSuccessCallback(response: FileBrowserResultsDto, itemList: ArrayList<String>) {
        val listView = findViewById<ListView>(R.id.entries_list_view)
        currentResponseFromAPI = response
        itemList.clear()
        if (response.parent != null) {
            Log.d("API", "Parent detected")
            hasParent = true
        } else hasParent = false
        itemList.add("...")
        for (i in 0 until response.entries.size) {
            itemList.add(response.entries[i].name)
        }
        runOnUiThread {
            val adapter = ArrayAdapter(
                this@ServerFilePickerActivity,
                android.R.layout.simple_list_item_1,
                itemList
            )
            listView.adapter = adapter
        }
    }

    private fun listViewOnClick(itemList: ArrayList<String>, parent: AdapterView<*>, view: View, position: Int, id: Long) {
        // We consider Parent
        val apiPosition = if (hasParent) position - 1  else position

        // If the user is clicking on ... aka. Parent then we navigate accordingly
        val isParentSelected = hasParent && position == 0

        val isDir = if (isParentSelected) true else currentResponseFromAPI?.entries[apiPosition]?.isDir
        val filePath = if (isParentSelected) currentResponseFromAPI?.parent else currentResponseFromAPI?.entries[apiPosition]?.filePath

        if (isDir == true && filePath != null) {
            Log.v("API", "We have directory so navigate")
            fileBrowserService.getFileList(
                filePath,
                {
                        response -> getFileListSuccessCallback(response, itemList)
                },
                {
                        err -> Log.e("API", err.message.toString())
                }
            )
        } else if (filePath != null) {
            val mediaStreamUrl: Uri = apiMediaPlaybackService.generateMediaUrl("192.168.88.6" , 8080, filePath);
            Log.i("API", "We generate URL for filePath: $filePath ${mediaStreamUrl.toString()}")
            val result = Intent()
            result.putExtra("media_url", mediaStreamUrl.toString() )
            setResult(RESULT_OK, result)
            finish()
        }
        else {
            Toast.makeText(
                this@ServerFilePickerActivity,
                "The entry is file or filePath isn null $isDir $filePath",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //Log.d("ServerFilePicker" ,"Settings of server: ${settings.mediaServerHost}:${settings.mediaServerPort}")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filepicker_server)
        val listView = findViewById<ListView>(R.id.entries_list_view)
        val itemList = ArrayList<String>()

        listView.setOnItemClickListener { parent, view, position, id -> listViewOnClick(itemList, parent, view, position, id)
        }

        fileBrowserService.getFileList(
            "/mnt/s5",
            {response -> getFileListSuccessCallback(response, itemList) },
            {
                err -> print(err)
            }
        )
    }

    override fun onCreateView(
        parent: View?,
        name: String,
        context: Context,
        attrs: AttributeSet
    ): View? {
        return super.onCreateView(parent, name, context, attrs)
    }

}

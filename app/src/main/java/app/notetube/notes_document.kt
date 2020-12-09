package app.notetube

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import app.notetube.adapters.NoteListAdapter
import app.notetube.models.DocumentListItem
import app.notetube.models.api.Document
import app.notetube.models.api.Note
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.lang.reflect.Type

class notes_document : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes_document)

        // Activity variables
        var currDocument : Document? = null

        // View elements
        val noteListView : ListView = findViewById(R.id.notes_list_view) as ListView

        // Run thread to load initial listview
        val thread = Thread(Runnable {
            try
            {
                currDocument = RequestDocumentById(1)
                // Load listview
                if (currDocument?.notes != null)
                {
                    val adapter = NoteListAdapter(this, currDocument!!.notes)
                    runOnUiThread({
                        noteListView.adapter = NoteListAdapter(this, currDocument!!.notes)
                    })
                }
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }
        })
        thread.start()

        val testBtn : Button = findViewById(R.id.testDialog)

        var documentItem : DocumentListItem = intent.getSerializableExtra(
            "VIDEO_DOCUMENT") as DocumentListItem

        val youtubePlayerView : YouTubePlayerView = findViewById(
            R.id.youtube_player_view) as YouTubePlayerView
        getLifecycle().addObserver(youtubePlayerView)

        youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youtubePlayer: YouTubePlayer) {
                youtubePlayer.loadVideo(documentItem.videoId, 0f)
                youtubePlayer.pause()
            }
        })


        // Load notes
        /*
        val noteListView : ListView = findViewById(R.id.notes_list_view) as ListView
        val adapter = NoteListAdapter(this, strArr)
        noteListView.adapter = adapter
        */

        noteListView.setOnItemClickListener { parent, view, position, id ->

        }



        testBtn.setOnClickListener() {
            var dialog = NoteCardEditDialog()
            val args : Bundle = Bundle()
            args.putSerializable("NOTE", null)
            dialog.arguments = args
            dialog.show(supportFragmentManager, "Edit Note Dialog")

            /*val tranaction = supportFragmentManager.beginTransaction()
            tranaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            tranaction.add(android.R.id.content, dialog)
                .addToBackStack(null)
                .commit()*/
        }

    }

    fun RequestDocumentById(documentId: Int) : Document? {
        // Create URI
        val url : String = getString(R.string.primary_url)
        val uri : String = url + "/document/" + documentId
        println(uri)
        // Request service
        val client : OkHttpClient = OkHttpClient()
        val request : Request = Request.Builder()
            .url(uri)
            .addHeader(
            "authorization",
            "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJub3RldHViZSIsImV4cCI6MTYwODcwMjE0MCwidXNlcklkIjoxfQ.X3ZLeXdNJYMRCN8eTQrJcvD7wtZW-ggmbtf6OP4qLoM"
            )
            .build()
        // Handle response
        var document : Document? = null
        val response : Response = client.newCall(request).execute()
        if (response.isSuccessful)
        {
            val jsonObj : JSONObject = JSONObject(response.body?.string())

            // Get note list
            val listType: Type = object : TypeToken<List<Note>>() {}.type
            val noteList : ArrayList<Note> = Gson().fromJson(
                jsonObj.getJSONArray("notes").toString(),
                listType
            )

            document = Document(
                jsonObj.getInt("id"),
                jsonObj.getString("documentName"),
                jsonObj.getString("youtubeVideoId"),
                jsonObj.getLong("createdDate"),
                noteList
            )
        }
        else
        {
            val jsonObj : JSONObject = JSONObject(response.body?.string())
            val message : String = jsonObj.getString("message")
        }
        return document
    }

}
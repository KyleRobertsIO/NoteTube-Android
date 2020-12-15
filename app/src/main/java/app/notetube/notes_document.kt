package app.notetube

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import app.notetube.adapters.NoteListAdapter
import app.notetube.models.DocumentListItem
import app.notetube.models.api.Document
import app.notetube.models.api.Note
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.android.synthetic.main.activity_notes_document.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.lang.reflect.Type

class notes_document : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes_document)

        // Load intent content
        val documentItem : Document = intent.getSerializableExtra(
            "VIDEO_DOCUMENT") as Document

        // Activity variables
        var currDocument : Document? = documentItem

        // View elements
        val noteListView : ListView = findViewById<ListView>(R.id.notes_list_view)
        val newNoteFab = findViewById<FloatingActionButton>(R.id.addFab)

        // Run thread to load initial listview
        val thread = Thread(Runnable
        {
            try
            {
                currDocument = RequestDocumentById(currDocument!!.id)
                // Load listview
                if (currDocument?.notes != null)
                {
                    val adapter = NoteListAdapter(this, currDocument!!.notes)
                    runOnUiThread {
                        noteListView.adapter = NoteListAdapter(this, currDocument!!.notes)
                    }
                }
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }
        })
        thread.start()

        // Load Youtube video player
        val youtubePlayerView : YouTubePlayerView = findViewById<YouTubePlayerView>(
            R.id.youtube_player_view)
        lifecycle.addObserver(youtubePlayerView)

        // Youtube player listener
        youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youtubePlayer: YouTubePlayer) {
                youtubePlayer.loadVideo(currDocument!!.youtubeVideoId, 0f)
                youtubePlayer.pause()
            }
        })

        // Handle click of note card
        noteListView.setOnItemClickListener { parent, view, position, id ->
            OpenEditNoteDialog(
                currDocument!!.notes!!.get(position),
                currDocument!!.id
            )
        }

        addFab.setOnClickListener {
            OpenNewNoteDialog(currDocument!!.id)
        }
    }
  
    companion object { // Permissions
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.INTERNET)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun OpenEditNoteDialog(selectNote: Note, documentId: Int)
    {
        var dialog = NoteCardEditDialog()
        val args : Bundle = Bundle()
        args.putSerializable("DOCUMENT_ID", documentId)
        args.putSerializable("NOTE", selectNote)
        dialog.arguments = args
        dialog.show(supportFragmentManager, "Edit Note Dialog")
    }

    private fun OpenNewNoteDialog(documentId: Int)
    {
        var dialog = NoteCardNewDialog()
        val args : Bundle = Bundle()
        args.putSerializable("DOCUMENT_ID", documentId)
        dialog.arguments = args
        dialog.show(supportFragmentManager, "New Note Dialog")
    }

    private fun RequestDocumentById(documentId: Int) : Document?
    {
        val sharedPreference = SharedPreference(this)

        // Create URI
        val url : String = getString(R.string.primary_url)
        val uri : String = url + "/document/" + documentId
        // Request service
        val client : OkHttpClient = OkHttpClient()
        val request : Request = Request.Builder()
            .url(uri)
            .addHeader(
            "authorization",
            "Bearer ${sharedPreference.getValueString("JWT")}"
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
package app.notetube

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import app.notetube.adapters.NoteListAdapter
import app.notetube.models.DocumentListItem
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView


class notes_document : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes_document)

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
        val strArr: ArrayList<String> = ArrayList<String>()
        strArr.add("Example 1")
        strArr.add("Example 2")

        val noteListView : ListView = findViewById(R.id.notes_list_view) as ListView
        val adapter = NoteListAdapter(this, strArr)
        noteListView.adapter = adapter

    }
}
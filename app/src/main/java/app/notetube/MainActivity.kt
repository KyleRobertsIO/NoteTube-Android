package app.notetube

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.ListView
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import app.notetube.adapters.DocumentListAdapter
import app.notetube.enums.DocumentType
import app.notetube.models.DocumentListItem

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Screen elements
        val documentListView : ListView = findViewById(R.id.documentListView) as ListView

        // Toolbar setup
        val mToolbar : Toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);

        // Load list of folders and documents
        val documents = ArrayList<DocumentListItem>();
        documents.add(
            DocumentListItem(
                1,
                "Intro To NodeJS",
                DocumentType.NOTE_DOCUMENT,
                "pU9Q6oiQNd0"
            )
        )
        documents.add(
            DocumentListItem(
                2,
                "Flutter Basics",
                DocumentType.NOTE_DOCUMENT,
                "ZlUDnC_9wmg"
            )
        )

        // Append data to listview
        val adapter = DocumentListAdapter(this, documents)
        documentListView.adapter = adapter

        // Click listview item
        documentListView.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(this, notes_document::class.java)
            intent.putExtra("VIDEO_DOCUMENT", documents.get(position))
            startActivity(intent)
        }

    }

    // Implement the appbar toolbar into the activity
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
}
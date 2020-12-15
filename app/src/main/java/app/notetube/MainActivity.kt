package app.notetube

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.ListView
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import app.notetube.adapters.DocumentListAdapter
import app.notetube.adapters.NoteListAdapter
import app.notetube.enums.DocumentType
import app.notetube.models.DocumentListItem
import app.notetube.models.api.Document
import app.notetube.models.api.Note
import app.notetube.models.api.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.lang.reflect.Type

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreference = SharedPreference(this)

        if (sharedPreference.getValueBool("isLoggedIn") == false) {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        // Activity variables
        var currUser : User? = null

        // Screen elements
        val documentListView : ListView = findViewById(R.id.documentListView) as ListView

        // Toolbar setup
        val mToolbar : Toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);

        // Load user data thread
        val thread = Thread(Runnable {
            try
            {
                currUser = RequestUserData()
                // Load listview
                if (currUser?.documents != null)
                {
                    val adapter = DocumentListAdapter(this, currUser!!.documents)
                    runOnUiThread {
                        documentListView.adapter = DocumentListAdapter(
                            this,
                            currUser!!.documents
                        )
                    }
                }
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }
        })
        thread.start()

        // Click listview item
        documentListView.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(this, notes_document::class.java)
            intent.putExtra("VIDEO_DOCUMENT", currUser?.documents?.get(position))
            startActivity(intent)
        }

    }

    override fun onBackPressed() {
        //Dont go back
    }

    // Implement the appbar toolbar into the activity
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    fun RequestUserData() : User?
    {
        // Create URI
        val url : String = getString(R.string.primary_url)
        val uri : String = url + "/user"
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
        var user : User? = null
        val response : Response = client.newCall(request).execute()
        if (response.isSuccessful)
        {
            val jsonObj : JSONObject = JSONObject(response.body?.string())

            // Get document list
            val listType: Type = object : TypeToken<List<Document>>() {}.type
            val documentList : ArrayList<Document> = Gson().fromJson(
                jsonObj.getJSONArray("documents").toString(),
                listType
            )
            user = User(
                jsonObj.getInt("id"),
                jsonObj.getString("email"),
                documentList
            )
        }
        else
        {
            val jsonObj : JSONObject = JSONObject(response.body?.string())
            val message : String = jsonObj.getString("message")
        }
        return user
    }

}
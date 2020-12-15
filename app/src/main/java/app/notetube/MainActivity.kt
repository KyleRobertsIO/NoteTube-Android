package app.notetube

import android.R.id
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import app.notetube.adapters.DocumentListAdapter
import app.notetube.models.api.Document
import app.notetube.models.api.User
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
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
        val documentListView : ListView = findViewById<ListView>(R.id.documentListView)
        val logoutButton = findViewById<ExtendedFloatingActionButton>(R.id.logoutFab)

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
            var selectedDocument : Document = parent.adapter.getItem(position) as Document
            //intent.putExtra("VIDEO_DOCUMENT", currUser?.documents?.get(position))
            intent.putExtra("VIDEO_DOCUMENT", selectedDocument)
            startActivity(intent)
        }

        logoutButton.setOnClickListener {
            sharedPreference.clearSharedPreference()
            val intent = Intent(this@MainActivity, MainActivity::class.java)
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

        val search : MenuItem? = menu?.findItem(R.id.app_bar_search)
        val searchView : SearchView = search?.actionView as SearchView
        searchView.queryHint = "Find document"

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                var adapter : DocumentListAdapter = documentListView.adapter as DocumentListAdapter
                if (newText != null) {
                    adapter.filter(newText)
                }
                return true
            }
        })

        return true
    }

    fun RequestUserData() : User?
    {
        val sharedPreference = SharedPreference(this)

        // Create URI
        val url : String = getString(R.string.primary_url)
        val uri : String = "$url/user"
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
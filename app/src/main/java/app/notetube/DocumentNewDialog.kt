package app.notetube

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import app.notetube.models.api.Document
import app.notetube.models.api.Note
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import kotlinx.android.synthetic.main.document_card_new_fragment.view.*
import kotlinx.android.synthetic.main.note_card_edit_fragment.view.*
import kotlinx.android.synthetic.main.note_card_edit_fragment.view.cancelDialogBtn
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.collections.ArrayList

class DocumentNewDialog : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var rootView: View = inflater.inflate(
            R.layout.document_card_new_fragment,
            container,
            false
        )

        //*******************************************
        //    Get passed arguments
        //*******************************************
        val documentArray: ArrayList<Document> = arguments?.get("DOCUMENT_ARRAY") as ArrayList<Document>

        val docTitleField = rootView.findViewById<TextInputLayout>(R.id.docTitleField)
        val linkField = rootView.findViewById<TextInputLayout>(R.id.linkField)
        val ytRegex = Regex(pattern = "http(?:s?)://(?:www\\.)?youtu(?:be\\.com/watch\\?v=|\\.be/)([\\w\\-_]*)(&(amp;)?\u200C\u200B[\\w?\u200C\u200B=]*)?")

        //*******************************************
        // Handle dialog actions
        //*******************************************
        rootView.saveDocumentBtn.setOnClickListener()
        {
            if (docTitleField.editText?.text.toString().isNotEmpty()
                && linkField.editText?.text.toString().isNotEmpty()
                && linkField.editText?.text.toString().matches(ytRegex)) {
                // Start thread for HTTP request
                val thread = Thread(Runnable {
                    var document = Document()

                    try {
                        val idRegex = Regex(pattern = "^.*((youtu.be\\/)|(v\\/)|(\\/u\\/\\w\\/)|(embed\\/)|(watch\\?))\\??v?=?([^#\\&\\?]*).*")
                        val video = linkField.editText?.text.toString()
                        val videoId = idRegex.find(video)?.groups?.get(7)
                        if (videoId != null) {
                            document = Document(0, docTitleField.editText?.text.toString(), videoId.value, 0, ArrayList<Note>())
                            requestNewDocument(document)

                            activity?.runOnUiThread {
                                documentArray.add(document)
                            }
                        }
                        dismiss()
                    } catch (e: Exception) { e.printStackTrace() }
                })
                thread.start()
            }else{
                if (docTitleField.editText?.text.toString().isEmpty()
                    && linkField.editText?.text.toString().isEmpty()) {
                    Toast.makeText(
                        context,
                        getString(R.string.toast_editing_note_unchanged),
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (!linkField.editText?.text.toString().matches(ytRegex)) {
                    Toast.makeText(
                        context,
                        getString(R.string.valid_link),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        //*******************************************
        //  Cancel button for current editing note
        //*******************************************
        rootView.cancelDialogBtn.setOnClickListener()
        {
            dismiss()
        }

        return rootView
    }

    override fun onStart() {
        super.onStart()
        val dialog: Dialog? = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            dialog.window?.setLayout(width, height)
        }
    }


    //*******************************************
    // Handling API Requests
    //*******************************************
    private fun requestNewDocument(document: Document) {
        val sharedPreference = SharedPreference(activity as Activity)

        // Create URI
        val url : String = getString(R.string.primary_url)
        val uri = "$url/document"

        // Create POST body
        val requestBody = Gson()
            .toJson(document)
            .toString()
            .toRequestBody()

        // Request service
        val client = OkHttpClient()
        val request : Request = Request.Builder()
            .method("POST", requestBody)
            .url(uri)
            .addHeader(
                "authorization",
                "Bearer ${sharedPreference.getValueString("JWT")}"
            )
            .addHeader("content-type", "application/json")
            .build()

        // Handle HTTP response
        val response : Response = client.newCall(request).execute()
        if (response.isSuccessful)
        {
            val jsonObj : JSONObject = JSONObject(response.body?.string())
            Log.d("JWT", jsonObj.toString())
        }
        else
        {
            val jsonObj : JSONObject = JSONObject(response.body?.string())
            Toast.makeText(
                context,
                getString(R.string.toast_connection_error),
                Toast.LENGTH_SHORT
            ).show()

            Log.d("JWT", jsonObj.toString())
        }
    }
}

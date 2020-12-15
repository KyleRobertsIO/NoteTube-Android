package app.notetube

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
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
import app.notetube.models.api.Note
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import kotlinx.android.synthetic.main.note_card_edit_fragment.view.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.util.*


class NoteCardEditDialog : DialogFragment() {

    private lateinit var noteBodyField: TextInputLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var rootView: View = inflater.inflate(
            R.layout.note_card_edit_fragment,
            container,
            false
        )

        //*******************************************
        //    Get passed arguments
        //*******************************************
        val currEditingNote : Note = arguments?.getSerializable("NOTE") as Note
        val documentId : Int = arguments?.getInt("DOCUMENT_ID") as Int

        noteBodyField = rootView.findViewById<TextInputLayout>(R.id.noteBodyField)

        val noteTitleEditText : EditText = rootView.findViewById(R.id.noteTitleEditText)
        val noteBodyEditText : EditText = rootView.findViewById(R.id.noteBodyEditText)

        noteTitleEditText.setText(currEditingNote.title)
        noteBodyEditText.setText(currEditingNote.body)

        noteBodyField.setEndIconOnClickListener {
          if (allPermissionsGranted()) {
              speak()
          } else {
              ActivityCompat.requestPermissions(
                 activity as Activity, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
              )
          }
       }

        //*******************************************
        // Handle dialog actions
        //*******************************************
        rootView.saveNoteBtn.setOnClickListener()
        {
            // Confirm note is updated
            val tempUpdatedNote : Note = Note()
            tempUpdatedNote.copy(currEditingNote)

            tempUpdatedNote.title = noteTitleEditText.text.toString()
            tempUpdatedNote.body = noteBodyEditText.text.toString()

            if (!currEditingNote.equalsEdit(tempUpdatedNote)) {
                // Start thread for HTTP request
                val thread = Thread(Runnable {
                    try {
                        val updatedNote : Note? = RequestNoteUpdate(documentId, tempUpdatedNote)
                        if(updatedNote != null) { currEditingNote.copy(updatedNote) }
                        dismiss()
                    } catch (e: Exception) { e.printStackTrace() }
                })
                thread.start()
            }else{
                Toast.makeText(
                    context,
                    getString(R.string.toast_editing_note_unchanged),
                    Toast.LENGTH_SHORT
                ).show()
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
    // Android Speech API section
    //*******************************************
    companion object { // Permissions
        private const val REQUEST_CODE_SPEECH_INPUT = 10
        private const val REQUEST_CODE_PERMISSIONS = 11
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.INTERNET)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
            activity?.let { it1 ->
                ContextCompat.checkSelfPermission(
                    it1.baseContext, it)
            } == PackageManager.PERMISSION_GRANTED
    }

    private fun speak() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak!")

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch(e: Exception) {
            Toast.makeText(activity, "Error!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            REQUEST_CODE_SPEECH_INPUT -> {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    noteBodyField.editText?.setText(result?.get(0))
                }
            }
        }
    }


    //*******************************************
    // Handling API Requests
    //*******************************************
    private fun RequestNoteUpdate(documentId: Int, note: Note) : Note? {
        val sharedPreference = SharedPreference(activity as Activity)

        // Create URI
        val url : String = getString(R.string.primary_url)
        val uri : String = "$url/document/$documentId/note"

        // Create POST body
        val requestBody = Gson()
            .toJson(note)
            .toString()
            .toRequestBody()

        // Request service
        val client = OkHttpClient()
        val request : Request = Request.Builder()
            .method("PUT", requestBody)
            .url(uri)
            .addHeader(
                "authorization",
                "Bearer ${sharedPreference.getValueString("JWT")}"
            )
            .addHeader("content-type", "application/json")
            .build()

        // Handle HTTP response
        val response : Response = client.newCall(request).execute()
        var updatedNote : Note? = null
        if (response.isSuccessful)
        {
            val jsonObj : JSONObject = JSONObject(response.body?.string())
            updatedNote = Gson().fromJson(jsonObj.toString(), Note::class.java)

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
        return updatedNote
    }
}
package app.notetube

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.note_card_edit_fragment.view.*
import com.google.android.material.textfield.TextInputLayout
import java.lang.Exception
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


        val note = arguments?.getSerializable("NOTE")
        noteBodyField = rootView.findViewById<TextInputLayout>(R.id.noteBodyField)
          
       noteBodyField.setEndIconOnClickListener {
          if (allPermissionsGranted()) {
              speak()
          } else {
              ActivityCompat.requestPermissions(
                 activity as Activity, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
              )
          }
       }
          
        // Handle dialog actions
        rootView.saveNoteBtn.setOnClickListener()
        {

        }

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

    /**
     * A bunch of speech code under here
     */

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
}
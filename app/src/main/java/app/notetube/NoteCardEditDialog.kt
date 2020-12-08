package app.notetube

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.note_card_edit_fragment.view.*

class NoteCardEditDialog : DialogFragment() {

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
            dialog.getWindow()?.setLayout(width, height)
        }
    }

}
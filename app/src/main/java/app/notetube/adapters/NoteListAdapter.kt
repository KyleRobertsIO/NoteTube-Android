package app.notetube.adapters

import android.content.Context
import android.view.LayoutInflater
import android.widget.BaseAdapter
import android.widget.EditText
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import app.notetube.MainActivity
import app.notetube.R
import app.notetube.models.api.Note
import app.notetube.notes_document

class NoteListAdapter(
    private var activity: notes_document,
    private var notes: ArrayList<Note>
): BaseAdapter() {

    private class ViewHolder(row: View?) {

        var noteTitle: TextView? = null
        var noteBody: TextView? = null

        init {
            this.noteTitle = row?.findViewById(R.id.noteTitleTextView)
            this.noteBody = row?.findViewById(R.id.noteBodyTextView)
        }
    }

    override fun getCount(): Int {
        return notes.size
    }

    override fun getItem(position: Int): Any {
        return notes[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view: View
        val viewHolder: NoteListAdapter.ViewHolder

        if(convertView == null) {
            val inflater = activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.document_note_card, null)
            viewHolder = NoteListAdapter.ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = NoteListAdapter.ViewHolder(view) as NoteListAdapter.ViewHolder
        }

        var noteTitle: String = notes[position].title
        var noteBody: String = notes[position].body

        viewHolder.noteTitle?.setText(noteTitle)
        viewHolder.noteBody?.setText(noteBody)

        return view as View
    }

}

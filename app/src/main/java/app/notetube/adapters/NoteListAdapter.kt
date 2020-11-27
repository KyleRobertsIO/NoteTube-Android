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
import app.notetube.notes_document

class NoteListAdapter(
    private var activity: notes_document,
    private var notes: ArrayList<String>
): BaseAdapter() {

    private class ViewHolder(row: View?) {

        var noteTitle: EditText? = null
        var noteBody: EditText? = null

        init {
            //this.listItemName = row?.findViewById(R.id.listItemName)
            //this.listItemIcon = row?.findViewById(R.id.listItemIcon)
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

        //var documentItem = dirDocuments[position]
        var noteTitle: String = notes[position]
        var noteBody: String = "Example of body goes here."

        viewHolder.noteTitle?.setText(noteTitle, TextView.BufferType.EDITABLE)
        viewHolder.noteBody?.setText(noteBody, TextView.BufferType.EDITABLE)

        return view as View
    }

}

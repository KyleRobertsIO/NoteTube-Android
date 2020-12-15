package app.notetube.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import app.notetube.MainActivity
import app.notetube.R
import app.notetube.enums.DocumentType
import app.notetube.models.api.Document

class DocumentListAdapter(
    private var activity: MainActivity,
    private var documents: ArrayList<Document>
): BaseAdapter() {

    private var filteredDocuments : ArrayList<Document> = ArrayList<Document>()
    private var originalDocuments : ArrayList<Document> = ArrayList<Document>()

    private class ViewHolder(row: View?) {

        var listItemName: TextView? = null

        init {
            this.listItemName = row?.findViewById(R.id.listItemName)
        }
    }

    init {
        this.originalDocuments.addAll(documents)
    }

    fun filter(charText : String) {
        val lowerCharText = charText.toLowerCase()
        documents.clear()
        if (lowerCharText.length == 0){
            documents.addAll(originalDocuments)
        }else{
            for (doc in originalDocuments){
                if (doc.documentName.toLowerCase().contains(lowerCharText)) {
                    filteredDocuments.add(doc)
                }
            }
            documents = filteredDocuments
        }
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return documents.size
    }

    override fun getItem(position: Int): Any {
        return documents[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view: View
        val viewHolder: ViewHolder

        if(convertView == null) {
            val inflater = activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.document_list_item, null)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = ViewHolder(view) as ViewHolder
        }

        var documentItem = documents[position]

        viewHolder.listItemName?.text = documentItem.documentName

        return view as View
    }
}
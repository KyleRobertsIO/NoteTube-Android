package app.notetube.adapters

import android.content.Context
import android.media.Image
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import app.notetube.MainActivity
import app.notetube.R
import app.notetube.models.DocumentListItem

class DocumentListAdapter(
    private var activity: MainActivity,
    private var dirDocuments: ArrayList<DocumentListItem>
): BaseAdapter() {

    private class ViewHolder(row: View?) {

        var listItemName: TextView? = null
        var listItemIcon: ImageView? = null

        init {
            this.listItemName = row?.findViewById(R.id.listItemName)
            this.listItemIcon = row?.findViewById(R.id.listItemIcon)
        }
    }

    override fun getCount(): Int {
        return dirDocuments.size
    }

    override fun getItem(position: Int): Any {
        return dirDocuments[position]
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

        var documentItem = dirDocuments[position]

        viewHolder.listItemName?.text = documentItem.name
        //viewHolder.photo?.setImageURI(Uri.parse(gift.photoPath))

        return view as View
    }
}
package app.notetube.models.api

import java.io.Serializable

class User: Serializable {

    var id: Int = 0
    var email: String = ""
    var documents: ArrayList<Document> = ArrayList<Document>()

    constructor(
        id: Int,
        email: String,
        documents: ArrayList<Document>
    ) {
        this.id = id
        this.email = email
        this.documents = documents
    }

}
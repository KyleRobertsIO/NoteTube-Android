package app.notetube.models.api

import java.io.Serializable

class Document: Serializable {

    var id: Int = 0
    var documentName: String = ""
    var youtubeVideoId: String = ""
    var createDate: Long = 0
    var notes: ArrayList<Note> = ArrayList<Note>()

    constructor(
        id: Int,
        documentName: String,
        youtubeVideoId: String,
        createdDate: Long,
        notes: ArrayList<Note>
    ) {
        this.id = id
        this.documentName = documentName
        this.youtubeVideoId = youtubeVideoId
        this.createDate = createdDate
        this.notes = notes
    }

    constructor()
}
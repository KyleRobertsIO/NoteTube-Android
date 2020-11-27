package app.notetube.models

import app.notetube.enums.DocumentType
import java.io.Serializable

class DocumentListItem: Serializable {

    var id: Int = 0
    var documentType: DocumentType = DocumentType.NOTE_DOCUMENT
    var name: String = ""
    var videoId: String = ""

    constructor(id: Int, name: String, documentType: DocumentType, videoId: String) {
        this.name = name
        this.documentType = documentType
        this.id = id
        this.videoId = videoId
    }
}
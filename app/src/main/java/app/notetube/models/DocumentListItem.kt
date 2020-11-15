package app.notetube.models

import app.notetube.enums.DocumentType

class DocumentListItem {

    var id: Int = 0
    var documentType: DocumentType = DocumentType.NOTE_DOCUMENT
    var name: String = ""

    constructor(id: Int, name: String, documentType: DocumentType) {
        this.name = name
        this.documentType = documentType
        this.id = id
    }
}
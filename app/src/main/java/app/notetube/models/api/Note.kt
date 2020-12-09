package app.notetube.models.api

import java.io.Serializable

class Note: Serializable {

    var id: Int = 0
    var title: String = ""
    var body: String = ""
    var startTime: Int = 0
    var endTime: Int = 0
    var flagImportant: Boolean = false

    constructor(
        id: Int,
        title: String,
        body: String,
        startTime: Int,
        endTime: Int,
        flagImportant: Boolean
    ) {
        this.id = id
        this.title = title
        this.body = body
        this.startTime = startTime
        this.endTime = endTime
        this.flagImportant = flagImportant
    }
}
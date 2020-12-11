package app.notetube.models.api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Note: Serializable {

    @SerializedName("id") var id: Int = 0
    @SerializedName("title") var title: String = ""
    @SerializedName("body") var body: String = ""
    @SerializedName("startTime") var startTime: Int = 0
    @SerializedName("endTime") var endTime: Int = 0
    @SerializedName("flagImportant") var flagImportant: Boolean = false

    constructor()

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

    fun equalsEdit(editedNote: Note): Boolean {
        if(this.title != editedNote.title){
            return false
        }
        if(this.body != editedNote.body){
            return false
        }
        if(this.startTime != editedNote.startTime){
            return false
        }
        if(this.endTime != editedNote.endTime){
            return false
        }
        if(this.flagImportant != editedNote.flagImportant){
            return false
        }
        return true
    }

    fun copy(providedNote: Note){
        this.id = providedNote.id
        this.title = providedNote.title
        this.body = providedNote.body
        this.startTime = providedNote.startTime
        this.endTime = providedNote.endTime
        this.flagImportant = providedNote.flagImportant
    }
}
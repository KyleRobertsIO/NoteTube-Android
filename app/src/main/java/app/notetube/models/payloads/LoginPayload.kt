package app.notetube.models.payloads

class LoginPayload {

    var email = ""
    var password = ""

    constructor(email: String?, password: String?) {
        this.email = email.toString()
        this.password = password.toString()
    }
}
package app.notetube

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.TextView
import app.notetube.models.payloads.LoginPayload
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import javax.xml.validation.Validator

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val emailField = findViewById<TextInputLayout>(R.id.newEmailField)
        val emailErrorText = findViewById<TextView>(R.id.emailErrorText)
        val emptyEmailErrorText = findViewById<TextView>(R.id.emptyEmailErrorText)
        val newPasswordField = findViewById<TextInputLayout>(R.id.newPasswordField)
        val passwordConfirmField = findViewById<TextInputLayout>(R.id.passwordConfirmField)
        val passwordErrorText = findViewById<TextView>(R.id.passwordErrorText)
        val emptyPasswordErrorText = findViewById<TextView>(R.id.emptyPasswordErrorText)
        val emptyConfirmPasswordErrorText = findViewById<TextView>(R.id.emptyConfirmPasswordErrorText)
        val submitRegisterButton = findViewById<MaterialButton>(R.id.submitRegisterButton)
        val goToLoginActivity = findViewById<Button>(R.id.goToLoginButton)

        submitRegisterButton.setOnClickListener {
            if (Patterns.EMAIL_ADDRESS.matcher(emailField.editText?.text.toString()).matches()) {
                emailErrorText.visibility = View.GONE
            } else if (emailField.editText?.text.toString().isNotBlank()) {
                emailErrorText.visibility = View.VISIBLE
            }

            if (emailField.editText?.text.toString().isNotBlank()) {
                emptyEmailErrorText.visibility = View.GONE
            } else {
                emptyEmailErrorText.visibility = View.VISIBLE
            }

            if (newPasswordField.editText?.text.toString() == passwordConfirmField.editText?.text.toString()) {
                passwordErrorText.visibility = View.GONE
            } else {
                passwordErrorText.visibility = View.VISIBLE
            }

            if (newPasswordField.editText?.text.toString().isNotEmpty()) {
                emptyPasswordErrorText.visibility = View.GONE
            } else {
                emptyPasswordErrorText.visibility = View.VISIBLE
            }

            if (passwordConfirmField.editText?.text.toString().isNotEmpty()) {
                emptyConfirmPasswordErrorText.visibility = View.GONE
            } else {
                emptyConfirmPasswordErrorText.visibility = View.VISIBLE
            }

            if (emailErrorText.visibility == View.GONE
                && emptyEmailErrorText.visibility == View.GONE
                && passwordErrorText.visibility == View.GONE
                && emptyConfirmPasswordErrorText.visibility == View.GONE
                && emptyPasswordErrorText.visibility == View.GONE) {

                    val newUser = LoginPayload(emailField.editText?.text.toString(), newPasswordField.editText?.text.toString())
                    var message = ""

                    val thread = Thread(Runnable {
                        try {
                            message = registerUser(newUser)

                            runOnUiThread {
                                if (message.isNotEmpty()) {
                                    MaterialAlertDialogBuilder(this)
                                        .setTitle("Error")
                                        .setMessage(message)
                                        .setNeutralButton("Ok") { dialog, _ ->
                                            dialog.cancel()
                                        }
                                        .show()
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Log.d("JWT", message)
                        }
                })
                thread.start()
            } else {
                Log.d("JTW", "Not Valid!")
            }
        }

        goToLoginActivity.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun registerUser(requestBody: LoginPayload): String {
        // Create URI
        val url : String = getString(R.string.primary_url)
        val uri = "$url/user"

        val body = Gson()
            .toJson(requestBody)
            .toString()
            .toRequestBody()

        // Request service
        val client = OkHttpClient()
        val request: Request = Request.Builder()
            .url(uri)
            .addHeader("content-type", "application/json")
            .method("POST", body)
            .build()

        // Handle response
        val response: Response = client.newCall(request).execute()
        var message = ""

        if (response.isSuccessful) {
            val jsonObj = JSONObject(response.body?.string())

            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        } else {
            val jsonObj = JSONObject(response.body?.string())
            message = jsonObj.getString("message")
        }
        return message
    }
}
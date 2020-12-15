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
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailField = findViewById<TextInputLayout>(R.id.emailField)
        val passwordField = findViewById<TextInputLayout>(R.id.passwordField)
        val loginButton = findViewById<MaterialButton>(R.id.submitLoginButton)
        val registerButton = findViewById<Button>(R.id.goToRegisterButton)
        val emailErrorText = findViewById<TextView>(R.id.emailErrorText)
        val emptyEmailErrorText = findViewById<TextView>(R.id.emptyEmailErrorText)
        val passwordErrorText = findViewById<TextView>(R.id.passwordErrorText)

        loginButton.setOnClickListener {
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

            if (passwordField.editText?.text.toString().isNotEmpty()) {
                passwordErrorText.visibility = View.GONE
            } else {
                passwordErrorText.visibility = View.VISIBLE
            }

            if (emailErrorText.visibility == View.GONE
                && emptyEmailErrorText.visibility == View.GONE
                && passwordErrorText.visibility == View.GONE)
                {
                val requestBody = LoginPayload(
                    emailField.editText?.text.toString(),
                    passwordField.editText?.text.toString()
                )
                var message = ""

                val thread = Thread(Runnable {
                    try {
                        message = loginUser(requestBody)

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
                    }
                })
                thread.start()
            }
        }

        registerButton.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(requestBody: LoginPayload): String {
        // Create URI
        val url : String = getString(R.string.primary_url)
        val uri = "$url/auth/login/mobile"

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
        var message = ""
        val response: Response = client.newCall(request).execute()

        if (response.isSuccessful) {
            val jsonObj = JSONObject(response.body?.string())
            val token = jsonObj.get("jwtToken").toString()
            saveToken(token)

            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
        } else {
            val jsonObj = JSONObject(response.body?.string())
            message = jsonObj.getString("message")
        }

        return message
    }

    private fun saveToken(token: String) {
        val sharedPreference = SharedPreference(this)
        sharedPreference.save("JWT", token)
        sharedPreference.saveBool("isLoggedIn", true)
    }
}
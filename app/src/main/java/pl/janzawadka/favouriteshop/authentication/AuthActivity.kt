package pl.janzawadka.favouriteshop.authentication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import pl.janzawadka.favouriteshop.R

class AuthActivity : AppCompatActivity() {

    private lateinit var authService: AuthService

    private lateinit var signInButton: Button
    private lateinit var signUpButton: Button
    private lateinit var confirmSignUpButton: Button
    private lateinit var confirmSignInButton: Button

    private lateinit var emailField: TextView
    private lateinit var passField: TextView
    private lateinit var confirmPassField: TextView


    private lateinit var errorField: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        authService = AuthService(this)
        getElements()
        createListeners()
    }

    private fun getElements(){
        signInButton = findViewById(R.id.sign_in)
        signUpButton = findViewById(R.id.sing_up)
        confirmSignUpButton = findViewById(R.id.confirm_sign_up)
        confirmSignInButton = findViewById(R.id.confirm_sign_in)

        emailField = findViewById(R.id.login)
        passField = findViewById(R.id.password)
        confirmPassField = findViewById(R.id.confirm_password)

        errorField = findViewById(R.id.errorField)

        setSignInMode()
    }

    private fun createListeners() {
        confirmPassField.visibility = View.GONE
        confirmSignUpButton.visibility = View.GONE

        confirmSignInButton.setOnClickListener {
            authService.singIn(emailField.text.toString(), passField.text.toString())
        }

        confirmSignUpButton.setOnClickListener {
            authService.signUp(emailField.text.toString(), passField.text.toString(), confirmPassField.text.toString())

        }

        signInButton.setOnClickListener {
            setSignInMode()
        }

        signUpButton.setOnClickListener {
            setSignUpMode()
        }

    }

    fun setSignUpMode() {
        confirmPassField.visibility = View.VISIBLE

        confirmSignUpButton.visibility = View.VISIBLE
        confirmSignInButton.visibility = View.GONE

        signInButton.visibility = View.VISIBLE
        signUpButton.visibility = View.GONE
    }

    fun setSignInMode() {
        confirmPassField.visibility = View.GONE


        confirmSignUpButton.visibility = View.GONE
        confirmSignInButton.visibility = View.VISIBLE

        signInButton.visibility = View.GONE
        signUpButton.visibility = View.VISIBLE
    }

    fun resetFields() {
        emailField.text = ""
        passField.text = ""
        confirmPassField.text = ""
    }

    fun setMessage(text: String) {
        errorField.text = text
    }

}
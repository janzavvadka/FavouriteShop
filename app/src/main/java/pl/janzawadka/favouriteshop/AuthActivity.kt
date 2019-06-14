package pl.janzawadka.favouriteshop

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.TextView
import pl.janzawadka.favouriteshop.auth.AuthService

class AuthActivity : AppCompatActivity() {

    private lateinit var authService: AuthService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        authService = AuthService(this)
        createListeners()
    }

    fun createListeners(){
        val signInButton: Button = findViewById(R.id.sign_in)
        val signUpButton: Button = findViewById(R.id.sing_up)
        val confirmPassField: TextView = findViewById(R.id.confirm_password)
        val confirmSingUpButton: Button = findViewById(R.id.confirm_sign_up)

        confirmPassField.visibility = View.GONE
        confirmSingUpButton.visibility = View.GONE

        signInButton.setOnClickListener {
            authService.singIn()
        }

        signUpButton.setOnClickListener {
            confirmPassField.visibility = View.VISIBLE
            signUpButton.visibility = View.GONE
            confirmSingUpButton.visibility = View.VISIBLE
        }

        confirmSingUpButton.setOnClickListener {
            authService.signUp()

        }
    }

    fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
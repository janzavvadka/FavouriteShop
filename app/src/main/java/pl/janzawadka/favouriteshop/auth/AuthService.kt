package pl.janzawadka.favouriteshop.auth

import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import pl.janzawadka.favouriteshop.AuthActivity
import pl.janzawadka.favouriteshop.R

class AuthService(var activity: AuthActivity) {

    private var auth = FirebaseAuth.getInstance()

    var user: FirebaseUser? = null

    fun singIn() {
        val emailField: TextView = getElementById(R.id.login)
        val passField: TextView = getElementById(R.id.password)
         if(emailField.text.isNullOrBlank() || passField.text.isNullOrBlank()) {
             setErrorMessage("Fields can't be empty")
             return
         }

        auth.signInWithEmailAndPassword(emailField.text.toString(), passField.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    user = auth.currentUser
                    if (user != null) {
                        activity.startMainActivity()
                    }
                } else {
                    setErrorMessage(task.exception?.message.toString())
                }
            }
    }

    fun signUp() {
        val signInButton: Button = getElementById(R.id.sign_in)
        val signUpButton: Button = getElementById(R.id.sing_up)
        val emailField: TextView = getElementById(R.id.login)
        val passField: TextView = getElementById(R.id.password)
        val confirmSingUpButton: Button = getElementById(R.id.confirm_sign_up)
        val confirmPassField: TextView = getElementById(R.id.confirm_password)

        if(emailField.text.isNullOrBlank() || passField.text.isNullOrBlank() || confirmPassField.text.isNullOrBlank()){
            setErrorMessage("Fields can't be empty")
            return
        }

        if( !validatePassword(passField.text.toString(), confirmPassField.text.toString()) ) {
            setErrorMessage("Password must be of minimum 6 characters and be the same")
            return
        }

        auth.createUserWithEmailAndPassword(emailField.text.toString(), passField.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    setErrorMessage("Registration successful")
                    confirmPassField.visibility = View.GONE
                    confirmSingUpButton.visibility = View.GONE
                    signInButton.visibility = View.VISIBLE
                    signUpButton.visibility = View.VISIBLE
                } else {
                    setErrorMessage(task.exception?.message.toString())
                    emailField.text = ""
                    passField.text = ""
                    confirmPassField.text = ""
                }
            }
    }

    private fun setErrorMessage(text: String) {
        val errorField: TextView = getElementById(R.id.errorField)
        errorField.text = text
    }

    private fun validatePassword(password: String, passwordConfirm: String): Boolean {
        return password == passwordConfirm && password.length > 6
    }

    private fun <T : View>getElementById(id: Int): T{
        return activity.findViewById(id)
    }
}
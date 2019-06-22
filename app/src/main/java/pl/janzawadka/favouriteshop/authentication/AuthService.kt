package pl.janzawadka.favouriteshop.authentication

import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import pl.janzawadka.favouriteshop.shop_list.ShopListActivity

class AuthService(var activity: AuthActivity) {

    private var auth = FirebaseAuth.getInstance()

    fun singIn(login: String, password: String) {
        if (!validateCredential(login, password))
            return

        auth.signInWithEmailAndPassword(login, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (auth.currentUser != null) {
                        startMainActivity()
                    }
                } else {
                    activity.setMessage(task.exception?.message.toString())
                }
            }
    }

    fun signUp(login: String, password: String, confirmPassword: String) {
        if (!validatePassword(login, password, confirmPassword))
            return

        auth.createUserWithEmailAndPassword(login, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    activity.setMessage("Add user correctly")
                    activity.setSignInMode()
                } else {
                    activity.setMessage(task.exception?.message.toString())
                    activity.resetFields()
                }
            }
    }

    private fun validateCredential(login: String, password: String): Boolean{
        if (login.isBlank() || password.isBlank()) {
            activity.setMessage("Fields can't be empty")
            return false
        }
        return true
    }

    private fun validatePassword(login: String, password: String, passwordConfirm: String): Boolean {
        if (login.isBlank() || password.isBlank() || passwordConfirm.isBlank()) {
            activity.setMessage("Fields can't be empty")
            return false
        }

        if (password != passwordConfirm || password.length <= 6) {
            activity.setMessage("Password must be of minimum 6 characters and be the same")
            return false
        }
        return true
    }

    private fun startMainActivity() {
        val intent = Intent(activity, ShopListActivity::class.java)
        activity.startActivity(intent)
    }

}
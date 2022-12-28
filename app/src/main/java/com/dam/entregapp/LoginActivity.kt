package com.dam.entregapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setup()
    }
    private fun setup(){
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val btn_login = findViewById<Button>(R.id.btn_login)
        val registro = findViewById<TextView>(R.id.registro)

        btn_login.setOnClickListener {
            val emailText = email.text.toString()
            val passwordText = password.text.toString()

            if(emailText.isEmpty() || passwordText.isEmpty()){
                Toast.makeText(this, "Por favor, introduce tus datos", Toast.LENGTH_SHORT).show()
            } else{
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email.text.toString(), password.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful){
                        showHome(it.result?.user?.email.toString(), ProviderType.BASIC)
                    } else {
                        showAlert()
                    }
                }
            }
        }

        registro.setOnClickListener {
            // abrimos la actividad de registro
            val a = Intent(this, SigninActivity::class.java)
            startActivity(a)
        }

    }
    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al ususario")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun showHome(email: String, provider: ProviderType){

        val homeIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }
}
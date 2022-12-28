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

class SigninActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        setup()
    }

    private fun setup(){
        val usuario = findViewById<EditText>(R.id.usuario)
        val phone = findViewById<EditText>(R.id.phone)
        val email = findViewById<EditText>(R.id.email)
        val contrasena = findViewById<EditText>(R.id.contrasena)
        val contrasena2 = findViewById<EditText>(R.id.contrasena2)

        val btn_registrar = findViewById<Button>(R.id.btn_registrar_correo)
        val btn_loginNow = findViewById<TextView>(R.id.btn_loginNow)

        btn_loginNow.setOnClickListener {
            finish()
        }

        btn_registrar.setOnClickListener {
            val usuarioText = usuario.text.toString()
            val phoneText = phone.text.toString()
            val emailText = email.text.toString()
            val contrasenaText = contrasena.text.toString()
            val contrasena2Text = contrasena2.text.toString()

            // comprobamos que se rellenan todos los campos antes de mandarlo a la base de datos
            if (usuarioText.isEmpty() || phoneText.isEmpty() || emailText.isEmpty() || contrasenaText.isEmpty()) {
                Toast.makeText(this, "Rellene todos los campos", Toast.LENGTH_LONG).show()
            }else if (!contrasenaText.equals(contrasena2Text)){
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            }else{
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.text.toString(), contrasena.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful){
                        showHome(it.result?.user?.email.toString(), ProviderType.BASIC)
                    } else {
                        showAlert()
                    }
                }
            }
        }
    }
    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error registrando al ususario")
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
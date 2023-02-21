package com.dam.entregapp.ui.fragments.register

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dam.entregapp.R
import com.dam.entregapp.data.model.User
import com.dam.entregapp.databinding.FragmentRegisterBinding
import com.dam.entregapp.ui.viewmodels.RegisterViewModel
import com.google.firebase.auth.FirebaseAuth

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private lateinit var registerViewModel: RegisterViewModel
    private var _binding: FragmentRegisterBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        registerViewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
        setup()
    }

    private fun setup() {

        binding.btnLoginNow.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding.btnRegistrarCorreo.setOnClickListener {
            userToDB()
        }
    }

    private fun showAlert(v: View) {
        val builder = AlertDialog.Builder(v.context)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al ususario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome() {

        findNavController().navigate(R.id.action_registerFragment_to_mainMenu)

    }

    private fun userToDB() {
        val usuarioText = binding.usuario.text.toString()
        val phoneText = binding.phone.text.toString()
        val emailText = binding.email.text.toString()
        val contrasenaText = binding.contrasena.text.toString()
        val contrasena2Text = binding.contrasena2.text.toString()

        Log.d("Registro", "Al hacer click")

        // comprobamos que se rellenan todos los campos antes de mandarlo a la base de datos
        if (usuarioText.isEmpty() || phoneText.isEmpty() || emailText.isEmpty() || contrasenaText.isEmpty()) {
            Toast.makeText(context, "Rellene todos los campos", Toast.LENGTH_LONG).show()
        } else if (!contrasenaText.equals(contrasena2Text)) {
            Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
        } else {
            Log.d("Registro", "Antes de registrar")
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailText, contrasenaText)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val user =
                            User(null, usuarioText, emailText, contrasenaText, phoneText.toInt())
                        registerViewModel.addUser(user)
                        Log.d("Registro", "Pues parece que funciona")
                        showHome()
                    } else {
                        showAlert(requireView())
                    }
                }
        }
    }
}
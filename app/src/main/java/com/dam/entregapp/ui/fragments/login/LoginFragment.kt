package com.dam.entregapp.ui.fragments.login

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dam.entregapp.R
import com.dam.entregapp.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment(R.layout.fragment_login) {

    private var _binding: FragmentLoginBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setup()
    }

    private fun setup() {

        binding.btnLogin.setOnClickListener {
            val emailText = binding.email.text.toString()
            val passwordText = binding.password.text.toString()

            if (emailText.isEmpty() || passwordText.isEmpty()) {
                Toast.makeText(context, "Por favor, introduce tus datos", Toast.LENGTH_SHORT).show()
            } else {
                //LogIn en FireBase
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    binding.email.text.toString(),
                    binding.password.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showHome()
                    } else {
                        showAlert(requireView())
                    }
                }
            }
        }

        binding.registro.setOnClickListener {
            // abrimos la actividad de registro
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
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
        findNavController().navigate(R.id.action_loginFragment_to_mainMenu2)
    }
}
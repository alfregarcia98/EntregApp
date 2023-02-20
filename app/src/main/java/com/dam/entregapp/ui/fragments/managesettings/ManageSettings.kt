package com.dam.entregapp.ui.fragments.managesettings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dam.entregapp.R
import com.dam.entregapp.data.model.User
import com.dam.entregapp.databinding.FragmentManageSettingsBinding
import com.dam.entregapp.ui.viewmodels.UserViewModel


class ManageSettings : Fragment(R.layout.fragment_manage_settings) {

    private lateinit var userViewModel: UserViewModel
    private var name = ""
    private var email = ""
    private var password = ""
    private var telephone = 0
    private var addr1 = ""
    private var addr2 = ""

    private var _binding: FragmentManageSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentManageSettingsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        binding.btnGuardar.setOnClickListener {
            updateUserDB()
        }


    }

    private fun updateUserDB() {
        //Get text from editTexts
        name = binding.name.text.toString()
        email = binding.email.text.toString()
        password = binding.password.text.toString()
        telephone = binding.telephone.text.toString().toInt()
        addr1 = binding.addr1.text.toString()
        //coordinates1 = getGeocoder(addr1).toString()
        addr2 = binding.addr2.text.toString()
        //coordinates2 = getGeocoder(addr2).toString()

        //Check that the form is complete before submitting data to the database
        if (!(name.isEmpty() || email.isEmpty() || password.isEmpty() || telephone == 0 || addr1.isEmpty() || addr2.isEmpty())) {
            val user = User(null, name, email, password, telephone)

            //add the user if all the fields are filled
            userViewModel.updateUser(user)
            Toast.makeText(context, "Data updated successfully!", Toast.LENGTH_SHORT).show()

            //navigate back to our home fragment
            findNavController().navigate(R.id.action_manageSettings_to_mainMenu)
        } else {
            Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show()
        }
    }
}
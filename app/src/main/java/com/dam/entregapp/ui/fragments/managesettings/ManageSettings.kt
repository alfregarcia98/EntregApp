package com.dam.entregapp.ui.fragments.managesettings

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dam.entregapp.LocationService
import com.dam.entregapp.R
import com.dam.entregapp.data.model.User
import com.dam.entregapp.databinding.FragmentManageAddressBinding
import com.dam.entregapp.logic.utils.Geocoder
import com.dam.entregapp.ui.viewmodels.UserViewModel

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class ManageSettings : Fragment(R.layout.fragment_manage_settings) {

    private lateinit var userViewModel: UserViewModel
    private var name = ""
    private var email = ""
    private var password = ""
    private var telephone = 0
    private var addr1 = ""
    private var addr2 = ""

    private var _binding: FragmentManageAddressBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentManageAddressBinding.inflate(inflater, container, false)
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
            addUserToDB()
        }


    }

    private fun addUserToDB() {
        //Get text from editTexts
        name = binding.name.text.toString()
        email = binding.email.text.toString()
        password = binding.password.text.toString()
        telephone = binding.telephone.text.toString().toInt()
        addr1 = binding.addr1.text.toString()
        addr2 = binding.addr2.text.toString()


        GlobalScope.launch(Dispatchers.IO) {
            var result = Geocoder.forwardGeocode(addr1)

            Log.d("Geocoder", result.toString())
            println(result)
        }

        //Check that the form is complete before submitting data to the database
        if (!(name.isEmpty() || email.isEmpty() || password.isEmpty() || telephone == 0 || addr1.isEmpty() || addr2.isEmpty())) {
            val user = User(0, name, email, password, telephone, addr1, addr2)

            //add the user if all the fields are filled
            userViewModel.addUser(user)
            Toast.makeText(context, "User created successfully!", Toast.LENGTH_SHORT).show()

            //navigate back to our home fragment
            findNavController().navigate(R.id.action_manageSettings_to_mainMenu)
        } else {
            Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show()
        }
    }
}
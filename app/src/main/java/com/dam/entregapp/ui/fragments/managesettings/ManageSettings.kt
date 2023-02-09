package com.dam.entregapp.ui.fragments.managesettings

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
import com.dam.entregapp.data.model.Address
import com.dam.entregapp.data.model.User
import com.dam.entregapp.databinding.FragmentManageAddressBinding
import com.dam.entregapp.model.GeocoderService
import com.dam.entregapp.ui.viewmodels.UserViewModel

import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ManageSettings : Fragment(R.layout.fragment_manage_settings) {

    private lateinit var userViewModel: UserViewModel
    private var name = ""
    private var email = ""
    private var password = ""
    private var telephone = 0
    private var addr1 = ""
    private var coordinates1 = ""
    private var addr2 = ""
    private var coordinates2 = ""

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

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://api.positionstack.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun getGeocoder(address: String): Pair<Double, Double> {
        var lon: Double = 0.0
        var lat: Double = 0.0

        val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }

        CoroutineScope(Dispatchers.IO + coroutineExceptionHandler).launch {
            val call = getRetrofit().create(GeocoderService::class.java)
                .listGeocoderResult("6dde42cd8f9c77849398a675529ffe26", "$address", "1")
            val geocoder = call.execute().body()
            Log.d("getGeocoder", "Print: $geocoder")

            lon = geocoder!!.data[0].longitude
            lat = geocoder!!.data[0].latitude

            Log.d("Geocoder", "Ubicacion: $lon,$lat")
        }
        return Pair(lon, lat)
    }

    private fun addUserToDB() {
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
            val user = User(0, name, email, password, telephone, addr1, addr2)

            //add the user if all the fields are filled
            userViewModel.addUser(user)
            //Prueba
            val address = Address(0,2,"Buena y bonita calle",67.5,69.6)
            userViewModel.addAddress(address)
            Toast.makeText(context, "Data updated successfully!", Toast.LENGTH_SHORT).show()

            //navigate back to our home fragment
            findNavController().navigate(R.id.action_manageSettings_to_mainMenu)
        } else {
            Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show()
        }
    }
}
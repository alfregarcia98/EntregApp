package com.dam.entregapp.ui.fragments.manageaddress

import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dam.entregapp.R
import com.dam.entregapp.data.model.Address
import com.dam.entregapp.databinding.FragmentManageAddressBinding
import com.dam.entregapp.logic.utils.Calculations
import com.dam.entregapp.logic.utils.Geocoder
import com.dam.entregapp.ui.viewmodels.UserViewModel
import java.util.*

class ManageAddress : Fragment(R.layout.fragment_manage_address),
    TimePickerDialog.OnTimeSetListener {

    private lateinit var userViewModel: UserViewModel
    private var addr1 = ""
    private var addr2 = ""

    private var hour = 0
    private var minute = 0
    private var selectingPrimaryTime = true

    private var cleanTimePrimary = ""
    private var cleanTimeSecondary = ""

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
            addAddress()
        }

        binding.btnUpdate.setOnClickListener {
            addAddress()
        }

        pickTime()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    //set on click listeners for our data and time pickers
    private fun pickTime() {
        binding.btHour1.setOnClickListener {
            selectingPrimaryTime = true
            getTimeCalendar()
            TimePickerDialog(context, this, hour, minute, true).show()

        }
        binding.btHour2.setOnClickListener {
            selectingPrimaryTime = false
            getTimeCalendar()
            TimePickerDialog(context, this, hour, minute, true).show()

        }
    }

    private fun addAddress() {
        addr1 = binding.addr1.text.toString()
        addr2 = binding.addr2.text.toString()
        //Check that the form is complete before submitting data to the database
        if (!(addr1.isEmpty() || addr2.isEmpty())) {

            Geocoder.getGeocoder(
                address = addr1,
                onResult = { (lon, lat) ->
                    val primaryAddress = Address(null, 1, addr1, cleanTimePrimary, lon, lat)
                    userViewModel.addAddress(primaryAddress)
                }
            )

            Geocoder.getGeocoder(
                address = addr2,
                onResult = { (lon, lat) ->
                    val secondaryAddress = Address(null, 1, addr2, cleanTimeSecondary, lon, lat)
                    userViewModel.addAddress(secondaryAddress)
                }
            )

            Toast.makeText(context, "Address saved successfully!", Toast.LENGTH_SHORT).show()

            //navigate back to our home fragment
            findNavController().navigate(R.id.action_manageAddress_to_mainMenu)
        } else {
            Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show()
        }
    }

    //get the time set
    override fun onTimeSet(TimePicker: TimePicker?, p1: Int, p2: Int) {
        Log.d("Fragment", "Time: $p1:$p2")

        if (selectingPrimaryTime) {
            cleanTimePrimary = Calculations.cleanTime(p1, p2)
            binding.txHour1.text = "Time: $cleanTimePrimary"
        } else {
            cleanTimeSecondary = Calculations.cleanTime(p1, p2)
            binding.txHour2.text = "Time: $cleanTimeSecondary"
        }
    }

    //get the current time
    private fun getTimeCalendar() {
        val cal = Calendar.getInstance()
        hour = cal.get(Calendar.HOUR_OF_DAY)
        minute = cal.get(Calendar.MINUTE)
    }

    /**
    //get the date set
    override fun onDateSet(p0: DatePicker?, yearX: Int, monthX: Int, dayX: Int) {

    cleanDate = Calculations.cleanDate(dayX, monthX, yearX)
    binding.txDate.text = "Date: $cleanDate"
    }

    //get the current date
    private fun getDateCalendar() {
    val cal = Calendar.getInstance()
    day = cal.get(Calendar.DAY_OF_MONTH)
    month = cal.get(Calendar.MONTH)
    year = cal.get(Calendar.YEAR)
    }*/
    /**
    private fun getRetrofit(): Retrofit {
    return Retrofit.Builder()
    .baseUrl("http://api.positionstack.com/v1/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    }

    private fun getGeocoder(
    address: String,
    onResult: (latLong: Pair<Double, Double>) -> Unit
    ) {
    val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
    throwable.printStackTrace()
    }

    CoroutineScope(Dispatchers.IO + coroutineExceptionHandler).launch {

    var lon: Double = 0.0
    var lat: Double = 0.0

    val call = getRetrofit().create(GeocoderService::class.java)
    .listGeocoderResult("6dde42cd8f9c77849398a675529ffe26", "$address", "1")
    val geocoder = call.execute().body()
    Log.d("getGeocoder", "Print: $geocoder")

    lon = geocoder!!.data[0].longitude
    lat = geocoder!!.data[0].latitude

    onResult(Pair(lon, lat))

    Log.d("Geocoder", "Ubicacion: $lon,$lat")
    }
    }*/
}
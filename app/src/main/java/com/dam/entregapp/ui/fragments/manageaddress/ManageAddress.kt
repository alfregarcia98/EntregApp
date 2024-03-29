package com.dam.entregapp.ui.fragments.manageaddress

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Intent
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
import com.dam.entregapp.BuildConfig
import com.dam.entregapp.location.LocationApp.Companion.prefs
import com.dam.entregapp.R
import com.dam.entregapp.data.model.Address
import com.dam.entregapp.databinding.FragmentManageAddressBinding
import com.dam.entregapp.logic.utils.Calculations
import com.dam.entregapp.ui.viewmodels.UserViewModel
import com.sucho.placepicker.AddressData
import com.sucho.placepicker.Constants
import com.sucho.placepicker.MapType
import com.sucho.placepicker.PlacePicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class ManageAddress : Fragment(R.layout.fragment_manage_address),
    TimePickerDialog.OnTimeSetListener {

    private lateinit var userViewModel: UserViewModel
    private var addr1 = ""
    private var addr2 = ""
    private var addr3 = ""
    private var addr4 = ""
    private var lat1 = 0.0
    private var lon1 = 0.0
    private var lat2 = 0.0
    private var lon2 = 0.0
    private var lat3 = 0.0
    private var lon3 = 0.0
    private var lat4 = 0.0
    private var lon4 = 0.0

    private var indice = 0

    private var hour = 0
    private var minute = 0

    private var timeSelector = ""

    private var cleanTimePrimaryStart = ""
    private var cleanTimePrimaryEnd = ""
    private var cleanTimeSecondaryStart = ""
    private var cleanTimeSecondaryEnd = ""
    private var cleanTimeThirdStart = ""
    private var cleanTimeThirdEnd = ""
    private var cleanTimeFourthStart = ""
    private var cleanTimeFourthEnd = ""

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

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        binding.btnGuardarSinAPI.setOnClickListener {
            if (binding.addr1.editText!!.text.isNotEmpty() && binding.addr2.editText!!.text.isNotEmpty()) {
                addAddressessNoAPI()
            }
            else {
                Toast.makeText(context, "Rellene al menos dos direcciones", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnErase.setOnClickListener {
            eraseAddress()
        }

        binding.btPlacePicker1.setOnClickListener {
            indice = 1
            showPlacePicker()
        }

        binding.btPlacePicker2.setOnClickListener {
            indice = 2
            showPlacePicker()
        }

        binding.btPlacePicker3.setOnClickListener {
            indice = 3
            showPlacePicker()
        }

        binding.btPlacePicker4.setOnClickListener {
            indice = 4
            showPlacePicker()
        }
        pickTime()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    //set on click listeners for our data and time pickers
    private fun pickTime() {
        binding.btHourPrimaryStart.setOnClickListener {
            timeSelector = "PrimaryStart"
            getTimeCalendar()
            TimePickerDialog(context, this, hour, minute, true).show()

        }

        binding.btHourPrimaryEnd.setOnClickListener {
            timeSelector = "PrimaryEnd"
            getTimeCalendar()
            TimePickerDialog(context, this, hour, minute, true).show()

        }

        binding.btHourSecondaryStart.setOnClickListener {
            timeSelector = "SecondaryStart"
            getTimeCalendar()
            TimePickerDialog(context, this, hour, minute, true).show()

        }

        binding.btHourSecondaryEnd.setOnClickListener {
            timeSelector = "SecondaryEnd"
            getTimeCalendar()
            TimePickerDialog(context, this, hour, minute, true).show()

        }

        binding.btHourThirdStart.setOnClickListener {
            timeSelector = "ThirdStart"
            getTimeCalendar()
            TimePickerDialog(context, this, hour, minute, true).show()

        }

        binding.btHourThirdEnd.setOnClickListener {
            timeSelector = "ThirdEnd"
            getTimeCalendar()
            TimePickerDialog(context, this, hour, minute, true).show()

        }

        binding.btHourFourthStart.setOnClickListener {
            timeSelector = "FourthStart"
            getTimeCalendar()
            TimePickerDialog(context, this, hour, minute, true).show()

        }

        binding.btHourFourthEnd.setOnClickListener {
            timeSelector = "FourthEnd"
            getTimeCalendar()
            TimePickerDialog(context, this, hour, minute, true).show()

        }
    }

    private fun showPlacePicker() {
        val intent = PlacePicker.IntentBuilder()
            .setLatLong(
                40.41692,
                -3.70218
            )  // Initial Latitude and Longitude the Map will load into
            .showLatLong(true)  // Show Coordinates in the Activity
            .setMapZoom(5.3f)  // Map Zoom Level. Default: 14.0
            .setAddressRequired(true) // Set If return only Coordinates if cannot fetch Address for the coordinates. Default: True
            .hideMarkerShadow(false) // Hides the shadow under the map marker. Default: False
//            .setMarkerDrawable(R.drawable.marker) // Change the default Marker Image
//            .setMarkerImageImageColor(R.color.colorPrimary)
//            .setFabColor(R.color.fabColor)
//            .setPrimaryTextColor(R.color.primaryTextColor) // Change text color of Shortened Address
//            .setSecondaryTextColor(R.color.secondaryTextColor) // Change text color of full Address
//            .setBottomViewColor(R.color.bottomViewColor) // Change Address View Background Color (Default: White)
//            .setMapRawResourceStyle(R.raw.map_style)  //Set Map Style (https://mapstyle.withgoogle.com/)
            .setMapType(MapType.NORMAL)
            .setPlaceSearchBar(
                false,
                BuildConfig.MAPS_API_KEY
            ) //Activate GooglePlace Search Bar. Default is false/not activated. SearchBar is a chargeable feature by Google
            .onlyCoordinates(false)  //Get only Coordinates from Place Picker
            .hideLocationButton(false)   //Hide Location Button (Default: false)
            .disableMarkerAnimation(false)   //Disable Marker Animation (Default: false)
            .build(requireActivity())
        startActivityForResult(intent, Constants.PLACE_PICKER_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                val addressData = data?.getParcelableExtra<AddressData>(Constants.ADDRESS_INTENT)
                Log.d("PickerResult", "$addressData")
                //TODO añadir excepcion por si devuelve error
                when (indice) {
                    1 -> {
                        binding.addr1.editText?.setText(addressData?.addressList?.get(0)?.getAddressLine(0))
                        binding.txPrimaryLat.text = addressData?.latitude.toString()
                        binding.txPrimaryLon.text = addressData?.longitude.toString()
                    }
                    2 -> {
                        binding.addr2.editText?.setText(addressData?.addressList?.get(0)?.getAddressLine(0))
                        binding.txSecondaryLat.text = addressData?.latitude.toString()
                        binding.txSecondaryLon.text = addressData?.longitude.toString()
                    }
                    3 -> {
                        binding.addr3.editText?.setText(addressData?.addressList?.get(0)?.getAddressLine(0))
                        binding.txThirdLat.text = addressData?.latitude.toString()
                        binding.txThirdLon.text = addressData?.longitude.toString()
                    }
                    4 -> {
                        binding.addr4.editText?.setText(addressData?.addressList?.get(0)?.getAddressLine(0))
                        binding.txFourthLat.text = addressData?.latitude.toString()
                        binding.txFourthLon.text = addressData?.longitude.toString()
                    }
                    // Add more cases as needed
                    else -> {
                        // No seleccionado
                    }
                }
                val direccion = addressData?.addressList?.get(0)?.getAddressLine(0)
                Log.d("PickerResult", "$direccion")

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun addAddressessNoAPI() {
        addr1 = binding.addr1.editText?.text.toString()
        addr2 = binding.addr2.editText?.text.toString()
        addr3 = binding.addr3.editText?.text.toString()
        addr4 = binding.addr4.editText?.text.toString()
        lat1 = binding.txPrimaryLat.text.toString().toDouble()
        lon1 = binding.txPrimaryLon.text.toString().toDouble()
        lat2 = binding.txSecondaryLat.text.toString().toDouble()
        lon2 = binding.txSecondaryLon.text.toString().toDouble()
        if (addr3.isNotEmpty()) {
            lat3 = binding.txThirdLat.text.toString().toDouble()
            lon3 = binding.txThirdLon.text.toString().toDouble()
        }
        if (addr4.isNotEmpty()) {
            lat4 = binding.txFourthLat.text.toString().toDouble()
            lon4 = binding.txFourthLon.text.toString().toDouble()
        }


        val primaryAddress =
            Address(
                1,
                prefs.getCurrentUserID(),
                addr1,
                cleanTimePrimaryStart,
                cleanTimePrimaryEnd,
                lon1,
                lat1
            )
        userViewModel.addAddress(primaryAddress)

        val secondaryAddress =
            Address(
                2,
                prefs.getCurrentUserID(),
                addr2,
                cleanTimePrimaryStart,
                cleanTimePrimaryEnd,
                lon2,
                lat2
            )
        userViewModel.addAddress(secondaryAddress)

        if (addr3.isNotEmpty()) {
            val thirdAddress =
                Address(
                    3,
                    prefs.getCurrentUserID(),
                    addr3,
                    cleanTimeThirdStart,
                    cleanTimeThirdEnd,
                    lon3,
                    lat3
                )
            userViewModel.addAddress(thirdAddress)
        }
        if (addr4.isNotEmpty()) {
            val fourthAddress =
                Address(
                    4,
                    prefs.getCurrentUserID(),
                    addr4,
                    cleanTimeFourthStart,
                    cleanTimeFourthEnd,
                    lon4,
                    lat4
                )
            userViewModel.addAddress(fourthAddress)
        }

        Toast.makeText(context, "Address saved successfully!", Toast.LENGTH_SHORT).show()

        //navigate back to our home fragment
        findNavController().navigate(R.id.action_manageAddress_to_mainMenu)
    }

    private fun eraseAddress() {
        CoroutineScope(Dispatchers.IO).launch {
            userViewModel.deleteAllAddress()
        }
    }

    //get the time set
    override fun onTimeSet(TimePicker: TimePicker?, p1: Int, p2: Int) {
        Log.d("Fragment", "Time: $p1:$p2")

        when (timeSelector) {
            "PrimaryStart" -> {
                cleanTimePrimaryStart = Calculations.cleanTime(p1, p2)
                binding.txHourPrimaryStart.text = "Time: $cleanTimePrimaryStart"
            }

            "PrimaryEnd" -> {
                cleanTimePrimaryEnd = Calculations.cleanTime(p1, p2)
                binding.txHourPrimaryEnd.text = "Time: $cleanTimePrimaryEnd"
            }

            "SecondaryStart" -> {
                cleanTimeSecondaryStart = Calculations.cleanTime(p1, p2)
                binding.txHourSecondaryStart.text = "Time: $cleanTimeSecondaryStart"
            }

            "SecondaryEnd" -> {
                cleanTimeSecondaryEnd = Calculations.cleanTime(p1, p2)
                binding.txHourSecondaryEnd.text = "Time: $cleanTimeSecondaryEnd"
            }

            "ThirdStart" -> {
                cleanTimeThirdStart = Calculations.cleanTime(p1, p2)
                binding.txHourThirdStart.text = "Time: $cleanTimeThirdStart"
            }

            "ThirdEnd" -> {
                cleanTimeThirdEnd = Calculations.cleanTime(p1, p2)
                binding.txHourThirdEnd.text = "Time: $cleanTimeThirdEnd"
            }

            "FourthStart" -> {
                cleanTimeFourthStart = Calculations.cleanTime(p1, p2)
                binding.txHourFourthStart.text = "Time: $cleanTimeFourthStart"
            }

            "FourthEnd" -> {
                cleanTimeFourthEnd = Calculations.cleanTime(p1, p2)
                binding.txHourFourthEnd.text = "Time: $cleanTimeFourthEnd"
            }
        }
    }

    //get the current time
    private fun getTimeCalendar() {
        val cal = Calendar.getInstance()
        hour = cal.get(Calendar.HOUR_OF_DAY)
        minute = cal.get(Calendar.MINUTE)
    }
}
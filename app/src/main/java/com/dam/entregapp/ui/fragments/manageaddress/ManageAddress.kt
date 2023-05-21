package com.dam.entregapp.ui.fragments.manageaddress

import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dam.entregapp.LocationApp.Companion.prefs
import com.dam.entregapp.R
import com.dam.entregapp.data.model.Address
import com.dam.entregapp.databinding.FragmentManageAddressBinding
import com.dam.entregapp.logic.utils.Calculations
import com.dam.entregapp.logic.utils.Geocoder
import com.dam.entregapp.ui.viewmodels.UserViewModel
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

        binding.btnGuardar.setOnClickListener {
            addAddress()
        }

        binding.btnUpdate.setOnClickListener {
            addAddress()
        }

        binding.btnErase.setOnClickListener {
            eraseAddress()
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

        binding.btnAddAddr.setOnClickListener{
            val editText = EditText(context)
            editText.hint = "Dirección"
            editText.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            editText.inputType = InputType.TYPE_CLASS_TEXT

            binding.layoutDirecciones.addView(editText, binding.layoutDirecciones.childCount - 1)
        }
    }

    private fun addAddress() {
        addr1 = binding.addr1.text.toString()
        addr2 = binding.addr2.text.toString()
        addr3 = binding.addr3.text.toString()
        addr4 = binding.addr4.text.toString()
        //Check that the form is complete before submitting data to the database
        if (!(addr1.isEmpty() || addr2.isEmpty())) {

            Geocoder.getGeocoder(
                address = addr1,
                onResult = { (lon, lat) ->
                    val primaryAddress =
                        Address(
                            0,
                            prefs.getCurrentUserID(),
                            addr1,
                            cleanTimePrimaryStart,
                            cleanTimePrimaryEnd,
                            lon,
                            lat
                        )
                    userViewModel.addAddress(primaryAddress)
                }
            )

            Geocoder.getGeocoder(
                address = addr2,
                onResult = { (lon, lat) ->
                    val secondaryAddress =
                        Address(
                            0,
                            prefs.getCurrentUserID(),
                            addr2,
                            cleanTimeSecondaryStart,
                            cleanTimeSecondaryEnd,
                            lon,
                            lat
                        )
                    userViewModel.addAddress(secondaryAddress)
                }
            )

            if(addr3.isNotEmpty()){

                Geocoder.getGeocoder(
                    address = addr3,
                    onResult = { (lon, lat) ->
                        val thirdAddress =
                            Address(
                                0,
                                prefs.getCurrentUserID(),
                                addr3,
                                cleanTimeThirdStart,
                                cleanTimeThirdEnd,
                                lon,
                                lat
                            )
                        userViewModel.addAddress(thirdAddress)
                    }
                )

            }
            if(addr4.isNotEmpty()) {

                Geocoder.getGeocoder(
                    address = addr4,
                    onResult = { (lon, lat) ->
                        val fourthAddress =
                            Address(
                                0,
                                prefs.getCurrentUserID(),
                                addr4,
                                cleanTimeFourthStart,
                                cleanTimeFourthEnd,
                                lon,
                                lat
                            )
                        userViewModel.addAddress(fourthAddress)
                    }
                )
            }

            Toast.makeText(context, "Address saved successfully!", Toast.LENGTH_SHORT).show()

            //navigate back to our home fragment
            findNavController().navigate(R.id.action_manageAddress_to_mainMenu)
        } else {
            Toast.makeText(context, "Please fill at least the two first addresses", Toast.LENGTH_SHORT).show()
        }
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
package com.dam.entregapp.ui.fragments.manageaddress

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.DateInterval
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dam.entregapp.R
import com.dam.entregapp.data.model.User
import com.dam.entregapp.databinding.FragmentManageAddressBinding
import com.dam.entregapp.logic.utils.Calculations
import com.dam.entregapp.ui.viewmodels.UserViewModel
import java.util.*

class ManageAddress : Fragment(R.layout.fragment_manage_address), TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private lateinit var userViewModel: UserViewModel
    private var addr1 = ""
    private var addr2 = ""

    private var name = ""
    private var email = ""
    private var password = ""
    private var telephone = 0

    private var day = 0
    private var month = 0
    private var year = 0
    private var hour = 0
    private var minute = 0

    private var cleanDate = ""
    private var cleanTime = ""

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

        binding.btnUpdate.setOnClickListener {
            updateUser()
        }

        pickDateAndTime()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    //set on click listeners for our data and time pickers
    private fun pickDateAndTime() {
        binding.btDate.setOnClickListener {
            getDateCalendar()
            DatePickerDialog(requireContext(), this, year, month, day).show()
        }

        binding.btHour.setOnClickListener {
            getTimeCalendar()
            TimePickerDialog(context, this, hour, minute, true).show()
        }
    }

    private fun updateUser() {
        addr1 = binding.addr1.text.toString()
        addr2 = binding.addr2.text.toString()
        if (!(addr1.isEmpty() || addr2.isEmpty())) {
      //      val user = User(0, name, email, password, telephone, addr1, addr2)
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

    //Check that the form is complete before submitting data to the database
    if (!(name.isEmpty() || email.isEmpty() || password.isEmpty() || telephone == 0 || addr1.isEmpty() || addr2.isEmpty())) {
 //   val user = User(0, name, email, password, telephone, addr1, addr2)

    //add the user if all the fields are filled
    //userViewModel.addUser(user)
    Toast.makeText(context, "User created successfully!", Toast.LENGTH_SHORT).show()

    //navigate back to our home fragment
    findNavController().navigate(R.id.action_manageAddress_to_mainMenu)
    } else {
    Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show()
    }
    }


    //get the time set
    override fun onTimeSet(TimePicker: TimePicker?, p1: Int, p2: Int) {
        Log.d("Fragment", "Time: $p1:$p2")

        cleanTime = Calculations.cleanTime(p1, p2)
        binding.txHour.text = "Time: $cleanTime"
    }

    //get the date set
    override fun onDateSet(p0: DatePicker?, yearX: Int, monthX: Int, dayX: Int) {

        cleanDate = Calculations.cleanDate(dayX, monthX, yearX)
        binding.txDate.text = "Date: $cleanDate"
    }

    //get the current time
    private fun getTimeCalendar() {
        val cal = Calendar.getInstance()
        hour = cal.get(Calendar.HOUR_OF_DAY)
        minute = cal.get(Calendar.MINUTE)
    }

    //get the current date
    private fun getDateCalendar() {
        val cal = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
    }
}
package com.dam.entregapp.ui.fragments.Statistics

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dam.entregapp.ui.ChartActivity
import com.dam.entregapp.location.LocationApp.Companion.prefs
import com.dam.entregapp.R
import com.dam.entregapp.databinding.FragmentStatisticsBinding
import com.dam.entregapp.service.StatisticsService
import com.dam.entregapp.ui.viewmodels.UserViewModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class StatisticsFragment : Fragment(R.layout.fragment_statistics) {

    private lateinit var statisticsService: StatisticsService
    private lateinit var userViewModel: UserViewModel

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        statisticsService = StatisticsService(userViewModel)

        binding.addr1Name.text = prefs.getPrimaryAddressName()
        binding.addr2Name.text = prefs.getSecondaryAddressName()
        binding.addr3Name.text = prefs.getThirdAddressName()
        binding.addr4Name.text = prefs.getFourthAddressName()

        binding.btnBorrar.setOnClickListener {
            borrarTracking()
        }

        binding.btnEnviar.setOnClickListener {
            submitStatistics()

            //TODO mejorar lugar
            Toast.makeText(context, "Estadísticas enviadas correctamente!", Toast.LENGTH_SHORT).show()

            //navigate back to our home fragment
            findNavController().navigate(R.id.action_statistics_to_mainMenu)
        }

        binding.btnChart.setOnClickListener {
            val intent = Intent(activity, ChartActivity::class.java)
            startActivity(intent)
        }
    }


    private fun submitStatistics() = CoroutineScope(Dispatchers.IO).launch {

        val statistics = statisticsService.getProcessedStatistics()
        val confirmacion = hashMapOf(
            "mainAddress" to prefs.getPrimaryAddressName(),
            "secondaryAddress" to prefs.getSecondaryAddressName(),
            "JSONStats" to statistics.toString(),
            "fecha" to FieldValue.serverTimestamp()
        )
        val db = Firebase.firestore
        val docRef = db.collection("users").document(prefs.getAuthID())

        docRef.collection("Statistics").document("Tracking").set(confirmacion)
    }

    private fun borrarTracking() {
        CoroutineScope(Dispatchers.IO).launch {
            userViewModel.deleteAllTrackingData()
        }
    }
}
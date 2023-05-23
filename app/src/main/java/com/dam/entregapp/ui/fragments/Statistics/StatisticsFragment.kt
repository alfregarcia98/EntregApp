package com.dam.entregapp.ui.fragments.Statistics

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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

        binding.btnBorrar.setOnClickListener {
            borrarTracking()
        }

        binding.btnEnviar.setOnClickListener {
            submitStatistics()
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


//Para evitar el error de cambiar la ui desde corrutina
/*fun Fragment?.runOnUiThread(action: () -> Unit) {
    this ?: return
    if (!isAdded) return // Fragment not attached to an Activity
    activity?.runOnUiThread(action)

    binding.btnBorrar.setOnClickListener {
        borrarTracking()
    }

    binding.btnEnviar.setOnClickListener {
        submitStatistics()
    }

    //TODO Query para agrupar por horas las ubicaciones
    //SELECT count(id), strftime('%H',date/1000, 'unixepoch','localtime') as hour from tracking_data_table group by hour;
    //TODO Query para agrupar por horas y por address_id las ubicaciones
    //SELECT address_id,  strftime('%H',date/1000, 'unixepoch','localtime') as hour, count(id) from tracking_data_table group by hour,address_id order by hour;

    //SELECT address_id,  strftime('%H',date/1000, 'unixepoch','localtime') as hour, count(id) as data_count from tracking_data_table group by hour,address_id order by hour;

}

    binding.addr1Name.text = prefs.getPrimaryAddressName()
    binding.addr2Name.text = prefs.getSecondaryAddressName()
    *//*        CoroutineScope(Dispatchers.IO).launch {

                    val trackingData = userViewModel.getTrackingData()

                    Log.d("Estadisticas", "TrackingData: $trackingData")


                    for (data in trackingData) {
                        Log.d(
                            "Estadisticas",
                            "Address ID: ${data.address_id}, Hour: ${data.hour}, Data Count: ${data.data_count}"
                        )
                    }
                    var main_count = trackingData[1].data_count
                    var second_count = trackingData[2].data_count
                    var total_count =
                        trackingData[0].data_count + trackingData[1].data_count + trackingData[2].data_count

                    if (total_count>0) {
                        porcentajePrincipal = ((main_count.toFloat() / total_count.toFloat()) * 100)
                        porcentajeSecundario = ((second_count.toFloat() / total_count.toFloat()) * 100)
                    }


        runOnUiThread {
            binding.count1Txt.text = main_count.toString()
            binding.count2Txt.text = second_count.toString()
            binding.count3Txt.text = total_count.toString()
            binding.porcentaje1Txt.text = porcentajePrincipal.toString()
            binding.porcentaje2Txt.text = porcentajeSecundario.toString()
        }*/
package com.dam.entregapp.ui.fragments.Statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dam.entregapp.LocationApp.Companion.prefs
import com.dam.entregapp.R
import com.dam.entregapp.databinding.FragmentStatisticsBinding
import com.dam.entregapp.ui.viewmodels.UserViewModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class Statistics : Fragment(R.layout.fragment_statistics) {

    private lateinit var userViewModel: UserViewModel
    val db = Firebase.firestore
    var porcentajePrincipal = 0f
    var porcentajeSecundario = 0f

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

        //Para evitar el error de cambiar la ui desde corrutina
        fun Fragment?.runOnUiThread(action: () -> Unit) {
            this ?: return
            if (!isAdded) return // Fragment not attached to an Activity
            activity?.runOnUiThread(action)

            binding.btnBorrar.setOnClickListener {
                borrarTracking()
            }

            binding.btnEnviar.setOnClickListener {
                submitStatistics()
            }


        }

        binding.addr1Name.text = prefs.getPrimaryAddressName()
        binding.addr2Name.text = prefs.getSecondaryAddressName()
        CoroutineScope(Dispatchers.IO).launch {
            //TODO Lo puedo hacer con un count query y así es mas eficiente
            val count1 =
                userViewModel.getTrackingWithAddrID(prefs.getPrimaryAddressID()).size
            val count2 =
                userViewModel.getTrackingWithAddrID(prefs.getSecondaryAddressID()).size
            val count3 = prefs.getTrackingCount()
            if (count3 != 0) {
                porcentajePrincipal = ((count1.toFloat() / count3.toFloat()) * 100)
                porcentajeSecundario = ((count2.toFloat() / count3.toFloat()) * 100)
            }
            //TODO Como añado la funcionalidad de las horas
            runOnUiThread {
                binding.count1Txt.text = count1.toString()
                binding.count2Txt.text = count2.toString()
                binding.count3Txt.text = count3.toString()

                binding.porcentaje1Txt.text = porcentajePrincipal.toString()
                binding.porcentaje2Txt.text = porcentajeSecundario.toString()
            }
        }

    }

    private fun submitStatistics() {

        var email = prefs.getEmail()
        db.collection("users").document(prefs.getEmail()).collection("Statistics")
            .document("Tracking")
            .set(
                hashMapOf(
                    "time" to FieldValue.serverTimestamp()
                )
            )

    }

    private fun borrarTracking() {
        CoroutineScope(Dispatchers.IO).launch {
            userViewModel.deleteAllTrackingData()
        }
        prefs.resetTrackingCounter()
    }

    //Para pasar de timeStamp a DateTime
    /*private fun getDateTime(s: String): String? {
        try {
            val sdf = SimpleDateFormat("MM/dd/yyyy")
            val netDate = Date(Long.parseLong(s) * 1000)
            return sdf.format(netDate)
        } catch (e: Exception) {
            return e.toString()
        }
    }*/
}
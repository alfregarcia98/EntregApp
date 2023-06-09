package com.dam.entregapp.ui.fragments.sincronizar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dam.entregapp.location.LocationApp.Companion.prefs
import com.dam.entregapp.R
import com.dam.entregapp.data.database.relations.UserWithAddress
import com.dam.entregapp.data.model.Address
import com.dam.entregapp.databinding.FragmentSyncBinding
import com.dam.entregapp.firestore.FirestoreAddress
import com.dam.entregapp.firestore.FirestoreDocument
import com.dam.entregapp.ui.MainActivity
import com.dam.entregapp.ui.viewmodels.UserViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SyncFragment : Fragment(R.layout.fragment_sync) {
    private lateinit var userViewModel: UserViewModel

    private var _binding: FragmentSyncBinding? = null
    private val binding get() = _binding!!

    private val db = Firebase.firestore
    private var lista: List<UserWithAddress> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSyncBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        binding.btnSubir.setOnClickListener {
            sync()
        }

        binding.btnRecuperar.setOnClickListener {
            restore()
        }

        setup()
    }

    private fun setup() = CoroutineScope(Dispatchers.IO).launch {
        binding.addr1Name.text = prefs.getPrimaryAddressName()
        binding.addr2Name.text = prefs.getSecondaryAddressName()
        binding.addr3Name.text = prefs.getThirdAddressName()
        binding.addr4Name.text = prefs.getFourthAddressName()

        lista = userViewModel.getUserWithAddress(prefs.getCurrentUserID())
        if (lista.isNotEmpty()) {
            requireActivity().runOnUiThread {
                binding.userTxt.text = lista[0].user.toString()
                binding.addressTxt.text = lista[0].addresses.toString()
            }
        }
    }


    private fun sync() = CoroutineScope(Dispatchers.IO).launch {
        try {
            if (lista.isNotEmpty()) {
                val docRef =
                    db.collection("users").document(prefs.getAuthID()).collection("Preferencias")
                        .document("Direcciones")
                docRef.set(object {
                    val addresses = lista[0].addresses
                })
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Log.d("Sync", "Error: ${e.message}")
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun restore() = CoroutineScope(Dispatchers.IO).launch {

        try {
            val querySnapshot =
                FirebaseFirestore.getInstance().collection("users").document(prefs.getAuthID())
                    .collection("Preferencias").document("Direcciones")

            querySnapshot.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document != null) {
                        val doc = document.toObject<FirestoreDocument>()
                        if (doc != null) {
                            val addresses = doc.addresses
                            userViewModel.deleteAllAddress()
                            var ids = 0
                            if (addresses != null) {
                                for (address in addresses) {
                                    Log.d("Restore", "$address")
                                    ids++
                                    addAddresToDB(address, ids)
                                }
                            }

                        }
                    }
                } else {
                    Log.d("Restore", task.exception!!.message!!) //Never ignore potential errors!
                }
            }
            showHome()

        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showHome() {
        val a = Intent(context, MainActivity::class.java)
        a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(a)
    }

    private fun addAddresToDB(address: FirestoreAddress, ids: Int) {
        val newAddress =
            Address(
                ids,
                address.user_id,
                address.name,
                address.start_hour,
                address.end_hour,
                address.lon,
                address.lat
            )

        lifecycleScope.launch {
            userViewModel.addAddress(newAddress)
        }

        Log.d("Restore", "Direccion a la base de datos local: $newAddress")

    }
}
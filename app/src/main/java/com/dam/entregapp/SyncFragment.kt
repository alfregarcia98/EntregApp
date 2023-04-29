package com.dam.entregapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dam.entregapp.LocationApp.Companion.prefs
import com.dam.entregapp.data.database.relations.UserWithAddress
import com.dam.entregapp.data.model.User
import com.dam.entregapp.databinding.FragmentManageSettingsBinding
import com.dam.entregapp.databinding.FragmentSyncBinding
import com.dam.entregapp.firestore.FirestoreAddresses
import com.dam.entregapp.firestore.FirestoreUser
import com.dam.entregapp.ui.viewmodels.MainViewModel
import com.dam.entregapp.ui.viewmodels.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SyncFragment : Fragment(R.layout.fragment_sync) {
    private lateinit var mainViewModel: MainViewModel

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

        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

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

        lista = mainViewModel.getUserWithAddress(prefs.getCurrentUserID())
        if (lista.isNotEmpty()) {
            binding.userTxt.text = lista[0].user.toString()
            binding.addressTxt.text = lista[0].addresses.toString()
        }
    }

    private fun sync() = CoroutineScope(Dispatchers.IO).launch {
        try {
            if (lista.isNotEmpty()) {
                val docRef = db.collection("users").document(prefs.getAuthID())
                docRef.collection("Preferencias").document("Lista").set(lista[0])
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun restore() = CoroutineScope(Dispatchers.IO).launch {

        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("users").document(prefs.getAuthID())
            .collection("Preferencias").document("Lista")

        docRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val myDataClass = documentSnapshot.get("addresses")?.let {
                    Log.d("Sync", "Devuelto solo addresses: $it")
                }
            } else {
                Log.d("Sync", "Document not found")
            }
        }.addOnFailureListener { exception ->
            Log.e("Sync", "Error getting document: ", exception)
        }



/*        try {
            val querySnapshot =
                FirebaseFirestore.getInstance().collection("users").document(prefs.getAuthID())
                    .collection("Preferencias").get().await()
            for (document in querySnapshot.documents) {
                //TODO no va lo del object porque no se hacerlo pero lista si se recupera bien
                val lista = document.data
                if (lista != null) {

                }
                Log.d("Sync", "Datos de lista: $lista")
            }
            withContext(Dispatchers.Main) {
                //Nada por ahora
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }
        }*/
    }

}
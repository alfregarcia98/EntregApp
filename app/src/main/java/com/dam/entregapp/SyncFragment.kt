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
import com.dam.entregapp.data.model.User
import com.dam.entregapp.databinding.FragmentManageSettingsBinding
import com.dam.entregapp.databinding.FragmentSyncBinding
import com.dam.entregapp.ui.viewmodels.UserViewModel

class SyncFragment : Fragment(R.layout.fragment_sync) {
    private lateinit var userViewModel: UserViewModel

    private var _binding: FragmentSyncBinding? = null
    private val binding get() = _binding!!

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



        binding.btnEnviar.setOnClickListener {
            sync()
        }

        setup()
    }

    private fun setup() {
        binding.addr1Name.text = LocationApp.prefs.getPrimaryAddressName()
        binding.addr2Name.text = LocationApp.prefs.getSecondaryAddressName()

        var users = userViewModel.getAllUsers
        Log.d("sync", "User: $users")
    }

    private fun sync() {
        TODO("Not yet implemented")
    }

}
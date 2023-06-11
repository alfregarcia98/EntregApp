package com.dam.entregapp.ui.fragments.settingsmenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dam.entregapp.R
import com.dam.entregapp.databinding.FragmentSettingsMenuBinding
import com.dam.entregapp.location.LocationApp
import com.google.firebase.auth.FirebaseAuth

class SettingsMenu : Fragment(R.layout.fragment_settings_menu) {

    private var _binding: FragmentSettingsMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsMenuBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.btnAddr.setOnClickListener {
            findNavController().navigate(R.id.action_mainMenu_to_manageAddress)
        }

        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_mainMenu_to_manageSettings)
        }

        binding.btnEstadisticas.setOnClickListener {
            findNavController().navigate(R.id.action_mainMenu_to_statistics)
        }

        binding.btnSync.setOnClickListener {
            findNavController().navigate(R.id.action_mainMenu_to_syncFragment)
        }

        binding.btLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            LocationApp.prefs.wipe()
            if (getActivity() != null) {
                getActivity()?.finishAffinity();

            }
        }
    }
}


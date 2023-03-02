package com.dam.entregapp.ui.fragments.mainmenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dam.entregapp.R
import com.dam.entregapp.databinding.FragmentMainMenuBinding

class MainMenu : Fragment(R.layout.fragment_main_menu) {

    private var _binding: FragmentMainMenuBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainMenuBinding.inflate(inflater, container, false)
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

        binding.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_mainMenu_to_loginFragment2)
        }

        binding.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_mainMenu_to_registerFragment)
        }

        binding.btnEstadisticas.setOnClickListener {
            findNavController().navigate(R.id.action_mainMenu_to_statistics)
        }
    }

}


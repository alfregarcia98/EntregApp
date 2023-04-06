package com.dam.entregapp.ui.fragments.scheduler

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dam.entregapp.R
import com.dam.entregapp.alarm.AlarmItem
import com.dam.entregapp.alarm.AndroidAlarmScheduler
import com.dam.entregapp.databinding.FragmentSchedulerBinding


class SchedulerFragment : Fragment(R.layout.fragment_scheduler) {

    private var _binding: FragmentSchedulerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSchedulerBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val scheduler = context?.let { AndroidAlarmScheduler(it) }
        var alarmItem: AlarmItem? = null

        binding.btnActivar.setOnClickListener {
            //AlarmScheduler

            alarmItem = AlarmItem("Alarma de detencion")
            scheduler!!::schedule?.let { it1 -> alarmItem?.let(it1) }

        }

        binding.btnDesactivar.setOnClickListener {
            scheduler!!::cancel?.let { it1 -> alarmItem?.let(it1) }
        }

    }
}
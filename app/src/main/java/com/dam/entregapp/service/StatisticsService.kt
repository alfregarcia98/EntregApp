package com.dam.entregapp.service

import android.util.Log
import com.dam.entregapp.logic.utils.ProcessedStatistics
import com.dam.entregapp.ui.viewmodels.UserViewModel

class StatisticsService(private val userViewModel: UserViewModel) {


    suspend fun getProcessedStatistics() : ProcessedStatistics {
        val trackingData = userViewModel.getTrackingData()

        val addressIds = trackingData.map { data -> data.address_id }
        Log.d("Chart", "Addresses: $addressIds")
        val uniqueAddressIds = addressIds.distinct()
        Log.d("Chart", "Unique addresses: $uniqueAddressIds")

        // since we only have 2 addresses for now
        val nonZeroAddresses = uniqueAddressIds.filter { address -> address != 0 }

        val startHour = 8
        val endHour = 22

        val statistics = ProcessedStatistics(startHour, endHour, 60)
        statistics.addressIds = nonZeroAddresses

        for (hour in startHour until endHour) {
            var data_point_count = 0
            for (address in uniqueAddressIds) {
                try {
                    val result =
                        trackingData.filter { data -> (data.address_id == address && data.hour.toInt() == hour) }
                            .first()
                    data_point_count += result.data_count
                } catch (e: NoSuchElementException) { }
            }

            for (address in nonZeroAddresses) {
                try {
                    val result =
                        trackingData.filter { data -> (data.address_id == address && data.hour.toInt() == hour) }
                            .first()

                    var main_count = result.data_count
                    var porcentaje = (main_count.toDouble()/data_point_count)*100
                    val label = porcentaje.toInt() / 10 + 1

                    addDataPoint(statistics, porcentaje)
                } catch (e: NoSuchElementException) {
                    addDataPoint(statistics, -1.0)
                }
            }
        }

        return statistics;
    }

    private fun addDataPoint(
        statistics: ProcessedStatistics,
        porcentaje: Double
    ) {
        if (statistics.data.last().size == 2) {
            statistics.data.add(ArrayList())
        }
        statistics.data.last().add(porcentaje)
    }
}
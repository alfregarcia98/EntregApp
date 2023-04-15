package com.dam.entregapp

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.HeatDataEntry
import com.anychart.enums.SelectionMode
import com.anychart.graphics.vector.SolidFill
import com.dam.entregapp.LocationApp.Companion.prefs
import com.dam.entregapp.ui.viewmodels.MainViewModel
import com.dam.entregapp.ui.viewmodels.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//TODO Como rellenarlo de manera dinamica con la informacion del usuario - Primero solucionar lo de estadisticas
class ChartActivity() : AppCompatActivity() {

    private lateinit var userViewModel: UserViewModel

    private val colorMap = mapOf(
        0 to "#ffffff",
        1 to "#cce7c9",
        2 to "#acd8a7",
        3 to "#8bca84",
        4 to "#72bf6a",
        5 to "#5bb450",
        6 to "#52a447",
        7 to "#46923c",
        8 to "#3b8132",
        9 to "#276221",
        10 to "#224A3E"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        val anyChartView = findViewById<AnyChartView>(R.id.any_chart_view)
        anyChartView.setProgressBar(findViewById<View>(R.id.progress_bar))
        val riskMap = AnyChart.heatMap()
        riskMap.stroke("1 #fff")
        riskMap.hovered()
            .stroke("6 #fff")
            .fill(SolidFill("#545f69", 1.0))
            .labels("{ fontColor: '#fff' }")
        riskMap.interactivity().selectionMode(SelectionMode.NONE)
        riskMap.title().enabled(true)
        riskMap.title()
            .text("Estadísticas usuario")
            .padding(0.0, 0.0, 20.0, 0.0)
        riskMap.labels().enabled(true)

        val labelListFunction = "function() {\n" +
                "      var namesList = [\"Sin datos\", \"0-10%\", \"10-20%\", \"20-30%\", \"30-40%\", \"40-50%\", \"50-60%\", \"60-70%\", \"70-80%\", \"80-90%\", \"90-100%\"];\n" +
                "      return namesList[this.heat];\n" +
                "    }"

        riskMap.labels()
            .minFontSize(14.0)
            .format(labelListFunction)
        riskMap.yAxis(0).stroke(null)
        riskMap.yAxis(0).labels().padding(0.0, 15.0, 0.0, 0.0)
        riskMap.yAxis(0).ticks(false)
        riskMap.xAxis(0).stroke(null)
        riskMap.xAxis(0).ticks(false)
        riskMap.tooltip().title().useHtml(true)
        riskMap.tooltip()
            .useHtml(true)
            .titleFormat((labelListFunction))
            .format(
                ("function () {\n" +
                        "       return '<span style=\"color: #CECECE\">Dirección: </span>' + this.x + '<br/>' +\n" +
                        "           '<span style=\"color: #CECECE\">Franja horaria: </span>' + this.y;\n" +
                        "   }")
            )


        CoroutineScope(Dispatchers.IO).launch {

            val trackingData = userViewModel.getTrackingData()
            var principal = prefs.getPrimaryAddressName()
            var secundaria = prefs.getSecondaryAddressName()

            //val result = trackingData.filter { data -> (data.address_id == 1 && data.hour == 14) }.first()

            Log.d("Chart", "TrackingData: $trackingData")

            for (data in trackingData) {
                Log.d(
                    "Estadisticas",
                    "Address ID: ${data.address_id}, Hour: ${data.hour}, Data Count: ${data.data_count}"
                )
            }

            val data: MutableList<DataEntry> = ArrayList()

            val startHour = 8
            val endHour = 22

            //Cuando había horas con un 0 inicial, como 08 y 09. He tenido que almacenar la hora como un string y luego a la hora de necesitar el numero como tal hacer un toInt().Cuando había horas con un 0 inicial, como 08 y 09. He tenido que almacenar la hora como un string y luego a la hora de necesitar el numero como tal hacer un toInt().
            //Por cada franja horaria
            for (hour in startHour until endHour) {
                val time_slot = "${hour}:00-${hour + 1}:00"

                // calculate total number of datapoints per timeslot
                var data_point_count = 0
                for (address in 0..2) {
                    try {
                        val result =
                            trackingData.filter { data -> (data.address_id == address && data.hour.toInt() == hour) }
                                .first()
                        data_point_count += result.data_count
                    } catch (e: NoSuchElementException) { }
                }

                //Por cada una de las direcciones
                for (address in 0..2) {

                    Log.d("loop", "Hora: $hour y Address: $address")

                    try {
                        val result =
                            trackingData.filter { data -> (data.address_id == address && data.hour.toInt() == hour) }
                                .first()

                        var count = result.data_count
                        Log.d("Result", "count: $count")

                        var main_count = result.data_count
                        var porcentaje = (main_count.toDouble()/data_point_count)*100
                        val label = porcentaje.toInt() / 10 + 1

                        Log.d("Porcentaje", "count: $porcentaje")

                        val color = getLinearColorHex(porcentaje)

                        if (result.address_id == 1) {
                            data.add(CustomHeatDataEntry("Principal", time_slot, label, color))
                        } else if (result.address_id == 2) {
                            data.add(CustomHeatDataEntry("Secundaria", time_slot, label, color))
                        }
                    } catch (e: NoSuchElementException) {
                        val color = getLinearColorHex(0.0)
                        if (address == 1) {
                            data.add(CustomHeatDataEntry("Principal", time_slot, 0, color))
                        } else if (address == 2) {
                            data.add(CustomHeatDataEntry("Secundaria", time_slot, 0, color))
                        }

                        println("Caught NoSuchElementException: ${e.message}")
                    }
                }
            }

            riskMap.data(data)

            runOnUiThread {
                anyChartView.setChart(riskMap)
            }
        }
    }

    fun getLinearColorHex(percent: Double): String {
        return colorMap[percent.toInt() / 10]!!

    }

    private inner class CustomHeatDataEntry internal constructor(
        x: String?,
        y: String?,
        heat: Int?,
        fill: String?
    ) :
        HeatDataEntry(x, y, heat) {
        init {
            setValue("fill", fill)
        }
    }
}
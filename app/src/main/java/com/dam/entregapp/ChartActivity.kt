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
import com.dam.entregapp.ui.viewmodels.MainViewModel
import com.dam.entregapp.ui.viewmodels.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//TODO Como rellenarlo de manera dinamica con la informacion del usuario - Primero solucionar lo de estadisticas
class ChartActivity() : AppCompatActivity() {

    private lateinit var userViewModel: UserViewModel

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
        riskMap.labels()
            .minFontSize(14.0)
            .format(
                "function() {\n" +
                        "      var namesList = [\"Sin datos\", \"Baja\", \"Media\", \"Alta\", \"Muy alta\"];\n" +
                        "      return namesList[this.heat];\n" +
                        "    }"
            )
        riskMap.yAxis(0).stroke(null)
        riskMap.yAxis(0).labels().padding(0.0, 15.0, 0.0, 0.0)
        riskMap.yAxis(0).ticks(false)
        riskMap.xAxis(0).stroke(null)
        riskMap.xAxis(0).ticks(false)
        riskMap.tooltip().title().useHtml(true)
        riskMap.tooltip()
            .useHtml(true)
            .titleFormat(
                ("function() {\n" +
                        "      var namesList = [\"Sin datos\", \"Baja\", \"Media\", \"Alta\", \"Muy alta\"];\n" +
                        "      return '<b>' + namesList[this.heat] + '</b> Probability';\n" +
                        "    }")
            )
            .format(
                ("function () {\n" +
                        "       return '<span style=\"color: #CECECE\">Dirección: </span>' + this.x + '<br/>' +\n" +
                        "           '<span style=\"color: #CECECE\">Franja horaria: </span>' + this.y;\n" +
                        "   }")
            )


        CoroutineScope(Dispatchers.IO).launch {

            val trackingData = userViewModel.getTrackingData()

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
            var total_count = 0

            //Cuando había horas con un 0 inicial, como 08 y 09. He tenido que almacenar la hora como un string y luego a la hora de necesitar el numero como tal hacer un toInt().Cuando había horas con un 0 inicial, como 08 y 09. He tenido que almacenar la hora como un string y luego a la hora de necesitar el numero como tal hacer un toInt().
            //Por cada franja horaria
            for (hour in startHour until endHour) {
                val time_slot = "${hour}:00-${hour + 1}:00"

                //Por cada una de las direcciones
                for (address in 0..2) {

                    Log.d("loop", "Hora: $hour y Address: $address")

                    try {
                        val result =
                            trackingData.filter { data -> (data.address_id == address && data.hour.toInt() == hour) }
                                .first()

                        var count = result.data_count
                        Log.d("Result", "count: $count")

                        var heat = 0
                        var main_count = result.data_count
                        total_count += main_count
                        var porcentaje = (main_count/total_count)*100

                        Log.d("Porcentaje", "count: $porcentaje")
                        if (result.address_id == 1) {
                            data.add(CustomHeatDataEntry("Principal", time_slot, 1, "#ef6c00"))
                        } else if (result.address_id == 2) {
                            data.add(CustomHeatDataEntry("Secundaria", time_slot, 1, "#ffb74d"))
                        }
                    } catch (e: NoSuchElementException) {
                        if (address == 1) {
                            data.add(CustomHeatDataEntry("Principal", time_slot, 0, "purple"))
                        } else if (address == 2) {
                            data.add(CustomHeatDataEntry("Secundaria", time_slot, 0, "purple"))
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
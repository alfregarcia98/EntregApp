package com.dam.entregapp.ui

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
import com.dam.entregapp.R
import com.dam.entregapp.service.StatisticsService
import com.dam.entregapp.ui.viewmodels.UserViewModel
import com.google.common.collect.Comparators.min
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChartActivity() : AppCompatActivity() {

    private lateinit var userViewModel: UserViewModel
    private lateinit var statisticsService: StatisticsService

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

    private val addressLabels = listOf("Principal", "Secundaria", "Tercera", "Cuarta")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        statisticsService = StatisticsService(userViewModel)

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
                "      var namesList = [\"N/A\", \"0-10%\", \"10-20%\", \"20-30%\", \"30-40%\", \"40-50%\", \"50-60%\", \"60-70%\", \"70-80%\", \"80-90%\", \"90-100%\"];\n" +
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
            val statistics = statisticsService.getProcessedStatistics()

            val data: MutableList<DataEntry> = ArrayList()

            var idx = 0
            for (hour in statistics.startHour until statistics.endHour) {
                // todo: use resolutionMin to construct time_slot string
                val timeSlot = "${hour}:00-${hour + 1}:00"

                if (statistics.data.size > idx && idx >= 0) {

                    val dataPointsForSlot = statistics.data.get(idx)

                    //Todo: Comprobar que no afecta en los distintos casos. En un principio si no hay datos no se añaden y punto. Si no hay datos de alguna intermedia se muestran erroneamente las direcciones
                    addHeatDataEntry(dataPointsForSlot, 0, data, timeSlot)
                    addHeatDataEntry(dataPointsForSlot, 1, data, timeSlot)
                    addHeatDataEntry(dataPointsForSlot, 2, data, timeSlot)
                    addHeatDataEntry(dataPointsForSlot, 3, data, timeSlot)
                    ++idx

                } else{
                    Log.d("Index", "Ha llegao aqui con indice: $idx")
                    Log.d("Index", "Ha llegao aqui con statistics: ${statistics.data}")
                }
            }
            Log.d("Index", "Data: ${data}")
            riskMap.data(data)

            runOnUiThread {
                anyChartView.setChart(riskMap)
            }
        }
    }

    private fun addHeatDataEntry(
        dataPointsForSlot: java.util.ArrayList<Double>,
        dataPointIndex: Int,
        data: MutableList<DataEntry>,
        timeSlot: String
    ) {


        if (dataPointsForSlot.size > dataPointIndex && dataPointIndex >= 0) {

            val dataPoint = dataPointsForSlot.get(dataPointIndex)

            if (dataPoint == -1.0) {
                data.add(
                    CustomHeatDataEntry(
                        addressLabels.get(dataPointIndex),
                        timeSlot,
                        0,
                        getLinearColorHex(0.0)
                    )
                )
            } else {
                val color = getLinearColorHex(dataPoint)
                val label = dataPoint.toInt() / 10 + 1

                data.add(
                    CustomHeatDataEntry(
                        addressLabels.get(dataPointIndex),
                        timeSlot,
                        min(label, 10),
                        color
                    )
                )
            }
        } else {
            // handle the case where dataPointIndex is out of bounds
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
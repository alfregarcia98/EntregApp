package com.dam.entregapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.HeatDataEntry
import com.anychart.enums.SelectionMode
import com.anychart.graphics.vector.SolidFill

class ChartActivity() : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)
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
                        "      var namesList = [\"Low\", \"Medium\", \"High\", \"Extreme\"];\n" +
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
                        "      var namesList = [\"Low\", \"Medium\", \"High\", \"Extreme\"];\n" +
                        "      return '<b>' + namesList[this.heat] + '</b> Probability';\n" +
                        "    }")
            )
            .format(
                ("function () {\n" +
                        "       return '<span style=\"color: #CECECE\">Dirección: </span>' + this.x + '<br/>' +\n" +
                        "           '<span style=\"color: #CECECE\">Franja horaria: </span>' + this.y;\n" +
                        "   }")
            )
        val data: MutableList<DataEntry> = ArrayList()
        data.add(CustomHeatDataEntry("Principal", "8:00-9:00", 0, "#90caf9"))
        data.add(CustomHeatDataEntry("Principal", "9:00-10:00", 0, "#90caf9"))
        data.add(CustomHeatDataEntry("Principal", "10:00-11:00", 0, "#90caf9"))
        data.add(CustomHeatDataEntry("Principal", "11:00-12:00", 0, "#90caf9"))
        data.add(CustomHeatDataEntry("Principal", "12:00-13:00", 0, "#90caf9"))
        data.add(CustomHeatDataEntry("Principal", "13:00-14:00", 0, "#90caf9"))
        data.add(CustomHeatDataEntry("Principal", "14:00-15:00", 0, "#90caf9"))
        data.add(CustomHeatDataEntry("Principal", "15:00-16:00", 0, "#90caf9"))
        data.add(CustomHeatDataEntry("Principal", "16:00-17:00", 0, "#90caf9"))
        data.add(CustomHeatDataEntry("Principal", "17:00-18:00", 3, "#d84315"))
        data.add(CustomHeatDataEntry("Secundaria", "8:00-9:00", 0, "#90caf9"))
        data.add(CustomHeatDataEntry("Secundaria", "9:00-10:00", 0, "#90caf9"))
        data.add(CustomHeatDataEntry("Secundaria", "10:00-11:00", 0, "#90caf9"))
        data.add(CustomHeatDataEntry("Secundaria", "11:00-12:00", 0, "#90caf9"))
        data.add(CustomHeatDataEntry("Secundaria", "12:00-13:00", 0, "#90caf9"))
        data.add(CustomHeatDataEntry("Secundaria", "13:00-14:00", 0, "#90caf9"))
        data.add(CustomHeatDataEntry("Secundaria", "14:00-15:00", 0, "#90caf9"))
        data.add(CustomHeatDataEntry("Secundaria", "15:00-16:00", 1, "#ffb74d"))
        data.add(CustomHeatDataEntry("Secundaria", "16:00-17:00", 1, "#ffb74d"))
        data.add(CustomHeatDataEntry("Secundaria", "17:00-18:00", 2, "#ef6c00"))
//        data.add(CustomHeatDataEntry("Possible", "Insignificant", 0, "#90caf9"))
//        data.add(CustomHeatDataEntry("Possible", "Minor", 0, "#90caf9"))
//        data.add(CustomHeatDataEntry("Possible", "Moderate", 1, "#ffb74d"))
//        data.add(CustomHeatDataEntry("Possible", "Major", 1, "#ffb74d"))
//        data.add(CustomHeatDataEntry("Possible", "Extreme", 1, "#ffb74d"))
//        data.add(CustomHeatDataEntry("Likely", "Insignificant", 0, "#90caf9"))
//        data.add(CustomHeatDataEntry("Likely", "Minor", 1, "#ffb74d"))
//        data.add(CustomHeatDataEntry("Likely", "Moderate", 1, "#ffb74d"))
//        data.add(CustomHeatDataEntry("Likely", "Major", 2, "#ef6c00"))
//        data.add(CustomHeatDataEntry("Likely", "Extreme", 2, "#ef6c00"))
//        data.add(CustomHeatDataEntry("Almost\\nCertain", "Insignificant", 0, "#90caf9"))
//        data.add(CustomHeatDataEntry("Almost\\nCertain", "Minor", 1, "#ffb74d"))
//        data.add(CustomHeatDataEntry("Almost\\nCertain", "Moderate", 1, "#ffb74d"))
//        data.add(CustomHeatDataEntry("Almost\\nCertain", "Major", 2, "#ef6c00"))
//        data.add(CustomHeatDataEntry("Almost\\nCertain", "Extreme", 3, "#d84315"))
        riskMap.data(data)
        anyChartView.setChart(riskMap)
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
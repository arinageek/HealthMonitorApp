package com.example.healthmonitorapp.ui.Data

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.map
import com.example.healthmonitorapp.R
import com.example.healthmonitorapp.database.Day
import com.example.healthmonitorapp.databinding.FragmentChartBinding
import com.github.aachartmodel.aainfographics.aachartcreator.*
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAMarker
import com.github.aachartmodel.aainfographics.aatools.AAGradientColor
import com.github.aachartmodel.aainfographics.aatools.AALinearGradientDirection
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ChartFragment : Fragment(R.layout.fragment_chart) {

    private val viewModel by viewModels<DataViewModel>()
    private val TAG = "ChartFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentChartBinding.bind(view)

        lateinit var aaChartModel: AAChartModel

        val stopsArr: Array<Any> = arrayOf(arrayOf(0.00, "#febc0f"), arrayOf(1.00, "#febc1f"))
        val stopsArr2: Array<Any> = arrayOf(arrayOf(0.00, "#F33c52"), arrayOf(1.00, "#F33c62"))
        val stopsArr3: Array<Any> = arrayOf(arrayOf(0.00, "#69d61c"), arrayOf(1.00, "#69d61c"))
        val gradientColorDic1 =
            AAGradientColor.linearGradient(AALinearGradientDirection.ToRight, stopsArr)
        val gradientColorDic2 =
            AAGradientColor.linearGradient(AALinearGradientDirection.ToRight, stopsArr2)
        val gradientColorDic3 =
            AAGradientColor.linearGradient(AALinearGradientDirection.ToRight, stopsArr3)

        val fillColor = "#FFFFFF"
        val lineColor = "#FFFFFF"

        fun createChart(days: List<Day>): AAChartModel {
            return AAChartModel()
                .chartType(AAChartType.Spline)
                .title("")
                .backgroundColor("#000000")
                .yAxisTitle("")
                .dataLabelsEnabled(false)
                .tooltipEnabled(true)
                .markerRadius(0f)
                .xAxisVisible(false)
                .yAxisVisible(false)
                .categories(days.map { day -> day.date }.toTypedArray())
                .series(
                    arrayOf(
                        AASeriesElement()
                            .name("Humidity")
                            .lineWidth(4f)
                            .data(
                                days.map { day ->
                                    if (day.feeling == 1) day.humidity
                                    else AADataElement().marker(
                                        AAMarker()
                                            .radius(5f)
                                            .symbol(AAChartSymbolType.Diamond.value)
                                            .fillColor(fillColor)
                                            .lineWidth(5f)
                                            .lineColor(lineColor)
                                    ).y(day.humidity.toFloat())
                                }.toTypedArray()
                            )
                            .color(gradientColorDic1),
                        AASeriesElement()
                            .name("Min temp")
                            .lineWidth(4f)
                            .data(
                                days.map { day ->
                                    if (day.feeling == 1) day.temp_min
                                    else AADataElement().marker(
                                        AAMarker()
                                            .radius(5f)
                                            .symbol(AAChartSymbolType.Diamond.value)
                                            .fillColor(fillColor)
                                            .lineWidth(5f)
                                            .lineColor(lineColor)
                                    ).y(day.temp_min.toFloat())
                                }.toTypedArray()
                            )
                            .color(gradientColorDic2),
                        AASeriesElement()
                            .name("Max temp")
                            .lineWidth(4f)
                            .data(
                                days.map { day ->
                                    if (day.feeling == 1) day.temp_max
                                    else AADataElement().marker(
                                        AAMarker()
                                            .radius(5f)
                                            .symbol(AAChartSymbolType.Diamond.value)
                                            .fillColor(fillColor)
                                            .lineWidth(5f)
                                            .lineColor(lineColor)
                                    ).y(day.temp_max.toFloat())
                                }.toTypedArray()
                            )
                            .color(gradientColorDic3)
                    )
                )
        }

        viewModel.days.observe(viewLifecycleOwner) { days ->
            if (days.isNotEmpty()) {
                aaChartModel = createChart(days)
                binding.chartView.aa_drawChartWithChartModel(aaChartModel)
            }
        }
    }
}
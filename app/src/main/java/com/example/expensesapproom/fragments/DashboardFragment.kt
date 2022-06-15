package com.example.expensesapproom.fragments

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.expensesapproom.R
import com.example.expensesapproom.data.viewmodel.ExpenseViewModel
import com.example.expensesapproom.data.viewmodelfactory.ExpenseViewModelFactory
import com.example.expensesapproom.databinding.FragmentDashboardBinding
import com.example.expensesapproom.utils.TransformingDateUtil
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import kotlinx.android.synthetic.main.fragment_dashboard.*
import java.text.SimpleDateFormat
import java.util.*


class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var expenseViewModel: ExpenseViewModel

    private lateinit var itemSelectedOnSpinner: String
    private val myCalender = Calendar.getInstance()
    private var year = myCalender.get(Calendar.YEAR)
    private var month = myCalender.get(Calendar.MONTH)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDashboardBinding.inflate(inflater,container,false)

        expenseViewModel = ViewModelProvider(requireActivity(), ExpenseViewModelFactory(requireActivity().application)).get(ExpenseViewModel::class.java)

        itemSelectedOnSpinner = TransformingDateUtil.transformingDateFromIntToString(month,year).toString()
        itemSelectedOnSpinner = TransformingDateUtil.transformingSpinnerInputToDate(itemSelectedOnSpinner)

        binding.toolbarDashboardFragment.title = "Dashboard"

        //toolbar click listener
        binding.toolbarDashboardFragment.setOnMenuItemClickListener { menuItem ->
            navigateToAddFragment(menuItem)
        }

        //Creating LineChart
        val dataXAxis: List<String?> = createDataForTheXAxis().reversed()  // Apr / 22, Mar / 22, Feb / 22, Jan / 22, Dec / 21
        val dataYAxis: List<Float> = createDataForTheYAxis()

        val lineDataSet = createMonthlyDataLineChart(dataYAxis)
        val _lineDataSet = MutableLiveData(lineDataSet)

        _lineDataSet.value = lineDataSet
        lineDataSet.apply {
            lineWidth = 2f
            setDrawValues(false)
            //valueTextSize = 12f
            isHighlightEnabled = true
            setDrawHighlightIndicators(false)
            setDrawCircles(false)
            mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        }

        binding.lineChart.data = LineData(_lineDataSet.value)
        setPropertiesLineChart(dataXAxis, dataYAxis)

        //creating and setting the data for the spinner Pie Chart
        val spinnerList = TransformingDateUtil.creatingDataForTheSpinner(month,year)
        val arrayAdapter = ArrayAdapter(requireActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,spinnerList)
        binding.spinnerDashboard.adapter = arrayAdapter

        //reading the info from spinner
        binding.spinnerDashboard.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {

                itemSelectedOnSpinner = adapterView?.getItemAtPosition(position).toString()
                itemSelectedOnSpinner = TransformingDateUtil.transformingSpinnerInputToDate(itemSelectedOnSpinner)

                val queryDate = "%${itemSelectedOnSpinner}%"
                expenseViewModel.getTotalAmountByMonthLive(queryDate).observe(viewLifecycleOwner, Observer { sum ->
                    binding.tvTotalSelectedMonth.text = "Total: $sum €"
                })

                val pieDataSet = updatingPieChartByChangingSpinner()
                val _pieDataSet = MutableLiveData(pieDataSet)
                val colorList = mutableListOf<Int>(Color.RED, Color.YELLOW, Color.CYAN, Color.GREEN, Color.MAGENTA, Color.LTGRAY)
                pieDataSet.colors = colorList
                _pieDataSet.value = pieDataSet

                pieDataSet.valueTextSize = 16f
                pieDataSet.valueFormatter = PercentFormatter(pieChart)

                setPropertiesPieChart()

                binding.pieChart.data = PieData(_pieDataSet.value)
                binding.pieChart.invalidate()

            }
            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                // blank override. There is always something selected
            }
        }

        return binding.root
    }

    private fun navigateToAddFragment(menuItem: MenuItem): Boolean{
        return when (menuItem.itemId) {
            R.id.addIcon -> {
                // Navigate to add screen
                findNavController().navigate(R.id.action_dashboardFragment_to_addFragment)
                true
            }
            else -> false
        }
    }

    private fun createDataForTheXAxis(): MutableList<String?>{
        /** Creating the mutableListOf Month/Year **/
        val calendar = Calendar.getInstance()
        var month = (calendar.get(Calendar.MONTH)) /** January = 0, February = 1 etc... **/
        var year = calendar.get(Calendar.YEAR)

        val spinnerlist = mutableListOf<String?>()
        for (i in 0..9){
            if(month == 0){
                spinnerlist.add(TransformingDateUtil.transformingDateFromIntToString(month,year))
                month = 12
                year -= 1
            }else{
                spinnerlist.add(TransformingDateUtil.transformingDateFromIntToString(month,year))
            }
            month -= 1
        }
        return spinnerlist
    }

    private fun createDataForTheYAxis(): MutableList<Float>{
        val totalAmountListByMonth = mutableListOf<Float>()
        val dates = createDynamicListOfMonths().reversed()

        for (i in 0..9){
            val query = "%${dates[i]}%"
            totalAmountListByMonth.add(expenseViewModel.getTotalAmountByMonth(query).toFloat())
        }

        return totalAmountListByMonth
    }

    private fun createDynamicListOfMonths(): MutableList<String>{
        val monthList = mutableListOf<String>()

        val formatter = SimpleDateFormat("MM-yyyy")
        val myCalender = Calendar.getInstance()
        var year = myCalender.get(Calendar.YEAR)
        var month = myCalender.get(Calendar.MONTH)

        for(i in 0..9){
            if(month == 0){
                val dateToFormat = "${month+1}-$year"
                val formattedDate = formatter.parse(dateToFormat)
                val desiredFormat = SimpleDateFormat("MM-yyyy").format(formattedDate)
                monthList.add(desiredFormat)

                month = 12
                year--
            }else{
                val dateToFormat = "${month+1}-$year"
                val formattedDate = formatter.parse(dateToFormat)
                val desiredFormat = SimpleDateFormat("MM-yyyy").format(formattedDate)
                monthList.add(desiredFormat)
            }
            month--
        }
        return monthList
    }

    private fun createMonthlyDataLineChart(dataYAxis: List<Float>): LineDataSet {
        val monthlyData = mutableListOf<Entry>()
        monthlyData.add(Entry(0f, dataYAxis[0])) // 9 Months ago
        monthlyData.add(Entry(1f, dataYAxis[1])) // 8 Months ago
        monthlyData.add(Entry(2f, dataYAxis[2])) // etc..
        monthlyData.add(Entry(3f, dataYAxis[3]))
        monthlyData.add(Entry(4f, dataYAxis[4]))
        monthlyData.add(Entry(5f, dataYAxis[5]))
        monthlyData.add(Entry(6f, dataYAxis[6]))
        monthlyData.add(Entry(7f, dataYAxis[7]))
        monthlyData.add(Entry(8f, dataYAxis[8]))
        monthlyData.add(Entry(9f, dataYAxis[9]))

        return LineDataSet(monthlyData, "monthly Overview")
    }

    private fun setPropertiesLineChart(dataXAxis: List<String?>, dataYAxis: List<Float>){
        var color = Color.BLACK
        when (context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {color = Color.WHITE}
            Configuration.UI_MODE_NIGHT_NO -> {color = Color.BLACK}
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {Color.RED}
        }

        if(dataYAxis.sum() == 0.0f){
            binding.lineChart.axisLeft.axisMaximum = 500f
            binding.lineChart.axisLeft.axisMinimum = 0f
        }

        binding.lineChart.apply {
            invalidate()
            axisRight.isEnabled = false
            xAxis.isGranularityEnabled = true
            xAxis.granularity = 0f
            xAxis.setDrawGridLines(false)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.valueFormatter = IndexAxisValueFormatter(dataXAxis)
            xAxis.textColor = color
            xAxis.axisLineColor = color
            animateX(500)
            axisLeft.setDrawGridLines(false)
            axisLeft.axisLineColor = color
            axisLeft.textColor = color
            setTouchEnabled(false)
            isDragEnabled = false
            setScaleEnabled(false)
            setPinchZoom(false)
            description = null
            legend.isEnabled = false
        }
    }

    private fun updatingPieChartByChangingSpinner(): PieDataSet{
        val totalAmountByCategoryAndMonthData = mutableListOf<PieEntry>()
        val mapOfDataPieChart = creatingDataForThePieChart()

        for(item in mapOfDataPieChart){
            totalAmountByCategoryAndMonthData.add(PieEntry(item.key,item.value))
        }

        val pieDataSet = PieDataSet(totalAmountByCategoryAndMonthData,"Categories")
        return pieDataSet
    }

    private fun creatingDataForThePieChart(): Map<Float,String>{
        var totalAmountMapByCategoryAndDate = mutableMapOf<Float,String>()
        val listOfCategory = creatingListOfCategories()
        val queryDate = "%$itemSelectedOnSpinner%"

        for (category in listOfCategory){
            if(!expenseViewModel.getTotalAmountByCategoryAndDate(category,queryDate).equals(0.0)){
                totalAmountMapByCategoryAndDate.put(expenseViewModel.getTotalAmountByCategoryAndDate(category,queryDate).toFloat(), category)
            }
        }
        return totalAmountMapByCategoryAndDate
    }

    private fun creatingListOfCategories(): List<String> {
        return listOf("Rent", "Grocery Shopping", "Restaurant", "Entertainment", "Clothes", "Petrol")
    }

    private fun setPropertiesPieChart(){
        binding.pieChart.setEntryLabelTextSize(20f)
        binding.pieChart.setDrawEntryLabels(false)
        //binding.pieChart.setEntryLabelColor(Color.TRANSPARENT)
        binding.pieChart.holeRadius = 45f
        binding.pieChart.transparentCircleRadius = 45f
        binding.pieChart.setHoleColor(Color.TRANSPARENT)
        binding.pieChart.centerText = "Categories"
        binding.pieChart.setCenterTextSize(20f)
        binding.pieChart.isDragDecelerationEnabled = false
        binding.pieChart.setTouchEnabled(false)
        binding.pieChart.setUsePercentValues(true)
        binding.pieChart.animateY(500)
        binding.pieChart.description.isEnabled = false

        when (context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {binding.pieChart.legend.textColor =  Color.WHITE
                binding.pieChart.setCenterTextColor(Color.WHITE)
                binding.tvTotalSelectedMonth.setTextColor(Color.WHITE)}
            Configuration.UI_MODE_NIGHT_NO -> {binding.pieChart.legend.textColor = Color.BLACK
                binding.pieChart.setCenterTextColor(Color.BLACK)
                binding.tvTotalSelectedMonth.setTextColor(Color.BLACK)}
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {binding.pieChart.legend.textColor = Color.BLACK
                binding.pieChart.setCenterTextColor(Color.BLACK)
                binding.tvTotalSelectedMonth.setTextColor(Color.BLACK)}
        }
        binding.pieChart.legend.textSize = 15f
        binding.pieChart.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        binding.pieChart.legend.orientation = Legend.LegendOrientation.HORIZONTAL
        binding.pieChart.legend.isWordWrapEnabled = true
        binding.pieChart.legend.verticalAlignment
        binding.pieChart.legend.xEntrySpace = 20f
    }

}
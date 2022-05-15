package com.example.expensesapproom.fragments

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.expensesapproom.*
import com.example.expensesapproom.data.viewmodel.ExpenseViewModel
import com.example.expensesapproom.data.viewmodelfactory.ExpenseViewModelFactory
import com.example.expensesapproom.databinding.FragmentDashboardBinding
import com.example.expensesapproom.utils.TransformingDateUtil
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
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
        binding.toolbarDashboardFragment.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.addIcon -> {
                    // Navigate to add screen
                    findNavController().navigate(R.id.action_dashboardFragment_to_addFragment)
                    true
                }
                else -> false
            }
        }

        //Creating LineChart
        var dataXAxis: List<String?> = creatingDataForTheXAxis().reversed()  // Apr / 22, Mar / 22, Feb / 22, Jan / 22, Dec / 21
        var dataYAxis: List<Float> = creatingDataForTheYAxis()

        val monthlyData = mutableListOf<Entry>()
        monthlyData.add(Entry(0f,dataYAxis[0])) // 9 Months ago
        monthlyData.add(Entry(1f,dataYAxis[1])) // 8 Months ago
        monthlyData.add(Entry(2f,dataYAxis[2])) // etc..
        monthlyData.add(Entry(3f,dataYAxis[3]))
        monthlyData.add(Entry(4f,dataYAxis[4]))
        monthlyData.add(Entry(5f,dataYAxis[5]))
        monthlyData.add(Entry(6f,dataYAxis[6]))
        monthlyData.add(Entry(7f,dataYAxis[7]))
        monthlyData.add(Entry(8f,dataYAxis[8]))
        monthlyData.add(Entry(9f,dataYAxis[9]))

        val lineDataSet = LineDataSet(monthlyData, "monthly Overview")
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

        binding.lineChart.data = LineData(_lineDataSet.value)
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
            axisLeft.setDrawGridLines(false)
            axisLeft.axisLineColor = color
            axisLeft.textColor = color
//            axisLeft.axisMaximum = 10f
//            axisLeft.axisMinimum = 0f
            setTouchEnabled(false)
            isDragEnabled = false
            setScaleEnabled(false)
            setPinchZoom(false)
            description = null
            legend.isEnabled = false
        }

        //Creating Pie Chart
//        val totalAmountByCategoryAndMonthData = mutableListOf<PieEntry>()
//        val listOfDataPieChart = creatingDataForThePieChart()
//
//        totalAmountByCategoryAndMonthData.add(PieEntry(listOfDataPieChart[0],"Rent"))
//        totalAmountByCategoryAndMonthData.add(PieEntry(listOfDataPieChart[1],"Grocery Shopping"))
//        totalAmountByCategoryAndMonthData.add(PieEntry(listOfDataPieChart[2],"Restaurant"))
//        totalAmountByCategoryAndMonthData.add(PieEntry(listOfDataPieChart[3],"Entertainment"))
//        totalAmountByCategoryAndMonthData.add(PieEntry(listOfDataPieChart[4],"Clothes"))
//        totalAmountByCategoryAndMonthData.add(PieEntry(listOfDataPieChart[5],"Petrol"))
//
//        val pieDataSet = PieDataSet(totalAmountByCategoryAndMonthData,"Categories")
//        val _pieDataSet = MutableLiveData(pieDataSet)
//
//        val colorList = mutableListOf<Int>(Color.RED, Color.YELLOW, Color.CYAN, Color.GREEN, Color.MAGENTA, Color.LTGRAY)
//        pieDataSet.colors = colorList
//
//        _pieDataSet.value = pieDataSet
//
//
//        binding.pieChart.data = PieData(_pieDataSet.value)
//        binding.pieChart.invalidate()
//        //enable/disable the label of the entries, color of the entries
//        binding.pieChart.setDrawEntryLabels(true)
//        binding.pieChart.setEntryLabelColor(Color.BLACK)
//        //binding.pieChart.setEntryLabelTextSize(16f)
//        // binding.pieChart.setUsePercentValues(true) //don't know exactly what it does
//        binding.pieChart.centerText = "Categories"
//        binding.pieChart.setCenterTextSize(20f)
//        //binding.pieChart.centerTextRadiusPercent = 100f // how much space the text in the center occupies
//        //binding.pieChart.isInTouchMode
//        binding.pieChart.isDragDecelerationEnabled = false

        //creating and setting the data for the spinner Pie Chart
        var spinnerList = TransformingDateUtil.creatingDataForTheSpinner(month,year)
        var arrayAdapter = ArrayAdapter(requireActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,spinnerList)
        binding.spinnerDashboard.adapter = arrayAdapter

        //reading the info from spinner
        binding.spinnerDashboard.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {

                itemSelectedOnSpinner = adapterView?.getItemAtPosition(position).toString()
                itemSelectedOnSpinner = TransformingDateUtil.transformingSpinnerInputToDate(itemSelectedOnSpinner)
                var pieDataSet = updatingPieChartByChangingSpinner()
                val _pieDataSet = MutableLiveData(pieDataSet)
                val colorList = mutableListOf<Int>(Color.RED, Color.YELLOW, Color.CYAN, Color.GREEN, Color.MAGENTA, Color.LTGRAY)
                pieDataSet.colors = colorList
                _pieDataSet.value = pieDataSet
                binding.pieChart.data = PieData(_pieDataSet.value)
                binding.pieChart.invalidate()
                //enable/disable the label of the entries, color of the entries
                binding.pieChart.setEntryLabelTextSize(20f)
                binding.pieChart.setDrawEntryLabels(false)
                binding.pieChart.setEntryLabelColor(Color.BLACK)
                binding.pieChart.centerText = "Categories"
                binding.pieChart.setCenterTextSize(20f)
                binding.pieChart.isDragDecelerationEnabled = false

                binding.pieChart.description.isEnabled = false
                binding.pieChart.legend.textSize = 14f
                when (context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
                    Configuration.UI_MODE_NIGHT_YES -> {binding.pieChart.legend.textColor =  Color.WHITE}
                    Configuration.UI_MODE_NIGHT_NO -> {binding.pieChart.legend.textColor = Color.BLACK}
                    Configuration.UI_MODE_NIGHT_UNDEFINED -> {binding.pieChart.legend.textColor = Color.BLACK}
                }
                binding.pieChart.legend.isWordWrapEnabled = true
                binding.pieChart.legend.verticalAlignment
                binding.pieChart.legend.xEntrySpace = 36f
                binding.pieChart.legend.yEntrySpace = 36f
            }
            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                // blank override. There is always something selected
            }
        }

        return binding.root
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
            if(expenseViewModel.getTotalAmountByCategoryAndDate(category,queryDate).toFloat().equals(0.0)){
                // do nothing
            }else{
                totalAmountMapByCategoryAndDate.put(expenseViewModel.getTotalAmountByCategoryAndDate(category,queryDate).toFloat(), category)
            }
        }

        return totalAmountMapByCategoryAndDate
    }

    private fun creatingListOfCategories(): List<String>{
        val listOfCategories = listOf<String>("Rent","Grocery Shopping","Restaurant","Entertainment","Clothes","Petrol")
        return listOfCategories
    }

    private fun creatingDataForTheYAxis(): MutableList<Float>{
        var totalAmountListByMonth = mutableListOf<Float>()
        //TODO create the data dynamically
        val dates = createDynamicListOfMonths().reversed()
        //val dates : List<String> = mutableListOf("07-2021","08-2021","09-2021","10-2021","11-2021","12-2021","01-2022","02-2022","03-2022","04-2022")

        for (i in 0..9){
            val query = "%${dates[i]}%"
            totalAmountListByMonth.add(expenseViewModel.getTotalAmountByMonth(query).toFloat())
        }

        return totalAmountListByMonth
    }

    private fun createDynamicListOfMonths(): MutableList<String>{
        var monthList = mutableListOf<String>()

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

    private fun creatingDataForTheXAxis(): MutableList<String?>{
        /** Creating the mutableListOf Month/Year **/
        val calendar = Calendar.getInstance()
        var month = (calendar.get(Calendar.MONTH)) /** January = 0, February = 1 etc... **/
        var year = calendar.get(Calendar.YEAR)

        var spinnerlist = mutableListOf<String?>()
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

}
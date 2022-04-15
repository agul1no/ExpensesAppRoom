package com.example.expensesapproom.fragments

import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColor
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.expensesapproom.R
import com.example.expensesapproom.TransformingDate
import com.example.expensesapproom.creatingDataForTheSpinner
import com.example.expensesapproom.data.viewmodel.ExpenseViewModel
import com.example.expensesapproom.data.viewmodelfactory.ExpenseViewModelFactory
import com.example.expensesapproom.databinding.FragmentDashboardBinding
import com.example.expensesapproom.databinding.FragmentHomeBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.R.attr.*
import kotlinx.android.synthetic.main.fragment_dashboard.*
import java.security.KeyStore
import java.text.SimpleDateFormat
import java.util.*


class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var expenseViewModel: ExpenseViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDashboardBinding.inflate(inflater,container,false)

        val myCalender = Calendar.getInstance()
        var year = myCalender.get(Calendar.YEAR)
        var month = myCalender.get(Calendar.MONTH)
        expenseViewModel = ViewModelProvider(requireActivity(), ExpenseViewModelFactory(requireActivity().application)).get(ExpenseViewModel::class.java)

        binding.toolbarDashboardFragment.setTitle("Dashboard")

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

        val dayData = mutableListOf<Entry>()
        dayData.add(Entry(0f,dataYAxis[0])) // 9 Months ago
        dayData.add(Entry(1f,dataYAxis[1])) // 8 Months ago
        dayData.add(Entry(2f,dataYAxis[2])) // etc..
        dayData.add(Entry(3f,dataYAxis[3]))
        dayData.add(Entry(4f,dataYAxis[4]))
        dayData.add(Entry(5f,dataYAxis[5]))
        dayData.add(Entry(6f,dataYAxis[6]))
        dayData.add(Entry(7f,dataYAxis[7]))
        dayData.add(Entry(8f,dataYAxis[8]))
        dayData.add(Entry(9f,dataYAxis[9]))

        val lineDataSet = LineDataSet(dayData, "monthly Overview")
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
        val data = mutableListOf<PieEntry>()
        var pieDataSet = PieDataSet(data,"Categories")
        val _pieDataSet = MutableLiveData(pieDataSet)
        data.add(PieEntry(480f,"Rent"))
        data.add(PieEntry(250f,"Grocery Shopping"))
        data.add(PieEntry(100f,"Restaurant"))
        data.add(PieEntry(100f,"Entertainment"))
        data.add(PieEntry(50f,"Clothes"))
        data.add(PieEntry(100f,"Petrol"))

        val colorList = mutableListOf<Int>(Color.RED, Color.YELLOW, Color.CYAN, Color.GREEN, Color.MAGENTA, Color.LTGRAY)
        pieDataSet.colors = colorList

        _pieDataSet.value = pieDataSet


        binding.pieChart.data = PieData(_pieDataSet.value)
        binding.pieChart.invalidate()
        //enable/disable the label of the entries, color of the entries
        binding.pieChart.setDrawEntryLabels(true)
        binding.pieChart.setEntryLabelColor(Color.BLACK)
        //binding.pieChart.setEntryLabelTextSize(16f)
        // binding.pieChart.setUsePercentValues(true) //don't know exactly what it does
        binding.pieChart.centerText = "Categories"
        binding.pieChart.setCenterTextSize(20f)
        //binding.pieChart.centerTextRadiusPercent = 100f // how much space the text in the center occupies
        //binding.pieChart.isInTouchMode
        binding.pieChart.isDragDecelerationEnabled = false

        //creating and setting the data for the spinner
        var spinnerList = creatingDataForTheSpinner()
        var arrayAdapter = ArrayAdapter(requireActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,spinnerList)
        binding.spinnerDashboard.adapter = arrayAdapter

        return binding.root
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
                spinnerlist.add(transformingDateFromIntToString(month,year))
                month = 12
                year -= 1
            }else{
                spinnerlist.add(transformingDateFromIntToString(month,year))
            }
            month -= 1
        }
        return spinnerlist
    }

    private fun transformingDateFromIntToString(month: Int, year: Int): String? {
        var monthString: String? = null
        var yearString: String? = null
        when(month){
            0 -> monthString = "Jan"
            1 -> monthString = "Feb"
            2 -> monthString = "Mar"
            3 -> monthString = "Apr"
            4 -> monthString = "Mai"
            5 -> monthString = "Jun"
            6 -> monthString = "Jul"
            7 -> monthString = "Aug"
            8 -> monthString = "Sep"
            9 -> monthString = "Oct"
            10 -> monthString = "Nov"
            11 -> monthString = "Dec"
        }
        when(year){
            2021 -> yearString = "21"
            2022 -> yearString = "22"
            2023 -> yearString = "23"
            2024 -> yearString = "24"
            2025 -> yearString = "25"
            2026 -> yearString = "26"
            2027 -> yearString = "27"
            2028 -> yearString = "28"
            2029 -> yearString = "29"
            2030 -> yearString = "30"
        }
        return "$monthString / $yearString"
    }

}
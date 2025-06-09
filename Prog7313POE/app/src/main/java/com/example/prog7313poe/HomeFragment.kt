package com.example.prog7313poe

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.prog7313poe.Database.Budgets.BudgetData
import com.example.prog7313poe.Database.Budgets.FirebaseBudgetDbHelper
import com.example.prog7313poe.Database.Expenses.ExpenseData
import com.example.prog7313poe.Database.Expenses.FirebaseExpenseDbHelper
import com.example.prog7313poe.databinding.FragmentHomeBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // You'll need these helpers to pull your real data:
    private val budgetDb = FirebaseBudgetDbHelper
    private val expenseDb = FirebaseExpenseDbHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1) Fetch budgets & expenses in parallel
        budgetDb.getAllBudgets { budgets ->
            expenseDb.getAllExpenses { expenses ->
                showChart(budgets, expenses)
            }
        }
    }

    private fun showChart(budgets: List<BudgetData>, expenses: List<ExpenseData>) {
        // 2) Build category list from your budgets (or your category node)
        val categories = budgets.map { it.budgetCategory }.distinct()

        // 3) Sum spending per category within each budget’s date range
        val spending = categories.map { cat ->
            expenses
                .filter { it.expenseCategory == cat }
                .sumOf { it.expenseAmount }
                .toFloat()
        }

        // 4) Optionally pull min/max goals per category (here we just use overall min/max)
        val allMax = budgets.map { it.budgetMaxAmount.toFloat() }
        val allMin = budgets.map { it.budgetMinAmount.toFloat() }
        val minGoal = allMin.minOrNull() ?: 0f
        val maxGoal = allMax.maxOrNull() ?: 0f

        // 5) Now copy your MPChart setup:
        val chart = binding.barChart
        val entries = spending.mapIndexed { i, v -> BarEntry(i.toFloat(), v) }
        val dataSet = BarDataSet(entries, "Spent per Category").apply {
            colors = ColorTemplate.MATERIAL_COLORS.toList()
        }
        val data = BarData(dataSet).apply { barWidth = 0.9f }

        chart.apply {
            this.data = data
            setFitBars(true)
            description.isEnabled = false
            axisRight.isEnabled = false

            // limit lines
            axisLeft.apply {
                removeAllLimitLines()
                addLimitLine(LimitLine(minGoal, "Min Goal").apply { lineColor = Color.RED })
                addLimitLine(LimitLine(maxGoal, "Max Goal").apply { lineColor = Color.GREEN })
            }

            xAxis.apply {
                granularity = 1f
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = IndexAxisValueFormatter(categories)
            }

            invalidate() // refresh
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

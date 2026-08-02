package com.example.saveurself.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saveurself.data.Category
import com.example.saveurself.data.Expense
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

class ExpenseViewModel : ViewModel() {
    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()

    init {
        // Seed with sample data
        _expenses.value = listOf(
            Expense("Lunch at Cafe", 450.0, Category.Food, LocalDate.now()),
            Expense("Uber Ride", 1200.0, Category.Transport, LocalDate.now().minusDays(1)),
            Expense("Vitamins", 800.0, Category.Health, LocalDate.now().minusDays(2)),
            Expense("Grocery Shopping", 2000.0, Category.Shopping, LocalDate.now().minusDays(3)),
            Expense("Coffee", 150.0, Category.Food, LocalDate.now().minusDays(4))
        )
    }

    val totalSpent: StateFlow<Double> = _expenses
        .map { list -> list.sumOf { it.amount } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun addExpense(description: String, amount: Double, category: Category) {
        val newExpense = Expense(description, amount, category, LocalDate.now())
        _expenses.value = listOf(newExpense) + _expenses.value
    }

    fun deleteExpense(expense: Expense) {
        _expenses.value = _expenses.value.filter { it.id != expense.id }
    }
}

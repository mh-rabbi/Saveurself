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

    private val _selectedCategory = MutableStateFlow<String?>("All")
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _selectedDate = MutableStateFlow<LocalDate>(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    init {
        // Seed with sample data
        _expenses.value = listOf(
            Expense("Lunch at Cafe", 450.0, Category.Food, LocalDate.now()),
            Expense("Uber Ride", 1200.0, Category.Transport, LocalDate.now().minusDays(1)),
            Expense("Vitamins", 800.0, Category.Health, LocalDate.now().minusDays(2)),
            Expense("Grocery Shopping", 2000.0, Category.Shopping, LocalDate.now().minusDays(3)),
            Expense("Coffee", 150.0, Category.Food, LocalDate.now().minusDays(4)),
            Expense("Dinner Out", 1200.0, Category.Food, LocalDate.now())
        )
    }

    val filteredExpenses: StateFlow<List<Expense>> = kotlinx.coroutines.flow.combine(
        _expenses,
        _selectedCategory
    ) { expenses, category ->
        val currentMonth = LocalDate.now().month
        val currentYear = LocalDate.now().year
        val monthExpenses = expenses.filter { it.date.month == currentMonth && it.date.year == currentYear }
        
        if (category == null || category == "All") {
            monthExpenses
        } else {
            monthExpenses.filter { it.category.name == category }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expensesForSelectedDate: StateFlow<List<Expense>> = kotlinx.coroutines.flow.combine(
        _expenses,
        _selectedDate
    ) { expenses, date ->
        expenses.filter { it.date == date }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalSpentForSelectedDate: StateFlow<Double> = expensesForSelectedDate
        .map { list -> list.sumOf { it.amount } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalSpent: StateFlow<Double> = filteredExpenses
        .map { list -> list.sumOf { it.amount } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun selectCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun addExpense(description: String, amount: Double, category: Category, date: LocalDate = LocalDate.now()) {
        val newExpense = Expense(description, amount, category, date)
        _expenses.value = listOf(newExpense) + _expenses.value
    }

    fun deleteExpense(expense: Expense) {
        _expenses.value = _expenses.value.filter { it.id != expense.id }
    }
}

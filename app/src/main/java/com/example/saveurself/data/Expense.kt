package com.example.saveurself.data

import java.time.LocalDate
import java.util.UUID

data class Expense(
    val description: String,
    val amount: Double,
    val category: Category,
    val date: LocalDate,
    val id: String = UUID.randomUUID().toString()
)

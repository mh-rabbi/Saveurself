package com.example.saveurself

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.scene.SinglePaneSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.example.saveurself.ui.dashboard.AddExpenseForm
import com.example.saveurself.ui.dashboard.DashboardScreen
import com.example.saveurself.ui.dashboard.ExpenseViewModel
import com.example.saveurself.ui.navigation.BottomSheetSceneStrategy
import com.example.saveurself.ui.theme.SaveurselfTheme
import kotlinx.serialization.Serializable

@Serializable
object DashboardRoute : NavKey

@Serializable
object AddExpenseRoute : NavKey

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SaveurselfTheme {
                val backStack = rememberNavBackStack(DashboardRoute)
                val expenseViewModel: ExpenseViewModel = viewModel()
                val bottomSheetStrategy = remember { BottomSheetSceneStrategy<NavKey>() }

                NavDisplay(
                    backStack = backStack,
                    modifier = Modifier.fillMaxSize(),
                    onBack = { backStack.removeAt(backStack.size - 1) },
                    sceneStrategy = bottomSheetStrategy then SinglePaneSceneStrategy(),
                    entryProvider = { key: NavKey ->
                        when (key) {
                            is DashboardRoute -> NavEntry(key) {
                                DashboardScreen(
                                    viewModel = expenseViewModel,
                                    onAddExpenseClick = { backStack.add(AddExpenseRoute) }
                                )
                            }
                            is AddExpenseRoute -> NavEntry(
                                key = key,
                                metadata = BottomSheetSceneStrategy.bottomSheet()
                            ) {
                                AddExpenseForm(
                                    onAddExpense = { desc, amt, cat ->
                                        expenseViewModel.addExpense(desc,
                                            amt, cat,
                                            expenseViewModel.selectedDate.value)
                                    },
                                    onDismiss = { backStack.removeAt(backStack.size - 1) }
                                )
                            }
                            else -> NavEntry(key) { Text("Unknown route: $key") }
                        }
                    }
                )
            }
        }
    }
}

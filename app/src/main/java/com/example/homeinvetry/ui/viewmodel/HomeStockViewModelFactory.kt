package com.example.homeinvetry.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class HomeStockViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(DashboardViewModel::class.java) -> DashboardViewModel(application) as T
            modelClass.isAssignableFrom(InventoryViewModel::class.java) -> InventoryViewModel(application) as T
            modelClass.isAssignableFrom(ShoppingViewModel::class.java) -> ShoppingViewModel(application) as T
            modelClass.isAssignableFrom(RemindersViewModel::class.java) -> RemindersViewModel(application) as T
            modelClass.isAssignableFrom(AddItemViewModel::class.java) -> AddItemViewModel(application) as T
            modelClass.isAssignableFrom(ItemDetailViewModel::class.java) -> ItemDetailViewModel(application) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

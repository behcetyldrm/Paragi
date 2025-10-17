package com.example.paragi.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.paragi.database.ParagiDao
import com.example.paragi.model.DebtItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DebtDetailVM @Inject constructor(private val dao: ParagiDao): ViewModel() {

    var selectedItem by mutableStateOf<DebtItem>(DebtItem(
        name = "",
        category = "",
        price = 0f,
        pricePerInstallment = 0f,
        firstPaymentDate = 0,
        paymentType = "",
        payableLocation = "",
        note = ""
    ))

    fun getData(id: Int) {
        viewModelScope.launch {
            selectedItem = dao.getSelectedDebt(id)
        }
    }

    fun deleteData(debtItem: DebtItem) {
        viewModelScope.launch (Dispatchers.IO){
            dao.deleteDebt(debtItem)
        }
    }
}
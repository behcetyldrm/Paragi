package com.example.paragi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.paragi.database.ParagiDao
import com.example.paragi.model.DebtItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddDebtVM @Inject constructor(private val dao: ParagiDao) : ViewModel() {

    fun addDebt(debtItem: DebtItem) {
        viewModelScope.launch (Dispatchers.IO){
            dao.insertDebt(debtItem)
        }
    }
}
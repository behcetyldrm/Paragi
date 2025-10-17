package com.example.paragi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.paragi.database.ParagiDao
import com.example.paragi.model.DebtItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Month
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class MainScreenVM @Inject constructor(private val dao: ParagiDao): ViewModel() {

    private var _selectedYear = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR))
    private var _selectedMonth = MutableStateFlow(Calendar.getInstance().get(Calendar.MONTH))

    val selectedYear: StateFlow<Int> = _selectedYear
    val selectedMonth: StateFlow<Int> = _selectedMonth

    val allData: StateFlow<List<DebtItem>> = dao.getAllDebt()
        .stateIn(
            scope = viewModelScope,
            //scope belirtilen sürede veri akışı devam eder. süre dolunca veri akışı otomatik kesilir
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val filteredData: StateFlow<List<DebtItem>> = combine(
        flow = allData,
        flow2 = selectedMonth,
        flow3 = selectedYear
    ) { debts, month, year ->

        debts.filter { debt ->
            debt.isActiveInMonth(year, month)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun getInstallment(debt: DebtItem) : Int? {
        return debt.getInstallmentForMonth(selectedYear.value, selectedMonth.value)
    }

    fun updateDate(year: Int, month: Int) {
        _selectedYear.value = year
        _selectedMonth.value = month
    }
}
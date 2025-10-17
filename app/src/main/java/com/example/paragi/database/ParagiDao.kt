package com.example.paragi.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.paragi.model.DebtItem
import com.example.paragi.model.InvestmentItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ParagiDao {

    //MainScreen
    @Query("SELECT * FROM debt")
    fun getAllDebt() : Flow<List<DebtItem>>

    //Debt Add
    @Insert
    suspend fun insertDebt(debtItem: DebtItem)

    //Investment Add
    @Insert
    suspend fun insertInvestment(investmentItem: InvestmentItem)

    //Debt Detail
    @Query("SELECT * FROM debt WHERE id= :debtId")
    suspend fun getSelectedDebt(debtId: Int) : DebtItem

    @Update
    suspend fun updateDebt(debtItem: DebtItem)

    @Delete
    suspend fun deleteDebt(debtItem: DebtItem)
}
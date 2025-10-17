package com.example.paragi.model

import android.icu.util.Calendar
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "debt")
data class DebtItem(
    val name: String,
    val category: String,
    val price: Float,
    val installment: Int = 1,
    val pricePerInstallment: Float,
    val note: String,
    val firstPaymentDate: Long,
    val payersNum: Int = 1,
    val paymentType: String,
    val payableLocation: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
) {
    fun getInstallmentForMonth(year: Int, month: Int): Int? {
        val firstDate = Calendar.getInstance().apply {
            timeInMillis = firstPaymentDate
        }
        val firstYear = firstDate.get(Calendar.YEAR)
        val firstMonth = firstDate.get(Calendar.MONTH)

        // months since the first payment month (0-based). Negative => not started yet
        val monthsSinceStart = (year - firstYear) * 12 + (month - firstMonth)
        if (monthsSinceStart < 0) return null

        val currentInstallment = monthsSinceStart + 1 // 1..installment
        return if (monthsSinceStart in 0 until installment) currentInstallment else null
    }

    fun isActiveInMonth(year: Int, month: Int) : Boolean {
        return getInstallmentForMonth(year, month) != null
    }

    fun getRemainingAmountForMonth(year: Int, month: Int): Float {
        val firstDate = Calendar.getInstance().apply { timeInMillis = firstPaymentDate }
        val firstYear = firstDate.get(Calendar.YEAR)
        val firstMonth = firstDate.get(Calendar.MONTH)

        val monthsSinceStart = (year - firstYear) * 12 + (month - firstMonth)
        if (monthsSinceStart < 0) return price

        val completedInstallments = monthsSinceStart.coerceAtMost(installment)
        val paidAmount = completedInstallments * pricePerInstallment
        val remaining = price - paidAmount
        return if (remaining > 0f) remaining else 0f
    }
}

@Entity(tableName = "investment")
data class InvestmentItem(
    //val name: String,
    val category: String,
    val goldInstallment: Int? = null,
    val price: Float? = null,
    val gramPiece: Int? = null,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)

/*@Entity("fortune")
data class FortuneItem(

)*/


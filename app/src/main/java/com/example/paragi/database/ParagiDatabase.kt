package com.example.paragi.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.paragi.model.DebtItem
import com.example.paragi.model.InvestmentItem

@Database(entities = [DebtItem::class, InvestmentItem::class], version = 1)
abstract class ParagiDatabase : RoomDatabase() {
    abstract fun paragiDao(): ParagiDao
}
package com.example.paragi.view

import android.annotation.SuppressLint
import android.icu.util.Calendar
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.paragi.model.DebtItem
import com.example.paragi.viewmodel.MainScreenVM

@SuppressLint("DefaultLocale")
@Composable
fun MainScreen(navController: NavController, viewModel: MainScreenVM = hiltViewModel()) {

    val focusManager = LocalFocusManager.current
    var showDialog by remember { mutableStateOf(false) }
    val items = mapOf("Veri Ekle" to "add_debt")
    var showDatePicker by remember { mutableStateOf(false) }

    val allData = viewModel.allData.collectAsState().value
    val year by viewModel.selectedYear.collectAsState()
    val month by viewModel.selectedMonth.collectAsState()
    val filteredDebts by viewModel.filteredData.collectAsState()
    val totalPriceFromMonth = filteredDebts.sumOf { it.pricePerInstallment.toDouble() }.toFloat()
    val remainingTotal = allData.sumOf { it.getRemainingAmountForMonth(year, month).toDouble() }.toFloat()

    val monthStr = when(month){
        0 -> "Ocak"
        1 -> "Şubat"
        2 -> "Mart"
        3 -> "Nisan"
        4 -> "Mayıs"
        5 -> "Hazira"
        6 -> "Temmuz"
        7 -> "Ağustos"
        8 -> "Eylül"
        9 -> "Ekim"
        10 -> "Kasım"
        11 -> "Aralık"
        else -> "-"
    }

    YearMonthPicker(
        visible = showDatePicker,
        currentYear = year,
        currentMonth = month,
        onDismiss = { showDatePicker = false },
        onConfirm = { newYear, newMonth ->
            viewModel.updateDate(newYear, newMonth)
            showDatePicker = false
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { focusManager.clearFocus() },
    ) {

        Box (
            modifier = Modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
                        bottomStart = 32.dp,
                        bottomEnd = 32.dp
                    )
                )
                .background(Color(0xffffab01))
                .height(250.dp),
            contentAlignment = Alignment.Center
        ){
            IconButton(
                onClick = { showDialog = true },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(top = 2.dp, end = 10.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color(0xffea4d3d),
                    contentColor = Color.White
                )
            ) {
                Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "setting")

                DropdownMenu(
                    expanded = showDialog,
                    onDismissRequest = { showDialog = false }
                ) {
                    items.forEach { (item, route) ->
                        DropdownMenuItem(
                            text = { Text(text = item, fontSize = 18.sp) },
                            onClick = {
                                showDialog = false
                                navController.navigate(route)
                            }
                        )
                    }
                }
            }

            Text(
                text = "$monthStr $year",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier
                    .padding(top = 4.dp, bottom = 4.dp)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .align(Alignment.TopCenter)
                    .clickable{ showDatePicker = true }
            )

            Column(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.statusBars),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                Text(
                    text = "${String.format("%.2f", totalPriceFromMonth)} TL",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = "Toplam Borç: ${String.format("%.2f", remainingTotal)} TL",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }



        Spacer(Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredDebts){ debt ->
                DebtsCard(navController, debt, year, month)
            }
        }
    }

}

@Composable
fun YearMonthPicker(
    visible: Boolean, // Dialog'u göster/gizle
    currentYear: Int,
    currentMonth: Int,
    onDismiss: () -> Unit, // Dialog kapandığında ne olacak
    onConfirm: (year: Int, month: Int) -> Unit // "Tamam"a basıldığında ne olacak
){
    if (visible) {
        var selectedYear by remember { mutableIntStateOf(currentYear) }
        var selectedMonth by remember { mutableIntStateOf(currentMonth) }

        val years = (2025..2040).toList() // Gösterilecek yıl aralığı
        val months = listOf(
            "Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran",
            "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"
        )

        Dialog(onDismissRequest = onDismiss) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Tarih Seçin",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Row(
                        modifier = Modifier.height(150.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            items(months.indices.toList()) { monthIndex ->
                                Text(
                                    text = months[monthIndex],
                                    fontSize = 20.sp,
                                    modifier = Modifier
                                        .padding(vertical = 8.dp)
                                        .clickable { selectedMonth = monthIndex },
                                    color = if (selectedMonth == monthIndex) Color(0xffffab01) else Color.Black,
                                    fontWeight = if (selectedMonth == monthIndex) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }

                        LazyColumn (
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            items(years) { year ->
                                Text(
                                    text = year.toString(),
                                    fontSize = 20.sp,
                                    modifier = Modifier
                                        .padding(vertical = 8.dp)
                                        .clickable { selectedYear = year },
                                    color = if (selectedYear == year) Color(0xffffab01) else Color.Black,
                                    fontWeight = if (selectedYear == year) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("İptal")
                        }
                        Spacer(Modifier.width(8.dp))
                        TextButton(onClick = {
                            onConfirm(selectedYear, selectedMonth)
                        }) {
                            Text("Tamam")
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun DebtsCard(
    navController: NavController,
    debt: DebtItem,
    year: Int,
    month: Int
) {

    val debtName = if (debt.name.length > 10){
        debt.name.substring(0, 10) + "..."
    } else {
        debt.name
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable { /*daha sonra yapılacak*/ },
        shape = RoundedCornerShape(32.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = run {
            val currentInstallment = debt.getInstallmentForMonth(year, month)
            val isLastInstallment = currentInstallment != null && currentInstallment == debt.installment
            CardDefaults.cardColors(
                containerColor = if (isLastInstallment) Color(0xffb1dc8b) else Color(0xfffed876),
                contentColor = Color.Black
            )
        }
    ) {
        Row (
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){

            val installment = debt.getInstallmentForMonth(year, month)
            Text(
                text = debtName,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start
            )
            Text(
                text = "$installment/${debt.installment}",
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "${String.format("%.1f", debt.pricePerInstallment)} TL",
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1f)
            )

        }
    }
}
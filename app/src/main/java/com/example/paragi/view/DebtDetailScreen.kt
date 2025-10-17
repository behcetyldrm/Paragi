package com.example.paragi.view

import android.annotation.SuppressLint
import android.icu.util.Calendar
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.paragi.viewmodel.DebtDetailVM
import kotlin.collections.component1
import kotlin.collections.component2


@SuppressLint("DefaultLocale")
@Composable
fun DebtDetailScreen(navController: NavController, id: Int, selectedYear: Int, selectedMonth: Int, viewModel: DebtDetailVM = hiltViewModel()) {

    LaunchedEffect(key1 = id) {
        viewModel.getData(id)
    }
    
    val debt = viewModel.selectedItem
    var expanded by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog){
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Silme İşlemi") },
            text = { Text(text = "Bu ödemeyi silmek istediğinize emin misiniz? Geri alınamaz!") },
            confirmButton = { TextButton(onClick = {
                navController.popBackStack()
                viewModel.deleteData(debt)
                showDialog = false
            }) { 
                Text(text = "Sil", color = Color(0xffea4d3d))
            } },
            dismissButton = { TextButton(onClick = {showDialog = false}) { Text("İptal") } }
        )
    }

    Column(modifier = Modifier.fillMaxSize()){
        Box(
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
                onClick = { expanded = true },
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
                SettingMenu(
                    expanded = expanded,
                    onDismiss = { expanded = false },
                    onDeleteClick = { showDialog = true }
                )
            }

            Text(
                text = debt.name,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier
                    .padding(top = 4.dp, bottom = 2.dp)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .align(Alignment.TopCenter)
            )

            Column(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.statusBars),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                Text(
                    text = "${String.format("%.2f", debt.price)} TL",
                    fontSize = 36.sp,
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
                    text = "Taksit Tutarı: ${String.format("%.2f", debt.pricePerInstallment)} TL",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Text(
                    text = "Taksit Tutarı(Kişi b.): ${String.format("%.2f", (debt.pricePerInstallment)/debt.payersNum)} TL",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
        Spacer(Modifier.height(28.dp))

        //*******Detay kutuları*******
        LazyColumn {
            item{
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ){
                    DetailFieldBox(title = "Taksit Sayısı") {
                        val currentInstallment = debt.getInstallmentForMonth(selectedYear, selectedMonth)
                        val installmentText = currentInstallment?.let { "$it/${debt.installment}" } ?: "0/${debt.installment}"
                        
                        Text(
                            text = installmentText,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }

                    DetailFieldBox(title = "Ödeyen Sayısı") {
                        Text(
                            text = debt.payersNum.toString(),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(Modifier.height(28.dp))

                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ){
                    DetailFieldBox(title = "Seçili Ay") {
                        val monthNames = listOf(
                            "Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran",
                            "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"
                        )
                        val monthName = monthNames[selectedMonth]
                        
                        Text(
                            text = "$monthName $selectedYear",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }

                    DetailFieldBox(title = "Kategori") {
                        Text(
                            text = debt.category,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(Modifier.height(28.dp))

                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ){
                    DetailFieldBox(title = "Ödeme Türü") {
                        Text(
                            text = debt.paymentType,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }

                    DetailFieldBox(title = "Ödeme Yeri") {
                        Text(
                            text = debt.payableLocation.ifEmpty { "Belirtilmemiş" },
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                DetailFieldBoxFullWidth(
                    title = "Not",
                    modifier = Modifier.padding(vertical = 28.dp, horizontal = 12.dp)
                ) {
                    Text(
                        text = debt.note.ifEmpty { "Not eklenmemiş." },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
                    )
                }
            }
        }
    }


}

@Composable
fun DetailFieldBox(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .size(width = 160.dp, height = 120.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFEF6C00)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DetailText(title)
            Spacer(Modifier.weight(1f))
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                content()
            }
            Spacer(Modifier.weight(1f))
        }
    }
}

@Composable
fun DetailFieldBoxFullWidth(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFEF6C00)),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            DetailText(title)
            content()
        }
    }
}

@Composable
fun DetailText(value: String) {
    Text(
        text = value,
        fontSize = 20.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        fontWeight = FontWeight.SemiBold,
        color = Color.White,
        textAlign = TextAlign.Center
    )
}

@Composable
fun SettingMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onDeleteClick: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss
    ) {
        DropdownMenuItem(
            text = { 
                Text(
                    text = "Sil",
                    modifier = Modifier.padding(horizontal = 8.dp),
                    fontSize = 18.sp,
                    color = Color(0xffea4d3d)
                ) 
            },
            onClick = {
                onDeleteClick()
                onDismiss()
            }
        )
        DropdownMenuItem(
            text = { 
                Text(
                    text = "Düzenle",
                    modifier = Modifier.padding(horizontal = 8.dp),
                    fontSize = 18.sp
                ) 
            },
            onClick = {
                // Şuan aktif değil
                onDismiss()
            },
            enabled = false
        )
    }
}
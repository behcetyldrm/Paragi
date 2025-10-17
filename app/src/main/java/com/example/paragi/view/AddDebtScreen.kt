package com.example.paragi.view
import android.annotation.SuppressLint
import android.icu.util.Calendar
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.paragi.viewmodel.AddDebtVM
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import com.example.paragi.model.DebtItem

@SuppressLint("DefaultLocale")
@Composable
fun AddDebtScreen(navController: NavController, viewmodel: AddDebtVM = hiltViewModel()) {

    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var installment by remember { mutableIntStateOf(1) }
    var payersNum by remember { mutableIntStateOf(1) }
    var paymentType by remember { mutableStateOf("") }
    var payableLocation by remember { mutableStateOf("") }
    var firstPaymentDate by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedYear by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    var selectedMonth by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.MONTH)) }
    var category by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    val priceCheck = price.toFloatOrNull() ?: 0.0f
    val pricePerInstallment = (priceCheck / installment)
    val pricerPerPayer = (pricePerInstallment / payersNum)

    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current //güncel odak durumunu alır

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                //tıklama efektini kaldırır
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                focusManager.clearFocus() //odak durumunu siler
            }
    ) {
        //******Fiyat ve başlık ekranı*******
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
                .height(250.dp)
        ){
            IconButton(
                onClick = {showDialog = true},
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(end = 10.dp, top = 2.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color(0xffea4d3d),
                    contentColor = Color.White
                )
            ) {
                Icon(imageVector = Icons.Filled.Check, contentDescription = "save")
            }

            if (showDialog){
                SpecialAlertDialog(
                    onConfirm = {
                        val calendar = Calendar.getInstance()
                        if (price.isNotEmpty()){
                            if (price.toFloat() > 0 && name.isNotBlank() && paymentType.isNotBlank() && category.isNotBlank()){
                                calendar.set(selectedYear, selectedMonth, 1)
                                val paymentMonth = calendar.timeInMillis
                                val item = DebtItem(
                                    name = name,
                                    price = price.toFloat(),
                                    category = category,
                                    payersNum = payersNum,
                                    paymentType = paymentType,
                                    note = note,
                                    payableLocation = payableLocation,
                                    installment = installment,
                                    firstPaymentDate = paymentMonth,
                                    pricePerInstallment = pricePerInstallment
                                )

                                viewmodel.addDebt(item)
                                showDialog = false
                                navController.navigate("main_screen") {
                                    popUpTo("add_debt") {
                                        inclusive = true
                                    }
                                }

                            } else {
                                Toast.makeText(context, "Lütfen gerekli alanları doldurunuz.", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "Lütfen gerekli alanları doldurunuz.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onDismiss = {showDialog = false}
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                //*****Başlık*****
                TextField(
                    value = name,
                    onValueChange = {
                        if (it.length <= 20) {
                            name = it
                        }
                    },
                    placeholder = {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Başlık",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.align(Alignment.Center),
                                color = Color.Black.copy(alpha = 0.55f)
                            )
                        }
                    },
                    modifier = Modifier
                        .padding(top = 2.dp, bottom = 4.dp)
                        .width(IntrinsicSize.Min),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    textStyle = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                )
                //*****Fiyat****
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(top = 10.dp)
                ){
                    BasicTextField(
                        value = price,
                        onValueChange = {
                            if (it.isEmpty()) {
                                price = ""
                            } else if (it == "."){
                                price = "0."
                            } else {
                                val correctedValue = it.replace(",", ".")
                                if (correctedValue.matches(Regex("^(0|[1-9]\\d*)(\\.\\d*)?$"))) {
                                    price = correctedValue
                                }
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        decorationBox = { innerTextField ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (price.isEmpty()) {
                                    Text(
                                        text = "0,00 TL",
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        color = Color.Black.copy(alpha = 0.55f)
                                    )
                                } else {
                                    Text(
                                        text = price,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        color = Color.White
                                    )
                                    Text(" TL", fontSize = 32.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                                }
                            }
                        }
                    )
                }
                //*****Taksit tutarları****
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                   verticalArrangement = Arrangement.Bottom
                ) {

                    Text(
                        text = "Taksit tutarı: ${String.format("%.2f", pricePerInstallment)} TL",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = "Taksit tutarı(Kişi b.): ${String.format("%.2f", pricerPerPayer)} TL",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
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
                    InputFieldBox(title = "Taksit Sayısı") {
                        NumPicker(
                            onValueChange = { installment = it.toInt() },
                            items = (1..24).toList()
                        )
                    }

                    InputFieldBox(title = "Ödeyen Sayısı") {
                        NumPicker(
                            onValueChange = { payersNum = it.toInt() },
                            items = (1..8).toList()
                        )
                    }
                }

                Spacer(Modifier.height(28.dp))

                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ){
                    InputFieldBox(
                        title = "İlk Ödeme",
                        isRequired = true,
                        modifier = Modifier.clickable (
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ){ showDatePicker = true }
                    ) {
                        Spacer(Modifier.weight(1f))
                        val display = firstPaymentDate.ifEmpty { "Seçiniz" }
                        Text(
                            text = display,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.weight(1f))
                    }

                    InputFieldBox(
                        title = "Kategori",
                        isRequired = true
                    ) {
                        val categoryList = listOf("Teknoloji", "Bakım", "Giyim" ,"Döviz", "Kira", "Fatura", "Diğer")
                        StrPicker(
                            onValueChange = { category = it },
                            items = categoryList
                        )
                    }
                }

                Spacer(Modifier.height(28.dp))

                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ){
                    InputFieldBox(
                        title = "Ödeme Türü",
                        isRequired = true
                    ) {
                        val paymentTypeList = listOf("Nakit", "Kredi Kartı", "Diğer")
                        StrPicker(
                            onValueChange = { paymentType = it },
                            items = paymentTypeList
                        )
                    }

                    InputFieldBox(title = "Ödeme Yeri") {
                        TextField(
                            value = payableLocation,
                            onValueChange = {
                                if (it.length <= 16){
                                    payableLocation = it
                                }
                            },
                            placeholder = {
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "Yazınız...",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.align(Alignment.Center),
                                        color = Color.White.copy(0.7f)
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxSize()
                                .width(IntrinsicSize.Min),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            textStyle = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }

                InputFieldBoxFullWidth(
                    title = "Not",
                    modifier = Modifier.padding(vertical = 28.dp, horizontal = 12.dp)
                ) {
                    TextField(
                        value = note,
                        onValueChange = {
                            if (it.length <= 100){
                                note = it
                            }
                        },
                        placeholder = { Text("Not ekle...", color = Color.White.copy(0.7f)) },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        textStyle = TextStyle(fontSize = 18.sp)
                    )
                }
            }
        }
    }

    val months = listOf(
        "Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran",
        "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"
    )
    YearMonthPicker(
        visible = showDatePicker,
        currentYear = selectedYear,
        currentMonth = selectedMonth,
        onDismiss = { showDatePicker = false },
        onConfirm = { year, month ->
            selectedYear = year
            selectedMonth = month
            firstPaymentDate = months[month]
            showDatePicker = false
        }
    )
}

@Composable
fun StrPicker(
    onValueChange: (String) -> Unit,
    items: List<String>
) {
    var selectedValue by remember { mutableStateOf("Seçiniz") }
    var expanded by remember { mutableStateOf(false) }

    Box (
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { expanded = true },
        contentAlignment = Alignment.Center
    ){
        Text(
            text = selectedValue,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        items.forEach { item ->
            DropdownMenuItem(
                text = { Text(item, fontSize = 20.sp) },
                onClick = {
                    selectedValue = item
                    onValueChange(selectedValue)
                    expanded = false
                }
            )
        }
    }
}
@Composable
fun NumPicker(
    onValueChange: (Long) -> Unit,
    items: List<Int>,
) {
    var selectedValue by remember { mutableIntStateOf(1) }
    var expanded by remember { mutableStateOf(false) }

    Box (
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { expanded = true },
        contentAlignment = Alignment.Center
    ){
        Text(
            text = selectedValue.toString(),
            fontSize = 32.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ){
        items.forEach { item ->
            DropdownMenuItem(
                text = { Text(text = item.toString(), fontSize = 20.sp) },
                onClick = {
                    selectedValue = item
                    onValueChange(selectedValue.toLong())
                    expanded = false
                }
            )
        }
    }
}
@Composable
fun SpecialText(value: String) {
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
fun InputFieldBox(
    title: String,
    modifier: Modifier = Modifier,
    isRequired: Boolean = false,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .size(width = 160.dp, height = 120.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFEF6C00)),
        contentAlignment = Alignment.Center
    ) {
        Column {
            SpecialText(title + if (isRequired) "*" else "")
            content()
        }
    }
}

@Composable
fun InputFieldBoxFullWidth(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFEF6C00)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            SpecialText(title)
            content()
        }
    }
}

@Composable
fun SpecialAlertDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Uyarı", fontSize = 24.sp, fontWeight = FontWeight.SemiBold) },
        text = { Text(text = "Kaydetmek istediğinize emin misiniz?", fontSize = 18.sp) },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                    onDismiss()
                }
            ) { Text("Kaydet", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color(0xffea4d3d)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}
package com.example.mickcalc // ⚠️ ตรวจสอบและเปลี่ยน package name ให้ตรงกับโปรเจกต์ใหม่ของคุณ

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

// =============================================================
// 1. ViewModel (Logic & Business State) ตามหลัก MVVM
// =============================================================
class InstallmentViewModel : ViewModel() {
    var principalInput by mutableStateOf("")
        private set

    var interestRateInput by mutableStateOf("")
        private set

    var monthsInput by mutableStateOf("")
        private set

    var monthlyPaymentResult by mutableStateOf(0.0)
        private set

    var totalInterestResult by mutableStateOf(0.0)
        private set

    var totalPaymentResult by mutableStateOf(0.0)
        private set

    fun onPrincipalChange(newValue: String) {
        principalInput = newValue
    }

    fun onInterestRateChange(newValue: String) {
        interestRateInput = newValue
    }

    fun onMonthsChange(newValue: String) {
        monthsInput = newValue
    }

    // ฟังก์ชันคำนวณค่างวดผ่อนชำระ
    fun calculateInstallment() {
        val principal = principalInput.toDoubleOrNull() ?: 0.0
        val rate = interestRateInput.toDoubleOrNull() ?: 0.0
        val months = monthsInput.toIntOrNull() ?: 0

        if (principal > 0 && months > 0) {
            // สูตรคำนวณ: ดอกเบี้ยรวม = ยอดจัด * (อัตราดอกเบี้ย% / 100) * (จำนวนเดือน / 12)
            val totalInterest = principal * (rate / 100.0) * (months / 12.0)
            val totalPayment = principal + totalInterest
            val monthlyPayment = totalPayment / months

            totalInterestResult = totalInterest
            totalPaymentResult = totalPayment
            monthlyPaymentResult = monthlyPayment
        } else {
            totalInterestResult = 0.0
            totalPaymentResult = 0.0
            monthlyPaymentResult = 0.0
        }
    }
}

// =============================================================
// 2. Main Activity Entry Point
// =============================================================
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    InstallmentCalculatorScreen()
                }
            }
        }
    }
}

// =============================================================
// 3. UI Layer (Jetpack Compose Screen)
// =============================================================
@Composable
fun InstallmentCalculatorScreen(
    viewModel: InstallmentViewModel = viewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "ระบบคำนวณค่างวดผ่อนชำระ (MVVM)",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ช่องกรอก ยอดจัด / ราคาสินค้า
        OutlinedTextField(
            value = viewModel.principalInput,
            onValueChange = { viewModel.onPrincipalChange(it) },
            label = { Text("ราคาสินค้า / ยอดจัด (บาท)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ช่องกรอก อัตราดอกเบี้ยต่อปี
        OutlinedTextField(
            value = viewModel.interestRateInput,
            onValueChange = { viewModel.onInterestRateChange(it) },
            label = { Text("อัตราดอกเบี้ยต่อปี (%)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ช่องกรอก จำนวนเดือนที่ต้องการผ่อน
        OutlinedTextField(
            value = viewModel.monthsInput,
            onValueChange = { viewModel.onMonthsChange(it) },
            label = { Text("ระยะเวลาผ่อน (จำนวนเดือน)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { viewModel.calculateInstallment() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("คำนวณค่างวด", style = MaterialTheme.typography.titleMedium)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // การ์ดแสดงผลลัพธ์การคำนวณ
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "ค่างวดต่อเดือน: %.2f บาท".format(viewModel.monthlyPaymentResult),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "ดอกเบี้ยรวม: %.2f บาท".format(viewModel.totalInterestResult),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "ยอดชำระรวมทั้งสิ้น: %.2f บาท".format(viewModel.totalPaymentResult),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
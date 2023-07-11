package com.example.myproject

import android.text.InputType
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material3.Text
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanCalculator() {
    var billAmount by remember { mutableStateOf("") }
    var mortgageTime by remember { mutableStateOf("") }
    var interestRate by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var paymentType by remember{ mutableStateOf("") }

    val options = listOf("Monthly", "Semi-annually", "Annually")
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(options[0]) }


    var monthly_pay by remember { mutableStateOf("0.0") }
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(30.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Loan Calculator",
            fontSize = 30.sp,
            lineHeight = 50.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 28.dp)
        )

        EditNumberField(
            label = R.string.bill_amount,
            leadingIcon = R.drawable.dollar,
            value = billAmount,
            onValueChange = { billAmount = it },
            showError = showError
        )

        EditNumberField(
            label = R.string.period,
            leadingIcon = R.drawable.time,
            value = mortgageTime,
            onValueChange = { mortgageTime = it },
            showError = showError
        )
        EditNumberField(
            label = R.string.interest,
            leadingIcon = R.drawable.percentage,
            value = interestRate,
            onValueChange = { interestRate = it },
            showError = showError,
        )
// drop down menu
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            TextField(
                readOnly = true,
                value = selectedOptionText,
                onValueChange = {paymentType=it },
                label = { Text("Payment Type") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded
                    )
                },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {
                options.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(text = selectionOption) },
                        onClick = {
                            selectedOptionText = selectionOption
                            expanded = false
                        }
                    )
                }
            }
        }
        Button(
            onClick = {
                if (validateInput(billAmount,  mortgageTime, interestRate)) {
                    val amount = billAmount.toDouble()
                    val interest = interestRate.toDouble()
                    val paymentType=paymentType.toString()
                    monthly_pay = calculate_loan_Payment(amount,  interest)
                    showError = false
                } else {
                    showError = true
                }
            },
            modifier = Modifier.padding( bottom =32.dp,top=32.dp)
        ) {
            Text("Calculate")
        }
        if (showError) {
            Text(
                text = "Invalid input",
                style = TextStyle(
                    fontSize = 14.sp,
                    color = androidx.compose.ui.graphics.Color.Red
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
        } else {
            Text(
                text = stringResource(R.string.monthly_payment, monthly_pay),
                style = MaterialTheme.typography.displaySmall
            )
        }
    }
}
fun calculate_loan_Payment(amount: Double,  interest: Double = 15.0): String {
    val interestAmount = interest / 100 * amount
    return NumberFormat.getCurrencyInstance().format(interestAmount)
}


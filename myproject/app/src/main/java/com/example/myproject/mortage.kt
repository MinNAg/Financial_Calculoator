package com.example.myproject

import android.annotation.SuppressLint
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myproject.ui.theme.TableScreen
import kotlin.math.ceil
import kotlin.math.pow

@SuppressLint("MutableCollectionMutableState")
@Composable
fun MortgageCalculator() {

    var billAmount by remember { mutableStateOf("") }
    var downPayment by remember { mutableStateOf("") }
    var mortgageTime by remember { mutableStateOf("") }
    var interestRate by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

//    var monthly_pay by remember { mutableStateOf("0.0") }

//    lateinit var mortageResult:mortageData

    //var mortageResult: mortageData? by remember { mutableStateOf(null) }

    //var resultList: List<mortageData> by remember { mutableStateOf(mutableListOf()) }

    var rList:MutableList<List<Double>> by remember {
        mutableStateOf(mutableListOf())
    }
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(30.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Mortgage Calculator",
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
            label = R.string.down_payment,
            leadingIcon = R.drawable.percentage,
            value = downPayment,
            onValueChange = { downPayment = it },
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

        Button(
            onClick = {

                showError = if (validateInput(billAmount, downPayment, mortgageTime, interestRate)) {
                    clearList(rList)
                    calculation(billAmount, downPayment, mortgageTime,
                        interestRate,rList)
                    false
                } else {
                    true
                }
            },
            modifier = Modifier.padding(bottom = 32.dp)
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
                    text = stringResource(R.string.monthly_payment),
                    style = MaterialTheme.typography.displaySmall
                )
                Text(text = rList.toString())
                TableScreen(rList)



        }
    }
}

// modified TextField Function

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNumberField(
    @StringRes label: Int,
    @DrawableRes leadingIcon: Int,
    value: String,
    onValueChange: (String) -> Unit,
    showError: Boolean
) {
    TextField(
        value = value,
        leadingIcon = { Icon(painter = painterResource(id = leadingIcon), contentDescription = null) },
        onValueChange = onValueChange,
        singleLine = true,
        label = { Text(stringResource(label)) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        isError = showError,
        modifier = Modifier.padding(bottom=28.dp)
    )
}

// checking the input data valid or not
fun validateInput(vararg values: String): Boolean {
    for (value in values) {
        if (value.isEmpty() || value.toDoubleOrNull() == null) {
            return false
        }
    }
    return true
}
fun calculation(billAmount:String, downPayment:String, mortgageTime:String, interestRate:String,rList:MutableList<List<Double>>){

        var amount = billAmount.toDouble()
        val downPay = downPayment.toDouble()
        val interest = interestRate.toDouble()
        val time=mortgageTime.toDouble()
        val index=ceil(time *12)
        for (i in 0 until index.toInt() step 1) {

            rList.add(calculatePayment(amount, time, downPay, interest))

            amount -= rList[i][1]//amount- principal


        }
}


fun calculatePayment(
    amount: Double,
    downPay: Double,
    time: Double,
    interest: Double = 15.0
): List<Double> {
    val interestAmount = interest / 100 * (amount - downPay)
    val index = time * 12
    val monthlyInterestrate = interest / 12
    val monthlyPay = amount * (monthlyInterestrate * (1 + monthlyInterestrate).pow(index))/((1+monthlyInterestrate).pow(index-1))
    val principal = monthlyPay - interestAmount
    //val total=interestAmount+principal //same as monthlyPay
    var remain = amount - principal
    //    return NumberFormat.getCurrencyInstance().format(monthlyPay)


    return listOf(interestAmount, principal, index, monthlyPay, remain)
}

fun clearList(rList:MutableList<List<Double>>){
    rList.clear()
}
package com.hazrat.tipapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hazrat.tipapp.components.InputField
import com.hazrat.tipapp.ui.theme.JetTipAppTheme
import com.hazrat.tipapp.utils.calculateTotalPerPerson
import com.hazrat.tipapp.utils.calculateTotalTip
import com.hazrat.tipapp.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetTipAppTheme {
                // A surface container using the 'background' color from the theme
                MyApp {
//                    TopHeader()
                    MainContent()
                }
            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        content()
    }
}


@Composable
fun TopHeader(totalPerPerson: Double = 0.0) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .height(150.dp)
            .clip(shape = CircleShape.copy(CornerSize(12.dp))),
//            .clip(shape = RoundedCornerShape(12.dp))
        color = Color(0xFFe5d2f6)
    ) {
        Column(modifier = Modifier
            .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val total = "%.2f".format(totalPerPerson)
            Text(text = "Total Per Person", style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            ))
            Text(text = "$$total", style = TextStyle(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp
            ))
        }
    }
}


@Composable
fun MainContent() {
    val splitByState = remember {
        mutableStateOf(1)
    }
    val range = IntRange(start = 1, endInclusive = 100)

    val tipAmountState = remember {
        mutableStateOf(0.0)
    }
    val totalPerPersonState = remember {
        mutableStateOf(0.0)
    }
    Column {

        BillForm(splitByState = splitByState,
            tipAmountState = tipAmountState,
            range = range,
            totalPerPersonState =  totalPerPersonState){}
    }


}


@Composable
fun BillForm(
    modifier: Modifier= Modifier,
    range: IntRange = 1..100,
    splitByState: MutableState<Int>,
    tipAmountState: MutableState<Double>,
    totalPerPersonState: MutableState<Double>,

    onValChange: (String) -> Unit = {}
) {

    val totalBillState = remember {
        mutableStateOf("")
    }

    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }


    val sliderPositionState = remember {
        mutableStateOf(0f)
    }
    val tipPercentage = (sliderPositionState.value*100).toInt()
    val keyboardController = LocalSoftwareKeyboardController.current



    TopHeader(totalPerPerson = totalPerPersonState.value)
    Surface(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(width = 1.dp, color= Color.LightGray)
    ) {
        Column (
            modifier = Modifier.padding(6.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ){
            InputField(valueState = totalBillState,
                labelId = "Enter Bil",
                enable = true,
                isSingleLine = true,
                onAction = KeyboardActions{
                    if (!validState) return@KeyboardActions
                    onValChange(
                        totalBillState.value.trim()
                    )
                    keyboardController?.hide()
                }
            )
            if (validState){
            SplitArea(splitByState, range, totalBillState, tipPercentage, totalPerPersonState)
                //Tip Row
                Row (
                    modifier = Modifier .padding(horizontal = 3.dp, vertical = 12.dp)
                ){
                    Text(text = "Tip",
                        modifier = Modifier
                            .align(alignment = Alignment.CenterVertically))
                    Spacer(modifier = Modifier.width(200.dp))
                    Text(text = "$${tipAmountState.value}",
                        modifier = Modifier
                            .align(alignment = Alignment.CenterVertically))
                }

            TipSlider(tipPercentage, sliderPositionState, tipAmountState, totalBillState,splitByState, totalPerPersonState )
            }else{
                Box(){

                }
            }
        }
    }
}

@Composable
fun SplitArea(
    splitByState: MutableState<Int>,
    range: IntRange,
    totalBillState: MutableState<String>,
    tipPercentage: Int,
    totalPerPersonState: MutableState<Double>
) {
    Row(
        modifier = Modifier.padding(3.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = "Split",
            modifier = Modifier.align(alignment = Alignment.CenterVertically)
        )
        Spacer(modifier = Modifier.width(120.dp))
        Row(
            modifier = Modifier.padding(horizontal = 3.dp),
            horizontalArrangement = Arrangement.End
        ) {
            RoundIconButton(imageVector = Icons.Filled.Remove,
                onClick = {
                    splitByState.value =
                        if (splitByState.value > 1) splitByState.value - 1
                        else 1
                        totalPerPersonState.value =
                            calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(),
                                splitByState = splitByState.value, tipPercentage = tipPercentage)
                }
            )
            Text(
                text = "${splitByState.value}",
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 9.dp, end = 9.dp)
            )

            RoundIconButton(imageVector = Icons.Filled.Add,
                onClick = {
                    if (splitByState.value < range.last) {
                        splitByState.value = splitByState.value + 1
                        totalPerPersonState.value =
                            calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(),
                                splitByState = splitByState.value, tipPercentage = tipPercentage)
                    }
                }
            )
        }
    }
}

@Composable
fun TipSlider(
    tipPercentage: Int,
    sliderPositionState: MutableState<Float>,
    tipAmountState: MutableState<Double>,
    totalBillState: MutableState<String>,
    splitByState:MutableState<Int>,
    totalPerPersonState:MutableState<Double>
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "$tipPercentage %")
        Spacer(modifier = Modifier.height(14.dp))
        //slider
        Slider(value = sliderPositionState.value,
            onValueChange = { newVal ->
                sliderPositionState.value = newVal
                tipAmountState.value =
                    calculateTotalTip(
                        totalBill = totalBillState.value.toDouble(),
                        tipPercentage = tipPercentage
                    )
                totalPerPersonState.value =
                    calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(),
                        splitByState = splitByState.value, tipPercentage = tipPercentage)
            },
            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
            onValueChangeFinished = {

            }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    JetTipAppTheme {
        MyApp {

        }
    }
}
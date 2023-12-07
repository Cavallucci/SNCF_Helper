package com.example.sncf

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sncf.ui.theme.SNCFTheme
import java.util.Calendar


data class Infos(
    var name: String,
    var date: String,
    var depart: String,
    var destination: String,
    var horaire: String,
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SNCFTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TicketBookingApp()
                }
            }
        }
    }
}

@Composable
fun TicketBookingApp() {
    var currentStep by remember { mutableStateOf(1) }
    var infos by remember { mutableStateOf(Infos("", "", "", "", "")) }

    when (currentStep) {
        1 -> Start(onNext = { currentStep = 2 })
        2 -> SelectPerson(
            onInfosSelected = { info ->
                infos.name = info
                currentStep = 3
            }
        )
        3 -> SelectDate(
            onInfosSelected = { info ->
                infos.date = info
                currentStep = 5
            }
        )
    }
}

@Composable
fun Start(onNext: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(
            modifier = Modifier
                .size(300.dp)
                .clip(CircleShape)
                .aspectRatio(1f),
            colors = ButtonDefaults.buttonColors(Color.Red),
            onClick = onNext
        ) {
            Column (
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = "Cliquer ici pour", fontSize = 36.sp, color = Color.White)
                Text(text = "commencer", fontSize = 36.sp, color = Color.White)
            }
        }
    }
}

@Composable
fun SelectPerson(
    onInfosSelected: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "Pour qui prendre le billet ?", fontSize = 36.sp)
        Spacer(modifier = Modifier.size(16.dp))
        Button(onClick = {
            val selectedInfo = "Papy"
            onInfosSelected(selectedInfo)
        }) {
            Text(text = "Papy", fontSize = 24.sp)
        }
        Spacer(modifier = Modifier.size(16.dp))
        Button(onClick = {
            val selectedInfo = "Mamie"
            onInfosSelected(selectedInfo)
        }) {
            Text(text = "Mamie", fontSize = 24.sp)
        }
    }
}

@Composable
fun SelectDate(
    onInfosSelected: (String) -> Unit,
) {
    var date by remember { mutableStateOf("") }
    val calendar = Calendar.getInstance()
    val context = LocalContext.current

    fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                date = "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}"
                onInfosSelected(date)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "Date du trajet ?", fontSize = 36.sp)
        Spacer(modifier = Modifier.size(16.dp))

        Button(onClick = { showDatePickerDialog() }) {
            Text(text = "Cliquer ici pour choisir une date", fontSize = 24.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SNCFTheme {
        TicketBookingApp()
    }
}
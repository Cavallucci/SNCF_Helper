package com.example.sncf

import android.app.DatePickerDialog
import android.os.Bundle
import android.telecom.Call
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sncf.ui.theme.SNCFTheme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
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
                currentStep = 4
            }
        )
        4 -> SelectDepart(
            onInfosSelected = { info ->
                infos.depart = info
                currentStep = 5
            },
            question = "Départ ?"
        )
        5 -> SelectDepart(
            onInfosSelected = { info ->
                infos.destination = info
                currentStep = 6
            },
            question = "Destination ?"
        )
        6 -> SelectTrain(
            onInfosSelected = { info ->
                infos.horaire = info
                currentStep = 7
            },
            informations = infos
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
        Text(text = "Pour qui ?", fontSize = 55.sp)
        Spacer(modifier = Modifier.size(100.dp))
        Button(onClick = {
            val selectedInfo = "Papy"
            onInfosSelected(selectedInfo)
        }) {
            Text(text = "Papy", fontSize = 45.sp)
        }
        Spacer(modifier = Modifier.size(100.dp))
        Button(onClick = {
            val selectedInfo = "Mamie"
            onInfosSelected(selectedInfo)
        }) {
            Text(text = "Mamie", fontSize = 45.sp)
        }
        Spacer(modifier = Modifier.size(100.dp))
        Button(onClick = {
            val selectedInfo = "Les deux"
            onInfosSelected(selectedInfo)
        }) {
            Text(text = "Les deux", fontSize = 45.sp)
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
        Text(text = "Date du trajet ?", fontSize = 45.sp)
        Spacer(modifier = Modifier.size(100.dp))

        Button(onClick = { showDatePickerDialog() }) {
            Text(text = "Cliquer ici pour choisir une date",
                fontSize = 36.sp,
                lineHeight = 48.sp,
                textAlign = TextAlign.Center,
                )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectDepart(
    onInfosSelected: (String) -> Unit,
    question: String,
) {
    var isClicked by remember { mutableStateOf(false) }
    var cityName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = question, fontSize = 36.sp)
        Spacer(modifier = Modifier.size(45.dp))
        Button(onClick = {
            val selectedInfo = "Liverdun"
            onInfosSelected(selectedInfo)
        }) {
            Text(text = "Liverdun", fontSize = 36.sp)
        }
        Spacer(modifier = Modifier.size(45.dp))
        Button(onClick = {
            val selectedInfo = "Nancy"
            onInfosSelected(selectedInfo)
        }) {
            Text(text = "Nancy", fontSize = 36.sp)
        }
        Spacer(modifier = Modifier.size(45.dp))
        Button(onClick = {
            val selectedInfo = "Paris"
            onInfosSelected(selectedInfo)
        }) {
            Text(text = "Paris", fontSize = 36.sp)
        }
        Spacer(modifier = Modifier.size(45.dp))
        Button(onClick = {
            val selectedInfo = "Saverne"
            onInfosSelected(selectedInfo)
        }) {
            Text(text = "Saverne", fontSize = 36.sp)
        }
        Spacer(modifier = Modifier.size(45.dp))

        Button(onClick = { isClicked = true }) {
            Text(text = "Autre", fontSize = 36.sp) }

        if (isClicked) {
            Spacer(modifier = Modifier.size(16.dp))
            Text(text = "Veuillez écrire le nom de la ville :", fontSize = 20.sp)
            TextField(
                value = cityName,
                onValueChange = { cityName = it },
                label = { Text(text = "Ville") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                singleLine = true,
                shape = MaterialTheme.shapes.small,
            )

            Spacer(modifier = Modifier.size(16.dp))
            Button(onClick = {
                if (cityName.isNotBlank()) {
                    val selectedInfo = cityName
                    onInfosSelected(selectedInfo)
                }
            }) {
                Text(text = "Valider", fontSize = 36.sp)
            }
        }
    }
}

data class Train(
    val trainNumber: String,
    val departureTime: String,
    val arrivalTime: String
)

data class Trains(
    val trains: List<Train>
)

interface SNCFService {
    @GET("journeys")
    fun getTrains(
        @Header("Authorization") authorization: String,
        @Query("datetime") date: String,
        @Query("from") depart: String,
        @Query("to") destination: String
    ): Call<Trains>
}

@Composable
fun SelectTrain(
    onInfosSelected: (String) -> Unit,
    informations: Infos,
) {
    //appel API SNCF
    // 80ebf15b-8c29-4391-86f3-b936b4f1da22
    //Accédez à l'API : https://api.sncf.com/v1

    val date = informations.date
    val depart = informations.depart
    val destination = informations.destination
    val apiKey = "80ebf15b-8c29-4391-86f3-b936b4f1da22"

    Text(text = "Vos infos :")
    Spacer(modifier = Modifier.size(16.dp))
    Text(text = date)
    Spacer(modifier = Modifier.size(16.dp))
    Text(text = depart)
    Spacer(modifier = Modifier.size(16.dp))
    Text(text= destination)

    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.sncf.com/v1/coverage/sncf/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service = retrofit.create(SNCFService::class.java)
    val response = service.getTrains("Bearer $apiKey", date, "admin:$depart", "admin:$destination")

    

    // Afficher la réponse dans un Text
    if (trainState.value != null) {
        Text(text = trainState.value.toString(), fontSize = 36.sp)
    } else {
        // Afficher un indicateur de chargement ou un message d'erreur si nécessaire
        Text(text = "Chargement en cours...", fontSize = 36.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SNCFTheme {
        TicketBookingApp()
    }
}
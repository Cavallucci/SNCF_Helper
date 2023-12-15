package com.example.sncf

import Main
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
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
import androidx.compose.foundation.lazy.LazyColumn
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
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Url
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

@Composable
fun SelectTrain(
    onInfosSelected: (String) -> Unit,
    informations: Infos,
) {
    //appel API SNCF
    // 80ebf15b-8c29-4391-86f3-b936b4f1da22
    //Exemple trouver les trajets Paris - Lyon du 23/12/2023 : https://api.sncf.com/v1/coverage/sncf/journeys?from=stop_area:SNCF:87686006&to=stop_area:SNCF:87722025&datetime=20231223T140151

    val tokenAuth = "80ebf15b-8c29-4391-86f3-b936b4f1da22"

    //afficher l'exemple :

    val gson = GsonBuilder().setLenient().create()
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.sncf.com/v1/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val apiService = retrofit.create(SNCFService::class.java)

    // Exemple de trajets Paris - Lyon du 23/12/2023
    val gareDepart = "stop_area:SNCF:87686006"
    val gareArrivee = "stop_area:SNCF:87722025"
    val dateDepart = "20231223T100000"

    var departureTimes by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    fun fetchJourneys(url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getJourneys(url, tokenAuth)
                val journeys = response.journeys
                val publicTransportSections = journeys.flatMap { journey ->
                    for (section in journey.sections) {
                        if (section.type == "public_transport") {
                            return@flatMap listOf(section)
                        }
                    }
                    return@flatMap emptyList()
                }

                val sectionDepartures = publicTransportSections.mapNotNull { it.departure_date_time }
                departureTimes = departureTimes + sectionDepartures

                // Vérifiez s'il y a un lien pour la prochaine page de journeys
                val nextLink = response.links?.find { it.rel == "next" }?.href
                if (!nextLink.isNullOrBlank()) {
                    if (nextLink.substring(120, 128) == "20231224") {
                        return@launch
                    }
                    fetchJourneys(nextLink)
                }
            } catch (e: Exception) {
                Log.e("Error", "Erreur: ${e.message}")
                onInfosSelected("Erreur: ${e.message}")
            }
        }
    }
    Log.d("DepartureTimes", departureTimes.toString())
// Appel initial avec l'URL de départ
    LaunchedEffect(key1 = Unit) {
        val initialUrl = "https://api.sncf.com/v1/coverage/sncf/journeys?from=stop_area%3ASNCF%3A87686006&to=stop_area%3ASNCF%3A87722025&datetime=20231223T100000"
        fetchJourneys(initialUrl)
        isLoading = false
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            Text(text = "Trajets disponibles :", fontSize = 36.sp)
            Spacer(modifier = Modifier.size(16.dp))
        }

        if (isLoading) {
            item {
                Text(text = "Chargement...", fontSize = 36.sp)
            }
        } else {
            items(departureTimes.size) { index ->
                //parser index 20231223T100000 -> 10h00
                val hour = departureTimes[index].substring(9, 11)
                val minute = departureTimes[index].substring(11, 13)

                Text(text = "Départ : $hour:$minute", fontSize = 36.sp)
                Spacer(modifier = Modifier.size(16.dp))
            }

            if (departureTimes.isEmpty()) {
                item {
                    Text(text = "Aucun trajet disponible", fontSize = 36.sp)
                }
            }
        }
    }

}

interface SNCFService {
    @GET
    suspend fun getJourneys(
        @Url url: String,
        @Header("Authorization") token: String
    ): Main
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SNCFTheme {
        TicketBookingApp()
    }
}
package com.example.sncf

import Main
import Sections
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
    var infosTrain: Sections?,
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
    var selectedTrain by remember { mutableStateOf<Sections?>(null) }
    var infos by remember { mutableStateOf(Infos("", "", "", "", selectedTrain)) }

    when (currentStep) {
        1 -> Start(onNext = { currentStep = 2 })
        2 -> SelectPerson(
            onInfosSelected = { info ->
                infos.name = info
                currentStep = 3
            },
            onCancel = { currentStep = 1 }
        )
        3 -> SelectDate(
            onInfosSelected = { info ->
                infos.date = info
                currentStep = 4
            },
            onCancel = { currentStep = 1 }
        )
        4 -> SelectDepart(
            onInfosSelected = { info ->
                infos.depart = info
                currentStep = 5
            },
            question = "Départ ?",
            onCancel = { currentStep = 1 }
        )
        5 -> SelectDepart(
            onInfosSelected = { info ->
                infos.destination = info
                currentStep = 6
            },
            question = "Destination ?",
            onCancel = { currentStep = 1 }
        )
        6 -> SelectTrain(
            onInfosSelected = { info ->
                infos.infosTrain = info
                currentStep = 7
            },
            informations = infos,
            onCancel = { currentStep = 1 }
        )
        7 -> ResumInfos(
            train = infos.infosTrain ?: error("Selected train is null"),
            onCancel = { currentStep = 1 }
        )
    }
}

@Composable
fun CancelButton(onCancel: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Button(
            onClick = onCancel,
            colors = ButtonDefaults.buttonColors(Color.Red)
        ) {
            Text(text = "Annuler", fontSize = 26.sp, color = Color.White)
        }
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
    onCancel: () -> Unit
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
    CancelButton(onCancel)
}

@Composable
fun SelectDate(
    onInfosSelected: (String) -> Unit,
    onCancel: () -> Unit
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
    CancelButton(onCancel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectDepart(
    onInfosSelected: (String) -> Unit,
    question: String,
    onCancel: () -> Unit
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
    CancelButton(onCancel)
}

@Composable
fun SelectTrain(
    onInfosSelected: (Sections) -> Unit,
    informations: Infos,
    onCancel: () -> Unit
) {
    val tokenAuth = "80ebf15b-8c29-4391-86f3-b936b4f1da22"

    val gson = GsonBuilder().setLenient().create()
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.sncf.com/v1/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    val apiService = retrofit.create(SNCFService::class.java)

    val infos = recupInfos(informations)
    val gareDepart = infos.depart
    val gareArrivee = infos.destination
    val dateDepart = infos.date

    var departureSections by remember { mutableStateOf<List<Sections>>(emptyList()) }
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

                // Vérifiez s'il y a un lien pour la prochaine page de journeys
                val nextLink = response.links?.find { it.rel == "next" }?.href
                if (!nextLink.isNullOrBlank()) {
                    if (nextLink.substring(120, 128) != dateDepart) {
                        isLoading = false
                        return@launch
                    }
                    departureSections = departureSections + publicTransportSections
                    fetchJourneys(nextLink)
                }
            } catch (e: Exception) {
                Log.e("Error", "Erreur: ${e.message}")
                onInfosSelected(departureSections[0])
            }
        }
    }
// Appel initial avec l'URL de départ
    LaunchedEffect(key1 = Unit) {
        val initialUrl = "https://api.sncf.com/v1/coverage/sncf/journeys?from=${gareDepart}&to=${gareArrivee}&datetime=${dateDepart}T100000"
        fetchJourneys(initialUrl)
    }

    var selectedTrain by remember { mutableStateOf<Sections?>(null) }

    showTrains(departureSections, isLoading) { selectedTrain ->
        onInfosSelected(selectedTrain)
    }
    CancelButton(onCancel)
}

@Composable
fun showTrains(departureTimes: List<Sections>, isLoading: Boolean, onItemSelected: (Sections) -> Unit): Sections? {
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
            item {
                Text(text = "Cliquer sur le trajet de votre choix", fontSize = 36.sp)
            }
            items(departureTimes.size) { index ->
                //parser index 20231223T100000 -> 10h00
                val tmp = departureTimes[index]
                val departHour = tmp.departure_date_time.substring(9, 11)
                val departMinute = tmp.departure_date_time.substring(11, 13)
                val arriveeHour = tmp.arrival_date_time.substring(9, 11)
                val arriveeMinute = tmp.arrival_date_time.substring(11, 13)

                Button(onClick = {
                    onItemSelected(tmp)
                }) {
                    Text(text = "$departHour:$departMinute - $arriveeHour:$arriveeMinute", fontSize = 26.sp)
                }
                Spacer(modifier = Modifier.size(6.dp))
            }

            if (departureTimes.isEmpty()) {
                item {
                    Text(text = "Aucun trajet disponible", fontSize = 36.sp)
                }
            }
        }
    }
    return null
}

fun recupInfos(informations: Infos): Infos {
    val gareDepart = when (informations.depart) {
        "Liverdun" -> "stop_area:SNCF:87141069"
        "Nancy" -> "stop_area:SNCF:87141002"
        "Paris" -> "stop_area:SNCF:87113001"
        "Saverne" -> "stop_area:SNCF:87212225"
        else -> "stop_area:SNCF:87113001"
    }

    val gareArrivee = when (informations.destination) {
        "Liverdun" -> "stop_area:SNCF:87141069"
        "Nancy" -> "stop_area:SNCF:87141002"
        "Paris" -> "stop_area:SNCF:87113001"
        "Saverne" -> "stop_area:SNCF:87212225"
        else -> "stop_area:SNCF:87113001"
    }

    // Parse la date 01/01/2021 -> 20210101T100000
    val dateDepart = informations.date
    val day = dateDepart.substring(0, 2)
    val month = dateDepart.substring(3, 5)
    val year = dateDepart.substring(6, 10)
    val date = "$year$month$day"

    return informations.copy(date = date, depart = gareDepart, destination = gareArrivee)
}
interface SNCFService {
    @GET
    suspend fun getJourneys(
        @Url url: String,
        @Header("Authorization") token: String
    ): Main
}

@Composable
fun ResumInfos(train: Sections,
               onCancel: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "Résumé des informations", fontSize = 36.sp)
        Spacer(modifier = Modifier.size(16.dp))
        //afficher le to, from, datedepart, datearrivee
        val departHour = train.departure_date_time.substring(9, 11)
        val departMinute = train.departure_date_time.substring(11, 13)
        val arriveeHour = train.arrival_date_time.substring(9, 11)
        val arriveeMinute = train.arrival_date_time.substring(11, 13)
        Text(text="Départ depuis ${train.from.name} à $departHour:$departMinute", fontSize = 26.sp)
        Text(text="Arrivée à ${train.to.name} à $arriveeHour:$arriveeMinute", fontSize = 26.sp)
        Spacer(modifier = Modifier.size(16.dp))
        Button(onClick = {
            //envoyer les infos à l'API
        }) {
            Text(text = "Valider", fontSize = 36.sp)
        }
    }
    CancelButton(onCancel)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SNCFTheme {
        TicketBookingApp()
    }
}
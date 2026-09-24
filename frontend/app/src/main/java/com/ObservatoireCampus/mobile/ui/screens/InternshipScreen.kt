package com.ObservatoireCampus.mobile.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ObservatoireCampus.mobile.R

import com.ObservatoireCampus.mobile.ui.components.TopBar
import com.ObservatoireCampus.mobile.ui.theme.ObcampusBackground
import com.ObservatoireCampus.mobile.ui.theme.ObcampusPrimary
import com.ObservatoireCampus.mobile.viewmodel.LanguageViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InternshipScreen(
    languageViewModel: LanguageViewModel,
    onBack: () -> Unit
) {
    var titleText by remember { mutableStateOf("À propos") }
    var personalInfoTitle by remember { mutableStateOf("Informations Personnelles") }
    var nameLabel by remember { mutableStateOf("Nom & Prénom") }
    var birthLabel by remember { mutableStateOf("Date de naissance") }
    var schoolLabel by remember { mutableStateOf("École") }
    var levelLabel by remember { mutableStateOf("Niveau d'études") }
    var labLabel by remember { mutableStateOf("Laboratoire") }

    var stageContextTitle by remember { mutableStateOf("Contexte du Projet") }
    var stageObjectivesTitle by remember { mutableStateOf("Objectifs du Stage") }
    var supervisorsLabel by remember { mutableStateOf("Encadrant") }
    var durationLabel by remember { mutableStateOf("Durée") }

    var translatedName by remember { mutableStateOf("Ben Ayed Yasmine") }
    var translatedSchool by remember { mutableStateOf("ENSEIRB-MATMECA (Bordeaux)") }
    var translatedLevel by remember { mutableStateOf("2ème année - Cycle d'Ingénieur") }
    var translatedLab by remember { mutableStateOf("Laboratoire LaBRI (Bordeaux)") }

    var supervisor1 by remember { mutableStateOf("M. Mohamed Mosbah") }
    var supervisor2 by remember { mutableStateOf("M. Royston Fernandes") }

    val originalDescription = "Le campus de l'université de Bordeaux est l'un des plus vastes d'Europe (187 hectares, 58 000 usagers quotidiens). Pour répondre aux enjeux de fluidité et de monitoring en temps réel, la Chaire Mobilité et Transports Intelligents a initié l'outil « Observatoire Mobilité de Bordeaux Université ». L'objectif est de porter cet outil sur mobile pour offrir une expérience interactive et géolocalisée aux usagers."
    var translatedDescription by remember { mutableStateOf(originalDescription) }

    val obj1 = "La consultation en temps réel des données de mobilité du campus."
    val obj2 = "L'intégration et l'agrégation cohérente de sources hétérogènes."
    val obj3 = "L'implémentation d'indicateurs avancés (congestion, qualité, disponibilité)."
    val obj4 = "La mise en œuvre d'un module simple de recommandation ou de prédiction."

    var tObj1 by remember { mutableStateOf(obj1) }
    var tObj2 by remember { mutableStateOf(obj2) }
    var tObj3 by remember { mutableStateOf(obj3) }
    var tObj4 by remember { mutableStateOf(obj4) }

    LaunchedEffect(languageViewModel.currentLanguage) {
        titleText = languageViewModel.translate("À propos")
        personalInfoTitle = languageViewModel.translate("Informations Personnelles")
        nameLabel = languageViewModel.translate("Nom & Prénom")
        birthLabel = languageViewModel.translate("Date de naissance")
        schoolLabel = languageViewModel.translate("École")
        levelLabel = languageViewModel.translate("Niveau d'études")
        labLabel = languageViewModel.translate("Laboratoire")

        stageContextTitle = languageViewModel.translate("Contexte du Projet")
        stageObjectivesTitle = languageViewModel.translate("Objectifs du Stage")
        supervisorsLabel = languageViewModel.translate("Encadrant")
        durationLabel = languageViewModel.translate("Durée")

        translatedName = languageViewModel.translate("Ben Ayed Yasmine")
        translatedSchool = languageViewModel.translate("ENSEIRB-MATMECA (Bordeaux)")
        translatedLevel = languageViewModel.translate("2ème année - Cycle d'Ingénieur")
        translatedLab = languageViewModel.translate("Laboratoire LaBRI (Bordeaux)")

        supervisor1 = languageViewModel.translate("M. Mohamed Mosbah")
        supervisor2 = languageViewModel.translate("M. Royston Fernandes")

        translatedDescription = languageViewModel.translate(originalDescription)
        tObj1 = languageViewModel.translate(obj1)
        tObj2 = languageViewModel.translate(obj2)
        tObj3 = languageViewModel.translate(obj3)
        tObj4 = languageViewModel.translate(obj4)
    }

    Scaffold(
        topBar = {
            // Utilisation du composant TopBar commun pour afficher la Navbar visible
            TopBar(
                languageViewModel = languageViewModel,
                onMenuClick = onBack,
                isBackButton = true
            )
        },
        containerColor = ObcampusBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- PHOTO DE PROFIL RESTE STYLISÉE AVEC VOTRE IMAGE ---
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(ObcampusPrimary, ObcampusPrimary.copy(alpha = 0.6f))
                        )
                    )
                    .padding(4.dp) // Effet de bordure
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.yasmine),
                        contentDescription = "Photo de Yasmine",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- CARTE INFORMATIONS PERSONNELLES ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = ObcampusPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = personalInfoTitle,
                            style = MaterialTheme.typography.titleMedium,
                            color = ObcampusPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    InfoRow(label = nameLabel, value = translatedName)
                    InfoRow(label = birthLabel, value = "20/08/2002")
                    InfoRow(label = schoolLabel, value = translatedSchool)
                    InfoRow(label = levelLabel, value = translatedLevel)
                    InfoRow(label = labLabel, value = translatedLab)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- CARTE SUJET & CADRE DE STAGE ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Book,
                            contentDescription = null,
                            tint = ObcampusPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Développement d'une Application Mobile pour l'Observatoire Mobilité",
                            style = MaterialTheme.typography.titleMedium,
                            color = ObcampusPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        color = Color.LightGray.copy(alpha = 0.5f)
                    )

                    // Contexte
                    Text(
                        text = stageContextTitle,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = ObcampusPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = translatedDescription,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Justify,
                        color = Color.DarkGray,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Objectifs
                    Text(
                        text = stageObjectivesTitle,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = ObcampusPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    BulletPoint(tObj1)
                    BulletPoint(tObj2)
                    BulletPoint(tObj3)
                    BulletPoint(tObj4)

                    Spacer(modifier = Modifier.height(20.dp))

                    InfoRow(label = supervisorsLabel, value = supervisor1)
                    InfoRow(label = supervisorsLabel, value = supervisor2)

                    InfoRow(label = durationLabel, value = "Du 01/06/2026 au 30/09/2026")
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black
        )
    }
}

@Composable
fun BulletPoint(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 6.dp, end = 10.dp)
                .size(6.dp)
                .clip(CircleShape)
                .background(ObcampusPrimary)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.DarkGray
        )
    }
}
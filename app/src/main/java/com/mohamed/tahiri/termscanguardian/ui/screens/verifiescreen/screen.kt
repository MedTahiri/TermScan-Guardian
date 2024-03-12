package com.mohamed.tahiri.termscanguardian.ui.screens.verifiescreen

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.mohamed.tahiri.termscanguardian.R
import com.mohamed.tahiri.termscanguardian.data.Resulta
import com.mohamed.tahiri.termscanguardian.screen
import com.mohamed.tahiri.termscanguardian.ui.theme.TermScanGuardianTheme

@RequiresApi(Build.VERSION_CODES.P)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifieScreen(navController: NavHostController) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
    val value = prefs.getString("text_key", "")
    val gson = Gson()
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(text = "TermScan Guardian", color = Color.White,fontSize = MaterialTheme.typography.titleLarge.fontSize)
            },
            navigationIcon = {
                IconButton(onClick = { navController.navigate(screen.HomeScreen.name) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_small_left),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.primary)
        )
        Column(
            modifier = Modifier
                .fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Top
        ) {
            LazyColumn(modifier = Modifier.fillMaxWidth(.9f)) {
                item {
                    Text(
                        text = gson.fromJson(value.toString(), Resulta::class.java).summary.toLowerCase(),
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = MaterialTheme.typography.titleLarge.fontSize.div(1.5)
                    )
                }
                items(gson.fromJson(value.toString(), Resulta::class.java).sections) {
                    Card(modifier = Modifier.padding(0.dp,5.dp).shadow(5.dp, shape = RoundedCornerShape(10.dp)),colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background)) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = it.title.toLowerCase(),fontSize = MaterialTheme.typography.titleLarge.fontSize.div(1.5))
                                val iconTint = when (it.risk) {
                                    "low" -> Color.Green
                                    "middle" -> Color.Yellow
                                    "high" -> Color.Red
                                    else -> Color.Black
                                }
                                Icon(
                                    painter = painterResource(id = R.drawable.bulb),
                                    contentDescription = "",
                                    tint = iconTint
                                )
                            }
                        }
                        Text(modifier = Modifier.padding(10.dp), text = it.content.toLowerCase(),fontSize = MaterialTheme.typography.titleLarge.fontSize.div(1.5))
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.P)
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TermScanGuardianTheme {
        VerifieScreen(navController = rememberNavController())
    }
}
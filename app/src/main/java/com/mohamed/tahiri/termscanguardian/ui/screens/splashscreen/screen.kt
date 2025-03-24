package com.mohamed.tahiri.termscanguardian.ui.screens.splashscreen

import android.os.Handler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.mohamed.tahiri.termscanguardian.R
import com.mohamed.tahiri.termscanguardian.screen
import com.mohamed.tahiri.termscanguardian.ui.theme.TermScanGuardianTheme

@Composable
fun SplashScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primary),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.logo_foreground),
            contentDescription = "App Logo",
            tint = MaterialTheme.colorScheme.background,
            modifier = Modifier.size(300.dp)
        )
//        Spacer(modifier = Modifier.height(0.dp))
//        Text(
//            text = "TermScan\nGuardian",
//            color = MaterialTheme.colorScheme.background,
//            style = MaterialTheme.typography.headlineMedium,
//            textAlign = TextAlign.Center,
//            fontWeight = FontWeight.Bold
//        )
    }
    Handler().postDelayed({
        navController.navigate(screen.HomeScreen.name)
    }, 3000)
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TermScanGuardianTheme {
        SplashScreen(navController = rememberNavController())
    }
}
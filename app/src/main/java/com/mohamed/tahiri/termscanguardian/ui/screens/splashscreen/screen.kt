package com.mohamed.tahiri.termscanguardian.ui.screens.splashscreen

import android.os.Handler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
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
        Text(buildAnnotatedString {
            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.background)) {
                append("TermScan")
            }
            append("\n")
            append("\n")
            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.background)) {
                append(" Guardian")
            }
        }, fontSize = 40.sp, fontWeight = FontWeight.ExtraBold)
    }
    Handler().postDelayed({
        navController.navigate(screen.HomeScreen.name)
    }, 3000)
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    TermScanGuardianTheme {

    }
}
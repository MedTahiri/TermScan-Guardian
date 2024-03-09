package com.mohamed.tahiri.termscanguardian.ui.screens.splashscreen

import android.content.Context
import android.os.Handler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.mohamed.tahiri.termscanguardian.screen
import com.mohamed.tahiri.termscanguardian.ui.getNameFromFile
import com.mohamed.tahiri.termscanguardian.ui.saveNameToFile
import com.mohamed.tahiri.termscanguardian.ui.theme.TermScanGuardianTheme
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader

@Composable
fun SplashScreen(navController: NavHostController) {
    val context = LocalContext.current
    var showDialog = remember { mutableStateOf(false) }
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
    if (getNameFromFile(context) == "") {
        showDialog.value = true
    } else {
        Handler().postDelayed({
            navController.navigate(screen.HomeScreen.name)
        }, 3000)
    }
    if (showDialog.value) {
        GetNameDialog{ fullName ->
            saveNameToFile(context, fullName)
            showDialog.value = false
            navController.navigate(screen.HomeScreen.name)
        }
    }
}

@Composable
fun GetNameDialog(onNameEntered: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = { },
        title = { Text(text = "Enter Full Name") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    onNameEntered(name)
                }),
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onNameEntered(name)
                },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Save")
            }
        }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    TermScanGuardianTheme {

    }
}
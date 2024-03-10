package com.mohamed.tahiri.termscanguardian

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.mohamed.tahiri.termscanguardian.database.AppDatabase
import com.mohamed.tahiri.termscanguardian.database.DataModelViewModel
import com.mohamed.tahiri.termscanguardian.ui.screens.homescreen.HomeScreen
import com.mohamed.tahiri.termscanguardian.ui.screens.splashscreen.SplashScreen
import com.mohamed.tahiri.termscanguardian.ui.screens.verifiescreen.VerifieScreen
import com.mohamed.tahiri.termscanguardian.ui.theme.TermScanGuardianTheme

class MainActivity : ComponentActivity() {
    private val db by lazy {
        Room.databaseBuilder(
            context = applicationContext,
            klass = AppDatabase::class.java,
            name = "database.db"
        ).build()
    }
    private val viewModel by viewModels<DataModelViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DataModelViewModel(db.dataDao()) as T
                }
            }
        }
    )

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TermScanGuardianTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Main(viewModel)
                }
            }
        }

    }
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun Main(viewModel: DataModelViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = screen.SplashScreen.name) {
        composable(screen.SplashScreen.name) {
            SplashScreen(navController)
        }
        composable(screen.HomeScreen.name) {
            HomeScreen(navController, viewModel)
        }
        composable(screen.VerifieScreen.name) {
            VerifieScreen(navController)
        }
    }
}


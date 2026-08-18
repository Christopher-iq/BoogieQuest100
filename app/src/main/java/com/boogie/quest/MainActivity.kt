package com.boogie.quest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.boogie.quest.ui.BoogieApp
import com.boogie.quest.ui.theme.BoogieTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BoogieTheme {
                val vm: GameViewModel = viewModel()
                BoogieApp(vm)
            }
        }
    }
}

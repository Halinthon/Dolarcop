package com.dolarcop.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.dolarcop.app.ui.CalculatorScreen
import com.dolarcop.app.ui.theme.DolarCOPTheme
import com.dolarcop.app.viewmodel.CalculatorViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: CalculatorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DolarCOPTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CalculatorScreen(viewModel = viewModel)
                }
            }
        }
    }
}

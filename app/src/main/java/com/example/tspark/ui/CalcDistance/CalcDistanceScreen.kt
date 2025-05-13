package com.example.tspark.ui.CalcDistance

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tspark.ui.AppViewModelProvider

@Composable
fun CalcDistanceScreen(
    modifier: Modifier,
    viewModel: ViewModelCalcDistance = viewModel(factory = AppViewModelProvider.Factory)
) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Distance Calculator")

        //todo add text fields and buttons
    }
}
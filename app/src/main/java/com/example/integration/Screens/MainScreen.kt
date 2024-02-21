package com.example.integration.Screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController


@Composable
fun MainScreen(navController: NavController){
    Column(Modifier.fillMaxSize()) {
        Button(onClick = {
            navController.navigate("UpdateScreen")
        }) {
            Text(text = "Update Stock")
        }
        Button(onClick = {
            navController.navigate("Writing")
        }) {
            Text(text = "Create Stock")
        }
    }
}
package com.example.integration.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.integration.functionalities.FireBaseWrite

@Composable
fun EditScreen(navController: NavController,cat:String,subCat:String,name:String,pr:Int,q:Int,ur:String){

    var quantity = remember{ mutableStateOf(q.toString()) }
    var price = remember{ mutableStateOf(pr.toString()) }
    var imageURL by remember{ mutableStateOf(ur) }
    var nameit by remember{ mutableStateOf(name) }
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedTextField(label = { Text(text = "Name")},value = nameit, onValueChange = {
            nameit = it
        })
        OutlinedTextField(label = { Text(text = "Quantity")},value = quantity.value, onValueChange = {
            quantity.value = it
        })
        OutlinedTextField(label = { Text(text = "Price")},value = price.value, onValueChange = {
            price.value = it
        })
        Button(onClick = {
            FireBaseWrite(
                cat,
                subCat,
                nameit,
                quantity.value.toInt(),
                price.value.toInt(),
                imageURL
            )
            navController.navigateUp()
        }) {
            Text(text = "Save")
        }
    }

}

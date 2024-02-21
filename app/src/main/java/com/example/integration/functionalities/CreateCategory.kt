package com.example.integration.functionalities

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.database.database

@Composable
fun CreateCategory(navController: NavController){
    val cate = remember{ mutableStateOf("") }
    val check = remember{ mutableStateOf(false) }
    Column {

        OutlinedTextField(label = {Text(text = "Enter the Category")},value = cate.value, onValueChange = {cate.value = it})
        Button(onClick = { check.value = true }) {

        }
    }
    if(check.value){
        val db = Firebase.database.reference
        val cat = db.child(cate.value).child("__")
        cat.child("__").setValue(Product("name",0,0,"https://www.google.com/url?sa=i&url=https%3A%2F%2Fcommons.wikimedia.org%2Fwiki%2FFile%3AAndroid_robot.svg&psig=AOvVaw1DfMR0xhFnglZtyO9YeVug&ust=1707359884039000&source=images&cd=vfe&opi=89978449&ved=0CBMQjRxqFwoTCLiK8ayZmIQDFQAAAAAdAAAAABAE"))
        navController.navigateUp()
    }
}
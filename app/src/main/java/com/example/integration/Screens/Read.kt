package com.example.integration.Screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


@Composable
fun readFire(navController: NavController) {
    var listhere by remember{ mutableStateOf(mutableListOf<String>("")) }
    val ref: DatabaseReference = Firebase.database.reference
    ref.addValueEventListener(
        object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf("")
                val snappy = snapshot.value as Map<String,String>
                val snap2 = snappy.keys
                for(i in snap2){
                    val value = i
                    value.let {
                        list.add(it)
                    }
                }
                list.removeAt(0)
                listhere = list
            }

            override fun onCancelled(error: DatabaseError) {

            }
        }
    )
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        LazyColumn(Modifier.fillMaxWidth()) {
            items(listhere) {
                values ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                "subCat",
                                values
                            )
                            navController.navigate("SubCat")
                        }, horizontalArrangement = Arrangement.Center) {
                    Text(text = values)
                }
            }
        }
        Button(onClick = {
            navController.navigate("CreateCategory")
        },Modifier.wrapContentSize()) {
            Text(text = "Add category")
        }
    }

}


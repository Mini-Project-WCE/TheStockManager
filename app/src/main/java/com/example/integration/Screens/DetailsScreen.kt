package com.example.integration.Screens

import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.integration.functionalities.Product
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.parcelize.Parcelize


@SuppressLint("CoroutineCreationDuringComposition", "MutableCollectionMutableState",
    "SuspiciousIndentation"
)
@Composable
fun DetailsScreen(cat: String,subCat:String,navController: NavController){
    var itemList by remember{ mutableStateOf(mutableListOf<Product>())}
    val ref: DatabaseReference = Firebase.database.getReference(cat).child(subCat)
//    Firebase.database.reference.child(cat)
    ref.addValueEventListener(
        object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Product>()
                for(i in snapshot.children){
                    val quantity = i.child("qty").getValue(Int::class.java) ?: 0
                    val price = i.child("price").getValue(Int::class.java) ?: 0
                    val imageUrl = i.child("url").getValue(String::class.java) ?: ""

                    val firebaseItem = Product(i.key.toString(),quantity, price, imageUrl)
                    list.add(firebaseItem)
                }
                itemList = list
            }
            override fun onCancelled(error: DatabaseError) { }
        }
    )
    Column(Modifier.fillMaxSize()) {
        LazyColumn(Modifier.fillMaxWidth()) {
            items(itemList) { values ->
                if(values.name!="--")
                Row(Modifier.fillMaxSize()) {
                    Box(
                        Modifier
                            .height(100.dp)
                            .width(100.dp)
                            .background(Color.LightGray)
                    ) {
                        AsyncImage(
                            model = values.url,
                            contentDescription = null,
                            Modifier.fillMaxSize()
                        )
                    }
                    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
                        Row(
                            Modifier
                                .fillMaxWidth(), horizontalArrangement = Arrangement.Center
                        ) {
                            Text(text = values.name)
                        }
                        Row(
                            Modifier
                                .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Text(text = "Qty: ${values.qty}      Price: ₹ ${values.price}")
                        }
                        Button(onClick = {
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                "EditScr",
                                EditData(cat,subCat,values.name,values.qty,values.price,values.url)
                            )
                            navController.navigate("EditScreen")
                        }) {
                            Text(text = "Edit")
                        }
                    }
                }
            }
        }
        Button(onClick = {
            navController.currentBackStackEntry?.savedStateHandle?.set(
                "catSubCat",
                DetailsData(cat,subCat)
            )
            navController.navigate("Writing")
        }) {
            Text(text = "Add a new product")
        }
    }
}

@Parcelize
data class EditData(val cat:String,val subCat:String,val name:String,val qty:Int,val price:Int,val url:String):Parcelable
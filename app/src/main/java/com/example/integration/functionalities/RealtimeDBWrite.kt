package com.example.integration.functionalities

import com.google.firebase.Firebase
import com.google.firebase.database.database


fun FireBaseWrite(cate:String,subCat:String,name:String, qty:Int, price:Int,url:String){
    val db = Firebase.database.reference
    val cat = db.child(cate).child(subCat)
    cat.child(name).setValue(Product(name,price,qty,url))
}


data class Product(
    val name:String,val price:Int,val qty:Int,val url:String
)
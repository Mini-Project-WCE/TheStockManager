package com.example.integration.Screens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.integration.functionalities.FireBaseWrite
import com.google.firebase.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import java.io.ByteArrayOutputStream


@Composable
fun Writing(navController: NavController,cate:String,subCate:String){
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageUri = uri
        }
    var url by remember{ mutableStateOf("") }
    val name = remember{
        mutableStateOf("")
    }
    val cat = remember{
        mutableStateOf(cate)
    }
    val subCat = remember{
        mutableStateOf(subCate)
    }
    val qty = remember{
        mutableStateOf("")
    }
    val price = remember{
        mutableStateOf("")
    }
    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {

        imageUri?.let {
            if (Build.VERSION.SDK_INT < 28) {
                bitmap.value = MediaStore.Images
                    .Media.getBitmap(context.contentResolver, it)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, it)
                bitmap.value = ImageDecoder.decodeBitmap(source)
            }

            bitmap.value?.let { btm ->
                Image(
                    bitmap = btm.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(400.dp)
                        .padding(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = {
            navController.navigate("ClickImage")
        }) {
            Text(text = "Click Image")
        }
        Button(onClick = { launcher.launch("image/*") }) {
            Text(text = "Pick Image")
        }


        OutlinedTextField(value = cat.value, onValueChange = {
            cat.value = it
        }, label = { Text(text = "Category") })
        OutlinedTextField(value = subCat.value, onValueChange = {
            subCat.value = it
        }, label = { Text(text = "Sub Category") })
        OutlinedTextField(value = name.value, onValueChange = {
            name.value = it
        }, label = { Text(text = "Name") })
        OutlinedTextField(value = qty.value, onValueChange = {
            qty.value = it
        }, label = { Text(text = "Qty") })
        OutlinedTextField(value = price.value, onValueChange = {
            price.value = it
        }, label = { Text(text = "Price") })
        Button(onClick = {


            if(bitmap.value!=null) {
                val storage: FirebaseStorage = Firebase.storage
                val storageRef = storage.reference

                val mountainsRef = storageRef.child(cat.value).child("${name.value}.jpg")
                val baos = ByteArrayOutputStream()

                val bitmap1 = bitmap.value!!
                bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()
                val uploadTask = mountainsRef.putBytes(data)

                uploadTask.continueWithTask {
                    mountainsRef.downloadUrl
                }.addOnCompleteListener {
                    task ->
                    url = task.result.toString()

                    FireBaseWrite(
                        cat.value,
                        subCat.value,
                        name.value,
                        qty.value.toInt(),
                        price.value.toInt(),
                        url
                    )

                    navController.navigateUp()
                }
            }
        }) {
            Text(text = "Click")
        }
    }
}


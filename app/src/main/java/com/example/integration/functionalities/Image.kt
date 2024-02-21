package com.example.integration.functionalities

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
import coil.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import java.io.ByteArrayOutputStream


@Composable
fun PickImageFromGallery(navController: NavController) {

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageUri = uri
        }
    val cat = remember{ mutableStateOf("") }
    val name = remember{ mutableStateOf("") }
    val url = remember{ mutableStateOf("") }
    val check = remember{ mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {

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
        OutlinedTextField(label = { Text(text = "Category")},value = cat.value, onValueChange = {
            cat.value = it
        } )
        OutlinedTextField(label = { Text(text = "Name")},value = name.value, onValueChange = {
            name.value = it
        } )
        Button(
            onClick = {
                if(bitmap.value!=null) {
                    val storage: FirebaseStorage = Firebase.storage
                    val storageRef = storage.reference

                    val mountainsRef = storageRef.child(cat.value).child("${name.value}.jpg")
                    val baos = ByteArrayOutputStream()

                    val bitmap1 = bitmap.value!!
                    bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val data = baos.toByteArray()
                    val uploadTask = mountainsRef.putBytes(data)

                    uploadTask.continueWithTask{
                        mountainsRef.downloadUrl
                    }.addOnCompleteListener {
                            task->
                        url.value = task.result.toString()
                        check.value = true
                    }
                }
            }
        ) {
            Text(text = "Click")
        }
        if(check.value) {
            Firebase.storage.reference.child(cat.value).child("${name.value}.jpg").downloadUrl.addOnSuccessListener {
                url.value = it.toString()
            }

            AsyncImage(
                model = url.value,
                contentDescription = null,
            )
        }

    }

}




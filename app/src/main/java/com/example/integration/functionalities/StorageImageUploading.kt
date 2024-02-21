package com.example.integration.functionalities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClickImage(context: Context,navController: NavController){
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()
    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE or
                        CameraController.VIDEO_CAPTURE
            )
        }
    }
    val viewModel :MainViewModel = viewModel()
    val bitmaps by viewModel.bitmaps.collectAsState()

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            PhotoBottomSheetContent(
                bitmaps = bitmaps,
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            CameraPreview(
                controller = controller,
                modifier = Modifier
                    .fillMaxSize()
            )

            IconButton(
                onClick = {
                    controller.cameraSelector =
                        if (controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                            CameraSelector.DEFAULT_FRONT_CAMERA
                        } else CameraSelector.DEFAULT_BACK_CAMERA
                },
                modifier = Modifier
                    .offset(16.dp, 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Cameraswitch,
                    contentDescription = "Switch camera"
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                IconButton(
                    onClick = {
                        scope.launch {
                            scaffoldState.bottomSheetState.expand()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Photo,
                        contentDescription = "Open gallery"
                    )
                }
                IconButton(
                    onClick = {
                        takePhoto(
                            controller = controller,
                            context,
                            navController
                        )
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Take photo"
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.R)
private fun takePhoto(
    controller: LifecycleCameraController,
    context: Context,
    navController: NavController
) {
    val file = "/storage/emulated/0/Pictures"
    val ext = System.currentTimeMillis().toString()
    val outputFile = File("$file/$ext.jpg")

    val outputFileOptions: ImageCapture.OutputFileOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()

    controller.takePicture(outputFileOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {

                val compressedImageByteArray = compressImage(outputFile.path, 200) // Adjust quality as needed

                val compressedOutputFile = File("$file/${ext}_compressed.jpg")
                val outputStream = FileOutputStream(compressedOutputFile)
                outputStream.write(compressedImageByteArray)
                outputStream.close()

                val savedUri = Uri.fromFile(compressedOutputFile)
                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(savedUri.path),
                    arrayOf("image/jpeg")
                ) { _, uri ->
                    Handler(Looper.getMainLooper()).post {
                        navController.navigateUp()
                    }
                }
            }

            override fun onError(exception: ImageCaptureException) {
                Log.d("Error", "Error Occurred")
            }
        }
    )
}


fun compressImage(imagePath: String, targetSizeKb: Int): ByteArray {
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(imagePath, options)

    options.inSampleSize = calculateInSampleSize(options, targetSizeKb)

    options.inJustDecodeBounds = false
    val bitmap = BitmapFactory.decodeFile(imagePath, options)

    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

    return stream.toByteArray()
}

private fun calculateInSampleSize(options: BitmapFactory.Options, targetSizeKb: Int): Int {
    val imageHeight = options.outHeight
    val imageWidth = options.outWidth
    val imageSizeKb = (imageHeight * imageWidth * 4) / 1024

    var inSampleSize = 1
    while (imageSizeKb / (inSampleSize * inSampleSize) > targetSizeKb) {
        inSampleSize *= 2
    }

    return inSampleSize
}

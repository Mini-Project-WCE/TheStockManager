package com.example.integration

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.integration.Screens.DetailsData
import com.example.integration.Screens.DetailsScreen
import com.example.integration.Screens.EditData
import com.example.integration.Screens.EditScreen
import com.example.integration.Screens.MainScreen
import com.example.integration.Screens.SubCategories
import com.example.integration.Screens.Writing
import com.example.integration.Screens.readFire
import com.example.integration.functionalities.ClickImage
import com.example.integration.functionalities.CreateCategory
import com.example.integration.functionalities.CreateSubCategory


@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun Navigation(){
    val context = LocalContext.current
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "UpdateScreen"){
        composable("MainScreen"){
            MainScreen(navController)
        }
        composable("UpdateScreen"){
            readFire(navController)
        }
        composable("Writing"){
            val data = navController.previousBackStackEntry?.savedStateHandle?.get<DetailsData>("catSubCat")?:DetailsData("","")
            Writing(navController,data.value1,data.value2)
        }
        composable("SubCat"){
            SubCategories(cat = navController.previousBackStackEntry?.savedStateHandle?.get<String>("subCat")?:"",navController)
        }
        composable("details"){
            val data = navController.previousBackStackEntry?.savedStateHandle?.get<DetailsData>("details")?:DetailsData("","")
            DetailsScreen(data.value1,data.value2,navController)
        }
        composable("ClickImage"){
            ClickImage(context = context, navController = navController)
        }
        composable("CreateCategory"){
            CreateCategory(navController)
        }
        composable("CreateSubCategory"){
            CreateSubCategory(navController.previousBackStackEntry?.savedStateHandle?.get<String>("Cat")?:"--",navController)
        }
        composable("EditScreen"){
            val data = navController.previousBackStackEntry?.savedStateHandle?.get<EditData>("EditScr")?:EditData("","","",0,0,"")
            EditScreen(navController,data.cat,data.subCat,data.name,data.price,data.qty,data.url)
        }
    }
}

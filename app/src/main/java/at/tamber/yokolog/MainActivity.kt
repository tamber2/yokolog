package at.tamber.yokolog

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import at.tamber.yokolog.screens.GraphScreen
import at.tamber.yokolog.screens.edit.EditItemScreen
import at.tamber.yokolog.screens.edit.ItemEditViewModelProvider
import at.tamber.yokolog.screens.home.HomeScreen
import at.tamber.yokolog.screens.itemtypes.edit.ItemTypeEditScreen
import at.tamber.yokolog.screens.itemtypes.edit.ItemTypeEditViewModelProvider
import at.tamber.yokolog.screens.itemtypes.view.ItemTypesScreen
import at.tamber.yokolog.screens.itemtypes.view.ItemTypesViewModelProvider
import at.tamber.yokolog.screens.settings.SettingsScreen
import at.tamber.yokolog.ui.theme.YokologTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext
import java.io.FileDescriptor
import java.io.OutputStream
import java.nio.charset.Charset


const val KEY_ITEM_ID: String = "KEY_ITEM_ID"

class MainActivity : ComponentActivity(), FileSelectionEntryPoint {
    private val TAG = "MainActivity"
    private val fileSelectionInteractor: StorageAccessFrameworkInteractor = StorageAccessFrameworkInteractor(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: starting")
        setContent {
            YokologTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigation(rememberNavController(), fileSelectionInteractor)
                }
            }
        }

    }

    override val fileSelectionOwner: ComponentActivity
        get() = this

    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    private val scope = CoroutineScope(newSingleThreadContext("fileOutputStream"))

    override fun onFileCreated(fileDescriptor: FileDescriptor?, fileUri: Uri?) {
        scope.launch {
            withContext(Dispatchers.IO) {
                (application as Application).container.itemsRepository.getAllItemsStream().collect { items ->
                    var result = "id;datetime;description;type;+\n"
                    items.forEach {
                        result += it.item.id.toString() + ";" +
                                getDate(it.item.timestamp) + ";" +
                                it.item.description + ";" +
                                it.itemType.name + ";\n"
                    }
                    Log.d(TAG, "export result:\n$result")

                    //file is there, write content
                    if (fileUri != null) {
                        val fileOutputStream: OutputStream? = contentResolver.openOutputStream(fileUri)
                        withContext(Dispatchers.IO) {
                            fileOutputStream?.write(result.toByteArray(Charset.forName("UTF-8")))
                            fileOutputStream?.close()
                        }
                    }
                }
            }
        }
    }

    override fun onFileSelected(fileDescriptor: FileDescriptor?) {
    }
}

@Composable
fun Navigation(navController: NavHostController, fileSelectionInteractor: StorageAccessFrameworkInteractor) {
    NavHost(navController = navController, startDestination = Screen.MainScreen.route) {
        composable(route = Screen.SettingsScreen.route) {
            SettingsScreen(navController = navController) {
                fileSelectionInteractor.beginCreatingFile(CreateFileParams(fileMimeType = "csv/text", fileExtension = "csv", suggestedName = "yokolog_export"))
            }
        }
        composable(route = Screen.ItemTypeScreen.route) {
            ItemTypesScreen(viewModel = viewModel(factory = ItemTypesViewModelProvider.Factory()), navController = navController)
        }
        composable(route = Screen.MainScreen.route) {
            HomeScreen(navController = navController)
        }
        composable(route = Screen.GraphScreen.route) {
            GraphScreen(navController = navController)
        }
        composable(
            route = Screen.EditItemScreen.route,
            arguments = listOf(
                navArgument(KEY_ITEM_ID) {
                    type = NavType.IntType
                    defaultValue = 0
                }
            )) {
            val id = it.arguments?.getInt(KEY_ITEM_ID)!!
            EditItemScreen(viewModel = viewModel(factory = ItemEditViewModelProvider.Factory(itemId = id)), navController = navController)
        }
        composable(
            route = Screen.ItemTypeEditScreen.route,
            arguments = listOf(
                navArgument(KEY_ITEM_ID) {
                    type = NavType.IntType
                    defaultValue = 0
                }
            )) {
            val id = it.arguments?.getInt(KEY_ITEM_ID)!!
            ItemTypeEditScreen(viewModel = viewModel(factory = ItemTypeEditViewModelProvider.Factory(itemId = id)), navController = navController)
        }
    }
}

sealed class Screen(val route: String) {
    data object MainScreen : Screen(route = "MainScreen")
    data object SettingsScreen : Screen(route = "SettingsScreen")
    data object GraphScreen : Screen(route = "GraphScreen")
    data object ItemTypeScreen : Screen(route = "ItemTypeScreen")
    data object EditItemScreen : Screen(route = "EditItemScreen/{KEY_ITEM_ID}") {
        fun createRoute(id: Int) = "EditItemScreen/$id"
    }

    data object ItemTypeEditScreen : Screen(route = "EditItemTypeScreen/{KEY_ITEM_ID}") {
        fun createRoute(id: Int) = "EditItemTypeScreen/$id"
    }

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }


}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    YokologTheme {
        //Navigation(rememberNavController(), null)
    }
}

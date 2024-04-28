package at.tamber.yokolog.screens.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import at.tamber.yokolog.AppViewModelProvider
import at.tamber.yokolog.Application
import at.tamber.yokolog.R
import at.tamber.yokolog.Screen
import at.tamber.yokolog.db.ItemType
import at.tamber.yokolog.screens.add.ItemViewModel
import at.tamber.yokolog.screens.add.NewEntryScreen
import at.tamber.yokolog.ui.theme.YokologTheme
import kotlinx.coroutines.launch

private const val TAG: String = "HomeScreen"

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController, viewModel: ItemViewModel = viewModel(factory = AppViewModelProvider.Factory)) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }


    val coroutineScope = rememberCoroutineScope()
    val isDebuggable = 0 != LocalContext.current.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE
    Log.d(TAG, "isDebuggable: $isDebuggable")
    val context = LocalContext.current

    coroutineScope.launch {
        addBasicItemTypesIfDbEmpty(context)
    }

    if (isDebuggable) {
        coroutineScope.launch {
            addDebugItems(viewModel, context)
        }
    }

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(stringResource(R.string.app_name))
            }, actions = {
                // RowScope here, so these icons will be placed horizontally
                IconButton(onClick = { navController.navigate(Screen.SettingsScreen.route) }) {
                    Icon(Icons.Filled.Settings, contentDescription = stringResource(id = R.string.settings))
                }
                /*  IconButton(onClick = { navController.navigate(Screen.GraphScreen.route) }) {
                      Icon(Icons.Filled.AutoGraph, contentDescription = null)
                  }*/
            })
    }, floatingActionButton = {
        ExtendedFloatingActionButton(
            text = { Text(stringResource(R.string.add_new_item)) },
            icon = { Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_new_item)) },
            onClick = {
                showBottomSheet = true
            }
        )
    }) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ListScreen(navController)
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState
            ) {
                NewEntryScreen({
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showBottomSheet = false
                        }
                    }
                })
            }
        }
    }
}

suspend fun addBasicItemTypesIfDbEmpty(context: Context) {
    val repo = (context.applicationContext as Application).container.itemTypeRepository
    repo.getAllItemsStream().collect { items ->
        if (items.isNotEmpty()) {
            Log.d(TAG, "db contains items, not adding basic types")
        } else {
            repo.insertItems(
                listOf(
                    ItemType(name = "Weight", color = "#FFEB3B", isNumeric = true, unit = "kg"),
                    ItemType(name = "Excrement", color = "#D500F9", isNumeric = false, unit = ""),
                    ItemType(name = "Heat", color = "#9E9E9E", isNumeric = false, unit = "")
                )
            )
        }
    }
}

private suspend fun addDebugItems(viewModel: ItemViewModel, context: Context) {
    val repo = (context.applicationContext as Application).container.itemsRepository
    repo.getAllItemsStream().collect { items ->
        if (items.isNotEmpty()) {
            Log.d(TAG, "db contains items, not adding debug items")
        } else {
            Log.d(TAG, "db empty, adding debug items")
            for (i in 1..9999) { //FIXME debug items
                /*  when (IconType.entries.shuffled().first()) {
                      IconType.POOP -> viewModel.saveItem("test poop lol", IconType.POOP)
                      IconType.WEIGHT -> {
                          val randomValues = Random.nextFloat() * Random.nextInt(3, 30)
                          viewModel.saveItem(randomValues.toString(), IconType.WEIGHT)
                      }

                      IconType.HEAT -> viewModel.saveItem("test heat", IconType.HEAT)
                  }*/
            }
        }
    }
}


@Preview
@Composable
fun PreviewHomeScreen() {
    YokologTheme {
        //Navigation(rememberNavController(), fileSelectionInteractor)
    }
}
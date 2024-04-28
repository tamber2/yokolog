package at.tamber.yokolog.screens.itemtypes.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import at.tamber.yokolog.AppViewModelProvider
import at.tamber.yokolog.R
import at.tamber.yokolog.Screen
import at.tamber.yokolog.db.ItemType
import at.tamber.yokolog.getColorFromString
import at.tamber.yokolog.ui.theme.YokologTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemTypesScreen(navController: NavHostController, viewModel: ItemTypesViewModel = viewModel(factory = AppViewModelProvider.Factory)) {
    val homeUiState by viewModel.state.collectAsState()
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.item_types_title))
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
            )
        }, floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(stringResource(R.string.add_new_item_type)) },
                icon = { Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_new_item_type_icon)) },
                onClick = {
                    showBottomSheet = true
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(homeUiState.itemList) { item ->
                    ItemRow(item, navController)
                }
            }
        }
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState
            ) {
                NewItemTypeScreen(onSaved = {
                    showBottomSheet = false
                })
            }
        }
    }
}


@Composable
fun ItemRow(item: ItemType, navController: NavHostController) {
    Column {
        ListItem(headlineContent = {
            Text(item.name)
        },
            leadingContent = {
                Box(
                    Modifier
                        .clip(shape = CircleShape)
                        .width(24.dp)
                        .height(24.dp)
                        .background(getColorFromString(item.color))
                        .shadow(12.dp)
                )
            },
            modifier = Modifier.background(getColorFromString(item.color), shape = RectangleShape),
            trailingContent = {
                Row {
                    IconButton(onClick = {
                        navController.navigate(Screen.ItemTypeEditScreen.createRoute(item.id))
                    }) {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = stringResource(R.string.edit),
                        )
                    }
                }
            })
        HorizontalDivider()
    }
}

@Preview
@Composable
fun PreviewItemTypesScreen() {
    YokologTheme {
        Surface {
            ItemTypesScreen(rememberNavController(), viewModel(factory = ItemTypesViewModelProvider.Factory()))
        }
    }
}

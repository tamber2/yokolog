package at.tamber.yokolog.screens.home

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import at.tamber.yokolog.AppViewModelProvider
import at.tamber.yokolog.R
import at.tamber.yokolog.Screen
import at.tamber.yokolog.db.ItemWithType
import at.tamber.yokolog.getColorFromString
import at.tamber.yokolog.getDate
import at.tamber.yokolog.screens.add.ItemViewModel
import at.tamber.yokolog.ui.theme.YokologTheme

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ListScreen(navController: NavHostController, viewModel: ItemViewModel = viewModel(factory = AppViewModelProvider.Factory)) {
    val homeUiState by viewModel.homeUiState.collectAsState()

    if (homeUiState.itemList.isEmpty()) {
        //show add new item hint
        AddNewItemHint()
    } else {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(homeUiState.itemList) { item ->
                ItemRow(item, navController)
            }
        }
    }
}

@Composable
fun AddNewItemHint() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ElevatedCard(
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.size(width = 240.dp, height = 100.dp)
        ) {
            Text(
                text = stringResource(R.string.add_new_item_hint), modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 0.dp), textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun ItemRow(item: ItemWithType, navController: NavHostController) {
    Column {
        ListItem(headlineContent = { Text(getHeadlineText(item)) },
            leadingContent = {
                Box(
                    Modifier
                        .clip(shape = CircleShape)
                        .width(24.dp)
                        .height(24.dp)
                        .background(getColorFromString(item.itemType.color))
                        .shadow(12.dp)
                )
            },
            supportingContent = { Text(getDate(item.item.timestamp)) },
            trailingContent = {
                IconButton(onClick = {
                    navController.navigate(Screen.EditItemScreen.createRoute(item.item.id))
                }) {
                    Icon(
                        Icons.Filled.Edit,
                        contentDescription = stringResource(R.string.edit),
                    )
                }
            })
        HorizontalDivider()
    }
}

fun getHeadlineText(itemWithType: ItemWithType): String {
    return if (itemWithType.itemType.unit.isNotEmpty()) {
        itemWithType.itemType.name + ": " + itemWithType.item.description + " " + itemWithType.itemType.unit
    } else {
        itemWithType.itemType.name + ": " + itemWithType.item.description
    }
}

@Preview
@Composable
fun PreviewListScreen() {
    YokologTheme {
        Surface { ListScreen(rememberNavController()) }
    }
}


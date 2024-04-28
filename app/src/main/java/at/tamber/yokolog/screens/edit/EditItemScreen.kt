package at.tamber.yokolog.screens.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import at.tamber.yokolog.R
import at.tamber.yokolog.screens.add.ItemView
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditItemScreen(viewModel: ItemEditViewModel, navController: NavHostController) {
    val scope = rememberCoroutineScope()
    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text("Edit entry")
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.back)
                    )
                }
            },
            actions = {
                IconButton(onClick = {
                    scope.launch {
                        viewModel.saveChanges()
                        //navigate back
                        navController.popBackStack()
                    }
                }) {
                    Icon(Icons.Filled.Save, contentDescription = null)
                }
                IconButton(onClick = {
                    scope.launch {
                        viewModel.deleteItem()
                        //navigate back
                        navController.popBackStack()
                    }
                }) {
                    Icon(Icons.Filled.DeleteForever, contentDescription = null)
                }

            },
        )
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            EditItemScreenContent(viewModel)
        }
    }
}

@Composable
fun EditItemScreenContent(viewModel: ItemEditViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 0.dp, 0.dp, 24.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (viewModel.item.value != null) {
            LazyRow {
                items(1) {
                    viewModel.item.value?.itemType?.let { iconTypeId ->
                        ItemView(
                            index = 0,
                            selected = false,
                            onClick = { },
                            label = iconTypeId.name
                        )
                    }
                }
            }
            var text by remember { mutableStateOf(viewModel.item.value!!.item.description) }
            OutlinedTextField(
                leadingIcon = { Icon(Icons.Outlined.Description, contentDescription = "Description text input field") },
                value = text,
                placeholder = { Text(text = "Enter your description") },
                onValueChange = {
                    text = it
                    viewModel.editedDescription(it)
                },
                label = { Text("") },
                keyboardOptions = if (viewModel.item.value?.itemType?.isNumeric == true) KeyboardOptions(keyboardType = KeyboardType.Number) else KeyboardOptions(keyboardType = KeyboardType.Text)
            )
        }
    }
}

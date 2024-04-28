package at.tamber.yokolog.screens.itemtypes.edit

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Scale
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import at.tamber.yokolog.AppViewModelProvider
import at.tamber.yokolog.R
import at.tamber.yokolog.StateException
import at.tamber.yokolog.color
import at.tamber.yokolog.toHexCode
import at.tamber.yokolog.ui.theme.YokologTheme
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import kotlinx.coroutines.launch


private const val TAG: String = "EditItemTypeScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemTypeEditScreen(viewModel: ItemTypeEditViewModel = viewModel(factory = AppViewModelProvider.Factory), navController: NavHostController) {
    val homeUiState by viewModel.itemsState.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text("Edit item type")
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
                        try {
                            viewModel.save(context)
                            navController.popBackStack()
                        } catch (ex: StateException) {
                            Toast.makeText(context, ex.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }) {
                    Icon(Icons.Filled.Save, contentDescription = null)
                }
                IconButton(onClick = {
                    scope.launch {
                        try {
                            viewModel.delete(context)
                            navController.popBackStack()
                        } catch (ex: StateException) {
                            Toast.makeText(context, ex.message, Toast.LENGTH_SHORT).show()
                        }
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
            EditItemTypeScreenContent(viewModel)
        }
    }
}

@Composable
fun EditItemTypeScreenContent(viewModel: ItemTypeEditViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 0.dp, 0.dp, 24.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!viewModel.item.isInitialized) {
            return
        }

        var text by remember { mutableStateOf(viewModel.item.value!!.name) }
        OutlinedTextField(
            leadingIcon = { Icon(Icons.Outlined.Description, contentDescription = "Description text input field") },
            value = text,
            placeholder = { Text(text = "Enter your description") },
            onValueChange = {
                text = it
                viewModel.editName(it)
            },
            label = { Text("") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        Spacer(Modifier.size(8.dp))

        //unit text input
        var unitText by remember { mutableStateOf(viewModel.item.value!!.unit) }
        OutlinedTextField(
            leadingIcon = {
                Icon(
                    Icons.Outlined.Scale,
                    contentDescription = stringResource(R.string.unit_icon)
                )
            },
            value = unitText,
            onValueChange = {
                unitText = it
                viewModel.editUnit(it)
            },
            label = { Text(stringResource(id = R.string.unit_label)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    //keyboardController?.hide()
                }
            ),
        )

        Spacer(Modifier.size(8.dp))

        val colorPickerSize = 100.dp
        //color input
        val colorPickerController = rememberColorPickerController()
        Row(verticalAlignment = Alignment.CenterVertically) {
            HsvColorPicker(
                initialColor = viewModel.item.value!!.color.color,
                modifier = Modifier
                    .width(colorPickerSize)
                    .height(colorPickerSize),
                controller = colorPickerController,
                onColorChanged = {
                    viewModel.editColor(it.color.toHexCode())
                }
            )
            Spacer(Modifier.size(8.dp))
            Box(
                Modifier
                    .clip(shape = CircleShape)
                    .width(colorPickerSize)
                    .height(colorPickerSize)
                    .background(colorPickerController.selectedColor.value)
            )
        }

        Spacer(Modifier.size(8.dp))
    }
}

@Preview
@Composable
fun PreviewEditEntryScreen() {
    YokologTheme {
    }
}


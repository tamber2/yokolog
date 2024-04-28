package at.tamber.yokolog.screens.itemtypes.view

import android.util.Log
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Scale
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import at.tamber.yokolog.AppViewModelProvider
import at.tamber.yokolog.R
import at.tamber.yokolog.StateException
import at.tamber.yokolog.ui.theme.YokologTheme
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import kotlinx.coroutines.launch


private const val TAG: String = "NewEntryScreen"

@Composable
fun NewItemTypeScreen(viewModel: ItemTypesViewModel = viewModel(factory = AppViewModelProvider.Factory), onSaved: () -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp, 0.dp, 24.dp, 24.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //name text input
        var descriptionText by remember { mutableStateOf("") }
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    Icons.Outlined.Description,
                    contentDescription = stringResource(R.string.description_text_input_field)
                )
            },
            value = descriptionText,
            onValueChange = { descriptionText = it },
            label = { Text(stringResource(R.string.description_placeholder)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.moveFocus(
                        focusDirection = FocusDirection.Next,
                    )
                }
            ),
        )

        Spacer(Modifier.size(8.dp))

        //unit text input
        var unitText by remember { mutableStateOf("") }
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    Icons.Outlined.Scale,
                    contentDescription = stringResource(R.string.unit_icon)
                )
            },
            value = unitText,
            onValueChange = { unitText = it },
            label = { Text(stringResource(id = R.string.unit_label)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                }
            ),
        )

        Spacer(Modifier.size(8.dp))

        var isNumeric by remember { mutableStateOf(false) }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(id = R.string.is_numeric_label))
            Spacer(Modifier.size(8.dp))
            Switch(
                checked = isNumeric,
                onCheckedChange = {
                    isNumeric = it
                },
                thumbContent = if (isNumeric) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            modifier = Modifier.size(SwitchDefaults.IconSize),
                        )
                    }
                } else {
                    null
                }
            )
        }
        Spacer(Modifier.size(8.dp))

        val colorPickerSize = 100.dp
        //color input
        val colorPickerController = rememberColorPickerController()
        Row(verticalAlignment = Alignment.CenterVertically) {
            HsvColorPicker(
                modifier = Modifier
                    .width(colorPickerSize)
                    .height(colorPickerSize),
                controller = colorPickerController,
                onColorChanged = {
                    // do something
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

        OutlinedButton(
            onClick = {
                Log.d(TAG, "save clicked")
                if (descriptionText.trim().isEmpty()) {
                    Toast.makeText(context, context.getString(R.string.error_empty_description), Toast.LENGTH_SHORT).show()
                } else {
                    coroutineScope.launch {
                        try {
                            viewModel.save(descriptionText.trim(), unitText.trim(), colorPickerController.selectedColor.value, isNumeric)
                            onSaved.invoke()
                        } catch (ex: StateException) {
                            Toast.makeText(context, context.getString(R.string.error_cannot_add_type), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
        ) {
            Text("Save")
        }
        Spacer(Modifier.size(16.dp))
    }
}

@Preview
@Composable
fun PreviewNewEntryScreen() {
    YokologTheme {
        Surface { NewItemTypeScreen(onSaved = {}) }
    }
}


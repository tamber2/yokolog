package at.tamber.yokolog.screens.add

import android.content.Context
import android.text.format.DateUtils
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import at.tamber.yokolog.AppViewModelProvider
import at.tamber.yokolog.R
import at.tamber.yokolog.db.ItemType
import at.tamber.yokolog.ui.theme.YokologTheme
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Date


private const val TAG: String = "NewEntryScreen"

@Composable
fun NewEntryScreen(
    onSaveClicked: () -> Unit, viewModel: ItemViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val typeState by viewModel.itemTypeState.collectAsState()
    val errorText = stringResource(R.string.error_input)
    if (typeState.itemTypeList.isEmpty()) {
        return
    }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 0.dp, 0.dp, 24.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var textInputValue by remember { mutableStateOf("") }

        var selectedType by remember { mutableStateOf(typeState.itemTypeList[0]) }
        LazyColumnWithSelection(typeState.itemTypeList) {
            selectedType = it
            textInputValue = ""
        }

        Row {
            DatePickerWithDialog()
            Spacer(modifier = Modifier.size(16.dp))
            TimePickerWithDialog()
        }
        val focusRequester = remember { FocusRequester() }
        OutlinedTextField(
            modifier = Modifier.focusRequester(focusRequester),
            leadingIcon = {
                Icon(
                    Icons.Outlined.Description,
                    contentDescription = "Description text input field"
                )
            },
            value = textInputValue,
            placeholder = { Text(text = stringResource(R.string.description_placeholder)) },
            onValueChange = { textInputValue = it },
            label = { Text("") },
            keyboardOptions = if (selectedType.isNumeric) KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)
            else
                KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    //save on done
                    coroutineScope.launch {
                        if (save(viewModel, context, textInputValue.trim(), selectedType, errorText)) {
                            onSaveClicked.invoke()
                        }
                    }
                }
            ),
        )
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        Spacer(Modifier.size(8.dp))
        OutlinedButton(
            onClick = {
                Log.d(TAG, "save clicked")
                coroutineScope.launch {
                    if (save(viewModel, context, textInputValue.trim(), selectedType, errorText)) {
                        onSaveClicked.invoke()
                    }
                }
            },
        ) {
            Text("Save")
        }
    }
}

suspend fun save(viewModel: ItemViewModel, context: Context, description: String, itemType: ItemType, errorText: String): Boolean {
    return if (viewModel.validateInput(description = description, itemType = itemType)) {
        viewModel.saveItem(description = description, itemType = itemType)
        true
    } else {
        //show toast?
        Toast.makeText(context, errorText, Toast.LENGTH_SHORT).show()
        false
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerWithDialog() {
    val date = Date().time
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = date,
        yearRange = IntRange(currentYear - 10, currentYear + 10)
    )
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val colors: DatePickerColors = DatePickerDefaults.colors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainer)

        DatePickerDialog(
            colors = colors,
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                    }
                ) { Text(stringResource(R.string.close).uppercase()) }
            },
        )
        {
            DatePicker(
                state = datePickerState,
            )
        }
    }
    val formatter = DateFormat.getDateInstance(DateFormat.SHORT)
    Button(onClick = { showDatePicker = true }) {
        Text(text = formatter.format(datePickerState.selectedDateMillis!!))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerWithDialog() {
    val context = LocalContext.current
    val current = LocalDateTime.now()
    val selectedHour by remember { mutableIntStateOf(current.hour) }
    val selectedMinute by remember { mutableIntStateOf(current.minute) }
    var showDialog by remember { mutableStateOf(false) }
    val timeState = rememberTimePickerState(
        initialHour = selectedHour,
        initialMinute = selectedMinute
    )

    if (showDialog) {
        BasicAlertDialog(
            onDismissRequest = { showDialog = false },
            modifier =
            Modifier
                .clip(RoundedCornerShape(24.dp))
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainer),
        ) {
            Column(
                modifier = Modifier.padding(12.dp, 16.dp, 12.dp, 0.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TimePicker(state = timeState)
                Row(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth(), horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { showDialog = false }) {
                        Text(text = stringResource(id = R.string.close).uppercase())
                    }
                }
            }
        }
    }

    Button(onClick = { showDialog = true }) {
        val time = Calendar.getInstance()

        time.set(Calendar.HOUR_OF_DAY, timeState.hour)
        time.set(Calendar.MINUTE, timeState.minute)

        val timeString = DateUtils.formatDateTime(context, time.timeInMillis, DateUtils.FORMAT_SHOW_TIME)
        Text(text = timeString)
    }
}

@Composable
fun LazyColumnWithSelection(items: List<ItemType>, onClick: (ItemType) -> Unit) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val onItemClick = { index: Int ->
        selectedIndex = index
        onClick.invoke(items[index])
    }

    LazyRow {
        items(items.count()) { index ->
            ItemView(
                index = index,
                selected = selectedIndex == index,
                onClick = onItemClick,
                label = items[index].name
            )
        }
    }
}

@Composable
fun ItemView(
    index: Int,
    selected: Boolean,
    onClick: (Int) -> Unit,
    label: String
) {
    ElevatedFilterChip(
        leadingIcon = if (selected) {
            {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Done icon",
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else {
            {
            }
        },
        onClick = { onClick.invoke(index) },
        selected = selected,
        label = {
            Text(label)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
    )
}

@Preview
@Composable
fun PreviewNewEntryScreen() {
    YokologTheme {
        Surface { NewEntryScreen({ }) }
    }
}

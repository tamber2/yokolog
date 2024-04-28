package at.tamber.yokolog.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Subject
import androidx.compose.material.icons.outlined.ImportExport
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import at.tamber.yokolog.R
import at.tamber.yokolog.Screen
import at.tamber.yokolog.ui.theme.YokologTheme
import kotlinx.coroutines.launch
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import me.zhanghai.compose.preference.preference

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavHostController, onCsvExportClicked: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.settings))
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
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {

            ProvidePreferenceLocals {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    preference(
                        key = "csv_export",
                        title = { Text(text = stringResource(R.string.csv_export)) },
                        summary = { Text(text = stringResource(R.string.csv_export_summary)) },
                        enabled = true,
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.ImportExport,
                                contentDescription = null
                            )
                        },
                        onClick = {
                            coroutineScope.launch {
                                onCsvExportClicked.invoke()
                            }
                        }
                    )

                    preference(
                        key = "item_types",
                        title = { Text(text = stringResource(R.string.item_types_title)) },
                        summary = { Text(text = stringResource(R.string.item_types_summary)) },
                        enabled = true,
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.Subject,
                                contentDescription = null
                            )
                        },
                        onClick = {
                            //show dialog maybe?
                            navController.navigate(Screen.ItemTypeScreen.route)
                        }
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun PreviewSettingsScreen() {
    YokologTheme {
        Surface {
            SettingsScreen(rememberNavController()) {}
        }
    }
}

package at.tamber.yokolog.screens

import android.graphics.Typeface
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import at.tamber.yokolog.AppViewModelProvider
import at.tamber.yokolog.screens.add.ItemViewModel
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.CartesianChartHost
import com.patrykandpatrick.vico.compose.chart.edges.rememberFadingEdges
import com.patrykandpatrick.vico.compose.chart.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.chart.layer.rememberLineSpec
import com.patrykandpatrick.vico.compose.chart.layout.fullWidth
import com.patrykandpatrick.vico.compose.chart.rememberCartesianChart
import com.patrykandpatrick.vico.compose.chart.zoom.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.component.shape.shader.color
import com.patrykandpatrick.vico.compose.dimensions.dimensionsOf
import com.patrykandpatrick.vico.core.axis.vertical.VerticalAxis
import com.patrykandpatrick.vico.core.chart.layout.HorizontalLayout
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraphScreen(navController: NavHostController, viewModel: ItemViewModel = viewModel(factory = AppViewModelProvider.Factory)) {
    Scaffold(topBar = {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
            title = {
                Text("Weight graph")
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
        )
    }) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {

        }
    }
}

@Composable
fun Graph(
    modelProducer: CartesianChartModelProducer,
) {
    val scope = rememberCoroutineScope()

    CartesianChartHost(
        chart =
        rememberCartesianChart(
            rememberLineCartesianLayer(
                lines = listOf(rememberLineSpec(shader = DynamicShaders.color(Color.Yellow))),
//                axisValueOverrider = axisValueOverrider,
            ),
            startAxis =
            rememberStartAxis(
                guideline = null,
                horizontalLabelPosition = VerticalAxis.HorizontalLabelPosition.Inside,
                titleComponent =
                rememberTextComponent(
                    color = Color.Black,
                    background = rememberShapeComponent(Shapes.pillShape, Color.Blue),
                    padding = dimensionsOf(horizontal = 8.dp, vertical = 2.dp),
                    margins = dimensionsOf(end = 4.dp),
                    typeface = Typeface.MONOSPACE,
                ),
                title = "title X?",
            ),
            bottomAxis =
            rememberBottomAxis(
                titleComponent =
                rememberTextComponent(
                    background = rememberShapeComponent(Shapes.pillShape, Color.Red),
                    color = Color.White,
                    padding = dimensionsOf(horizontal = 8.dp, vertical = 2.dp),
                    margins = dimensionsOf(top = 4.dp),
                    typeface = Typeface.MONOSPACE,
                ),
                title = "bottom title"
            ),
            fadingEdges = rememberFadingEdges(),
        ),
        modelProducer = modelProducer,
        //modifier = modifier,
        //marker = rememberMarker(MarkerComponent.LabelPosition.AroundPoint),
        runInitialAnimation = false,
        horizontalLayout = HorizontalLayout.fullWidth(),
        zoomState = rememberVicoZoomState(zoomEnabled = false),
    )
}
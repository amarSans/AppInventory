import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muammar.inventory.data.BarData


@Composable
fun BarChart(barData: List<BarData>, modifier: Modifier = Modifier) {
    val maxValue = barData.maxOfOrNull { it.value } ?: 1f

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        barData.forEach { data ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                // Bar
                Canvas(
                    modifier = Modifier
                        .width(40.dp)
                        .height(150.dp)
                ) {
                    val barHeight = (data.value / maxValue) * size.height
                    drawRect(
                        color = Color(0xFF4DD0E1),
                        topLeft = androidx.compose.ui.geometry.Offset(
                            x = 0f,
                            y = size.height - barHeight
                        ),
                        size = androidx.compose.ui.geometry.Size(width = size.width, height = barHeight)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                // Label
                Text(text = data.label, fontSize = 12.sp, color =  Color(0xFF4DD0E1))
            }
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun BarChartPreview() {
    val sampleData = listOf(
        BarData("Jan", 50f, ),
        BarData("Feb", 80f, ),
        BarData("Mar", 30f, ),
        BarData("Mar", 30f, ),
        BarData("Mar", 30f, ),
        
        BarData("Mar", 30f, ),
        BarData("Mar", 30f, ),
        BarData("Mar", 30f, ),
        BarData("Mar", 30f, ),
        BarData("Apr", 70f, )
    )

    BarChart(barData = sampleData)
}

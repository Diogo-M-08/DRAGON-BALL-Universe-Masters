import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import util.Gelds
import util.toHumanReadableString
import vw.GameViewModel

@Composable
@Preview
fun App() {
    MaterialTheme {
        Screen()
    }
}

@Composable
@Preview
fun Screen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dragon Ball Universe Master") },
                backgroundColor = Color(0xFF6200EA),
                contentColor = Color.White
            )
        },
        content = {
            val coroutineScope = rememberCoroutineScope()
            val viewModel by remember {
                mutableStateOf(
                    GameViewModel(
                        scope = coroutineScope,
                    )
                )
            }
            DisposableEffect(viewModel) {
                onDispose {
                    viewModel.clear()
                }
            }

            val gameState: GameState? by viewModel.gameState.collectAsState()
            val currentMoney: Gelds? by remember(gameState) {
                derivedStateOf { gameState?.stashedMoney }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.reset() },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text("Reset Game", color = Color.White, fontSize = 18.sp)
                }

                gameState?.let { state ->
                    Text(
                        "Bank: ${currentMoney?.toHumanReadableString()} Gelds",
                        style = MaterialTheme.typography.h4.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF6200EA),
                        modifier = Modifier.padding(8.dp)
                    )

                    Button(
                        onClick = { viewModel.clickMoney(state) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .height(100.dp) // Set height as needed
                            .background(Color.Transparent) // Transparent background for button
                    ) {
                        Image(
                            painter = painterResource(), // Use the actual image resource name
                            contentDescription = "Click Money",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    state.availableJobs.forEach { availableJob ->
                        Generator(
                            gameJob = availableJob,
                            alreadyBought = state.workers.any { it.jobId == availableJob.id },
                            onBuy = { viewModel.addWorker(state, availableJob) },
                            onUpgrade = { viewModel.upgradeJob(state, availableJob) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun Generator(
    gameJob: GameJob,
    alreadyBought: Boolean,
    modifier: Modifier = Modifier,
    onBuy: () -> Unit = {},
    onUpgrade: () -> Unit = {},
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        backgroundColor = Color(0xFFF5F5F5),
        elevation = 4.dp,
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(16.dp)
        ) {
            Column {
                Text("Generator ${gameJob.id}", fontWeight = FontWeight.Bold)
                Text("Level: ${gameJob.level.level}")
                Text("Costs: ${gameJob.level.cost.toHumanReadableString()} Gelds")
                Text("Earns: ${gameJob.level.earn.toHumanReadableString()} Gelds")
                Text("Duration: ${gameJob.level.duration.inWholeSeconds} Seconds")
            }
            Column {
                if (!alreadyBought) {
                    Button(onClick = onBuy) {
                        Text("Buy")
                    }
                } else {
                    Text("Bought", color = Color.Green, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onUpgrade) {
                    Text("Upgrade")
                }
            }
        }
    }
}

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import idle_game.composeapp.generated.resources.*
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
                    .background(Color.White)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Clicker Image in the center
                Image(
                    painter = painterResource(Res.drawable.zeni),
                    contentDescription = "Clicker",
                    modifier = Modifier
                        .size(150.dp)
                        .align(Alignment.CenterHorizontally)
                        .clickable {
                            gameState?.let { state -> viewModel.clickMoney(state) }
                        }
                )



                Spacer(modifier = Modifier.height(16.dp))
                gameState?.let { state ->
                    Text(
                        "Bank: ${currentMoney?.toHumanReadableString()} Gelds",
                        style = MaterialTheme.typography.h4,
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        state.availableJobs.forEach { availableJob ->
                            Generator(
                                gameJob = availableJob,
                                alreadyBought = state.workers.any { it.jobId == availableJob.id },
                                onBuy = { viewModel.addWorker(state, availableJob) },
                                onUpgrade = { viewModel.upgradeJob(state, availableJob) }
                            )
                        }
                    }
                }

                Button(
                    modifier = Modifier.padding(18.dp),
                    onClick = { viewModel.reset() },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(206, 177, 67),
                        contentColor = Color.White
                    ),
                ) {
                    Text("Reset Game")
                }
            }
        }
    )
}

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit
) {
    val gradientColors = listOf(Color(0xFFB8860B), Color(0xFFDAA520))

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(60.dp)
            .background(
                brush = Brush.horizontalGradient(colors = gradientColors),
                shape = RoundedCornerShape(12.dp)
            ),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent
        ),
        contentPadding = PaddingValues()
    ) {
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.button,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun Generator(
    gameJob: GameJob,
    alreadyBought: Boolean,
    modifier: Modifier = Modifier,
    onBuy: () -> Unit = {},
    onUpgrade: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(listOf(Color(0xFFB8860B), Color(0xFFDAA520))),
                    RoundedCornerShape(8.dp)
                )
                .border(
                    width = 6.dp,
                    color = Color(115, 101, 41),
                    shape = RoundedCornerShape(5.dp)
                )
                .padding(8.dp)
        ) {
            Column {
                Text("GENERATOR 1")
                Text("Level: ${gameJob.level.level}")
                Text("Costs: ${gameJob.level.cost.toHumanReadableString()} Gelds")
                Text("Earns: ${gameJob.level.earn.toHumanReadableString()} Gelds")
                Text("Duration: ${gameJob.level.duration.inWholeSeconds} Seconds")
            }

            if (!alreadyBought) {
                Button(onClick = onBuy) {
                    Text("Buy")
                }
            } else {
                Text("Bought")
            }
            Button(onClick = onUpgrade) {
                Text("Upgrade")
            }
        }
    }
}

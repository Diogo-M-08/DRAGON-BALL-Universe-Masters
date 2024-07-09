import com.ionspin.kotlin.bignum.decimal.times
import kotlinx.serialization.Serializable
import util.Gelds
import util.GeldsSerializer
import util.gelds
import kotlin.math.abs
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Serializable
data class GameState(
    @Serializable(with = GeldsSerializer::class)
    internal val stashedMoney: Gelds,
    val workers: List<GameWorker>,
    val availableJobs: List<GameJob> = listOf(
        // level, kosten, einkommen, dauer
        GameJob(1, Level(1, 50.gelds, 5.gelds, 2.seconds)),
        GameJob(2, Level(1, 300.gelds, 10.gelds, 3.seconds)),
        GameJob(3, Level(1, 2000.gelds, 150.gelds, 10.seconds)),
        GameJob(4, Level(1, 10000.gelds, 800.gelds, 15.seconds)),
        GameJob(5, Level(1, 100000.gelds, 1000.gelds, 20.seconds)),
        GameJob(6, Level(1, 500000.gelds, 250000.gelds, 30.seconds))


    )
)

@Serializable
data class GameWorker(
    val jobId: Int,
    val createdAt: Long,
) {

    fun earnedWorker(job: GameJob, now: Long): Pair<Long, Gelds> {
        val collected = abs((now - createdAt) / job.level.duration.inWholeMilliseconds)
        return collected to collected * job.level.earn
    }
}

@Serializable
data class GameJob(
    val id: Int,
    val level: Level,
)

@Serializable
data class Level(
    val level: Int,
    @Serializable(with = GeldsSerializer::class)
    val cost: Gelds,
    @Serializable(with = GeldsSerializer::class)
    val earn: Gelds,
    val duration: Duration,
) {
    fun upgradeEfficiency() = copy(
        level = level + 1,
        earn = earn * 2,
        cost = cost * 3,
    )
}

@Serializable
data class ClickButtonLevel(
    val level: Int,
    @Serializable(with = GeldsSerializer::class)
    val earn: Gelds,
)

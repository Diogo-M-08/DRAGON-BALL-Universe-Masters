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
        GameJob(1, Level(1, 5.gelds, 1.gelds, 1.seconds)),
        GameJob(3, Level(1, 500.gelds, 9.gelds, 5.seconds)),
        GameJob(3, Level(1, 8100.gelds, 1800.gelds, 10.seconds)),
        GameJob(4, Level(1, 900000.gelds, 3000.gelds, 15.seconds)),
        GameJob(5, Level(1, 8100.gelds, 1800.gelds, 20.seconds)),
        GameJob(6, Level(1, 8100.gelds, 1800.gelds, 30.seconds))


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

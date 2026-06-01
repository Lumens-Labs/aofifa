package com.example.myapplication.domain.logic

import com.example.myapplication.domain.model.Match
import com.example.myapplication.domain.model.Player
import kotlin.math.roundToInt

data class PlayerStats(
    val playerId: String,
    val wins: Int = 0,
    val losses: Int = 0,
    val goalsFor: Int = 0,
    val goalsAgainst: Int = 0,
    val points: Int = 0,
    val winRate: Double = 0.0,
    val bestVictimId: String? = null,
    val nemesisId: String? = null
)

object RankingEngine {

    fun calculateAsadoRanking(playerIds: List<String>, matches: List<Match>): List<PlayerStats> {
        val statsMap = playerIds.associateWith { PlayerStats(it) }.toMutableMap()

        matches.forEach { match ->
            val winner = statsMap[match.winnerId] ?: PlayerStats(match.winnerId)
            val loser = statsMap[match.loserId] ?: PlayerStats(match.loserId)

            statsMap[match.winnerId] = winner.copy(
                wins = winner.wins + 1,
                goalsFor = winner.goalsFor + match.winnerGoles,
                goalsAgainst = winner.goalsAgainst + match.loserGoles
            )
            statsMap[match.loserId] = loser.copy(
                losses = loser.losses + 1,
                goalsFor = loser.goalsFor + match.loserGoles,
                goalsAgainst = loser.goalsAgainst + match.winnerGoles
            )
        }

        return statsMap.values.map { stats ->
            val totalGames = stats.wins + stats.losses
            val winRate = if (totalGames > 0) (stats.wins.toDouble() / totalGames * 100.0) else 0.0
            val bestVictim = calculateBestVictim(stats.playerId, matches)
            val nemesis = calculateNemesis(stats.playerId, matches)
            
            stats.copy(
                winRate = (winRate * 100.0).roundToInt() / 100.0,
                bestVictimId = bestVictim,
                nemesisId = nemesis
            )
        }.sortedWith { p1, p2 ->
            if (p1.wins != p2.wins) return@sortedWith p2.wins.compareTo(p1.wins)
            if (p1.losses != p2.losses) return@sortedWith p1.losses.compareTo(p2.losses)
            val h2h = calculateH2H(p1.playerId, p2.playerId, matches)
            if (h2h != 0) return@sortedWith h2h
            val diff1 = p1.goalsFor - p1.goalsAgainst
            val diff2 = p2.goalsFor - p2.goalsAgainst
            diff2.compareTo(diff1)
        }
    }

    private fun calculateH2H(p1Id: String, p2Id: String, matches: List<Match>): Int {
        var p1Wins = 0
        var p2Wins = 0
        matches.forEach { match ->
            if (match.winnerId == p1Id && match.loserId == p2Id) p1Wins++
            if (match.winnerId == p2Id && match.loserId == p1Id) p2Wins++
        }
        return p2Wins.compareTo(p1Wins)
    }

    private fun calculateBestVictim(playerId: String, matches: List<Match>): String? {
        val victims = matches.filter { it.winnerId == playerId }.groupBy { it.loserId }
        return victims.maxByOrNull { it.value.size }?.key
    }

    private fun calculateNemesis(playerId: String, matches: List<Match>): String? {
        val nemeses = matches.filter { it.loserId == playerId }.groupBy { it.winnerId }
        return nemeses.maxByOrNull { it.value.size }?.key
    }

    fun calculateGlobalPoints(sortedStats: List<PlayerStats>): List<PlayerStats> {
        val n = sortedStats.size
        if (n <= 1) return sortedStats.map { it.copy(points = if (n == 1) 10 else 0) }

        return sortedStats.mapIndexed { i, stats ->
            val points = (10.0 - (9.0 * i) / (n - 1)).roundToInt().coerceAtLeast(1)
            stats.copy(points = points)
        }
    }
}

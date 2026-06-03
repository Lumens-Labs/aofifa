package com.example.myapplication.domain.logic

import com.example.myapplication.domain.model.*

object TournamentEngine {

    fun generateInitialLeague(participants: List<String>): LeagueConfig {
        val fixtures = mutableListOf<TournamentMatch>()
        for (i in participants.indices) {
            for (j in i + 1 until participants.size) {
                fixtures.add(TournamentMatch(participants[i], participants[j]))
            }
        }
        return LeagueConfig(fixtures.shuffled())
    }

    fun generateInitialWinnerStays(participants: List<String>): WinnerStaysConfig? {
        if (participants.size < 2) return null
        return WinnerStaysConfig(
            currentWinnerId = participants[0],
            nextChallengerId = participants[1],
            queue = participants.drop(2)
        )
    }

    fun rotateWinnerStays(
        config: WinnerStaysConfig,
        winnerId: String,
        loserId: String,
        activeParticipants: List<String>
    ): WinnerStaysConfig {
        val fullList = mutableListOf<String>()
        
        // The winner stays
        fullList.add(winnerId)
        
        // The queue players that are still active
        fullList.addAll(config.queue.filter { activeParticipants.contains(it) })
        
        // The loser goes to the end if still active
        if (activeParticipants.contains(loserId)) {
            fullList.add(loserId)
        }
        
        // Ensure everyone active is in the rotation
        activeParticipants.forEach { if (!fullList.contains(it) && it != config.nextChallengerId) fullList.add(it) }

        val nextChallenger = fullList.getOrNull(1) ?: fullList.getOrNull(0)
        
        return WinnerStaysConfig(
            currentWinnerId = winnerId,
            nextChallengerId = nextChallenger,
            queue = fullList.drop(if (nextChallenger != null) 2 else 1)
        )
    }

    fun calculateLeagueTable(participants: List<String>, matches: List<Match>, fixtures: List<TournamentMatch>): List<AsadoPlayerRank> {
        val statsMap = participants.associateWith { pid ->
            var wins = 0
            var losses = 0
            var points = 0
            
            matches.filter { it.winnerId == pid || it.loserId == pid }.forEach { m ->
                if (m.winnerId == pid) {
                    wins++
                    points += 3
                } else {
                    losses++
                }
            }
            pid to (wins to (losses to points))
        }

        return participants.map { pid ->
            val stats = statsMap[pid]?.second ?: (0 to (0 to 0))
            AsadoPlayerRank(pid, stats.first, stats.second.first, stats.second.second, 0)
        }.sortedByDescending { it.points }
            .mapIndexed { index, rank -> rank.copy(position = index) }
    }
}

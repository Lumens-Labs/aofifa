package com.example.myapplication.ui.theme

import com.example.myapplication.R

object PlayerIcons {
    val TEAM_AVATARS = mapOf(
        "5ee34512-1df1-48bf-a236-00f2d2aebdad" to R.drawable.mansfieldtown,
        "5ae24da6-2b7c-4f23-afce-c5a9cf0f5d79" to R.drawable.swindontown,
        "f67a4e49-0ec8-4983-a647-65703697c5db" to R.drawable.tranmererovers,
        "d0552b10-0b9c-45dd-9fab-39481025c64e" to R.drawable.walsal
    )

    fun getAvatar(playerId: String): Int {
        return TEAM_AVATARS[playerId] ?: R.drawable.ic_launcher_foreground
    }
}

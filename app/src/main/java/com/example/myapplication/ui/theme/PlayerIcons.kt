package com.example.myapplication.ui.theme

import android.content.Context
import com.example.myapplication.R
import com.example.myapplication.domain.model.resolveAvatarUrl
import com.example.myapplication.domain.model.Player

object PlayerIcons {
    private val TEAM_AVATARS_MAP = mapOf(
        "5ee34512-1df1-48bf-a236-00f2d2aebdad" to R.drawable.mansfieldtown,
        "5ae24da6-2b7c-4f23-afce-c5a9cf0f5d79" to R.drawable.swindontown,
        "f67a4e49-0ec8-4983-a647-65703697c5db" to R.drawable.tranmererovers,
        "d0552b10-0b9c-45dd-9fab-39481025c64e" to R.drawable.walsal
    )

    fun getAvatar(playerId: String, avatarUrl: String?, context: Context): Any {
        if (!avatarUrl.isNullOrEmpty()) {
            // 1. Try resolving as drawable resource first
            val resId = context.resources.getIdentifier(avatarUrl, "drawable", context.packageName)
            if (resId != 0) return resId
            
            // 2. Fallback to web resolution
            val dummyPlayer = Player(id = playerId, name = "", avatarUrl = avatarUrl)
            return dummyPlayer.resolveAvatarUrl() ?: avatarUrl
        }
        
        return TEAM_AVATARS_MAP[playerId] ?: R.drawable.ic_launcher_foreground
    }

    fun getAvatar(playerId: String): Int {
        return TEAM_AVATARS_MAP[playerId] ?: R.drawable.ic_launcher_foreground
    }

    val DRAWABLE_BADGES = listOf(
        "walsal", "wexham", "barrowafc", "morecambe", "sc_bastia", "stockport", 
        "colchester", "genoble_gf", "crawleytown", "forestgreen", "gillinhamfc", 
        "grimsbytown", "rochdaleafc", "salfordcity", "stevenagefc", "swindontown", 
        "afcwimbledon", "bradfordcity", "leytonorient", "miltonkeynes", "nottscountry", 
        "suttonunited", "harrogatetown", "mansfieldtown", "newportcounty", "riverplate_fc",
        "crewealexandra", "tranmererovers", "doncasterrovers", "northamptontown", 
        "accrintonstanley", "hartlepoolunited"
    )
}

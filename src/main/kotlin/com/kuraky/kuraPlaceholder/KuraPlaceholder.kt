package com.kuraky.kuraPlaceholder

import com.kuraky.api.Api
import org.bukkit.plugin.java.JavaPlugin
import com.kuraky.kuraPlaceholder.api.KuraExpansion
import com.kuraky.kuraPlaceholder.api.Placeholders
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class KuraPlaceholder : JavaPlugin() {

    override fun onEnable() {

        Placeholders.register(object : KuraExpansion {
            override val prefix = "player"
            override fun request(player: Player?, param: String): String? {
                if (player == null) return ""
                return when (param.lowercase()) {
                    "name" -> player.name
                    "displayname" -> player.displayName
                    "ping" -> player.ping.toString()
                    "health" -> player.health.toInt().toString()
                    else -> null
                }
            }
        })

        Placeholders.register(object : KuraExpansion {
            override val prefix = "server"
            override fun request(player: Player?, param: String): String? {
                return when (param.lowercase()) {
                    "online" -> Bukkit.getOnlinePlayers().size.toString()
                    "max_players" -> Bukkit.getMaxPlayers().toString()
                    else -> null
                }
            }
        })

        val msg = Api.chat.format("&#00FF00[KuraPlaceholder] ¡API Iniciada!")
        Bukkit.getConsoleSender().sendMessage(msg)
    }


    override fun onDisable() {
        // Plugin shutdown logic
        val msg = Api.chat.format("&#e7133f[KuraPlaceholder] ¡API Desabilitado!")
    }
}

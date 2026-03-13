package com.kuraky.kuraPlaceholder.api

import org.bukkit.entity.Player
import java.util.regex.Matcher
import java.util.regex.Pattern

interface KuraExpansion {
    val prefix: String
    fun request(player: Player?, param: String): String?
}
object Placeholders {

    private val globalVars = mutableMapOf<String, () -> String>()
    private val playerVars = mutableMapOf<String, (Player) -> String>()
    private val expansions = mutableMapOf<String, KuraExpansion>()

    private val pattern = Pattern.compile("%([a-zA-Z0-9_\\-]+)%")

    // ==========================================
    // MÉTODOS DE REGISTRO
    // ==========================================

    /** Registra una variable global (ej: servidor_online) */
    fun register(name: String, action: () -> String) {
        globalVars[name.lowercase()] = action
    }

    /** Registra una variable de jugador (ej: jugador_dinero) */
    fun register(name: String, action: (Player) -> String) {
        playerVars[name.lowercase()] = action
    }

    /** Registra un grupo de variables (Modo Avanzado) */
    fun register(expansion: KuraExpansion) {
        expansions[expansion.prefix.lowercase()] = expansion
    }

    // ==========================================
    // TRADUCTOR DE VARIABLES
    // ==========================================

    /** Reemplaza todas las variables de un texto */
    fun parse(player: Player?, text: String): String {
        if (!text.contains("%")) return text

        val matcher = pattern.matcher(text)
        val buffer = StringBuffer()

        while (matcher.find()) {
            val variable = matcher.group(1).lowercase()
            var result: String? = null

            // 1. Busca exacta (Modo Novato)
            if (player != null && playerVars.containsKey(variable)) {
                result = playerVars[variable]!!.invoke(player)
            } else if (globalVars.containsKey(variable)) {
                result = globalVars[variable]!!.invoke()
            }

            // 2. Busca por prefijo (Modo Profesional)
            if (result == null) {
                val parts = variable.split("_", limit = 2)
                if (parts.size == 2) {
                    val prefix = parts[0]
                    val param = parts[1]

                    val expansion = expansions[prefix]
                    if (expansion != null) {
                        result = expansion.request(player, param)
                    }
                }
            }

            // 3. Reemplaza o ignora
            if (result != null) {
                matcher.appendReplacement(buffer, Matcher.quoteReplacement(result))
            } else {
                matcher.appendReplacement(buffer, matcher.group(0))
            }
        }
        matcher.appendTail(buffer)
        return buffer.toString()
    }
}
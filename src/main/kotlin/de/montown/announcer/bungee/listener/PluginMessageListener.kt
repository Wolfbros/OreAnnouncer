package de.montown.announcer.bungee.listener

import de.montown.announcer.bungee.Main
import de.montown.announcer.bungee.discord.DiscordBot
import de.montown.announcer.bungee.misc.ConfigLoader
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.event.PluginMessageEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import java.io.ByteArrayInputStream
import java.io.DataInputStream

object PluginMessageListener : Listener {

    private val config = ConfigLoader.config
    private val message = config?.getString("BungeeConfig.message") ?: ""

    init {
        Main.plugin.proxy.apply {
            registerChannel("oreannouncer:message")
        }
        Main.plugin.proxy.pluginManager.registerListener(Main.plugin, this)
    }


    @EventHandler
    fun onServerMessage(event: PluginMessageEvent) {
        if (!event.tag.equals("oreannouncer:message", ignoreCase = true)) {
            return
        }
        val stream = DataInputStream(ByteArrayInputStream(event.data))
        val playerName = stream.readUTF()
        val player = Main.plugin.proxy.getPlayer(playerName)
        val amount = stream.readUTF()
        val block = stream.readUTF()
        val coords = stream.readUTF().replace(",", "")
        val world = stream.readUTF()
        val prefix = ""
        val server = player.server.info.name

        val finalMessage = ChatColor.translateAlternateColorCodes('&', message)
            .replace("%prefix%", prefix)
            .replace("%player%", playerName)
            .replace("%amount%", amount.toString())
            .replace("%block%", block)
            .replace("%coordinates%", coords)
            .replace("%world%", world)
            .replace("%server%", server)

        Main.plugin.proxy.players.filter { it.hasPermission("ores.announce") }.forEach {
            it.sendMessage(TextComponent(finalMessage))
        }
        DiscordBot.sendUpdate(playerName, amount.toString(), block, coords, world, server)
    }
}
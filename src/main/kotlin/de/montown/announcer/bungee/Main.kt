package de.montown.announcer.bungee

import de.montown.announcer.bungee.discord.DiscordBot
import de.montown.announcer.bungee.listener.PluginMessageListener
import de.montown.announcer.bungee.misc.ConfigLoader
import net.md_5.bungee.api.plugin.Plugin

class Main : Plugin(){

    companion object {
        lateinit var plugin: Main
    }

    override fun onEnable() {
        plugin = this
        ConfigLoader
        PluginMessageListener
        DiscordBot
    }
}

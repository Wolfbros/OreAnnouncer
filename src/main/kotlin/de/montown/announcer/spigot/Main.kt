package de.montown.announcer.spigot

import de.montown.announcer.spigot.discord.DiscordBot
import de.montown.announcer.spigot.listeners.InteractListener
import de.montown.announcer.spigot.misc.ConfigLoader
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class Main: JavaPlugin() {



    companion object{
        lateinit var plugin: Main
    }

    override fun onEnable(){
        plugin = this
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "oreannouncer:message")
        ConfigLoader
        InteractListener
        DiscordBot
    }
}
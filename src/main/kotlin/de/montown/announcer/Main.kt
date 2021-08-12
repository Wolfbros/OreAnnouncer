package de.montown.announcer

import de.montown.announcer.listeners.InteractListener
import de.montown.announcer.misc.ConfigLoader
import org.bukkit.plugin.java.JavaPlugin

class Main: JavaPlugin() {



    companion object{
        lateinit var plugin: Main
    }

    override fun onEnable(){
        plugin = this
        ConfigLoader
        InteractListener
    }
}
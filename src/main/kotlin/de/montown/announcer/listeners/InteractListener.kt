package de.montown.announcer.listeners

import de.montown.announcer.Main
import de.montown.announcer.misc.ConfigLoader
import de.montown.announcer.misc.prefix
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

object InteractListener : Listener {

    val config = ConfigLoader.config

    private val materials = ArrayList<Material>()
    private const val permission = "ores.announce"
    private val cache = ArrayList<Location>()
    private val message = config?.getString("Config.message") ?: ""

    init {
        config?.getStringList("Config.blocks")?.forEach {
            materials.add(Material.valueOf(it))
        }
        Bukkit.getServer().pluginManager.registerEvents(this, Main.plugin)
        val interval = config?.getInt("Config.cacheclear")
        if (interval != null && interval != 0)
            Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, Runnable {
                cache.clear()
            }, (20 * 60 * interval).toLong(), (20 * 60 * interval).toLong())
    }

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        if (!event.hasBlock()) return
        if (event.action != Action.LEFT_CLICK_BLOCK) return
        val block = event.clickedBlock ?: return
        if (!materials.contains(block.type)) return
        val amount = getNode(block, block.type)
        if (amount == 0) return
        val operators = Bukkit.getOnlinePlayers().filter { it.hasPermission(permission) }

        operators.forEach {
            it.sendMessage(
                ChatColor.translateAlternateColorCodes('&', message)
                    .replace("%prefix%", prefix)
                    .replace("%player%", event.player.name)
                    .replace("%amount%", amount.toString())
                    .replace("%block%", block.type.name)
                    .replace("%coordinates%", "${block.location.blockX}, ${block.location.blockY}, ${block.location.blockZ}")
                    .replace("%world%", block.location.world!!.name)
            )
        }


    }

    fun getNode(block: Block, type: Material): Int {
        if (cache.contains(block.location)) return 0
        if (block.type != type) return 0
        cache.add(block.location)
        val location = block.location
        var returnInt = 1
        returnInt += getNode(block.location.world!!.getBlockAt(location.add(1.0, 0.0, 0.0)), type)
        returnInt += getNode(block.location.world!!.getBlockAt(location.add(-2.0, 0.0, 0.0)), type)
        returnInt += getNode(block.location.world!!.getBlockAt(location.add(1.0, 1.0, 0.0)), type)
        returnInt += getNode(block.location.world!!.getBlockAt(location.add(0.0, -2.0, 0.0)), type)
        returnInt += getNode(block.location.world!!.getBlockAt(location.add(0.0, 1.0, 1.0)), type)
        returnInt += getNode(block.location.world!!.getBlockAt(location.add(0.0, 0.0, -2.0)), type)
        return returnInt
    }
}
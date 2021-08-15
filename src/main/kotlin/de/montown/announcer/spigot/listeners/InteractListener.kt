package de.montown.announcer.spigot.listeners

import com.google.common.io.ByteStreams
import de.montown.announcer.spigot.Main
import de.montown.announcer.spigot.discord.DiscordBot
import de.montown.announcer.spigot.misc.ConfigLoader
import de.montown.announcer.spigot.misc.prefix
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
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

        if(config!!.getBoolean("Config.enableBungeecord")){
            sendToBungeecord(event.player, amount, block)
        }else{
            val operators = Bukkit.getOnlinePlayers().filter { it.hasPermission(permission) }
            val message = ChatColor.translateAlternateColorCodes('&', message)
                .replace("%prefix%", prefix)
                .replace("%player%", event.player.name)
                .replace("%amount%", amount.toString())
                .replace("%block%", block.type.name)
                .replace("%coordinates%", "${block.location.blockX}, ${block.location.blockY}, ${block.location.blockZ}")
                .replace("%world%", block.location.world!!.name)

            operators.forEach { it.sendMessage(message) }
            DiscordBot.sendUpdate(message.replace("§[a-zA-Z0-9]".toRegex(), ""))
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

    fun sendToBungeecord(player:Player, amount:Int, block: Block){
        val out = ByteStreams.newDataOutput()
        out.writeUTF(player.name)
        out.writeUTF(amount.toString())
        out.writeUTF(block.type.name)
        out.writeUTF("${block.location.blockX}, ${block.location.blockY}, ${block.location.blockZ}")
        out.writeUTF(block.location.world!!.name)
        player.sendPluginMessage(Main.plugin,"oreannouncer:message", out.toByteArray())
    }
}
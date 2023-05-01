package de.montown.announcer.bungee.discord

import de.montown.announcer.bungee.misc.ConfigLoader
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.ChunkingFilter
import net.dv8tion.jda.api.utils.MemberCachePolicy
import java.awt.Color
import java.util.*

object DiscordBot {

    private var api: JDA?

    init {
        val token = ConfigLoader.config?.getString("BungeeConfig.discord.token")
        val playing = ConfigLoader.config?.getString("BungeeConfig.discord.playing") ?: "Nothing to see here"
        api = if (token == null || token == "") {
            null
        } else {
            JDABuilder.createDefault(token)
                .setAutoReconnect(true)
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .build().awaitReady()
        }
        if (api != null) {
            api!!.presence.activity = Activity.playing(playing)
        }
    }

    fun sendUpdate(
        playerName: String,
        amount: String,
        block: String,
        coords: String,
        world: String,
        server: String
    ) {
        if (api == null) return
        val embedBuilder = EmbedBuilder()
        val current = Calendar.getInstance().time.time
        if (block.contains("GOLD")){
            embedBuilder.setColor(Color.YELLOW)
        }
        if (block.contains("DIAMOND")){
            embedBuilder.setColor(Color.BLUE)
        }
        if (block.contains("EMERALD")){
            embedBuilder.setColor(Color.GREEN)
        }
        if (block.contains("EMERALD")){
            embedBuilder.setColor(Color.GREEN)
        }
        if (block.contains("ANCIENT")){
            embedBuilder.setColor(Color.LIGHT_GRAY)
        }
        embedBuilder.setTitle("$playerName hat $amount $block gefunden")
        embedBuilder.addField(
            "Zeit:", "<t:" + current / 1000 + ":F>\n"
                    + "\uD83D\uDD57 " + "<t:" + current / 1000 + ":R>", true
        )
        embedBuilder.addField(
            "Infos:", "- Server: $server\n"
                    + "- Welt: $world\n"
                    + "- Block: $block\n"
                    + "- Anzahl: $amount\n"
                    + "- Koordinaten: $coords", true
        )
        embedBuilder.addField(
            "Befehle:", "- /tp $playerName\n"
                    + "- /server $server\n"
                    + "- /mv tp $world\n"
                    + "- /tp $coords", true
        )
        ConfigLoader.config?.let { api!!.getTextChannelById(it.getLong("BungeeConfig.discord.channel")) }
            ?.sendMessageEmbeds(embedBuilder.build())?.queue()

    }

}
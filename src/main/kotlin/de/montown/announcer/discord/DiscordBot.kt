package de.montown.announcer.discord

import de.montown.announcer.misc.ConfigLoader
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.ChunkingFilter
import net.dv8tion.jda.api.utils.MemberCachePolicy
import java.io.ObjectInputFilter

object DiscordBot {

    private var api: JDA?

    init {
        val token = ConfigLoader.config?.getString("Config.discord.token")
        val playing = ConfigLoader.config?.getString("Config.discord.playing")?:"Nothing to see here"
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
        if(api !=null){
            api!!.presence.activity = Activity.playing(playing)
        }
    }

    fun sendUpdate(string: String) {
        if (api == null) return
        ConfigLoader.config?.let { api!!.getTextChannelById(it.getLong("Config.discord.channel")) }?.sendMessage(string)?.queue()
    }

}
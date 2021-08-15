package de.montown.announcer.bungee.misc


import com.google.common.base.Charsets
import de.montown.announcer.bungee.Main
import de.montown.announcer.spigot.misc.prefix
import net.md_5.bungee.config.Configuration
import net.md_5.bungee.config.ConfigurationProvider
import net.md_5.bungee.config.YamlConfiguration
import java.io.*
import java.util.*


object ConfigLoader {

    private val main = Main.plugin

    /**
     * @return the String of messages.yml
     */
    var config: Configuration? = null
        private set
    private val messageFile: File
    private const val version = "1"

    init {
        main.dataFolder.mkdir()
        messageFile = File(main.dataFolder, "config.yml")
        if (!messageFile.exists()) create()
        read()
        if (!Objects.requireNonNull(config!!.getString("BungeeConfig.ConfigVersion")).equals(version, ignoreCase = true)) changeVersion()
        prefix = config!!.getString("BungeeConfig.prefix").toString()
    }

    fun saveConfig() {
        ConfigurationProvider.getProvider(YamlConfiguration::class.java).save(config, messageFile)
    }

    /**
     * Reads messages.yml
     */
    private fun read() {
        try {
            val custom = InputStreamReader(FileInputStream(messageFile), Charsets.UTF_8)
            config = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(custom)
            if (config!!.keys.isEmpty()) {
                println("[Warnung] Max hat verkackt")
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

    /**
     * Updates version - safes old version
     */
    private fun changeVersion() {
        try {
            val inputStream: InputStream = FileInputStream(messageFile)
            val outputStream: OutputStream = FileOutputStream(File(main.dataFolder, "config-old.yml"))
            var read: Int
            val bytes = ByteArray(1024)
            while (inputStream.read(bytes).also { read = it } != -1) {
                outputStream.write(bytes, 0, read)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val old = config
        create()
        read()
        old?.keys?.forEach { config?.set(it, config!!.get(it)) }
        saveConfig()
    }

    /**
     * Creates or updates messages.yml
     */
    private fun create() {
        try {
            val inputStream = main.getResourceAsStream("config-bungee.yml")
            val outputStream: OutputStream = FileOutputStream(messageFile)
            var read: Int
            val bytes = ByteArray(1024)
            assert(inputStream != null)
            while (inputStream!!.read(bytes).also { read = it } != -1) {
                outputStream.write(bytes, 0, read)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}
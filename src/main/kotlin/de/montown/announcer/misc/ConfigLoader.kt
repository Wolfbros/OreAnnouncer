package de.montown.announcer.misc

import com.google.common.base.Charsets
import de.montown.announcer.Main
import org.bukkit.configuration.file.YamlConfiguration
import java.io.*
import java.util.*

object ConfigLoader {

    private val main = Main.plugin
    /**
     * @return the String of messages.yml
     */
    var config: YamlConfiguration? = null
        private set
    private val messageFile: File
    private const val version = "1"

    init {
        main.dataFolder.mkdir()
        messageFile = File(main.dataFolder, "config.yml")
        if (!messageFile.exists()) create()
        read()
        if (!Objects.requireNonNull(config!!.getString("Config.ConfigVersion")).equals(version, ignoreCase = true)) changeVersion()
        prefix = config!!.getString("Config.prefix").toString()
    }

    fun saveConfig() {
        config?.save(messageFile)
    }

    /**
     * Reads messages.yml
     */
    private fun read() {
        try {
            val custom = InputStreamReader(FileInputStream(messageFile), Charsets.UTF_8)
            config = YamlConfiguration.loadConfiguration(custom)
            if (config!!.getKeys(false).isEmpty()) {
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
        create()
    }

    /**
     * Creates or updates messages.yml
     */
    private fun create() {
        try {
            val inputStream = main.getResource("config.yml")
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
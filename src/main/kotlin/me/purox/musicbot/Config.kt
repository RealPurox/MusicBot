package me.purox.musicbot

import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.lang.Exception

class Config {

    @Transient private val logger = LoggerFactory.getLogger(Config::class.java)

    val token = "token-here"
    val prefix = "m!"

    fun load() : Config {

        val file = File("config.json")
        var config = Config()

        if (!file.exists()) {
            try {
                val fileWriter = FileWriter(file)

                GSON.toJson(config, fileWriter)
                fileWriter.close()
            } catch (e: Exception) {
                logger.error("Failed to create config", e)
                System.exit(0)
            }

            logger.info("Config created, please fill in your credentials and restart the process.")
            System.exit(0)
        }

        try {
            val fileReader = FileReader(file)

            config = GSON.fromJson(fileReader, Config::class.java)
        } catch (e: Exception) {
            logger.error("Failed to read config", e)
            System.exit(0)
        }

        return config
    }

}


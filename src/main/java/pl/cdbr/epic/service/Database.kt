package pl.cdbr.epic.service

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import pl.cdbr.epic.model.AppConfig
import pl.cdbr.epic.model.Hierarchy
import pl.cdbr.epic.model.Part
import pl.cdbr.epic.model.SearchConfig
import java.io.File

object Database {
    private val mapper = jacksonObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)

    private val dirName = System.getProperty("user.home") + File.separator + "EPIC"

    private val hierarchyFileName = "hierarchy.json"
    private val partsFileName = "parts.json"
    private val configFileName = "config.json"
    private val packagesFileName = "packages.json"
    private val suppliersFileName = "suppliers.json"

    private val dir = File(dirName)
    private val hierarchyFile = File(dirName + File.separator + hierarchyFileName)
    private val partsFile = File(dirName + File.separator + partsFileName)
    private val configFile = File(dirName + File.separator + configFileName)
    private val packagesFile = File(dirName + File.separator + packagesFileName)
    private val suppliersFile = File(dirName + File.separator + suppliersFileName)

    var parts = mutableListOf<Part>()
    var config = AppConfig.default()

    fun isInitialized() =
            dir.exists() && hierarchyFile.exists() && partsFile.exists()
                    && configFile.exists()
//                    && packagesFile.exists()
//                    && suppliersFile.exists()

    fun initialize() {
        println("Initializing in $dir")
        if (!dir.exists()) {
            dir.mkdir()
        }

        if (!hierarchyFile.exists()) {
            val hier = Hierarchy.initialHier
            mapper.writeValue(hierarchyFile, hier)
        }

        if (!partsFile.exists()) {
            val parts = Part.testData()
            mapper.writeValue(partsFile, parts)
        }

        if (!configFile.exists()) {
            mapper.writeValue(configFile, AppConfig.default())
        }
    }

    fun load() {
        val hier = mapper.readValue<Map<String, Map<String, List<String>>>>(hierarchyFile)
        Hierarchy.load(hier)

        parts = mapper.readValue<MutableList<Part>>(partsFile)
        config = mapper.readValue<AppConfig>(configFile)
    }

    fun save() {
        if (!dir.exists()) {
            dir.mkdir()
        }

        mapper.writeValue(hierarchyFile, Hierarchy.unload())
        mapper.writeValue(partsFile, parts)
        mapper.writeValue(configFile, config)
    }

    fun newPartId() = (parts.map { it.id }.max() ?: 0) + 1

    fun savePart(part: Part) {
        val position = parts.indexOfFirst { it.id == part.id }
        if (position >= 0) {
            parts[position] = part
        } else {
            parts.add(part)
        }
    }
}
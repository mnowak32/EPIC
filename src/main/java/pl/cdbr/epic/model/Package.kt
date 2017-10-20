package pl.cdbr.epic.model

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import kotlin.reflect.full.createInstance

@JsonDeserialize(using = PackageDeserializer::class)
data class Package(val name: String)

class PackageDeserializer : JsonDeserializer<Package>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Package {
        val pack = p.readValueAs<Map<String, String>>(jacksonTypeRef<Map<String, String>>())
        return Packager.of(pack["name"] ?: "none")
    }
}

object Packager {
    private val initial = listOf("DIP-4", "DIP-8", "DIP-10", "DIP-12", "DIP-14", "DIP-16", "DIP-18", "DIP-20", "TO-92", "TO-220")

    val packs = mutableListOf<Package>()

    fun initialData() = initial.map(::Package)

    fun of(name: String): Package {
        val p = packs.find { it.name == name }
        return if (p == null) {
            val newP = Package(name)
            packs += newP
            newP
        } else {
            p
        }
    }

    fun load(packages: Collection<Package>) {
        packages.forEach { of(it.name) }
    }
}
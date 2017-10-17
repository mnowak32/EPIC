package pl.cdbr.epic.model

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import pl.cdbr.epic.model.Hierarchy.groups
import pl.cdbr.epic.model.Hierarchy.subtypes
import pl.cdbr.epic.model.Hierarchy.types

@JsonSerialize(using = SubtypeSerializer::class)
@JsonDeserialize(using = SubtypeDeserializer::class)
data class Subtype(val name: String, val type: Type)

data class Type(val name: String, val group: Group)
data class Group(val name: String)

class SubtypeSerializer : JsonSerializer<Subtype>() {
    override fun serialize(value: Subtype, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeString("${value.type.group.name}|${value.type.name}|${value.name}")
    }
}

class SubtypeDeserializer : JsonDeserializer<Subtype>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Subtype {
        val inp = p.valueAsString
        val parts = inp.split("|", limit = 3)
        if (parts.size < 3) {
            throw JsonParseException(p, "Subtype do dupy")
        }
        return Hierarchy.of(parts[0], parts[1], parts[2])
    }
}

object Hierarchy {
    val initialHier = mapOf(
            "Discrete" to mapOf(
                    "Resistor" to listOf(
                            "Small", "Power", "Shunt", "Potentiometer"
                    ),
                    "Capacitor" to listOf(
                            "Electrolytic", "Ceramic", "Polymer", "Tantalum"
                    ),
                    "Inductor" to listOf(
                            "Power", "Choke", "Transformer"
                    ),
                    "Diode" to listOf(
                            "Schottky", "Zener", "Power"
                    ),
                    "Transistor" to listOf(
                            "PNP", "NPN", "N-FET", "P-FET", "Darlington"
                    )
            ),
            "IC" to mapOf(
                    "Audio" to listOf(
                            "Amplifier", "Filter", "DSP"
                    ),
                    "Power" to listOf(
                            "Buck/boost", "Linear regualtor", "Current sense"
                    ),
                    "Driver" to listOf(
                            "PWM", "Stepper", "Bridge"
                    ),
                    "Logic" to listOf(
                            "CMOS", "TTL"
                    ),
                    "Interface" to listOf(
                            "I2C", "USB", "UART"
                    ),
                    "Microcontroller" to listOf(
                            "8 bit Atmel", "32 bit ARM", "Other"
                    )
            )
    )

    val groups = mutableListOf<Group>()
    val types = mutableListOf<Type>()
    val subtypes = mutableListOf<Subtype>()

    fun load(hier: Map<String, Map<String, List<String>>>) {
        hier.forEach {
            val g = Group(it.key)
            groups += g
            it.value.forEach {
                val t = Type(it.key, g)
                types += t
                it.value.forEach {
                    val s = Subtype(it, t)
                    subtypes += s
                }
            }
        }
    }

    fun unload() = groups.map { gr ->
            gr.name to (
                types.filter { it.group == gr }.map { tp ->
                    tp.name to (
                        subtypes.filter { it.type == tp }.map {
                            it.name
                        }.toList()
                    )
                }.toMap()
            )
        }.toMap()

    fun of(group: String, type: String, subtype: String): Subtype {
        val fg = groups.find { it.name == group }
        val g = if (fg == null) {
            val ng = Group(group)
            groups += ng
            ng
        } else {
            fg
        }
        val ft = types.find { it.name == type && it.group == g }
        val t = if (ft == null) {
            val nt = Type(type, g)
            types += nt
            nt
        } else {
            ft
        }
        val fs = subtypes.find { it.name == subtype && it.type == t }
        val s = if (fs == null) {
            val ns = Subtype(subtype, t)
            subtypes += ns
            ns
        } else {
            fs
        }

        return s
    }
}

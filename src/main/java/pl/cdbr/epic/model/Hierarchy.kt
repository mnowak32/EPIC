package pl.cdbr.epic.model

data class Subtype(val name: String, val type: Type)
data class Type(val name: String, val group: Group)
data class Group(val name: String)

object Hierarchy {
    private val hier = mapOf(
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

    init {
        loadGroups(hier)
        println(subtypes)
    }

    private fun loadGroups(grMap: Map<String, Map<String, List<String>>>) {
        grMap.forEach {
            val g = Group(it.key)
            groups += g
            loadTypes(it.value, g)
        }
    }

    private fun loadTypes(typMap: Map<String, List<String>>, g: Group) {
        typMap.forEach {
            val t = Type(it.key, g)
            types += t
            loadSubtypes(it.value, t)
        }
    }

    private fun loadSubtypes(subList: List<String>, t: Type) {
        subList.forEach {
            val s = Subtype(it, t)
            subtypes += s
        }
    }

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

fun main(vararg args: String) {
    println(Hierarchy.of("IC", "Logic", "TTL"))
    println(Hierarchy.of("Mechanics", "Screw", "Metric"))
    println(Hierarchy.subtypes)
}
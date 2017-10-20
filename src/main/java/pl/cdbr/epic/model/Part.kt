package pl.cdbr.epic.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import tornadofx.*
import java.util.concurrent.ConcurrentSkipListSet

@JsonIgnoreProperties(ignoreUnknown = true)
data class Part(
        val id: Int,
        val subtype: Subtype,
        val supplier: Supplier,
        val name: String,
        val description: String,
        val count: Long,
        val sources: List<Source> = emptyList(),
        val pack: Package,
        val value: String = ""
) {
    fun withId(newId: Int) = Part(newId, subtype, supplier, name, description, count, sources, pack, value)

    companion object {
        fun testData() = listOf(
                Part(1, Hierarchy.of("IC", "Audio", "Amplifier"), Supplier("ST"), "LM386", "Mono audio amplifier, 3-15V", 5L, emptyList(), Package("DIP-8")),
                Part(2, Hierarchy.of("IC", "Microcontroller", "8 bit Atmel"), Supplier("Atmel"), "ATTiny85P", "AVR tiny, 8 bit, 2 kB RAM, 8 kB Flash, 16 MHz", 2L, emptyList(), Package("DIP-8")),
                Part(3, Hierarchy.of("IC", "Logic", "CMOS"), Supplier("ST"), "40106", "6* inverting Schmidt trigger", 1L, emptyList(), Package("DIP-14")),
                Part(4, Hierarchy.of("Module", "Power", "Buck"), Supplier("QSKJ"), "*DC-DC ADJ 3A", "DC-DC 12-24V to 0.8-17V 3A Step Down Power Supply", 4L, emptyList(), Package("4 pin PCB")),
                Part(5, Hierarchy.of("Discrete", "Transistor", "N-FET"), Supplier("IR"), "2N7000", "60V 600mA Vg(th) 3V", 15L, emptyList(), Package("TO-92"))
        )
    }
}

class PartModel : ItemViewModel<Part>() {
    val id = bind(Part::id)
    val subtype = bind(Part::subtype)
    val supplier = bind(Part::supplier)
    val name = bind(Part::name)
    val value = bind(Part::value)
    val description = bind(Part::description)
    val count = bind(Part::count)
    val sources = bind(Part::sources)
}

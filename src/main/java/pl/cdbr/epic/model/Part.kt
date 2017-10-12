package pl.cdbr.epic.model

import tornadofx.*

data class Part(
        val id: Int,
        val subtype: Subtype,
        val supplier: Supplier,
        val name: String,
        val value: String?,
        val description: String,
        val count: Long,
        val sources: List<Source> = emptyList(),
        val pack: Package
) {
    companion object {
        fun testData() = listOf(
                Part(1, Subtype("Amplifier", Type("Audio", Group("IC"))), Supplier("ST"), "LM386", "", "Mono audio amplifier, 3-15V", 5L, emptyList(), Package("DIP-8"))
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

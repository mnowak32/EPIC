package pl.cdbr.epic.model

data class Vendor(val name: String)
data class Source(val vendor: Vendor, val partNo: String)
package com.example.homeinvetry.core.common

data class ParsedVoiceItem(
    val name: String,
    val quantity: Double,
    val unit: String,
    val location: String
)

object VoiceCommandParser {
    fun parseAddCommand(spokenText: String): ParsedVoiceItem {
        val text = spokenText.lowercase().trim()

        var quantity = 1.0
        var unit = "kg"
        var location = "Pantry"
        var qtyString = ""

        val qtyRegex = Regex("""(\d+(\.\d+)?)""")
        val qtyMatch = qtyRegex.find(text)
        if (qtyMatch != null) {
            qtyString = qtyMatch.value
            quantity = qtyString.toDoubleOrNull() ?: 1.0
        }

        when {
            text.contains("litre") || text.contains("liter") || text.contains(" l ") -> unit = "litre"
            text.contains("gram") || text.contains(" g ") -> unit = "g"
            text.contains("pack") || text.contains("packet") -> unit = "packets"
            text.contains("piece") -> unit = "pieces"
            text.contains("box") -> unit = "boxes"
            text.contains("bottle") -> unit = "bottles"
            text.contains("kg") || text.contains("kilo") -> unit = "kg"
        }

        when {
            text.contains("kitchen") -> location = "Kitchen"
            text.contains("fridge") || text.contains("refrigerator") -> location = "Fridge"
            text.contains("freezer") -> location = "Freezer"
            text.contains("bathroom") -> location = "Bathroom"
            text.contains("store room") || text.contains("storeroom") -> location = "Store Room"
            text.contains("pantry") -> location = "Pantry"
        }

        var cleanedName = text
            .replace("add", "")
            .replace("to $location", "", ignoreCase = true)
            .replace("in $location", "", ignoreCase = true)

        if (qtyString.isNotEmpty()) {
            cleanedName = cleanedName.replace(qtyString, "")
        }

        cleanedName = cleanedName
            .replace("litres", "")
            .replace("litre", "")
            .replace("liter", "")
            .replace("kilograms", "")
            .replace("kilogram", "")
            .replace("kilos", "")
            .replace("kg", "")
            .replace("of", "")

        val words = cleanedName.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }
        val finalName = if (words.isNotEmpty()) {
            words.joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
        } else {
            spokenText
        }

        return ParsedVoiceItem(
            name = finalName,
            quantity = quantity,
            unit = unit,
            location = location
        )
    }
}

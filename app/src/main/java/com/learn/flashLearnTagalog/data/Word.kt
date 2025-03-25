package com.learn.flashLearnTagalog.data

data class Word(
    val english: String = "",
    val translations: List<String> = listOf(),
    val type: String = "",
    val category: String = "",
    val image: String? = "",
    val correctIndex: Int = 0,
    val incorrectTranslation: Boolean? = null
) {
    val id: String = "$type,$english".hashCode().toString()
    val length = if (translations.isNotEmpty()) translations[correctIndex].length else 0
}

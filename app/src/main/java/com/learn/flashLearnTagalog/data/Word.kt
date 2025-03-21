package com.learn.flashLearnTagalog.data

data class Word (
    val english : String = "",
    val translation : String = "",
    val type : String = "",
    val category : String = "",
    val image : String? = "",
    val correctTranslation : Boolean? = null,
    val uncommon : Boolean? = null
    ){
    val id : String = hashCode().toString()
    val length : Int = translation.length
}

package com.learn.flashLearnTagalog.data

data class User (
    val username: String,
    val wordStats: Map<String, Array<Any>>,
    val lessonStats: Map<String, Array<Any>>
)
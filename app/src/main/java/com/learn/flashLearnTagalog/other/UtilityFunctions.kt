package com.learn.flashLearnTagalog.other

import java.security.MessageDigest

class UtilityFunctions {

    companion object {
        fun sha256(input :String) : String{
            val bytes = input.toByteArray()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            return digest.fold("") { str, it -> str + "%02x".format(it) }
        }
    }


}
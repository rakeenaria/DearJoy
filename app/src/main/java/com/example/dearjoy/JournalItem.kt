package com.example.dearjoy

data class JournalItem(
    val journalID: String? = null,
    val userID: String? = null,
    val content: String? = null,
    val createdDate: String? = null,
    var dataMood: Int? = null
)
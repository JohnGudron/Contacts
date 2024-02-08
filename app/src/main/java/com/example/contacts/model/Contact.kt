package com.example.contacts.model

data class Contact(
    val id: Int,
    val name: String,
    val surname: String,
    val number: String,
    val isChecked: Boolean = false
)
package com.example.contacts

import android.app.Application
import com.example.contacts.model.ContactService

class App: Application() {
    val contactService = ContactService()
}
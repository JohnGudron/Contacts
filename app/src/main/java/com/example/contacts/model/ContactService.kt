package com.example.contacts.model

import kotlin.random.Random

typealias ContactListener = (contacts: List<Contact>) -> Unit

class ContactService {

    private val listeners = mutableSetOf<ContactListener>()

    private var names = listOf("John", "Sam", "Kent", "Donald", "Barak","Joseph", "Maria", "Anna",
        "Victoria", "Margarita", "Nadezda", "Andrey", "Maxim", "Diana", "Alexandra")
    private var surnames = listOf("Smith", "Matrix", "Kim", "New", "Obama", "Putin",
        "Lenin", "Ivanov", "Petrov", "Sidorov")

    private var data = MutableList(30) {
        Contact(
            it,
            names.random(),
            surnames.random(),
            Random.nextLong(1000000000, 9999999999).toString()
        )
    }

    fun addContact(contact: Contact) {
        data = MutableList(data.size) { data[it] }
        data.add(contact.copy(id = data.size))
        notifyChanges()
    }

    fun deleteContacts() {
        data = MutableList(data.size) { data[it] }
        data.removeIf { it.isChecked }
        notifyChanges()
    }

    fun checkContact(contact: Contact) {
        val ind = data.indexOfFirst { it.id == contact.id  }
        if(ind!= -1) {
            data = MutableList(data.size) { data[it] }
            data[ind] = data[ind].copy(isChecked = !contact.isChecked)
            notifyChanges()
        } else {
            println("Trying to check nonexistent element")
        }
    }

    fun uncheckContacts() {
        data = MutableList(data.size) { data[it].copy(isChecked = false) }
        notifyChanges()
    }

    fun editContact(contact: Contact) {
        val ind = data.indexOfFirst { it.id == contact.id  }
        if(ind!= -1) {
            data = MutableList(data.size) { data[it] }
            data[ind] = contact
            notifyChanges()
        } else {
            println("Trying to edit nonexistent element")
        }
    }

    fun moveContact(from: Int, to: Int) {
        data = MutableList(data.size) { data[it] }
        /*
        TouchHelper invokes on every items intersection, so everything works fine.
        For example, if we move 0 to 3 we have the following: 0 to 1 (1 to 0), 0(now 1) to 2(2 to 1), 0(now2) to 3(to 2)
        */
        data[to] = data[from].also { data[from] = data[to] }
        notifyChanges()
    }

    fun addListener(listener: ContactListener) {
        listeners.add(listener)
        listener.invoke(data)
    }

    fun removeListener(listener: ContactListener) {
        listeners.remove(listener)
    }

    private fun notifyChanges() {
        listeners.forEach { it.invoke(data) }
    }

}
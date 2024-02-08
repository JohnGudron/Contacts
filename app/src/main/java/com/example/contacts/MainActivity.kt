package com.example.contacts

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.contacts.model.Contact
import com.example.contacts.model.ContactListener

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: ContactAdapter

    private val contactListener:ContactListener = {
        adapter.data = it
    }
    private val contactService
        get() = (applicationContext as App).contactService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = ContactAdapter(object : ContactActionListener {

            override fun onContactDelete(contact: Contact) {
                contactService.checkContact(contact)
            }

            override fun onContactMove(contact: Contact, moveTo: Int) {
                TODO("Not yet implemented")
            }

            override fun onContactEdit(contact: Contact) {
                showEditDialog(contact)
            }

        }, false)
        contactService.addListener(contactListener)

        val layoutManager = LinearLayoutManager(this)
        val recycler = findViewById<RecyclerView>(R.id.contactRecycler)
        recycler.layoutManager = layoutManager
        recycler.adapter = adapter
        itemTouchHelper.attachToRecyclerView(recycler)

        val addBtn = findViewById<Button>(R.id.addBtn)
        val deleteBtn = findViewById<ImageButton>(R.id.deleteBtn)
        val cancelBtn = findViewById<Button>(R.id.cancelBtn)

        addBtn.setOnClickListener {
            if (addBtn.text == "ADD") {
                showAddDialog()
            } else {
                contactService.deleteContacts()
            }
        }

        deleteBtn.setOnClickListener {
            adapter.isDeleting = true
            adapter.notifyDataSetChanged()
            addBtn.text = "DELETE"
            cancelBtn.visibility = View.VISIBLE
        }

        cancelBtn.setOnClickListener {
            adapter.isDeleting = false
            adapter.notifyDataSetChanged()
            contactService.uncheckContacts()
            cancelBtn.visibility = View.GONE
            addBtn.text = "ADD"
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        contactService.removeListener(contactListener)
    }
    private fun showEditDialog(contact: Contact) {
        val dialogView = layoutInflater.inflate(R.layout.edit_dialog,null)
        val nameEt = dialogView.findViewById<EditText>(R.id.nameEt).apply { setText(contact.name) }
        val surnameEt = dialogView.findViewById<EditText>(R.id.surnameEt).apply { setText(contact.surname) }
        val numberEt = dialogView.findViewById<EditText>(R.id.numberEt).apply { setText(contact.number) }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Contact Editing")
            .setMessage("Input desirable data")
            .setPositiveButton("OK") { _, _ ->
                contactService.editContact(contact.copy(name = nameEt.text.toString(), surname = surnameEt.text.toString(), number = numberEt.text.toString()))
            }
            .create()
        dialog.show()
    }

    private fun showAddDialog() {
        val dialogView = layoutInflater.inflate(R.layout.edit_dialog,null)
        val nameEt = dialogView.findViewById<EditText>(R.id.nameEt)
        val surnameEt = dialogView.findViewById<EditText>(R.id.surnameEt)
        val numberEt = dialogView.findViewById<EditText>(R.id.numberEt)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Contact Adding")
            .setMessage("Input desirable data")
            .setNegativeButton("CANCEL") { _,_ ->
                Toast.makeText(this,"You add nothing",Toast.LENGTH_LONG).show()
            }
            .setPositiveButton("OK") { _, _ ->
                when {
                    nameEt.text.isEmpty() -> {
                        Toast.makeText(this,"You should enter name",Toast.LENGTH_LONG).show()
                        showAddDialog() // not sure that it's correct way
                    }
                    surnameEt.text.isEmpty() -> {
                        Toast.makeText(this,"You should enter surname",Toast.LENGTH_LONG).show()
                        showAddDialog()
                    }
                    numberEt.text.isEmpty() -> {
                        Toast.makeText(this,"You should enter number",Toast.LENGTH_LONG).show()
                        showAddDialog()
                    }
                    else -> contactService.addContact(Contact(0,nameEt.text.toString(), surnameEt.text.toString(), numberEt.text.toString()))
                }
            }
            .create()
        dialog.show()
    }

    private val itemTouchHelper by lazy {
        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END,
            0
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val from = viewHolder.adapterPosition
                val to = target.adapterPosition
                contactService.moveContact(from,to)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                TODO("Not yet implemented")
            }
        }

        ItemTouchHelper(simpleItemTouchCallback)
    }
}
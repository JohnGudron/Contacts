package com.example.contacts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.contacts.model.Contact

interface ContactActionListener {
    fun onContactDelete(contact: Contact)

    fun onContactMove(contact: Contact, moveTo: Int)

    fun onContactEdit(contact: Contact)
}
class ContactAdapter(private val actionListener: ContactActionListener, var isDeleting: Boolean): RecyclerView.Adapter<ContactViewHolder>(), View.OnClickListener {

    var data: List<Contact> = emptyList()
        set(value) {
            val diffCallback = ContactDiffCallback(field, value)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            field = value
            diffResult.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false)
        view.setOnClickListener (this)
        view.findViewById<CheckBox>(R.id.item_checkBox).setOnClickListener(this)
        return ContactViewHolder(view)
    }

    override fun onClick(v: View) {
        val contact = (v.tag as Contact)
        when (v.id) {
            R.id.item_checkBox -> {
                actionListener.onContactDelete(contact)
            } else -> {
                if (!isDeleting) actionListener.onContactEdit(contact)
            }
        }
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = data[position]
        with (holder) {
            val idTv = itemView.findViewById<TextView>(R.id.idTv)
            val nameTv = itemView.findViewById<TextView>(R.id.nameTv)
            val surnameTv = itemView.findViewById<TextView>(R.id.surnameTv)
            val numberTv = itemView.findViewById<TextView>(R.id.numberTv)
            val checkBox = itemView.findViewById<CheckBox>(R.id.item_checkBox)

            checkBox.tag = contact
            itemView.tag = contact

            if (isDeleting) {
                checkBox.isVisible = true
                checkBox.isChecked = contact.isChecked
            } else {
                checkBox.isVisible = false
            }

            idTv.text = contact.id.toString()
            nameTv.text = contact.name
            surnameTv.text = contact.surname
            numberTv.text = contact.number
        }
    }
}

class ContactViewHolder(view: View): RecyclerView.ViewHolder(view)

class ContactDiffCallback(private val oldList: List<Contact>, private val newList: List<Contact>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
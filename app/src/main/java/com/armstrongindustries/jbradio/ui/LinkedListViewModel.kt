package com.armstrongindustries.jbradio.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// Node class representing each element in the linked list
data class Node<T>(var value: T, var next: Node<T>? = null)

// ViewModel to hold the LinkedList logic
class LinkedListViewModel<T> : ViewModel() {
    private var head: Node<T>? = null
    private var size = 0

    // MutableLiveData to observe the list
    private val _list = MutableLiveData<List<T>>(emptyList())
    val list: LiveData<List<T>> get() = _list

    // Function to add a value to the list
    fun add(value: T) {
        if (size < 3) {
            val newNode = Node(value)
            if (head == null) {
                head = newNode
            } else {
                var current = head
                while (current?.next != null) {
                    current = current.next
                }
                current?.next = newNode
            }
            size++
            updateLiveData()
        } else {
            println("List is full. Cannot add more than 3 items.")
        }
    }

    // Function to remove the head element
    fun remove() {
        if (head != null) {
            head = head?.next
            size--
            updateLiveData()
        } else {
            println("List is empty. Cannot remove any items.")
        }
    }

    // Update LiveData with current list values
    private fun updateLiveData() {
        val currentList = mutableListOf<T>()
        var current = head
        while (current != null) {
            currentList.add(current.value)
            current = current.next
        }
        _list.value = currentList
    }
}

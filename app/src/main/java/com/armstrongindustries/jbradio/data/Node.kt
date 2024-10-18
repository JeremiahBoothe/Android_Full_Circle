package com.armstrongindustries.jbradio.data

/**
 * Node for LinkedListViewModel
 * @param value The value of the node.
 * @param next The next node in the linked list.
 * @param <T> The type of the value stored in the node.
 */
data class Node<T>(var value: T, var next: Node<T>? = null)

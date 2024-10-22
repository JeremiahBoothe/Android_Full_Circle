package com.armstrongindustries.jbradio.data

/**
 * Node for the linked list
 * @param T type of the value
 * @param value value of the node
 * @param next next node
 */
data class Node<T>(var value: T, var next: Node<T>? = null)

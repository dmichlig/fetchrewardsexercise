package com.michlig.fetchrewardsexercise.model

class ListEntry(val id: Int, val listId: Int, val name: String): Comparable<ListEntry> {
    override fun compareTo(other: ListEntry): Int {
        return this.id.compareTo(other.id)
    }
}
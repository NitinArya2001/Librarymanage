package com.example.demo

import com.example.demo.data.Book

class User(val username: String) {
    private val borrowBooks: MutableList<Book> = mutableListOf()

    fun getBorrowedBooks(): List<Book> = borrowBooks

    fun borrowBook(book:Book){
        borrowBooks.add(book)
    }

    fun returnBook(book: Book){
        borrowBooks.remove(book)
    }
}
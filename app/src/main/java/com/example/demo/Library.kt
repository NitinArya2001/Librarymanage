package com.example.demo

import com.example.demo.data.Book

class Library {
    private val books: MutableList<Book> = mutableListOf()
    private var currentUser: User? = null

    init{
        books.add(Book("","Book1","Nitin",true))
        books.add(Book("","Book2","Rahul",true))
        books.add(Book("","Book3","Arya",true))
        books.add(Book("","Book4","Aman",true))
        books.add(Book("","Book5","joy",true))
        books.add(Book("","Book6","Rohit",true))
    }

    fun setCurrentUser(user: User){
        currentUser = user
    }
    fun getBooks(): List<Book> = books

    fun borrowBook(book: Book){
        if(book.isAvailable){
            book.isAvailable = true
            currentUser?.borrowBook(book)
        }
    }

    fun returnBook(book: Book){
        if(!book.isAvailable){
            book.isAvailable =true
            currentUser?.returnBook(book)
        }
    }

    fun isBookAvailable(title: String): Boolean{
        return books.any { it.name == title && it.isAvailable }
    }

}

















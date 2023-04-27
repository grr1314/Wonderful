// BookManager.aidl
package com.example.myapplication;
import com.example.myapplication.Book;
interface BookManager {
    List<Book> getBookList();
    void addBook(in Book book);
}
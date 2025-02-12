package com.example.demo

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.demo.data.Book
import com.example.demo.ui.theme.DemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LibraryManagement()
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryManagement(){
    val library = remember { Library() }
    val user = remember { User("user1") }
    library.setCurrentUser(user)

    var searchText by remember { mutableStateOf("") }
    var filteredBooks by remember { mutableStateOf(library.getBooks()) }
    var filteredBorrowedBooks by remember { mutableStateOf(library.getBorrowedBooks()) }

    LaunchedEffect(searchText) {
        filteredBooks = library.getBooks().filter { it.name?.contains(searchText, ignoreCase = true)
            ?: false }
        filteredBorrowedBooks = library.getBorrowedBooks().filter{ it.name.contains(searchText, ignoreCase = true}
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Library Management") })
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                SearchBar(searchText = searchText,onSearchChanged = { searchText = it})
                Spacer(modifier = Modifier.height(30.dp))
                BookList(
                    books = filteredBooks,
                    showBorrowButton = true,
                    onReturnBook = {book -> library.returnBook(book)}
                )
                Spacer(modifier = Modifier.height(30.dp))
                Text(text = "User Books")

                BookList(books = filteredBooks,
                    onReturnBook = { book -> library.returnBook(book) },
                    showBorrowButton = false)


            }
        }
    )

}

@Composable
fun SearchBar(searchText :String, onSearchChanged: (String) -> Unit){
    OutlinedTextField(
        value = searchText,
        onValueChange = onSearchChanged,
        label = {Text("Seach Books")},
        modifier = Modifier.fillMaxWidth()
            .padding(6.dp),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Search
        )
    )
}



@Composable
fun BookList(
    books: List<Book>,
    BorrowButton: Boolean,
    onReturnBook: (Book) -> Unit)
{
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(books){ book ->
            BookItem(
                book,
                onBorrowBack = onBorrowBook,
                onReturnBack = onReturnBook
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
fun BookItem(
    book: Book,
    onBorrowBack: (Book) -> Unit,
    onReturnBack: (Book) -> Unit
){
    Row(modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween) {
        Column {
            book.name?.let { Text(text = it, style = MaterialTheme.typography.titleSmall) }
            Text(
                text = if(book.isAvailable) "Available" else "Not Available",
                style = MaterialTheme.typography.bodyMedium

            )
        }
        if(book.isAvailable){
            Button(
                onClick = {onBorrowBack(book)}

            ){
                Text("Borrow")
            }
        }
        if(!book.isAvailable){
            Button(
                onClick = {onReturnBack(book)}

            ){
                Text("Return")
            }
        }
    }
}




@Preview(showBackground = true)
@Composable
fun DefaultPreview(){
    LibraryManagement()
}










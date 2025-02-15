package com.example.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.demo.data.Item
import com.example.demo.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HomeScreen()
        }
    }
}


@Composable
fun HomeScreen(viewModel: ProductViewModel = hiltViewModel()) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedSortOption by remember { mutableStateOf("None") }
    val items by viewModel.items.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val listState = rememberLazyListState()
    val isFetchingMore by viewModel.isFetchingMore.collectAsState()

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastIndex ->
                if (lastIndex != null && lastIndex == items.size - 1 && items.size % 10 == 0) {
                    viewModel.loadMoreItems()
                }
            }
    }

    Column(modifier = Modifier.fillMaxSize().padding(vertical = 100.dp)) {
        SearchBar(searchQuery) { newQuery ->
            searchQuery = newQuery.trim()

            if (searchQuery.isEmpty()) {
                viewModel.fetchItems()
            } else {
                viewModel.searchItems(searchQuery)
            }
        }

        SortDropdown { newSort ->
            selectedSortOption = newSort
            when (newSort) {
                "Price: Low to High" -> viewModel.getSortedItems("price", "asc")
                "Price: High to Low" -> viewModel.getSortedItems("price", "desc")
                "Name: A to Z" -> viewModel.getSortedItems("name", "asc")
                "Name: Z to A" -> viewModel.getSortedItems("name", "desc")
            }
        }

        LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
            items(items.size) { index ->
                ItemCard(item = items[index])
            }

            if (isFetchingMore) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }

        if (isLoading && items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        if (errorMessage?.isNotEmpty() == true) {
            Text(
                text = errorMessage!!,
                color = Color.Red,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}


@Composable
fun SearchBar(query: String, onQueryChanged: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = {
            onQueryChanged(it)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        label = { Text("Search by name or brand") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
        singleLine = true
    )
}

@Composable
fun SortDropdown(onSortSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val sortOptions = listOf("None", "Price: Low to High", "Price: High to Low", "Name: A to Z", "Name: Z to A")

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
        IconButton(onClick = { expanded = true }) {
            Image(painterResource( R.drawable.filter), contentDescription = "Filter",Modifier.size(20.dp))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            sortOptions.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        onSortSelected(option)
                        expanded = false
                    }
                ) {
                    Text(option)
                }
            }
        }
    }
}



@Composable
fun ItemCard(item: Item) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 4.dp
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Image(
                painter = rememberAsyncImagePainter(R.drawable.book),
                contentDescription = item.name,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.width(26.dp))
            Column {
                Text(text = item.name, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(text = item.brand, color = Color.Gray, fontSize = 20.sp)
                Text(text = "₹${item.price}", fontWeight = FontWeight.Bold, color = Color.Blue, fontSize = 20.sp)
            }
        }
    }
}

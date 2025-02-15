package com.example.demo.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demo.Repository.ProductRepository
import com.example.demo.data.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(private val repository: ProductRepository) : ViewModel() {

    private val _items = MutableStateFlow<List<Item>>(emptyList())
    val items: StateFlow<List<Item>> get() = _items

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _isFetchingMore = MutableStateFlow(false)
    val isFetchingMore: StateFlow<Boolean> get() = _isFetchingMore

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    private var lastSearchResults: List<Item>? = null
    private var currentPage = 1
    private val pageSize = 10
    private var isLastPage = false


    init{
        fetchItems()
    }

    /**
     * Fetch paginated items and update state.
     *
     * @param page Page number (1-based).
     * @param pageSize Number of items per page.
     */
    fun fetchItems() {
        Log.d("ProductViewModel", "fetchItems: called")
        if (_isLoading.value || isLastPage) return

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val newItems = repository.fetchItems(currentPage, pageSize)
                if (newItems.isEmpty()) {
                    isLastPage = true // Stop further calls if no more data
                } else {
                    _items.value = _items.value + newItems
                    currentPage++
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to fetch items: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    /**
     * Search items by name or brand.
     *
     * @param query Search query.
     */
    fun searchItems(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.searchItems(query)
                _items.value = result
            } catch (e: Exception) {
                _errorMessage.value = "Search failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Fetch sorted items.
     *
     * @param sortBy Sort by "name" or "price".
     * @param order Sort order "asc" or "desc".
     */
    fun getSortedItems(sortBy: String, order: String) {
        val currentList = lastSearchResults ?: _items.value
        executeRequest(
            request = { repository.sortItems(currentList,sortBy, order) },
            onSuccess = { _items.value = it },
            errorMessage = "Sorting failed"
        )
    }

    /**
     * A generic function to execute a repository request with error handling.
     */
    private fun <T> executeRequest(
        request: suspend () -> T,
        onSuccess: (T) -> Unit,
        errorMessage: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = request()
                onSuccess(result)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "$errorMessage: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Fetch next batch of items when scrolling reaches the end.
     */
    fun loadMoreItems() {
        if (_isFetchingMore.value || isLastPage) return

        viewModelScope.launch {
            _isFetchingMore.value = true
            try {
                delay(1500)
                val newItems = repository.fetchItems(currentPage, pageSize)

                if (newItems.isNotEmpty()) {
                    _items.value = _items.value + newItems
                    currentPage++
                } else {
                    isLastPage = true
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load more items: ${e.message}"
            } finally {
                _isFetchingMore.value = false
            }
        }
    }
}
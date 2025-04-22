package xyz.androidrey.multimoduletemplate.main.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import xyz.androidrey.multimoduletemplate.main.domain.repository.DataRepository
import xyz.androidrey.multimoduletemplate.main.data.entity.Product
import xyz.androidrey.multimoduletemplate.main.data.entity.SortOption
import xyz.androidrey.multimoduletemplate.main.data.source.local.TheDatabase
import xyz.androidrey.multimoduletemplate.main.data.source.remote.UserRemoteMediator
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalPagingApi::class)
class HomeViewModel @Inject constructor(
    private val repo: DataRepository,
    private val remoteMediator: UserRemoteMediator,
    private val database: TheDatabase
) : ViewModel() {

    private val _sortOption = MutableStateFlow(SortOption.TITLE)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateSort(option: SortOption) {
        _sortOption.value = option
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getSortedPagingFlow(): Flow<PagingData<Product>> {
        return combine(sortOption, searchQuery) { sort, query ->
            sort to query
        }.flatMapLatest { (sort, query) ->
            remoteMediator.currentSortOption = sort
            Pager(
                config = PagingConfig(pageSize = 20),
                remoteMediator = remoteMediator,
                pagingSourceFactory = {
                    if (query.isNotBlank()) {
                        database.productDao.getProductPagingBySearch(query)
                    } else {
                        when (sort) {
                            SortOption.TITLE -> database.productDao.getProductPagingSortedByTitle()
                            SortOption.PRICE -> database.productDao.getProductPagingSortedByPrice()
                            SortOption.RATING -> database.productDao.getProductPagingSortedByRating()
                        }
                    }
                }
            ).flow
        }.cachedIn(viewModelScope)
    }

}
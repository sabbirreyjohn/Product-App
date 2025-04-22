package xyz.androidrey.multimoduletemplate.main.data.source.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import xyz.androidrey.multimoduletemplate.main.data.entity.Product
import xyz.androidrey.multimoduletemplate.main.data.entity.SortOption
import xyz.androidrey.multimoduletemplate.main.data.entity.response.ProductResponse
import xyz.androidrey.multimoduletemplate.main.data.source.local.TheDatabase
import xyz.androidrey.multimoduletemplate.network.NetworkResult
import xyz.androidrey.multimoduletemplate.network.http.RequestHandler
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class UserRemoteMediator @Inject internal constructor(
    private val database: TheDatabase,
    private val requestHandler: RequestHandler
) : RemoteMediator<Int, Product>() {

    var currentSortOption: SortOption = SortOption.TITLE

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.SKIP_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Product>
    ): MediatorResult {
        return try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> 0
                LoadType.PREPEND -> return MediatorResult.Success(
                    endOfPaginationReached = true
                )

                LoadType.APPEND -> {
                    state.pages.sumOf { it.data.size }
                }
            }
            val queryParams = mapOf(
                "skip" to loadKey.toString(),
                "limit" to "5",
                "sortBy" to currentSortOption.name.lowercase(),
                "order" to "asc"
            )

            val products = requestHandler.get<ProductResponse>(
                urlPathSegments = listOf("products"),
                queryParams = queryParams
            )

            when (products) {
                is NetworkResult.Error -> {
                    MediatorResult.Error(products.exception)
                }

                is NetworkResult.Success -> {
                    database.withTransaction {
                        if (loadType == LoadType.REFRESH) {
                            database.productDao.clearAll()
                        }
                        database.productDao.insertAll(products.result.products)
                    }

                    val isEndReached = products.result.products.isEmpty() ||
                            (products.result.skip + products.result.products.size >= products.result.total)

                    MediatorResult.Success(endOfPaginationReached = isEndReached) }
            }
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}
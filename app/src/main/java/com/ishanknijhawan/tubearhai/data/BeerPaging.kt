package com.ishanknijhawan.tubearhai.data

import android.util.Log
import androidx.paging.PagingSource
import com.ishanknijhawan.tubearhai.api.BeerApi
import retrofit2.HttpException
import java.io.IOException


const val PAGING_INDEX = 1

class BeerPaging(private val beerApi: BeerApi) :
    PagingSource<Int, Beer>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Beer> {
        val position = params.key ?: PAGING_INDEX
        return try {
            val response = beerApi.getBeers(position, params.loadSize)
            val beers = response.body()!!

            LoadResult.Page(
                data = beers,
                prevKey = if (position == PAGING_INDEX) null else position - 1,
                nextKey = if (beers.isEmpty()) null else position + 1
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }
}
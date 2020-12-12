package com.ishanknijhawan.tubearhai.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.ishanknijhawan.tubearhai.api.BeerApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BeerRepository @Inject constructor(private val beerApi: BeerApi) {
    fun getBeerPages() = Pager(
        config = PagingConfig(
            pageSize = 10,
            maxSize = 500,
            enablePlaceholders = true
        ),
        pagingSourceFactory = { BeerPaging(beerApi) }
    ).liveData
}
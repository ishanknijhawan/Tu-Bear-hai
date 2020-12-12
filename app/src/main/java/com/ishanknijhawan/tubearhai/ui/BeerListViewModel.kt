package com.ishanknijhawan.tubearhai.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.ishanknijhawan.tubearhai.data.BeerRepository

class BeerListViewModel @ViewModelInject constructor(repository: BeerRepository) :
    ViewModel() {
    val beers = repository.getBeerPages().cachedIn(viewModelScope)
}
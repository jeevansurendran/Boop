package com.silverpants.instantaneous.ui.search

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.silverpants.instantaneous.domain.user.ObservableUserSearchCase
import kotlinx.coroutines.flow.debounce

class SearchViewModel @ViewModelInject constructor(private val observableUserSearchCase: ObservableUserSearchCase) :
    ViewModel() {
    private val _query = MutableLiveData("")
    val query: LiveData<String> = _query

    val result by lazy {
        observableUserSearchCase(_query.asFlow().debounce(100L)).asLiveData()
    }

    fun setQueryString(query: String) {
        _query.value = query
    }
}
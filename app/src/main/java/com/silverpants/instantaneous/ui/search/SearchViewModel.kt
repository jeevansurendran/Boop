package com.silverpants.instantaneous.ui.search

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import com.silverpants.instantaneous.domain.user.ObservableUserSearchCase

class SearchViewModel @ViewModelInject constructor(private val observableUserSearchCase: ObservableUserSearchCase) {
    private val _query = MutableLiveData("")
    private val query: LiveData<String> = _query

    val result by lazy {
        observableUserSearchCase(_query.asFlow()).asLiveData()
    }

    fun setQueryString(query: String) {
        _query.value = query
    }
}
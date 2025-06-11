package com.netimur.businesscard

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {
    private val _started = MutableStateFlow(false)
    val started = _started.asStateFlow()

    init {
        repeat()
    }

    fun repeat() {
        _started.update {
            true
        }
    }
}
package com.netimur.businesscard

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class MainViewModel : ViewModel() {
    private val _restartEvent = MutableSharedFlow<Boolean>()
    val restartEvent: SharedFlow<Boolean> = _restartEvent.asSharedFlow()
}
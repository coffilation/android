package com.coffilation.app.domain

import kotlinx.coroutines.flow.StateFlow

/**
 * @author pvl-zolotov on 11.05.2023
 */
class BasicModel<T, R>(
    val state: StateFlow<BasicState<T>>,
    val nextPage: (R) -> Unit,
    val retry: (R) -> Unit,
    val refresh: (R) -> Unit,
)

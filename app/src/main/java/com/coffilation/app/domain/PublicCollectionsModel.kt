package com.coffilation.app.domain

import kotlinx.coroutines.flow.StateFlow

/**
 * @author pvl-zolotov on 09.05.2023
 */
class PublicCollectionsModel(
    val state: StateFlow<PublicCollectionsState>,
    val nextPage: () -> Unit,
    val retry: () -> Unit,
    val refresh: () -> Unit,
)

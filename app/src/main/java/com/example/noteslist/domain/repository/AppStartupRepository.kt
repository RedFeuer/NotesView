package com.example.noteslist.domain.repository

interface AppStartupRepository {
    suspend fun shouldShowInitialShimmer() : Boolean
    suspend fun markInitialShimmerShown()
}
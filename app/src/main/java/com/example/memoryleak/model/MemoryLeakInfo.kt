package com.example.memoryleak.model

data class MemoryLeakInfo(
    val id: String,
    val title: String,
    val category: LeakCategory,
    val summary: String,
    val detailedExplanation: String,
    val badCodeSnippet: String,
    val fixedCodeSnippet: String,
    val solutionExplanation: String
)

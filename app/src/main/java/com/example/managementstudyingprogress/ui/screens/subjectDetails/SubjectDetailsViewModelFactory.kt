package com.example.managementstudyingprogress.ui.screens.subjectDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.managementstudyingprogress.data.db.Lab5Database

class SubjectDetailsViewModelFactory(
    private val database: Lab5Database
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SubjectDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SubjectDetailsViewModel(database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
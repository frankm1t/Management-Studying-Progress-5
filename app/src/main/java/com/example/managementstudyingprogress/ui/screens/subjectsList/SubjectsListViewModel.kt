package com.example.managementstudyingprogress.ui.screens.subjectsList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.managementstudyingprogress.data.db.Lab5Database
import com.example.managementstudyingprogress.data.entity.SubjectEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SubjectsListViewModel(private val database: Lab5Database) : ViewModel() {
    private val _subjectsList = MutableStateFlow<List<SubjectEntity>>(emptyList())
    val subjectsList: StateFlow<List<SubjectEntity>> get() = _subjectsList

    private val _filteredSubjects = MutableStateFlow<List<SubjectEntity>>(emptyList())
    val filteredSubjects: StateFlow<List<SubjectEntity>> get() = _filteredSubjects

    var searchQuery: String = ""
        set(value) {
            field = value
            filterSubjects(value)
        }

    init {
        fetchSubjects() // Fetch subjects initially
    }

    fun fetchSubjects() {
        viewModelScope.launch {
            _subjectsList.value = withContext(Dispatchers.IO) {
                database.subjectsDao.getAllSubjects()
            }
            // Filter subjects after fetching
            filterSubjects(searchQuery)
        }
    }

    private fun filterSubjects(query: String) {
        viewModelScope.launch {
            val filteredList = _subjectsList.value.filter { subject ->
                subject.title.contains(query, ignoreCase = true)
            }
            _filteredSubjects.value = filteredList
        }
    }

    fun addNewSubject(title: String) {
        if (title.isNotEmpty()) {
            viewModelScope.launch {
                val maxId = database.subjectsDao.getMaxSubjectId() ?: 0
                val newSubject = SubjectEntity(id = maxId + 1, title = title)
                database.subjectsDao.addSubject(newSubject)

                // Update the local list directly without fetching all subjects again
                _subjectsList.value = _subjectsList.value + newSubject
                filterSubjects(searchQuery) // Update filtered subjects based on the current search query
            }
        }
    }

    fun editSubject(subject: SubjectEntity, newTitle: String) {
        if (newTitle.isNotEmpty()) {
            val updatedSubject = subject.copy(title = newTitle)
            viewModelScope.launch {
                database.subjectsDao.updateSubject(updatedSubject)

                // Update the local list directly
                _subjectsList.value = _subjectsList.value.map {
                    if (it.id == subject.id) updatedSubject else it
                }
                filterSubjects(searchQuery) // Update filtered subjects
            }
        }
    }

    fun deleteSubject(subject: SubjectEntity) {
        viewModelScope.launch {
            database.subjectsDao.deleteSubject(subject)

            // Update the local list directly
            _subjectsList.value = _subjectsList.value.filter { it.id != subject.id }
            filterSubjects(searchQuery) // Update filtered subjects
        }
    }
}
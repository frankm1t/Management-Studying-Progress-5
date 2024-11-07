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

    init {
        fetchSubjects()
    }

    fun fetchSubjects(query: String = "") {
        viewModelScope.launch {
            val subjects = database.subjectsDao.getAllSubjects()
            if (query.isEmpty()) {
                _filteredSubjects.value = subjects
            } else {
                _filteredSubjects.value = subjects.filter {
                    it.title.contains(query, ignoreCase = true)
                }
            }
        }
    }

    fun addNewSubject(title: String) {
        viewModelScope.launch {
            val newSubject = SubjectEntity(title = title)
            database.subjectsDao.addSubject(newSubject)
            fetchSubjects()  // Update the list after adding a new subject
        }
    }

    fun editSubject(subject: SubjectEntity, newTitle: String) {
        viewModelScope.launch {
            val updatedSubject = subject.copy(title = newTitle)
            database.subjectsDao.updateSubject(updatedSubject)
            fetchSubjects()  // Update the list after editing the subject
        }
    }

    fun deleteSubject(subject: SubjectEntity) {
        viewModelScope.launch {
            database.subjectsDao.deleteSubject(subject)
            fetchSubjects()  // Update the list after deleting a subject
        }
    }
}


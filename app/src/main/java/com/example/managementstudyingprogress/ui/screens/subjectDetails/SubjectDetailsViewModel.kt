package com.example.managementstudyingprogress.ui.screens.subjectDetails

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.managementstudyingprogress.data.db.Lab5Database
import com.example.managementstudyingprogress.data.entity.LabStatus
import com.example.managementstudyingprogress.data.entity.SubjectEntity
import com.example.managementstudyingprogress.data.entity.SubjectLabEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SubjectDetailsViewModel(private val database: Lab5Database) : ViewModel() {
    private val _subjectStateFlow = MutableStateFlow<SubjectEntity?>(null)
    val subjectStateFlow: StateFlow<SubjectEntity?> get() = _subjectStateFlow

    private val _subjectLabsListStateFlow = MutableStateFlow<List<SubjectLabEntity>>(emptyList())
    val subjectLabsListStateFlow: StateFlow<List<SubjectLabEntity>> get() = _subjectLabsListStateFlow

    var selectedStatus by mutableStateOf(LabStatus.NOT_STARTED)

    fun initData(id: Int) {
        viewModelScope.launch {
            _subjectStateFlow.value = database.subjectsDao.getSubjectById(id)
            _subjectLabsListStateFlow.value = database.subjectLabsDao.getSubjectLabsBySubjectId(id)
        }
    }

    fun addNewLab(newLabTitle: String, newDescription: String, newComment: String) {
        val title = newLabTitle.trim()
        if (title.isNotEmpty()) {
            val newLab = SubjectLabEntity(
                subjectId = subjectStateFlow.value?.id ?: 0,
                title = title,
                description = newDescription.trim(),
                comment = newComment.trim(),
                status = selectedStatus
            )
            viewModelScope.launch {
                database.subjectLabsDao.addSubjectLab(newLab)
                refreshLabs()
            }
        }
    }

    fun deleteLab(lab: SubjectLabEntity) {
        viewModelScope.launch {
            database.subjectLabsDao.deleteSubjectLab(lab)
            refreshLabs()
        }
    }

    fun editLab(editedLab: SubjectLabEntity, title: String, description: String, comment: String) {
        val updatedLab = editedLab.copy(
            title = title.trim(),
            description = description.trim(),
            comment = comment.trim(),
            status = selectedStatus
        )
        viewModelScope.launch {
            database.subjectLabsDao.updateSubjectLab(updatedLab)
            refreshLabs()
        }
    }

    private suspend fun refreshLabs() {
        val currentSubjectId = subjectStateFlow.value?.id ?: return
        _subjectLabsListStateFlow.value = database.subjectLabsDao.getSubjectLabsBySubjectId(currentSubjectId)
    }

    fun getStatusColor(status: LabStatus): Color {
        return when (status) {
            LabStatus.IN_PROGRESS -> Color(0xFFB87502)
            LabStatus.COMPLETED -> Color(0xFF035406)
            LabStatus.NOT_STARTED -> Color(0xFFB85042)
        }
    }
}
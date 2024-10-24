package com.example.managementstudyingprogress.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subjectsLabs")
data class SubjectLabEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "subject_id") val subjectId: Int,
    val title: String,
    val description: String,
    val comment: String? = null,
    val status: LabStatus,
)

enum class LabStatus(val label: String) {
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    NOT_STARTED("Not Started")
}

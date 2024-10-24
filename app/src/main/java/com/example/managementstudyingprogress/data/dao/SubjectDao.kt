package com.example.managementstudyingprogress.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.managementstudyingprogress.data.entity.SubjectEntity


@Dao
interface SubjectDao {
    @Query("SELECT * FROM subjects")
    suspend fun getAllSubjects(): List<SubjectEntity>

    @Query("SELECT * FROM subjects WHERE id = :id")
    suspend fun getSubjectById(id: Int): SubjectEntity?

    @Query("SELECT MAX(id) FROM subjects")
    suspend fun getMaxSubjectId(): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSubject(subject: SubjectEntity)

    @Update
    suspend fun updateSubject(subject: SubjectEntity)

    @Delete
    suspend fun deleteSubject(subject: SubjectEntity)
}

package com.example.managementstudyingprogress.di

import android.content.Context
import androidx.room.Room
import com.example.managementstudyingprogress.data.db.Lab5Database
import com.example.managementstudyingprogress.ui.screens.subjectDetails.SubjectDetailsViewModel
import com.example.managementstudyingprogress.ui.screens.subjectsList.SubjectsListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val appModule = module {

    single<Lab5Database> {
        Room.databaseBuilder(
            get<Context>(),
            Lab5Database::class.java, "lab5Database"
        ).build()
    }

    viewModel { SubjectsListViewModel(get()) }
    viewModel { SubjectDetailsViewModel(get()) }
}
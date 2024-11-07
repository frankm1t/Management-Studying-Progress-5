package com.example.managementstudyingprogress.data.db


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import com.example.managementstudyingprogress.data.dao.SubjectDao
import com.example.managementstudyingprogress.data.dao.SubjectLabsDao
import com.example.managementstudyingprogress.data.entity.LabStatus
import com.example.managementstudyingprogress.data.entity.SubjectLabEntity
import com.example.managementstudyingprogress.data.entity.SubjectEntity


@Database(entities = [SubjectEntity::class, SubjectLabEntity::class], version = 1)
abstract class Lab5Database : RoomDatabase() {
    //DAO properties for each entity (table)
    // must be abstract (because Room will generate instances by itself)
    abstract val subjectsDao: SubjectDao
    abstract val subjectLabsDao: SubjectLabsDao
}

/**
 * DatabaseStorage - custom class where you initialize and store Lab4Database single instance
 *
 */
object DatabaseStorage {
    // ! Important - all operations with DB must be done from non-UI thread!
    // coroutineScope: CoroutineScope - is the scope which allows to run asynchronous operations
    // > we will learn it soon! For now just put it here
    private val coroutineScope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        },
    )

    // single instance of Lab4Database
    private var _database: Lab5Database? = null

    fun getDatabase(context: Context): Lab5Database {
        // if _database already contains Lab4Database instance, return this instance
        if (_database != null) return _database as Lab5Database
        // if not, create instance, preload some data and return this instance
        else {
            // creating Lab4Database instance by builder
            _database = Room.databaseBuilder(
                context,
                Lab5Database::class.java, "lab4Database"
            ).build()

            return _database as Lab5Database
        }
    }

}
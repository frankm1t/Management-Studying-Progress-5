package com.example.managementstudyingprogress.ui.screens.subjectsList

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.managementstudyingprogress.ui.theme.ManagementStudyingProgressTheme
import com.example.managementstudyingprogress.data.db.DatabaseStorage
import com.example.managementstudyingprogress.data.entity.SubjectEntity
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectsListScreen(onDetailsScreen: (Int) -> Unit) {
    val context = LocalContext.current
    val db = DatabaseStorage.getDatabase(context)

    // Create the ViewModel using the factory
    val viewModel: SubjectsListViewModel = viewModel(factory = SubjectsListViewModelFactory(db))

    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var newSubjectTitle by remember { mutableStateOf("") }
    var selectedSubject by remember { mutableStateOf<SubjectEntity?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var subjectToDelete by remember { mutableStateOf<SubjectEntity?>(null) }

    // Observe the filtered subjects
    val subjects by viewModel.filteredSubjects.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    fun updateSearch(query: String) {
        searchQuery = query
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE7E8D1))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            TextField(
                value = searchQuery,
                onValueChange = { query ->
                    updateSearch(query)
                    viewModel.fetchSubjects(query)  // Pass the search query to ViewModel
                },
                placeholder = { Text("Search subjects", color = Color.Gray) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon",
                        tint = Color.Gray
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .padding(top =12.dp),
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFFA7BEAE),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }
        Divider(
            color = Color.Gray,
            thickness = 1.dp,
            modifier = Modifier.fillMaxWidth()
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 8.dp)
        ) {
            items(subjects) { subject ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFFE4E5D1)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onDetailsScreen(subject.id) }
                            .padding(16.dp)
                    ) {
                        Text(
                            text = subject.title,
                            fontSize = 24.sp,
                            color = Color(0xFF333333),
                            modifier = Modifier.weight(1f).align(Alignment.CenterVertically)
                        )

                        IconButton(onClick = {
                            selectedSubject = subject
                            newSubjectTitle = subject.title
                            showEditDialog = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = Color(0xFF4E4E50)
                            )
                        }
                        IconButton(onClick = {
                            showDeleteDialog = true
                            subjectToDelete = subject
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color(0xFFB85042)
                            )
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA7BEAE)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .padding(bottom = 40.dp)
                ) {
                    Text(
                        text = "Add Subject",
                        fontSize = 20.sp,
                        color = Color(0xFF4E4E50)
                    )
                }
            }
        }
    }

    // Dialog for adding a new subject
    if (showAddDialog) {
        Dialog(onDismissRequest = { showAddDialog = false }) {
            Surface(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFFE4E5D1)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .background(Color(0xFFE4E5D1)),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Add New Subject",
                        fontSize = 20.sp,
                        color = Color(0xFF4E4E50),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    TextField(
                        value = newSubjectTitle,
                        onValueChange = { newSubjectTitle = it },
                        label = { Text("Title") },
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color(0xFFA7BEAE),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                viewModel.addNewSubject(newSubjectTitle)
                                showAddDialog = false
                                newSubjectTitle = ""
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA7BEAE)),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Text("Add", color = Color(0xFF4E4E50))
                        }

                        Button(
                            onClick = { showAddDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4E4E50)),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Text("Cancel", color = Color(0xFFE4E5D1))
                        }
                    }
                }
            }
        }
    }

    if (showEditDialog && selectedSubject != null) {
        Dialog(onDismissRequest = { showEditDialog = false }) {
            Surface(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFFE4E5D1)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .background(Color(0xFFE4E5D1)),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Edit Subject",
                        fontSize = 20.sp,
                        color = Color(0xFF4E4E50),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    TextField(
                        value = newSubjectTitle,
                        onValueChange = { newSubjectTitle = it },
                        label = { Text("Title") },
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color(0xFFA7BEAE),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = Color(0xFF4E4E50),
                            unfocusedTextColor = Color(0xFF4E4E50)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                selectedSubject?.let { viewModel.editSubject(it, newSubjectTitle) }
                                showEditDialog = false
                                newSubjectTitle = ""
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA7BEAE)),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Text("Save", color = Color(0xFF4E4E50))
                        }

                        Button(
                            onClick = { showEditDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4E4E50)),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Text("Cancel", color = Color(0xFFE4E5D1))
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog && subjectToDelete != null) {
        Dialog(onDismissRequest = { showDeleteDialog = false }) {
            Surface(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFFE4E5D1)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Are you sure you want to delete this subject?",
                        fontSize = 20.sp,
                        color = Color(0xFF4E4E50),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                subjectToDelete?.let { viewModel.deleteSubject(it) }
                                showDeleteDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFB85042)
                            ),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Text("Delete", color = Color(0xFFE4E5D1))
                        }

                        Button(
                            onClick = { showDeleteDialog = false },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4E4E50)
                            ),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Text("Cancel", color = Color(0xFFE4E5D1))
                        }
                    }
                }
            }
        }
    }
}


//@Preview(showBackground = true)
//@Composable
//private fun SubjectListScreenPreview() {
//    ManagementStudyingProgressTheme {
//        SubjectsListScreen({})
//    }
//}
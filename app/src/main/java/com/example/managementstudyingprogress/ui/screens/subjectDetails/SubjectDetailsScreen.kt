package com.example.managementstudyingprogress.ui.screens.subjectDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.managementstudyingprogress.data.entity.SubjectEntity
import com.example.managementstudyingprogress.data.entity.SubjectLabEntity
import com.example.managementstudyingprogress.data.db.DatabaseStorage
import com.example.managementstudyingprogress.data.entity.LabStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectDetailsScreen(
    id: Int
) {
    val context = LocalContext.current
    val db = DatabaseStorage.getDatabase(context)

    val subjectState = remember { mutableStateOf<SubjectEntity?>(null) }
    val subjectLabsState = remember { mutableStateOf<List<SubjectLabEntity>>(emptyList()) }


    var showAddDialog by remember { mutableStateOf(false) }
    var newLabTitle by remember { mutableStateOf("") }
    var newDescription by remember { mutableStateOf("") }
    var newComment by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var labToDelete by remember { mutableStateOf<SubjectLabEntity?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editedLab by remember { mutableStateOf<SubjectLabEntity?>(null) }
    var editLabTitle by remember { mutableStateOf("") }
    var editDescription by remember { mutableStateOf("") }
    var editComment by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf(LabStatus.NOT_STARTED) }

    LaunchedEffect(Unit) {
        subjectState.value = db.subjectsDao.getSubjectById(id)
        subjectLabsState.value = db.subjectLabsDao.getSubjectLabsBySubjectId(id)
    }

    fun addNewLab() {
        val title = newLabTitle.trim()
        val description = newDescription.trim()
        val comment = newComment.trim()

        if (title.isNotEmpty()) {
            val newLab = SubjectLabEntity(
                subjectId = subjectState.value?.id ?: 0,
                title = title,
                description = description,
                comment = comment,
                status = selectedStatus
            )

            coroutineScope.launch {
                db.subjectLabsDao.addSubjectLab(newLab)
                subjectLabsState.value = withContext(Dispatchers.IO) {
                    db.subjectLabsDao.getSubjectLabsBySubjectId(subjectState.value?.id ?: 0)
                }
            }
            showAddDialog = false
            newLabTitle = ""
            newDescription = ""
            newComment = ""
        }
    }

    fun deleteLab() {
        labToDelete?.let { lab ->
            coroutineScope.launch {
                db.subjectLabsDao.deleteSubjectLab(lab)
                subjectLabsState.value = withContext(Dispatchers.IO) {
                    db.subjectLabsDao.getSubjectLabsBySubjectId(subjectState.value?.id ?: 0)
                }
            }
            showDeleteDialog = false
            labToDelete = null
        }
    }

    fun editLab() {
        editedLab?.let { lab ->
            val title = editLabTitle?.trim() ?: ""
            val description = editDescription?.trim() ?: ""
            val comment = editComment?.trim() ?: ""

            if (title.isNotEmpty()) {
                val updatedLab = lab.copy(
                    title = title,
                    description = description,
                    comment = comment,
                    status = selectedStatus
                )

                coroutineScope.launch {
                    db.subjectLabsDao.updateSubjectLab(updatedLab)
                    subjectLabsState.value = withContext(Dispatchers.IO) {
                        db.subjectLabsDao.getSubjectLabsBySubjectId(subjectState.value?.id ?: 0)
                    }
                }
                showEditDialog = false
            }
        }
    }

    fun getStatusColor(status: LabStatus): Color {
        return when (status) {
            LabStatus.IN_PROGRESS -> Color(0xFFB87502)
            LabStatus.COMPLETED -> Color(0xFF035406)
            LabStatus.NOT_STARTED -> Color(0xFFB85042) // OrangeRed for "Not Started"
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE7E8D1))
            .padding(top = 24.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            shadowElevation = 8.dp,
            color = Color(0xFFE4E5D1)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Subject Details",
                    fontSize = 28.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    color = Color(0xFF4A4A4A)
                )
                Text(
                    text = "ID: ${subjectState.value?.id}",
                    fontSize = 18.sp,
                    color = Color(0xFF7A7A7A),
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .align(Alignment.Start)
                )
                Text(
                    text = "Title: ${subjectState.value?.title}",
                    fontSize = 18.sp,
                    color = Color(0xFF7A7A7A),
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .align(Alignment.Start)
                )
            }
        }
        Text(
            text = "Labs:",
            fontSize = 28.sp,
            modifier = Modifier
                .padding(top = 10.dp)
                .align(Alignment.CenterHorizontally),
            color = Color(0xFF4A4A4A)
        )
        Divider(
            color = Color.Gray,
            thickness = 1.dp,
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .align(Alignment.CenterHorizontally)
        ) {
            items(subjectLabsState.value) { lab ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    shadowElevation = 8.dp,
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFFE7E8D1)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFA7BEAE), shape = RoundedCornerShape(24.dp))
                            .border(
                                width = 1.dp,
                                shape = RoundedCornerShape(24.dp),
                                color = Color.Transparent
                            )
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = lab.title,
                                    fontSize = 20.sp,
                                    color = Color(0xFF3A3A3A),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .padding(top = 4.dp)
                                        .fillMaxWidth()
                                        .wrapContentWidth(Alignment.Start)
                                )
                            }

                            Row(
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                IconButton(onClick = {
                                    editedLab = lab
                                    editLabTitle = lab.title
                                    editDescription = lab.description
                                    editComment = lab.comment.toString()
                                    showEditDialog = true
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit",
                                        tint = Color(0xFF4E4E50)
                                    )
                                }

                                IconButton(onClick = {
                                    labToDelete = lab
                                    showDeleteDialog = true
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = Color(0xFFB85042)
                                    )
                                }
                            }
                        }

                        Row(modifier = Modifier.padding(top = 8.dp)) {
                            Text(
                                text = "Status: ",
                                fontSize = 16.sp,
                                color = Color(0xFF6D6D6D)
                            )
                            Text(
                                text = lab.status.label,
                                fontSize = 16.sp,
                                color = getStatusColor(lab.status)
                            )
                        }
                        Column(modifier = Modifier.padding(top = 12.dp)) {
                            Text(
                                text = "Description:",
                                fontSize = 16.sp,
                                color = Color(0xFF6D6D6D),
                            )
                            Text(
                                text = lab.description,
                                fontSize = 16.sp,
                                color = Color.Gray,
                            )
                        }

                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            Text(
                                text = "Comment:",
                                fontSize = 16.sp,
                                color = Color(0xFF6D6D6D),
                            )
                            Text(
                                text = lab.comment.toString(),
                                fontSize = 16.sp,
                                color = Color.Gray,
                            )
                        }

                    }
                }
            }

            item {
                Button(
                    onClick = {
                        showAddDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF88A290)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .padding(bottom = 40.dp)
                        .border(
                            width = 1.dp,
                            color = Color.Transparent,
                            shape = RoundedCornerShape(24.dp)
                        )
                        .background(Color(0xFF88A290), shape = RoundedCornerShape(24.dp))
                ) {
                    Text(
                        text = "Add Lab",
                        fontSize = 20.sp,
                        color = Color(0xFF4E4E50)
                    )
                }
            }
        }

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
                            text = "Add New Lab",
                            fontSize = 20.sp,
                            color = Color(0xFF4E4E50),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        TextField(
                            value = newLabTitle,
                            onValueChange = { newLabTitle = it },
                            label = { Text("Title") },
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color(0xFFA7BEAE),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )
                        TextField(
                            value = newDescription,
                            onValueChange = { newDescription = it },
                            label = { Text("Description") },
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color(0xFFA7BEAE),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )
                        TextField(
                            value = newComment,
                            onValueChange = { newComment = it },
                            label = { Text("Comment") },
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color(0xFFA7BEAE),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        )

                        Text("Status", modifier = Modifier.padding(vertical = 8.dp))
                        LabStatus.values().forEach { status ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                RadioButton(
                                    selected = (selectedStatus == status),
                                    onClick = { selectedStatus = status }
                                )
                                Text(
                                    color = getStatusColor(status),
                                    text = status.label,
                                    modifier = Modifier.padding(start = 8.dp)

                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = { addNewLab() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        0xFFA7BEAE
                                    )
                                ),
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                Text("Add", color = Color(0xFF4E4E50))
                            }
                            Button(
                                onClick = { showAddDialog = false },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        0xFF4E4E50
                                    )
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
        if (showDeleteDialog) {
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
                            text = "Are you sure you want to delete this lab?",
                            fontSize = 20.sp,
                            color = Color(0xFF4E4E50),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = { deleteLab() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        0xFFB85042
                                    )
                                ),
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Text("Delete", color = Color(0xFFE4E5D1))
                            }

                            Button(
                                onClick = { showDeleteDialog = false },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        0xFF4E4E50
                                    )
                                ),
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Text("Cancel", color = Color(0xFFE4E5D1))
                            }
                        }
                    }
                }
            }
        }

        if (showEditDialog) {
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
                            text = "Edit Lab",
                            fontSize = 20.sp,
                            color = Color(0xFF4E4E50),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        TextField(
                            value = editLabTitle,
                            onValueChange = { editLabTitle = it },
                            label = { Text("Title") },
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color(0xFFA7BEAE),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )
                        TextField(
                            value = editDescription,
                            onValueChange = { editDescription = it },
                            label = { Text("Description") },
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color(0xFFA7BEAE),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )
                        TextField(
                            value = editComment,
                            onValueChange = { editComment = it },
                            label = { Text("Comment") },
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color(0xFFA7BEAE),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        )

                        Text("Status", modifier = Modifier.padding(vertical = 8.dp))
                        LabStatus.values().forEach { status ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                RadioButton(
                                    selected = (selectedStatus == status),
                                    onClick = { selectedStatus = status }
                                )
                                Text(
                                    color = getStatusColor(status),
                                    text = status.label,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }


                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = { editLab() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFA7BEAE)
                                ),
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                Text("Save", color = Color(0xFF4E4E50))
                            }
                            Button(
                                onClick = { showEditDialog = false },
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
}

@Preview(showBackground = true)
@Composable
fun SubjectDetailsScreenPreview() {
    SubjectDetailsScreen(1)
}
package com.example.managementstudyingprogress.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.managementstudyingprogress.data.db.DatabaseStorage
import com.example.managementstudyingprogress.ui.screens.subjectDetails.SubjectDetailsScreen
import com.example.managementstudyingprogress.ui.screens.subjectDetails.SubjectDetailsViewModel
import com.example.managementstudyingprogress.ui.screens.subjectDetails.SubjectDetailsViewModelFactory
import com.example.managementstudyingprogress.ui.screens.subjectsList.SubjectsListScreen


const val SCREEN_SUBJECTS_LIST = "subjectsList"
const val SCREEN_SUBJECT_DETAILS = "subjectDetails"

@Composable
fun NavigationGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = SCREEN_SUBJECTS_LIST
    ) {

        composable(
            route = SCREEN_SUBJECTS_LIST
        ) {

            SubjectsListScreen { id ->
                navController.navigate("$SCREEN_SUBJECT_DETAILS/$id")
            }
        }

        composable(
            route = "$SCREEN_SUBJECT_DETAILS/{id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.IntType
                    nullable = false
                }
            )
        ) { backStack ->
            val context = LocalContext.current
            val database = DatabaseStorage.getDatabase(context)
            val viewModel: SubjectDetailsViewModel = viewModel(
                factory = SubjectDetailsViewModelFactory(database)
            )

            SubjectDetailsScreen(viewModel = viewModel, id = backStack.arguments?.getInt("id") ?: 0)
        }


    }
}
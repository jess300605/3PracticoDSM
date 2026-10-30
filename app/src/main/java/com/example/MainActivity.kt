package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.UserRole
import com.example.ui.AppScreen
import com.example.ui.MainTab
import com.example.ui.MainViewModel
import com.example.ui.screens.ApiSettingsDialog
import com.example.ui.screens.DeleteConfirmationDialog
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.InfoDialog
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.RatingDialog
import com.example.ui.screens.RegisterScreen
import com.example.ui.screens.ResourceFormDialog
import com.example.ui.screens.ResourceListScreen
import com.example.ui.screens.TeacherCrudScreen
import com.example.ui.theme.AprendeAppTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AprendeAppTheme {
                val currentScreen by viewModel.currentScreen.collectAsState()
                val currentTab by viewModel.currentTab.collectAsState()
                val currentUser by viewModel.currentUser.collectAsState()
                val isLoading by viewModel.isLoading.collectAsState()
                val snackbarMessage by viewModel.snackbarMessage.collectAsState()

                val resources by viewModel.filteredResources.collectAsState()
                val allResources by viewModel.resourceRepository.resources.collectAsState()
                val favoriteIds by viewModel.favoriteIds.collectAsState()
                val isOffline by viewModel.isOfflineMode.collectAsState()
                val searchQuery by viewModel.searchQuery.collectAsState()
                val selectedType by viewModel.selectedTypeFilter.collectAsState()
                val sortOption by viewModel.sortOption.collectAsState()
                val apiUrl by viewModel.apiUrl.collectAsState()

                // Dialog states
                val itemToRate by viewModel.itemToRate.collectAsState()
                val itemToDelete by viewModel.itemToDelete.collectAsState()
                val itemToEdit by viewModel.itemToEdit.collectAsState()
                val isAddingNew by viewModel.isAddingNew.collectAsState()
                val showApiConfig by viewModel.showApiConfigDialog.collectAsState()
                val showInfoDialog by viewModel.showInfoDialog.collectAsState()

                val isTeacher = currentUser?.role == UserRole.DOCENTE
                val snackbarHostState = remember { SnackbarHostState() }

                // Display snackbars
                LaunchedEffect(snackbarMessage) {
                    snackbarMessage?.let {
                        snackbarHostState.showSnackbar(it)
                        viewModel.clearSnackbar()
                    }
                }

                when (currentScreen) {
                    AppScreen.LOGIN -> {
                        LoginScreen(
                            isLoading = isLoading,
                            onLoginClick = { email, password ->
                                viewModel.login(email, password) { _, _ -> }
                            },
                            onDemoLoginClick = { role ->
                                viewModel.loginDemo(role)
                            },
                            onNavigateToRegister = {
                                viewModel.navigateTo(AppScreen.REGISTER)
                            },
                            onShowInfo = {
                                viewModel.openInfoDialog()
                            }
                        )
                    }

                    AppScreen.REGISTER -> {
                        val validationResult by viewModel.passwordValidation.collectAsState()

                        BackHandler {
                            viewModel.navigateTo(AppScreen.LOGIN)
                        }

                        RegisterScreen(
                            isLoading = isLoading,
                            validationResult = validationResult,
                            onPasswordChange = { viewModel.setRegisterPassword(it) },
                            onRegisterClick = { nombre, email, password, role ->
                                viewModel.register(nombre, email, password, role) { _, _ -> }
                            },
                            onNavigateBack = {
                                viewModel.navigateTo(AppScreen.LOGIN)
                            }
                        )
                    }

                    AppScreen.MAIN -> {
                        BackHandler(enabled = currentTab != MainTab.RESOURCES) {
                            viewModel.selectTab(MainTab.RESOURCES)
                        }

                        MainScreenContent(
                            currentTab = currentTab,
                            userRole = currentUser?.role ?: UserRole.ESTUDIANTE,
                            userName = currentUser?.nombre ?: stringResource(R.string.default_user_name),
                            isTeacher = isTeacher,
                            isOffline = isOffline,
                            isLoading = isLoading,
                            resources = resources,
                            allResources = allResources,
                            favoriteIds = favoriteIds,
                            searchQuery = searchQuery,
                            selectedType = selectedType,
                            sortOption = sortOption,
                            apiUrl = apiUrl,
                            snackbarHostState = snackbarHostState,
                            onTabSelected = { viewModel.selectTab(it) },
                            onSearchChange = { viewModel.setSearchQuery(it) },
                            onTypeSelect = { viewModel.setSelectedTypeFilter(it) },
                            onSortSelect = { viewModel.setSortOption(it) },
                            onToggleFavorite = { viewModel.toggleFavorite(it) },
                            onRateClick = { viewModel.openRatingDialog(it) },
                            onEditClick = { viewModel.openEditDialog(it) },
                            onDeleteClick = { viewModel.openDeleteDialog(it) },
                            onAddClick = { viewModel.openAddDialog() },
                            onRefresh = { viewModel.loadResources() },
                            onOpenApiConfig = { viewModel.openApiConfig() },
                            onOpenInfoDialog = { viewModel.openInfoDialog() },
                            onLogout = { viewModel.logout() }
                        )
                    }
                }

                // Global Dialogs
                itemToRate?.let { item ->
                    RatingDialog(
                        item = item,
                        onDismiss = { viewModel.closeRatingDialog() },
                        onSubmit = { rating -> viewModel.submitRating(item, rating) }
                    )
                }

                itemToDelete?.let { item ->
                    DeleteConfirmationDialog(
                        item = item,
                        onDismiss = { viewModel.closeDeleteDialog() },
                        onConfirm = { viewModel.executeDelete(item) }
                    )
                }

                if (isAddingNew || itemToEdit != null) {
                    ResourceFormDialog(
                        initialItem = itemToEdit,
                        onDismiss = { viewModel.closeEditOrAddDialog() },
                        onSave = { savedItem, isEdit ->
                            viewModel.saveResource(savedItem, isEdit)
                        }
                    )
                }

                if (showApiConfig) {
                    ApiSettingsDialog(
                        currentUrl = apiUrl,
                        onDismiss = { viewModel.closeApiConfig() },
                        onSaveUrl = { viewModel.updateApiUrl(it) },
                        onResetUrl = { viewModel.resetApiUrl() },
                        onTestUrl = { viewModel.testApiUrl(it) }
                    )
                }

                if (showInfoDialog) {
                    InfoDialog(onDismiss = { viewModel.closeInfoDialog() })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenContent(
    currentTab: MainTab,
    userRole: UserRole,
    userName: String,
    isTeacher: Boolean,
    isOffline: Boolean,
    isLoading: Boolean,
    resources: List<com.example.data.model.ResourceItem>,
    allResources: List<com.example.data.model.ResourceItem>,
    favoriteIds: Set<String>,
    searchQuery: String,
    selectedType: String,
    sortOption: com.example.ui.SortOption,
    apiUrl: String,
    snackbarHostState: SnackbarHostState,
    onTabSelected: (MainTab) -> Unit,
    onSearchChange: (String) -> Unit,
    onTypeSelect: (String) -> Unit,
    onSortSelect: (com.example.ui.SortOption) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onRateClick: (com.example.data.model.ResourceItem) -> Unit,
    onEditClick: (com.example.data.model.ResourceItem) -> Unit,
    onDeleteClick: (com.example.data.model.ResourceItem) -> Unit,
    onAddClick: () -> Unit,
    onRefresh: () -> Unit,
    onOpenApiConfig: () -> Unit,
    onOpenInfoDialog: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(R.string.app_name),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = if (isTeacher) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = stringResource(userRole.labelRes),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = stringResource(R.string.app_university),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onOpenApiConfig,
                        modifier = Modifier.testTag("top_btn_api")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Cloud,
                            contentDescription = stringResource(R.string.api_settings_title)
                        )
                    }

                    IconButton(
                        onClick = onOpenInfoDialog,
                        modifier = Modifier.testTag("top_btn_info")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = stringResource(R.string.info_dialog_title)
                        )
                    }

                    IconButton(
                        onClick = onLogout,
                        modifier = Modifier.testTag("top_btn_logout")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = stringResource(R.string.btn_logout),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar {
                // Tab 1: Recursos
                NavigationBarItem(
                    selected = currentTab == MainTab.RESOURCES,
                    onClick = { onTabSelected(MainTab.RESOURCES) },
                    icon = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_resources)) },
                    modifier = Modifier.testTag("nav_tab_resources")
                )

                // Tab 2: Favoritos
                NavigationBarItem(
                    selected = currentTab == MainTab.FAVORITES,
                    onClick = { onTabSelected(MainTab.FAVORITES) },
                    icon = {
                        if (favoriteIds.isNotEmpty()) {
                            BadgedBox(badge = { Badge { Text("${favoriteIds.size}") } }) {
                                Icon(Icons.Filled.Favorite, contentDescription = null)
                            }
                        } else {
                            Icon(Icons.Filled.Favorite, contentDescription = null)
                        }
                    },
                    label = { Text(stringResource(R.string.nav_favorites)) },
                    modifier = Modifier.testTag("nav_tab_favorites")
                )

                // Tab 3: Gestión CRUD (Docente)
                NavigationBarItem(
                    selected = currentTab == MainTab.CRUD_DOCENTE,
                    onClick = { onTabSelected(MainTab.CRUD_DOCENTE) },
                    icon = {
                        if (isTeacher) {
                            Icon(Icons.Filled.Build, contentDescription = null)
                        } else {
                            Icon(Icons.Filled.School, contentDescription = null)
                        }
                    },
                    label = { Text(stringResource(R.string.nav_manage)) },
                    modifier = Modifier.testTag("nav_tab_crud")
                )

                // Tab 4: Perfil
                NavigationBarItem(
                    selected = currentTab == MainTab.PROFILE,
                    onClick = { onTabSelected(MainTab.PROFILE) },
                    icon = { Icon(Icons.Filled.Person, contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_profile)) },
                    modifier = Modifier.testTag("nav_tab_profile")
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                MainTab.RESOURCES -> {
                    ResourceListScreen(
                        resources = resources,
                        favoriteIds = favoriteIds,
                        isTeacher = isTeacher,
                        isOffline = isOffline,
                        isLoading = isLoading,
                        searchQuery = searchQuery,
                        selectedType = selectedType,
                        sortOption = sortOption,
                        onSearchChange = onSearchChange,
                        onTypeSelect = onTypeSelect,
                        onSortSelect = onSortSelect,
                        onToggleFavorite = onToggleFavorite,
                        onRateClick = onRateClick,
                        onEditClick = if (isTeacher) onEditClick else null,
                        onDeleteClick = if (isTeacher) onDeleteClick else null,
                        onRefresh = onRefresh
                    )
                }

                MainTab.FAVORITES -> {
                    FavoritesScreen(
                        resources = allResources,
                        favoriteIds = favoriteIds,
                        isTeacher = isTeacher,
                        onToggleFavorite = onToggleFavorite,
                        onRateClick = onRateClick
                    )
                }

                MainTab.CRUD_DOCENTE -> {
                    TeacherCrudScreen(
                        resources = allResources,
                        isTeacher = isTeacher,
                        onAddClick = onAddClick,
                        onEditClick = onEditClick,
                        onDeleteClick = onDeleteClick
                    )
                }

                MainTab.PROFILE -> {
                    ProfileScreen(
                        user = com.example.data.model.User(
                            id = "",
                            nombre = userName,
                            email = if (isTeacher) "docente@udb.edu.sv" else "estudiante@udb.edu.sv",
                            passwordHash = "",
                            role = userRole
                        ),
                        apiUrl = apiUrl,
                        onOpenApiConfig = onOpenApiConfig,
                        onOpenInfoDialog = onOpenInfoDialog,
                        onLogout = onLogout
                    )
                }
            }
        }
    }
}

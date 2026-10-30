package com.example.ui

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.local.AppDatabase
import com.example.data.model.LocalizedException
import com.example.data.model.PasswordValidationResult
import com.example.data.model.PasswordValidator
import com.example.data.model.ResourceItem
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.data.repository.AuthRepository
import com.example.data.repository.ResourceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    LOGIN,
    REGISTER,
    MAIN
}

enum class MainTab {
    RESOURCES,
    FAVORITES,
    CRUD_DOCENTE,
    PROFILE
}

enum class SortOption {
    TITLE_ASC,
    TITLE_DESC,
    RATING_DESC,
    ID_DESC
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    val authRepository = AuthRepository(db.userDao())
    val resourceRepository = ResourceRepository(application, db.resourceDao(), db.favoriteDao(), db.ratingDao())

    val currentUser: StateFlow<User?> = authRepository.currentUser
    val apiUrl: StateFlow<String> = resourceRepository.apiUrl
    val isOfflineMode: StateFlow<Boolean> = resourceRepository.isOfflineMode

    // Navigation & Tab State
    private val _currentScreen = MutableStateFlow(AppScreen.LOGIN)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _currentTab = MutableStateFlow(MainTab.RESOURCES)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    // Loading & Feedback
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    // Search, Filter & Sort
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedTypeFilter = MutableStateFlow("Todos")
    val selectedTypeFilter: StateFlow<String> = _selectedTypeFilter.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.TITLE_ASC)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    // Active Dialogs
    private val _itemToRate = MutableStateFlow<ResourceItem?>(null)
    val itemToRate: StateFlow<ResourceItem?> = _itemToRate.asStateFlow()

    private val _itemToDelete = MutableStateFlow<ResourceItem?>(null)
    val itemToDelete: StateFlow<ResourceItem?> = _itemToDelete.asStateFlow()

    private val _itemToEdit = MutableStateFlow<ResourceItem?>(null)
    val itemToEdit: StateFlow<ResourceItem?> = _itemToEdit.asStateFlow()

    private val _isAddingNew = MutableStateFlow(false)
    val isAddingNew: StateFlow<Boolean> = _isAddingNew.asStateFlow()

    private val _showApiConfigDialog = MutableStateFlow(false)
    val showApiConfigDialog: StateFlow<Boolean> = _showApiConfigDialog.asStateFlow()

    private val _showInfoDialog = MutableStateFlow(false)
    val showInfoDialog: StateFlow<Boolean> = _showInfoDialog.asStateFlow()

    // Registration password live validation
    private val _registerPassword = MutableStateFlow("")
    val registerPassword: StateFlow<String> = _registerPassword.asStateFlow()

    val passwordValidation: StateFlow<PasswordValidationResult> = _registerPassword.combine(_registerPassword) { pwd, _ ->
        PasswordValidator.validate(pwd)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, PasswordValidator.validate(""))

    // Combined filtered resources
    val filteredResources: StateFlow<List<ResourceItem>> = combine(
        resourceRepository.resources,
        _searchQuery,
        _selectedTypeFilter,
        _sortOption
    ) { allItems, query, typeFilter, sort ->
        var list = allItems

        // Filter by Type
        if (typeFilter != "Todos") {
            list = list.filter { it.tipo.equals(typeFilter, ignoreCase = true) }
        }

        // Advanced Search by ID, Title, or Type
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter {
                it.id.lowercase().contains(q) ||
                it.titulo.lowercase().contains(q) ||
                it.tipo.lowercase().contains(q) ||
                it.descripcion.lowercase().contains(q)
            }
        }

        // Sort
        when (sort) {
            SortOption.TITLE_ASC -> list.sortedBy { it.titulo.lowercase() }
            SortOption.TITLE_DESC -> list.sortedByDescending { it.titulo.lowercase() }
            SortOption.RATING_DESC -> list.sortedByDescending { it.calificacion }
            SortOption.ID_DESC -> list.sortedByDescending { it.id.toLongOrNull() ?: 0L }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            authRepository.initializeDefaultUsers()
            loadResources()
        }

        // Collect current user changes to update favorite listener
        viewModelScope.launch {
            currentUser.collect { user ->
                if (user != null) {
                    resourceRepository.getFavoriteIdsFlow(user.id).collect { ids ->
                        _favoriteIds.value = ids.toSet()
                    }
                } else {
                    _favoriteIds.value = emptySet()
                }
            }
        }
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun selectTab(tab: MainTab) {
        _currentTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedTypeFilter(type: String) {
        _selectedTypeFilter.value = type
    }

    fun setSortOption(sort: SortOption) {
        _sortOption.value = sort
    }

    fun setRegisterPassword(password: String) {
        _registerPassword.value = password
    }

    /** Obtiene un texto de strings.xml (con argumentos opcionales). */
    private fun str(@StringRes id: Int, vararg args: Any): String =
        getApplication<Application>().getString(id, *args)

    /** Convierte un error en texto para el usuario usando strings.xml. */
    private fun errorText(error: Throwable?, @StringRes fallback: Int): String =
        if (error is LocalizedException) str(error.messageRes) else str(fallback)

    fun showSnackbar(msg: String) {
        _snackbarMessage.value = msg
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun loadResources() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = resourceRepository.loadResources(forceRefresh = true)
            _isLoading.value = false
            if (result.isFailure) {
                showSnackbar(str(R.string.snack_load_error))
            }
        }
    }

    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = authRepository.login(email, password)
            _isLoading.value = false
            if (result.isSuccess) {
                _currentScreen.value = AppScreen.MAIN
                onResult(true, null)
            } else {
                val errorMsg = errorText(result.exceptionOrNull(), R.string.snack_login_error_default)
                showSnackbar(errorMsg)
                onResult(false, errorMsg)
            }
        }
    }

    fun loginDemo(role: UserRole) {
        viewModelScope.launch {
            _isLoading.value = true
            authRepository.loginDemo(role)
            _isLoading.value = false
            _currentScreen.value = AppScreen.MAIN
            showSnackbar(str(R.string.snack_logged_in_as, str(role.labelRes)))
        }
    }

    fun register(nombre: String, email: String, password: String, role: UserRole, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = authRepository.register(nombre, email, password, role)
            _isLoading.value = false
            if (result.isSuccess) {
                _currentScreen.value = AppScreen.MAIN
                showSnackbar(str(R.string.snack_account_created))
                onResult(true, null)
            } else {
                val errorMsg = errorText(result.exceptionOrNull(), R.string.snack_register_error_default)
                showSnackbar(errorMsg)
                onResult(false, errorMsg)
            }
        }
    }

    fun logout() {
        authRepository.logout()
        _currentScreen.value = AppScreen.LOGIN
        _currentTab.value = MainTab.RESOURCES
        showSnackbar(str(R.string.snack_logged_out))
    }

    fun toggleFavorite(resourceId: String) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val added = resourceRepository.toggleFavorite(user.id, resourceId)
            showSnackbar(str(if (added) R.string.favorite_added else R.string.favorite_removed))
        }
    }

    // Rating
    fun openRatingDialog(item: ResourceItem) {
        _itemToRate.value = item
    }

    fun closeRatingDialog() {
        _itemToRate.value = null
    }

    fun submitRating(item: ResourceItem, rating: Int) {
        viewModelScope.launch {
            val userId = currentUser.value?.id ?: return@launch
            _isLoading.value = true
            val res = resourceRepository.rateResource(userId, item.id, rating)
            _isLoading.value = false
            closeRatingDialog()
            if (res.isSuccess) {
                showSnackbar(str(R.string.snack_rating_sent, rating))
            } else {
                showSnackbar(str(R.string.snack_rating_error))
            }
        }
    }

    // CRUD Docente Actions
    fun openAddDialog() {
        _isAddingNew.value = true
    }

    fun openEditDialog(item: ResourceItem) {
        _itemToEdit.value = item
    }

    fun closeEditOrAddDialog() {
        _isAddingNew.value = false
        _itemToEdit.value = null
    }

    fun saveResource(item: ResourceItem, isEdit: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = if (isEdit) {
                resourceRepository.updateResource(item)
            } else {
                resourceRepository.addResource(item)
            }
            _isLoading.value = false
            if (result.isSuccess) {
                // Solo se cierra el formulario si la API aceptó el cambio
                closeEditOrAddDialog()
                showSnackbar(str(if (isEdit) R.string.msg_updated_success else R.string.msg_created_success))
            } else {
                showSnackbar(str(R.string.snack_save_error))
            }
        }
    }

    fun openDeleteDialog(item: ResourceItem) {
        _itemToDelete.value = item
    }

    fun closeDeleteDialog() {
        _itemToDelete.value = null
    }

    fun executeDelete(item: ResourceItem) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = resourceRepository.deleteResource(item.id)
            _isLoading.value = false
            closeDeleteDialog()
            if (result.isSuccess) {
                showSnackbar(str(R.string.msg_deleted_success))
            } else {
                showSnackbar(str(R.string.snack_delete_error))
            }
        }
    }

    // API Config Actions
    fun openApiConfig() {
        _showApiConfigDialog.value = true
    }

    fun closeApiConfig() {
        _showApiConfigDialog.value = false
    }

    fun openInfoDialog() {
        _showInfoDialog.value = true
    }

    fun closeInfoDialog() {
        _showInfoDialog.value = false
    }

    fun updateApiUrl(url: String) {
        viewModelScope.launch {
            resourceRepository.setApiUrl(url)
            closeApiConfig()
            showSnackbar(str(R.string.snack_api_url_updated))
        }
    }

    fun resetApiUrl() {
        viewModelScope.launch {
            resourceRepository.resetApiUrl()
            closeApiConfig()
            showSnackbar(str(R.string.snack_api_url_reset))
        }
    }

    suspend fun testApiUrl(url: String): Boolean {
        return resourceRepository.testApiConnection(url)
    }
}

package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.EvaluationRecord
import com.example.data.model.SocraticSession
import com.example.data.model.UserProfile
import com.example.data.repository.FirebaseRepository
import com.example.data.repository.GeminiService
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

enum class AppScreen {
    ONBOARDING,
    DIAGNOSIS,
    SCENARIO,
    ZEBRA,
    ARCHIVE,
    REALITY_TRENDS,
    GUIDE,
    CHAT
}

data class UiState(
    val currentScreen: AppScreen = AppScreen.ONBOARDING,
    val userName: String = "",
    val userStory: String = "",
    val isDiagnosing: Boolean = false,
    val currentSession: SocraticSession? = null,
    val activeSessionId: String? = null,
    val userProfile: UserProfile? = null,
    val sessionsList: List<SocraticSession> = emptyList(),
    val evaluationsList: List<EvaluationRecord> = emptyList(),
    val errorMessage: String? = null,
    val ethicalViolationMessage: String? = null,
    val isZebraUpdating: Boolean = false,
    val showWeeklyDialog: Boolean = false
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FirebaseRepository(application)
    private val geminiService = GeminiService()

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        val currentUser = Firebase.auth.currentUser
        if (currentUser != null) {
            val displayName = currentUser.displayName ?: ""
            _uiState.value = _uiState.value.copy(userName = displayName)
            observeUserData(currentUser.uid)
        }
    }

    fun onUserAuthenticated() {
        val currentUser = Firebase.auth.currentUser ?: return
        val displayName = currentUser.displayName ?: ""
        if (_uiState.value.userName.isBlank()) {
            _uiState.value = _uiState.value.copy(userName = displayName)
        }
        observeUserData(currentUser.uid)
    }

    private fun observeUserData(uid: String) {
        // Observe profile
        viewModelScope.launch {
            repository.observeUserProfile(uid)
                .catch { /* handled in repo */ }
                .collect { profile ->
                    _uiState.value = _uiState.value.copy(userProfile = profile)
                }
        }

        // Observe sessions
        viewModelScope.launch {
            repository.observeSessions()
                .catch { /* handled in repo */ }
                .collect { sessions ->
                    _uiState.value = _uiState.value.copy(sessionsList = sessions)
                    // If no active session, pick the latest
                    if (_uiState.value.currentSession == null && sessions.isNotEmpty()) {
                        val latest = sessions.first()
                        _uiState.value = _uiState.value.copy(
                            currentSession = latest,
                            activeSessionId = latest.id
                        )
                    }
                }
        }

        // Observe evaluations
        viewModelScope.launch {
            repository.observeEvaluations()
                .catch { /* handled in repo */ }
                .collect { evals ->
                    _uiState.value = _uiState.value.copy(evaluationsList = evals)
                }
        }
    }

    fun updateUserName(name: String) {
        _uiState.value = _uiState.value.copy(userName = name)
    }

    fun updateUserStory(story: String) {
        _uiState.value = _uiState.value.copy(userStory = story)
    }

    fun navigateTo(screen: AppScreen) {
        _uiState.value = _uiState.value.copy(currentScreen = screen)
    }

    fun selectSession(session: SocraticSession) {
        _uiState.value = _uiState.value.copy(
            currentSession = session,
            activeSessionId = session.id,
            currentScreen = AppScreen.DIAGNOSIS
        )
    }

    fun clearEthicalMessage() {
        _uiState.value = _uiState.value.copy(ethicalViolationMessage = null)
    }

    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun toggleWeeklyDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showWeeklyDialog = show)
    }

    fun startDiagnosis() {
        val uid = Firebase.auth.currentUser?.uid ?: return
        val name = _uiState.value.userName.ifBlank { "Pencari Kebijakan" }
        val story = _uiState.value.userStory.trim()

        if (story.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Silakan ceritakan beban atau pertanyaan hidup Anda terlebih dahulu.")
            return
        }

        _uiState.value = _uiState.value.copy(isDiagnosing = true, errorMessage = null, ethicalViolationMessage = null)

        viewModelScope.launch {
            try {
                // Save user profile first
                repository.saveUserProfile(name)

                // Run AI diagnosis
                val result = geminiService.diagnose(name, story, uid)

                if (result.isUnethical) {
                    _uiState.value = _uiState.value.copy(
                        isDiagnosing = false,
                        ethicalViolationMessage = result.unethicalRefusalMessage
                    )
                    return@launch
                }

                // Save session into Firestore
                val sessionId = repository.saveSession(result.session)

                val sessionWithId = result.session.copy(id = sessionId)

                _uiState.value = _uiState.value.copy(
                    isDiagnosing = false,
                    currentSession = sessionWithId,
                    activeSessionId = sessionId,
                    currentScreen = AppScreen.DIAGNOSIS
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isDiagnosing = false,
                    errorMessage = "Gagal memproses diagnosis: ${e.message}"
                )
            }
        }
    }

    fun updateZebraStatus(newStatus: String) {
        val sessionId = _uiState.value.activeSessionId ?: return
        val currentSession = _uiState.value.currentSession ?: return
        val zebra = currentSession.getParsedZebra()

        _uiState.value = _uiState.value.copy(isZebraUpdating = true)

        viewModelScope.launch {
            try {
                repository.updateZebraStatus(sessionId, newStatus, zebra.dampakPoin)

                val updatedZebraMap = HashMap(currentSession.zebra)
                updatedZebraMap["status"] = newStatus
                val updatedSession = currentSession.copy(zebra = updatedZebraMap)

                _uiState.value = _uiState.value.copy(
                    currentSession = updatedSession,
                    isZebraUpdating = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isZebraUpdating = false,
                    errorMessage = "Gagal memperbarui status ZEBRA: ${e.message}"
                )
            }
        }
    }

    fun submitWeeklyEvaluation(status: String, cerita: String) {
        val sessionId = _uiState.value.activeSessionId ?: return
        viewModelScope.launch {
            try {
                repository.saveWeeklyEvaluation(sessionId, status, cerita)
                _uiState.value = _uiState.value.copy(showWeeklyDialog = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Gagal menyimpan evaluasi mingguan: ${e.message}"
                )
            }
        }
    }
}

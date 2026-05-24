package si.sensum.demo.model

sealed interface UiStatus {
    data object Idle : UiStatus
    data object Loading : UiStatus
    data class Success(val message: String) : UiStatus
    data class Error(val message: String) : UiStatus
}
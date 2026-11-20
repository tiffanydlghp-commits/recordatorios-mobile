package com.example.recordatorios.ui.detalle

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.recordatorios.data.AppContainer
import com.example.recordatorios.data.RecordatorioRepository
import com.example.recordatorios.domain.Recordatorio
import com.example.recordatorios.navigation.Rutas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

/** Estados de la pantalla de detalle. */
sealed interface DetalleUiState {
    data object Cargando : DetalleUiState
    data class Encontrado(val recordatorio: Recordatorio) : DetalleUiState
    data object NoEncontrado : DetalleUiState
    data class Error(val mensaje: String) : DetalleUiState
}

/**
 * ViewModel del detalle. Recibe SOLO el identificador desde la navegación
 * (SavedStateHandle) y busca el registro completo en el repositorio.
 */
class DetalleViewModel(
    savedStateHandle: SavedStateHandle,
    private val repositorio: RecordatorioRepository
) : ViewModel() {

    private val id: Int = savedStateHandle.get<Int>(Rutas.ARG_ID) ?: -1

    private val _uiState = MutableStateFlow<DetalleUiState>(DetalleUiState.Cargando)
    val uiState: StateFlow<DetalleUiState> = _uiState.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        _uiState.update { DetalleUiState.Cargando }
        viewModelScope.launch {
            try {
                val recordatorio = repositorio.obtenerPorId(id)
                _uiState.update {
                    if (recordatorio != null) DetalleUiState.Encontrado(recordatorio) else DetalleUiState.NoEncontrado
                }
            } catch (e: IOException) {
                _uiState.update { DetalleUiState.Error("No se pudo cargar el recordatorio.") }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { DetalleViewModel(createSavedStateHandle(), AppContainer.repositorio) }
        }
    }
}

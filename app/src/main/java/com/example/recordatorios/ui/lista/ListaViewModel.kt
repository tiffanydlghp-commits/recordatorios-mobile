package com.example.recordatorios.ui.lista

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.recordatorios.data.AppContainer
import com.example.recordatorios.data.ControlSimulacion
import com.example.recordatorios.data.Escenario
import com.example.recordatorios.data.RecordatorioRepository
import com.example.recordatorios.domain.EstadoRecordatorio
import com.example.recordatorios.domain.Recordatorio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

/** CONSIGNA 3 — Estados posibles de la pantalla de listado (mutuamente excluyentes). */
sealed interface ListaUiState {
    data object Cargando : ListaUiState
    data object Vacio : ListaUiState
    data class Exito(
        val recordatorios: List<Recordatorio>,
        val filtro: EstadoRecordatorio? = null   // null = "Todos"
    ) : ListaUiState {
        val visibles: List<Recordatorio>
            get() = if (filtro == null) recordatorios
            else recordatorios.filter { it.estado::class == filtro::class }
    }
    data class Error(val mensaje: String) : ListaUiState
}

/**
 * CONSIGNAS 3 y 4 — ViewModel del listado.
 * - Estado interno con MutableStateFlow, expuesto como StateFlow de solo lectura.
 * - Depende de la interfaz RecordatorioRepository (no de una lista concreta).
 * - No guarda referencias a Activity, View ni componentes visuales.
 */
class ListaViewModel(
    private val repositorio: RecordatorioRepository,
    private val simulador: ControlSimulacion
) : ViewModel() {

    private val _uiState = MutableStateFlow<ListaUiState>(ListaUiState.Cargando)
    val uiState: StateFlow<ListaUiState> = _uiState.asStateFlow()

    /** Carga (o recarga) los recordatorios sin bloquear la interfaz. */
    fun cargar() {
        _uiState.update { ListaUiState.Cargando }
        viewModelScope.launch {
            try {
                val lista = repositorio.obtenerRecordatorios()
                _uiState.update { if (lista.isEmpty()) ListaUiState.Vacio else ListaUiState.Exito(lista) }
            } catch (e: IOException) {
                _uiState.update {
                    ListaUiState.Error("No pudimos cargar tus recordatorios. Revisa tu conexión e intenta nuevamente.")
                }
            }
        }
    }

    fun reintentar() = cargar()

    fun cambiarFiltro(filtro: EstadoRecordatorio?) {
        _uiState.update { estado ->
            if (estado is ListaUiState.Exito) estado.copy(filtro = filtro) else estado
        }
    }

    /** Mecanismo de simulación para comprobar los estados vacío y error. */
    fun simular(escenario: Escenario) {
        simulador.simular(escenario)
        if (escenario != Escenario.ERROR_GUARDADO) cargar()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { ListaViewModel(AppContainer.repositorio, AppContainer.simulador) }
        }
    }
}

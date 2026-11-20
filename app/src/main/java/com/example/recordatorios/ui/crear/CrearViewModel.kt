package com.example.recordatorios.ui.crear

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.recordatorios.data.AppContainer
import com.example.recordatorios.data.DatosSimulados
import com.example.recordatorios.data.RecordatorioRepository
import com.example.recordatorios.domain.Categoria
import com.example.recordatorios.domain.Prioridad
import com.example.recordatorios.validation.ErroresFormulario
import com.example.recordatorios.validation.validarFormulario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * CONSIGNA 3 — Estado inmutable del formulario de creación.
 * Contiene: valores de los campos, mensajes de validación, estado de envío,
 * resultado (éxito) y error general de guardado.
 */
data class CrearUiState(
    val titulo: String = "",
    val descripcion: String = "",
    val categoria: Categoria? = null,
    val prioridad: Prioridad? = null,
    val errores: ErroresFormulario = ErroresFormulario(),
    val tituloEditado: Boolean = false,         // para no mostrar errores antes de escribir
    val enviando: Boolean = false,
    val guardadoExitoso: Boolean = false,
    val errorGeneral: String? = null
) {
    /** El botón Guardar se habilita solo si el formulario es válido y no hay un envío en curso. */
    val puedeGuardar: Boolean
        get() = !errores.hayErrores && !enviando
}

/** CONSIGNAS 3 y 4 — ViewModel del formulario. Recibe eventos de la UI y aplica las reglas. */
class CrearViewModel(
    private val repositorio: RecordatorioRepository
) : ViewModel() {

    val categoriasDisponibles: List<Categoria> = DatosSimulados.categoriasUnicas.sortedBy { it.id }
    val prioridadesDisponibles: List<Prioridad> = listOf(Prioridad.Alta, Prioridad.Media, Prioridad.Baja)

    private val _uiState = MutableStateFlow(CrearUiState().validado())
    val uiState: StateFlow<CrearUiState> = _uiState.asStateFlow()

    fun onTituloChange(nuevo: String) =
        _uiState.update { it.copy(titulo = nuevo, tituloEditado = true, errorGeneral = null).validado() }

    fun onDescripcionChange(nueva: String) =
        _uiState.update { it.copy(descripcion = nueva, errorGeneral = null).validado() }

    fun onCategoriaChange(categoria: Categoria) =
        _uiState.update { it.copy(categoria = categoria, errorGeneral = null).validado() }

    fun onPrioridadChange(prioridad: Prioridad) =
        _uiState.update { it.copy(prioridad = prioridad, errorGeneral = null).validado() }

    fun guardar() {
        val actual = _uiState.value
        if (!actual.puedeGuardar) return
        val categoria = actual.categoria ?: return
        val prioridad = actual.prioridad ?: return

        _uiState.update { it.copy(enviando = true, errorGeneral = null) }
        viewModelScope.launch {
            try {
                repositorio.guardar(
                    titulo = actual.titulo,
                    descripcion = actual.descripcion.takeIf { it.isNotBlank() },
                    categoria = categoria,
                    prioridad = prioridad
                )
                _uiState.update { it.copy(enviando = false, guardadoExitoso = true) }
            } catch (e: IOException) {
                _uiState.update {
                    it.copy(enviando = false, errorGeneral = "No se pudo guardar el recordatorio. Intenta nuevamente.")
                }
            }
        }
    }

    /** Aplica las reglas de validación y devuelve una NUEVA instancia del estado. */
    private fun CrearUiState.validado(): CrearUiState = copy(
        errores = validarFormulario(
            titulo = titulo,
            descripcion = descripcion,
            categoria = categoria,
            prioridad = prioridad,
            categoriasPermitidas = categoriasDisponibles.toSet()
        )
    )

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { CrearViewModel(AppContainer.repositorio) }
        }
    }
}

package com.example.recordatorios.data

import com.example.recordatorios.domain.Categoria
import com.example.recordatorios.domain.Prioridad
import com.example.recordatorios.domain.Recordatorio
import com.example.recordatorios.validation.crearRecordatorio
import kotlinx.coroutines.delay
import java.io.IOException

/**
 * CONSIGNA 4 — Repositorio.
 * Los ViewModel dependen de esta INTERFAZ y no de una lista concreta.
 * Más adelante se puede reemplazar por Room o un servicio remoto sin tocar las pantallas.
 */
interface RecordatorioRepository {
    suspend fun obtenerRecordatorios(): List<Recordatorio>
    suspend fun obtenerPorId(id: Int): Recordatorio?
    suspend fun guardar(
        titulo: String,
        descripcion: String?,
        categoria: Categoria,
        prioridad: Prioridad
    ): Recordatorio
}

/** Escenarios para probar todos los estados de la interfaz sin base de datos ni servidor. */
enum class Escenario(val etiqueta: String) {
    DATOS("Datos normales"),
    VACIO("Lista vacía"),
    ERROR_CARGA("Error al cargar"),
    ERROR_GUARDADO("Error al guardar")
}

/** Permite activar un escenario de simulación (solo para pruebas). */
interface ControlSimulacion {
    fun simular(escenario: Escenario)
}

/** Implementación en memoria. Simula demoras de red con delay(). */
class RecordatorioRepositoryEnMemoria(
    private val datosIniciales: List<Recordatorio> = DatosSimulados.recordatorios,
    private val demoraMs: Long = 1200
) : RecordatorioRepository, ControlSimulacion {

    private val recordatorios = datosIniciales.toMutableList()
    private var fallarProximaCarga = false
    private var fallarProximoGuardado = false

    override fun simular(escenario: Escenario) {
        when (escenario) {
            Escenario.DATOS -> {
                recordatorios.clear()
                recordatorios.addAll(datosIniciales)
            }
            Escenario.VACIO -> recordatorios.clear()
            Escenario.ERROR_CARGA -> fallarProximaCarga = true
            Escenario.ERROR_GUARDADO -> fallarProximoGuardado = true
        }
    }

    override suspend fun obtenerRecordatorios(): List<Recordatorio> {
        delay(demoraMs)
        if (fallarProximaCarga) {
            fallarProximaCarga = false   // falla una vez; el reintento funciona
            throw IOException("No se pudo conectar con el servidor")
        }
        return recordatorios.toList()
    }

    override suspend fun obtenerPorId(id: Int): Recordatorio? {
        delay(demoraMs / 2)
        return recordatorios.firstOrNull { it.id == id }
    }

    override suspend fun guardar(
        titulo: String,
        descripcion: String?,
        categoria: Categoria,
        prioridad: Prioridad
    ): Recordatorio {
        delay(demoraMs)
        if (fallarProximoGuardado) {
            fallarProximoGuardado = false
            throw IOException("No se pudo guardar el recordatorio")
        }
        val nuevoId = (recordatorios.maxOfOrNull { it.id } ?: 0) + 1
        val nuevo = crearRecordatorio(
            id = nuevoId,
            titulo = titulo,
            categoria = categoria,
            prioridad = prioridad,
            descripcion = descripcion
        )
        recordatorios.add(0, nuevo)   // el nuevo aparece primero en la lista
        return nuevo
    }
}

/** Contenedor simple de dependencias: una sola instancia compartida por toda la app. */
object AppContainer {
    private val repositorioEnMemoria = RecordatorioRepositoryEnMemoria()
    val repositorio: RecordatorioRepository = repositorioEnMemoria
    val simulador: ControlSimulacion = repositorioEnMemoria
}

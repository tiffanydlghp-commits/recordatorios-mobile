package com.example.recordatorios.data

import com.example.recordatorios.domain.Categoria
import com.example.recordatorios.domain.EstadoRecordatorio
import com.example.recordatorios.domain.Prioridad
import com.example.recordatorios.domain.Recordatorio
import com.example.recordatorios.validation.crearRecordatorio

/**
 * CONSIGNA 3 — Datos simulados usando List, Set y Map.
 */
object DatosSimulados {

    // Categorías disponibles
    val TRABAJO = Categoria(1, "Trabajo")
    val ESTUDIOS = Categoria(2, "Estudios")
    val PERSONAL = Categoria(3, "Personal")
    val SALUD = Categoria(4, "Salud")
    val HOGAR = Categoria(5, "Hogar")

    /** LIST: mantiene el orden en que se muestran los recordatorios. Todos pasan por las validaciones. */
    val recordatorios: List<Recordatorio> = listOf(
        crearRecordatorio(
            id = 1, titulo = "Entregar informe mensual", categoria = TRABAJO,
            prioridad = Prioridad.Alta, estado = EstadoRecordatorio.EnProgreso,
            descripcion = "Enviar resultados a jefatura", fechaLimite = "20/11/2026"
        ),
        crearRecordatorio(
            id = 2, titulo = "Estudiar Kotlin: colecciones", categoria = ESTUDIOS,
            prioridad = Prioridad.Media, fechaLimite = "22/11/2026"
        ),
        crearRecordatorio(
            id = 3, titulo = "Pagar cuenta de la luz", categoria = HOGAR,
            prioridad = Prioridad.Alta, estado = EstadoRecordatorio.Vencido, fechaLimite = "15/11/2026"
        ),
        crearRecordatorio(
            id = 4, titulo = "Control médico anual", categoria = SALUD,
            prioridad = Prioridad.Media, ubicacion = "CESFAM", fechaLimite = "28/11/2026"
        ),
        crearRecordatorio(
            id = 5, titulo = "Comprar regalo de cumpleaños", categoria = PERSONAL,
            prioridad = Prioridad.Baja, estado = EstadoRecordatorio.Completado("10/11/2026")
        ),
        crearRecordatorio(
            id = 6, titulo = "Subir Actividad 1 a la plataforma", categoria = ESTUDIOS,
            prioridad = Prioridad.Alta, descripcion = "PDF con evidencias y enlace al repositorio"
        ),
        crearRecordatorio(
            id = 7, titulo = "Reunión de equipo", categoria = TRABAJO,
            prioridad = Prioridad.Media, estado = EstadoRecordatorio.Completado("12/11/2026")
        ),
        crearRecordatorio(
            id = 8, titulo = "Ordenar la bodega", categoria = HOGAR,
            prioridad = Prioridad.Baja
        )
    )

    /** SET: categorías sin repetir. Trabajo, Estudios y Hogar aparecen 2 veces en la lista, pero en el Set solo 1. */
    val categoriasUnicas: Set<Categoria> = recordatorios.map { it.categoria }.toSet()

    /** MAP: cada categoría asociada a una etiqueta (emoji) para mostrar en pantalla. */
    val etiquetaPorCategoria: Map<Categoria, String> = mapOf(
        TRABAJO to "💼",
        ESTUDIOS to "📚",
        PERSONAL to "🙂",
        SALUD to "🩺",
        HOGAR to "🏠"
    )

    /** MAP: recordatorios agrupados por categoría (groupBy devuelve un Map). */
    val recordatoriosPorCategoria: Map<Categoria, List<Recordatorio>> =
        recordatorios.groupBy { it.categoria }
}

// ---------- Filtros (lambdas sobre colecciones) ----------

/** Filtra por estado. Se compara la clase porque Completado lleva una fecha distinta en cada caso. */
fun List<Recordatorio>.filtrarPorEstado(estado: EstadoRecordatorio): List<Recordatorio> =
    filter { it.estado::class == estado::class }

/** Filtra por prioridad. */
fun List<Recordatorio>.filtrarPorPrioridad(prioridad: Prioridad): List<Recordatorio> =
    filter { it.prioridad == prioridad }

/** Recordatorios que aún no se completan, ordenados de mayor a menor prioridad. */
fun List<Recordatorio>.pendientesOrdenados(): List<Recordatorio> =
    filter { it.estado !is EstadoRecordatorio.Completado }
        .sortedByDescending { it.prioridad.nivel }

/** Conteo por estado, útil para el resumen de la pantalla (otro Map). */
fun List<Recordatorio>.conteoPorEstado(): Map<String, Int> =
    groupingBy { it.estado.etiqueta }.eachCount()

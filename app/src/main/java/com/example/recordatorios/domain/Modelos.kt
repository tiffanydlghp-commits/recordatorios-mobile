package com.example.recordatorios.domain

/**
 * CONSIGNA 2 — Modelo de datos de la app de recordatorios.
 *
 * - data class inmutables (solo `val`): Categoria y Recordatorio.
 * - sealed class: Prioridad, EstadoRecordatorio y ResultadoValidacion.
 * - Datos opcionales con tipos nulables (String?): descripcion, fechaLimite, ubicacion.
 */

/** Categoría de un recordatorio. Al ser data class, dos categorías con el mismo id y nombre son iguales (útil para el Set). */
data class Categoria(
    val id: Int,
    val nombre: String
)

/** Prioridad: conjunto cerrado de opciones. Cada una tiene un nivel para ordenar y una etiqueta para mostrar. */
sealed class Prioridad(val nivel: Int, val etiqueta: String) {
    data object Alta : Prioridad(nivel = 3, etiqueta = "Alta")
    data object Media : Prioridad(nivel = 2, etiqueta = "Media")
    data object Baja : Prioridad(nivel = 1, etiqueta = "Baja")
}

/** Estado del recordatorio. "Completado" guarda además la fecha en que se completó. */
sealed class EstadoRecordatorio(val etiqueta: String) {
    data object Pendiente : EstadoRecordatorio("Pendiente")
    data object EnProgreso : EstadoRecordatorio("En progreso")
    data class Completado(val fechaCompletado: String) : EstadoRecordatorio("Completado")
    data object Vencido : EstadoRecordatorio("Vencido")
}

/** Recordatorio inmutable. Para "modificarlo" se crea una copia con copy(...). */
data class Recordatorio(
    val id: Int,
    val titulo: String,
    val categoria: Categoria,
    val prioridad: Prioridad,
    val estado: EstadoRecordatorio = EstadoRecordatorio.Pendiente,
    val descripcion: String? = null,   // opcional
    val fechaLimite: String? = null,   // opcional, formato dd/MM/yyyy
    val ubicacion: String? = null      // opcional (se usará en módulos posteriores)
)

/** Resultado de validar un recordatorio: o es válido, o trae la lista de errores. */
sealed class ResultadoValidacion {
    data object Valido : ResultadoValidacion()
    data class Invalido(val errores: List<String>) : ResultadoValidacion()
}

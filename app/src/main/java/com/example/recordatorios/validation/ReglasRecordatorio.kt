package com.example.recordatorios.validation

import com.example.recordatorios.domain.Categoria
import com.example.recordatorios.domain.EstadoRecordatorio
import com.example.recordatorios.domain.Prioridad
import com.example.recordatorios.domain.Recordatorio
import com.example.recordatorios.domain.ResultadoValidacion

/**
 * Reglas de validación (Actividad 1) + reglas del formulario (Actividad 2).
 */

const val LARGO_MINIMO_TITULO = 4
const val LARGO_MAXIMO_TITULO = 60
const val LARGO_MAXIMO_DESCRIPCION = 200

/** El título debe tener entre 4 y 60 caracteres sin contar espacios al inicio/fin. */
fun String.esTituloValido(): Boolean = trim().length in LARGO_MINIMO_TITULO..LARGO_MAXIMO_TITULO

/** Fecha con formato dd/MM/yyyy. Usa toIntOrNull() para no cerrar la app con texto inválido. */
fun String.esFechaValida(): Boolean {
    val partes = trim().split("/")
    if (partes.size != 3) return false
    val dia = partes[0].toIntOrNull() ?: return false
    val mes = partes[1].toIntOrNull() ?: return false
    val anio = partes[2].toIntOrNull() ?: return false
    return dia in 1..31 && mes in 1..12 && anio in 2000..2100
}

/** Valida un recordatorio completo y devuelve todos los errores encontrados. */
fun validarRecordatorio(recordatorio: Recordatorio): ResultadoValidacion {
    val errores = mutableListOf<String>()

    if (recordatorio.id <= 0) errores += "El id debe ser mayor que 0"
    if (!recordatorio.titulo.esTituloValido()) {
        errores += "El título debe tener entre $LARGO_MINIMO_TITULO y $LARGO_MAXIMO_TITULO caracteres"
    }
    if (recordatorio.categoria.nombre.isBlank()) errores += "La categoría no puede estar vacía"
    recordatorio.descripcion?.let {
        if (it.length > LARGO_MAXIMO_DESCRIPCION) errores += "La descripción supera $LARGO_MAXIMO_DESCRIPCION caracteres"
    }
    recordatorio.fechaLimite?.let { fecha ->
        if (!fecha.esFechaValida()) errores += "La fecha límite debe tener formato dd/MM/yyyy"
    }
    if (recordatorio.estado is EstadoRecordatorio.Vencido && recordatorio.fechaLimite == null) {
        errores += "Un recordatorio vencido debe tener fecha límite"
    }

    return if (errores.isEmpty()) ResultadoValidacion.Valido else ResultadoValidacion.Invalido(errores)
}

/** Crea un recordatorio solo si es válido; si no, require() lanza IllegalArgumentException. */
fun crearRecordatorio(
    id: Int,
    titulo: String,
    categoria: Categoria,
    prioridad: Prioridad,
    estado: EstadoRecordatorio = EstadoRecordatorio.Pendiente,
    descripcion: String? = null,
    fechaLimite: String? = null,
    ubicacion: String? = null
): Recordatorio {
    val recordatorio = Recordatorio(
        id = id,
        titulo = titulo.trim(),
        categoria = categoria,
        prioridad = prioridad,
        estado = estado,
        descripcion = descripcion?.trim()?.takeIf { it.isNotEmpty() },
        fechaLimite = fechaLimite,
        ubicacion = ubicacion?.trim()?.takeIf { it.isNotEmpty() }
    )
    val resultado = validarRecordatorio(recordatorio)
    require(resultado is ResultadoValidacion.Valido) {
        "Recordatorio inválido: ${(resultado as ResultadoValidacion.Invalido).errores.joinToString()}"
    }
    return recordatorio
}

/** Versión segura: devuelve null en vez de lanzar excepción. */
fun crearRecordatorioOrNull(
    id: Int,
    titulo: String,
    categoria: Categoria,
    prioridad: Prioridad
): Recordatorio? = try {
    crearRecordatorio(id, titulo, categoria, prioridad)
} catch (e: IllegalArgumentException) {
    null
}

// ---------------- Actividad 2: validación del formulario ----------------

/** Mensajes de error de cada campo del formulario. null = campo correcto. */
data class ErroresFormulario(
    val titulo: String? = null,
    val descripcion: String? = null,
    val categoria: String? = null,
    val prioridad: String? = null
) {
    val hayErrores: Boolean
        get() = titulo != null || descripcion != null || categoria != null || prioridad != null
}

/**
 * Reglas del formulario de creación:
 * - título: al menos 4 caracteres después de trim()
 * - descripción: máximo 200 caracteres
 * - categoría: debe pertenecer a las opciones definidas
 * - prioridad: debe estar seleccionada
 */
fun validarFormulario(
    titulo: String,
    descripcion: String,
    categoria: Categoria?,
    prioridad: Prioridad?,
    categoriasPermitidas: Set<Categoria>
): ErroresFormulario = ErroresFormulario(
    titulo = when {
        titulo.isBlank() -> "El título es obligatorio"
        titulo.trim().length < LARGO_MINIMO_TITULO -> "El título debe tener al menos $LARGO_MINIMO_TITULO caracteres"
        titulo.trim().length > LARGO_MAXIMO_TITULO -> "El título no puede superar $LARGO_MAXIMO_TITULO caracteres"
        else -> null
    },
    descripcion = if (descripcion.length > LARGO_MAXIMO_DESCRIPCION) {
        "La descripción no puede superar $LARGO_MAXIMO_DESCRIPCION caracteres"
    } else null,
    categoria = when {
        categoria == null -> "Selecciona una categoría"
        categoria !in categoriasPermitidas -> "La categoría no es válida"
        else -> null
    },
    prioridad = if (prioridad == null) "Selecciona una prioridad" else null
)

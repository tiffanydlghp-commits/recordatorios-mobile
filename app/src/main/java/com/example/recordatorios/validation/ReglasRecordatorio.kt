package com.example.recordatorios.validation

import com.example.recordatorios.domain.Categoria
import com.example.recordatorios.domain.EstadoRecordatorio
import com.example.recordatorios.domain.Prioridad
import com.example.recordatorios.domain.Recordatorio
import com.example.recordatorios.domain.ResultadoValidacion

/**
 * Reglas de validación para evitar estados inválidos.
 * Se usan funciones de extensión, null safety, toIntOrNull() y require().
 */

const val LARGO_MINIMO_TITULO = 4
const val LARGO_MAXIMO_TITULO = 60

/** Función de extensión: el título debe tener entre 4 y 60 caracteres sin contar espacios al inicio/fin. */
fun String.esTituloValido(): Boolean = trim().length in LARGO_MINIMO_TITULO..LARGO_MAXIMO_TITULO

/** Fecha con formato dd/MM/yyyy y valores razonables. Usa toIntOrNull() para no cerrar la app con texto inválido. */
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

    // Dato opcional: solo se valida si viene informado (?.let)
    recordatorio.fechaLimite?.let { fecha ->
        if (!fecha.esFechaValida()) errores += "La fecha límite debe tener formato dd/MM/yyyy"
    }

    // Regla de negocio: un recordatorio vencido debe tener fecha límite
    if (recordatorio.estado is EstadoRecordatorio.Vencido && recordatorio.fechaLimite == null) {
        errores += "Un recordatorio vencido debe tener fecha límite"
    }

    return if (errores.isEmpty()) ResultadoValidacion.Valido else ResultadoValidacion.Invalido(errores)
}

/**
 * Crea un recordatorio solo si es válido. Si no lo es, require() lanza IllegalArgumentException
 * con el detalle, evitando que un estado inválido llegue a la interfaz.
 */
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
        // Si la descripción viene vacía o con solo espacios, se guarda como null
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

/** Versión segura: en vez de lanzar excepción devuelve null (para formularios de los próximos módulos). */
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

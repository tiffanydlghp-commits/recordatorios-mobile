package com.example.recordatorios.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.recordatorios.data.DatosSimulados
import com.example.recordatorios.domain.Categoria
import com.example.recordatorios.domain.EstadoRecordatorio
import com.example.recordatorios.domain.Prioridad

/** CONSIGNA 3 — MAP que asocia cada categoría con un color. */
val colorPorCategoria: Map<Categoria, Color> = mapOf(
    DatosSimulados.TRABAJO to Color(0xFF1E88E5),   // azul
    DatosSimulados.ESTUDIOS to Color(0xFF8E24AA),  // morado
    DatosSimulados.PERSONAL to Color(0xFF43A047),  // verde
    DatosSimulados.SALUD to Color(0xFFE53935),     // rojo
    DatosSimulados.HOGAR to Color(0xFFFB8C00)      // naranjo
)

/** Color por defecto si una categoría no está en el Map (operador Elvis). */
fun Categoria.color(): Color = colorPorCategoria[this] ?: Color.Gray

/** Color según la prioridad (when exhaustivo gracias a la sealed class: no necesita else). */
fun Prioridad.color(): Color = when (this) {
    Prioridad.Alta -> Color(0xFFD32F2F)
    Prioridad.Media -> Color(0xFFF9A825)
    Prioridad.Baja -> Color(0xFF388E3C)
}

/** Color según el estado. */
fun EstadoRecordatorio.color(): Color = when (this) {
    EstadoRecordatorio.Pendiente -> Color(0xFF757575)
    EstadoRecordatorio.EnProgreso -> Color(0xFF1976D2)
    is EstadoRecordatorio.Completado -> Color(0xFF2E7D32)
    EstadoRecordatorio.Vencido -> Color(0xFFC62828)
}

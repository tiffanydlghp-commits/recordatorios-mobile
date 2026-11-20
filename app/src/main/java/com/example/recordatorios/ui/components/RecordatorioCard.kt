package com.example.recordatorios.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.recordatorios.data.DatosSimulados
import com.example.recordatorios.domain.Categoria
import com.example.recordatorios.domain.Recordatorio
import com.example.recordatorios.ui.theme.color

/**
 * CONSIGNA 2 — Componente reutilizable para representar un recordatorio.
 * Comunica la selección mediante la lambda onClick (no recibe el NavController).
 */
@Composable
fun RecordatorioCard(
    recordatorio: Recordatorio,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colorCategoria = recordatorio.categoria.color()

    Card(
        onClick = { onClick(recordatorio.id) },
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(colorCategoria)
            )
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = recordatorio.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                recordatorio.descripcion?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(Modifier.height(8.dp))
                EtiquetaCategoria(categoria = recordatorio.categoria)
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Etiqueta(
                        texto = "Prioridad: ${recordatorio.prioridad.etiqueta}",
                        color = recordatorio.prioridad.color()
                    )
                    Etiqueta(texto = recordatorio.estado.etiqueta, color = recordatorio.estado.color())
                }
            }
        }
    }
}

/** Nombre de la categoría con su emoji y color (ambos tomados de los Map). */
@Composable
fun EtiquetaCategoria(categoria: Categoria, modifier: Modifier = Modifier) {
    val emoji = DatosSimulados.etiquetaPorCategoria[categoria] ?: "📌"
    Text(
        text = "$emoji ${categoria.nombre}",
        style = MaterialTheme.typography.bodyMedium,
        color = categoria.color(),
        fontWeight = FontWeight.Medium,
        modifier = modifier
    )
}

/** Pastilla con un punto de color y texto (el texto siempre acompaña al color). */
@Composable
fun Etiqueta(texto: String, color: Color, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Spacer(Modifier.width(6.dp))
        Text(texto, style = MaterialTheme.typography.labelMedium, color = color)
    }
}

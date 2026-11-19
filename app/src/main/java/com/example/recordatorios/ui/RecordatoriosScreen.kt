package com.example.recordatorios.ui
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.recordatorios.R
import com.example.recordatorios.data.DatosSimulados
import com.example.recordatorios.data.filtrarPorEstado
import com.example.recordatorios.domain.EstadoRecordatorio
import com.example.recordatorios.domain.Recordatorio
import com.example.recordatorios.ui.theme.RecordatoriosTheme
import com.example.recordatorios.ui.theme.color

/**
 * CONSIGNA 4 — Pantalla inicial con Jetpack Compose y Material 3.
 * Muestra la lista de recordatorios simulados con título, categoría, prioridad y estado.
 */

/** Opciones del filtro: null = "Todos". */
private val opcionesFiltro: List<EstadoRecordatorio?> = listOf(
    null,
    EstadoRecordatorio.Pendiente,
    EstadoRecordatorio.EnProgreso,
    EstadoRecordatorio.Completado(""),
    EstadoRecordatorio.Vencido
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordatoriosScreen(
    recordatorios: List<Recordatorio>,
    modifier: Modifier = Modifier
) {
    // Índice del filtro seleccionado. rememberSaveable lo conserva al girar el teléfono.
    var filtroSeleccionado by rememberSaveable { mutableStateOf(0) }
    val estadoFiltro = opcionesFiltro[filtroSeleccionado]

    // Dato derivado: lista filtrada según el estado elegido
    val visibles = if (estadoFiltro == null) recordatorios else recordatorios.filtrarPorEstado(estadoFiltro)

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.titulo_pantalla)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)   // evita que la barra superior tape el contenido
                .fillMaxSize()
        ) {
            ResumenRecordatorios(recordatorios = recordatorios)

            // Filtros por estado
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(opcionesFiltro.size) { indice ->
                    val opcion = opcionesFiltro[indice]
                    FilterChip(
                        selected = filtroSeleccionado == indice,
                        onClick = { filtroSeleccionado = indice },
                        label = { Text(opcion?.etiqueta ?: stringResource(R.string.filtro_todos)) }
                    )
                }
            }

            if (visibles.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.sin_recordatorios),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(visibles, key = { it.id }) { recordatorio ->
                        RecordatorioCard(recordatorio = recordatorio)
                    }
                }
            }
        }
    }
}

/** Fila superior con totales: usa el Set de categorías y el conteo por estado. */
@Composable
private fun ResumenRecordatorios(recordatorios: List<Recordatorio>) {
    val total = recordatorios.size
    val pendientes = recordatorios.count { it.estado !is EstadoRecordatorio.Completado }
    val categorias = DatosSimulados.categoriasUnicas.size

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DatoResumen(stringResource(R.string.resumen_total), total.toString(), Modifier.weight(1f))
        DatoResumen(stringResource(R.string.resumen_pendientes), pendientes.toString(), Modifier.weight(1f))
        DatoResumen(stringResource(R.string.resumen_categorias), categorias.toString(), Modifier.weight(1f))
    }
}

@Composable
private fun DatoResumen(etiqueta: String, valor: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(valor, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(etiqueta, style = MaterialTheme.typography.labelMedium)
        }
    }
}

/** Tarjeta de un recordatorio: título, categoría (color y etiqueta desde los Map), prioridad y estado. */
@Composable
fun RecordatorioCard(recordatorio: Recordatorio, modifier: Modifier = Modifier) {
    val colorCategoria = recordatorio.categoria.color()
    val emoji = DatosSimulados.etiquetaPorCategoria[recordatorio.categoria] ?: "📌"

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            // Franja lateral con el color de la categoría
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

                // Dato opcional: solo se muestra si existe
                recordatorio.descripcion?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "$emoji ${recordatorio.categoria.nombre}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorCategoria,
                    fontWeight = FontWeight.Medium
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Etiqueta(
                        texto = "${stringResource(R.string.prioridad)}: ${recordatorio.prioridad.etiqueta}",
                        color = recordatorio.prioridad.color()
                    )
                    Etiqueta(texto = recordatorio.estado.etiqueta, color = recordatorio.estado.color())
                }

                Text(
                    text = recordatorio.fechaLimite?.let { "${stringResource(R.string.fecha_limite)}: $it" }
                        ?: stringResource(R.string.sin_fecha),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }
    }
}

/** Pastilla de color con un punto y texto. */
@Composable
private fun Etiqueta(texto: String, color: Color) {
    Row(
        modifier = Modifier
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RecordatoriosScreenPreview() {
    RecordatoriosTheme {
        RecordatoriosScreen(recordatorios = DatosSimulados.recordatorios)
    }
}

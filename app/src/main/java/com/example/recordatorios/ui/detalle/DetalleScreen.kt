package com.example.recordatorios.ui.detalle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recordatorios.R
import com.example.recordatorios.data.DatosSimulados
import com.example.recordatorios.domain.EstadoRecordatorio
import com.example.recordatorios.domain.Recordatorio
import com.example.recordatorios.ui.components.CargandoContenido
import com.example.recordatorios.ui.components.ErrorContenido
import com.example.recordatorios.ui.components.Etiqueta
import com.example.recordatorios.ui.components.EtiquetaCategoria
import com.example.recordatorios.ui.components.VacioContenido
import com.example.recordatorios.ui.theme.RecordatoriosTheme
import com.example.recordatorios.ui.theme.color

/** CONSIGNA 1 — Pantalla de DETALLE. Recibe el registro buscado por su id. */
@Composable
fun DetalleScreen(
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetalleViewModel = viewModel(factory = DetalleViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    DetalleContenido(
        uiState = uiState,
        onVolver = onVolver,
        onReintentar = viewModel::cargar,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleContenido(
    uiState: DetalleUiState,
    onVolver: () -> Unit,
    onReintentar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.detalle)) },
                navigationIcon = { TextButton(onClick = onVolver) { Text("←") } },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        val contenidoModifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()

        when (uiState) {
            DetalleUiState.Cargando -> CargandoContenido(
                mensaje = stringResource(R.string.cargando_detalle),
                modifier = contenidoModifier
            )
            DetalleUiState.NoEncontrado -> VacioContenido(
                mensaje = stringResource(R.string.no_encontrado),
                textoAccion = stringResource(R.string.volver),
                onAccion = onVolver,
                modifier = contenidoModifier
            )
            is DetalleUiState.Error -> ErrorContenido(
                mensaje = uiState.mensaje,
                onReintentar = onReintentar,
                modifier = contenidoModifier
            )
            is DetalleUiState.Encontrado -> DetalleRecordatorio(
                recordatorio = uiState.recordatorio,
                onVolver = onVolver,
                modifier = contenidoModifier
            )
        }
    }
}

@Composable
private fun DetalleRecordatorio(
    recordatorio: Recordatorio,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = recordatorio.titulo,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        EtiquetaCategoria(categoria = recordatorio.categoria)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Etiqueta(
                texto = "Prioridad: ${recordatorio.prioridad.etiqueta}",
                color = recordatorio.prioridad.color()
            )
            Etiqueta(texto = recordatorio.estado.etiqueta, color = recordatorio.estado.color())
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                FilaDato("Descripción", recordatorio.descripcion ?: "Sin descripción")
                HorizontalDivider()
                FilaDato("Fecha límite", recordatorio.fechaLimite ?: "Sin fecha límite")
                HorizontalDivider()
                FilaDato("Ubicación", recordatorio.ubicacion ?: "Sin ubicación")
                (recordatorio.estado as? EstadoRecordatorio.Completado)?.let {
                    HorizontalDivider()
                    FilaDato("Completado el", it.fechaCompletado)
                }
                HorizontalDivider()
                FilaDato("Identificador", "#${recordatorio.id}")
            }
        }

        Button(onClick = onVolver, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.volver))
        }
    }
}

@Composable
private fun FilaDato(etiqueta: String, valor: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(etiqueta, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(valor, style = MaterialTheme.typography.bodyLarge)
    }
}

@Preview(showBackground = true, name = "Detalle")
@Composable
private fun DetallePreview() {
    RecordatoriosTheme {
        DetalleContenido(
            uiState = DetalleUiState.Encontrado(DatosSimulados.recordatorios.first()),
            onVolver = {},
            onReintentar = {}
        )
    }
}

package com.example.recordatorios.ui.lista

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recordatorios.R
import com.example.recordatorios.data.DatosSimulados
import com.example.recordatorios.data.Escenario
import com.example.recordatorios.domain.EstadoRecordatorio
import com.example.recordatorios.ui.components.CargandoContenido
import com.example.recordatorios.ui.components.ErrorContenido
import com.example.recordatorios.ui.components.RecordatorioCard
import com.example.recordatorios.ui.components.VacioContenido
import com.example.recordatorios.ui.theme.RecordatoriosTheme

/** Opciones del filtro: null = "Todos". */
private val opcionesFiltro: List<EstadoRecordatorio?> = listOf(
    null,
    EstadoRecordatorio.Pendiente,
    EstadoRecordatorio.EnProgreso,
    EstadoRecordatorio.Completado(fechaCompletado = ""),
    EstadoRecordatorio.Vencido
)

/**
 * CONSIGNA 1 — Pantalla de LISTADO (versión conectada al ViewModel).
 * Observa el StateFlow con collectAsStateWithLifecycle (respeta el ciclo de vida).
 */
@Composable
fun ListaScreen(
    onCrearRecordatorio: () -> Unit,
    onRecordatorioClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ListaViewModel = viewModel(factory = ListaViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Se carga cada vez que se entra al listado (por ejemplo, al volver después de guardar)
    LaunchedEffect(Unit) { viewModel.cargar() }

    ListaContenido(
        uiState = uiState,
        onCrearRecordatorio = onCrearRecordatorio,
        onRecordatorioClick = onRecordatorioClick,
        onReintentar = viewModel::reintentar,
        onCambiarFiltro = viewModel::cambiarFiltro,
        onSimular = viewModel::simular,
        modifier = modifier
    )
}

/** Versión "sin estado" de la pantalla: solo dibuja el UiState y avisa eventos por lambdas. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaContenido(
    uiState: ListaUiState,
    onCrearRecordatorio: () -> Unit,
    onRecordatorioClick: (Int) -> Unit,
    onReintentar: () -> Unit,
    onCambiarFiltro: (EstadoRecordatorio?) -> Unit,
    onSimular: (Escenario) -> Unit,
    modifier: Modifier = Modifier
) {
    var menuAbierto by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.titulo_pantalla)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    // Mecanismo de simulación para probar los estados vacío y error
                    Box {
                        TextButton(onClick = { menuAbierto = true }) { Text("Simular ▾") }
                        DropdownMenu(expanded = menuAbierto, onDismissRequest = { menuAbierto = false }) {
                            Escenario.entries.forEach { escenario ->
                                DropdownMenuItem(
                                    text = { Text(escenario.etiqueta) },
                                    onClick = {
                                        menuAbierto = false
                                        onSimular(escenario)
                                    }
                                )
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (uiState !is ListaUiState.Cargando) {
                ExtendedFloatingActionButton(onClick = onCrearRecordatorio) {
                    Text("+  " + stringResource(R.string.nuevo_recordatorio))
                }
            }
        }
    ) { innerPadding ->
        val contenidoModifier = Modifier
            .padding(innerPadding)   // evita que la barra superior tape el contenido
            .fillMaxSize()

        // when exhaustivo: nunca se muestran carga, datos y error al mismo tiempo
        when (uiState) {
            ListaUiState.Cargando -> CargandoContenido(
                mensaje = stringResource(R.string.cargando_recordatorios),
                modifier = contenidoModifier
            )
            ListaUiState.Vacio -> VacioContenido(
                mensaje = stringResource(R.string.sin_recordatorios_aun),
                textoAccion = stringResource(R.string.crear_recordatorio),
                onAccion = onCrearRecordatorio,
                modifier = contenidoModifier
            )
            is ListaUiState.Error -> ErrorContenido(
                mensaje = uiState.mensaje,
                onReintentar = onReintentar,
                modifier = contenidoModifier
            )
            is ListaUiState.Exito -> ListaConDatos(
                estado = uiState,
                onRecordatorioClick = onRecordatorioClick,
                onCambiarFiltro = onCambiarFiltro,
                modifier = contenidoModifier
            )
        }
    }
}

@Composable
private fun ListaConDatos(
    estado: ListaUiState.Exito,
    onRecordatorioClick: (Int) -> Unit,
    onCambiarFiltro: (EstadoRecordatorio?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Resumen(estado = estado)

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(opcionesFiltro.size) { indice ->
                val opcion = opcionesFiltro[indice]
                val seleccionado = opcion?.let { it::class } == estado.filtro?.let { it::class }
                FilterChip(
                    selected = seleccionado,
                    onClick = { onCambiarFiltro(opcion) },
                    label = { Text(opcion?.etiqueta ?: stringResource(R.string.filtro_todos)) }
                )
            }
        }

        if (estado.visibles.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.sin_recordatorios), style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                // espacio extra abajo para que el botón flotante no tape la última tarjeta
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(estado.visibles, key = { it.id }) { recordatorio ->
                    RecordatorioCard(recordatorio = recordatorio, onClick = onRecordatorioClick)
                }
            }
        }
    }
}

@Composable
private fun Resumen(estado: ListaUiState.Exito, modifier: Modifier = Modifier) {
    val lista = estado.recordatorios
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DatoResumen(stringResource(R.string.resumen_total), lista.size.toString(), Modifier.weight(1f))
        DatoResumen(
            stringResource(R.string.resumen_pendientes),
            lista.count { it.estado !is EstadoRecordatorio.Completado }.toString(),
            Modifier.weight(1f)
        )
        DatoResumen(
            stringResource(R.string.resumen_categorias),
            lista.map { it.categoria }.toSet().size.toString(),
            Modifier.weight(1f)
        )
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

// ---------- Previews de cada estado ----------

@Preview(showBackground = true, name = "Lista con datos")
@Composable
private fun ListaExitoPreview() {
    RecordatoriosTheme {
        ListaContenido(
            uiState = ListaUiState.Exito(DatosSimulados.recordatorios),
            onCrearRecordatorio = {}, onRecordatorioClick = {}, onReintentar = {},
            onCambiarFiltro = {}, onSimular = {}
        )
    }
}

@Preview(showBackground = true, name = "Lista vacía")
@Composable
private fun ListaVaciaPreview() {
    RecordatoriosTheme {
        ListaContenido(
            uiState = ListaUiState.Vacio,
            onCrearRecordatorio = {}, onRecordatorioClick = {}, onReintentar = {},
            onCambiarFiltro = {}, onSimular = {}
        )
    }
}

@Preview(showBackground = true, name = "Lista con error")
@Composable
private fun ListaErrorPreview() {
    RecordatoriosTheme {
        ListaContenido(
            uiState = ListaUiState.Error("No pudimos cargar tus recordatorios."),
            onCrearRecordatorio = {}, onRecordatorioClick = {}, onReintentar = {},
            onCambiarFiltro = {}, onSimular = {}
        )
    }
}

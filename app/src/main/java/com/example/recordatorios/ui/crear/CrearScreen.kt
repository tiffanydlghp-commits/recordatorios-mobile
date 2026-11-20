package com.example.recordatorios.ui.crear

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recordatorios.R
import com.example.recordatorios.data.DatosSimulados
import com.example.recordatorios.domain.Categoria
import com.example.recordatorios.domain.Prioridad
import com.example.recordatorios.ui.components.CampoTexto
import com.example.recordatorios.ui.components.SelectorCategoria
import com.example.recordatorios.ui.components.SelectorPrioridad
import com.example.recordatorios.ui.theme.RecordatoriosTheme
import com.example.recordatorios.validation.LARGO_MAXIMO_DESCRIPCION

/**
 * CONSIGNAS 1 y 2 — Pantalla de CREACIÓN con formulario validado.
 * La pantalla no modifica el estado: solo envía eventos al ViewModel.
 */
@Composable
fun CrearScreen(
    onGuardado: () -> Unit,
    onCancelar: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CrearViewModel = viewModel(factory = CrearViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Cuando el guardado termina con éxito, se vuelve al listado
    LaunchedEffect(uiState.guardadoExitoso) {
        if (uiState.guardadoExitoso) onGuardado()
    }

    CrearContenido(
        uiState = uiState,
        categorias = viewModel.categoriasDisponibles,
        prioridades = viewModel.prioridadesDisponibles,
        onTituloChange = viewModel::onTituloChange,
        onDescripcionChange = viewModel::onDescripcionChange,
        onCategoriaChange = viewModel::onCategoriaChange,
        onPrioridadChange = viewModel::onPrioridadChange,
        onGuardar = viewModel::guardar,
        onCancelar = onCancelar,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearContenido(
    uiState: CrearUiState,
    categorias: List<Categoria>,
    prioridades: List<Prioridad>,
    onTituloChange: (String) -> Unit,
    onDescripcionChange: (String) -> Unit,
    onCategoriaChange: (Categoria) -> Unit,
    onPrioridadChange: (Prioridad) -> Unit,
    onGuardar: () -> Unit,
    onCancelar: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Los errores se muestran después de que la persona empieza a completar el formulario
    val mostrarErrores = uiState.tituloEditado || uiState.categoria != null || uiState.prioridad != null

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nuevo_recordatorio)) },
                navigationIcon = {
                    TextButton(onClick = onCancelar, enabled = !uiState.enviando) { Text("✕") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CampoTexto(
                valor = uiState.titulo,
                onValorChange = onTituloChange,
                etiqueta = stringResource(R.string.campo_titulo),
                mensajeError = if (uiState.tituloEditado) uiState.errores.titulo else null,
                ayuda = stringResource(R.string.ayuda_titulo)
            )

            CampoTexto(
                valor = uiState.descripcion,
                onValorChange = onDescripcionChange,
                etiqueta = stringResource(R.string.campo_descripcion),
                mensajeError = uiState.errores.descripcion,
                ayuda = "${uiState.descripcion.length}/$LARGO_MAXIMO_DESCRIPCION",
                singleLine = false,
                minLines = 3
            )

            SelectorCategoria(
                opciones = categorias,
                seleccionada = uiState.categoria,
                onSeleccionar = onCategoriaChange,
                mensajeError = if (mostrarErrores) uiState.errores.categoria else null
            )

            SelectorPrioridad(
                opciones = prioridades,
                seleccionada = uiState.prioridad,
                onSeleccionar = onPrioridadChange,
                mensajeError = if (mostrarErrores) uiState.errores.prioridad else null
            )

            // Error general de guardado (texto + ícono, no solo color)
            uiState.errorGeneral?.let { mensaje ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { liveRegion = LiveRegionMode.Polite }
                ) {
                    Text(
                        text = "⚠ $mensaje",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(Modifier.size(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onCancelar,
                    enabled = !uiState.enviando,
                    modifier = Modifier.weight(1f)
                ) { Text(stringResource(R.string.cancelar)) }

                Button(
                    onClick = onGuardar,
                    enabled = uiState.puedeGuardar,   // deshabilitado si es inválido o si se está enviando
                    modifier = Modifier.weight(1f)
                ) {
                    if (uiState.enviando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.guardando))
                    } else {
                        Text(stringResource(R.string.guardar))
                    }
                }
            }

            if (!uiState.puedeGuardar && !uiState.enviando) {
                Text(
                    text = stringResource(R.string.completa_campos),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Formulario con errores")
@Composable
private fun CrearPreview() {
    RecordatoriosTheme {
        CrearContenido(
            uiState = CrearUiState(
                titulo = "ab",
                tituloEditado = true,
                errores = com.example.recordatorios.validation.ErroresFormulario(
                    titulo = "El título debe tener al menos 4 caracteres",
                    prioridad = "Selecciona una prioridad"
                ),
                categoria = DatosSimulados.ESTUDIOS
            ),
            categorias = DatosSimulados.categoriasUnicas.toList(),
            prioridades = listOf(Prioridad.Alta, Prioridad.Media, Prioridad.Baja),
            onTituloChange = {}, onDescripcionChange = {}, onCategoriaChange = {},
            onPrioridadChange = {}, onGuardar = {}, onCancelar = {}
        )
    }
}

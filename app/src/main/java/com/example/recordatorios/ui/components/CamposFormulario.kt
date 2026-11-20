package com.example.recordatorios.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.recordatorios.domain.Categoria
import com.example.recordatorios.domain.Prioridad

/**
 * CONSIGNA 2 — Componentes CONTROLADOS del formulario:
 * reciben el valor actual y avisan los cambios por lambdas; no guardan estado propio.
 */

/** Campo de texto con mensaje de error textual, isError y propiedad semántica de error. */
@Composable
fun CampoTexto(
    valor: String,
    onValorChange: (String) -> Unit,
    etiqueta: String,
    mensajeError: String?,
    modifier: Modifier = Modifier,
    ayuda: String? = null,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorChange,
        label = { Text(etiqueta) },
        isError = mensajeError != null,
        singleLine = singleLine,
        minLines = minLines,
        supportingText = {
            when {
                mensajeError != null -> Text("⚠ $mensajeError")   // ícono + texto: el color no es el único indicador
                ayuda != null -> Text(ayuda)
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .semantics { if (mensajeError != null) error(mensajeError) }
    )
}

/** Selector de categoría (solo permite las opciones definidas). */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SelectorCategoria(
    opciones: List<Categoria>,
    seleccionada: Categoria?,
    onSeleccionar: (Categoria) -> Unit,
    mensajeError: String?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text("Categoría *", style = MaterialTheme.typography.titleSmall)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            opciones.forEach { categoria ->
                FilterChip(
                    selected = categoria == seleccionada,
                    onClick = { onSeleccionar(categoria) },
                    label = { Text(categoria.nombre) }
                )
            }
        }
        MensajeCampo(mensajeError = mensajeError)
    }
}

/** Selector de prioridad. */
@Composable
fun SelectorPrioridad(
    opciones: List<Prioridad>,
    seleccionada: Prioridad?,
    onSeleccionar: (Prioridad) -> Unit,
    mensajeError: String?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text("Prioridad *", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            opciones.forEach { prioridad ->
                FilterChip(
                    selected = prioridad == seleccionada,
                    onClick = { onSeleccionar(prioridad) },
                    label = { Text(prioridad.etiqueta) }
                )
            }
        }
        MensajeCampo(mensajeError = mensajeError)
    }
}

@Composable
private fun MensajeCampo(mensajeError: String?) {
    if (mensajeError != null) {
        Text(
            text = "⚠ $mensajeError",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.semantics { error(mensajeError) }
        )
    }
}

package com.example.recordatorios.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * CONSIGNA 2 — Componentes para comunicar carga, vacío o error.
 * Todos comparten la misma estructura centrada y reciben un modifier por defecto.
 */

@Composable
fun CargandoContenido(
    mensaje: String,
    modifier: Modifier = Modifier
) {
    ContenedorEstado(modifier = modifier) {
        CircularProgressIndicator()
        Spacer(Modifier.height(16.dp))
        Text(mensaje, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun VacioContenido(
    mensaje: String,
    textoAccion: String,
    onAccion: () -> Unit,
    modifier: Modifier = Modifier
) {
    ContenedorEstado(modifier = modifier) {
        Text("📭", style = MaterialTheme.typography.displayMedium)
        Spacer(Modifier.height(12.dp))
        Text(mensaje, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onAccion) { Text(textoAccion) }
    }
}

@Composable
fun ErrorContenido(
    mensaje: String,
    onReintentar: () -> Unit,
    modifier: Modifier = Modifier
) {
    ContenedorEstado(modifier = modifier) {
        Text("⚠️", style = MaterialTheme.typography.displayMedium)
        Spacer(Modifier.height(12.dp))
        Text(
            text = mensaje,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
            // El lector de pantalla anuncia el error apenas aparece
            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = onReintentar) { Text("Reintentar") }
    }
}

@Composable
private fun ContenedorEstado(
    modifier: Modifier = Modifier,
    contenido: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        contenido()
    }
}

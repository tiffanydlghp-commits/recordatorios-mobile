package com.example.recordatorios

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.recordatorios.data.DatosSimulados
import com.example.recordatorios.data.conteoPorEstado
import com.example.recordatorios.ui.RecordatoriosScreen
import com.example.recordatorios.ui.theme.RecordatoriosTheme

private const val TAG = "Recordatorios"

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Mensajes en Logcat (filtrar por "Recordatorios") para evidenciar que los datos se cargan
        val datos = DatosSimulados.recordatorios
        Log.d(TAG, "onCreate: ${datos.size} recordatorios cargados")
        Log.d(TAG, "Categorías únicas (Set): ${DatosSimulados.categoriasUnicas.map { it.nombre }}")
        Log.d(TAG, "Conteo por estado (Map): ${datos.conteoPorEstado()}")

        setContent {
            RecordatoriosTheme {
                RecordatoriosScreen(recordatorios = datos)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }
}

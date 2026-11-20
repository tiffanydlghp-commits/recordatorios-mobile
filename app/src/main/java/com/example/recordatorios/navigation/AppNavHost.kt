package com.example.recordatorios.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.recordatorios.ui.crear.CrearScreen
import com.example.recordatorios.ui.detalle.DetalleScreen
import com.example.recordatorios.ui.lista.ListaScreen

/**
 * CONSIGNA 5 — Rutas de la aplicación.
 *  lista            → destino inicial, sin argumentos
 *  crear            → formulario, sin argumentos
 *  detalle/{id}     → recibe SOLO el identificador (Int)
 */
object Rutas {
    const val LISTA = "lista"
    const val CREAR = "crear"
    const val ARG_ID = "id"
    const val DETALLE = "detalle/{$ARG_ID}"
    fun detalle(id: Int) = "detalle/$id"
}

/**
 * NavHost de la app. El NavController se crea y administra aquí (nivel superior);
 * las pantallas solo reciben lambdas, nunca el NavController.
 */
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Rutas.LISTA,
        modifier = modifier
    ) {
        composable(Rutas.LISTA) {
            ListaScreen(
                onCrearRecordatorio = { navController.navigate(Rutas.CREAR) },
                onRecordatorioClick = { id -> navController.navigate(Rutas.detalle(id)) }
            )
        }

        composable(Rutas.CREAR) {
            CrearScreen(
                // Al guardar o cancelar se vuelve al listado existente (no se crea otra copia)
                onGuardado = { navController.popBackStack() },
                onCancelar = { navController.popBackStack() }
            )
        }

        composable(
            route = Rutas.DETALLE,
            arguments = listOf(navArgument(Rutas.ARG_ID) { type = NavType.IntType })
        ) {
            DetalleScreen(onVolver = { navController.popBackStack() })
        }
    }
}

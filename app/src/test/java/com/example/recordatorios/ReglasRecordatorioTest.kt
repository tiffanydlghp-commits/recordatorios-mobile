package com.example.recordatorios

import com.example.recordatorios.data.DatosSimulados
import com.example.recordatorios.data.filtrarPorEstado
import com.example.recordatorios.data.filtrarPorPrioridad
import com.example.recordatorios.domain.EstadoRecordatorio
import com.example.recordatorios.domain.Prioridad
import com.example.recordatorios.validation.crearRecordatorio
import com.example.recordatorios.validation.crearRecordatorioOrNull
import com.example.recordatorios.validation.esFechaValida
import com.example.recordatorios.validation.esTituloValido
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Pruebas locales (JVM) de las reglas de validación y de las colecciones. */
class ReglasRecordatorioTest {

    @Test
    fun tituloCorto_esInvalido() {
        assertFalse("ok".esTituloValido())
        assertFalse("   ab   ".esTituloValido())
        assertTrue("Estudiar".esTituloValido())
    }

    @Test
    fun fechaConTexto_noCierraLaApp() {
        assertFalse("diez/11/2026".esFechaValida())
        assertFalse("32/01/2026".esFechaValida())
        assertTrue("20/11/2026".esFechaValida())
    }

    @Test(expected = IllegalArgumentException::class)
    fun crearRecordatorioInvalido_lanzaExcepcion() {
        crearRecordatorio(id = 0, titulo = "ab", categoria = DatosSimulados.TRABAJO, prioridad = Prioridad.Alta)
    }

    @Test
    fun crearRecordatorioOrNull_devuelveNullSiEsInvalido() {
        assertNull(crearRecordatorioOrNull(1, "x", DatosSimulados.HOGAR, Prioridad.Baja))
    }

    @Test
    fun vencidoSinFecha_esInvalido() {
        assertNull(
            runCatching {
                crearRecordatorio(
                    id = 9, titulo = "Sin fecha", categoria = DatosSimulados.HOGAR,
                    prioridad = Prioridad.Baja, estado = EstadoRecordatorio.Vencido
                )
            }.getOrNull()
        )
    }

    @Test
    fun setDeCategorias_noTieneRepetidos() {
        val lista = DatosSimulados.recordatorios.map { it.categoria }
        assertEquals(8, lista.size)
        assertEquals(5, DatosSimulados.categoriasUnicas.size)
    }

    @Test
    fun filtros_porEstadoYPrioridad() {
        val datos = DatosSimulados.recordatorios
        assertEquals(2, datos.filtrarPorEstado(EstadoRecordatorio.Completado("")).size)
        assertEquals(3, datos.filtrarPorPrioridad(Prioridad.Alta).size)
    }
}

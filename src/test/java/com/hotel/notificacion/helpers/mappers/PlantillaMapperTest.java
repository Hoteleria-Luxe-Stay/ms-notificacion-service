package com.hotel.notificacion.helpers.mappers;

import com.hotel.notificacion.api.dto.PlantillaResponse;
import com.hotel.notificacion.core.plantilla.model.Plantilla;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PlantillaMapperTest {

    @Test
    void toResponse_conPlantillaCompleta_mapeoCompleto() {
        Plantilla p = buildPlantilla();

        PlantillaResponse response = PlantillaMapper.toResponse(p);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getCodigo()).isEqualTo("BIENVENIDA");
        assertThat(response.getNombre()).isEqualTo("Plantilla Bienvenida");
        assertThat(response.getAsunto()).isEqualTo("Bienvenido a LuxeStay");
        assertThat(response.getContenido()).isEqualTo("<html>Hola {{nombre}}</html>");
        assertThat(response.getTipo()).isEqualTo("EMAIL");
        assertThat(response.getActiva()).isTrue();
        assertThat(response.getFechaCreacion()).isNotNull();
        assertThat(response.getFechaModificacion()).isNotNull();
    }

    @Test
    void toResponse_plantillaNull_retornaNull() {
        assertThat(PlantillaMapper.toResponse(null)).isNull();
    }

    @Test
    void toResponse_activaNull_retornaFalse() {
        Plantilla p = buildPlantilla();
        p.setActiva(null);

        PlantillaResponse response = PlantillaMapper.toResponse(p);

        assertThat(response.getActiva()).isFalse();
    }

    @Test
    void toResponse_sinFechas_fechasNull() {
        Plantilla p = buildPlantilla();
        p.setFechaCreacion(null);
        p.setFechaModificacion(null);

        PlantillaResponse response = PlantillaMapper.toResponse(p);

        assertThat(response.getFechaCreacion()).isNull();
        assertThat(response.getFechaModificacion()).isNull();
    }

    @Test
    void toResponse_activaFalse_retornaFalse() {
        Plantilla p = buildPlantilla();
        p.setActiva(false);

        PlantillaResponse response = PlantillaMapper.toResponse(p);

        assertThat(response.getActiva()).isFalse();
    }

    @Test
    void constructor_lanzaUnsupportedOperationException() {
        assertThatThrownBy(() -> {
            var constructor = PlantillaMapper.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        }).hasCauseInstanceOf(UnsupportedOperationException.class);
    }

    // --- Helper ---

    private Plantilla buildPlantilla() {
        Plantilla p = new Plantilla();
        p.setId(1L);
        p.setCodigo("BIENVENIDA");
        p.setNombre("Plantilla Bienvenida");
        p.setAsunto("Bienvenido a LuxeStay");
        p.setContenido("<html>Hola {{nombre}}</html>");
        p.setTipo("EMAIL");
        p.setActiva(true);
        p.setFechaCreacion(LocalDateTime.now());
        p.setFechaModificacion(LocalDateTime.now());
        return p;
    }
}

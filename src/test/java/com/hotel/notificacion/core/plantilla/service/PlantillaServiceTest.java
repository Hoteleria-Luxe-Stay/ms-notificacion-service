package com.hotel.notificacion.core.plantilla.service;

import com.hotel.notificacion.core.plantilla.model.Plantilla;
import com.hotel.notificacion.core.plantilla.repository.PlantillaRepository;
import com.hotel.notificacion.helpers.exceptions.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlantillaServiceTest {

    @Mock
    private PlantillaRepository plantillaRepository;

    @InjectMocks
    private PlantillaService plantillaService;

    // --- listar() ---

    @Test
    void listar_retornaTodasLasPlantillas() {
        when(plantillaRepository.findAll()).thenReturn(List.of(new Plantilla(), new Plantilla()));

        List<Plantilla> result = plantillaService.listar();

        assertThat(result).hasSize(2);
        verify(plantillaRepository).findAll();
    }

    // --- buscarPorId() ---

    @Test
    void buscarPorId_encontrado_retornaPlantilla() {
        Plantilla p = new Plantilla();
        p.setId(1L);
        when(plantillaRepository.findById(1L)).thenReturn(Optional.of(p));

        Plantilla result = plantillaService.buscarPorId(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void buscarPorId_noEncontrado_lanzaEntityNotFoundException() {
        when(plantillaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> plantillaService.buscarPorId(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    // --- crear() ---

    @Test
    void crear_conActivaNull_defaultTrue() {
        com.hotel.notificacion.api.dto.PlantillaRequest request = buildRequest(null);
        Plantilla saved = new Plantilla();
        saved.setId(1L);
        saved.setActiva(true);
        when(plantillaRepository.save(any())).thenReturn(saved);

        Plantilla result = plantillaService.crear(request);

        ArgumentCaptor<Plantilla> captor = ArgumentCaptor.forClass(Plantilla.class);
        verify(plantillaRepository).save(captor.capture());
        assertThat(captor.getValue().getActiva()).isTrue();
    }

    @Test
    void crear_conActivaFalse_guardaFalse() {
        com.hotel.notificacion.api.dto.PlantillaRequest request = buildRequest(false);
        when(plantillaRepository.save(any())).thenReturn(new Plantilla());

        plantillaService.crear(request);

        ArgumentCaptor<Plantilla> captor = ArgumentCaptor.forClass(Plantilla.class);
        verify(plantillaRepository).save(captor.capture());
        assertThat(captor.getValue().getActiva()).isFalse();
    }

    @Test
    void crear_estableceFechasCreacionYModificacion() {
        com.hotel.notificacion.api.dto.PlantillaRequest request = buildRequest(true);
        when(plantillaRepository.save(any())).thenReturn(new Plantilla());

        plantillaService.crear(request);

        ArgumentCaptor<Plantilla> captor = ArgumentCaptor.forClass(Plantilla.class);
        verify(plantillaRepository).save(captor.capture());
        assertThat(captor.getValue().getFechaCreacion()).isNotNull();
        assertThat(captor.getValue().getFechaModificacion()).isNotNull();
    }

    @Test
    void crear_mapeoCompletoDeCampos() {
        com.hotel.notificacion.api.dto.PlantillaRequest request = buildRequest(true);
        when(plantillaRepository.save(any())).thenReturn(new Plantilla());

        plantillaService.crear(request);

        ArgumentCaptor<Plantilla> captor = ArgumentCaptor.forClass(Plantilla.class);
        verify(plantillaRepository).save(captor.capture());
        Plantilla captured = captor.getValue();
        assertThat(captured.getCodigo()).isEqualTo("CODIGO_TEST");
        assertThat(captured.getNombre()).isEqualTo("Nombre Test");
        assertThat(captured.getAsunto()).isEqualTo("Asunto Test");
        assertThat(captured.getContenido()).isEqualTo("Contenido test");
    }

    // --- actualizar() ---

    @Test
    void actualizar_encontrado_actualizaCampos() {
        Plantilla existente = new Plantilla();
        existente.setId(1L);
        existente.setActiva(true);
        when(plantillaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(plantillaRepository.save(any())).thenReturn(existente);

        com.hotel.notificacion.api.dto.PlantillaRequest request = buildRequest(false);
        Plantilla result = plantillaService.actualizar(1L, request);

        assertThat(result).isNotNull();
        verify(plantillaRepository).save(any());
    }

    @Test
    void actualizar_noEncontrado_lanzaEntityNotFoundException() {
        when(plantillaRepository.findById(99L)).thenReturn(Optional.empty());

        com.hotel.notificacion.api.dto.PlantillaRequest request = buildRequest(true);

        assertThatThrownBy(() -> plantillaService.actualizar(99L, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void actualizar_activaNull_mantienValorExistente() {
        Plantilla existente = new Plantilla();
        existente.setId(1L);
        existente.setActiva(true);
        when(plantillaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(plantillaRepository.save(any())).thenReturn(existente);

        com.hotel.notificacion.api.dto.PlantillaRequest request = buildRequest(null);
        plantillaService.actualizar(1L, request);

        ArgumentCaptor<Plantilla> captor = ArgumentCaptor.forClass(Plantilla.class);
        verify(plantillaRepository).save(captor.capture());
        assertThat(captor.getValue().getActiva()).isTrue();
    }

    @Test
    void actualizar_actualizaFechaModificacion() {
        Plantilla existente = new Plantilla();
        existente.setId(1L);
        existente.setActiva(true);
        when(plantillaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(plantillaRepository.save(any())).thenReturn(existente);

        com.hotel.notificacion.api.dto.PlantillaRequest request = buildRequest(true);
        plantillaService.actualizar(1L, request);

        ArgumentCaptor<Plantilla> captor = ArgumentCaptor.forClass(Plantilla.class);
        verify(plantillaRepository).save(captor.capture());
        assertThat(captor.getValue().getFechaModificacion()).isNotNull();
    }

    // --- eliminar() ---

    @Test
    void eliminar_encontrado_eliminaCorrectamente() {
        when(plantillaRepository.existsById(1L)).thenReturn(true);

        plantillaService.eliminar(1L);

        verify(plantillaRepository).deleteById(1L);
    }

    @Test
    void eliminar_noEncontrado_lanzaEntityNotFoundException() {
        when(plantillaRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> plantillaService.eliminar(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");

        verify(plantillaRepository, never()).deleteById(any());
    }

    // --- Helper ---

    private com.hotel.notificacion.api.dto.PlantillaRequest buildRequest(Boolean activa) {
        com.hotel.notificacion.api.dto.PlantillaRequest request = new com.hotel.notificacion.api.dto.PlantillaRequest();
        request.setCodigo("CODIGO_TEST");
        request.setNombre("Nombre Test");
        request.setAsunto("Asunto Test");
        request.setContenido("Contenido test");
        request.setTipo(com.hotel.notificacion.api.dto.PlantillaRequest.TipoEnum.EMAIL);
        request.setActiva(activa);
        return request;
    }
}

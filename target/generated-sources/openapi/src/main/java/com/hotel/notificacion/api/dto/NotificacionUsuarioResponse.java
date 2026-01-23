package com.hotel.notificacion.api.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * NotificacionUsuarioResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-01-23T03:55:42.557644-05:00[America/Lima]", comments = "Generator version: 7.6.0")
public class NotificacionUsuarioResponse {

  private Long id;

  private String titulo;

  private String mensaje;

  private String tipo;

  private Boolean leida;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime fechaCreacion;

  public NotificacionUsuarioResponse id(Long id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
  */
  
  @Schema(name = "id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public NotificacionUsuarioResponse titulo(String titulo) {
    this.titulo = titulo;
    return this;
  }

  /**
   * Get titulo
   * @return titulo
  */
  
  @Schema(name = "titulo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("titulo")
  public String getTitulo() {
    return titulo;
  }

  public void setTitulo(String titulo) {
    this.titulo = titulo;
  }

  public NotificacionUsuarioResponse mensaje(String mensaje) {
    this.mensaje = mensaje;
    return this;
  }

  /**
   * Get mensaje
   * @return mensaje
  */
  
  @Schema(name = "mensaje", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("mensaje")
  public String getMensaje() {
    return mensaje;
  }

  public void setMensaje(String mensaje) {
    this.mensaje = mensaje;
  }

  public NotificacionUsuarioResponse tipo(String tipo) {
    this.tipo = tipo;
    return this;
  }

  /**
   * Get tipo
   * @return tipo
  */
  
  @Schema(name = "tipo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("tipo")
  public String getTipo() {
    return tipo;
  }

  public void setTipo(String tipo) {
    this.tipo = tipo;
  }

  public NotificacionUsuarioResponse leida(Boolean leida) {
    this.leida = leida;
    return this;
  }

  /**
   * Get leida
   * @return leida
  */
  
  @Schema(name = "leida", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("leida")
  public Boolean getLeida() {
    return leida;
  }

  public void setLeida(Boolean leida) {
    this.leida = leida;
  }

  public NotificacionUsuarioResponse fechaCreacion(OffsetDateTime fechaCreacion) {
    this.fechaCreacion = fechaCreacion;
    return this;
  }

  /**
   * Get fechaCreacion
   * @return fechaCreacion
  */
  @Valid 
  @Schema(name = "fechaCreacion", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("fechaCreacion")
  public OffsetDateTime getFechaCreacion() {
    return fechaCreacion;
  }

  public void setFechaCreacion(OffsetDateTime fechaCreacion) {
    this.fechaCreacion = fechaCreacion;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NotificacionUsuarioResponse notificacionUsuarioResponse = (NotificacionUsuarioResponse) o;
    return Objects.equals(this.id, notificacionUsuarioResponse.id) &&
        Objects.equals(this.titulo, notificacionUsuarioResponse.titulo) &&
        Objects.equals(this.mensaje, notificacionUsuarioResponse.mensaje) &&
        Objects.equals(this.tipo, notificacionUsuarioResponse.tipo) &&
        Objects.equals(this.leida, notificacionUsuarioResponse.leida) &&
        Objects.equals(this.fechaCreacion, notificacionUsuarioResponse.fechaCreacion);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, titulo, mensaje, tipo, leida, fechaCreacion);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NotificacionUsuarioResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    titulo: ").append(toIndentedString(titulo)).append("\n");
    sb.append("    mensaje: ").append(toIndentedString(mensaje)).append("\n");
    sb.append("    tipo: ").append(toIndentedString(tipo)).append("\n");
    sb.append("    leida: ").append(toIndentedString(leida)).append("\n");
    sb.append("    fechaCreacion: ").append(toIndentedString(fechaCreacion)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}


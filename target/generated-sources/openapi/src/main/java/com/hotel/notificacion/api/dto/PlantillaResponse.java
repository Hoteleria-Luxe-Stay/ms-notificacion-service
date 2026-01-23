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
 * PlantillaResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-01-23T03:55:42.557644-05:00[America/Lima]", comments = "Generator version: 7.6.0")
public class PlantillaResponse {

  private Long id;

  private String codigo;

  private String nombre;

  private String asunto;

  private String contenido;

  private String tipo;

  private Boolean activa;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime fechaCreacion;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime fechaModificacion;

  public PlantillaResponse id(Long id) {
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

  public PlantillaResponse codigo(String codigo) {
    this.codigo = codigo;
    return this;
  }

  /**
   * Get codigo
   * @return codigo
  */
  
  @Schema(name = "codigo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("codigo")
  public String getCodigo() {
    return codigo;
  }

  public void setCodigo(String codigo) {
    this.codigo = codigo;
  }

  public PlantillaResponse nombre(String nombre) {
    this.nombre = nombre;
    return this;
  }

  /**
   * Get nombre
   * @return nombre
  */
  
  @Schema(name = "nombre", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("nombre")
  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public PlantillaResponse asunto(String asunto) {
    this.asunto = asunto;
    return this;
  }

  /**
   * Get asunto
   * @return asunto
  */
  
  @Schema(name = "asunto", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("asunto")
  public String getAsunto() {
    return asunto;
  }

  public void setAsunto(String asunto) {
    this.asunto = asunto;
  }

  public PlantillaResponse contenido(String contenido) {
    this.contenido = contenido;
    return this;
  }

  /**
   * Get contenido
   * @return contenido
  */
  
  @Schema(name = "contenido", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("contenido")
  public String getContenido() {
    return contenido;
  }

  public void setContenido(String contenido) {
    this.contenido = contenido;
  }

  public PlantillaResponse tipo(String tipo) {
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

  public PlantillaResponse activa(Boolean activa) {
    this.activa = activa;
    return this;
  }

  /**
   * Get activa
   * @return activa
  */
  
  @Schema(name = "activa", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("activa")
  public Boolean getActiva() {
    return activa;
  }

  public void setActiva(Boolean activa) {
    this.activa = activa;
  }

  public PlantillaResponse fechaCreacion(OffsetDateTime fechaCreacion) {
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

  public PlantillaResponse fechaModificacion(OffsetDateTime fechaModificacion) {
    this.fechaModificacion = fechaModificacion;
    return this;
  }

  /**
   * Get fechaModificacion
   * @return fechaModificacion
  */
  @Valid 
  @Schema(name = "fechaModificacion", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("fechaModificacion")
  public OffsetDateTime getFechaModificacion() {
    return fechaModificacion;
  }

  public void setFechaModificacion(OffsetDateTime fechaModificacion) {
    this.fechaModificacion = fechaModificacion;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PlantillaResponse plantillaResponse = (PlantillaResponse) o;
    return Objects.equals(this.id, plantillaResponse.id) &&
        Objects.equals(this.codigo, plantillaResponse.codigo) &&
        Objects.equals(this.nombre, plantillaResponse.nombre) &&
        Objects.equals(this.asunto, plantillaResponse.asunto) &&
        Objects.equals(this.contenido, plantillaResponse.contenido) &&
        Objects.equals(this.tipo, plantillaResponse.tipo) &&
        Objects.equals(this.activa, plantillaResponse.activa) &&
        Objects.equals(this.fechaCreacion, plantillaResponse.fechaCreacion) &&
        Objects.equals(this.fechaModificacion, plantillaResponse.fechaModificacion);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, codigo, nombre, asunto, contenido, tipo, activa, fechaCreacion, fechaModificacion);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PlantillaResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    codigo: ").append(toIndentedString(codigo)).append("\n");
    sb.append("    nombre: ").append(toIndentedString(nombre)).append("\n");
    sb.append("    asunto: ").append(toIndentedString(asunto)).append("\n");
    sb.append("    contenido: ").append(toIndentedString(contenido)).append("\n");
    sb.append("    tipo: ").append(toIndentedString(tipo)).append("\n");
    sb.append("    activa: ").append(toIndentedString(activa)).append("\n");
    sb.append("    fechaCreacion: ").append(toIndentedString(fechaCreacion)).append("\n");
    sb.append("    fechaModificacion: ").append(toIndentedString(fechaModificacion)).append("\n");
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


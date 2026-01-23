package com.hotel.notificacion.api.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
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
 * NotificacionResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-01-23T03:55:42.557644-05:00[America/Lima]", comments = "Generator version: 7.6.0")
public class NotificacionResponse {

  private Long id;

  /**
   * Gets or Sets tipo
   */
  public enum TipoEnum {
    EMAIL("EMAIL"),
    
    SMS("SMS"),
    
    PUSH("PUSH");

    private String value;

    TipoEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static TipoEnum fromValue(String value) {
      for (TipoEnum b : TipoEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private TipoEnum tipo;

  private String destinatario;

  private String asunto;

  private String contenido;

  /**
   * Gets or Sets estado
   */
  public enum EstadoEnum {
    PENDIENTE("PENDIENTE"),
    
    ENVIADA("ENVIADA"),
    
    FALLIDA("FALLIDA");

    private String value;

    EstadoEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static EstadoEnum fromValue(String value) {
      for (EstadoEnum b : EstadoEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private EstadoEnum estado;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime fechaCreacion;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime fechaEnvio;

  private Integer intentos;

  private String errorMensaje;

  public NotificacionResponse id(Long id) {
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

  public NotificacionResponse tipo(TipoEnum tipo) {
    this.tipo = tipo;
    return this;
  }

  /**
   * Get tipo
   * @return tipo
  */
  
  @Schema(name = "tipo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("tipo")
  public TipoEnum getTipo() {
    return tipo;
  }

  public void setTipo(TipoEnum tipo) {
    this.tipo = tipo;
  }

  public NotificacionResponse destinatario(String destinatario) {
    this.destinatario = destinatario;
    return this;
  }

  /**
   * Get destinatario
   * @return destinatario
  */
  
  @Schema(name = "destinatario", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("destinatario")
  public String getDestinatario() {
    return destinatario;
  }

  public void setDestinatario(String destinatario) {
    this.destinatario = destinatario;
  }

  public NotificacionResponse asunto(String asunto) {
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

  public NotificacionResponse contenido(String contenido) {
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

  public NotificacionResponse estado(EstadoEnum estado) {
    this.estado = estado;
    return this;
  }

  /**
   * Get estado
   * @return estado
  */
  
  @Schema(name = "estado", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("estado")
  public EstadoEnum getEstado() {
    return estado;
  }

  public void setEstado(EstadoEnum estado) {
    this.estado = estado;
  }

  public NotificacionResponse fechaCreacion(OffsetDateTime fechaCreacion) {
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

  public NotificacionResponse fechaEnvio(OffsetDateTime fechaEnvio) {
    this.fechaEnvio = fechaEnvio;
    return this;
  }

  /**
   * Get fechaEnvio
   * @return fechaEnvio
  */
  @Valid 
  @Schema(name = "fechaEnvio", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("fechaEnvio")
  public OffsetDateTime getFechaEnvio() {
    return fechaEnvio;
  }

  public void setFechaEnvio(OffsetDateTime fechaEnvio) {
    this.fechaEnvio = fechaEnvio;
  }

  public NotificacionResponse intentos(Integer intentos) {
    this.intentos = intentos;
    return this;
  }

  /**
   * Get intentos
   * @return intentos
  */
  
  @Schema(name = "intentos", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("intentos")
  public Integer getIntentos() {
    return intentos;
  }

  public void setIntentos(Integer intentos) {
    this.intentos = intentos;
  }

  public NotificacionResponse errorMensaje(String errorMensaje) {
    this.errorMensaje = errorMensaje;
    return this;
  }

  /**
   * Get errorMensaje
   * @return errorMensaje
  */
  
  @Schema(name = "errorMensaje", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("errorMensaje")
  public String getErrorMensaje() {
    return errorMensaje;
  }

  public void setErrorMensaje(String errorMensaje) {
    this.errorMensaje = errorMensaje;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NotificacionResponse notificacionResponse = (NotificacionResponse) o;
    return Objects.equals(this.id, notificacionResponse.id) &&
        Objects.equals(this.tipo, notificacionResponse.tipo) &&
        Objects.equals(this.destinatario, notificacionResponse.destinatario) &&
        Objects.equals(this.asunto, notificacionResponse.asunto) &&
        Objects.equals(this.contenido, notificacionResponse.contenido) &&
        Objects.equals(this.estado, notificacionResponse.estado) &&
        Objects.equals(this.fechaCreacion, notificacionResponse.fechaCreacion) &&
        Objects.equals(this.fechaEnvio, notificacionResponse.fechaEnvio) &&
        Objects.equals(this.intentos, notificacionResponse.intentos) &&
        Objects.equals(this.errorMensaje, notificacionResponse.errorMensaje);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, tipo, destinatario, asunto, contenido, estado, fechaCreacion, fechaEnvio, intentos, errorMensaje);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NotificacionResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    tipo: ").append(toIndentedString(tipo)).append("\n");
    sb.append("    destinatario: ").append(toIndentedString(destinatario)).append("\n");
    sb.append("    asunto: ").append(toIndentedString(asunto)).append("\n");
    sb.append("    contenido: ").append(toIndentedString(contenido)).append("\n");
    sb.append("    estado: ").append(toIndentedString(estado)).append("\n");
    sb.append("    fechaCreacion: ").append(toIndentedString(fechaCreacion)).append("\n");
    sb.append("    fechaEnvio: ").append(toIndentedString(fechaEnvio)).append("\n");
    sb.append("    intentos: ").append(toIndentedString(intentos)).append("\n");
    sb.append("    errorMensaje: ").append(toIndentedString(errorMensaje)).append("\n");
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


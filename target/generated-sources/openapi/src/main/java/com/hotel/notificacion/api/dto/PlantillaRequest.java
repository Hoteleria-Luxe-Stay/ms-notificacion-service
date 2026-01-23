package com.hotel.notificacion.api.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * PlantillaRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-01-23T03:55:42.557644-05:00[America/Lima]", comments = "Generator version: 7.6.0")
public class PlantillaRequest {

  private String codigo;

  private String nombre;

  private String asunto;

  private String contenido;

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

  private Boolean activa = true;

  public PlantillaRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public PlantillaRequest(String codigo, String nombre, String asunto, String contenido) {
    this.codigo = codigo;
    this.nombre = nombre;
    this.asunto = asunto;
    this.contenido = contenido;
  }

  public PlantillaRequest codigo(String codigo) {
    this.codigo = codigo;
    return this;
  }

  /**
   * Codigo unico (ej. RESERVA_CONFIRMADA)
   * @return codigo
  */
  @NotNull @Size(max = 50) 
  @Schema(name = "codigo", description = "Codigo unico (ej. RESERVA_CONFIRMADA)", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("codigo")
  public String getCodigo() {
    return codigo;
  }

  public void setCodigo(String codigo) {
    this.codigo = codigo;
  }

  public PlantillaRequest nombre(String nombre) {
    this.nombre = nombre;
    return this;
  }

  /**
   * Get nombre
   * @return nombre
  */
  @NotNull @Size(max = 100) 
  @Schema(name = "nombre", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("nombre")
  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public PlantillaRequest asunto(String asunto) {
    this.asunto = asunto;
    return this;
  }

  /**
   * Get asunto
   * @return asunto
  */
  @NotNull @Size(max = 200) 
  @Schema(name = "asunto", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("asunto")
  public String getAsunto() {
    return asunto;
  }

  public void setAsunto(String asunto) {
    this.asunto = asunto;
  }

  public PlantillaRequest contenido(String contenido) {
    this.contenido = contenido;
    return this;
  }

  /**
   * Soporta variables como {{nombre}}, {{fecha}}
   * @return contenido
  */
  @NotNull 
  @Schema(name = "contenido", description = "Soporta variables como {{nombre}}, {{fecha}}", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("contenido")
  public String getContenido() {
    return contenido;
  }

  public void setContenido(String contenido) {
    this.contenido = contenido;
  }

  public PlantillaRequest tipo(TipoEnum tipo) {
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

  public PlantillaRequest activa(Boolean activa) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PlantillaRequest plantillaRequest = (PlantillaRequest) o;
    return Objects.equals(this.codigo, plantillaRequest.codigo) &&
        Objects.equals(this.nombre, plantillaRequest.nombre) &&
        Objects.equals(this.asunto, plantillaRequest.asunto) &&
        Objects.equals(this.contenido, plantillaRequest.contenido) &&
        Objects.equals(this.tipo, plantillaRequest.tipo) &&
        Objects.equals(this.activa, plantillaRequest.activa);
  }

  @Override
  public int hashCode() {
    return Objects.hash(codigo, nombre, asunto, contenido, tipo, activa);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PlantillaRequest {\n");
    sb.append("    codigo: ").append(toIndentedString(codigo)).append("\n");
    sb.append("    nombre: ").append(toIndentedString(nombre)).append("\n");
    sb.append("    asunto: ").append(toIndentedString(asunto)).append("\n");
    sb.append("    contenido: ").append(toIndentedString(contenido)).append("\n");
    sb.append("    tipo: ").append(toIndentedString(tipo)).append("\n");
    sb.append("    activa: ").append(toIndentedString(activa)).append("\n");
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


package com.hotel.notificacion.api.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.Map;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * EnviarNotificacionRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-01-23T03:55:42.557644-05:00[America/Lima]", comments = "Generator version: 7.6.0")
public class EnviarNotificacionRequest {

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

  private Long plantillaId;

  @Valid
  private Map<String, String> variables = new HashMap<>();

  public EnviarNotificacionRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public EnviarNotificacionRequest(TipoEnum tipo, String destinatario, String asunto, String contenido) {
    this.tipo = tipo;
    this.destinatario = destinatario;
    this.asunto = asunto;
    this.contenido = contenido;
  }

  public EnviarNotificacionRequest tipo(TipoEnum tipo) {
    this.tipo = tipo;
    return this;
  }

  /**
   * Get tipo
   * @return tipo
  */
  @NotNull 
  @Schema(name = "tipo", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("tipo")
  public TipoEnum getTipo() {
    return tipo;
  }

  public void setTipo(TipoEnum tipo) {
    this.tipo = tipo;
  }

  public EnviarNotificacionRequest destinatario(String destinatario) {
    this.destinatario = destinatario;
    return this;
  }

  /**
   * Email, numero de telefono o ID de usuario
   * @return destinatario
  */
  @NotNull 
  @Schema(name = "destinatario", description = "Email, numero de telefono o ID de usuario", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("destinatario")
  public String getDestinatario() {
    return destinatario;
  }

  public void setDestinatario(String destinatario) {
    this.destinatario = destinatario;
  }

  public EnviarNotificacionRequest asunto(String asunto) {
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

  public EnviarNotificacionRequest contenido(String contenido) {
    this.contenido = contenido;
    return this;
  }

  /**
   * Get contenido
   * @return contenido
  */
  @NotNull 
  @Schema(name = "contenido", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("contenido")
  public String getContenido() {
    return contenido;
  }

  public void setContenido(String contenido) {
    this.contenido = contenido;
  }

  public EnviarNotificacionRequest plantillaId(Long plantillaId) {
    this.plantillaId = plantillaId;
    return this;
  }

  /**
   * Usar plantilla en lugar de contenido
   * @return plantillaId
  */
  
  @Schema(name = "plantillaId", description = "Usar plantilla en lugar de contenido", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("plantillaId")
  public Long getPlantillaId() {
    return plantillaId;
  }

  public void setPlantillaId(Long plantillaId) {
    this.plantillaId = plantillaId;
  }

  public EnviarNotificacionRequest variables(Map<String, String> variables) {
    this.variables = variables;
    return this;
  }

  public EnviarNotificacionRequest putVariablesItem(String key, String variablesItem) {
    if (this.variables == null) {
      this.variables = new HashMap<>();
    }
    this.variables.put(key, variablesItem);
    return this;
  }

  /**
   * Variables para la plantilla
   * @return variables
  */
  
  @Schema(name = "variables", description = "Variables para la plantilla", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("variables")
  public Map<String, String> getVariables() {
    return variables;
  }

  public void setVariables(Map<String, String> variables) {
    this.variables = variables;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EnviarNotificacionRequest enviarNotificacionRequest = (EnviarNotificacionRequest) o;
    return Objects.equals(this.tipo, enviarNotificacionRequest.tipo) &&
        Objects.equals(this.destinatario, enviarNotificacionRequest.destinatario) &&
        Objects.equals(this.asunto, enviarNotificacionRequest.asunto) &&
        Objects.equals(this.contenido, enviarNotificacionRequest.contenido) &&
        Objects.equals(this.plantillaId, enviarNotificacionRequest.plantillaId) &&
        Objects.equals(this.variables, enviarNotificacionRequest.variables);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tipo, destinatario, asunto, contenido, plantillaId, variables);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EnviarNotificacionRequest {\n");
    sb.append("    tipo: ").append(toIndentedString(tipo)).append("\n");
    sb.append("    destinatario: ").append(toIndentedString(destinatario)).append("\n");
    sb.append("    asunto: ").append(toIndentedString(asunto)).append("\n");
    sb.append("    contenido: ").append(toIndentedString(contenido)).append("\n");
    sb.append("    plantillaId: ").append(toIndentedString(plantillaId)).append("\n");
    sb.append("    variables: ").append(toIndentedString(variables)).append("\n");
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


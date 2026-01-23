package com.hotel.notificacion.api.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
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
 * EstadisticasResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-01-23T03:55:42.557644-05:00[America/Lima]", comments = "Generator version: 7.6.0")
public class EstadisticasResponse {

  private Integer totalEnviadas;

  private Integer totalFallidas;

  private Integer totalPendientes;

  @Valid
  private Map<String, Integer> porTipo = new HashMap<>();

  private Double tasaExito;

  public EstadisticasResponse totalEnviadas(Integer totalEnviadas) {
    this.totalEnviadas = totalEnviadas;
    return this;
  }

  /**
   * Get totalEnviadas
   * @return totalEnviadas
  */
  
  @Schema(name = "totalEnviadas", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("totalEnviadas")
  public Integer getTotalEnviadas() {
    return totalEnviadas;
  }

  public void setTotalEnviadas(Integer totalEnviadas) {
    this.totalEnviadas = totalEnviadas;
  }

  public EstadisticasResponse totalFallidas(Integer totalFallidas) {
    this.totalFallidas = totalFallidas;
    return this;
  }

  /**
   * Get totalFallidas
   * @return totalFallidas
  */
  
  @Schema(name = "totalFallidas", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("totalFallidas")
  public Integer getTotalFallidas() {
    return totalFallidas;
  }

  public void setTotalFallidas(Integer totalFallidas) {
    this.totalFallidas = totalFallidas;
  }

  public EstadisticasResponse totalPendientes(Integer totalPendientes) {
    this.totalPendientes = totalPendientes;
    return this;
  }

  /**
   * Get totalPendientes
   * @return totalPendientes
  */
  
  @Schema(name = "totalPendientes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("totalPendientes")
  public Integer getTotalPendientes() {
    return totalPendientes;
  }

  public void setTotalPendientes(Integer totalPendientes) {
    this.totalPendientes = totalPendientes;
  }

  public EstadisticasResponse porTipo(Map<String, Integer> porTipo) {
    this.porTipo = porTipo;
    return this;
  }

  public EstadisticasResponse putPorTipoItem(String key, Integer porTipoItem) {
    if (this.porTipo == null) {
      this.porTipo = new HashMap<>();
    }
    this.porTipo.put(key, porTipoItem);
    return this;
  }

  /**
   * Get porTipo
   * @return porTipo
  */
  
  @Schema(name = "porTipo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("porTipo")
  public Map<String, Integer> getPorTipo() {
    return porTipo;
  }

  public void setPorTipo(Map<String, Integer> porTipo) {
    this.porTipo = porTipo;
  }

  public EstadisticasResponse tasaExito(Double tasaExito) {
    this.tasaExito = tasaExito;
    return this;
  }

  /**
   * Get tasaExito
   * @return tasaExito
  */
  
  @Schema(name = "tasaExito", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("tasaExito")
  public Double getTasaExito() {
    return tasaExito;
  }

  public void setTasaExito(Double tasaExito) {
    this.tasaExito = tasaExito;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EstadisticasResponse estadisticasResponse = (EstadisticasResponse) o;
    return Objects.equals(this.totalEnviadas, estadisticasResponse.totalEnviadas) &&
        Objects.equals(this.totalFallidas, estadisticasResponse.totalFallidas) &&
        Objects.equals(this.totalPendientes, estadisticasResponse.totalPendientes) &&
        Objects.equals(this.porTipo, estadisticasResponse.porTipo) &&
        Objects.equals(this.tasaExito, estadisticasResponse.tasaExito);
  }

  @Override
  public int hashCode() {
    return Objects.hash(totalEnviadas, totalFallidas, totalPendientes, porTipo, tasaExito);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EstadisticasResponse {\n");
    sb.append("    totalEnviadas: ").append(toIndentedString(totalEnviadas)).append("\n");
    sb.append("    totalFallidas: ").append(toIndentedString(totalFallidas)).append("\n");
    sb.append("    totalPendientes: ").append(toIndentedString(totalPendientes)).append("\n");
    sb.append("    porTipo: ").append(toIndentedString(porTipo)).append("\n");
    sb.append("    tasaExito: ").append(toIndentedString(tasaExito)).append("\n");
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


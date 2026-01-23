package com.hotel.notificacion.api.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ContactoRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-01-23T03:55:42.557644-05:00[America/Lima]", comments = "Generator version: 7.6.0")
public class ContactoRequest {

  private String nombre;

  private String email;

  private String telefono;

  private String asunto;

  private String mensaje;

  public ContactoRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ContactoRequest(String nombre, String email, String mensaje) {
    this.nombre = nombre;
    this.email = email;
    this.mensaje = mensaje;
  }

  public ContactoRequest nombre(String nombre) {
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

  public ContactoRequest email(String email) {
    this.email = email;
    return this;
  }

  /**
   * Get email
   * @return email
  */
  @NotNull @jakarta.validation.constraints.Email 
  @Schema(name = "email", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("email")
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public ContactoRequest telefono(String telefono) {
    this.telefono = telefono;
    return this;
  }

  /**
   * Get telefono
   * @return telefono
  */
  @Size(max = 20) 
  @Schema(name = "telefono", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("telefono")
  public String getTelefono() {
    return telefono;
  }

  public void setTelefono(String telefono) {
    this.telefono = telefono;
  }

  public ContactoRequest asunto(String asunto) {
    this.asunto = asunto;
    return this;
  }

  /**
   * Get asunto
   * @return asunto
  */
  @Size(max = 200) 
  @Schema(name = "asunto", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("asunto")
  public String getAsunto() {
    return asunto;
  }

  public void setAsunto(String asunto) {
    this.asunto = asunto;
  }

  public ContactoRequest mensaje(String mensaje) {
    this.mensaje = mensaje;
    return this;
  }

  /**
   * Get mensaje
   * @return mensaje
  */
  @NotNull @Size(max = 2000) 
  @Schema(name = "mensaje", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("mensaje")
  public String getMensaje() {
    return mensaje;
  }

  public void setMensaje(String mensaje) {
    this.mensaje = mensaje;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ContactoRequest contactoRequest = (ContactoRequest) o;
    return Objects.equals(this.nombre, contactoRequest.nombre) &&
        Objects.equals(this.email, contactoRequest.email) &&
        Objects.equals(this.telefono, contactoRequest.telefono) &&
        Objects.equals(this.asunto, contactoRequest.asunto) &&
        Objects.equals(this.mensaje, contactoRequest.mensaje);
  }

  @Override
  public int hashCode() {
    return Objects.hash(nombre, email, telefono, asunto, mensaje);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ContactoRequest {\n");
    sb.append("    nombre: ").append(toIndentedString(nombre)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    telefono: ").append(toIndentedString(telefono)).append("\n");
    sb.append("    asunto: ").append(toIndentedString(asunto)).append("\n");
    sb.append("    mensaje: ").append(toIndentedString(mensaje)).append("\n");
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


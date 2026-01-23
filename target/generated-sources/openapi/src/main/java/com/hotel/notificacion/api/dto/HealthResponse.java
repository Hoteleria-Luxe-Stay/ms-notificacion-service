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
 * HealthResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-01-23T03:55:42.557644-05:00[America/Lima]", comments = "Generator version: 7.6.0")
public class HealthResponse {

  /**
   * Gets or Sets status
   */
  public enum StatusEnum {
    UP("UP"),
    
    DOWN("DOWN");

    private String value;

    StatusEnum(String value) {
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
    public static StatusEnum fromValue(String value) {
      for (StatusEnum b : StatusEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private StatusEnum status;

  /**
   * Gets or Sets rabbitmq
   */
  public enum RabbitmqEnum {
    UP("UP"),
    
    DOWN("DOWN");

    private String value;

    RabbitmqEnum(String value) {
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
    public static RabbitmqEnum fromValue(String value) {
      for (RabbitmqEnum b : RabbitmqEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private RabbitmqEnum rabbitmq;

  /**
   * Gets or Sets database
   */
  public enum DatabaseEnum {
    UP("UP"),
    
    DOWN("DOWN");

    private String value;

    DatabaseEnum(String value) {
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
    public static DatabaseEnum fromValue(String value) {
      for (DatabaseEnum b : DatabaseEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private DatabaseEnum database;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime timestamp;

  public HealthResponse status(StatusEnum status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
  */
  
  @Schema(name = "status", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("status")
  public StatusEnum getStatus() {
    return status;
  }

  public void setStatus(StatusEnum status) {
    this.status = status;
  }

  public HealthResponse rabbitmq(RabbitmqEnum rabbitmq) {
    this.rabbitmq = rabbitmq;
    return this;
  }

  /**
   * Get rabbitmq
   * @return rabbitmq
  */
  
  @Schema(name = "rabbitmq", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rabbitmq")
  public RabbitmqEnum getRabbitmq() {
    return rabbitmq;
  }

  public void setRabbitmq(RabbitmqEnum rabbitmq) {
    this.rabbitmq = rabbitmq;
  }

  public HealthResponse database(DatabaseEnum database) {
    this.database = database;
    return this;
  }

  /**
   * Get database
   * @return database
  */
  
  @Schema(name = "database", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("database")
  public DatabaseEnum getDatabase() {
    return database;
  }

  public void setDatabase(DatabaseEnum database) {
    this.database = database;
  }

  public HealthResponse timestamp(OffsetDateTime timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  /**
   * Get timestamp
   * @return timestamp
  */
  @Valid 
  @Schema(name = "timestamp", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("timestamp")
  public OffsetDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(OffsetDateTime timestamp) {
    this.timestamp = timestamp;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HealthResponse healthResponse = (HealthResponse) o;
    return Objects.equals(this.status, healthResponse.status) &&
        Objects.equals(this.rabbitmq, healthResponse.rabbitmq) &&
        Objects.equals(this.database, healthResponse.database) &&
        Objects.equals(this.timestamp, healthResponse.timestamp);
  }

  @Override
  public int hashCode() {
    return Objects.hash(status, rabbitmq, database, timestamp);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HealthResponse {\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    rabbitmq: ").append(toIndentedString(rabbitmq)).append("\n");
    sb.append("    database: ").append(toIndentedString(database)).append("\n");
    sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
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


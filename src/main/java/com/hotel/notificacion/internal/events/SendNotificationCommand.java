package com.hotel.notificacion.internal.events;

import java.util.Map;

public class SendNotificationCommand {

    private String tipo;
    private String destinatario;
    private String asunto;
    private String contenido;
    private String plantillaCodigo;
    private Map<String, String> variables;

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getPlantillaCodigo() {
        return plantillaCodigo;
    }

    public void setPlantillaCodigo(String plantillaCodigo) {
        this.plantillaCodigo = plantillaCodigo;
    }

    public Map<String, String> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, String> variables) {
        this.variables = variables;
    }
}

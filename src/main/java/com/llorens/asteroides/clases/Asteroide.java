package com.llorens.asteroides.clases;

import java.util.Date;

public class Asteroide {
    public String nombre;
    public Double diametro;
    public Double velocidad;
    public String fecha;
    public String planeta;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getDiametro() {
        return diametro;
    }

    public void setDiametro(Double diametro) {
        this.diametro = diametro;
    }

    public Double getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(Double velocidad) {
        this.velocidad = velocidad;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getPlaneta() {
        return planeta;
    }

    public void setPlaneta(String planeta) {
        this.planeta = planeta;
    }
}

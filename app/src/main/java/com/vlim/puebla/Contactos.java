package com.vlim.puebla;

public class Contactos{
    String id;
    String contactoNombre;

    public Contactos() {
    }

    public Contactos(String i, String contactoNombre) {
        super();
        this.id = i;
        this.contactoNombre = contactoNombre;
    }
    public String getIdContacto() {
        return id;
    }
    public void setIdContacto(String id) {
        this.id = id;
    }
    public String getNombreContacto() {
        return contactoNombre;
    }
    public void setNombreContacto(String contactoNombre) {
        this.contactoNombre = contactoNombre;
    }
}
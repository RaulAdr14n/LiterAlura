package com.rauldev.literAlura.service;

public interface IConvierteDatos {
    <T> T ObtenerDatos(String json, Class<T> clase);
}

package com.rauldev.literAlura.service;

import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class ConvierteDatos implements IConvierteDatos {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public <T> T ObtenerDatos(String json, Class<T> clase) {
        try {
            JsonNode raizNodo = objectMapper.readTree(json);

            JsonNode resultados = raizNodo.path("results");

            // Verifica si el array "results" está vacío
            if (!resultados.isArray() || resultados.size() == 0) {
                return null;
            }

            JsonNode primerResultado = raizNodo.path("results").get(0);

            int id = primerResultado.path("id").asInt();
            String titulo = primerResultado.path("title").asText();

            JsonNode autor = primerResultado.path("authors").get(0);
            JsonNode languagesNode = primerResultado.path("languages");

            int cantidadDescargas = primerResultado.path("download_count").asInt();

            ObjectNode datosRequeridos = objectMapper.createObjectNode();
            datosRequeridos.put("id", id);
            datosRequeridos.set("authors", autor);
            datosRequeridos.put("title", titulo);

            if (languagesNode.isArray()) {
                ArrayNode languagesArray = objectMapper.createArrayNode();
                for (JsonNode lang : languagesNode) {
                    languagesArray.add(lang.asText());
                }
                datosRequeridos.set("languages", languagesArray);
            } else {
                datosRequeridos.put("languages", "No disponible");
            }

            datosRequeridos.put("download_count", cantidadDescargas);

            return objectMapper.treeToValue(datosRequeridos, clase);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error procesando JSON: " + e.getMessage(), e);
        }
    }
}

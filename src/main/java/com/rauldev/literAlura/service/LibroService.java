package com.rauldev.literAlura.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rauldev.literAlura.model.Autor;
import com.rauldev.literAlura.model.DatosAutor;
import com.rauldev.literAlura.model.DatosLibro;
import com.rauldev.literAlura.model.Libro;
import com.rauldev.literAlura.repository.AutorRepository;
import com.rauldev.literAlura.repository.LibroRepository;

// import jakarta.transaction.Transactional;


@Service
public class LibroService {

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private AutorRepository autorRepository;

    public void guardarLibroConAutor(DatosLibro datosLibro) {
        try {
            if (datosLibro.autor() == null) {
                System.out.println("El libro no tiene autor registrado.");
                return;
            }

            // Guardar o recuperar el autor
            DatosAutor datosAutor = datosLibro.autor();
            Autor autor = autorRepository.findByNombre(datosAutor.nombre())
                    .orElseGet(() -> {
                        Autor nuevoAutor = new Autor(datosAutor);
                        System.out.println("Guardando nuevo autor: " + datosAutor.nombre());
                        return autorRepository.save(nuevoAutor);
                    });

            // Verificar si el libro ya existe
            if (libroRepository.findByTituloContainingIgnoreCase(datosLibro.titulo()).isPresent()) {
                System.out.println("El libro ya existe en la base de datos.");
                return;
            }

            // Guardar el nuevo libro
            Libro libro = new Libro(datosLibro, autor);
            Libro libroGuardado = libroRepository.save(libro);
            System.out.println("\nLibro guardado exitosamente:");
            System.out.println(libroGuardado);

        } catch (Exception e) {
            System.out.println("Error al guardar el libro y autor: " + e.getMessage());
        }
        // DatosAutor datosAutor = autorRepository.findByNombre(datosLibro.getAutor().getNombre())
        //     .orElseGet(() -> {
        //         DatosAutor nuevoAutor = new DatosAutor();
        //         nuevoAutor = datosLibro.getAutor();
        //         System.out.println("Guardando nuevo autor: " + nuevoAutor.getNombre());
        //         return autorRepository.save(nuevoAutor);

        //     });

        //     // Guardar el nuevo libro
        //     DatosLibro libro = new DatosLibro(datosLibro, datosAutor);
        //     return (libroRepository.save(libro));

    }

}
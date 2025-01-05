package com.rauldev.literAlura.principal;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rauldev.literAlura.model.Autor;
import com.rauldev.literAlura.model.DatosLibro;
import com.rauldev.literAlura.model.Libro;
import com.rauldev.literAlura.repository.AutorRepository;
import com.rauldev.literAlura.repository.LibroRepository;
import com.rauldev.literAlura.service.ApiService;
import com.rauldev.literAlura.service.ConvierteDatos;
import com.rauldev.literAlura.service.LibroService;

@Component
public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private final String URL_BASE = "https://gutendex.com/books";

    @Autowired
    private ApiService apiService = new ApiService();

    @Autowired
    ConvierteDatos conversor = new ConvierteDatos();

    @Autowired(required = false)
    List<Libro> libros = new ArrayList<>();

    @Autowired
    private LibroService libroService;

    @Autowired
    private LibroRepository libroRepositorio;

    @Autowired
    private AutorRepository autorRepositorio;

    public void iniciar() {
        int opcion;
        do {
            mostrarMenu();
            opcion = leerOpcion();
            ejecutarOpcion(opcion);
        } while (opcion != 6);
        System.out.println("Gracias por usar el programa.");
    }

    private void mostrarMenu() {
        System.out.println("--- Menú Principal ---");
        System.out.println("1. Buscar libro por título");
        System.out.println("2. Listar libros registrados");
        System.out.println("3. Listar autores registrados");
        System.out.println("4. Listar autores vivos en determinado año");
        System.out.println("5. Listar libros por idioma");
        System.out.println("6. Salir");
        System.out.print("Seleccione una opción: ");
    }

    private int leerOpcion() {
        while (!teclado.hasNextInt()) {
            System.out.print("Por favor, ingrese un número válido: ");
            teclado.next();
        }
        return teclado.nextInt();
    }

    private void ejecutarOpcion(int opcion) {
        teclado.nextLine(); // Limpiar el buffer después de leer el entero
        switch (opcion) {
            case 1 -> buscarLibroPorTitulo();
            case 2 -> listarLibrosRegistrados();
            case 3 -> listarAutoresRegistrados();
            case 4 -> listarAutoresVivosPorAnio();
            case 5 -> listarLibrosPorIdioma();
            case 6 -> System.out.println("Saliendo...");
            default -> System.out.println("Opción no válida. Intente nuevamente.");
        }
    }

    private void buscarLibroPorTitulo() {

        try {
            // Primero hay que buscarlo en la base de datos
            System.out.print("Ingrese el título del libro a buscar: ");
            String tituloLibro = teclado.nextLine();
            if (tituloLibro.isEmpty()) {
                System.out.println("El título no puede estar vacío.");
                return;
            }
            // Primero hay que buscarlo en la base de datos
            Optional<Libro> libroLocal = libroRepositorio.findByTituloContainingIgnoreCase(tituloLibro);
            if (libroLocal.isPresent()) {
                System.out.println("\nLibro encontrado en la base de datos:");
                System.out.println(libroLocal.get());
                return;
            }
            // De no encontrarse entonces buscarlo en la web
            String urlBusqueda = URL_BASE + "/?search=" + URLEncoder.encode(tituloLibro, "UTF-8");
            var json = apiService.obtenerDatos(urlBusqueda);

            if (json == null || json.isEmpty()) {
                System.out.println("No se ha encontrado el libro.");
            } else {
                var libro = conversor.ObtenerDatos(json, DatosLibro.class);
                if (libro == null) {
                    System.out.println("No se ha encontrado el libro.");
                } else {
                    // libros.add(libro); // Añadir a la lista local
                    System.out.println("Libro encontrado en la web:" + libro);
                    libroService.guardarLibroConAutor(libro); // Guardar en la base de datos
                    System.out.println("El libro se guardó correctamente en la base de datos:");
                    System.out.println(libro);

                }
            }
        } catch (UnsupportedEncodingException e) {
            System.err.println("Error al codificar el texto: " + e.getMessage());
        }
    }

    private void listarLibrosRegistrados() {
        System.out.println("---Libros registrados---");
        List<Libro> libros = libroRepositorio.findAll();
        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados en la base de datos.");
            return;
        }
        libros.forEach(lib -> {
            System.out.println("---" + lib.getTitulo() + "---");
            System.out.println("Autor: " + lib.getAutor().getNombre());
            System.out.println("Fecha de nacimiento: " + (lib.getAutor().getFechaNacimiento()));
            System.out.println("Fecha de fallecimiento: " + (lib.getAutor().getFechaFallecimiento()));
            System.out.println("Cantidad de descargas: " + lib.getCantidadDescargas());
            System.out.println("----------------------------");
        });
    }

    private void listarAutoresRegistrados() {
        System.out.println("--- Autores registrados ---");
        List<Autor> autores = autorRepositorio.findAutoresConLibros();
        if (autores.isEmpty()) {
            System.out.println("No hay autores registrados en la base de datos.");
            return;
        }

        for (Autor autor : autores) {
            String fechaNacimiento = autor.getFechaNacimiento() != null ? autor.getFechaNacimiento() : "No disponible";
            String fechaFallecimiento = autor.getFechaFallecimiento() != null ? autor.getFechaFallecimiento()
                    : "No disponible";

            System.out.printf("Autor: %s | Fecha de nacimiento: %s | Fecha de fallecimiento: %s%n",
                    autor.getNombre(), fechaNacimiento, fechaFallecimiento);

            System.out.println("Libros:");
            autor.getLibros().forEach(libro -> System.out.printf("- %s%n", libro.getTitulo()));
            System.out.println("--------------------------------------------------");
        }
    }

    private boolean estaVivoEnAnio(Autor autor, int anio) {
        int anioNacimiento = Integer.parseInt(autor.getFechaNacimiento().substring(0, 4));
        Integer anioFallecimiento = autor.getFechaFallecimiento() != null
                ? Integer.parseInt(autor.getFechaFallecimiento().substring(0, 4))
                : null;

        return anio >= anioNacimiento && (anioFallecimiento == null || anio <= anioFallecimiento);
    }

    private void listarAutoresVivosPorAnio() {
        System.out.println("--- Autores vivos por año ---");
        System.out.print("Ingrese el año para consultar: ");

        try {
            int anio = Integer.parseInt(teclado.nextLine().trim());

            if (anio < 0 || anio > 2024) {
                System.out.println("Por favor, ingrese un año válido.");
                return;
            }

            List<Autor> autores = autorRepositorio.findAll();
            List<Autor> autoresVivos = autores.stream()
                    .filter(autor -> estaVivoEnAnio(autor, anio))
                    .toList();

            if (autoresVivos.isEmpty()) {
                System.out.println("No se encontraron autores vivos para el año especificado.");
            } else {
                System.out.println("Autores vivos en " + anio + ":");
                autoresVivos.forEach(autor -> {
                    String fechaNacimiento = autor.getFechaNacimiento();
                    String fechaFallecimiento = autor.getFechaFallecimiento() != null ? autor.getFechaFallecimiento()
                            : "Presente";
                    System.out.printf("- %s (Nacimiento: %s, Fallecimiento: %s)%n",
                            autor.getNombre(), fechaNacimiento, fechaFallecimiento);
                });

            }

        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese un año válido en formato numérico.");
        } catch (Exception e) {
            System.out.println("Ocurrió un error: " + e.getMessage());
        }
    }

    private void listarLibrosPorIdioma() {
        System.out.println("--- Libros por idioma ---");
        System.out.println("Ingrese el código del idioma para filtrar (por ejemplo, es, en, zh):");
        String idioma = teclado.nextLine().trim().toLowerCase();

        List<Libro> libros = libroRepositorio.findAll();
        List<Libro> librosFiltrados = libros.stream()
                .filter(libro -> libro.getIdioma().equalsIgnoreCase(idioma))
                .toList();

        if (librosFiltrados.isEmpty()) {
            System.out.println("No se encontraron libros en el idioma: " + idioma);
            return;
        }

        System.out.println("\nLibros disponibles en " + obtenerNombreIdioma(idioma) + ":");
        librosFiltrados.forEach(libro -> System.out.printf("- %s (Autor: %s)%n",
                libro.getTitulo(),
                libro.getAutor().getNombre()));
    }

    private String obtenerNombreIdioma(String codigo) {
        Map<String, String> idiomas = Map.ofEntries(
                Map.entry("es", "Español"),
                Map.entry("en", "Inglés"),
                Map.entry("fr", "Francés"),
                Map.entry("pt", "Portugués"),
                Map.entry("zh", "Chino"),
                Map.entry("de", "Alemán"),
                Map.entry("it", "Italiano"),
                Map.entry("ja", "Japonés"),
                Map.entry("ru", "Ruso"),
                Map.entry("ko", "Coreano"),
                Map.entry("ar", "Árabe"),
                Map.entry("hi", "Hindi"),
                Map.entry("bn", "Bengalí"),
                Map.entry("ms", "Malayo"),
                Map.entry("tr", "Turco"),
                Map.entry("nl", "Holandés"),
                Map.entry("sv", "Sueco"),
                Map.entry("pl", "Polaco"),
                Map.entry("fi", "Finlandés"),
                Map.entry("no", "Noruego"),
                Map.entry("da", "Danés"),
                Map.entry("cs", "Checo"),
                Map.entry("el", "Griego"),
                Map.entry("th", "Tailandés"),
                Map.entry("vi", "Vietnamita"),
                Map.entry("he", "Hebreo"),
                Map.entry("id", "Indonesio"),
                Map.entry("uk", "Ucraniano"),
                Map.entry("hu", "Húngaro"),
                Map.entry("ro", "Rumano"),
                Map.entry("sk", "Eslovaco"),
                Map.entry("bg", "Búlgaro"),
                Map.entry("sr", "Serbio"),
                Map.entry("hr", "Croata"),
                Map.entry("sl", "Esloveno"));
        return idiomas.getOrDefault(codigo, codigo);
    }

}

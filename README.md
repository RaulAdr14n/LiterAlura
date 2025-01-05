# LiterAlura

**LiterAlura** es una aplicación que guarda datos de libros en una base de datos PostgreSQL utilizando Spring Boot y JPA. La información de los libros se obtiene de la API de [Gutendex](https://www.gutendex.com/), que proporciona datos sobre libros de dominio público.

## Tecnologías utilizadas

- **Java**: Lenguaje de programación principal.
- **Spring Boot**: Framework para desarrollo rápido de aplicaciones Java.
- **Spring Data JPA**: Para el manejo de la base de datos usando el estándar JPA.
- **PostgreSQL**: Base de datos para almacenar la información de los libros y autores.
- **Gutendex API**: API para obtener información de libros.

## Funcionalidades

El programa cuenta con un menú con las siguientes opciones:

1. **Buscar libro por título**  
   Esta opción permite buscar un libro en la API de Gutendex utilizando el título proporcionado. El programa devolverá el primer libro encontrado junto con el primer autor disponible.

2. **Listar libros registrados**  
   Muestra todos los libros que han sido registrados en la base de datos, mostrando el título, autor, idioma y cantidad de descargas.

3. **Listar autores registrados**  
   Muestra los autores registrados en la base de datos, con detalles sobre su nombre, fecha de nacimiento y fallecimiento, y los libros asociados a ellos.

4. **Listar autores vivos por año**  
   Permite filtrar autores vivos en un año específico, mostrando aquellos cuya fecha de nacimiento y fallecimiento (si corresponde) se ajusten al año consultado.

5. **Listar libros por idioma**  
   Filtra los libros registrados por idioma. El usuario ingresa un código de idioma (por ejemplo, "es" para español, "en" para inglés) y se muestran todos los libros disponibles en ese idioma.

## Cómo ejecutar el proyecto

1. **Clonar el repositorio:**

   ```bash
   git clone https://github.com/tu-usuario/literalura.git
   cd literalura

package com.aluracursos.literalura.principal;

import com.aluracursos.literalura.model.Datos;
import com.aluracursos.literalura.model.DatosLibros;
import com.aluracursos.literalura.service.ConsumoAPI;
import com.aluracursos.literalura.service.ConvierteDatos;

import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private static final String URL_BASE = "http://gutendex.com/books/";
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();
    private Scanner teclado = new Scanner(System.in);
    public void muestraElMenu(){
/*
        //Hacer while para mostrar el menu con un swich
        boolean bandera = true;
        while(bandera)
        {
            System.out.println("Biblioteca Alura");
            System.out.println("""
                    ****************************************
                    Elija la opcion a traves de su número:
                    1)Buscar libro por titulo
                    2)Listas libros Registrados
                    3)Listar autores registrados
                    4)Listar autores vivos en un determinado año
                    5)Listar libros por idioma
                    0)Salir
                    *********************************************
                    """);
            System.out.print("INGRESE UNA OPCION VALIDA: ");
            Scanner leer = new Scanner(System.in);
            char opcion = leer.next() .charAt(0);
            switch(opcion){
                case '1'-> {//Buscar libros por titulo

                }
                case '2' ->
                case '3' ->
                case '4'->
                case '5' ->
                case '0' ->{
                    System.out.println("CERRANDO PROGRAMA");
                    bandera = false;
                }
                default -> System.out.println("OPCION INCORRECTA");
            }
*/
        //Muestra todos los datos de la API
        var json = consumoAPI.obtenerDatos(URL_BASE );
        System.out.println(json);
        var datos = conversor.obtenerDatos(json, Datos.class);
        System.out.println(datos);

        //Top de los 10 libros mas descargados
        System.out.println("Top de los 10 libros mas descargados");
        datos.resultados().stream()
                .sorted(Comparator.comparing(DatosLibros::numeroDeDescargas).reversed() )
                .limit(10)
                .map(l -> l.titulo().toUpperCase())
                .forEach(System.out::println);

        //Busqueda de libros por nombre
        System.out.println("Ingrese el nombre del libro que desee buscar");
        var tituloLibro = teclado.nextLine();;
        json = consumoAPI.obtenerDatos(URL_BASE+"?search="+ tituloLibro.replace(" ","+"));
        var datosBusqueda = conversor.obtenerDatos(json,Datos.class );
        Optional<DatosLibros> libroBuscado = datosBusqueda.resultados().stream()
                .filter(l -> l.titulo().toUpperCase().contains(tituloLibro.toUpperCase() ))
                .findFirst();
        if(libroBuscado.isPresent()){
            System.out.println("Libro Encontrado");
            System.out.println(libroBuscado.get());
        }else{
            System.out.println("Libro No Encontrado");
        }

        //Trabajando con estadisticas
        DoubleSummaryStatistics est = datos.resultados().stream()
                .filter(d -> d.numeroDeDescargas()>0)
                .collect(Collectors.summarizingDouble(DatosLibros::numeroDeDescargas ) );
        System.out.println("Cantidad media de descargas: " + est.getAverage());
        System.out.println("Cantidad maxima de descargas: " + est.getMax());
        System.out.println("Cantidad minima de descargas: " + est.getMin());
        System.out.println("Cantidad se registros base: " + est.getCount() );
    }
}

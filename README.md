# Sumo Combat Simulator

Sistema de simulacion de combate de Sumo basado en arquitectura Cliente-Servidor. El proyecto utiliza Sockets TCP para la comunicacion de red y gestion de concurrencia mediante hilos (Multithreading) para el procesamiento en tiempo real.

---

## Especificaciones Tecnicas
* Lenguaje: Java 17+
* Arquitectura: Modelo-Vista-Controlador (MVC)
* Comunicacion: Sockets TCP/IP
* Persistencia: Archivos de configuracion .properties

---

## Estructura del Proyecto
* Servidor: Gestiona el area de combate (Dohyo) y la logica de arbitraje mediante la clase HiloCombate para el calculo de colisiones.
* Cliente: Implementacion de los luchadores (Rikishi) con gestion de estados y transmision de coordenadas al servidor.
* Recursos: Carpeta Data para la carga dinamica de atributos de los combatientes.

---

## Guia de Inicio
1. Ejecutar la clase LauncherServidor.java para inicializar el puerto de escucha.
2. Ejecutar dos instancias de LauncherCliente.java para establecer la conexion de los luchadores.
3. El sistema sincronizara los datos de combate automaticamente desde el directorio de recursos.

---

## Desarrolladores
* Anyelo Esteban Casas Zapata
* Diego Yañes
* Sebastian Zambrano


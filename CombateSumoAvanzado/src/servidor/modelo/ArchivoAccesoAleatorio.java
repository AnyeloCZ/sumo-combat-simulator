package servidor.modelo;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Gestiona el archivo de acceso aleatorio que persiste resultados de combates.
 * Formato de registro fijo: nombre1(50) + nombre2(50) + resultado1(10) + resultado2(10) + victorias1(int) + victorias2(int)
 * Cada char ocupa 2 bytes con writeChars.
 *
 * @author Sebastian Zambrano - 20251020102, Anyelo Casas - 20251020106, Diego Yañes - 20251020103
 * @version 1.0
 */
public class ArchivoAccesoAleatorio {

    /** Ruta del archivo RAF. */
    private static final String RUTA = "Data/resultados_combates.dat";
    /** Tamano campo nombre en chars. */
    private static final int TAM_NOMBRE    = 50;
    /** Tamano campo resultado en chars. */
    private static final int TAM_RESULTADO = 10;
    /** Bytes por registro: (50+50+10+10)*2 bytes/char + 4+4 bytes int. */
    private static final int TAM_REGISTRO  = (TAM_NOMBRE * 2 + TAM_NOMBRE * 2 +
                                               TAM_RESULTADO * 2 + TAM_RESULTADO * 2) + 8;

    /**
     * Escribe el resultado de un combate en la posicion correspondiente.
     * @param numCombate Numero del combate (1, 2 o 3).
     * @param nombre1    Nombre del primer luchador.
     * @param nombre2    Nombre del segundo luchador.
     * @param resultado1 "GANADOR" o "PERDEDOR" del primero.
     * @param resultado2 "GANADOR" o "PERDEDOR" del segundo.
     * @param victorias1 Victorias del primero.
     * @param victorias2 Victorias del segundo.
     * @throws IOException Si ocurre error al escribir.
     */
    public void escribirResultado(int numCombate, String nombre1, String nombre2,
                                   String resultado1, String resultado2,
                                   int victorias1, int victorias2) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(RUTA, "rw")) {
            raf.seek((long)(numCombate - 1) * TAM_REGISTRO);
            escribirCampo(raf, nombre1,    TAM_NOMBRE);
            escribirCampo(raf, nombre2,    TAM_NOMBRE);
            escribirCampo(raf, resultado1, TAM_RESULTADO);
            escribirCampo(raf, resultado2, TAM_RESULTADO);
            raf.writeInt(victorias1);
            raf.writeInt(victorias2);
        }
    }

    /**
     * Lee todos los registros del RAF.
     * @param totalCombates Total de combates a leer.
     * @return Contenido como texto formateado.
     * @throws IOException Si ocurre error al leer.
     */
    public String leerTodos(int totalCombates) throws IOException {
        StringBuilder sb = new StringBuilder("=== RESULTADOS DE LOS COMBATES ===\n");
        try (RandomAccessFile raf = new RandomAccessFile(RUTA, "r")) {
            for (int i = 0; i < totalCombates; i++) {
                raf.seek((long) i * TAM_REGISTRO);
                String n1 = leerCampo(raf, TAM_NOMBRE);
                String n2 = leerCampo(raf, TAM_NOMBRE);
                String r1 = leerCampo(raf, TAM_RESULTADO);
                String r2 = leerCampo(raf, TAM_RESULTADO);
                int v1    = raf.readInt();
                int v2    = raf.readInt();
                sb.append("Combate ").append(i + 1).append(":\n")
                  .append("  ").append(n1).append(" -> ").append(r1).append(" (V:").append(v1).append(")\n")
                  .append("  ").append(n2).append(" -> ").append(r2).append(" (V:").append(v2).append(")\n");
            }
        }
        return sb.toString();
    }

    /**
     * Escribe un campo de longitud fija rellenando con espacios.
     * @param raf    Archivo RAF.
     * @param texto  Texto a escribir.
     * @param tamano Tamano fijo en caracteres.
     * @throws IOException Si ocurre error.
     */
    private void escribirCampo(RandomAccessFile raf, String texto, int tamano) throws IOException {
        StringBuilder sb = new StringBuilder(texto);
        while (sb.length() < tamano) sb.append(' ');
        raf.writeChars(sb.substring(0, tamano));
    }

    /**
     * Lee un campo de longitud fija del RAF.
     * @param raf    Archivo RAF.
     * @param tamano Numero de caracteres a leer.
     * @return Texto sin espacios finales.
     * @throws IOException Si ocurre error.
     */
    private String leerCampo(RandomAccessFile raf, int tamano) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tamano; i++) sb.append(raf.readChar());
        return sb.toString().trim();
    }
}

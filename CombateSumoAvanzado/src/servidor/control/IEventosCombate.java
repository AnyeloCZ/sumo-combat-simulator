package servidor.control;

/**
 * Interfaz que define los eventos del combate de sumo.
 * Permite que los hilos notifiquen a la vista sin conocerla directamente.
 * Cumple el principio de Inversión de Dependencias (D del SOLID).
 * Está en el paquete control porque define comportamiento de coordinación,
 * no una entidad del dominio.
 *
 * @author Sebastian Zambrano - 20251020102, Anyelo Casas - 20251020106, Diego Yañes - 20251020103
 * @version 1.0
 */
public interface IEventosCombate {

    /**
     * Notifica que un luchador ha llegado al servidor.
     *
     * @param nombre Nombre del luchador.
     * @param peso   Peso del luchador en kg.
     */
    void onLuchadorLlego(String nombre, double peso);

    /**
     * Notifica que el combate ha iniciado con los dos luchadores.
     *
     * @param nombre1 Nombre del primer luchador.
     * @param nombre2 Nombre del segundo luchador.
     */
    void onCombateIniciado(String nombre1, String nombre2);

    /**
     * Notifica que se ejecutó un turno en el combate.
     *
     * @param atacante Nombre del luchador que atacó.
     * @param kimarite Nombre de la técnica utilizada.
     */
    void onTurnoEjecutado(String atacante, String kimarite);

    /**
     * Notifica que el combate ha terminado con un ganador.
     *
     * @param ganador   Nombre del luchador ganador.
     * @param peso      Peso del ganador.
     * @param victorias Victorias acumuladas del ganador.
     */
    void onCombateTerminado(String ganador, double peso, int victorias);

    /**
     * Notifica un error ocurrido durante el combate.
     *
     * @param mensaje Descripción del error.
     */
    void onError(String mensaje);
}

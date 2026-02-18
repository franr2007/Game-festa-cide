package pacman;

/**
 * Clase HiloReset, se asegura de reiniciar el juego cuando los hilos de los
 * personajes hayan terminado.
 *
 * @author Carlos Aguirre.
 */
public class HiloReset implements Runnable {

    private Juego padre;
    private byte reinicioCompleto;

    /**
     * Constructor de la clase HiloReset. Obtiene como parámetro el padre para
     * poder comunicarse con el.
     *
     * @param padre Instancia de la clase Juego.
     */
    public HiloReset(Juego padre, byte reinicioCompleto) {
        this.padre = padre;
        this.reinicioCompleto = reinicioCompleto;
    }

    /**
     * Comprueba que solo queda un hilo con vida, este, entonces reinicia el
     * juego y termina este hilo.
     */
    @Override
    public void run() {
        while (padre.quedaMasDeUnHiloVivo()) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {
                System.out.println("ERROR: Al intentar dormir el hilo reset." + ex);
            }
        }
        padre.nuevoIntento(this.reinicioCompleto);
    }
}

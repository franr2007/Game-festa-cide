package pacman;

/**
 * Clase HiloPersonaje. Este puede ser un fantasma o un comecocos.
 *
 * @author Carlos, Millán.
 */
public class HiloPersonaje implements Runnable {

    private final int id;
    private final int personaje;
    private int y;
    private final int yInicial;
    private int x;
    private final int xInicial;
    private byte direccion;
    private Juego juego;
    private long velocidad;
    private boolean vivo;
    private boolean comido;

    /**
     * Contructor de la clase Personaje. Define el estado del objeto.
     *
     * @param y Coordenada Y en la que se encuentra.
     * @param x Coordenada X en la que se encuentra.
     * @param padre Instancia de la clase Juego para poder comunicarse con el.
     * @param id En el caso de los fantasmas los identifica para posteriormente
     * poder darles un color.
     * @param personaje El personaje del que se trata. 2 PacMan, 3 fantasma.
     * @param velocidad Frecuencia a la que se mueve el personaje.
     */
    public HiloPersonaje(int y, int x, Juego padre, int id, int personaje, long velocidad) {
        this.id = id;
        this.personaje = personaje;

        if (personaje == Juego.FANTASMA) {
            this.direccion = padre.getValorAleatorio(4);
        }

        this.x = x;
        this.xInicial = x;
        this.y = y;
        this.yInicial = y;
        this.juego = padre;
        this.velocidad = velocidad;
        this.direccion = Juego.QUIETO;
        this.vivo = true;
    }

    /**
     * Obtiene la direccion a la que se dirige el personaje.
     *
     * @return byte Con la dirección.
     */
    public byte getDireccion() {
        return this.direccion;
    }

    /**
     * Obtiene el id. Solo para los fantasmas.
     *
     * @return entero Con el id del fantasma.
     */
    public int getId() {
        return this.id;
    }

    /**
     * Obtiene la coordenada Y en la que se encuentra.
     *
     * @return entero Con la coordenada Y.
     */
    public int getY() {
        return this.y;
    }

    /**
     * Obtiene la coordenada X en la que se encuentra.
     *
     * @return entero Con la coordenada X.
     */
    public int getX() {
        return this.x;
    }

    /**
     * Establece la nueva dirección que va a tomar el personaje.
     *
     * @param direccion La nueva dirección.
     */
    public void setDireccion(byte direccion) {
        this.direccion = direccion;
    }

    /**
     * Define las coordenadas del personaje.
     *
     * @param y Coordenada Y.
     * @param x Coordenada X.
     */
    public void setYX(int y, int x) {
        this.y = y;
        this.x = x;
    }

    /**
     * Acaba con la vida util del hilo.
     */
    public void matarHilo() {
        this.x = xInicial;
        this.y = yInicial;
        this.direccion = Juego.QUIETO;
        this.vivo = false;
    }

    /**
     * Si se trata de un fantasma y es comido, este se situa en la coordenada
     * 0,0 la cual no se refresca ya que es un bloque. En otras palabras solo
     * aparta al fantasma del tablero.
     */
    public void comido() {
        this.x = 0;
        this.y = 0;
    }

    /**
     * Mueve el personaje en una dirección mientras esté vivo.
     */
    @Override
    public void run() {
        try {
            while (vivo) {
                Thread.sleep(velocidad);

                // Si no ha sido comido/apartado.
                if (!(this.x == 0 && this.y == 0)) {
                    juego.accionPersonaje(y, x, personaje, direccion, id);
                }
            }

        } catch (InterruptedException ex) {
            System.out.println("ERROR - Con HiloPersonaje " + ex);
        }

        this.vivo = true;
    }
}

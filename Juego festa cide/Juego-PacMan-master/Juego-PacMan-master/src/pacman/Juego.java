package pacman;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

/**
 * Juego del PacMan. Trabajo de clase.
 *
 * @version 11/03/2019
 * @author DaPeLle, Carlos, Millán.
 */
public class Juego extends JFrame {

    // Control de guardado.
    private GuardarCargar controlGuardado;

    // Puntos del juego y puntos que se come el PacMan.
    private int recordAnterior;
    private int puntosTotales;
    private int cantidadPuntosMax;
    private int cantidadPuntosPorComer;

    // Los fantasmas son vulnerables?
    private boolean fantasmasVulnerables;

    // Indica si el juego está pausado o no.
    private boolean pausarHilos;

    // Control de velocidad.
    private static final long VELOCIDAD_FANTASMA = 200;
    private static final long VELOCIDAD_PACMAN = 140;

    // Direcciones de movimiento de pacman y fantasmas.
    public static final byte QUIETO = 0;
    public static final byte ARRIBA = 1;
    public static final byte ABAJO = 2;
    public static final byte IZQUIERDA = 3;
    public static final byte DERECHA = 4;

    // Constantes usadas para el metodo comprobar casilla, este dice que accion o acciones se van a realizar.
    private static final byte PUEDES_MOVERTE = 0;
    private static final byte CHOQUE_PARED = 1;
    private static final byte CHOQUE_IGUALES = 2;
    private static final byte ELIMINADO = 3;

    // Elementos del tablero tablero principal.
    private static final byte PARED = 0;
    private static final byte VACIO = 1;
    public static final byte PACMAN = 2;
    public static final byte FANTASMA = 3;

    // Colores.
    private final Color COLOR_FONDO_ESCENARIO = Color.black;
    private final Color COLOR_FONDO_ESCENARIO_FIN = Color.blue;
    private final Color COLOR_PANEL_INFORMATIVO = Color.black;
    private final Color COLOR_LETRA = Color.white;

    // Elementos swing.
    private JPanel panelCentral, panelSur, panelPuntos, panelVidas;
    private JLabel lblVida, lblTextoPuntos, lblPuntos;
    private JLabel[] coleccionImgVidas;
    private String tituloVentana = "PacMan";

    // Fuente de texto
    private final Font fuenteTexto = new Font("Default", 1, 20);

    // Imágenes.
    private ImageIcon pacmanImagen;
    private final ImageIcon coleccionImgFantasmas[] = {
        new ImageIcon(getClass().getResource("/recursos/fantasma1.png")),
        new ImageIcon(getClass().getResource("/recursos/fantasma2.png")),
        new ImageIcon(getClass().getResource("/recursos/fantasma3.png")),
        new ImageIcon(getClass().getResource("/recursos/fantasma4.png")),
        new ImageIcon(getClass().getResource("/recursos/fantasma5.png")),
        new ImageIcon(getClass().getResource("/recursos/fantasma6.png")),
        new ImageIcon(getClass().getResource("/recursos/fantasma7.png"))};
    private final ImageIcon imgCopa = new ImageIcon(getClass().getResource("/recursos/copa.png"));
    private final ImageIcon imgPacManArriba = new ImageIcon(getClass().getResource("/recursos/pacman_arriba.png"));
    private final ImageIcon imgPacManAbajo = new ImageIcon(getClass().getResource("/recursos/pacman_abajo.png"));
    private final ImageIcon imgPacManIzq = new ImageIcon(getClass().getResource("/recursos/pacman_izq.png"));
    private final ImageIcon imgPacManDcha = new ImageIcon(getClass().getResource("/recursos/pacman_dcha.png"));
    private final ImageIcon imgPacManArribaCerrada = new ImageIcon(getClass().getResource("/recursos/pacman_arriba_cerrado.png"));
    private final ImageIcon imgPacManAbajoCerrada = new ImageIcon(getClass().getResource("/recursos/pacman_abajo_cerrado.png"));
    private final ImageIcon imgPacManIzqCerrada = new ImageIcon(getClass().getResource("/recursos/pacman_izq_cerrado.png"));
    private final ImageIcon imgPacManDchaCerrada = new ImageIcon(getClass().getResource("/recursos/pacman_dcha_cerrado.png"));
    private final ImageIcon imgPacManMuerto = new ImageIcon(getClass().getResource("/recursos/pacman_muerto.png"));
    private final ImageIcon imgPared = new ImageIcon(getClass().getResource("/recursos/pared.png"));
    private final ImageIcon imgVidaPacman = new ImageIcon(getClass().getResource("/recursos/pacman_vida.png"));
    private final ImageIcon imgVidaPacmanGastada = new ImageIcon(getClass().getResource("/recursos/pacman_vida_gastada.png"));
    private final ImageIcon coleccionImgPuntos[] = {
        new ImageIcon(getClass().getResource("/recursos/suelo.png")),
        new ImageIcon(getClass().getResource("/recursos/Captura_de_pantalla_2026-02-23_171413-removebg-preview.png")),
        new ImageIcon(getClass().getResource("/recursos/Captura_de_pantalla_2026-02-23_171526-removebg-preview.png"))
    };

    // Otros.
    private final ArrayList<HiloPersonaje> coleccionFantasmas;
    private HiloPersonaje pacman;
    private byte vidasPacMan;
    private final byte vidasAlComienzoPacMan = 5;
    private boolean bocaAbierta;
    private final ThreadGroup grupoHilos;
    private Random rand;

    // Escenarios.
    private final int ESCENARIO_ACTUAL[][];
    private final int ESCENARIO_ACTUAL_PUNTOS[][];

    // ESCENARIO GENERAL (personajes y bloques). Orden Y, X.
    // 0 Pared, 1 vacio, 2 PacMan, 3 fantasma, +10 punto, +20 punto grande.
    private final static int ESCENARIO_ORIGINAL[][] = {
        {0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0},
        {0, 2, 1, 1, 1, 1, 0, 1, 1, 1, 1, 3, 0},
        {0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0},
        {0, 1, 1, 1, 0, 1, 0, 1, 0, 1, 1, 1, 0},
        {0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0},
        {0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0},
        {0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0},
        {0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0},
        {0, 1, 0, 1, 0, 1, 0, 3, 0, 1, 0, 1, 0},
        {0, 1, 1, 1, 0, 1, 0, 1, 0, 1, 1, 1, 0},
        {0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0},
        {0, 1, 3, 1, 1, 1, 0, 1, 1, 1, 1, 3, 0},
        {0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0}};

    // ESCENARIO PUNTOS (los que se comen). 0 Vacio, 1 punto, 2 punto grande.
    // Prevalece el escenario de los personajes frente a este.
    private final static int ESCENARIO_PUNTOS_ORIGINAL[][] = {
        {0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0},
        {0, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 2, 0},
        {0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0},
        {0, 1, 1, 1, 0, 1, 0, 1, 0, 1, 1, 1, 0},
        {0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0},
        {0, 1, 0, 1, 1, 1, 2, 1, 1, 1, 0, 1, 0},
        {0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0},
        {0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0},
        {0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0},
        {0, 1, 1, 1, 0, 1, 0, 1, 0, 1, 1, 1, 0},
        {0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0},
        {0, 2, 1, 1, 1, 1, 0, 1, 1, 1, 1, 2, 0},
        {0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0}};

    // Obtener el numero de filas y columas totales.
    private final int FILAS = ESCENARIO_ORIGINAL.length;
    private final int COLUMNAS = ESCENARIO_ORIGINAL[0].length;

    /**
     * Constructor de la clase Juego.Inicializa todos los elementos para
     * conformar el juego.
     *
     * @param recordAnterior El record anteriormente conseguido.
     */
    public Juego(int recordAnterior) {
        // Cargar el record anterior para saber si se ha batido.
        this.recordAnterior = recordAnterior;

        // Definir los puntos totales.
        this.puntosTotales = 0;
        this.cantidadPuntosMax = 0;
        this.cantidadPuntosPorComer = 0;

        // Definir si está pausada la partida.
        this.pausarHilos = false;

        // Definir si los fantasmas son vulnerables.
        this.fantasmasVulnerables = false;

        // Crea una coleccion de objetos de tipo HiloPersonaje, en este caso
        // va destinado a los fantasmas.
        this.coleccionFantasmas = new ArrayList();

        // Crear un nuevo escenario.
        ESCENARIO_ACTUAL = new int[FILAS][COLUMNAS];
        ESCENARIO_ACTUAL_PUNTOS = new int[FILAS][COLUMNAS];

        // Crear instancia de random.
        // Para cuando los fantasmas necesiten generar una nueva dirección.
        this.rand = new Random();

        // Crear un nuevo grupo de hilos para controlarlos más adelante.
        this.grupoHilos = new ThreadGroup("Objetos");

        // Definir vidas del pacman.
        this.vidasPacMan = vidasAlComienzoPacMan;
        this.coleccionImgVidas = new JLabel[25];

        // Dejar al pacman con la boca abierta (literal).
        this.bocaAbierta = true;

        // Crear todos los elementos de Swing.
        this.crearElementosSwing();
        this.crearDistribucionSwing();
        this.colocarElementosSwing();
        this.definirEstilosSwing();
        this.crearEscenario();
        this.controlarEventosTeclado();

        // Propiedades de la ventana Swing.
        this.setResizable(false);
        this.setTitle("PacMan");
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack(); // Compacta todo.
        this.setVisible(true);
    }

    // Crea instancias de los elementos Swing que se van a usar.
    private void crearElementosSwing() {
        this.controlGuardado = new GuardarCargar();
        this.panelCentral = new JPanel();
        this.panelSur = new JPanel();
        this.panelVidas = new JPanel();
        this.panelPuntos = new JPanel();
        this.lblVida = new JLabel("Vidas restantes");
        this.lblTextoPuntos = new JLabel("Puntos: ");
        this.lblPuntos = new JLabel("0");
    }

    // Crea la distribucion de los diferentes paneles.
    private void crearDistribucionSwing() {
        this.getContentPane().setLayout(new BorderLayout());
        this.panelCentral.setLayout(new GridLayout(FILAS, COLUMNAS));
        this.panelSur.setLayout(new GridLayout(2, 1, 5, 5));
        this.panelPuntos.setLayout(new GridLayout(1, 3));
        this.panelVidas.setLayout(new GridLayout(1, coleccionImgVidas.length));
    }

    // Coloca cada elemento en su respectivo panel.
    private void colocarElementosSwing() {
        this.getContentPane().add(panelCentral, BorderLayout.CENTER);
        this.getContentPane().add(panelSur, BorderLayout.SOUTH);
        this.panelSur.add(panelPuntos);
        this.panelPuntos.add(this.lblVida);
        this.panelPuntos.add(this.lblTextoPuntos);
        this.panelPuntos.add(this.lblPuntos);
        this.panelSur.add(panelVidas);

        // Añadir vidas al panel de vidas.
        for (int i = 0; i < this.coleccionImgVidas.length; i++) {
            this.coleccionImgVidas[i] = new JLabel();
            this.panelVidas.add(coleccionImgVidas[i]);
        }
    }

    // Define todos los estilos de Swing.
    private void definirEstilosSwing() {
        this.getContentPane().setBackground(COLOR_FONDO_ESCENARIO);
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(Juego.class.getResource("/recursos/icono.png")));
        this.panelCentral.setBackground(COLOR_FONDO_ESCENARIO);
        this.panelSur.setBackground(COLOR_PANEL_INFORMATIVO);
        this.panelPuntos.setBackground(COLOR_PANEL_INFORMATIVO);
        this.panelVidas.setBackground(COLOR_PANEL_INFORMATIVO);
        this.lblVida.setFont(this.fuenteTexto);
        this.lblVida.setForeground(COLOR_LETRA);
        this.lblTextoPuntos.setFont(this.fuenteTexto);
        this.lblTextoPuntos.setForeground(COLOR_LETRA);
        this.lblTextoPuntos.setHorizontalAlignment(SwingConstants.RIGHT);
        this.lblPuntos.setFont(this.fuenteTexto);
        this.lblPuntos.setForeground(COLOR_LETRA);
        this.panelSur.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    // Esto es lo que sucederá al pulsar alguna tecla.
    private void controlarEventosTeclado() {

        // Control de eventos de teclado para pacman.
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!pausarHilos) {
                    ArrayList<Byte> direcciones = getPosicionesValidas(pacman.getY(), pacman.getX(), pacman.getDireccion(), PACMAN);

                    switch (e.getExtendedKeyCode()) {
                        // DERECHA (D o flecha)
                        case 68:
                        case 39:
                            pacman.setDireccion(direcciones.contains(DERECHA) ? DERECHA : pacman.getDireccion());
                            break;

                        // IZQUIERDA (A o flecha)
                        case 65:
                        case 37:
                            pacman.setDireccion(direcciones.contains(IZQUIERDA) ? IZQUIERDA : pacman.getDireccion());
                            break;

                        // ABAJO (S o flecha)
                        case 83:
                        case 40:
                            pacman.setDireccion(direcciones.contains(ABAJO) ? ABAJO : pacman.getDireccion());
                            break;

                        // ARRIBA (W o flecha)
                        case 87:
                        case 38:
                            pacman.setDireccion(direcciones.contains(ARRIBA) ? ARRIBA : pacman.getDireccion());
                            break;

                        // Pulsar la letra M, esto muestra por consola el mapa logico
                        case 77:
                            mostrarEscenarios();
                            break;
                        default:
                            break;
                    }
                }
                // PAUSE (P)
                if (e.getExtendedKeyCode() == 80) {
                    Juego.this.pausarHilos = !Juego.this.pausarHilos;
                    if (!Juego.this.pausarHilos) {
                        despertarHilos();
                        Juego.this.setTitle(tituloVentana);
                    } else {
                        Juego.this.setTitle(tituloVentana + " - PAUSADO");
                    }
                }
            }
        });
    }

    // Al pulsar la letra M del teclado se muestra una imagen del tablero lógico. Solo consola.
    private void mostrarEscenarios() {
        System.out.println("Instantánea actual del mapa lógico:");
        for (int y = 0; y < FILAS; y++) {
            for (int x = 0; x < COLUMNAS; x++) {
                System.out.print(ESCENARIO_ACTUAL[y][x] + " ");
            }
            System.out.println("");
        }
    }

    // Usado SOLO para pausar el juego. Llama a todos los hilos.
    private synchronized void despertarHilos() {
        notifyAll();
    }

    /**
     * Cambia la imagen del pacman en función de su dirección. Es llamado desde
     * Hilo personaje.
     *
     * @param direccion Direccion a la que va el pacman. Esta viene representada
     * en un byte.
     */
    public synchronized void definirImagenPacman(byte direccion) {
        this.bocaAbierta = !this.bocaAbierta;

        switch (direccion) {
            case ARRIBA:
                pacmanImagen = bocaAbierta ? imgPacManArriba : imgPacManArribaCerrada;
                break;
            case ABAJO:
                pacmanImagen = bocaAbierta ? imgPacManAbajo : imgPacManAbajoCerrada;
                break;
            case IZQUIERDA:
                pacmanImagen = bocaAbierta ? imgPacManIzq : imgPacManIzqCerrada;
                break;
            case DERECHA:
                pacmanImagen = bocaAbierta ? imgPacManDcha : imgPacManDchaCerrada;
                break;
        }
    }

    // Crea y muestra el escenario.
    private void crearEscenario() {

        // Antes de nada hay que cargar el escenario original en el escenario actual.
        this.cargarEscenarioOriginal(0);

        JLabel etiqueta = null;
        int xindice = 0;
        pacmanImagen = imgPacManDcha;

        // Crear escenario.
        for (int y = 0; y < FILAS; y++) {
            for (int x = 0; x < COLUMNAS; x++) {

                // Contar los puntos que hay en el mapa de puntos.
                this.cantidadPuntosMax += ESCENARIO_PUNTOS_ORIGINAL[y][x] > 0 ? 1 : 0;

                // Obtener el elemento que hay en una coordenada concreta.
                int elemento = ESCENARIO_ACTUAL[y][x];

                if (elemento == PARED) {
                    etiqueta = new JLabel(imgPared);

                } else if (elemento == VACIO) {
                    etiqueta = new JLabel(coleccionImgPuntos[ESCENARIO_ACTUAL_PUNTOS[y][x]]);

                } else if (elemento == PACMAN) {
                    etiqueta = new JLabel(pacmanImagen);
                    pacman = new HiloPersonaje(y, x, this, 0, PACMAN, VELOCIDAD_PACMAN);
                    Thread hiloPacman = new Thread(grupoHilos, pacman);
                    hiloPacman.start();

                } else if (elemento == FANTASMA) {
                    etiqueta = new JLabel();
                    HiloPersonaje f = new HiloPersonaje(y, x, this, coleccionFantasmas.size(), FANTASMA, VELOCIDAD_FANTASMA);
                    coleccionFantasmas.add(f); // Añado el fantasma a una colección.
                }

                this.panelCentral.add(etiqueta, xindice++);
            }
        }

        // Establecer la cantidad de puntos que hay que comerse para ganar.
        this.cantidadPuntosPorComer = this.cantidadPuntosMax;

        // Arrancar todos los fantasmas.
        for (HiloPersonaje f : coleccionFantasmas) {
            Thread hf = new Thread(grupoHilos, f);
            hf.start();
        }

        // Muestra las vidas del PacMan.
        this.pintarVidasPacMan();

        // Centrar todo para que el la ventana pueda ser centrada.
        this.pack();
    }

    // Recarga de nuevo todas las imagenes siguiendo con el mapa lógico.
    private void refrescar() {
        this.lblPuntos.setText(Integer.toString(this.puntosTotales));

        JLabel etiqueta = null;
        int indiceGrid = 0;
        int objeto = 0;

        for (int y = 0; y < FILAS; y++) {
            for (int x = 0; x < COLUMNAS; x++) {
                objeto = ESCENARIO_ACTUAL[y][x];
                if (objeto == PARED) {
                    indiceGrid++;
                    continue;
                } else if (objeto == VACIO) {
                    etiqueta = (JLabel) panelCentral.getComponent(indiceGrid);
                    etiqueta.setIcon(coleccionImgPuntos[ESCENARIO_ACTUAL_PUNTOS[y][x]]);
                } else if (objeto == PACMAN) {
                    etiqueta = (JLabel) panelCentral.getComponent(indiceGrid);
                    etiqueta.setIcon(pacmanImagen);
                } else if (objeto == FANTASMA) {
                    etiqueta = (JLabel) panelCentral.getComponent(indiceGrid);
                    int i = getIDFantasma(y, x);
                    if (this.fantasmasVulnerables) {
                        etiqueta.setIcon(coleccionImgFantasmas[6]);
                    } else {
                        if (i > coleccionImgFantasmas.length - 1) {
                            etiqueta.setIcon(coleccionImgFantasmas[0]);
                        } else {
                            etiqueta.setIcon(coleccionImgFantasmas[i]);
                        }
                    }
                }
                indiceGrid++;
            }
        }
        this.validate();
        this.repaint();
    }

    // Obtiene el índice de un fantasma mediante las coordenadas X e Y.
    // Este es usado para identificar cada fantasma y asignarle un color.
    private int getIDFantasma(int y, int x) {
        for (HiloPersonaje fantasma : this.coleccionFantasmas) {
            if (fantasma.getY() == y && fantasma.getX() == x) {
                return fantasma.getId();
            }
        }
        return 0;
    }

    // Muestra las vidas del PacMan.
    private void pintarVidasPacMan() {
        for (int i = 0; i < vidasPacMan; i++) {
            this.coleccionImgVidas[i].setIcon(imgVidaPacman);
        }
        for (int i = vidasPacMan; i < coleccionImgVidas.length; i++) {
            this.coleccionImgVidas[i].setIcon(imgVidaPacmanGastada);
        }
    }

    /**
     * Controla los movimientos de un personaje en el mapa lógico y actualiza
     * las coordenadas del objeto, para que este sepa en todo momento donde
     * esta.
     *
     * @param y Coordenada Y actual del personaje.
     * @param x Coordenada X actual del personaje.
     * @param personaje Fantasma o PacMan.
     * @param direccion Dirección hacia la que va.
     * @param id Identificador del fantasma. Al PacMan le da igual.
     */
    protected synchronized void accionPersonaje(int y, int x, int personaje, byte direccion, int id) {
        if (!pausarHilos) {
            int[] accionCoordenadas = this.comprobarCasilla(y, x, personaje, direccion);
            int xPrima = accionCoordenadas[1];
            int yPrima = accionCoordenadas[2];

            switch (accionCoordenadas[0]) {
                case PUEDES_MOVERTE:
                    this.casoMePuedoMover(y, x, yPrima, xPrima, personaje, direccion, id);
                    break;
                case CHOQUE_IGUALES:
                case CHOQUE_PARED:
                    this.casoDeChoque(personaje, id, direccion);
                    break;
                case ELIMINADO:
                    this.casoEliminado(personaje, x, y, xPrima, yPrima, direccion);
                default:
            }
        }
    }

    // Al llamar a este metodo se mueve el personaje en la direccion especificada.
    private void casoMePuedoMover(int yAnterior, int xAnterior, int ySiguiente, int xSiguiente, int personaje, byte direccion, int idPersonaje) {
        ESCENARIO_ACTUAL[yAnterior][xAnterior] = VACIO;
        ESCENARIO_ACTUAL[ySiguiente][xSiguiente] = personaje;

        if (personaje == PACMAN) {
            this.pacman.setYX(ySiguiente, xSiguiente);
            this.definirImagenPacman(direccion);

            // El pacman come puntos.
            if (this.comerPuntos(ySiguiente, xSiguiente)) {
                this.hasGanado();
                return; // Para que no refresque.
            }

        } else if (personaje == FANTASMA) {
            this.coleccionFantasmas.get(idPersonaje).setYX(ySiguiente, xSiguiente);
        }

        // Para mostrar como se mueve.
        this.refrescar();
    }

    // Este es el caso en el que el personaje choca contra algo.
    private void casoDeChoque(int personaje, int id, byte direccion) {
        if (personaje == FANTASMA) { // REBOTE
            HiloPersonaje fantasma = coleccionFantasmas.get(id);
            ArrayList<Byte> coleccionDireccionesPosibles = getPosicionesValidas(fantasma.getY(), fantasma.getX(), fantasma.getDireccion(), FANTASMA);

            int i = coleccionDireccionesPosibles.size();
            if (i == 0) { // EXCEPCION - Este caso es en el que un fantasma choca contra otro y solo puede volver por donde ha venido.
                fantasma.setDireccion(direccionContraria(direccion));
            } else {
                int valor = this.getValorAleatorio(i);
                fantasma.setDireccion(coleccionDireccionesPosibles.get(valor));
            }
        }
        if (personaje == PACMAN) {
            this.pacman.setDireccion(QUIETO);
        }
    }

    // Lo que sucede si eres eliminado por un fantasma.
    private void casoEliminado(int personaje, int xAnterior, int yAnterior, int xSiguiente, int ySiguiente, byte direccion) {
        if (personaje == PACMAN || personaje == FANTASMA) {

            if (this.fantasmasVulnerables) {
                if (this.comerPuntos(yAnterior, xAnterior)) {
                    this.hasGanado();
                    return; // Para que no continue por aqui.
                }
                this.puntosTotales += 100;

                if (personaje == PACMAN) {
                    // En el caso de que el PacMan se coma un fantasma.
                    ESCENARIO_ACTUAL[yAnterior][xAnterior] = VACIO;
                    ESCENARIO_ACTUAL[ySiguiente][xSiguiente] = PACMAN;
                    this.pacman.setYX(ySiguiente, xSiguiente);
                    this.definirImagenPacman(direccion);
                    this.refrescar();

                    for (HiloPersonaje fantasma : this.coleccionFantasmas) {
                        if (fantasma.getX() == xSiguiente && fantasma.getY() == ySiguiente) {
                            fantasma.comido();
                        }
                    }

                    // El pacman puede comer puntos y fantasmas a la vez, que gloton.
                    if (this.comerPuntos(ySiguiente, xSiguiente)) {
                        this.hasGanado();
                        return; // Para que no refresque.
                    }

                } else {
                    // En caso de que sea el fantasma el que choca con PacMan.
                    for (HiloPersonaje fantasma : this.coleccionFantasmas) {
                        if (fantasma.getX() == xAnterior && fantasma.getY() == yAnterior) {
                            fantasma.comido();
                            ESCENARIO_ACTUAL[yAnterior][xAnterior] = VACIO;
                        }
                    }
                }

            } else {
                // Fin de partida.
                this.pausarHilos = true;

                // Si es el pacman que se coma el punto en el que estaba.
                if (personaje == PACMAN) {
                    if (this.comerPuntos(yAnterior, xAnterior)) {
                        this.hasGanado();
                        return; // Para que no continue por aqui.
                    }
                }

                // Dibujar escenario fin con pacman acabado
                ESCENARIO_ACTUAL[yAnterior][xAnterior] = VACIO;
                ESCENARIO_ACTUAL[ySiguiente][xSiguiente] = 2;
                this.pacmanImagen = imgPacManMuerto;

                // Mostrar como es eliminado.
                this.refrescar();

                // Reiniciar objetos.
                this.finalizarHilos();

                // Comprobar si le quedan vidas o no.
                if (vidasPacMan > 0) {
                    this.vidasPacMan--;
                    this.pintarVidasPacMan();
                    this.parpadeoPantalla((byte) 3);
                    this.crearHiloReset((byte) 1);
                } else {
                    this.panelCentral.setBackground(COLOR_FONDO_ESCENARIO_FIN);

                    int decision = 0;
                    if (this.puntosTotales > this.recordAnterior) {
                        this.controlGuardado.guardar(this.puntosTotales);
                        decision = JOptionPane.showConfirmDialog(this, "¡NUEVO RÉCORD!\n¡Ya no te quedan más vidas!" + "\nTu puntuacion es: " + this.puntosTotales + " has batido el récord anterior." + "\n¿Deseas empezar de nuevo?", "GAME OVER!", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, imgCopa);
                    } else {
                        decision = JOptionPane.showConfirmDialog(this, "¡Ya no te quedan más vidas!" + "\nTu puntuacion es: " + this.puntosTotales + "\n¿Deseas empezar de nuevo?", "GAME OVER!", JOptionPane.YES_NO_OPTION);
                    }

                    if (decision == JOptionPane.OK_OPTION) {
                        this.crearHiloReset((byte) 0);
                    } else {
                        System.exit(0);
                    }
                }
            }
        }
    }

    // Comprueba la casilla a la que se dirige el objeto y le dice que hacer.
    // Devuelve: int[] (orden, coordenada Y, coordenada X)
    // 1º Orden - 0 se puede mover, 1 casoDeChoque pared, 2 casoDeChoque iguales, 3 comecocos casoEliminado.
    // 2º y 3º Coordenada Y, X a la que avanzará.
    private int[] comprobarCasilla(int y, int x, int personaje, byte direccion) {
        int[] resultado = {PUEDES_MOVERTE, 0, 0};
        int yFutura = y, xFutura = x, objeto;

        // Obtiene la posicion siguiente.
        // Esto es lo que permite salir de un límite y aparecer en el otro.
        switch (direccion) {
            case ARRIBA:
                yFutura = y - 1 < 0 ? FILAS - 1 : y - 1;
                break;
            case ABAJO:
                yFutura = y + 1 >= FILAS ? 0 : y + 1;
                break;
            case IZQUIERDA:
                xFutura = x - 1 < 0 ? COLUMNAS - 1 : x - 1;
                break;
            case DERECHA:
                xFutura = x + 1 >= COLUMNAS ? 0 : x + 1;
                break;
            default:
        }

        // Obtener el objeto con el cual se va a topar. Predicción.
        objeto = ESCENARIO_ACTUAL[yFutura][xFutura];

        // Interpretar los resultados.
        if (objeto == PARED) {
            resultado[0] = CHOQUE_PARED;
        } else if (objeto == VACIO) {
            resultado[0] = PUEDES_MOVERTE;
        } else {
            switch (personaje) {
                case PACMAN:
                    if (objeto == FANTASMA) {
                        resultado[0] = ELIMINADO;
                    }
                    break;
                case FANTASMA:
                    if (objeto == FANTASMA) {
                        resultado[0] = CHOQUE_IGUALES;
                    } else if (objeto == PACMAN) {
                        resultado[0] = ELIMINADO;
                    }
                    break;
            }
        }
        resultado[1] = xFutura;
        resultado[2] = yFutura;

        return resultado;
    }

    // Obtiene un ArrayList con las posiciones válidas.
    private ArrayList<Byte> getPosicionesValidas(int y, int x, byte direccion, int personaje) {
        boolean[] direcciones = new boolean[4];
        byte direccionContr = direccionContraria(direccion);
        ArrayList<Byte> coleccionDirecciones = new ArrayList<Byte>();

        // Obtener objetos adyacentes
        direcciones[0] = !isObstaculo(ESCENARIO_ACTUAL[y - 1 < 0 ? FILAS - 1 : y - 1][x], personaje); // Arriba
        direcciones[1] = !isObstaculo(ESCENARIO_ACTUAL[y + 1 >= FILAS ? 0 : y + 1][x], personaje); // Abajo
        direcciones[2] = !isObstaculo(ESCENARIO_ACTUAL[y][x - 1 < 0 ? COLUMNAS - 1 : x - 1], personaje); // Izquierda
        direcciones[3] = !isObstaculo(ESCENARIO_ACTUAL[y][x + 1 >= COLUMNAS ? 0 : x + 1], personaje); // Derecha

        // Esto permite al fantasma descartar el volver por el mismo camino por el que vino.
        if (personaje == FANTASMA) {
            for (byte i = 0; i < direcciones.length; i++) {
                if (i + 1 == direccionContr) {
                    direcciones[i] = false;
                    break;
                }
            }
        }

        // Crea un array con las direcciones posibles.
        for (byte i = 0; i < 4; i++) {
            if (direcciones[i]) {
                coleccionDirecciones.add((byte) (i + 1));
            }
        }
        return coleccionDirecciones;
    }

    // Obtiene la direccion contraria a la direccion dada.
    private byte direccionContraria(byte d) {
        if (d == ARRIBA) {
            return ABAJO;
        } else if (d == ABAJO) {
            return ARRIBA;
        } else if (d == DERECHA) {
            return IZQUIERDA;
        } else {
            return DERECHA;
        }
    }

    // Comprueba si el objeto pasado es un obstáculo para el personaje.
    private boolean isObstaculo(int objeto, int personaje) {
        if (personaje == PACMAN) {
            return objeto == PARED;
        } else {
            return objeto == PARED || objeto == FANTASMA;
        }
    }

    // Se come los puntos de una determinada coordenada, si se come todos gana.
    // Retorna true si ha ganado y false si no.
    private boolean comerPuntos(int y, int x) {
        int objetoPunto = ESCENARIO_ACTUAL_PUNTOS[y][x];
        if (objetoPunto == 1) {
            this.puntosTotales += 10;
            this.cantidadPuntosPorComer--;
        } else if (objetoPunto == 2) {
            this.puntosTotales += 20;
            this.cantidadPuntosPorComer--;

            // Hacer vulnerables a los fantasmas.
            this.fantasmasVulnerables = true;
            this.crearHiloCuentaAtras();
        }
        ESCENARIO_ACTUAL_PUNTOS[y][x] = 0;

        // Si no quedan más puntos por comer es porque has ganado.
        if (this.cantidadPuntosPorComer == 0) {
            return true;
        }
        return false;
    }

    private void crearHiloCuentaAtras() {
        HiloCuentaAtras objHilo = new HiloCuentaAtras(this);
        Thread hilo = new Thread(objHilo);
        hilo.start();
    }

    protected void setFantasmasInvulnerables() {
        this.fantasmasVulnerables = false;
    }

    // Lo que sucede cuando ganas.
    private void hasGanado() {
        this.puntosTotales += 200;
        this.refrescar(); // Para mostrar como el PacMan se come el ultimo punto.
        this.pausarHilos = true;
        this.finalizarHilos();
        this.parpadeoPantalla((byte) 4);
        this.panelCentral.setBackground(COLOR_FONDO_ESCENARIO_FIN);

        int decision = 0;
        if (this.puntosTotales > this.recordAnterior) {
            this.controlGuardado.guardar(this.puntosTotales);
            decision = JOptionPane.showConfirmDialog(this, "¡¡¡NUEVO RÉCORD!!!\nTu puntuacion es: " + this.puntosTotales + " has batido un nuevo récord." + "\n¿Continuamos?", "¡PERFECTO! !Nuevo récord!", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, imgCopa);
        } else {
            decision = JOptionPane.showConfirmDialog(this, "¡¡HAS GANADO!!\nTu puntuacion es: " + this.puntosTotales + "\n¿Continuamos?", "¡PERFECTO!", JOptionPane.YES_NO_OPTION);
        }

        if (decision == JOptionPane.OK_OPTION) {
            this.crearHiloReset((byte) 2);
        } else {
            System.exit(0);
        }
    }

    // Controla los parpadeos de la pantalla.
    private void parpadeoPantalla(byte numeroParpadeos) {
        // Parpadeo en fondo azul.
        try {
            for (int i = 0; i < numeroParpadeos; i++) {
                this.wait(200);
                panelCentral.setBackground(COLOR_FONDO_ESCENARIO_FIN);
                this.wait(200);
                panelCentral.setBackground(COLOR_FONDO_ESCENARIO);
            }
        } catch (InterruptedException ex) {
            System.out.println("ERROR - Escenario " + ex);
        }
    }

    // Avisa a los hilos para que terminen su labor.
    private void finalizarHilos() {
        // Reiniciar objetos.
        this.pacman.matarHilo();
        for (HiloPersonaje f : coleccionFantasmas) {
            f.matarHilo();
        }
    }

    /**
     * Comprueba que solo queda un hilo con vida. En este caso el hilo que
     * reinicia todo (hiloReset).
     *
     * @return true si hay hilos vivos, false si no los hay.
     */
    public boolean quedaMasDeUnHiloVivo() {
        return grupoHilos.activeCount() > 1;
    }

    /**
     * Crea un hilo el cual llama al método nuevoIntento, asegurandose antes de
     * que los otros hilos no existen.
     */
    protected void crearHiloReset(byte reinicioCompleto) {
        new Thread(new HiloReset(this, reinicioCompleto)).start();
    }

    /**
     * Si el personaje muere este vuelve a cargar el mapa y los hilos. Tipo
     * reinicio: 0 completo, 1 normal (para continuar), 2 respeta los puntos
     * (Has ganado)
     */
    protected void nuevoIntento(byte tipoReinicio) {
        // Valores por defecto.
        this.pausarHilos = false;
        this.bocaAbierta = true;
        this.panelCentral.setBackground(COLOR_FONDO_ESCENARIO);

        if (tipoReinicio == 0) {
            // REINICIO COMPLETO
            this.cantidadPuntosPorComer = this.cantidadPuntosMax;
            this.vidasPacMan = this.vidasAlComienzoPacMan;
            this.pintarVidasPacMan();
            this.puntosTotales = 0;

        } else if (tipoReinicio == 2) {
            // REINICIO PARCIAL - Has ganado.
            this.cantidadPuntosPorComer = this.cantidadPuntosMax;

            // Sumar una vida.
            if (this.vidasPacMan + 1 <= this.coleccionImgVidas.length) {
                this.vidasPacMan++;
                this.pintarVidasPacMan();
            }
        }

        // Crea hilos nuevos.
        Thread hilo;
        hilo = new Thread(grupoHilos, pacman);
        hilo.start();

        for (HiloPersonaje f : coleccionFantasmas) {
            hilo = new Thread(grupoHilos, f);
            hilo.start();
        }

        // Poner valores por defecto.
        this.setTitle(tituloVentana);
        this.pacmanImagen = imgPacManDcha;
        this.cargarEscenarioOriginal(tipoReinicio);

        this.refrescar();
    }

    // Crea una copia idéntica del escenario original en el escenario actual.
    private void cargarEscenarioOriginal(int tipoReinicio) {
        for (int y = 0; y < FILAS; y++) {
            for (int x = 0; x < COLUMNAS; x++) {
                ESCENARIO_ACTUAL[y][x] = ESCENARIO_ORIGINAL[y][x];

                if (tipoReinicio == 0 || tipoReinicio == 2) {
                    ESCENARIO_ACTUAL_PUNTOS[y][x] = ESCENARIO_PUNTOS_ORIGINAL[y][x];
                }
            }
        }
    }

    /**
     * Obtiene un valor aleatorio que va de 0 al valorLimite pasado por
     * parámetro.
     *
     * @param valorLimite Es el valor máximo que va a generar.
     * @return byte Valor generado.
     */
    public synchronized byte getValorAleatorio(int valorLimite) {
        if (valorLimite > Byte.MAX_VALUE || valorLimite < Byte.MIN_VALUE) {
            return 0;
        } else {
            return (byte) rand.nextInt(valorLimite);
        }
    }
}

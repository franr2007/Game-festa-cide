package pacman;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.Border;

/**
 * Pantalla de bienvenida.
 * 
 * Autor: Carlos Aguirre
 */
public class PantallaBienvenida extends JFrame {

    private int recordAnterior;
    private GuardarCargar controlGuardado;
    private Random rand;
    private JPanel panelGlobal, panelMenu;
    private JButton btnJugar, btnAcerca, btnSalir;
    private Icon imgIcono, imgPacman, imgFantasma1, imgFantasma2, imgCopa;
    private Icon[] coleccionPortadas;
    private final Font fuenteTexto = new Font("Default", Font.BOLD, 20);
    private JLabel lblPuntuacionMaxima, lblPortada;

    public PantallaBienvenida() {
        this.crearElementos();
        this.crearDistribucion();
        this.colocarElementos();
        this.definirEstilos();
        this.eventos();

        // Configuración de ventana
        this.setTitle("PacMan");
        this.setResizable(false);
        this.pack();
        this.setSize(526, 615);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);

        // Cargar record si existe
        this.cargarPuntuacion();
    }

    // Crear todos los elementos
    private void crearElementos() {
        this.controlGuardado = new GuardarCargar();
        this.rand = new Random();
        this.panelGlobal = new JPanel();
        this.panelMenu = new JPanel();

        // Portadas
        coleccionPortadas = new Icon[] {
            new ImageIcon(getClass().getResource("/recursos/portada1.png")),
            new ImageIcon(getClass().getResource("/recursos/portada2.png")),
            new ImageIcon(getClass().getResource("/recursos/portada3.png")),
            new ImageIcon(getClass().getResource("/recursos/portada4.png"))
        };

        // Fondo principal
        JLabel fondoGlobal = new JLabel(new ImageIcon(getClass().getResource("/recursos/fondo-removebg-preview.png")));
        fondoGlobal.setLayout(new BorderLayout());

        // Panel de botones
        panelMenu = new JPanel(new GridLayout(1, 3, 15, 0));
        panelMenu.setOpaque(false);

        // Botones
        btnJugar = new JButton("Jugar");
        btnAcerca = new JButton("Acerca");
        btnSalir = new JButton("Salir");

        panelMenu.add(btnJugar);
        panelMenu.add(btnAcerca);
        panelMenu.add(btnSalir);

        // Agregar panelMenu al fondo
        fondoGlobal.add(panelMenu, BorderLayout.SOUTH);

        // Portada y record
        lblPortada = new JLabel(coleccionPortadas[rand.nextInt(coleccionPortadas.length)], JLabel.CENTER);
        lblPuntuacionMaxima = new JLabel("", JLabel.CENTER);

        fondoGlobal.add(lblPortada, BorderLayout.CENTER);
        fondoGlobal.add(lblPuntuacionMaxima, BorderLayout.NORTH);

        // Agregar el fondo al panelGlobal
        panelGlobal.setLayout(new BorderLayout());
        panelGlobal.add(fondoGlobal, BorderLayout.CENTER);

        // Iconos
        imgIcono = new ImageIcon(getClass().getResource("/recursos/Logo_cide.png"));
        imgPacman = new ImageIcon(getClass().getResource("/recursos/pacman_dcha.png"));
        imgFantasma1 = new ImageIcon(getClass().getResource("/recursos/fantasma4.png"));
        imgFantasma2 = new ImageIcon(getClass().getResource("/recursos/fantasma7.png"));
        imgCopa = new ImageIcon(getClass().getResource("/recursos/copa.png"));
    }

    // Layout principal
    private void crearDistribucion() {
        this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
    }

    // Colocar panelGlobal en el frame
    private void colocarElementos() {
        this.getContentPane().add(panelGlobal);
    }

    // Estilos de botones y labels
    private void definirEstilos() {
        panelGlobal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        Border bordeBlanco = BorderFactory.createLineBorder(Color.white, 2);
        Color colorBoton = new Color(32, 38, 117);

        JButton[] botones = { btnJugar, btnAcerca, btnSalir };
        Icon[] iconos = { imgPacman, imgFantasma1, imgFantasma2 };

        for (int i = 0; i < botones.length; i++) {
            botones[i].setFont(fuenteTexto);
            botones[i].setForeground(Color.white);
            botones[i].setBackground(colorBoton);
            botones[i].setBorder(bordeBlanco);
            botones[i].setIcon(iconos[i]);
            botones[i].setFocusable(false);
            botones[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        lblPuntuacionMaxima.setFont(fuenteTexto);
        lblPuntuacionMaxima.setForeground(Color.white);

        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/recursos/fondo.png")));
    }

    // Eventos de botones y labels
    private void eventos() {
        btnJugar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Juego(recordAnterior);
                dispose();
            }
        });

        btnAcerca.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(PantallaBienvenida.this,
                        "Este joc es pot jugar tant amb WASD o amb las flechas de direcció per moure al pacman",
                        "Acerca de", JOptionPane.INFORMATION_MESSAGE, imgIcono);
            }
        });

        btnSalir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        lblPortada.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                lblPortada.setIcon(coleccionPortadas[rand.nextInt(coleccionPortadas.length)]);
            }
        });

        lblPuntuacionMaxima.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int decision = JOptionPane.showConfirmDialog(PantallaBienvenida.this,
                        "¿Deseas restablecer el record?", "Borrar record", JOptionPane.YES_NO_OPTION);
                if (decision == JOptionPane.OK_OPTION) {
                    controlGuardado.guardar(-1);
                    lblPuntuacionMaxima.setVisible(false);
                    recordAnterior = -1;
                }
            }
        });
    }

    // Cargar record si existe
    private void cargarPuntuacion() {
        recordAnterior = controlGuardado.cargar();
        if (recordAnterior != -1) {
            lblPuntuacionMaxima.setText("Récord: " + recordAnterior);
            lblPuntuacionMaxima.setIcon(imgCopa);
        }
    }

    // Main
    public static void main(String[] args) {
        new PantallaBienvenida();
    }
}
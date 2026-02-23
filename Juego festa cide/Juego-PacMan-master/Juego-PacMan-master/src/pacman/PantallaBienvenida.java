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
 * @author Carlos Aguirre
 */
public class PantallaBienvenida extends JFrame {

    private int recordAnterior;
    private GuardarCargar controlGuardado;
    private Random rand;
    private JPanel panelGlobal, panelMenu;
    private JButton btnJugar, btnAcerca, btnSalir;
    private Icon imgIcono, imgPacman, imgFantasma1, imgFantasma2, imgCopa;
    private Icon[] coleccionPortadas = {
        new ImageIcon(getClass().getResource("/recursos/portada1.png")),
        new ImageIcon(getClass().getResource("/recursos/portada2.png")),
        new ImageIcon(getClass().getResource("/recursos/portada3.png")),
        new ImageIcon(getClass().getResource("/recursos/portada4.png"))
    };
    private final Font fuenteTexto = new Font("Default", 1, 20);
    private JLabel lblPuntuacionMaxima, lblPortada;

    /**
     * Cosntructor de la clase. Inicializa elementos y da forma a la ventana.
     */
    public PantallaBienvenida() {
        this.crearElementos();
        this.crearDistribucion();
        this.colocarElementos();
        this.definirEstilos();
        this.eventos();

        // Propiedades de la ventana.
        this.setTitle("PacMan");
        this.setResizable(false);
        this.pack();
        this.setSize(526, 615);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);

        // Cargar puntuacion si la hay.
        this.cargarPuntuacion();
    }

    // Crea (instancia) todos los elementos necesarios.
    private void crearElementos() {
        this.controlGuardado = new GuardarCargar();
        this.rand = new Random();
        this.panelGlobal = new JPanel();
        this.panelMenu = new JPanel();
        this.btnJugar = new JButton("Jugar");
        this.btnAcerca = new JButton("Acerca");
        this.btnSalir = new JButton("Salir");
        this.imgIcono = new ImageIcon(getClass().getResource("/recursos/logo.png"));
        this.imgPacman = new ImageIcon(getClass().getResource("/recursos/pacman_dcha.png"));
        this.imgFantasma1 = Juego.escalarImg(new ImageIcon(getClass().getResource("/recursos/fantasma4.png")));
        this.imgFantasma2 = Juego.escalarImg(new ImageIcon(getClass().getResource("/recursos/fantasmaV.png"))); 
        this.imgCopa = new ImageIcon(getClass().getResource("/recursos/copa.png"));
        this.lblPuntuacionMaxima = new JLabel();
        this.lblPortada = new JLabel(coleccionPortadas[this.rand.nextInt(coleccionPortadas.length)]);
    }

    // Asigna una distribucion a cada panel.
    private void crearDistribucion() {
        this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        this.panelGlobal.setLayout(new BorderLayout());
        this.panelMenu.setLayout(new GridLayout(1, 3, 15, 0));
    }

    // Coloca los elementos en su respectivo panel.
    private void colocarElementos() {
        this.getContentPane().add(this.panelGlobal);
        this.panelGlobal.add(this.panelMenu, BorderLayout.SOUTH);
        this.panelMenu.add(this.btnJugar);
        this.panelMenu.add(this.btnAcerca);
        this.panelMenu.add(this.btnSalir);
        this.panelGlobal.add(lblPuntuacionMaxima, BorderLayout.NORTH);
        this.panelGlobal.add(lblPortada, BorderLayout.CENTER);
    }

    // Define todos los estilos.
    private void definirEstilos() {
        this.panelGlobal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        this.panelGlobal.setBackground(Color.black);
        this.panelMenu.setOpaque(false);
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(Juego.class.getResource("/recursos/icono.png")));

        Border bordeBlanco = BorderFactory.createLineBorder(Color.white, 2);
        Color colorBoton = new Color(32, 38, 117);
        this.btnJugar.setFont(fuenteTexto);
        this.btnSalir.setFont(fuenteTexto);
        this.btnJugar.setForeground(Color.white);
        this.btnJugar.setBackground(colorBoton);
        this.btnJugar.setBorder(bordeBlanco);
        this.btnAcerca.setFont(fuenteTexto);
        this.btnAcerca.setFont(fuenteTexto);
        this.btnAcerca.setForeground(Color.white);
        this.btnAcerca.setBackground(colorBoton);
        this.btnAcerca.setBorder(bordeBlanco);
        this.btnSalir.setFont(fuenteTexto);
        this.btnSalir.setFont(fuenteTexto);
        this.btnSalir.setForeground(Color.white);
        this.btnSalir.setBackground(colorBoton);
        this.btnSalir.setBorder(bordeBlanco);

        // Iconos diferentes para cada botón.
        this.btnJugar.setIcon(imgPacman);
        this.btnAcerca.setIcon(imgFantasma1);
        this.btnSalir.setIcon(imgFantasma2);

        this.btnJugar.setFocusable(false);
        this.btnAcerca.setFocusable(false);
        this.btnSalir.setFocusable(false);

        this.btnJugar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.btnAcerca.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.btnSalir.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Otros
        this.lblPuntuacionMaxima.setFont(fuenteTexto);
        this.lblPuntuacionMaxima.setForeground(Color.white);
    }

    // Controla todos los eventos.
    private void eventos() {
        this.btnJugar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                new Juego(recordAnterior);
                PantallaBienvenida.this.dispose();
            }
        });
        this.btnAcerca.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                JOptionPane.showMessageDialog(PantallaBienvenida.this, "Juego programado por: Carlos Aguirre en colaboracion con DaPelle y Millan.\nAutor original: Toru Iwatani (1980)\nVersion: 12/03/2019", "Acerca de", JOptionPane.INFORMATION_MESSAGE, imgIcono);
            }
        });
        this.btnSalir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                System.exit(0);
            }
        });
        this.lblPortada.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                lblPortada.setIcon(coleccionPortadas[rand.nextInt(coleccionPortadas.length)]);
            }
        });
        this.lblPuntuacionMaxima.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                int decision = JOptionPane.showConfirmDialog(PantallaBienvenida.this, "¿Deseas restablecer el record?", "Borrar record", JOptionPane.YES_NO_OPTION);
                if (decision == JOptionPane.OK_OPTION) {
                    controlGuardado.guardar(-1);
                    lblPuntuacionMaxima.setVisible(false);
                    recordAnterior = -1;
                }
            }
        });
    }

    // Carga la puntuacion anterior si la hay.
    private void cargarPuntuacion() {
        this.recordAnterior = this.controlGuardado.cargar();
        if (this.recordAnterior != -1) {
            this.lblPuntuacionMaxima.setText("Récord: " + this.recordAnterior);
            this.lblPuntuacionMaxima.setIcon(imgCopa);
        }
    }

    // Metodo principal.
    public static void main(String[] args) {
        new PantallaBienvenida();
    }
}

package pacman;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Clase que permite trabajar con ficheros.
 *
 * @author Carlos Aguirre
 */
public class GuardarCargar {

    private static final File ARCHIVO = new File("pacman.sav");

    public GuardarCargar() {
    }

    /**
     * Escribe la nueva puntuacion.
     *
     * @param puntuacion
     */
    protected void guardar(int puntuacion) {
        try (FileOutputStream fos = new FileOutputStream(ARCHIVO); DataOutputStream escritor = new DataOutputStream(fos);) {
            escritor.writeInt(puntuacion);
            escritor.flush();

        } catch (IOException ex) {
            System.out.println("ERROR - Al escribir el archivo.");
        }
    }

    /**
     * Carga la puntuacion anterior, si existe el archivo.
     *
     * @return Entero con la puntuación anterior.
     */
    protected int cargar() {
        try (FileInputStream fis = new FileInputStream(ARCHIVO); DataInputStream lector = new DataInputStream(fis);) {
            return lector.readInt();

        } catch (Exception ex) {
            System.out.println("ERROR - Al leer el archivo.");
        }
        return -1;
    }
}

package extras;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JInternalFrame;
import static view.FrmMain.escritorio;

public class Helper {

    public static Date sumarRestarDiasFecha(Date fecha, int dias) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha); // Configuramos la fecha que se recibe
        calendar.add(Calendar.DAY_OF_YEAR, dias);  // numero de días a añadir, o restar en caso de días<0
        return calendar.getTime(); // Devuelve el objeto Date con los nuevos días añadidos

    }

    public static void playSound(File f) {
        Runnable r = new Runnable() {
            private File f;

            public void run() {
                try {
                    playSoundInternal(this.f);

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            public Runnable setFile(File f) {
                this.f = f;
                return this;
            }
        }.setFile(f);

        new Thread(r).start();
    }

    public static void playSoundInternal(File f) throws IOException {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(f);
            try {
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                try {
                    clip.start();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    clip.drain();
                } finally {
                    clip.close();
                }
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            } finally {
                audioInputStream.close();
            }
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void ventana(JInternalFrame obj) {
        escritorio.add(obj);
        escritorio.moveToFront(obj);
        Dimension desktopSize = escritorio.getSize();
        Dimension FrameSize = obj.getSize();
        obj.setLocation((desktopSize.width - FrameSize.width) / 2, (desktopSize.height - FrameSize.height) / 2);
        obj.show();
    }

}

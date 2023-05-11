package extras;

import java.awt.event.KeyEvent;
import javax.swing.JTextField;

public class Validacion {
    
    public static void soloLetras(KeyEvent evt) {
        Character a = evt.getKeyChar();
        if (!Character.isLetter(a) && a != KeyEvent.VK_SPACE) {
            evt.consume();
        }
    }
    
    public static void soloNumerosEnteros(KeyEvent evt){
        Character num = evt.getKeyChar();
        if (num < '0' || num > '9') {
            evt.consume();
        }
    }
    
    public static void decimales(KeyEvent evt, JTextField txt){
        char caracter = evt.getKeyChar();
        if ((caracter < '0' || caracter > '9') 
                && (caracter != KeyEvent.VK_SPACE)
                && (caracter != '.')) {
            evt.consume();
        }
        if(caracter == '.' && txt.getText().contains(".")){
            evt.consume();
        }
    }
    
    public static void letrasNumeros(KeyEvent evt){
        Character a = evt.getKeyChar();
        if (!Character.isLetter(a) 
            && a != KeyEvent.VK_SPACE
            && (a < '0' || a > '9')) {
            evt.consume();
        }
    }
}

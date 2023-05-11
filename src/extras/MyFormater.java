/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package extras;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 *
 * @author Developer
 */
public class MyFormater {

    static DecimalFormatSymbols simbolo = new DecimalFormatSymbols();
    static DecimalFormat formateador = new DecimalFormat("#,###,###.##", simbolo);
    static String numeroFormateado = "";

    public static String formato(Double numeroDouble) {
        simbolo.setDecimalSeparator('.');
        simbolo.setGroupingSeparator(',');
        numeroFormateado = "$"+formateador.format(numeroDouble);
        return numeroFormateado;
    }

}

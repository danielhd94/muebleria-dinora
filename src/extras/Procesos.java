package extras;

import java.io.IOException;

public class Procesos {

    public void cargarPlantillaXlsx() {
        plantillaXlsx();
    }
    public void cargarPlantillaXls() {
        plantillaXls();
    }

    private void plantillaXlsx() {
        //ruta del archivo en el pc
        //rutal del archivo desde el src del proyecto
        String fileLocal = new String("src/Plantillas/plantilla2xlsx.xlsx");
        try {
            Runtime.getRuntime().exec("cmd /c start " + fileLocal);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void plantillaXls() {
        //ruta del archivo en el pc
        //rutal del archivo desde el src del proyecto
        String fileLocal = new String("src/Plantillas/plantilla1xls.xls");
        try {
            Runtime.getRuntime().exec("cmd /c start " + fileLocal);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

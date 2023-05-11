package Configuracion;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class Confi {

    public static Object[] cargarConfi() {
        Object[] datos = new Object[10];
        try {
            JAXBContext context = JAXBContext.newInstance(Sistema.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            String rutaConfi = "./MyConfiguration.xml";
            //JOptionPane.showMessageDialog(null,rutaConfi);
            Sistema sistema = (Sistema) unmarshaller.unmarshal(new File(rutaConfi));//new File("MyConfiguration.xml")
            System.out.print("Nombre: " + sistema.getNombre() + "\n");

            //Cargamos ahora el documento en los tipos
            // Sistema peliculerop = (Sistema) unmarshaller.unmarshal(new File("Componentes.xml"));
            //Obtenemos una instancia para obtener todas las ventas
            ArrayList<Componentes> componentes = sistema.getComponentes();
            for (Componentes c : componentes) {
                switch (Integer.parseInt(c.getId())) {
                    case 1: //FOLIO
                        datos[0] = c.getNombre();
                        System.out.println("ID: " + c.getId());
                        System.out.println("NOMBRE: " + c.getNombre());
                        System.out.println();
                        break;
                    case 2: //LOGOTIPO
                        datos[1] = c.getRutaLogo();
                        System.out.println("ID: " + c.getId());
                        System.out.println("NOMBRE: " + c.getNombre());
                        System.out.println("RUTALOGO: " + c.getRutaLogo());
                        System.out.println();
                        break;
                    case 3: //TICKET
                        datos[2] = c.getEncabezado();
                        datos[3] = c.getMensaje();
                        System.out.println("ID: " + c.getId());
                        System.out.println("NOMBRE: " + c.getNombre());
                        System.out.println("ENCABEZADO: " + c.getEncabezado());
                        System.out.println("MENSAJE: " + c.getMensaje());
                        System.out.println();
                        break;
                    case 4: //IMPUESTOS
                        datos[4] = c.getActivado();
                        System.out.println("ID: " + c.getId());
                        System.out.println("NOMBRE: " + c.getNombre());
                        System.out.println("ACTIVADO:" + c.getActivado());
                        System.out.println();
                        break;
                    case 5: //IMPRESORA
                        datos[5] = c.getImpresora();
                        System.out.println("ID: " + c.getId());
                        System.out.println("NOMBRE: " + c.getNombre());
                        System.out.println("NOMBRE IMPRESORA: " + c.getImpresora());
                        System.out.println();
                        break;
                }

            }

            /*Componentes pelicula = null;
        String result = "";
        for (int i = 0; i < peliculas.size(); i++) {
            pelicula = (Componentes) peliculas.get(i);
            
            result += "ID: " + pelicula.getId()
            + ", NOMBRE: " + pelicula.getNombre()
            + ", DIRECTOR: " + pelicula.getDirector()
            + ", GENERO: " + pelicula.getGenero()+"\n";
        }*/
            //System.out.println("Componentes: "+result);
            //JOptionPane.showMessageDialog(null, "Nombre: "+peliculero.getNombre()+"\n"+"Componentes: \n"+result);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,"Error al cargarConfi: "+ex);
        }
        return datos;
    }

    public static void guardarconfi(
            String nombreFolio,
            String rutaLogo,
            String encabezadoTicket,
            String mensajeTicket,
            String activvadoImpuestos,
            String nombreImpresora
    ) {
        try {
            JAXBContext context = JAXBContext.newInstance(Sistema.class);
            Marshaller marshaller = context.createMarshaller();

            Sistema sistema = new Sistema();
            sistema.setNombre("Mueblería Dinora");

            ArrayList<Componentes> componentes = new ArrayList();

            Componentes componente1 = new Componentes();
            Componentes componente2 = new Componentes();
            Componentes componente3 = new Componentes();
            Componentes componente4 = new Componentes();
            Componentes componente5 = new Componentes();
            //CONFIGUARACIÓN FOLIO
            componente1.setId("1");
            componente1.setNombre(nombreFolio);
            componentes.add(componente1);

            //CONFIGUARACIÓN LOGOTIPO
            componente2.setId("2");
            componente2.setNombre("logotipo");
            componente2.setRutaLogo(rutaLogo);
            componentes.add(componente2);

            //CONFIGUARACIÓN TICKET
            componente3.setId("3");
            componente3.setNombre("ticket");
            componente3.setEncabezado(encabezadoTicket);
            componente3.setMensaje(mensajeTicket);
            componentes.add(componente3);

            //CONFIGUARACIÓN IMPUESTOS
            componente4.setId("4");
            componente4.setNombre("impuestos");
            componente4.setActivado(activvadoImpuestos);
            componentes.add(componente4);

            //CONFIGUARACIÓN IMPRESORA
            componente5.setId("5");
            componente5.setNombre("impresora");
            componente5.setImpresora(nombreImpresora);
            componentes.add(componente5);

            sistema.setComponentes(componentes);
            String rutaConfi = new File("").getAbsolutePath() + "/src/MyConfiguration.xml";
                   rutaConfi = "./MyConfiguration.xml";
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(sistema, new FileWriter(rutaConfi)); //"MyConfiguration.xml"
            //marshaller.marshal(peliculero, Sistema.out);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,"Error al guardar configuracion: "+ex);
        }
    }
}

package Configuracion;

import java.util.ArrayList;
import javax.xml.bind.annotation.*;

@XmlRootElement(name = "Sistema")
@XmlType(propOrder={"nombre","componentes"})

public class Sistema {
    private String nombre;
    private ArrayList<Componentes> componentes = new ArrayList<Componentes>();

        
    //@XmlAttribute(name="nombre")
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @XmlElementWrapper(name = "componentes")
    @XmlElement(name = "componente")
    public ArrayList<Componentes> getComponentes() {
        return componentes;
    }

    public void setComponentes(ArrayList<Componentes> componentes) {
        this.componentes = componentes;
    }
}

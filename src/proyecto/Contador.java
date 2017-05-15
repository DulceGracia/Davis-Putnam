package proyecto;

import java.util.Comparator;
import java.util.Objects;

/*
 * @author Dulce Gracia 
 */
public class Contador {

    
    String nombre;
    int positivos, negativos;

    public Contador(Atomo a) {
        this.nombre = a.nombre;
        aumento(a);
    }

    public void aumento(Atomo a) {
        if (a.negado) {
            negativos++;
        } else {
            positivos++;
        }
    }

    @Override
    public String toString() {
        return "Contador{" + "nombre=" + nombre + ", positivos=" + positivos + ", negativos=" + negativos + '}';
    }

    public void aumento(Contador con) {
        negativos += con.negativos;
        positivos += con.positivos;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + Objects.hashCode(this.nombre);
        return hash;
    }

    public boolean iguales(Contador con) {
        if (this.equals(con) && con.negativos == negativos && con.positivos == positivos) {
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Contador other = (Contador) obj;
        if (!Objects.equals(this.nombre, other.nombre)) {
            return false;
        }
        return true;
    }
   

}



package proyecto;

import java.util.Objects;

/*
 * @author Dulce Gracia 
 */
public class Atomo implements Comparable<Atomo>{
    String nombre,importa="";
    boolean negado=false;
    boolean valor;
    public void negar(){
    negado=!negado;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 19 * hash + Objects.hashCode(this.nombre);
        hash = 19 * hash + (this.negado ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Atomo other = (Atomo) obj;
        if (!Objects.equals(this.nombre, other.nombre)) {
            return false;
        }
        return true;
    }

    public Atomo(String nombre) {
        this.nombre = nombre;
    }
    public Atomo(Atomo a) {
        this.nombre = a.nombre;
        this.negado=a.negado;
        this.valor=a.valor;
    }
    @Override
    public String toString(){
    return negado ? "~"+nombre : nombre;
    }

    @Override
    public int compareTo(Atomo o) {
        return nombre.compareToIgnoreCase(o.nombre);
    }
}

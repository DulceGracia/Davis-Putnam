package proyecto;

import java.util.LinkedList;

/*
 * @author Dulce Gracia 
 */
public class Clausula extends LinkedList<Atomo> {

    boolean negado = false;

    Clausula(Clausula c) {
        for (Atomo atomo : c) {
            or(atomo);
        }
        negado = c.negado;
    }

    Clausula(Atomo a) {
        this.or(a);
    }

    Clausula() {

    }

    public LinkedList<Contador> contar() {
        LinkedList<Contador> contador = new LinkedList<>();
        for (Atomo elemento : this) {
            Contador aux = new Contador(elemento);
            if (contador.contains(aux)) {
                contador.get(contador.indexOf(aux)).aumento(elemento);

            } else {
                contador.add(aux);
            }
        }
        return contador;
    }

    public void or(Atomo a) {
        add(new Atomo(a));
    }

    public void or(Clausula a) {
        addAll(new Clausula(a));
    }

    public Formula and(Clausula a) {
        Formula f = new Formula();
        f.and(a);
        f.and(this);
        return f;
    }

    public Formula negar() {
        Formula a = new Formula();
        for (Atomo atomo : this) {
            Clausula b = new Clausula();
            atomo = new Atomo(atomo);
            atomo.negar();
            b.or(atomo);
            a.and(b);
        }
        return a;
    }

    @Override
    public String toString() {
        String aux = "";
        for (int i = 0; i < size(); i++) {
            aux += get(i).toString();
            if (size() - 1 != i) {
                aux += "|";
            }
        }
        return '(' + aux + ')';
    }

}

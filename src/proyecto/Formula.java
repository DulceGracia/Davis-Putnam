package proyecto;

import java.util.LinkedList;

/*
 * @author Dulce Gracia 
 */
public class Formula extends LinkedList<Clausula> {

    boolean negado = false;

    public Formula(Formula f) {
        for (Clausula clausula : f) {
            and(clausula);
        }
    }

    public Formula(Atomo a) {
        and(new Clausula(a));

    }

    public Formula() {

    }

    public LinkedList<Contador> contar() {
        LinkedList<Contador> contador = new LinkedList<>();
        for (Clausula clausula : this) {
            LinkedList<Contador> contar = clausula.contar();
            for (Contador contador1 : contar) {
                if (contador.contains(contador1)) {
                    contador.get(contador.indexOf(contador1)).aumento(contador1);
                } else {
                    contador.add(contador1);
                }
            }
        }
        return contador;
    }

    public Formula negar() {
        LinkedList<Formula> negados = new LinkedList<>();
        Formula aux = new Formula();
        for (Clausula c : new Formula(this)) {
            negados.add(c.negar());
        }
        aux = negados.getFirst();
        for (int i = 1; i < negados.size(); i++) {
            aux = aux.or(negados.get(i));
        }
        return aux;
    }

    public void and(Clausula a) {
        add(new Clausula(a));
    }

    public Formula or(Formula f) {
        Formula or = new Formula();
        for (Clausula clausula_for1 : this) {
            for (Clausula clausula_for2 : f) {
                or.and(new Clausula(clausula_for2));
                or.getLast().or(clausula_for1);
            }
        }
        return or;
    }

    public void and(Formula a) {
        addAll(new Formula(a));
    }

    @Override
    public String toString() {
        String aux = "";
        for (int i = 0; i < size(); i++) {
            aux += get(i).toString();
            if (size() - 1 != i) {
                aux += "&";
            }
        }
        return "[" + aux + ']';
    }

}

package proyecto;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

/*
 * @author Dulce Gracia 
 */
public class calis {

    LinkedList<String> Posfijo;
    LinkedList<Formula> conjuntivo;
    LinkedList<Atomo> res;
    Comparator comp;

    public calis(LinkedList<String> Posfijo) {
        this.Posfijo = Posfijo;
        res = new LinkedList<>();
        modoConjuntivo();
        resolver();

    }

    public void resolver() {
        Formula f = new Formula(conjuntivo.getFirst());
        quitarTautologias(f);
        LinkedList<Contador> count = f.contar();
//        LinkedList<Atomo> res=new LinkedList();
        res = resolvido(f, res, count, 0,count.size());
        if (res.size() == count.size()) {
            System.out.println("EXITO");
        } else {
            System.out.println("FAIL!");
        }

        for (int i = 0; i < res.size(); i++) {
            System.out.println(res.get(i).nombre + "=" + res.get(i).valor);
        }
    }

    public LinkedList<Atomo> resolvido(Formula f, LinkedList<Atomo> res, LinkedList<Contador> count, int agregados,int numAt) {

        if (res.size() != numAt) {
            eliminarUnitaria(f, res);
            if (res.size() == numAt) {
                return res;
            }
            if (hayVacias(f)) {
                for (int i = 0; i < res.size() - agregados; i++) {
                    res.removeLast();
                }
                return res;
            }
            comp = new Comparator<Contador>() {
                @Override
                public int compare(Contador o1, Contador o2) {
                    return new Integer(o2.negativos + o2.positivos).compareTo(new Integer(o1.negativos + o1.positivos));
                }
            };
            count = f.contar();
            Collections.sort(count, comp);
            try{
                res.add(new Atomo(count.getFirst().nombre));
            }catch(Exception e){
                System.out.println(f);
            }            
            res.getLast().valor = count.getFirst().positivos > count.getFirst().negativos;            
            Formula aux = new Formula(f);
            eliminarAtomo(res.getLast(), aux);
            LinkedList<Atomo> arbol = resolvido(aux, res, count, res.size(),numAt);
            if (arbol.size() <= agregados) {
                res.getLast().valor = !res.getLast().valor;
                arbol = resolvido(aux, res, count, res.size(),numAt);
            }
            return res;
        }

        return res;
    }

    public void quitarTautologias(Formula formula) {
        Formula vacias = new Formula();
        LinkedList<Contador> conformula = formula.contar();
        for (Clausula clausula : formula) {
            LinkedList<Contador> conclausula = clausula.contar();
            for (Contador contador : conclausula) {
                Atomo aux = new Atomo(contador.nombre);
                if (contador.negativos > 0 && contador.positivos > 0) {
                    for (int i = 0; i < contador.negativos + contador.positivos; i++) {
                        clausula.remove(clausula.indexOf(aux));
                    }
                    if (conformula.get(conformula.indexOf(contador)).iguales(contador)) {
                        res.add(aux);
                        res.getLast().valor = (contador.negativos <= contador.positivos) ? true : false;
                    }
                }
                if (clausula.size() == 0) {
                    vacias.add(clausula);
                }
            }
        }
        formula.removeAll(vacias);
    }

    public void modoConjuntivo() {
        LinkedList<Formula> Exprecion = new LinkedList<>();
        Formula aux1, aux2, aux1negada, aux2negada;
        for (int i = 0; i < Posfijo.size(); i++) {
            if (Posfijo.get(i).matches("[a-zA-Z]")) {
                Exprecion.add(new Formula(new Atomo(Posfijo.get(i))));
            } else {
                switch (Posfijo.get(i)) {
                    case "~":
                        if (Exprecion.size() > 0) {
                            Exprecion.add(Exprecion.removeLast().negar());
                        } else {
                            // error
                        }
                        break;
                    case "|":
                        if (Exprecion.size() > 1) {
                            Exprecion.add(Exprecion.removeLast().or(Exprecion.removeLast()));
                        } else {
                            // error
                        }
                        break;
                    case "&":
                        if (Exprecion.size() > 1) {
                            aux1 = Exprecion.removeLast();
                            aux1.and(Exprecion.removeLast());
                            Exprecion.add(aux1);
                        } else {
                            //error
                        }
                        break;
                    case "=":
                        aux1 = Exprecion.removeLast();
                        aux2 = Exprecion.removeLast();
                        aux2negada = aux2.negar();
                        aux1negada = aux1.negar();
                        Formula primerpaso = aux1negada.or(aux2);
                        Formula segundopaso = aux2negada.or(aux1);
                        primerpaso.and(segundopaso);
                        Exprecion.add(primerpaso);
                        break;
                    case ">":
                        aux1 = Exprecion.removeLast();
                        aux2 = Exprecion.removeLast();
                        aux2negada = aux2.negar();
                        Exprecion.add(aux2negada.or(aux1));
                        break;
                }
            }
        }
        conjuntivo = Exprecion;
    }

    public void eliminarUnitaria(Formula f, LinkedList<Atomo> res) {
        // Clausula c;
        while (hayUnitarias(f)) {
            TreeSet<Atomo> atomos = new TreeSet();
            for (int i = 0; i < f.size(); i++) {
                if (f.get(i).size() == 1) {
                    LinkedList<Contador> count = f.contar();
                    for (int k = 0; k < count.size(); k++) {
                        if (f.get(i).getFirst().nombre.equals(count.get(k).nombre)) {
                            if (count.get(k).negativos > count.get(k).positivos) {
                                f.get(i).getFirst().valor = false;
                            } else {
                                f.get(i).getFirst().valor = true;
                            }
                            break;
                        }
                    }
                    atomos.add(f.get(i).get(0));
                    eliminarAtomo(f.get(i).get(0), f);
                    f.remove(f.get(i));
                    i--;
                }
            }

            for (Atomo atomo : atomos) {
                res.add(new Atomo(atomo));
            }
        }
    }

    public boolean hayUnitarias(Formula f) {
        for (int i = 0; i < f.size(); i++) {
            if (f.get(i).size() == 1) {
                return true;
            }
        }
        return false;
    }

    public boolean hayVacias(Formula f) {
        for (int i = 0; i < f.size(); i++) {
            if (f.get(i).size() == 0) {
                return true;
            }
        }
        return false;
    }

    public void eliminarAtomo(Atomo a,Formula f) {
        for (int i = 0; i < f.size(); i++) {
            for (int j = 0; j < f.get(i).size(); j++) {
                if (f.get(i).get(j).nombre.equals(a.nombre)) {
                    if(a.valor==f.get(i).get(j).negado){
                        f.get(i).remove(f.get(i).get(j));
                        j--;
                    }else{
                        j=f.get(i).size();
                        f.remove(f.get(i));                        
                        i--;                        
                    }                    
                }
            }
        }
    }
}
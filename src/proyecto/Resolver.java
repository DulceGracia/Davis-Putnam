package proyecto;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.JOptionPane;

/*
 * @author Dulce Gracia 
 */
public class Resolver {

    LinkedList<String> Posfijo;
    LinkedList<Formula> conjuntivo;
    LinkedList<Atomo> res;
    Comparator comp;
    String archivo="Resultados de la formula:\n\n";

    public Resolver(LinkedList<String> Posfijo) {
        this.Posfijo = Posfijo;
        res = new LinkedList<>();
        modoConjuntivo();
    }
//    public Resolver()
    public String resolver() {
        String mensaje="";
        Formula f = new Formula(conjuntivo.getFirst());
        Formula f2 = new Formula(conjuntivo.getFirst());
        
        quitarTautologias(f2);
        LinkedList<Contador> count = f.contar();
        LinkedList<Contador> count2 = f2.contar();
        quitarTautologias(f);

        res = resolucion(f, res, count, 0,count.size());
        if (res.size()>0) {
//            System.out.println("EXITO");
            count = f.contar();
            if(res.size()!=count2.size()){
                for (int i = res.size(); i < count2.size(); i++) {
                    res.add(new Atomo(count2.get(i).nombre));
                    res.getLast().importa="NO IMPORTA";
                }
            }
        } else {
           mensaje="Esta Expresion no tiene solucion";
          // JOptionPane.showMessageDialog(null, "Esta expresión no tiene solución.", "Error", 1);
        }

        for (int i = 0; i < res.size(); i++) {
            archivo+=res.get(i).nombre + "=" + res.get(i).valor+" "+res.get(i).importa+"\n";
//            System.out.println(res.get(i).nombre + "=" + res.get(i).valor+" "+res.get(i).importa);
        }
       return mensaje;
    }
    public Atomo getAtomo(Formula f, String nom) {
        for (int j = 0; j < f.size(); j++) {
            for (int k = 0; k < f.get(j).size(); k++) {
                if (f.get(j).get(k).nombre.equals(nom)) {
                    return f.get(j).get(k);
                }
            }
        }
        return null;
    }
    public LinkedList<Atomo> resolucion(Formula f, LinkedList<Atomo> res, LinkedList<Contador> count, int agregados,int numAt) {
        //(a|c)&(a|~b)&(~a|b)&(~b|~a)
        if (f.size()!=0) {
            eliminarUnitaria(f, res);
            if (f.size()==0) {
                return res;
            }
            
            if (hayVacias(f)) {
                return  new LinkedList<Atomo>();
            }
            //literal pura
            count = f.contar();
            
            for (int i = 0; i < count.size(); i++) {
                if (count.get(i).positivos == 0) {
                    res.add(new Atomo(getAtomo(f, count.get(i).nombre)));
                    res.getLast().valor = false;
                    eliminarAtomo(res.getLast(), f);
                    count = f.contar();
                    i--;
                } else if (count.get(i).negativos == 0) {
                    res.add(new Atomo(getAtomo(f, count.get(i).nombre)));
                    res.getLast().valor = true;
                    eliminarAtomo(res.getLast(), f);
                    count = f.contar();
                    i--;
                }
                if (f.size()==0) {
                    return res;
                }
            }
            
            count = f.contar();
            comp = new Comparator<Contador>() {
                @Override
                public int compare(Contador o1, Contador o2) {
                    return new Integer(o2.negativos + o2.positivos).compareTo(new Integer(o1.negativos + o1.positivos));
                }
            };
            
            Collections.sort(count, comp);
            try{
                res.add(new Atomo(count.getFirst().nombre));
            }catch(Exception e){
                System.out.println(""+f);
            }
            try{
                res.getLast().valor = count.getFirst().positivos >= count.getFirst().negativos;
            }catch(Exception e){
                System.out.println(f);
            }
            Formula aux = new Formula(f);
            eliminarAtomo(res.getLast(), aux);
            LinkedList<Atomo> res2=new LinkedList();
            for (Atomo aux1 : res) {
                res2.add(new Atomo(aux1));
            }
            LinkedList<Atomo> arbol = resolucion(aux, res2, count, res.size(),numAt);
            if (aux.size()!=0) {
                aux = new Formula(f);
                res.getLast().valor = !res.getLast().valor;
                eliminarAtomo(res.getLast(), aux);
                arbol = resolucion(aux, res, count, res.size(), numAt);
                f=aux;
                if(aux.size()!=0){
                    arbol=new LinkedList();
                }
                return arbol;
            } else {
                f=aux;
                
                return arbol;
            }
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
                  System.out.println("ENTRA");
                if (contador.negativos > 0 && contador.positivos > 0) {
                  
                    for (int i = 0; i < contador.negativos + contador.positivos; i++) {
                        System.out.println("Clausula x1: "+clausula);
                        clausula.remove(clausula.indexOf(aux));
                        System.out.println("clausula x2: "+clausula);
                    }
                    if (conformula.get(conformula.indexOf(contador)).iguales(contador)) {
                        res.add(aux);
                        res.getLast().valor = (contador.negativos <= contador.positivos);
                    }
                }
                if (clausula.size() == 0) {
                    vacias.add(clausula);
                }
            }
        }
        formula.removeAll(vacias);
//        LinkedList<Formula> exprs = new LinkedList();
//        Formula aux1, aux2, aux1negada, aux2negada;
//        for (int i = 0; i < Posfijo.size(); i++) {
//            if (Posfijo.get(i).matches("[a-zA-Z]")) {
//                exprs.add(new Formula(new Atomo(Posfijo.get(i))));
//            } else {
//                switch (Posfijo.get(i)) {
//                    case "~":
//                        if (exprs.size() > 0) {
//                            exprs.add(exprs.removeLast().negar());
//                        } 
//                        break;
//                    case "|":
//                        if (exprs.size() > 1) {
//                            exprs.add(exprs.removeLast().or(exprs.removeLast()));
//                        }
//                        break;
//                    case "&":
//                        if (exprs.size() > 1) {
//                            aux1 = exprs.removeLast();
//                            aux1.and(exprs.removeLast());
//                            exprs.add(aux1);
//                        }
//                        break;
//                    case "=":
//                        aux1 = exprs.removeLast();
//                        aux2 = exprs.removeLast();
//                        aux2negada = aux2.negar();
//                        aux1negada = aux1.negar();
//                        Formula primerpaso = aux1negada.or(aux2);
//                        Formula segundopaso = aux2negada.or(aux1);
//                        primerpaso.and(segundopaso);
//                        exprs.add(primerpaso);
//                        break;
//                    case ">":
//                        aux1 = exprs.removeLast();
//                        aux2 = exprs.removeLast();
//                        aux2negada = aux2.negar();
//                        exprs.add(aux2negada.or(aux1));
//                        break;
//                }
//            }
//        }
//        System.out.println(exprs);
    }

    public void modoConjuntivo() {
        LinkedList<Formula> Exprecion = new LinkedList();
        Formula aux1, aux2, aux1negada, aux2negada;
        for (int i = 0; i < Posfijo.size(); i++) {
            if (Posfijo.get(i).matches("[a-zA-Z]")) {
                Exprecion.add(new Formula(new Atomo(Posfijo.get(i))));
            } else {
                switch (Posfijo.get(i)) {
                    case "~":
                        if (Exprecion.size() > 0) {
                            Exprecion.add(Exprecion.removeLast().negar());
                        } 
                        break;
                    case "|":
                        if (Exprecion.size() > 1) {
                            Exprecion.add(Exprecion.removeLast().or(Exprecion.removeLast()));
                        }
                        break;
                    case "&":
                        if (Exprecion.size() > 1) {
                            aux1 = Exprecion.removeLast();
                            aux1.and(Exprecion.removeLast());
                            Exprecion.add(aux1);
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
        System.out.println(Exprecion);

        conjuntivo = Exprecion;
    }

    public void eliminarUnitaria(Formula f, LinkedList<Atomo> res) {
        
        while (hayUnitarias(f)) {
            TreeSet<Atomo> atomos = new TreeSet();
            for (int i = 0; i < f.size(); i++) {
                if (f.get(i).size() == 1) {
                    LinkedList<Contador> count = f.contar();
                    for (int k = 0; k < count.size(); k++) {
                        if (f.get(i).getFirst().nombre.equals(count.get(k).nombre)) {
                            f.get(i).getFirst().valor = !f.get(i).getFirst().negado;                            
                            break;
                        }
                    }
                    atomos.add(f.get(i).get(0));
                    eliminarAtomo(f.get(i).get(0), f);
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
        LinkedList<Clausula> cuello=new LinkedList();
        for (int i = 0; i < f.size(); i++) {
            for (int j = 0; j < f.get(i).size(); j++) {
                if (f.get(i).get(j).nombre.equals(a.nombre)) {
                    if(a.valor==f.get(i).get(j).negado){
                        f.get(i).remove(f.get(i).get(j));
                        j--;
                    }else{
                        j=f.get(i).size();
                        cuello.add(f.get(i));                        
                    }                    
                }
            }
        }
        f.removeAll(cuello);
    }
}

package proyecto;

import java.util.LinkedList;

/*
 * @author Dulce Gracia 
 */
public class Posfijo {

    public static LinkedList<String> convertir(String txt){
        LinkedList<String> infijo = new LinkedList<>();
        
        LinkedList<String> postfijo = new LinkedList<>();
        LinkedList<String> operadores = new LinkedList<>();
        String cadena = "";
        
        for (int i = 0; i <= txt.length(); i++) {
            if(txt.length() == i){
                infijo.add(cadena);
            }
            else if((txt.charAt(i) + "").matches("[a-zA-Z]")){
                cadena += txt.charAt(i);
                continue;
            }
            else if((txt.charAt(i) + "").matches("(~|&|\\||>|=|\\(|\\))")){
                if(cadena.matches("[a-zA-Z]+")){
                    infijo.add(cadena);
                    cadena = "";
                }
                
                infijo.add(txt.charAt(i) + "");
            }
        }
        
        for (String token : infijo) {
            if(token.matches("\\(")){
                operadores.add(token);
            }
            else if(token.matches("\\)")){
                while(!operadores.getLast().matches("\\(")){
                    postfijo.add(operadores.removeLast());
                }
                operadores.removeLast();
            }
            else if(token.matches("~|&|\\||>|=")){
                ordenPrioridad(postfijo, operadores, token);
            }
            else if(token.matches("[a-zA-Z]")){
                postfijo.add(token);
            }
        }
        while(!operadores.isEmpty()){
            postfijo.add(operadores.removeLast());
        }
        return  postfijo;
    }
    
    public static void ordenPrioridad(LinkedList<String> postfijo,LinkedList<String> operadores, String operador){
        if(operadores.isEmpty() || prioridadOperador(operador) >= prioridadOperador(operadores.getLast())){
            operadores.add(operador);
        }
        else{
            postfijo.add(operadores.removeLast());
            ordenPrioridad(postfijo, operadores, operador);
        }
    }
    
    public static int prioridadOperador(String operador){
        switch(operador){
            case "=":
                return 1;
            case ">":
                return 2;
            case "|":
                return 3;
            case "&":
                return 4;
            case "~": 
                return 5;
            default:
                return -1;
        }
    }
}

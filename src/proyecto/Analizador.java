

package proyecto;

/*
 * @author Dulce Gracia 
 */
public class Analizador {

    public static void main(String[] args) {
        new Vista();
        Atomo a=new Atomo("a");
        a.negar();
        
     Atomo   q=new Atomo("b");
        q.negar();
        
      Atomo  w=new Atomo("c");
        w.negar();
       
        Formula c=new Formula();
        c.and(new Clausula(a));
        c.and(new Clausula(q));
        c.and(new Clausula(w));
        //System.out.println(c.negar());
        

        
    }
    
}

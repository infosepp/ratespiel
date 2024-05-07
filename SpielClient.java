import javax.swing.*;
/**
 * Klasse fuer einen SpielClient
 * @author Henning Ainödhofer
 * @version 21.3.2017
 */

public class SpielClient extends Client { 
    public SpielClient(String ip, int p) {
        super(ip, p);
    }

    /**
     * Diese Methode der Server-Klasse wird hiermit ueberschrieben.
     * Der Client gibt die erhaltende Meldung, auf dem Textfeld aus.
     */

    public void processMessage(String message){
        switch(gibBefehlsbereich(message))
        {
            case "+OK":
            {
                System.out.println(gibTextbereich(message));
                break;
            }
            
            case "FLS":
            {
                System.out.println(gibTextbereich(message));
                break;
            }
            
            case "TRU":
            {
                System.out.println(gibTextbereich(message));
                break;
            }
                
            case "GHC":
            {
                highscorelisteDrucken(message);
                break;
            }
            
            case "END":
            {
                System.out.println(gibTextbereich(message));    
                break;
            }
            
            case "e1":
            {
                System.out.println(gibTextbereich(message));
                break;
            }
            
            case "e2":
            {
                System.out.println(gibTextbereich(message));
                break;
            }
            
            case "e3":
            {
                System.out.println(gibTextbereich(message));
                break;
            }
            
            default:
            {
                System.out.println("Befehl falsch. bitte richtigen Befehl eintippen.");
                break;
            }
        }
    }

    /**
     * Diese Methode gibt den Befehl zurück die die message beinhaltet
     * 
     * @param message
     * 
     * @return Befehl
     */
    private String gibBefehlsbereich(String message)
    {
        return message.split(" ")[0];
    }

    /**
     * Diese Methode gibt den Text zurück die die message beinhaltet
     * 
     * @param message
     * 
     * @return Text
     */
    private String gibTextbereich(String message)
    {
        String [] messageArray = message.split(" ");
        String text = "";
        for(int i = 1; i < messageArray.length; i++)
        {
            text = text+" "+ messageArray[i];
        }
        return text;
    }

    /**
     * Diese Methode druckt die Higscoreliste auf der Konsole aus.
     * @param message
     */
    private void highscorelisteDrucken(String message)
    {
        String [] plaetze = message.split(" ");
        for(int i = 1; i < plaetze.length; i++)
        {
            String [] eintrag = plaetze[i-1].split(":");
            System.out.println(i+". "+eintrag[0]+"\t : \t"+eintrag[1]);
        }
    }
}

import java.net.*;

/**
 * Klasse fuer einen SpielServer. Der Spielserver bietet die Möglickeit ein Spiel gegen den Server zu spielen. Bei dem Spiel muss man eine zufällige Zahl
 * zwischen 
 * @author Henning Ainödhofer
 * @version 21.03.2017
 */

public class SpielServer extends Server {

    private HighscoreGateway DBhighscore;
    private List<Spiel> spieleOnline; 
    
    public SpielServer(int p) {
        super(p);
        DBhighscore = new HighscoreGateway();
        spieleOnline = new List<>();
    }

    /**
     * Diese Methode der Server-Klasse wird hiermit ueberschrieben.
     * Der angemeldete Client bekommt die Meldung, dass er angenommen wurde.
     */

    public void processNewConnection(String pClientIP, int pClientPort){
        this.send(pClientIP, pClientPort, "+OK Verbindung hergestellt");
    }

    /**
     * Diese Methode der Server-Klasse wird hiermit ueberschrieben.
     * Der angemeldete Client bekommt die gesendete Meldung zurueckgeschickt.
     */
    public void processMessage(String pClientIP, int pClientPort, String pMessage){ 
        switch(gibBefehlsbereich(pMessage))
        {
            //Hier muss das Protokoll umgesetzt werden
            case "STR":
                {
                    if(gibTextbereich(pMessage) != "")
                    {
                      spieleOnline.append(new Spiel(pClientIP, pClientPort, gibZufallszahl(), gibTextbereich(pMessage))); 
                      this.send(pClientIP, pClientPort, "+OK Willkommen " + gibTextbereich(pMessage) + ", errate meine Zahl");
                    }
                    else
                    {
                        this.send(pClientIP, pClientPort, "-E1 Name fehlt");
                    }
                    break;
                }
            case "RAT":
                {
                   if(gibTextbereich(pMessage) != "")
                   {
                       int zahl = Integer.parseInt(gibTextbereich(pMessage));
                       if(zahl == gibZahlVonSpiel(pClientIP))
                       {
                           this.send(pClientIP, pClientPort, "TRU Die Zahl war richtig");
                           versucheErhoehenVonSpiel(pClientIP);
                           DBhighscore.hinzufuegen(this.gibNameVonSpiel(pClientIP), this.gibVersucheVonSpiel(pClientIP));
                       }
                       else if(zahl > 20 || zahl < 0)
                       {
                           this.send(pClientIP, pClientPort, " -E2 Die Zahl liegt nicht zwischen 0 und 20");
                       }
                       else
                       {
                           this.send(pClientIP, pClientPort, "FLS Die Zahl war leider falsch");
                           versucheErhoehenVonSpiel(pClientIP);
                       }
                   }
                   else
                   {
                       this.send(pClientIP, pClientPort, "-E3 Keine Zahl");
                   }
                   break; 
                }
            case "GHC":
                {
                    this.send(pClientIP, pClientPort, "GHC " + this.generiereStringAusList(DBhighscore.holeZehn()));
                    break;
                }
            case "END":
                {
                    this.send(pClientIP, pClientPort, "END Tschüss");
                    closeConnection(pClientIP, pClientPort);
                    break;
                }
                
            default:
            {
                this.send(pClientIP, pClientPort, "-E0 Falsche Angaben"); 
                break;
            }
        }

    }

    /**
     * Diese Methode der Server-Klasse wird hiermit ueberschrieben.
     * Die Verbindung wird beendet und aus der Liste der Clients gestrichen.
     */
    public void processClosingConnection(String pClientIP, int pClientPort){
        this.send(pClientIP, pClientPort, "EXT complete");
        this.closeConnection(pClientIP, pClientPort);
    }
    
    /**
     * Main-Methode die den Server auf Port 1024 startet.
     */
    public static void main(String [] args)
    {
        SpielServer es = new SpielServer(2000);
    }

    /**
     * Methode, die bei Aufruf eine Zufallszahl zwischen 0 und 20 zurück gibt.
     * @return Zufallszahl
     */
    private synchronized int gibZufallszahl()
    {
        return (int)(Math.random() * 20);
    }
    
    /**
     * Diese Methode gibt den Befehl zurück die die message beinhaltet
     * 
     * @param message
     * 
     * @return Befehl
     */
    private synchronized String gibBefehlsbereich(String message)
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
    private synchronized String gibTextbereich(String message)
    {
        String [] messageArray = message.split(" ");
        return messageArray[1];
    }
    
    /**
     * Methode, die die zu erratende Zahl vom Spiel mit der übergebenen ClientIP zurück gibt.
     * @param pClientIP
     * @return zu erratende Zahl
     */
    private synchronized int gibZahlVonSpiel(String pClientIP)
    {
        spieleOnline.toFirst();
        while (spieleOnline.hasAccess()) {
            if (spieleOnline.getContent().gibClientIP().equals(pClientIP)) {
                return spieleOnline.getContent().gibZahl();
            } else {
                spieleOnline.next();
            }
        }
        return -1;
    }
    
    /**
     * Methode, die die Anzahl der bisherigen Versuche vom Spiel mit der übergebenen ClientIP zurück gibt.
     * @param pClientIP
     * @return bisherige Versuche
     */
    private synchronized int gibVersucheVonSpiel(String pClientIP)
    {
        spieleOnline.toFirst();
        while (spieleOnline.hasAccess()) {
            if (spieleOnline.getContent().gibClientIP().equals(pClientIP)) {
                return spieleOnline.getContent().gibVersuche();
            } else {
                spieleOnline.next();
            }
        }
        return -1;
    }
    
    /**
     * Methode, die beim Spiel mit der übergebenen Client-IP, die Versuche um 1 erhöht.
     * @param pClientIP
     */
    private synchronized void versucheErhoehenVonSpiel(String pClientIP)
    {
        spieleOnline.toFirst();
        while(spieleOnline.hasAccess()){
            if (spieleOnline.getContent().gibClientIP().equals(pClientIP)){
                spieleOnline.getContent().erhoeheVeruche();
                break;
            }
            else{
                spieleOnline.next();
            }
        }
    }
    
    /**
     * Methode, die das Spiel mit der übergebenen Client-IP, löscht
     * @param pClientIP
     */
    private synchronized void loescheOnlineSpiel(String pClientIP, int pClientPort)
    {
        spieleOnline.toFirst();
        while (spieleOnline.hasAccess()) {
            if (spieleOnline.getContent().gibClientIP().equals(pClientIP)) {
                spieleOnline.remove();
                break;
            } else {
                spieleOnline.next();
            }
        }
    }
    
    /**
     * Methode, die den Spielernamen für die übergebene ClientIP zurück gibt.
     * @param pClientIP
     * @return Spielername
     */
    private synchronized String gibNameVonSpiel(String pClientIP)
    {
        spieleOnline.toFirst();
        while (spieleOnline.hasAccess()) {
            if (spieleOnline.getContent().gibClientIP().equals(pClientIP)) {
                return spieleOnline.getContent().gibName();
            } else {
                spieleOnline.next();
            }
        }
        return "Fehler!";
    }
    
    /**
     * Diese Methode generiert einen String aus den Einträgen der übergebenen Liste.
     * Dabei beachtet man das folgende Format:
     * Name:Punkte Name:Punkte Name:Punkte usw.
     * 
     * @param Liste mit Objekten vom Typ Eintrag
     * @return String mit Einrtägen durch Leerzeichen getrennt
     */
    private String generiereStringAusList(List<Eintrag> l)
    {
        String ausgabe = "";
        l.toFirst();
        while(l.hasAccess()){
            ausgabe = ausgabe + l.getContent().gibName() + ":" + l.getContent().gibPunkte() + " ";
            l.next();
        }
        return ausgabe;
    }
}

import tuwien.auto.calimero.KNXException;
import tuwien.auto.calimero.link.medium.TPSettings;

import javax.servlet.http.HttpServletResponse;
import javax.sound.midi.Track;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Classe définissant le chemin vers une action
 */
@Path("/entry-point")
public class EntryPoint {

    /**
     * variable récupérant l'adresse de l'hote
     */
    private static InetAddress localhost;
    static {

        try {

            localhost = InetAddress.getLocalHost();

        } catch (UnknownHostException ignored) {}

    }

    /**
     * variable créeant la connection KNX
     */
    private static KNXConnection knxConnection;
    static {

        try {

            knxConnection = new KNXConnection(new InetSocketAddress(localhost.getHostAddress(), 8000), new InetSocketAddress("192.168.1.10", 3214), false, new TPSettings(), 1000);

        } catch (KNXException | InterruptedException ignored) {}

    }

    /**
     * variable définissant la réponse de la page
     */
    private static String reponse = "0";

    /**
     * variable définissant la liste des reponses
     */
    private static String[] listeReponse = new String[]{
            "Bienvenue",
            "Vous avez demarre le chenillard",
            "Vous avez accelere le chenillard",
            "Vous avez ralenti le chenillard",
            "Vous avez arrete le chenillard" };

    /**
     * Méthode permettant de renvoyer une page web
     * @return on renvoi le texte de la page web
     */
    @GET
    @Path("accueil")
    @Produces(MediaType.TEXT_HTML)
    public String accueil() throws IOException {

        // on renvoi la page à charger
        return chargementPage("index.html");

    }

    @POST
    @Path("reponse")
    @Produces(MediaType.TEXT_HTML)
    public String reponse(@FormParam("reponse") String reponse) throws IOException {

        // on récupére la réponse
        this.reponse = reponse;

        // on envoi l'instruction
        knxConnection.instruction("1/0/"+this.reponse);

        // on renvoi la page à charger
        return chargementPage("index.html");

    }

    /**
     * Méthode permettant de charger une page
     * @param page : paramétre définissant la page à charger
     * @throws IOException : exception géré
     * @return on renvoi le texte
     */
    public String chargementPage(String page) throws IOException {

        // variable définissant le texte à renvoyer
        String texte = "";

        // si il n'y a pas d'exception
        try {

            // alors on ouvre le fichier et on le lie
            File fichier = new File(page);
            Scanner lecture = new Scanner(fichier);

            // variable permettant de regarder la précédente ligne
            String precedenteLine;

            // tant qu'il y a une nouvelle ligne
            while (lecture.hasNextLine()) {

                // on récupére la ligne
                precedenteLine = lecture.nextLine();

                // on envoi la ligne
                texte += precedenteLine;

                // si on trouve un champ de texte ayant le même nom que l'une des clés de l'hashmap
                if(precedenteLine.matches("(.*)<p name=\"reponse(.*)")){

                    // alors on envoi le paramétre
                    texte += listeReponse[Integer.parseInt(reponse)];

                }

            }

            // on ferme la lecture
            lecture.close();

            // on gére les exceptions
        } catch (FileNotFoundException ignored) {}

        // on renvoi le texte
        return texte;

    }

}
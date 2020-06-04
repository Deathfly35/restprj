
import tuwien.auto.calimero.CloseEvent;
import tuwien.auto.calimero.FrameEvent;
import tuwien.auto.calimero.KNXAddress;
import tuwien.auto.calimero.KNXException;
import tuwien.auto.calimero.cemi.CEMILData;
import tuwien.auto.calimero.link.KNXNetworkLinkIP;
import tuwien.auto.calimero.link.NetworkLinkListener;
import tuwien.auto.calimero.link.medium.TPSettings;
import tuwien.auto.calimero.process.ProcessCommunicator;
import tuwien.auto.calimero.process.ProcessCommunicatorImpl;

import java.net.InetSocketAddress;
import java.util.HashMap;

/**
 * Classe permettant d'initilialiser la connection KNX
 */
public class KNXConnection implements NetworkLinkListener {

    /**
     * variable définissant l'état des boutons
     */
    private HashMap<Character,Boolean> etatsBoutons;

    /**
     * variable permettant d'écouter et d'écrire les composants
     */
    private ProcessCommunicator pc;

    /**
     * variable stockant la connection KNX
     */
    private KNXNetworkLinkIP linkIP;

    /**
     * variable stockant le thread
     */
    private ThreadLocal thread;

    /**
     * variable définissant le temps d'attente
     */
    private int tempsAttente;

    /**
     * Constructeur
     * @param addresseSource : paramétre définissant l'adresse source
     * @param addresseDistante : paramétre définissant l'adresse distante
     * @param utilisationNAT : paramétre définissant si nous utilisons le NAT ou non
     * @param tpSetting : paramétre définissant les paramétres de la connection
     * @throws KNXException : exception géré
     * @throws InterruptedException : exception géré
     */
    public KNXConnection(InetSocketAddress addresseSource, InetSocketAddress addresseDistante, boolean utilisationNAT, TPSettings tpSetting, int tempsAttente) throws KNXException, InterruptedException {

        // on initialise la hashmap des boutons
        this.etatsBoutons = new HashMap<>();

        // on se connecte via KNX à l'adresse distante
        this.linkIP = KNXNetworkLinkIP.newTunnelingLink(addresseSource, addresseDistante, utilisationNAT, tpSetting);

        // on initialise
        this.pc = new ProcessCommunicatorImpl(this.linkIP);

        // on initialise le temps d'attente
        this.tempsAttente = tempsAttente;

        // on crée le thread
        this.thread = new ThreadLocal(tempsAttente,this.pc);

        // on lance l'écoute
        this.linkIP.addLinkListener(this);

    }

    /**
     * Méthode permettant d'arréter la connection
     */
    public void deconnexion() {

        // on arréte le thread
        this.thread.stopThread();

        // on crée le thread
        this.thread = new ThreadLocal(this.tempsAttente,this.pc);

        // on ajoute les actions
        this.thread.addAction(new Action(1,2));
        this.thread.addAction(new Action(2,2));
        this.thread.addAction(new Action(3,2));
        this.thread.addAction(new Action(4,2));

        // on lance le thread
        this.thread.start();

        // on ferme les deux paramétres
        this.pc.close();
        this.linkIP.close();

    }

    /**
     * Méthode permettant de retourner si la connection est fermée ou non
     * @return on renvoi vrai si la connection est fermé sinon on renvoi faux
     */
    public boolean estFerme(){

        // on renvoi l'état de la connection
        return !this.linkIP.isOpen();

    }

    /**
     * Méthode appelé lorsque la connection est ouverte
     * @param frameEvent : paramétre définissant l'événement
     */
    public void confirmation(FrameEvent frameEvent) {

    }

    /**
     * Méthode appelé lorsque l'on recoit une requete
     * @param frameEvent : paramétre définissant l'événement
     */
    public void indication(FrameEvent frameEvent) {

        // on récupére la destination
        KNXAddress destination = ((CEMILData)frameEvent.getFrame()).getDestination();

        // on vérifie l'instruction
        instruction(destination.toString());

    }

    /**
     * Méthode permettant de gérer les instructions recus
     * @param bouton : paramétre définissant le bouton appuyé
     */
    public void instruction(String bouton){

        // si la destination est bien un bouton
        if(bouton.charAt(0) == '1'){

            // on récupére le boutons sur lequel l'action arrive
            char idBouton = bouton.charAt(bouton.length() - 1);

            // alors on gére les cas des différents boutons
            switch(idBouton){

                // si on appuie sur le bouton 1
                case '1' :

                    // si le bouton n'a pas été ajouté à la liste alors on ajoute le bouton en indiquant qu'il n'a pas été appuyé
                    this.etatsBoutons.putIfAbsent(idBouton, false);

                    // on change la valeur du bouton
                    this.etatsBoutons.replace(idBouton,!this.etatsBoutons.get(idBouton));

                    // si le bouton est appuyé
                    if(this.etatsBoutons.get(idBouton)) {

                        // on arréte le thread
                        this.thread.stopThread();

                        // on crée le thread
                        this.thread = new ThreadLocal(this.tempsAttente,this.pc);

                        // on ajoute les actions
                        this.thread.addAction(new Action(1,0));
                        this.thread.addAction(new Action(2,0));
                        this.thread.addAction(new Action(3,0));
                        this.thread.addAction(new Action(4,0));

                        // sinon
                    } else {

                        // on arréte le thread
                        this.thread.stopThread();

                        // on crée le thread
                        this.thread = new ThreadLocal(this.tempsAttente,this.pc);

                        // on ajoute les actions
                        this.thread.addAction(new Action(4,0));
                        this.thread.addAction(new Action(3,0));
                        this.thread.addAction(new Action(2,0));
                        this.thread.addAction(new Action(1,0));

                    }

                    // on lance le thread
                    this.thread.start();

                    break;

                case '2':

                    // on ralentit le chenillard
                    this.tempsAttente -= 100;

                    // on change le temps d'attente entre chaque actions
                    this.thread.setTempsAttente(this.tempsAttente);

                    break;

                case '3':

                    // on accélére le chenillard
                    this.tempsAttente += 100;

                    // on change le temps d'attente entre chaque actions
                    this.thread.setTempsAttente(this.tempsAttente);

                    break;

                case '4':

                    // alors on se deconnecte
                    this.deconnexion();

                    break;

                // cas par défaut
                default:

                    break;

            }

        }

    }

    /**
     * Méthode permettant de gérer le cas ou le lien est fermé
     * @param closeEvent : paramétre définissant l'événement de fermeture
     */
    @Override
    public void linkClosed(CloseEvent closeEvent) {

        // on affiche que la connection est fermée
        System.out.println("connection fermé");

    }

}

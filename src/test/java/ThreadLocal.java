import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.KNXFormatException;
import tuwien.auto.calimero.KNXTimeoutException;
import tuwien.auto.calimero.link.KNXLinkClosedException;
import tuwien.auto.calimero.process.ProcessCommunicator;

import java.util.ArrayList;
import java.util.Iterator;

public class ThreadLocal extends Thread {

    /**
     * constante définissant le temps minimal entre chaque actions
     */
    private final int TEMPS_MINIMAL_ACTION = 500;

    /**
     * variable définissant le temp d'attente
     */
    private int tempsAttente;

    /**
     * variable définissant la liste d'action à effectué
     */
    private ArrayList<Action> actionList;

    /**
     * variable permettant d'écouter et d'écrire les composants
     */
    private ProcessCommunicator pc;

    /**
     * variable permettant de continuer les actions
     */
    private boolean continuer;

    /**
     * Constructeur
     * @param tempsAttente : paramétre définissant le temps d'attente
     * @param pc : paramétre définissant le pc
     */
    public ThreadLocal(int tempsAttente, ProcessCommunicator pc){

        // on initialise l'arraylist des actions
        this.actionList = new ArrayList<>();

        // on initialise le pc communicator
        this.pc = pc;

        // si le temps d'attente est inférieur au minimum autorisé alors on le met au temp minimal autorisé
        this.tempsAttente = Math.max(tempsAttente, this.TEMPS_MINIMAL_ACTION);

    }

    /**
     * Méthode permettant d'ajouter une action
     * @param action : paramétre définissant l'action à ajouter
     */
    public void addAction(Action action){

        // on ajoute l'action
        this.actionList.add(action);

    }

    /**
     * Méthode permettant de changer le temps d'attente entre chaque actions
     * @param tempsAttente : paramétre définissant le nouveau temps d'attente entre chaque action
     */
    public void setTempsAttente(int tempsAttente){

        // si le temps d'attente est inférieur au minimum autorisé alors on le met au temp minimal autorisé
        this.tempsAttente = Math.max(tempsAttente, this.TEMPS_MINIMAL_ACTION);

    }

    /**
     * Méthode permettant de vider les actions
     */
    public void stopThread(){

        // on arréte le thread
        this.continuer = false;

    }

    /**
     * Méthode permettant de lancer le thread
     */
    @Override
    public void run() {

        // on autorise le lancement du thread
        this.continuer = true;

        // variable définissant l'iterateur
        Iterator iterateur;

        // variable définissant l'action
        Action action = actionList.get(0);

        // variable définissant la derniére action effectué
        int indice = 0;

        // tant que l'on lance le thread
        while(this.continuer) {

            // on récupére l'iérateur de la liste des actions
            iterateur = actionList.iterator();

            // on parcours le tableau de leds allumés et si l'on veut continuer
            while (iterateur.hasNext() && this.continuer) {

                // on récupére l'action
                action = (Action) iterateur.next();

                try {

                    // si la led doit être allumé puis eteinte ou si elle doit être allumé
                    if (action.getActionEffectue() == 0 || action.getActionEffectue() == 1) {

                        // on allume la led
                        this.pc.write(new GroupAddress("0/0/" + action.getIdentifiantLampe()), true);

                    }

                    // on attend
                    Thread.sleep(this.tempsAttente);

                    // si la led doit être allumé puis eteinte ou si elle doit être eteinte
                    if (action.getActionEffectue() == 0 || action.getActionEffectue() == 2) {

                        // on eteint la led
                        this.pc.write(new GroupAddress("0/0/" + action.getIdentifiantLampe()), false);

                    }

                } catch (KNXFormatException | KNXTimeoutException | KNXLinkClosedException | InterruptedException ignored) { }

            }

        }

        try {

            // on éteint la derniére lampe sélectionné
            this.pc.write(new GroupAddress(action.getIdentifiantLampe()),false);

        } catch (KNXTimeoutException | KNXLinkClosedException ignored) {}

        // on vide les actions
        this.actionList = new ArrayList<>();
        
    }

}

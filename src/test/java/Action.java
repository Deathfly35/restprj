/**
 * Classe permettant de gérer les actions
 */
public class Action {

    /**
     * variable définissant l'identifiant de la lampe sur laquelle effectuer l'action
     */
    private int identifiantLampe;

    /**
     * variable définissant l'action à éffectué
     */
    private int actionEffectue;

    /**
     * Constructeur
     * @param identifiantLampe : paramétre définissant l'identifiant de la lampe
     * @param actionEffectue : paramétre définissant l'action à éffectué
     */
    public Action(int identifiantLampe, int actionEffectue){

        // on associe les paramétres
        this.identifiantLampe = identifiantLampe;
        this.actionEffectue = actionEffectue;

    }

    /**
     * Méthode permettant de récupérer les identifiants de la lampe
     * @return on renvoi l'identifiant de la lampe
     */
    public int getIdentifiantLampe(){

        // on renvoi l'identifiant de la lampe
        return identifiantLampe;

    }

    /**
     * Méthode permettant de récupérer l'action effectué
     * @return on renvoi l'action effectué
     */
    public int getActionEffectue(){

        // on renvoi l'action effectué
        return actionEffectue;

    }
}

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import tuwien.auto.calimero.link.medium.TPSettings;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * Classe principal permettant de lancer la page web ainsi que la connexion a KNX
 */
public class App {

    /**
     * Méthode appelé lors du lancement de l'application
     * @param args : paramétre définissant les arguments donnés à l'application
     * @throws Exception : exception géré
     */
    public static void main(String[] args) throws Exception {

        //Création du ServletHandler
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        //Création et initialisation du serveur Jetty
        Server jettyServer = new Server(8080);
        jettyServer.setHandler(context);

        //Définition du Servlet
        ServletHolder jerseyServlet = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
        jerseyServlet.setInitOrder(0);

        //Indique au Servlet Jersey quelles classes charger
        jerseyServlet.setInitParameter("jersey.config.server.provider.classnames", EntryPoint.class.getCanonicalName());

        try {

            //Lancement du serveur
            jettyServer.start();
            jettyServer.join();

        } finally {

            //Arret du serveur
            jettyServer.destroy();

        }

    }

}

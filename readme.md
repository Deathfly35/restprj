Objectifs : 
- Création d'une application permettant de lancer un chenillard
- Création d'un interface permettant de le gérer à distance en utilisant le protocole REST

Explication :
Pour l'utiliser il suffit d'importer le projet en oubliant pas d'importer maven, javafx, le fichier jar de simulation et en utilisant java 11 de préférence. 

Organisation : 
La classe faisant office de controleur est app, trouvable dans src/test/java celle-ci lance l'application de simulation ainsi que l'interprétation de l'appui sur des boutons qui sont gérés par toutes les autres classes dans le répertoire src/main/test/java.
Le visuel est affiché grâce à index.html qui définit le code html de la page web que l'on affichera celui-ci se trouvant dans src.

Pour l'ajout ou la modification d'une fonctionnalité : 
- modifier le fichier index.html pour afficher ce que l'on veut
- dans EntryPoint changer la fonction response pour récupérer les nouveaux paramétres
- dans EntryPoint changer la fonction chargementPage pour qu'elle affiche les modifications que vous désirez
- dans KNXConnection changer la fonction instruction si l'on veut détecter d'autres boutons ou encore intéragir différement du coté de la simulation.
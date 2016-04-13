# reing-LOG6302

Pour changer de configuration, il faut modifier la variable dans le setConfiguration() à la ligne 278 par :
- TP1
- TP2
- CFG
- DOM
- PDOM
- CFGINOUT
- CDG
- DDG
- PDG
- SLICE (interactif)
- SLICEALL

Vous pouvez ensuite compiler le projet avec la commande make. Pour lancer sur l'ensemble de test (fichiers dans le dossier examples/), lancer la commande make test. Make project lance le projet sur le projet zest qui doit être dans le dossier projects (à l'utilisateur de git clone le projet). Make wordcount permet de lancer le projet sur juste WordCount.java.

script.sh est un script qui lance dot sur tous les .dot dans le dossier graphs/. Pour le TP2, si vous voulez les résultats par package, il faut lancer ./script.sh UML (qui lance également le script python pour combiner les classes d'un même package).

# otp-service/Dockerfile
#
# IMPORTANT : ton image "latest" correspond en réalité à la version
# 2.10.0-SNAPSHOT (build de dev, branche dev-2.x). Un SNAPSHOT n'est pas
# figé : le contenu derrière le tag "latest" peut changer à tout moment.
# On fige donc l'image par son DIGEST exact (celui qui a servi à
# construire ton graph.obj), pas par son nom.
#
# Récupère-le avec : docker images --digests | grep opentripplanner
# et remplace la valeur ci-dessous.
FROM opentripplanner/opentripplanner@sha256:e4c7c39f7a5ac2f6195331df4348ddac04bb1b7e852f65576f32229f89237557

WORKDIR /var/opentripplanner

# On ne copie QUE ce dont OTP a besoin pour SERVIR le graphe.
# Le .osm.pbf et les .zip GTFS ne servent qu'à la construction (--build),
# pas au chargement (--load) : inutile de les embarquer, ça alourdirait
# l'image pour rien.
COPY otp-data/graph.obj          ./graph.obj
COPY otp-data/otp-config.json    ./otp-config.json
COPY otp-data/router-config.json ./router-config.json

# Render détecte automatiquement le port sur lequel le process écoute
# (pas besoin de lire $PORT comme sur d'autres PaaS) : OTP écoute par
# défaut sur 8080, on garde ce port.
EXPOSE 8080

# Ajuste selon le plan Render choisi (laisse ~20% de marge sous la RAM
# du plan pour l'overhead JVM). Ex : plan 2 Go -> -Xmx1536m
ENV JAVA_OPTIONS="-Xmx2G"

CMD ["--load", "--serve"]
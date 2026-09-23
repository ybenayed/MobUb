# otp-service/Dockerfile
#
# IMPORTANT : image figée par DIGEST exact (pas par le tag "latest", qui
# pointe en réalité vers un build de dev 2.10.0-SNAPSHOT et peut changer
# de contenu à tout moment). Ce digest est celui qui a servi à construire
# graph.obj — ne le change pas sans reconstruire le graphe avec la même
# version.
FROM opentripplanner/opentripplanner@sha256:e4c7c39f7a5ac2f6195331df4348ddac04bb1b7e852f65576f32229f89237557

WORKDIR /var/opentripplanner

# On ne copie QUE ce dont OTP a besoin pour SERVIR le graphe (pas le
# .osm.pbf ni les .zip GTFS, inutiles au chargement, voir .dockerignore).
COPY otp-data/graph.obj          ./graph.obj
COPY otp-data/otp-config.json    ./otp-config.json
COPY otp-data/router-config.json ./router-config.json

# Render détecte automatiquement le port sur lequel le process écoute.
EXPOSE 8080

# Sur le plan Free de Render (512 Mo RAM), reste prudent sur le heap :
# laisse de la marge pour l'OS + overhead JVM.
ENV JAVA_OPTIONS="-Xmx400m"

CMD ["--load", "--serve"]
FROM opentripplanner/opentripplanner@sha256:e4c7c39f7a5ac2f6195331df4348ddac04bb1b7e852f65576f32229f89237557

WORKDIR /var/opentripplanner

# Copie des fichiers de configuration et de données
COPY otp-data/aquitaine.osm.pbf  ./aquitaine.osm.pbf
COPY otp-data/otp-config.json    ./otp-config.json
COPY otp-data/router-config.json ./router-config.json

# Construction du graphe en utilisant l'exécutable/script interne de l'image
RUN /docker-entrypoint.sh --build .

EXPOSE 8080

ENV JAVA_OPTIONS="-Xmx450m"

# Lancement du serveur
CMD ["--server", "."]
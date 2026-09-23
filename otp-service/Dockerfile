FROM opentripplanner/opentripplanner@sha256:e4c7c39f7a5ac2f6195331df4348ddac04bb1b7e852f65576f32229f89237557

WORKDIR /var/opentripplanner

# Copie des fichiers de configuration et de données openstreetmap
COPY otp-data/aquitaine.osm.pbf  ./aquitaine.osm.pbf
COPY otp-data/otp-config.json    ./otp-config.json
COPY otp-data/router-config.json ./router-config.json

# Étape 1 : Construction du graphe au build (l'image sait exécuter OTP nativement sans java -jar)
RUN otp --build .

EXPOSE 8080

ENV JAVA_OPTIONS="-Xmx450m"

# Étape 2 : Lancement du serveur au démarrage du conteneur
CMD ["--server", "."]
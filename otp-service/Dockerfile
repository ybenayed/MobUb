FROM opentripplanner/opentripplanner:2.10.0-SNAPSHOT

WORKDIR /var/opentripplanner

# Copier les fichiers de configuration et le graphe pré-généré localement
COPY otp-data/otp-config.json ./otp-config.json
COPY otp-data/router-config.json ./router-config.json
COPY otp-data/graph.obj ./graph.obj

# Lancer le serveur OTP en mode --load
EXPOSE 8080
ENTRYPOINT ["java", "-Xmx2G", "-jar", "/app/opentripplanner.jar", "--load", "."]
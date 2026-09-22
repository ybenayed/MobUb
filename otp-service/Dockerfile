FROM opentripplanner/opentripplanner:latest

# Copier tout ton dossier de données locales (qui contient graph.obj) dans le conteneur
COPY ./otp-data /var/opentripplanner

# Commande pour lancer OTP en mode serveur
ENTRYPOINT ["java", "-Xmx2G", "-jar", "/usr/share/opentripplanner/opentripplanner.jar", "--load", "--serve", "/var/opentripplanner"]
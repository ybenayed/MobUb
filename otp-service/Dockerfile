FROM opentripplanner/opentripplanner:latest
COPY ./otp-data /var/opentripplanner
ENTRYPOINT ["java", "-jar", "/usr/share/opentripplanner/opentripplanner.jar", "--load", "/var/opentripplanner"]
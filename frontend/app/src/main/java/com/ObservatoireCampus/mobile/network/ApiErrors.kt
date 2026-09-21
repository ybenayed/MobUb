package com.ObservatoireCampus.mobile.network

import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

enum class ErrorContext { GENERAL, REALTIME, ITINERARY }

fun Throwable.toUserMessage(context: ErrorContext = ErrorContext.GENERAL): String {
    val realtime = context == ErrorContext.REALTIME
    val itinerary = context == ErrorContext.ITINERARY

    return when (this) {
        is UnknownHostException ->
            "Pas de connexion internet. Verifiez votre reseau."

        is SocketTimeoutException -> when {
            realtime -> "Temps reel indisponible : le serveur met trop de temps a repondre."
            itinerary -> "Le calcul de l'itineraire prend trop de temps. Reessayez."
            else -> "Le serveur met trop de temps a repondre. Reessayez dans un instant."
        }

        is ConnectException -> when {
            realtime -> "Temps reel indisponible : impossible de joindre le serveur."
            itinerary -> "Itineraire impossible : serveur injoignable."
            else -> "Impossible de se connecter au serveur."
        }

        is SSLException ->
            "Connexion securisee impossible avec le serveur."

        is HttpException -> when (code()) {
            400 -> "Requete invalide."
            401 -> "Session expiree. Veuillez vous reconnecter."
            403 -> "Acces refuse."
            404 -> if (realtime) "Aucune information temps reel pour cet element." else "Ressource introuvable."
            408 -> "Le serveur met trop de temps a repondre. Reessayez dans un instant."
            429 -> "Trop de requetes. Reessayez dans quelques minutes."
            in 500..599 -> when {
                realtime -> "Informations temps reel indisponibles pour le moment."
                itinerary -> "Le service d'itineraire est indisponible pour le moment. Reessayez plus tard."
                else -> "Le serveur rencontre un probleme. Reessayez plus tard."
            }
            else -> "Erreur inattendue (code ${code()})."
        }

        is IOException -> "Probleme de reseau. Verifiez votre connexion."

        else ->
            if (javaClass.simpleName.contains("Json", ignoreCase = true)) "Reponse du serveur illisible."
            else "Une erreur est survenue."
    }
}
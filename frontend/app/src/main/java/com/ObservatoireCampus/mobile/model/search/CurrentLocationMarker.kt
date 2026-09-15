package com.ObservatoireCampus.mobile.model.search

/**
 * Marqueur technique (jamais affiche tel quel) utilise a la place d'un texte
 * traduit en dur quand l'utilisateur choisit "Ma position" comme origine ou
 * destination d'un itineraire. Ainsi la base ne stocke jamais un libelle
 * fige dans une langue donnee : c'est l'ecran qui affiche l'historique qui
 * traduit dynamiquement selon la langue courante au moment de l'affichage.
 */
const val CURRENT_LOCATION_MARKER = "__current_location__"
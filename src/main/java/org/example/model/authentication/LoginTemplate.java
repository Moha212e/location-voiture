package org.example.model.authentication;

public abstract class LoginTemplate {

    // Méthode template finale : fixe le déroulement
    public final boolean login(String username, String password) {
        String encryptedPassword = encryptPassword(password);
        return authenticate(username, encryptedPassword);
    }


    // Méthodes à spécialiser
    protected abstract String encryptPassword(String password);
    protected abstract boolean authenticate(String username, String encryptedPassword);
}
/**
 * # Synthèse de la classe `LoginTemplate`
 *
 * ## Description générale
 * `LoginTemplate` est une classe abstraite qui implémente le **patron de conception Template Method** pour définir le squelette du processus d'authentification, tout en déléguant certaines étapes spécifiques aux sous-classes.
 *
 * ## Structure et implémentation
 *
 * ### Méthodes
 *
 * 1. **Méthode template (`login`)**
 *    - `public final boolean login(String username, String password)`
 *    - **Finale** : empêche les sous-classes de modifier l'algorithme principal
 *    - Définit le flux standard d'authentification :
 *      1. Chiffrement du mot de passe (appel à `encryptPassword`)
 *      2. Vérification des credentials (appel à `authenticate`)
 *
 * 2. **Méthodes abstraites à implémenter**
 *    - `protected abstract String encryptPassword(String password)`
 *      - Spécifie comment chiffrer le mot de passe
 *    - `protected abstract boolean authenticate(String username, String encryptedPassword)`
 *      - Spécifie comment vérifier l'authentification
 *
 * ## Caractéristiques clés
 *
 * ### Points forts
 * - **Encapsulation d'algorithme** : Le flux principal est fixé et non modifiable
 * - **Flexibilité** : Les parties variables sont abstraites et implémentables librement
 * - **Réutilisabilité** : Le squelette commun est défini une fois
 *
 * ### Pattern Template Method
 * - **Méthode template** : `login()` fixe la structure
 * - **Hooks** : Les méthodes abstraites sont les points d'extension
 * - **Hollywood Principle** : "Ne nous appelez pas, nous vous appellerons"
 *
 * ## Cas d'utilisation typique
 * - Systèmes nécessitant un processus d'authentification standardisé
 * - Quand le flux est fixe mais que certaines étapes doivent varier
 * - Framework d'authentification extensibles
 *
 * ## Relation avec `SimpleLogin`
 * `SimpleLogin` fournit une implémentation concrète :
 * - `encryptPassword` : retourne le mot de passe en clair
 * - `authenticate` : compare avec une Map d'utilisateurs
 *
 * Cette conception permet de créer facilement d'autres variants (par exemple avec chiffrement) tout en gardant le même flux principal.
 */
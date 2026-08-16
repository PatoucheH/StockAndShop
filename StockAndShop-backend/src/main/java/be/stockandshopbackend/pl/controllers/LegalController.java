package be.stockandshopbackend.pl.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Serves the public Privacy Policy and Terms of Service as self-contained HTML pages,
 * generated entirely from code (no static files). Prefixed with /api by WebConfig, so the
 * real paths are /api/privacy and /api/terms; the frontend nginx maps the clean URLs
 * /privacy and /terms onto them. Both are public (see SecurityConfig permitAll list).
 *
 * To update the content: edit LAST_UPDATED, CONTACT_EMAIL, PRIVACY_BODY or TERMS_BODY,
 * then push — the CI/CD redeploys the backend automatically.
 */
@RestController
public class LegalController {

    private static final String LAST_UPDATED = "5 août 2026";
    private static final String CONTACT_EMAIL = "stockandshop@patoutech.com";

    @GetMapping(value = "/privacy", produces = MediaType.TEXT_HTML_VALUE)
    public String privacy() {
        return buildPage("Politique de confidentialité", PRIVACY_BODY);
    }

    @GetMapping(value = "/terms", produces = MediaType.TEXT_HTML_VALUE)
    public String terms() {
        return buildPage("Conditions d'utilisation", TERMS_BODY);
    }

    private String buildPage(String title, String body) {
        return PAGE_TEMPLATE
                .replace("{{TITLE}}", title)
                .replace("{{UPDATED}}", LAST_UPDATED)
                .replace("{{BODY}}", body)
                .replace("{{CONTACT}}", CONTACT_EMAIL);
    }

    private static final String PAGE_TEMPLATE = """
            <!DOCTYPE html>
            <html lang="fr">
            <head>
            <meta charset="utf-8">
            <meta name="viewport" content="width=device-width, initial-scale=1">
            <link rel="icon" type="image/png" href="/StockAndShop_favicon.png">
            <title>{{TITLE}} — Stock&amp;Shop</title>
            <style>
              :root { color-scheme: light; }
              * { box-sizing: border-box; }
              body { margin:0; font-family: system-ui,-apple-system,"Segoe UI",Roboto,sans-serif; color:#1f2937; background:#f9fafb; line-height:1.6; }
              .topbar { background: linear-gradient(135deg,#064e3b,#047857 70%,#059669); color:#fff; padding:14px 20px; font-weight:700; font-size:18px; position:sticky; top:0; }
              .topbar .amp { color:#6ee7b7; }
              main { max-width:760px; margin:0 auto; padding:28px 20px 48px; }
              h1 { font-size:26px; margin:0 0 4px; color:#111827; }
              .updated { color:#9ca3af; font-size:14px; margin:0 0 24px; }
              h2 { font-size:18px; margin:28px 0 8px; color:#111827; }
              h3 { font-size:15px; margin:16px 0 4px; color:#374151; }
              p { margin:0 0 12px; }
              ul { margin:0 0 12px; padding-left:22px; }
              li { margin:4px 0; }
              a { color:#059669; }
              strong { color:#111827; }
              footer { margin-top:40px; padding-top:20px; border-top:1px solid #e5e7eb; color:#9ca3af; font-size:14px; }
              footer a { margin-right:14px; white-space:nowrap; }
            </style>
            </head>
            <body>
            <div class="topbar">Stock<span class="amp">&amp;</span>Shop</div>
            <main>
            <h1>{{TITLE}}</h1>
            <p class="updated">Dernière mise à jour : {{UPDATED}}</p>
            {{BODY}}
            <footer>
              Stock&amp;Shop · Développeur : Patoutech<br>
              <a href="/privacy">Confidentialité</a>
              <a href="/terms">Conditions d'utilisation</a>
              <a href="mailto:{{CONTACT}}">Contact</a>
            </footer>
            </main>
            </body>
            </html>
            """;

    private static final String PRIVACY_BODY = """
            <p>Stock&amp;Shop (« nous ») attache de l'importance à la protection de votre vie privée. Cette politique
            explique quelles données nous collectons, pourquoi, et comment nous les protégeons lorsque vous utilisez l'application.</p>

            <h2>1. Données que nous collectons</h2>
            <h3>1.1 Données que vous fournissez</h3>
            <ul>
              <li><strong>Adresse e-mail</strong> — lors de la création de votre compte et pour vous connecter.</li>
              <li><strong>Nom d'utilisateur</strong> — choisi par vous, visible par les membres de vos foyers.</li>
              <li><strong>Mot de passe</strong> — stocké uniquement sous forme hachée (BCrypt) ; nous ne le voyons jamais en clair.</li>
            </ul>
            <h3>1.2 Données liées à votre utilisation</h3>
            <ul>
              <li>Vos <strong>foyers</strong> et les membres qui les composent.</li>
              <li>Votre <strong>stock domestique</strong> (produits, quantités, catégories) et vos <strong>listes de courses</strong>.</li>
              <li>Vos <strong>dépenses partagées</strong>, remboursements et soldes au sein d'un foyer.</li>
              <li>Vos <strong>recettes favorites</strong> et les <strong>commentaires</strong> que vous publiez.</li>
            </ul>
            <h3>1.3 Données techniques</h3>
            <ul>
              <li><strong>Journaux d'accès</strong> — adresses IP et horodatages, via Cloudflare, à des fins de sécurité et de débogage.</li>
            </ul>

            <h2>2. Utilisation des données</h2>
            <ul>
              <li>Fournir et maintenir les fonctionnalités de l'application.</li>
              <li>Synchroniser en temps réel votre stock et vos listes entre les membres d'un foyer.</li>
              <li>Calculer les dépenses partagées et les soldes.</li>
              <li>Générer des recettes à partir de votre stock.</li>
              <li>Assurer la sécurité, prévenir les abus et corriger les problèmes techniques.</li>
            </ul>

            <h2>3. Génération de recettes par IA</h2>
            <p>Pour générer des recettes, la liste des <strong>produits de votre foyer</strong> est envoyée à
            <a href="https://www.anthropic.com/legal/privacy" target="_blank" rel="noopener">Anthropic (Claude)</a>.
            <strong>Aucune donnée identifiante</strong> (e-mail, nom d'utilisateur) n'est transmise : seuls les noms
            des ingrédients sont utilisés pour proposer des recettes.</p>

            <h2>4. Prestataires tiers</h2>
            <p>Nous ne partageons vos données qu'avec les prestataires strictement nécessaires au fonctionnement du service :</p>
            <ul>
              <li><strong>Infrastructure auto-hébergée (Belgique)</strong> — la base de données et l'API tournent sur notre propre matériel ; les sauvegardes sont chiffrées.</li>
              <li><strong>Anthropic (Claude)</strong> — génération de recettes (voir section 3).</li>
              <li><strong>Cloudflare</strong> — proxy de sécurité devant l'API (protection anti-abus, voit l'adresse IP des requêtes).</li>
            </ul>
            <p>L'application ne contient <strong>ni publicité, ni traceur publicitaire, ni connexion via un réseau social</strong>.</p>

            <h2>5. Partage des données</h2>
            <p>Nous ne <strong>vendons pas</strong> vos données personnelles. Nous ne les partageons qu'avec les prestataires ci-dessus, ou si la loi l'exige.</p>

            <h2>6. Sécurité</h2>
            <ul>
              <li>Communications chiffrées en HTTPS / TLS.</li>
              <li>Mots de passe hachés (BCrypt).</li>
              <li>Sauvegardes de base de données chiffrées.</li>
              <li>Accès à l'infrastructure restreint et authentifié.</li>
            </ul>
            <p>Aucun système n'est infaillible ; en cas de violation de données, nous informerons les utilisateurs concernés dans les 72 heures, conformément au RGPD.</p>

            <h2>7. Vos droits (RGPD)</h2>
            <p>Si vous résidez dans l'Espace économique européen, vous disposez des droits suivants :</p>
            <ul>
              <li><strong>Accès</strong> — obtenir une copie des données vous concernant.</li>
              <li><strong>Rectification</strong> — corriger des données inexactes.</li>
              <li><strong>Effacement</strong> — demander la suppression de votre compte et de vos données.</li>
              <li><strong>Opposition / limitation</strong> — restreindre certains traitements.</li>
              <li><strong>Réclamation</strong> — auprès de votre autorité de protection des données. En Belgique :
                <a href="https://www.autoriteprotectiondonnees.be" target="_blank" rel="noopener">autoriteprotectiondonnees.be</a>.</li>
            </ul>

            <h2>8. Suppression de votre compte</h2>
            <p>Vous pouvez supprimer votre compte directement dans l'application (écran <strong>Compte → Supprimer mon compte</strong>).
            La suppression <strong>anonymise</strong> votre compte : vos données identifiantes (e-mail, nom d'utilisateur, mot de passe)
            sont effacées et le compte ne peut plus être utilisé. Vos contributions à des données partagées (dépenses d'un foyer, par exemple)
            sont conservées de façon anonyme afin de ne pas altérer l'historique des autres membres. La suppression est bloquée tant que vous
            êtes propriétaire d'un foyer ou que votre solde n'est pas réglé.</p>

            <h2>9. Mineurs</h2>
            <p>Stock&amp;Shop est destiné aux personnes âgées d'au moins <strong>16 ans</strong>. Nous ne collectons pas sciemment de données concernant des enfants de moins de 16 ans.</p>

            <h2>10. Conservation des données</h2>
            <ul>
              <li>Comptes actifs : données conservées tant que le compte existe.</li>
              <li>Comptes supprimés : données identifiantes effacées immédiatement (anonymisation).</li>
              <li>Journaux d'accès : conservés de façon limitée à des fins de sécurité.</li>
            </ul>

            <h2>11. Modifications</h2>
            <p>Nous pouvons mettre à jour cette politique. Les changements sont publiés sur cette page avec une nouvelle date de mise à jour.</p>

            <h2>12. Contact</h2>
            <p>Pour toute question relative à cette politique ou à vos données, contactez-nous à
            <a href="mailto:{{CONTACT}}">{{CONTACT}}</a>.</p>
            """;

    private static final String TERMS_BODY = """
            <p>Les présentes conditions régissent votre utilisation de l'application Stock&amp;Shop (« l'Application »).
            En créant un compte ou en utilisant l'Application, vous acceptez d'être lié par ces conditions et par notre
            <a href="/privacy">politique de confidentialité</a>.</p>

            <h2>1. Acceptation</h2>
            <p>En installant ou en utilisant Stock&amp;Shop, vous confirmez avoir lu, compris et accepté ces conditions. Si vous ne les acceptez pas, n'utilisez pas l'Application.</p>

            <h2>2. Éligibilité</h2>
            <p>Vous devez être âgé d'au moins <strong>16 ans</strong> pour utiliser Stock&amp;Shop. En l'utilisant, vous déclarez remplir cette condition.</p>

            <h2>3. Compte et responsabilité</h2>
            <ul>
              <li>Vous êtes responsable de la confidentialité de vos identifiants de connexion.</li>
              <li>Vous êtes responsable de toute activité effectuée depuis votre compte.</li>
              <li>Vous vous engagez à fournir des informations exactes lors de la création du compte.</li>
              <li>Vous devez nous signaler sans délai toute utilisation non autorisée de votre compte.</li>
            </ul>

            <h2>4. Utilisation acceptable</h2>
            <p>En utilisant l'Application, vous vous engagez à NE PAS :</p>
            <ul>
              <li>Exploiter des failles, utiliser des outils automatisés ou perturber le service.</li>
              <li>Tenter de faire de l'ingénierie inverse ou d'extraire le code source de l'Application.</li>
              <li>Harceler, menacer ou usurper l'identité d'autres utilisateurs via les pseudos ou les commentaires.</li>
              <li>Publier du contenu illégal, diffamatoire, obscène ou portant atteinte aux droits de tiers.</li>
              <li>Utiliser l'Application en violation des lois applicables.</li>
            </ul>
            <p>Toute violation peut entraîner la suspension temporaire ou définitive du compte.</p>

            <h2>5. Recettes générées par IA</h2>
            <p>Les recettes proposées par Stock&amp;Shop sont générées à l'aide d'une intelligence artificielle (Anthropic Claude).
            Nous ne pouvons garantir que chaque recette est exacte, équilibrée ou adaptée à des régimes ou allergies particuliers.
            Utilisez ces suggestions à titre indicatif, et non comme un conseil nutritionnel ou médical. Vérifiez toujours les
            ingrédients si vous avez des allergies ou intolérances.</p>

            <h2>6. Contenu visible par d'autres</h2>
            <p>Votre <strong>nom d'utilisateur</strong> et vos <strong>commentaires</strong> sont visibles par les membres de vos foyers.
            Choisissez un pseudo qui ne contient pas d'informations personnelles ni de propos inappropriés.</p>

            <h2>7. Achats intégrés</h2>
            <p>Si Stock&amp;Shop propose à l'avenir des fonctionnalités payantes, les achats seront traités par l'App Store d'Apple
            ou le Google Play Store, selon leurs politiques respectives. Nous ne stockons ni ne traitons directement d'informations de paiement.</p>

            <h2>8. Absence de garantie</h2>
            <p>L'Application est fournie <strong>« en l'état » et « selon disponibilité »</strong>, sans garantie d'aucune sorte. Nous ne garantissons pas qu'elle sera ininterrompue, exempte d'erreurs ou parfaitement sécurisée.</p>

            <h2>9. Limitation de responsabilité</h2>
            <p>Dans les limites autorisées par la loi, Stock&amp;Shop et son développeur ne sauraient être tenus responsables des dommages indirects, accessoires ou consécutifs (perte de données, perte de profits) résultant de votre utilisation de l'Application.</p>

            <h2>10. Résiliation</h2>
            <p>Vous pouvez cesser d'utiliser l'Application et supprimer votre compte à tout moment (écran <strong>Compte</strong>). Nous pouvons suspendre ou résilier un compte en cas de violation des présentes conditions.</p>

            <h2>11. Propriété intellectuelle</h2>
            <p>L'ensemble du contenu, de la marque et du code de Stock&amp;Shop nous appartient et est protégé par le droit de la propriété intellectuelle. Une licence limitée, personnelle et non transférable vous est accordée pour un usage conforme à sa destination.</p>

            <h2>12. Modifications</h2>
            <p>Nous pouvons modifier ces conditions à tout moment. Les changements sont publiés sur cette page. La poursuite de l'utilisation après leur entrée en vigueur vaut acceptation.</p>

            <h2>13. Droit applicable</h2>
            <p>Les présentes conditions sont régies par le droit <strong>belge</strong>. Tout litige relève de la compétence des tribunaux belges, sauf disposition impérative de protection des consommateurs.</p>

            <h2>14. Contact</h2>
            <p>Pour toute question relative à ces conditions, contactez-nous à <a href="mailto:{{CONTACT}}">{{CONTACT}}</a>.</p>
            """;
}

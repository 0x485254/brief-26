package com.easygroup.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for the root endpoint.
 * Provides a friendly landing page with API information.
 */
@RestController
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @GetMapping("/")
    public String home() {
        logger.info("Root endpoint accessed");
        return """
                <html>
                  <head>
                    <style>
                      body {
                        font-family: 'Segoe UI', sans-serif;
                        background-color: #f4f4f9;
                        color: #333;
                        margin: 0;
                        padding: 20px;
                      }
                      .container {
                        max-width: 700px;
                        margin: 40px auto;
                        background: white;
                        padding: 30px;
                        border-radius: 10px;
                        box-shadow: 0 2px 8px rgba(0,0,0,0.1);
                      }
                      h1 {
                        color: #0b6e4f;
                      }
                      a {
                        color: #0b6e4f;
                        text-decoration: none;
                        font-weight: bold;
                      }
                      a:hover {
                        text-decoration: underline;
                      }
                    </style>
                  </head>
                  <body>
                    <div class="container">
                      <h1>👋 Bienvenue sur l'API EasyGroup</h1>
                      <p>Le backend de l’application EasyGroup est opérationnel et prêt à l’emploi.</p>
                      <p>EasyGroup vous permet de créer, gérer et partager des listes de personnes, puis de générer des groupes aléatoires en fonction de critères personnalisés.</p>
                      <p>👉 Pour explorer et tester les différentes routes disponibles, consultez notre documentation complète sur Postman :</p>
                      <p><a href="https://www.postman.com/zadig2b/easygroup-workspace/collection/soddqsz/easygroup-api" target="_blank">🌐 Accéder à la documentation API EasyGroup</a></p>
                    </div>
                  </body>
                </html>
                """;
    }
}

package com.volleyball.authservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        logger.info("🔧 Initialisation de la base de données...");
        
        try {
            // Créer la table users
            createUsersTable();
            
            // Insérer les données de test
            insertTestData();
            
            logger.info("✅ Base de données initialisée avec succès !");
            
        } catch (Exception e) {
            logger.error("❌ Erreur lors de l'initialisation de la base de données: {}", e.getMessage());
            // Ne pas arrêter l'application, juste logger l'erreur
        }
    }

    private void createUsersTable() {
        try {
            // D'abord, essayer de mettre à jour la structure existante
            updateExistingTable();

            String createTableSQL = """
                CREATE TABLE IF NOT EXISTS users (
                    id BIGSERIAL PRIMARY KEY,
                    nom VARCHAR(50) NOT NULL,
                    prenom VARCHAR(50) NOT NULL,
                    email VARCHAR(100) UNIQUE NOT NULL,
                    mot_de_passe VARCHAR(255) NOT NULL,
                    role VARCHAR(30) NOT NULL CHECK (role IN ('ADMIN', 'COACH', 'JOUEUR', 'RESPONSABLE_FINANCIER', 'STAFF_MEDICAL', 'INVITE')),
                    actif BOOLEAN DEFAULT TRUE,
                    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    date_modification TIMESTAMP
                )
                """;

            jdbcTemplate.execute(createTableSQL);
            logger.info("✅ Table 'users' créée ou vérifiée");

            // Créer les index
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_users_email ON users(email)");
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_users_role ON users(role)");
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_users_actif ON users(actif)");

            logger.info("✅ Index créés");

        } catch (Exception e) {
            logger.warn("⚠️ Erreur lors de la création de la table: {}", e.getMessage());
        }
    }

    private void updateExistingTable() {
        try {
            // Vérifier si la table existe
            Integer tableExists = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'users'",
                Integer.class
            );

            if (tableExists != null && tableExists > 0) {
                logger.info("🔄 Mise à jour de la structure de la table existante...");

                // Modifier la taille de la colonne role
                jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN role TYPE VARCHAR(30)");
                logger.info("✅ Colonne 'role' mise à jour vers VARCHAR(30)");

                // Supprimer l'ancienne contrainte CHECK
                jdbcTemplate.execute("ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check");

                // Ajouter la nouvelle contrainte CHECK avec tous les rôles
                jdbcTemplate.execute("""
                    ALTER TABLE users ADD CONSTRAINT users_role_check
                    CHECK (role IN ('ADMIN', 'COACH', 'JOUEUR', 'RESPONSABLE_FINANCIER', 'STAFF_MEDICAL', 'INVITE'))
                    """);
                logger.info("✅ Contrainte CHECK mise à jour avec les nouveaux rôles");
            }

        } catch (Exception e) {
            logger.info("ℹ️ Mise à jour de la table: {} (normal si table n'existe pas encore)", e.getMessage());
        }
    }

    private void insertTestData() {
        try {
            // Encoder les mots de passe
            String encodedPassword = passwordEncoder.encode("password123");
            
            // Insérer les utilisateurs de test seulement s'ils n'existent pas déjà
            String insertSQL = """
                INSERT INTO users (nom, prenom, email, mot_de_passe, role, actif, date_creation) 
                VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
                ON CONFLICT (email) DO NOTHING
                """;
            
            // Admin de test
            int adminInserted = jdbcTemplate.update(insertSQL, "Admin", "System", "admin@cok.tn", encodedPassword, "ADMIN", true);
            if (adminInserted > 0) {
                logger.info("✅ Utilisateur admin de test créé");
            } else {
                logger.info("ℹ️ Utilisateur admin de test existe déjà");
            }
            
            // Coach de test
            int coachInserted = jdbcTemplate.update(insertSQL, "Coach", "Test", "coach@cok.tn", encodedPassword, "COACH", true);
            if (coachInserted > 0) {
                logger.info("✅ Utilisateur coach de test créé");
            } else {
                logger.info("ℹ️ Utilisateur coach de test existe déjà");
            }
            
            // Joueur de test
            int joueurInserted = jdbcTemplate.update(insertSQL, "Joueur", "Test", "joueur@cok.tn", encodedPassword, "JOUEUR", true);
            if (joueurInserted > 0) {
                logger.info("✅ Utilisateur joueur de test créé");
            } else {
                logger.info("ℹ️ Utilisateur joueur de test existe déjà");
            }
            
            // Compter le nombre total d'utilisateurs
            Integer totalCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
            logger.info("📊 Total: {} utilisateur(s) dans la base de données", totalCount);
            
            logger.info("🎉 Initialisation des données de test terminée !");
            
        } catch (Exception e) {
            logger.warn("⚠️ Erreur lors de l'insertion des données de test: {}", e.getMessage());
        }
    }
}

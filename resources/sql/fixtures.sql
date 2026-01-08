-- ===========================================
-- MDD (Monde de Dév) - Fixtures / Données de test
-- ===========================================
-- Mot de passe pour tous les utilisateurs : Password1!
-- ===========================================

SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

USE mdd;

-- ===========================================
-- USERS
-- ===========================================
INSERT INTO users (email, username, password) VALUES
('alice@test.com', 'alice', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG'),
('bob@test.com', 'bob', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG'),
('charlie@test.com', 'charlie', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG');

-- ===========================================
-- TOPICS - Thèmes de programmation
-- ===========================================
INSERT INTO topics (name, description) VALUES
('Java', 'Langage de programmation orienté objet, robuste et portable. Utilisé pour les applications enterprise, Android, et bien plus.'),
('Angular', 'Framework frontend TypeScript développé par Google. Idéal pour les applications web single-page (SPA).'),
('Spring Boot', 'Framework Java pour créer des applications Spring autonomes et production-ready avec une configuration minimale.'),
('Python', 'Langage polyvalent et accessible, populaire pour le scripting, le machine learning et le développement web.'),
('JavaScript', 'Langage incontournable du web, côté client et serveur (Node.js). Écosystème riche avec npm.'),
('DevOps', 'Pratiques et outils pour CI/CD, conteneurisation (Docker), orchestration (Kubernetes) et infrastructure as code.');

-- ===========================================
-- SUBSCRIPTIONS - Abonnements
-- ===========================================
INSERT INTO subscriptions (user_id, topic_id) VALUES
-- Alice est abonnée à Java, Angular et Spring Boot
(1, 1), (1, 2), (1, 3),
-- Bob est abonné à Java et Python
(2, 1), (2, 4),
-- Charlie est abonné à Angular, JavaScript et DevOps
(3, 2), (3, 5), (3, 6);

-- ===========================================
-- POSTS - Articles
-- ===========================================
INSERT INTO posts (title, content, author_id, topic_id) VALUES
('Introduction à Spring Boot 3',
 'Spring Boot 3 simplifie considérablement le développement d''applications Java. Avec sa configuration automatique et ses starters, vous pouvez démarrer un projet en quelques minutes. Dans cet article, nous allons explorer les principales nouveautés de la version 3.x, notamment le support natif de GraalVM et les améliorations de performance.',
 1, 3),

('Les nouveautés d''Angular 20',
 'Angular 20 apporte des améliorations significatives en termes de performance et d''expérience développeur. Le nouveau système de build basé sur esbuild réduit considérablement les temps de compilation. Les Signals, introduits dans les versions précédentes, sont maintenant matures et offrent une alternative élégante aux Observables pour la gestion d''état local.',
 2, 2),

('Python pour le Machine Learning',
 'Python s''est imposé comme le langage de référence pour le machine learning grâce à ses bibliothèques comme TensorFlow, PyTorch et scikit-learn. Sa syntaxe claire et sa communauté active en font un choix idéal pour les data scientists. Découvrons ensemble comment démarrer votre premier projet ML.',
 2, 4),

('Docker en production : bonnes pratiques',
 'Déployer des containers Docker en production nécessite une attention particulière à la sécurité et aux performances. Dans cet article, nous abordons les bonnes pratiques : images légères basées sur Alpine, multi-stage builds, gestion des secrets, health checks et stratégies de logging.',
 3, 6),

('Les nouveautés de Java 21',
 'Java 21, version LTS, introduit des fonctionnalités très attendues : les Virtual Threads (Project Loom) pour une concurrence simplifiée, le pattern matching amélioré, les record patterns, et les sequenced collections. Ces ajouts modernisent considérablement le langage.',
 1, 1);

-- ===========================================
-- COMMENTS - Commentaires
-- ===========================================
INSERT INTO comments (content, author_id, post_id) VALUES
('Super article ! Ça m''a aidé à démarrer mon projet Spring Boot.', 2, 1),
('Très utile, merci pour ces explications détaillées.', 3, 1),
('J''ai une question sur les Signals : peut-on les combiner avec RxJS ?', 1, 2),
('Excellent résumé des bonnes pratiques Docker.', 1, 4),
('Les Virtual Threads changent vraiment la donne pour les applications I/O intensives.', 2, 5),
('Merci pour cet article complet sur Python et le ML !', 3, 3);

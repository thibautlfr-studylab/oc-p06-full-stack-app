-- ===========================================
-- MDD (Monde de Dev) - Fixtures / Donnees de test
-- ===========================================
-- Mot de passe pour tous les utilisateurs : Password1!
-- Hash BCrypt (cost=10)
-- ===========================================

USE mdd;

-- ===========================================
-- USERS
-- ===========================================
INSERT INTO users (email, username, password) VALUES
('alice@test.com', 'alice', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG'),
('bob@test.com', 'bob', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG'),
('charlie@test.com', 'charlie', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG');

-- ===========================================
-- TOPICS - Themes de programmation
-- ===========================================
INSERT INTO topics (name, description) VALUES
('Java', 'Langage de programmation oriente objet, robuste et portable. Utilise pour les applications enterprise, Android, et bien plus.'),
('Angular', 'Framework frontend TypeScript developpe par Google. Ideal pour les applications web single-page (SPA).'),
('Spring Boot', 'Framework Java pour creer des applications Spring autonomes et production-ready avec une configuration minimale.'),
('Python', 'Langage polyvalent et accessible, populaire pour le scripting, le machine learning et le developpement web.'),
('JavaScript', 'Langage incontournable du web, cote client et serveur (Node.js). Ecosysteme riche avec npm.'),
('DevOps', 'Pratiques et outils pour CI/CD, containerisation (Docker), orchestration (Kubernetes) et infrastructure as code.');

-- ===========================================
-- SUBSCRIPTIONS - Abonnements
-- ===========================================
INSERT INTO subscriptions (user_id, topic_id) VALUES
-- Alice est abonnee a Java, Angular et Spring Boot
(1, 1), (1, 2), (1, 3),
-- Bob est abonne a Java et Python
(2, 1), (2, 4),
-- Charlie est abonne a Angular, JavaScript et DevOps
(3, 2), (3, 5), (3, 6);

-- ===========================================
-- POSTS - Articles
-- ===========================================
INSERT INTO posts (title, content, author_id, topic_id) VALUES
('Introduction a Spring Boot 3',
 'Spring Boot 3 simplifie considerablement le developpement d''applications Java. Avec sa configuration automatique et ses starters, vous pouvez demarrer un projet en quelques minutes. Dans cet article, nous allons explorer les principales nouveautes de la version 3.x, notamment le support natif de GraalVM et les ameliorations de performance.',
 1, 3),

('Les nouveautes d''Angular 20',
 'Angular 20 apporte des ameliorations significatives en termes de performance et d''experience developpeur. Le nouveau systeme de build base sur esbuild reduit considerablement les temps de compilation. Les Signals, introduits dans les versions precedentes, sont maintenant matures et offrent une alternative elegante aux Observables pour la gestion d''etat local.',
 2, 2),

('Python pour le Machine Learning',
 'Python s''est impose comme le langage de reference pour le machine learning grace a ses bibliotheques comme TensorFlow, PyTorch et scikit-learn. Sa syntaxe claire et sa communaute active en font un choix ideal pour les data scientists. Decouvrons ensemble comment demarrer votre premier projet ML.',
 2, 4),

('Docker en production : bonnes pratiques',
 'Deployer des containers Docker en production necessite une attention particuliere a la securite et aux performances. Dans cet article, nous abordons les bonnes pratiques : images legeres basees sur Alpine, multi-stage builds, gestion des secrets, health checks et strategies de logging.',
 3, 6),

('Les nouveautes de Java 21',
 'Java 21, version LTS, introduit des fonctionnalites tres attendues : les Virtual Threads (Project Loom) pour une concurrence simplifiee, le pattern matching ameliore, les record patterns, et les sequenced collections. Ces ajouts modernisent considerablement le langage.',
 1, 1);

-- ===========================================
-- COMMENTS - Commentaires
-- ===========================================
INSERT INTO comments (content, author_id, post_id) VALUES
('Super article ! Ca m''a aide a demarrer mon projet Spring Boot.', 2, 1),
('Tres utile, merci pour ces explications detaillees.', 3, 1),
('J''ai une question sur les Signals : peut-on les combiner avec RxJS ?', 1, 2),
('Excellent resume des bonnes pratiques Docker.', 1, 4),
('Les Virtual Threads changent vraiment la donne pour les applications I/O intensives.', 2, 5),
('Merci pour cet article complet sur Python et le ML !', 3, 3);

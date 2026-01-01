.PHONY: install clean db-up db-down db-reset help

# Afficher l'aide
help:
	@echo "Commandes disponibles:"
	@echo "  make install   - Installer les dependances front et back"
	@echo "  make clean     - Nettoyer les dependances et fichiers generes"
	@echo "  make db-up     - Demarrer la base de donnees MySQL (Docker)"
	@echo "  make db-down   - Arreter la base de donnees"
	@echo "  make db-reset  - Reset complet de la DB (supprime les donnees)"

# Installer les dependances front et back
install:
	@echo "Installation des dependances frontend..."
	cd front && npm install
	@echo "Installation des dependances backend..."
	cd back && ./mvnw dependency:resolve

# Nettoyer les dependances
clean:
	@echo "Nettoyage du frontend..."
	rm -rf front/node_modules
	@echo "Nettoyage du backend..."
	cd back && ./mvnw clean

# Demarrer la base de donnees
db-up:
	@echo "Demarrage de MySQL..."
	docker-compose up -d
	@echo "MySQL demarre sur localhost:3306"

# Arreter la base de donnees
db-down:
	@echo "Arret de MySQL..."
	docker-compose down

# Reset complet de la DB (supprime les donnees)
db-reset:
	@echo "Reset de la base de donnees..."
	docker-compose down -v
	docker-compose up -d
	@echo "Base de donnees reinitalisee"

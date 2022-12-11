CREATE TABLE Utilisateur(
	idUtilisateur integer NOT NULL,

	CONSTRAINT Utilisateur_PK PRIMARY KEY (idUtilisateur)
);

CREATE TABLE Client (
	mailClient varchar(50) NOT NULL,
	idUtilisateur integer NOT NULL,
	nomClient varchar(20) NOT NULL,
	motDePasse varchar(10) NOT NULL,

	CONSTRAINT Client_FK FOREIGN KEY (idUtilisateur) REFERENCES Utilisateur(idUtilisateur),
    CONSTRAINT Client_PK PRIMARY KEY (mailClient)
);

CREATE TABLE Restaurant(
	mailRestaurant varchar(50) NOT NULL ,
	nomRestaurant varchar(20) NOT NULL,
	adresseRestaurant varchar(30) NOT NULL,
	nombrePlaces integer NOT NULL,
	textPresentation varchar(255) NOT NULL,

	CONSTRAINT Restaurant_PK PRIMARY KEY (mailRestaurant)
);

CREATE TABLE Commande(
    dateCommande date NOT NULL,
    heureCommande varchar(20) NOT NULL,
    idUtilisateur integer NOT NULL,
    mailRestaurant varchar(50) NOT NULL,
    prixCommande float NOT NULL,
    statut varchar(50) NOT NULL,

    CONSTRAINT statut_enum CHECK (statut IN ('attente de confirmation','validée','disponible','en livraison', 'annulée par le client', 'annulée par le restaurant')),
    CONSTRAINT CommandeUtilisateur_FK FOREIGN KEY(idUtilisateur)
    REFERENCES Utilisateur(idUtilisateur) ON UPDATE CASCADE,
    CONSTRAINT CommandeRestaurant_FK FOREIGN KEY(mailRestaurant) REFERENCES Restaurant(mailRestaurant),

    PRIMARY KEY (mailRestaurant, idUtilisateur, dateCommande,heureCommande)
);

CREATE TABLE Plat (
	numeroPlat integer NOT NULL,
	mailRestaurant varchar(50) NOT NULL,
	nomPlat varchar(20) NOT NULL,
	description varchar(255) NOT NULL,
	prix float NOT NULL,

	CONSTRAINT Plat_FK  FOREIGN KEY(mailRestaurant) REFERENCES Restaurant(mailRestaurant),

	CONSTRAINT Plat_PK PRIMARY KEY(numeroPlat , mailRestaurant)
);

CREATE TABLE Allergenes(
	nomAllergene varchar(20) NOT NULL,

	CONSTRAINT Allergenes_PK PRIMARY KEY(nomAllergene)
);

CREATE TABLE aPourAllergene(
	numeroPlat integer NOT NULL,
	mailRestaurant varchar(50) NOT NULL,
	nomAllergene varchar(20) NOT NULL,

	CONSTRAINT aPourAllergeneRestaurant_FK  FOREIGN KEY (numeroPlat, mailRestaurant) REFERENCES Plat(numeroPlat,mailRestaurant),
	CONSTRAINT ApourAllergeneAllergene_FK  FOREIGN KEY(nomAllergene) REFERENCES Allergenes(nomAllergene),

	CONSTRAINT aPourAllergene_PK  PRIMARY KEY(numeroPlat, mailRestaurant, nomAllergene)
);

CREATE TABLE aPourPlats (
    dateCommande date NOT NULL,
    heureCommande varchar(20)  NOT NULL,
    idUtilisateur integer NOT NULL,
    mailRestaurant varchar(50) NOT NULL,
    numeroPlat integer NOT NULL,
    quantite integer NOT NULL,
    CONSTRAINT aPourPlatsRestaurant_FK FOREIGN KEY (mailRestaurant, idUtilisateur, dateCommande,heureCommande)
    REFERENCES Commande(mailRestaurant, idUtilisateur, dateCommande,heureCommande) ON UPDATE CASCADE,
    CONSTRAINT aPourPlatsPlats_FK FOREIGN KEY (numeroPlat,mailRestaurant)
    REFERENCES Plat(numeroPlat,mailRestaurant),
    CONSTRAINT aPourPlats_PK  PRIMARY KEY (mailRestaurant, idUtilisateur, dateCommande,heureCommande, numeroPlat)
);


CREATE TABLE CommandeSurPlace(
	dateCommande date NOT NULL,
    heureCommande varchar(20) NOT NULL,
	idUtilisateur integer NOT NULL,
	mailRestaurant varchar(50) NOT NULL,
	nombrePersonnes integer NOT NULL,
	heureArrive varchar(20) NOT NULL,

	CONSTRAINT CommandeSurPlace_FK FOREIGN KEY(mailRestaurant, idUtilisateur, dateCommande,heureCommande)
    REFERENCES Commande(mailRestaurant, idUtilisateur, dateCommande,heureCommande) ON UPDATE CASCADE,
	CONSTRAINT SurPlace_PK PRIMARY KEY (dateCommande,heureCommande, idUtilisateur, mailRestaurant)
);

CREATE TABLE CommandeLivraison(
    dateCommande date NOT NULL,
    heureCommande varchar(20) NOT NULL,
    idUtilisateur integer NOT NULL,
    mailRestaurant varchar(50) NOT NULL,
	adresseLiv varchar(50) NOT NULL,

    CONSTRAINT CommandeLivraison_FK FOREIGN KEY(mailRestaurant, idUtilisateur, dateCommande,heureCommande)
    REFERENCES Commande(mailRestaurant, idUtilisateur, dateCommande,heureCommande) ON UPDATE CASCADE,
	CONSTRAINT CommandeLivraison_PK PRIMARY KEY (dateCommande,heureCommande,idUtilisateur,mailRestaurant)
);

CREATE TABLE Evaluation(
    dateEvaluation date NOT NULL,
    heureEvaluation varchar(20) NOT NULL,
    avis varchar(255)  NOT NULL,
	note int NOT NULL,
	CONSTRAINT Evaluation_PK PRIMARY KEY(dateEvaluation,heureEvaluation, avis , note )
);

CREATE TABLE aPourEvaluation(
	mailRestaurant varchar(50) NOT NULL,
	idUtilisateur integer NOT NULL,
	dateCommande date NOT NULL,
    heureCommande varchar(20) NOT NULL,
	dateEvaluation date NOT NULL,
	heureEvaluation varchar(20) NOT NULL,
	avis varchar(255) NOT NULL,
	note integer NOT NULL,

	CONSTRAINT EvalResto_FK FOREIGN KEY (mailRestaurant, idUtilisateur, dateCommande,heureCommande)
    REFERENCES Commande(mailRestaurant, idUtilisateur, dateCommande,heureCommande) ON UPDATE CASCADE,
    CONSTRAINT EvalDate_FK FOREIGN KEY (dateEvaluation,heureEvaluation,avis,note) REFERENCES Evaluation (dateEvaluation,heureEvaluation,avis,note),
	CONSTRAINT aPourEvaluation_PK PRIMARY KEY(dateCommande,heureCommande,idUtilisateur,mailRestaurant)
);

CREATE TABLE TexteAideLivreur (
    texteAide varchar(255) NOT NULL,
    CONSTRAINT TexteAideLivreur_PK PRIMARY KEY (texteAide)
);

CREATE TABLE aPourTexteAide (
    dateCommande date NOT NULL,
    heureCommande varchar(20) NOT NULL,
    idUtilisateur integer NOT NULL,
    mailRestaurant varchar(50) NOT NULL,
    texteAide varchar(255) NOT NULL,

    CONSTRAINT aPourtexteRestaurant_FK FOREIGN KEY (dateCommande,heureCommande,idUtilisateur,mailRestaurant)
    REFERENCES CommandeLivraison(dateCommande,heureCommande,idUtilisateur,mailRestaurant) ON UPDATE CASCADE,
    CONSTRAINT aPourTexte_FK FOREIGN KEY (texteAide) REFERENCES TexteAideLivreur(texteAide),
    CONSTRAINT aPourTexteAide_PK PRIMARY KEY (dateCommande,heureCommande,idUtilisateur,mailRestaurant)
);

CREATE TABLE Categories(
	nomCategorie varchar(20) NOT NULL,

	CONSTRAINT Categories_PK PRIMARY KEY(nomCategorie)
);

CREATE TABLE aPourCategorie(
	mailRestaurant varchar(50) NOT NULL,
	nomCategorie varchar(50) NOT NULL,
	CONSTRAINT aPourCategorieRest_FK FOREIGN KEY(mailRestaurant) REFERENCES Restaurant(mailRestaurant),
	CONSTRAINT aPourCategorie_FK FOREIGN KEY(nomCategorie) REFERENCES Categories(nomCategorie),
	CONSTRAINT aPourCategorie_PK PRIMARY KEY (mailRestaurant,nomCategorie)
);

CREATE TABLE aPourCategorieMere(
	nomCategorieFille varchar(50) NOT NULL,
	nomCategorie varchar(50) NOT NULL,
	CONSTRAINT aPourCategorieMere_FK FOREIGN KEY(nomCategorie) REFERENCES Categories(nomCategorie),
	CONSTRAINT aPourCategorieMere_PK PRIMARY KEY (nomCategorieFille)
);

CREATE TABLE heureLiv(
    heureLiv varchar(20) NOT NULL,
    CONSTRAINT heureLiv_PK PRIMARY KEY (heureLiv)
);
CREATE TABLE aPourHeureLiv(
	dateCommande date NOT NULL,
    heureCommande varchar(20) NOT NULL,
	idUtilisateur integer  NOT NULL,
	mailRestaurant varchar(50) NOT NULL,
	heureLiv varchar(20) NOT NULL,

	CONSTRAINT aPourHeureLivRest_FK FOREIGN KEY (dateCommande,heureCommande,idUtilisateur,mailRestaurant)
    REFERENCES CommandeLivraison(dateCommande,heureCommande,idUtilisateur,mailRestaurant) ON UPDATE CASCADE,
	CONSTRAINT aPourheureLiv_FK FOREIGN KEY (heureLiv) REFERENCES heureLiv(heureLiv),

	CONSTRAINT aPourHeureLiv_PK PRIMARY KEY (dateCommande,heureCommande,idUtilisateur,mailRestaurant)
);

CREATE TABLE Horaires (
	mailRestaurant varchar(50) NOT NULL,
	jour varchar(20) NOT NULL,
	heureOuverture varchar(10) NOT NULL,
	heureFermeture varchar(10) NOT NULL,

    CONSTRAINT jour_enum CHECK (Jour IN ('Lundi','Mardi','Mercredi','Jeudi','Vendredi', 'Samedi', 'Dimanche')),
	CONSTRAINT Horaires_FK FOREIGN KEY(mailRestaurant) REFERENCES Restaurant(mailRestaurant),
	CONSTRAINT Horaires_PK PRIMARY KEY (mailRestaurant,jour,heureOuverture)
);

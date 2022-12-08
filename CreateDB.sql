CREATE TABLE Utilisateur(
	idUtilisateur integer not null ,

	CONSTRAINT Utilisateur_PK PRIMARY KEY (idUtilisateur)
);

CREATE TABLE Client (
	mailClient varchar(50) not null,
	idUtilisateur integer not null,
	nomClient varchar(20) not null,
	motDePasse varchar(10) not null,

	CONSTRAINT Client_FK FOREIGN KEY (idUtilisateur) REFERENCES Utilisateur(idUtilisateur),
    CONSTRAINT Client_PK PRIMARY KEY (mailClient)
);

CREATE TABLE Restaurant(
	mailRestaurant varchar(50) not null ,
	nomRestaurant varchar(20) not null,
	adresseRestaurant varchar(30) not null ,
	nombrePlaces integer not null not null,
	textPresentation varchar(255),

	CONSTRAINT Restaurant_PK PRIMARY KEY (mailRestaurant)
);

CREATE TABLE Commande(
    dateCommande date,
    heureCommande varchar(20),
    idUtilisateur integer,
    mailRestaurant varchar(50),
    prixCommande float,
    statut varchar(50),

    CONSTRAINT statut_enum CHECK (statut IN ('attente de confirmation','validée','disponible','en livraison', 'annulée par le client', 'annulée par le restaurant')),
    CONSTRAINT CommandeUtilisateur_FK FOREIGN KEY(idUtilisateur)
    REFERENCES Utilisateur(idUtilisateur) ON UPDATE CASCADE,
    CONSTRAINT CommandeRestaurant_FK FOREIGN KEY(mailRestaurant) REFERENCES Restaurant(mailRestaurant),

    PRIMARY KEY (mailRestaurant, idUtilisateur, dateCommande,heureCommande)
);

CREATE TABLE Plat (
	numeroPlat integer not null,
	mailRestaurant varchar(50) not null ,
	nomPlat varchar(20) not null ,
	description varchar(255) not null ,
	prix float not null ,

	CONSTRAINT Plat_FK  FOREIGN KEY(mailRestaurant) REFERENCES Restaurant(mailRestaurant),

	CONSTRAINT Plat_PK PRIMARY KEY(numeroPlat , mailRestaurant)
);

CREATE TABLE Allergenes(
	nomAllergene varchar(20) not null,

	CONSTRAINT Allergenes_PK PRIMARY KEY(nomAllergene)
);

CREATE TABLE aPourAllergene(
	numeroPlat integer not null,
	mailRestaurant varchar(50) not null,
	nomAllergene varchar(20) not null,

	CONSTRAINT aPourAllergeneRestaurant_FK  FOREIGN KEY (numeroPlat, mailRestaurant) REFERENCES Plat(numeroPlat,mailRestaurant),
	CONSTRAINT ApourAllergeneAllergene_FK  FOREIGN KEY(nomAllergene) REFERENCES Allergenes(nomAllergene),

	CONSTRAINT aPourAllergene_PK  PRIMARY KEY(numeroPlat, mailRestaurant, nomAllergene)
);

CREATE TABLE aPourPlats (
    dateCommande date not null,
    heureCommande varchar(20)  not null,
    idUtilisateur integer not null,
    mailRestaurant varchar(50) not null,
    numeroPlat integer not null ,
    quantite integer not null,
    CONSTRAINT aPourPlatsRestaurant_FK FOREIGN KEY (mailRestaurant, idUtilisateur, dateCommande,heureCommande)
    REFERENCES Commande(mailRestaurant, idUtilisateur, dateCommande,heureCommande) ON UPDATE CASCADE,
    CONSTRAINT aPourPlatsPlats_FK FOREIGN KEY (numeroPlat,mailRestaurant)
    REFERENCES Plat(numeroPlat,mailRestaurant),
    CONSTRAINT aPourPlats_PK  PRIMARY KEY (mailRestaurant, idUtilisateur, dateCommande,heureCommande, numeroPlat)
);


CREATE TABLE CommandeSurPlace(
	dateCommande date not null ,
    heureCommande varchar(20) not null,
	idUtilisateur integer not null,
	mailRestaurant varchar(50) not null,
	nombrePersonnes integer not null,
	heureArrive varchar(20) not null,

	CONSTRAINT CommandeSurPlace_FK FOREIGN KEY(mailRestaurant, idUtilisateur, dateCommande,heureCommande)
    REFERENCES Commande(mailRestaurant, idUtilisateur, dateCommande,heureCommande) ON UPDATE CASCADE,
	CONSTRAINT SurPlace_PK PRIMARY KEY (dateCommande,heureCommande, idUtilisateur, mailRestaurant)
);

CREATE TABLE CommandeLivraison(
    dateCommande date not null,
    heureCommande varchar(20) not null not null not null,
    idUtilisateur integer not null not null not null,
    mailRestaurant varchar(50) not null not null not null,
	adresseLiv varchar(50) not null not null not null,

    CONSTRAINT CommandeLivraison_FK FOREIGN KEY(mailRestaurant, idUtilisateur, dateCommande,heureCommande)
    REFERENCES Commande(mailRestaurant, idUtilisateur, dateCommande,heureCommande) ON UPDATE CASCADE,
	CONSTRAINT CommandeLivraison_PK PRIMARY KEY (dateCommande,heureCommande,idUtilisateur,mailRestaurant)
);

CREATE TABLE Evaluation(
    dateEvaluation date not null not null,
    heureEvaluation varchar(20) not null not null,
    avis varchar(255)  not null not null,
	note int not null not null ,
	CONSTRAINT Evaluation_PK PRIMARY KEY(dateEvaluation,heureEvaluation, avis , note )
);

CREATE TABLE aPourEvaluation(
	mailRestaurant varchar(50) not null,
	idUtilisateur integer not null ,
	dateCommande date not null,
    heureCommande varchar(20) not null,
	dateEvaluation date not null,
	heureEvaluation varchar(20) not null,
	avis varchar(255) not null ,
	note integer not null ,

	CONSTRAINT EvalResto_FK FOREIGN KEY (mailRestaurant, idUtilisateur, dateCommande,heureCommande)
    REFERENCES Commande(mailRestaurant, idUtilisateur, dateCommande,heureCommande) ON UPDATE CASCADE,
    CONSTRAINT EvalDate_FK FOREIGN KEY (dateEvaluation,heureEvaluation,avis,note) REFERENCES Evaluation (dateEvaluation,heureEvaluation,avis,note),
	CONSTRAINT aPourEvaluation_PK PRIMARY KEY(dateCommande,heureCommande,idUtilisateur,mailRestaurant)
);

CREATE TABLE TexteAideLivreur (
    texteAide varchar(255) not null,
    CONSTRAINT TexteAideLivreur_PK PRIMARY KEY (texteAide)
);

CREATE TABLE aPourTexteAide (
    dateCommande date not null,
    heureCommande varchar(20) not null,
    idUtilisateur integer not null ,
    mailRestaurant varchar(50) not null,
    texteAide varchar(255) not null ,

    CONSTRAINT aPourtexteRestaurant_FK FOREIGN KEY (dateCommande,heureCommande,idUtilisateur,mailRestaurant)
    REFERENCES CommandeLivraison(dateCommande,heureCommande,idUtilisateur,mailRestaurant) ON UPDATE CASCADE,
    CONSTRAINT aPourTexte_FK FOREIGN KEY (texteAide) REFERENCES TexteAideLivreur(texteAide),
    CONSTRAINT aPourTexteAide_PK PRIMARY KEY (dateCommande,heureCommande,idUtilisateur,mailRestaurant)
);

CREATE TABLE Categories(
	nomCategorie varchar(20) not null,

	CONSTRAINT Categories_PK PRIMARY KEY(nomCategorie)
);

CREATE TABLE aPourCategorie(
	mailRestaurant varchar(50) not null,
	nomCategorie varchar(50) not null,
	CONSTRAINT aPourCategorieRest_FK FOREIGN KEY(mailRestaurant) REFERENCES Restaurant(mailRestaurant),
	CONSTRAINT aPourCategorie_FK FOREIGN KEY(nomCategorie) REFERENCES Categories(nomCategorie),
	CONSTRAINT aPourCategorie_PK PRIMARY KEY (mailRestaurant,nomCategorie)
);

CREATE TABLE aPourCategorieMere(
	nomCategorieFille varchar(50) not null,
	nomCategorie varchar(50) not null,
	CONSTRAINT aPourCategorieMere_FK FOREIGN KEY(nomCategorie) REFERENCES Categories(nomCategorie),
	CONSTRAINT aPourCategorieMere_PK PRIMARY KEY (nomCategorieFille)
);

CREATE TABLE heureLiv(
    heureLiv varchar(20) not null,
    CONSTRAINT heureLiv_PK PRIMARY KEY (heureLiv)
);
CREATE TABLE aPourHeureLiv(
	dateCommande date not null not null,
    heureCommande varchar(20) not null not null,
	idUtilisateur integer  not null not null,
	mailRestaurant varchar(50) not null not null ,
	heureLiv varchar(20) not null not null ,

	CONSTRAINT aPourHeureLivRest_FK FOREIGN KEY (dateCommande,heureCommande,idUtilisateur,mailRestaurant)
    REFERENCES CommandeLivraison(dateCommande,heureCommande,idUtilisateur,mailRestaurant) ON UPDATE CASCADE,
	CONSTRAINT aPourheureLiv_FK FOREIGN KEY (heureLiv) REFERENCES heureLiv(heureLiv),

	CONSTRAINT aPourHeureLiv_PK PRIMARY KEY (dateCommande,heureCommande,idUtilisateur,mailRestaurant)
);

CREATE TABLE Horaires (
	mailRestaurant varchar(50) not null,
	jour varchar(20),
	heureOuverture varchar(10) not null,
	heureFermeture varchar(10) not null,

    CONSTRAINT jour_enum CHECK (Jour IN ('Lundi','Mardi','Mercredi','Jeudi','Vendredi', 'Samedi', 'Dimanche')),
	CONSTRAINT Horaires_FK FOREIGN KEY(mailRestaurant) REFERENCES Restaurant(mailRestaurant),
	CONSTRAINT Horaires_PK PRIMARY KEY (mailRestaurant,jour,heureOuverture)
);

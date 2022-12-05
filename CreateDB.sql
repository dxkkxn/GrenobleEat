CREATE TABLE Utilisateur(
	idUtilisateur integer,
	PRIMARY KEY (idUtilisateur)
);

CREATE TABLE Client (
	mailClient varchar(50) ,
	idUtilisateur integer ,
	nomClient varchar(20),
	motDePasse varchar(10),

	FOREIGN KEY (idUtilisateur) REFERENCES Utilisateur(idUtilisateur),

    PRIMARY KEY (mailClient)
);

CREATE TABLE Restaurant(
	mailRestaurant varchar(50),
	nomRestaurant varchar(20),
	adresseRestaurant varchar(30),
	nombrePlaces integer,
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
    CONSTRAINT statut_enum CHECK (statut IN ('attente de confirmation',
    'validée','disponible','en livraison', 'annulée par le client', 'annulée par le restaurant')),

    FOREIGN KEY(idUtilisateur) REFERENCES Utilisateur(idUtilisateur),
    FOREIGN KEY(mailRestaurant) REFERENCES Restaurant(mailRestaurant),

    PRIMARY KEY (mailRestaurant, idUtilisateur, dateCommande,heureCommande)
);

CREATE TABLE Plat (
	numeroPlat integer,
	mailRestaurant varchar(50) ,
	nomPlat varchar(20) ,
	description varchar(255) ,
	prix float ,

	FOREIGN KEY(mailRestaurant) REFERENCES Restaurant(mailRestaurant),

	PRIMARY KEY(numeroPlat , mailRestaurant)
);

CREATE TABLE Allergenes(
	nomAllergene varchar(20),

	PRIMARY KEY(nomAllergene)
);

CREATE TABLE aPourAllergene(
	numeroPlat integer,
	mailRestaurant varchar(50),
	nomAllergene varchar(20) ,

	FOREIGN KEY (numeroPlat, mailRestaurant) REFERENCES Plat(numeroPlat,mailRestaurant),
	FOREIGN KEY(nomAllergene) REFERENCES Allergenes(nomAllergene),

	PRIMARY KEY(numeroPlat, mailRestaurant, nomAllergene)
);

CREATE TABLE aPourPlats (
    dateCommande date,
    heureCommande varchar(20),
    idUtilisateur integer,
    mailRestaurant varchar(50),
    numeroPlat integer ,
    quantite integer,
    FOREIGN KEY (mailRestaurant, idUtilisateur, dateCommande,heureCommande) REFERENCES Commande(mailRestaurant, idUtilisateur, dateCommande,heureCommande),

    FOREIGN KEY (numeroPlat,mailRestaurant) REFERENCES Plat(numeroPlat,mailRestaurant),
    PRIMARY KEY (mailRestaurant, idUtilisateur, dateCommande,heureCommande, numeroPlat)
);


CREATE TABLE CommandeSurPlace(
	dateCommande date,
    heureCommande varchar(20),
	idUtilisateur integer,
	mailRestaurant varchar(50),
	nombrePersonnes integer,
	heureArrive timestamp,

	FOREIGN KEY(mailRestaurant, idUtilisateur, dateCommande,heureCommande) REFERENCES Commande(mailRestaurant, idUtilisateur, dateCommande,heureCommande),
	CONSTRAINT SurPlace_PK PRIMARY KEY (dateCommande,heureCommande, idUtilisateur, mailRestaurant)
);

CREATE TABLE CommandeLivraison(
    dateCommande date,
    heureCommande varchar(20),
    idUtilisateur integer,
    mailRestaurant varchar(50),
	adresseLiv varchar(50),

    FOREIGN KEY(mailRestaurant, idUtilisateur, dateCommande,heureCommande) REFERENCES Commande(mailRestaurant, idUtilisateur, dateCommande,heureCommande),
	PRIMARY KEY (dateCommande,heureCommande,idUtilisateur,mailRestaurant)
);

CREATE TABLE Evaluation(
    dateEvaluation date,
    heureEvaluation varchar(20),
    avis varchar(255) ,
	note int ,
	PRIMARY KEY(dateEvaluation,heureEvaluation, avis , note )
);

CREATE TABLE aPourEvaluation(
	mailRestaurant varchar(50),
	idUtilisateur integer ,
	dateCommande date,
    heureCommande varchar(20),
	dateEvaluation date,
	heureEvaluation varchar(20),
	avis varchar(255) ,
	note integer ,

	FOREIGN KEY (mailRestaurant, idUtilisateur, dateCommande,heureCommande) REFERENCES Commande(mailRestaurant, idUtilisateur, dateCommande,heureCommande),
    FOREIGN KEY (dateEvaluation,heureEvaluation,avis,note) REFERENCES Evaluation (dateEvaluation,heureEvaluation,avis,note),
	PRIMARY KEY(dateCommande,heureCommande,idUtilisateur,mailRestaurant)
);

CREATE TABLE TexteAideLivreur (
    texteAide varchar(255) ,
    PRIMARY KEY (texteAide)
);

CREATE TABLE aPourTexteAide (
    dateCommande date,
    heureCommande varchar(20),
    idUtilisateur integer ,
    mailRestaurant varchar(50),
    texteAide varchar(255) ,

    FOREIGN KEY (dateCommande,heureCommande,idUtilisateur,mailRestaurant) REFERENCES CommandeLivraison(dateCommande,heureCommande,idUtilisateur,mailRestaurant),
    FOREIGN KEY (texteAide) REFERENCES TexteAideLivreur(texteAide),
    PRIMARY KEY (dateCommande,heureCommande,idUtilisateur,mailRestaurant)
);

CREATE TABLE Categories(
	nomCategorie varchar(20),

	PRIMARY KEY(nomCategorie)
);

CREATE TABLE aPourCategorie(
	mailRestaurant varchar(50),
	nomCategorie varchar(50),
	FOREIGN KEY(mailRestaurant) REFERENCES Restaurant(mailRestaurant),
	FOREIGN KEY(nomCategorie) REFERENCES Categories(nomCategorie),
	PRIMARY KEY (mailRestaurant,nomCategorie)
);

CREATE TABLE aPourCategorieMere(
	nomCategorieFille varchar(50),
	nomCategorie varchar(50),
	FOREIGN KEY(nomCategorie) REFERENCES Categories(nomCategorie),
	PRIMARY KEY (nomCategorieFille,nomCategorie)
);

CREATE TABLE heureLiv(
    heureLiv varchar(20) ,
    PRIMARY KEY (heureLiv)
);
CREATE TABLE aPourHeureLiv(
	dateCommande date,
    heureCommande varchar(20),
	idUtilisateur integer ,
	mailRestaurant varchar(50) ,
	heureLiv varchar(20) ,

	FOREIGN KEY (dateCommande,heureCommande,idUtilisateur,mailRestaurant) REFERENCES CommandeLivraison(dateCommande,heureCommande,idUtilisateur,mailRestaurant) ,
	FOREIGN KEY (heureLiv) REFERENCES heureLiv(heureLiv),

	PRIMARY KEY (dateCommande,heureCommande,idUtilisateur,mailRestaurant)
);

CREATE TABLE Horaires (
	mailRestaurant varchar(50),
	jour varchar(20),
	numService integer ,
	heureOuverture varchar(10),
	heureFermeture varchar(10),

    CONSTRAINT jour_enum CHECK (Jour IN ('Lundi','Mardi','Mercredi','Jeudi','Vendredi','Samedi','Dimanche')),
	FOREIGN KEY(mailRestaurant) REFERENCES Restaurant(mailRestaurant),

	PRIMARY KEY (mailRestaurant,jour,numService)
);

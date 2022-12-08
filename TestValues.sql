-- Utilisateurs
INSERT INTO Utilisateur VALUES(1);
INSERT INTO Utilisateur VALUES(2);
INSERT INTO Utilisateur VALUES(3);
INSERT INTO Utilisateur VALUES(4);
INSERT INTO Utilisateur VALUES(5);

-- clients
INSERT INTO Client VALUES ('skywalker@imag.fr', 1, 'Luke Skywalker', 'luk123');
INSERT INTO Client VALUES ('vador@imag.fr', 2, 'Dark Vador', 'dar123');
INSERT INTO Client VALUES ('solo@imag.fr', 3, 'Han Solo', 'han123');
INSERT INTO Client VALUES ('leia@imag.fr', 4, 'Leia Solo', 'lei123');
INSERT INTO Client VALUES ('jabba@imag.fr', 5, 'Jabba The Hut', 'jab123');

-- Restaurant et plats
Insert into Restaurant values ('restaurant1@gmail.com','restaurant1','addresse1',50,'Restaurant asiatique');
Insert into Plat values (1,'restaurant1@gmail.com','plat1','plat asiatique 1',15.5);
Insert into Plat values (2,'restaurant1@gmail.com','plat2','plat asiatique 2',16.5);
Insert into Plat values (3,'restaurant1@gmail.com','plat3','plat asiatique 3',17.5);
Insert into Plat values (4,'restaurant1@gmail.com','plat4','plat asiatique 4',18.5);
Insert into Plat values (5,'restaurant1@gmail.com','plat5','plat asiatique 5',19.5);

Insert into Restaurant values ('restaurant2@gmail.com','restaurant2','addresse2',30,'Restaurant marocain');
Insert into Plat values (1,'restaurant2@gmail.com','plat1','plat marocain 1',10.5);
Insert into Plat values (2,'restaurant2@gmail.com','plat2','plat marocain 2',12.5);
Insert into Plat values (3,'restaurant2@gmail.com','plat3','plat marocain 3',16.5);
Insert into Plat values (4,'restaurant2@gmail.com','plat4','plat marocain 4',18.5);
Insert into Plat values (5,'restaurant2@gmail.com','plat5','plat marocain 5',23.5);

-- Allergenes
Insert into Allergenes values ('Gluten');
Insert into Allergenes values ('Oeufs');
Insert into Allergenes values ('sésame');
Insert into Allergenes values ('Soja');
Insert into Allergenes values ('moutarde');

-- aPourAllergene
Insert into aPourAllergene values (2,'restaurant1@gmail.com','Oeufs');
Insert into aPourAllergene values (3,'restaurant2@gmail.com','Soja');

-- Categories

Insert into Categories values ('cuisine régionale');
Insert into Categories values ('cuisine des alpes');
Insert into Categories values ('cuisine savoyarde');
Insert into Categories values ('cuisine dauphinoise');


-- aPourCategorieMere
Insert into aPourCategorieMere values ('cuisine des alpes','cuisine régionale');
Insert into aPourCategorieMere values ('cuisine savoyarde','cuisine des alpes');
Insert into aPourCategorieMere values ('cuisine dauphinoise','cuisine des alpes');

-- aPourCategorie
Insert into aPourCategorie values ('restaurant1@gmail.com','cuisine régionale');
Insert into aPourCategorie values ('restaurant2@gmail.com','cuisine des alpes');

-- Commande
Insert into Commande values ('2022-11-13', '07:15:31',1,'restaurant1@gmail.com',26.56,'attente de confirmation');
Insert into Commande values ('2022-11-13', '08:12:33',2,'restaurant2@gmail.com',36.26,'validée');
Insert into Commande values ('2022-11-13', '10:23:05',3,'restaurant1@gmail.com',15.23,'en livraison');
Insert into Commande values ('2022-11-13', '11:07:31',4,'restaurant2@gmail.com',7.29,'disponible');
Insert into Commande values ('2022-11-13', '12:15:51',5,'restaurant1@gmail.com',20.43,'validée');

-- aPourPlats

Insert into aPourPlats values ('2022-11-13', '07:15:31',1,'restaurant1@gmail.com',1,1);
Insert into aPourPlats values ('2022-11-13', '08:12:33',2,'restaurant2@gmail.com',2,3);
Insert into aPourPlats values ('2022-11-13', '10:23:05',3,'restaurant1@gmail.com',4,3);
Insert into aPourPlats values ('2022-11-13', '11:07:31',4,'restaurant2@gmail.com',3,2);
Insert into aPourPlats values ('2022-11-13', '12:15:51',5,'restaurant1@gmail.com',2,1);

-- CommandeSurPlace
Insert into CommandeSurPlace values ('2022-11-13', '07:15:31',1,'restaurant1@gmail.com',1,'07:00:00');
Insert into CommandeSurPlace values ('2022-11-13', '08:12:33',2,'restaurant2@gmail.com',2, '08:00:00');
Insert into CommandeSurPlace values ('2022-11-13', '11:07:31',4,'restaurant2@gmail.com',2 ,'11:00:00');
Insert into CommandeSurPlace values ('2022-11-13', '12:15:51',5,'restaurant1@gmail.com',1,'12:00:00');

-- heureLiv
Insert into heureLiv values('10:53:05');
Insert into heureLiv values('08:34:05');

-- CommandeLivraison
Insert into CommandeLivraison values ('2022-11-13','10:23:05',3,'restaurant1@gmail.com','564 avenue des fleurs');
Insert into CommandeLivraison values ('2022-11-13','08:12:33',2,'restaurant2@gmail.com', '163 Avenue Albert Ier de Belgique');

-- aPourHeureLiv
Insert into aPourHeureLiv values ('2022-11-13','10:23:05',3,'restaurant1@gmail.com','10:53:05');
Insert into aPourHeureLiv values  ('2022-11-13','08:12:33',2,'restaurant2@gmail.com','08:34:05');


-- TexteAideLivreur
Insert into TexteAideLivreur values('Cette commande doit être livrée rapidement !!!');

-- aPourTexteAide
Insert into aPourTexteAide values ('2022-11-13','10:23:05' , 3 , 'restaurant1@gmail.com','Cette commande doit être livrée rapidement !!!'  );

-- Evaluation
Insert into Evaluation values('2022-11-13', '10:55:51' , 'Le plus beau restaurant de ma vie , je le recommande',5);
Insert into Evaluation values('2022-11-13', '09:52:23' , 'Très délicieux , par contre  ca prend beacoup de temps je mets un 4 ',4 );
Insert into Evaluation values('2022-11-13', '12:55:53', 'Je prends un plat et ca me coute 20 euros !!! et nul en plus ',1);

-- aPourEvaluation
Insert into aPourEvaluation values('restaurant1@gmail.com',3,'2022-11-13', '10:23:05','2022-11-13', '10:55:51' , 'Le plus beau restaurant de ma vie , je le recommande',5);
Insert into aPourEvaluation values('restaurant2@gmail.com',2,'2022-11-13', '08:12:33','2022-11-13','09:52:23' , 'Très délicieux , par contre  ca prend beacoup de temps je mets un 4 ',4 );
Insert into aPourEvaluation values('restaurant1@gmail.com',5,'2022-11-13', '12:15:51','2022-11-13','12:55:53', 'Je prends un plat et ca me coute 20 euros !!! et nul en plus ',1);




-- Horaires
Insert into Horaires values('restaurant1@gmail.com','Lundi','08:00','12:00');
Insert into Horaires values('restaurant1@gmail.com','Lundi','14:00','18:00');
Insert into Horaires values('restaurant1@gmail.com','Mardi','08:00','12:00');
Insert into Horaires values('restaurant1@gmail.com','Mardi','14:00','18:00');
Insert into Horaires values('restaurant1@gmail.com','Mercredi','14:00','18:00');
Insert into Horaires values('restaurant1@gmail.com','Mercredi','08:00','12:00');
Insert into Horaires values('restaurant1@gmail.com','Jeudi','08:00','12:00');
Insert into Horaires values('restaurant1@gmail.com','Jeudi','14:00','18:00');
Insert into Horaires values('restaurant1@gmail.com','Vendredi','08:00','12:00');
Insert into Horaires values('restaurant1@gmail.com','Vendredi','14:00','18:00');
Insert into Horaires values('restaurant1@gmail.com','Samedi','08:00','12:00');
Insert into Horaires values('restaurant1@gmail.com','Dimanche','08:00','12:00');

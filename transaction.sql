-- REQUETES SIMPLES (LECTURE)
SELECT nombrePlaces FROM Restaurant
WHERE mailRestaurant LIKE ?

SELECT SUM(nombrePersonnes) FROM CommandeSurPlace
WHERE WHERE dateCommande LIKE ? AND heureArrive BETWEEN ? AND ?


SELECT heureOuverture, heureFermeture
FROM Horaires WHERE jour LIKE ?
AND mailRestaurant LIKE ?

SELECT mailRestaurant FROM Restaurant WHERE nomRestaurant LIKE ?

SELECT numeroPlat FROM Plat
WHERE mailRestaurant LIKE ? AND nomPlat LIKE ?

SELECT prix
FROM Plat
WHERE mailRestaurant LIKE ? AND numeroPlat LIKE ?

SELECT idUtilisateur
FROM Client
WHERE mailClient LIKE ?

SELECT motDePasse
FROM Client
WHERE mailClient LIKE ?

SELECT nomCategorieFille FROM aPourCategorieMere
WHERE nomCategorie LIKE ?


-- recupére certains données des restaurants apartenant à une catégorie et
-- par conséquent les restaurants appartenants aux sous-categories de la
-- dite catégorie. Requete utilisée par printRestaurants
WITH RECURSIVE succesor AS
 (SELECT nomCategorieFille AS cat FROM aPourCategorieMere
 WHERE nomCategorie LIKE ?
 UNION ALL
 SELECT nomCategorieFille FROM succesor, aPourCategorieMere
 WHERE succesor.cat = aPourCategorieMere.nomCategorie)
 SELECT DISTINCT r.mailRestaurant, r.nomRestaurant, r.textPresentation, avg(ape.note) AS noteMoy
 FROM Restaurant r, aPourCategorie apc, succesor s, aPourEvaluation ape
 WHERE r.mailRestaurant = ape.mailRestaurant AND
 r.mailRestaurant = apc.mailRestaurant AND
      (apc.nomCategorie = s.cat OR
      apc.nomCategorie LIKE ?)
 GROUP BY mailRestaurant ORDER BY noteMoy DESC, r.nomRestaurant ASC

SELECT *, avg(note) as noteMoy
FROM Restaurant NATURAL JOIN aPourEvaluation
WHERE mailRestaurant LIKE ?

SELECT heureOuverture, heureFermeture
FROM Horaires WHERE jour LIKE ? AND mailRestaurant LIKE ?
ORDER BY heureOuverture ASC

SELECT nomRestaurant
FROM Restaurant

SELECT avis, note FROM aPourEvaluation"
WHERE mailRestaurant LIKE ?"

-- Même requête que la précedente mais on ajoute le filtrage par jour et heure
WITH RECURSIVE succesor AS
 (SELECT nomCategorieFille AS cat FROM aPourCategorieMere
 WHERE nomCategorie LIKE ?
 UNION ALL
 SELECT nomCategorieFille FROM succesor, aPourCategorieMere
 WHERE succesor.cat = aPourCategorieMere.nomCategorie)
 SELECT DISTINCT r.mailRestaurant, r.nomRestaurant, r.textPresentation, AVG(ape.note) AS noteMoy
 FROM Restaurant r, aPourCategorie apc, succesor s, aPourEvaluation ape, Horaires h
 WHERE r.mailRestaurant = ape.mailRestaurant AND r.mailRestaurant = h.mailRestaurant
 AND r.mailRestaurant = apc.mailRestaurant AND
      (apc.nomCategorie = s.cat OR
      apc.nomCategorie LIKE ?)
 AND h.jour LIKE ? AND h.heureOuverture <= ? AND h.heureFermeture > ?
 GROUP BY mailRestaurant ORDER BY noteMoy DESC, r.nomRestaurant ASC


-- Récupere tous les catégories d'un restaurant
WITH RECURSIVE ancestor AS (
 SELECT nomCategorie AS cat FROM aPourCategorieMere
 WHERE nomCategorieFille IN (SELECT nomCategorie FROM aPourCategorie WHERE mailRestaurant LIKE ?)
 UNION ALL
 SELECT nomCategorie FROM ancestor, aPourCategorieMere
 WHERE ancestor.cat = aPourCategorieMere.nomCategorieFille)
SELECT cat FROM ancestor
UNION
SELECT nomCategorie AS cat FROM aPourCategorie WHERE mailRestaurant LIKE ?

-- Requetes pour passer les commandes

-- Première étape : création d'une commande de prix 0, au restaurant renseigné et à la date courante
INSERT INTO Commande Values(?, ?, ?, ?, ?, ?)
-- A chaque fois que l'utilisateur choisit un plat, il est inséré avec sa quantité dans la table qui lie les plats aux commandes.
-- Le lien est fait grâce à la date récupérée précédemment
INSERT INTO aPourPlats VALUES( ?, ?, ?, ?, ?, ?)
-- Une fois que tous les plats ont été choisis, le prix qui a été calculé au fur et à mesure est mis à jour
UPDATE Commande SET prixCommande = ?
WHERE dateCommande LIKE ? AND heureCommande LIKE ?
AND idUtilisateur LIKE ? AND mailRestaurant LIKE ?
-- Finalement, on récupère les informations concernant la livraison / réservation le cas échéant.
INSERT INTO CommandeSurPlace VALUES (?, ?, ?, ?, ?, ?)
INSERT INTO CommandeLivraison Values(?, ?, ?, ?, ?)

-- La cohérence de la base de données est assurée par un Savepoint avant la première insertion
-- Le commit ne se fait qu'à la fin
SAVEPOINT avantCommande
ROLLBACK TO avantCommande
COMMIT






-- Transaction pour la supression d'un client
BEGIN TRANSACTION;
-- Création d'un nouveau idUtilisateur (entre [0..10] à fin des test de la requete)
WITH RECURSIVE T AS (
    SELECT 1 AS n, FLOOR(RAND() * 10) AS v
    UNION ALL
    SELECT n + 1, FLOOR(RAND() * 10)
    FROM T
    WHERE EXISTS(SELECT * FROM Utilisateur WHERE idUtilisateur = v
-- Le paramêtre n nous sert a trouver le dernier id crée (celui qui n'existait pas avant)
SELECT v FROM T WHERE n >= ALL(SELECT n FROM t)

DELETE FROM Client
WHERE idUtilisateur LIKE ?

UPDATE Utilisateur
SET idUtilisateur = ?
WHERE idUtilisateur= ?
-- Noter que pour que cela fonctionne nous avons ajouté un ON UPDATE CASCADE
-- dans toutes les tables qui réference idUtilisateur.
COMMIT;

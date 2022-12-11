# Documentation
## Prérequis
Vous devez avoir un serveur mariaDB et modifier le makefile avec l'utilisateur 
et le mot de passe pour acceder à la base des données.

## Compilation et execution
```
make && make exec 
```

## Utilisation
### Connection
```
GrenobleEat (Not connected)> connect
Please enter you email : skywalker@imag.fr
Please enter you password : luk123
connection OK
```
### Parcours de categories
```
GrenobleEat> browseCategories
Current category: cuisine régionale
Available subcategories :
	cuisine des alpes
GrenobleEat>
```
On peut descendre dans le arbre des catégories juste en tapant le 
nom de la catégorie. 
### Affichage des restaurants
```
GrenobleEat> printRestaurants
Email: restaurant2@gmail.com
Name: restaurant2
Average mark: 4.0000

Email: restaurant1@gmail.com
Name: restaurant1
Average mark: 3.0000
```
La commande printRestaurants affiche les restaurants de la catégorie courante
par ordre de leur note et cas d'égalité par ordre alphabétique.

### Affichage des restaurants (Filtrage)
```
GrenobleEat> printRestaurants Lundi 11:00
Email: restaurant1@gmail.com
Name: restaurant1
Average mark: 3.0000
```
Posibilité de filtrer par jour de la semaine et heure. Dans l'example ci-dessus,
nous cherchons tous les restaurants appartenant à la catégorie courante ouverts
le Lundi à 11:00.

### Quitter le mode parcours
```
GrenobleEat> quitBrowseMode
Quiting browsing categories mode
```

### Supression du compte
```
GrenobleEat> deleteAccount
Commandes disponibles :
	 - disconnect
	 - browseCategories
	 - passerCommande
GrenobleEat (Not connected)>
```

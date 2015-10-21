# OpenSecuritySMS [![Build Status](https://travis-ci.org/OpenSecurityOrganization/OpenSecuritySMS.svg?branch=master)](https://travis-ci.org/OpenSecurityOrganization/OpenSecuritySMS)
This project's goal is to help people to send SMS safely.

Fork it and pull us !!

TO DO
-----

- [ ] Displaying of last message in the main activity even if it was send by us

French Version : 

Le but de cette application est d'envoyer des SMS cryptés avec le système asymétrique RSA. 
Chaque utilisateur dispose d'un couple clé privée/publique. 
On envoie la clé publique à un autre utilisateur par une requête spéciale de l'application (non implémentée)
et nous récupérons sa clé publique en échange. Ainsi, une conversation cryptée peut avoir lieu. Un message reçu crypté
ne peut être décrypté que par notre application, donc une application standard SMS recevra le message comme tel.

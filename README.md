# OpenSecuritySMS [![Build Status](https://travis-ci.org/OpenSecurityOrganization/OpenSecuritySMS.svg?branch=master)](https://travis-ci.org/OpenSecurityOrganization/OpenSecuritySMS)
This project's goal is to help people to send SMS safely.

Fork it and pull us !!

TO DO
-----

- [x] Displaying of last message in the main activity even if it was send by us
- [ ] Show full date when we swipe left an older message (yesterday or more)
- [x] Adding sending message function and listener to Send Button
- [x] Listener to recevied message 
- [ ] Update OpenSecurity activity when we return to this (with back native button for example) to display news potentials messages
- [ ] Generate couple of public and private keys
- [ ] Adding transmission of public key to an other contact
- [ ] Permit of our application to be default sms Application (choice of the user)


French Version : 

Le but de cette application est d'envoyer des SMS cryptés avec le système asymétrique RSA. 
Chaque utilisateur dispose d'un couple clé privée/publique. 
On envoie la clé publique à un autre utilisateur par une requête spéciale de l'application (non implémentée)
et nous récupérons sa clé publique en échange. Ainsi, une conversation cryptée peut avoir lieu. Un message reçu crypté
ne peut être décrypté que par notre application, donc une application standard SMS recevra le message comme tel.

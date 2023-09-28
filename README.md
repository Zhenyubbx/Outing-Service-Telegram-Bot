# Outing Service Telegram Bot
 
Have you ever find yourself frustrated over taking too much time finding places to eat? Fret not! 
This project aims to develop a Python Telegram Bot that allows users to discover places to go in Singapore, 
including Attractions, F&B outlets, as well as Bars and Clubs. 

*All data are retrieved from the Singapore Tourism Board APIs which are then further processed to provide more features. 

## Structure

This project utilises a microservice architecture developed using Java Spring with Maven, where each backend service is responsible 
for specific types of places. In addition, each service is deployed in Dockerised containers for easier CI/CD during 
future developments.

## Features

1. Get nearest Attractions from current location
2. Search Attractions by keyword
3. Get nearest F&B outlets from current location 
4. Search F&B outlets by keyword 
5. Get nearest Bars or Clubs from current location 
6. Search Bars or Clubs by keyword

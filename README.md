# Card-Simulator-Backend

# Project Overview
This project simulates opening cards as well as storing them in your own collection and 
calculating your overall collection price.

## Project Structure


└───src
├───main
    ├───java
    │   └───com
    │       └───cardsim
    │               └───Simulator
    │                   └───Web
    │                       ├───Config
    │                       ├───Controllers
    │                       ├───Firebase
    │                       └───Service
    └───resources
        ├───static

The files are broken down into 4 directories: 
1. Config - Files that set up initial configuration.
2. Controllers - Files that contain the Rest Controllers
3. Firebase -
4. Service - Runs the files necessary

## Setting up .env
For your .env file you need 3 variables.
1. FIREBASE_ADMIN_SDK_FILENAME - The name of the SDK File. This file should be placed in the root
directory. Can be found in your firebase project settings underneath "Service Accounts"
2. FIREBASE_API_KEY - Firebase Web API Key. Can be found in your firebase project settings
3. POKEMON_TCG_API_KEY - API Key to the Pokemon 

## How to Test 
1. Set up your .env file.
2. Run the "CardSimulatorWebApplication" program.

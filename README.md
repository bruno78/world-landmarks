# World Landmarks

## Project Summary

World Landmarks uses MLKit and Wikipedia API to recognize landmarks in pictures and bring information about them. The World Landmarks app also uses Firebase to authenticate users, to store pictures and data about landmarks. 

## Pre-requisites 

* Android SDK v28
* Android min SDK v17

## Tools Used

* [**Firebase**](https://firebase.google.com/) 16.0.4: For user authentication, storing data and image files. It will keep these features centralized, on the cloud, and easily available across other mobile devices that the user has.
* [**Glide**](https://bumptech.github.io/glide/) 4.8.0: For handling the loading and caching of images.
* [**MLKit**](https://developers.google.com/ml-kit/) 18.0.1: For processing landmark images.
* [**Timber**](https://github.com/JakeWharton/timber) 4.7.1: For log debug messages.When set for production, these debug logs are removed. 
* [**Retrofit**](https://square.github.io/retrofit/) 2.4.0: For network calls making it easy to parse json.
* [**Gradle**](https://gradle.org/) 3.1.4: Helps to accelerate developer productivity
* [**ButterKnife**](http://jakewharton.github.io/butterknife/) 9.0.0: For field and method binding views. This library helps to create a more readable and cleaner code. 
* [**Espresso**](https://developer.android.com/training/testing/espresso/) 3.0.2: For integration test

## Instructions

Download or clone this repo on your machine, open the project using Android Studio. Go to [Firebase](https://firebase.google.com/), create an account, setup a project for Android, setup MLKit, as well as Firebase authentication, database and storage. Follow the instructions on Firebase console, and download the appropriate json file. Once Gradle builds the project, click "run" and choose an emulator.

## User Experience

* Users can sign up or login using their email
* Users can logout
* Users can upload or take a picture to detect landmarks
* Users can save the landmark with information gathered from wikipedia
* Users can see the list of saved landmarks
* Users can see the detailed landmark with picture and information 
* Users can delete a saved landmark
* Users can add a widget on their phone screen to see the last 3 saved widgets
* Users can tap on widget title to open the app
* Users can tap on landmark from the widget to see the landmark detail. 

## TODO 

### Main Activity

- [x] Create Refine location
- [x] Fix if landmark list != null || landmark list > 0
- [x] Fix clear text when getting the next landmark 
- [x] Create RecyclerView for the results:
- [x] Add clear button handle both cases photo taken and uploaded.
- [ ] Add bottom menu 
- [x] Fix views 
- [x] Refactor (landmark detection code to its own class)

### ImageInfo Activity

- [x] Add WikipediaAPI
- [x] Add Loaders
- [x] Implement Firebase save and delete functions
- [x] Fix fab state button to keep on rotation

### Registration

- [x] Implement Registration sign in and sign up
- [x] Implement Logout

### Firebase

- [x] Authenticate user to read and write Database and Storage

### Add views

- [x] ImageInfo Activity 
- [x] Registration
- [x] Saved Landmark List Activity 
- [ ] Landmark Maps Activity
- [x] Widget Activity 


# PopularMovies-Stage2
Project 2 of the Udacity Android Nanodegree course. In this stage you’ll add additional functionality to the app you built in [Stage 1](https://github.com/rahul09m/PopularMovies-Stage1/).

You’ll add more information to your movie details view:
- You’ll allow users to view and play trailers ( either in the youtube app or a web browser).
- You’ll allow users to read reviews of a selected movie.
- You’ll also allow users to mark a movie as a favorite in the details view by tapping a button(star). This is for a local movies collection that you will maintain and does not require an API request*.
- You’ll modify the existing sorting criteria for the main view to include an additional pivot to show their favorites collection.
- Lastly, you’ll optimize your app experience for tablet.

###Getting Started
This sample uses the Gradle build system. To build this project, use the "gradlew build" command or use "Import Project" in Android Studio.

This app uses The Movie Database API to retrieve movies. To use this app, you will need to generate your own API Key and input in the app's 'build.gradle' file:


      buildTypes.each {

          it.buildConfigField 'String', 'API_KEY', "YourApiKeyHere"
          }
    

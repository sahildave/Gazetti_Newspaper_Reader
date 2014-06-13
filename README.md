Gazetti
==============


An upcoming blazingly fast Android app to make reading newspaper easy and "not boring".
--------------

minSDK - API 10 (2.3.3)

Current Release - 0.1_alpha APK (You can only view news publish before 4th June from this release)

This app is focussed on giving full control to the user for what he wants to read, down to the category level. You can select the Newspapers, and the categories from the newspaper which you want to read. If you dont like *Politics*, dont select it in your feed selector.

The latest news would be displayed in a list and the news would be displayed in a beautiful manner, free of external links and ads. You can enjoy reading your newspapers distraction free.


Technology used
--------------

- Parse.com as Backend - To collect the latest news from newspapers periodically and maintaining it. Cloud code is written in JavaScript.
- Jsoup for parsing articles into "Reading Mode" - The app finds the top heading, top image and the article body from the links. It skips the unnecessary ads if within the body.
- Gson - To parse the user's feed preference into string and save it to sharedPrefs.
- Picasso - To load the images in article asynchronously.

Features to be added before Beta Release
-------------------
- 5 newspapers with 5 categories. 
- Request Page - The user would be able to request any newspaper or category he'd like to see in the next edition.

Features to be added before Stable release
-------------------
- Dark theme/Night mode
- Business Newspaper and Category
- Non-English Language support.

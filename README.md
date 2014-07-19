Gazetti
==============


An upcoming blazing fast Android app to make reading newspaper easy and interesting.
--------------

minSDK - API 10 (2.3.3) 
Current Release - 0.1_alpha APK *(News from June not available due to Parse server downtime)*

This app is focused on giving full control to the users for what they want to read, down to the category level. From a Newspapers you'd like to read and select the category. If you don't like *Politics*, don't select it in your feed selector!

The latest news would be displayed in a list and the article would be displayed in a reading mode, free of external links and ads. You can now enjoy reading your newspapers distraction free.


Technology used
--------------

- **Parse.com as Backend** - To collect the latest news from newspapers periodically and maintaining it. Cloud code is written in JavaScript.
- **Jsoup** for parsing articles into "Reading Mode" - The app finds the top heading, top image and the article body from the links. It skips the unnecessary ads if within the body.
- **Gson** - To parse the user's feed preference into string and save it to sharedPrefs.
- **Picasso** - To load the images in article asynchronously.

Features to be added before Beta Release
-------------------
- 5 newspapers with 5 categories. 
- Request Page - The user would be able to request any newspaper or category he'd like to see in the next edition.

Features to be added before Stable release
-------------------
- Dark theme/Night mode
- Business Newspaper and Category
- Non-English Language support.

Screenshots
--------------------

![](https://raw.githubusercontent.com/sahildave/Gazetti_Newspaper_Reader/master/screens/phone_news.png)

![](https://raw.githubusercontent.com/sahildave/Gazetti_Newspaper_Reader/master/screens/tab_news.png)

![](https://raw.githubusercontent.com/sahildave/Gazetti_Newspaper_Reader/master/screens/phone_home.jpg)


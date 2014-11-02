Gazetti
==============


An blazing fast Android app to make reading newspaper easy and interesting.
--------------

minSDK - API 10 (2.3.3)

<a href="https://play.google.com/store/apps/details?id=in.sahildave.gazetti">
  <img alt="Get it on Google Play"
       src="http://developer.android.com/images/brand/en_generic_rgb_wo_60.png" />
</a>
This app is focused on giving full control to the users for what they want to read, down to the category level. From a Newspapers you'd like to read and select the category. If you don't like *Politics*, don't select it in your feed selector!

The latest news would be displayed in a list and the article would be displayed in a reading mode, free of external links and ads. You can now enjoy reading your newspapers distraction free.


Technology used
--------------

- **Parse.com as Backend** - To collect the latest news from newspapers periodically and maintaining it. Cloud code is written in JavaScript.
- **Jsoup** for parsing articles into "Reading Mode" - The app finds the top heading, top image and the article body from the links. It skips the unnecessary ads if within the body.
- **Gson** - To parse the user's feed preference into string and save it to sharedPrefs.
- **Picasso** - To load the images in article asynchronously.
- **Other Libraries Used** - ListViewAnimations, KenBurnsView (in Tablet layout), SmoothProgressBar, JazzyViewPager, TextJustify

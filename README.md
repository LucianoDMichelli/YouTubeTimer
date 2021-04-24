# YouTubeTimer

The original idea for this app was for the user to turn on some music and be able to fall asleep without their phone dying or using unnecessary wifi (for those with data caps) or mobile data. When the app closes itself, the screen will time out and turn off as normal.

![Video Screen](https://i.gyazo.com/c98a088151f425ccb80e2de91ac84517.png)

### Features

**Timer:** Can be cancelled at any time. Will continue if new video is selected from Related Videos section. Closes app when finished.

**Lights Out Button:** Turns status bar and entire screen (except for the video player) black. Creates less light if user is trying to sleep, and saves battery if user has a device with an OLED screen.

**Preferences:**
* Loop video or autoplay next video
* Turn off Bluetooth when timer is complete
* Fade out volume as timer ends
* Pause timer while video is paused

**YouTube Features:**
* Video Info (Title, Description, Views, Likes/Dislikes, # of Comments, Date Posted, Channel Name)
  * Liking and Disliking not supported
* Comments
  * Shows top 20 comments with (max) 5 replies each (full replies, submitting comments, and Liking/Disliking comments not supported)
* Related Videos
  * Shows top 5 related videos and allows user to select one to watch
* Built-in player features
  * Share video, change quality, fullscreen, open video in YouTube app, More Videos (selecting one will open it in official YouTube app)
  
## Notes

You will need an API key for the YouTube Data v3 API: https://developers.google.com/youtube/v3/getting-started

There is a limit on the number of API calls that can be made per day (resets at midnight Pacific Time) . You will be given 10,000 quota points per day by default. Each API method has a different cost ([full chart found here](https://developers.google.com/youtube/v3/determine_quota_cost)). Unsupported YouTube features are due to this quota.

The Search: list method costs 100 points per search. This means that, assuming 1 search per video watched, a user (or users sharing the same API key) can watch 100 videos per day. Getting the related videos costs another 100 points, and getting the comments costs 1 point. Neither of these are generated for a given video until the first time a user clicks on the sections to expand them. 

(Note: Related videos WILL be automatically generated if user has "Autoplay next video" enabled in preferences. This is necessary so that we can get the video ID of the next video. Having this enabled cuts the max videos per day to 50)

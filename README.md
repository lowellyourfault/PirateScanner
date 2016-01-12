# Pirate Scanner
Pirate scanner scans for pirate app on Android devices.

# How it works
Pirate scanner automatically grab the known-and-registered [pirate app list](https://console.cloud.google.com/m/cloudstorage/b/anti-piracy/o/android-pirate-app-list.json) from WWW, and check the list against the apps installed on Android devices to find the offenders. Downloaded list are stored on app internal storage so as it will work even when the device is not connected to any network.

Scanned results are returned to the caller as a callback.

# Implementations
- Initialize [PirateScanner](https://github.com/A-BloodType/PirateScanner/blob/master/pirate-scanner/src/main/java/com/makeez/piratescanner/PirateScanner.java).
```java
PirateScanner scanner = new PirateScanner();
```

- Start scanning.
```java
scanner.start(context, new PirateScanner.Callback() {
    @Override
    public void onCompleted(@Nullable List<Pirate> pirates) {
        // Do anything you want. (eg. prompt user to unsintall, quit app, etc)
    }
});
```

**Note** that scanner is executed in an `AsyncTask`, thus there's no need to spawn a worker thread for it.

- Set app specific [filters](https://github.com/A-BloodType/PirateScanner/blob/master/pirate-scanner/src/main/java/com/makeez/piratescanner/Filter.java).
```java
scanner.addFilter(Filter.BYPASS_LICENSE_CHECK);
```
By default, scanner scans through the whole [list](https://console.cloud.google.com/m/cloudstorage/b/anti-piracy/o/android-pirate-app-list.json) it grabs from WWW. Some list might not be related to the app which the developer may want to skip scanning on it. In this scenario, developer can set the filters that is related to the app only.
Below lists the filters currently available.
```java
/**
 * For pirates that block ad serving within app.
 */
public static final int BLOCK_ADS = 1;
/**
 * For pirates that bypass in-app billing (of sort).
 */
public static final int BYPASS_IAB = 2;
/**
 * For pirates that bypass license verification (of sort).
 */
public static final int BYPASS_LICENSE_CHECK = 3;
/**
 * For pirates that remove permission requested by the app.
 */
public static final int REMOVE_PERMISSION = 4;
```

**Note** that `addFilter()` must be called before `start()`.

- Add custom (static) [Pirate](https://github.com/A-BloodType/PirateScanner/blob/master/pirate-scanner/src/main/java/com/makeez/piratescanner/Pirate.java) to the scanner.
```java
scanner.addPirate(new Pirate(
         1,
         "Lucky Patcher",
         new String[] {
             "com.dimonvideo.luckypatcher",
             "com.chelpus.lackypatch"
         },
         new int[] {
             Filter.BYPASS_IAB,
             Filter.BYPASS_LICENSE_CHECK,
             Filter.REMOVE_PERMISSION
         }));
```
Occasionally, a pirate that 1 is targetting may not appear on the [official list](https://console.cloud.google.com/m/cloudstorage/b/anti-piracy/o/android-pirate-app-list.json). In this scenario, developer can manually add it to the list by calling `addPirate()` with the accurate definition of the target pirate. Pirate ID (1st argument) must be unique among the static list created by developers and must be >= 0. Negative ID are all reserved for official list usage.

**Note** that `addPirate()` must be called before `start()`.

# Integration
Gradle and Maven support are not available at the moment.

# Software requirements
1. Android SDK >= 9
2. Java JDK >= 7

# DIY
PirateScanner might not work well for certain (currently unknown) use case. In this case, developer can just grab a copy of the [pirate app list](https://console.cloud.google.com/m/cloudstorage/b/anti-piracy/o/android-pirate-app-list.json) and use it as intended.

# Feedback
There is no official way to report a new offending app currently. If you would like to send in your request, feel free to e-mail me at `tantzewee@gmail.com`. Alternatively, you may visit this [XDA thread](http://forum.xda-developers.com/coding/java-android/library-anti-piracy-online-pirate-app-t3291306) and leave your comment there.

# DISCLAIMER
The [pirate app list](https://console.cloud.google.com/m/cloudstorage/b/anti-piracy/o/android-pirate-app-list.json) can be used freely either together with the scanner code or indepently. I hold no responsibility for any possible defects found either in code or in list that break anything anytime.

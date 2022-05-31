Lovense SDK for Android
========================

The Lovense Android SDK is a set of application interfaces based on Android 4.3 and above. Use this SDK to develop applications for Android mobile devices. By calling the Lovense SDK interface, you can easily control Lovense toys and build applications with rich functions and strong interactivity.

TRY IT OUT
----------

In order to be able to use the API you must create a free account on the Lovense Official website in order to get your token
1. Create a new account at https://www.lovense.com/sextoys/developer/join/.
2. Go to https://www.lovense.com/user/developer/info and get your developer token.
3. Start coding! Visit https://developer.lovense.com/lovense-android-sdk-demo.zip to download the demo app.
4. Check out https://developer.lovense.com/#android-sdk for a list of commands and callbacks.
5. You can also download the demo app from this repo.


INSTALLATION
------------

- Create a new account at https://www.lovense.com/sextoys/developer/join/.

- Go to https://www.lovense.com/user/developer/info and get your developer token.

- Download and extract the Lovense SDK at https://developer.lovense.com/lovense-android-sdk.zip

- Copy lovense.aar to your `app/libs` directory

- Add `lovense.arr` to your app `build.gradle`. Configure `libs` in the program `build.gradle`.

- Configure permissions in `AndroidManifest.xml` with the following:
```
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```

- Register service in `AndroidManifest.xml`
<service android:name="com.xtremeprog.sdk.ble.BleService" android:enabled="true" />

- Connect Lovense toys and send commands 

```
// Pass your token into the Lovense framework
Lovense.getInstance(getApplication()).setDeveloperToken("Your token");

// Add a scan success notification
Lovense.getInstance(getApplication()).searchToys(new OnSearchToyListener() {
  @Override
  public void onSearchToy(LovenseToy lovenseToy) { } // Find toys

  @Override
  public void finishSearch() { }  // Scan finish

  @Override
  public void onError(LovenseError msg) { } // error

});

//Add a connection success notification
Lovense.getInstance(getApplication()).connectToy(toyId, new OnConnectListener() {
  @Override
  public void onConnect(String toyId,String status) { // Toy connection status
    switch (status) {
      case LovenseToy.STATE_CONNECTING:
        break;
      case LovenseToy.STATE_CONNECTED:
        break;
      case LovenseToy.STATE_FAILED:
        break;
      case LovenseToy.SERVICE_DISCOVERED:
        break;
    }
  }
  @Override
  public void onError(LovenseError lovenseError) {} // Connection error
 });

// Add sending command notification
Lovense.getInstance(getApplication()).addListener(toyId, new OnCallBack() {});

// Search for the toys over Bluetooth
Lovense.getInstance(getApplication()).addListener(toyId, new OnCallBack() {});

//Stop searching for toys
Lovense.getInstance(getApplication()).stopSearching();

// Save the toys
Lovense.getInstance(getApplication()).saveToys(lovenseToys, new OnErrorListener());

// Retrieve the saved toys
Lovense.getInstance(getApplication()).listToys(new OnErrorListener());

// Connect the toy
Lovense.getInstance(getApplication()).connectToy(toyId,new OnConnectListener());

// Disconnect the toy
Lovense.getInstance(getApplication()).disconnect(toyId);

// Send a command to the toy
Lovense.getInstance(getApplication()).sendCommand(toyId,LovenseToy.COMMAND _VIBRATE,vibrateLevel);
```



GIVE FEEDBACK
-------------
Please report bugs or issues to [Lovense Support](mailto:developer@mail.lovense.com).

You can also join our [Lovense Developer Community Discord](https://discord.gg/dW9f54BwqR), visit the [Lovense Developers Website](https://developer.lovense.com/#introduction), or open an issue in this repository.

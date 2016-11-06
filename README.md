Cloudinary, Android and Parse - Photo Album sample
==================================================

Demo for writing an Android application using Parse as a backend and Cloudinary as image backend for storing,
applying transformations and serving of images.

For more details about **Cloudinary**: http://cloudinary.com/

For more details about **Android development**: http://developer.android.com/develop/index.html

For more details about **Parse**: https://www.parse.com/


## Setup and run the sample project

* [Setup the Parse backend in 7 simple steps](https://github.com/cloudinary/cloudinary_parse#setup-the-sample-project)
* Setup a working [Android Studio](http://developer.android.com/sdk/index.html).
* Clone or [download](https://github.com/cloudinary/cloudinary-android-parse-sample/archive/master.zip) this repository
* Copy the `AndroidManifest.xml.sample` file into `AndroidManifest.xml` and modify the meta-data fields within it to reflect your Cloudinary cloud name and your Parse Application ID and Client Key.
* Import your project (from Android Studio welcome screen > "Open an existing Android Studio project" -> [Choose the cloudinary-android-parse-sample path])
* Run the application on an Android device or a simulator (Run -> Run 'App' -> [Choose device to run on])

## How does it work
The application is composed of 5 android activities (`SplashScreenActivity`, `LoginActivity`, `ListPhotoActivity`, `ShowPhotoActivity` and `UploadPhotoActivity`), the application singleton (`PhotoAlbumApplication`), a few helper classes and a few external libraries.

### Upload process
The Cloudinary API key and API secret are protected in Parse cloud code. They are not bundled in the application and are not accessible by any user - making the design more secure and protecting yours and your users' privacy and data integrity.

In order to upload in image from the application:

* A request to upload is sent by the application to Parse backend using a cloud function.
* The cloud function (by default, `sign_upload_request`) creates a signed request (using Cloudinary API parameters) and returns it to the application.
* The application uploads the image to Cloudinary using the retrieved signed request. When the upload is complete, Cloudinary returns a signed result of the operation containing the image public\_id, other identifiers required to access the image and metadata about the image (disk size, dimensions, etc...)
* The application saves the signed reference of the image to Parse backend.
* The Parse beforeSave filter verifies the authenticity of the saved reference and aborts the process if it's invalid.

### PhotoAlbumApplication.java
Contains initializations of Universal Image Loader (UIL), Parse and Cloudinary. Cloudinary is initialized using the `CLOUDINARY_URL` meta-data entry in AndroidManifest.xml by passing the `Context` to the Cloudinary constructor

### Activities
#### SplashScreenActivity.java
Displays a splash-screen and then launches the LoginActivity

#### LoginActivity.java
Displays a login screen. The Chuck Norris image demonstrates the usage of Cloudinary Facebook profile image retrieval (See [DownloadImageTask.java](#downloadimagetaskjava) for more information about the image download code)
After the user has provided a username and a password, invokes a task which tries to register (if username doesn't exist yet) or login on behalf of the user using Parse.

#### ListPhotoActivity.java
Displays an endless list of uploaded images. The list is fetched from Parse by querying the `PARSE_MODEL`. The images themselves are retrieved from Cloudinary.
When an image is clicked, the `ShowPhotoActivity` is launched passing it the image identifier in the intent extra parameters.  
This activity also has a menu with upload, refresh and logout actions.

Based partially on [ImageGridActivity.java](https://github.com/nostra13/Android-Universal-Image-Loader/blob/v1.9.2/sample/src/com/nostra13/example/universalimageloader/ImageGridActivity.java) from Universal Image Loader

#### ShowPhotoActivity.java
Displays a slidable pager with different transformations of the selected photo.

Based on [Android Blank Activity with Scrollable Tabs + Swipe template](http://developer.android.com/tools/projects/templates.html#blank-activity)

#### UploadPhotoActivity.java
Sends an image selection intent to allow the user to select an image to upload.
Once the user selects an image, the [upload process](#uploadprocess) begins.

### Helper classes
#### Constants.java
Contains several configuration parameters.

#### DownloadImageTask.java
A simple but useful AsyncTask which fetches an image by given URL into a given ImageView in the background

#### L.java
Logging helper class

### Libraries
#### cloudinary-android:1.1.2 (*gradle)
The Cloudinary library used to upload and retrieve Cloudinary images

#### Parse-1.8.1.jar
The Parse library used for user management (sign-up, login), and image objects management (add, list, fetch)

#### universalimageloader:universal-image-loader:1.9.3 (*gradle)
A very comprehensive and flexible library for ["asynchronous image loading, caching and displaying"](https://github.com/nostra13/Android-Universal-Image-Loader#-universal-image-loader-for-android)

## Read more

[Cloudinary documentation](http://cloudinary.com/documentation)  
[Cloudinary for Android](https://github.com/cloudinary/cloudinary_android)  
[Cloudinary for Parse](https://github.com/cloudinary/cloudinary_parse)

# Support

You can [open an issue through GitHub](https://github.com/cloudinary/cloudinary-android-parse-sample/issues).

Contact us at [info@cloudinary.com](mailto:info@cloudinary.com)

Or via Twitter: [@cloudinary](https://twitter.com/#!/cloudinary)

## Licenses

* This project is released under the MIT license.
* [Parse third-party licenses](https://github.com/cloudinary/cloudinary-android-parse-sample/blob/master/third_party_licenses.txt)
* [Android-Universal-Image-Loader](https://github.com/nostra13/Android-Universal-Image-Loader) is distributed under [the Apache License Version 2.0](https://github.com/nostra13/Android-Universal-Image-Loader/blob/master/LICENSE)

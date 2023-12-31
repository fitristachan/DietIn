# DietIn

This project utilizes Kotlin and Jetpack Compose along with integration of a TensorFlow Lite (TFLite) model for making an app that will help user to know if they food containing lectine or not. We use Firebase Authentication and GCP Architecture too for the API. With knowing that, we hope it will help user with their diet. User can scan images directly from camera or scan images from gallery. 

## Overview

We hope it will help users that need to avoid lectine. To help users, we provide some features like below:
The app have some features such as:
1. Authentication using Firebase: custom and OAuth using Google
2. Scan images from gamera or gallery with TensorFlow model
3. Show the detail result, static if it's opened through scan and online from our API if it's from history
4. Storing and retrieving scan histories, including feature to searching and filtering them by date and lectin/non-lectin markers
5. Delete history
6. Show some articles using static article
7. Change username, password, and photo of users


## Installation

1. **Clone the repository:**

   ```bash
   git clone https://github.com/fitristachan/DietIn.git
   
2. **Open the project in Android Studio:**
    Open Android Studio and select File -> Open and navigate to the cloned repository.

3. **Build and run the project:**
    Once the project is opened, build and run it on an Android device or emulator.

## Usage

To utilize the TensorFlow Lite model within this project, follow these steps:

1. **Download the TFLite model file:**
    Obtain the TensorFlow Lite model file from https://github.com/fitristachan/DietIn/blob/10db8a047453246651bb8a6ac44fa585a9210803/app/src/main/ml/indonesian_foods.tflite and place it in the designated directory within the project (e.g., app/src/main/ml/indonesian_foods.tflite).

2. **Download the Label**
    Obtain the label from https://github.com/fitristachan/DietIn/blob/10db8a047453246651bb8a6ac44fa585a9210803/app/src/main/assets/ingredients.json and don't forget to load and used the label by using this kotlin file: [ReadLabels.kt](src%2Fmain%2Fjava%2Fcom%2Fdietinapp%2Fmodel%2FReadLabels.kt)

3. **Integrate the model in the code:**

    Open the relevant Kotlin file [ProcessImage.kt](src%2Fmain%2Fjava%2Fcom%2Fdietinapp%2Fmodel%2FProcessImage.kt) where the TensorFlow Lite model is loaded and used. Ensure that the path to the model file matches the location where you placed the downloaded model in the assets folder.

4. **Use the model functionality in the app:**
   Implement the necessary logic within your app to utilize the loaded TensorFlow Lite model for the desired functionality.


## Acknowledgements

The mobile development of this project who created the front-end is:
| Student ID      | Name          | University               | Course                 |
|-----------------|---------------|--------------------------|------------------------|
| A128BSX2056     | Fitri Sagita  | Politeknik Negeri Jakarta| Mobile Development     |


## Contact
For any inquiries or feedback, feel free to contact me at 
1. Email: fitristarius@gmail.com
2. Linkedin: [Fitri Sagita](https://id.linkedin.com/in/fitri-sagita-4a530a210)

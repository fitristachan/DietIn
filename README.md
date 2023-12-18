# Project Title

This project utilizes Kotlin and Jetpack Compose along with integration of a TensorFlow Lite (TFLite) model for [purpose]. The model serves the purpose of [explain the functionality or purpose of the model briefly].

## Overview

Provide a brief overview or introduction to the project, including its goals, functionalities, and any relevant information about the app.

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

   3. **Use the model functionality in the app:**
   Implement the necessary logic within your app to utilize the loaded TensorFlow Lite model for the desired functionality.


## Acknowledgements

This project is made by **CH2-PS549 - DietIn Team** for Bangkit Product Capstone
The member who created this project are:
M128BSY1615 – Muhammad Riziq Ramadhan Al Arafah – Politeknik Negeri Jakarta - Machine Learning
M011BSY1869 – Fadlan Ahya Imani – Universitas Padjadjaran - Machine Learning
M128BSX1189 – Shierra Intan Anggari – Politeknik Negeri Jakarta- Machine Learning
C012BSY3605 – Muhammad Nurul Afif Maliki – Cloud Computing - Telkom University
C676BSX3192 – Shoraahatan Salsabila – Cloud Computing - Universitas Global Jakarta
A128BSX2056 – Fitri Sagita – Politeknik Negeri Jakarta - Mobile Development


## Contact
For any inquiries or feedback, feel free to contact me at [fitristarius@gmail.com].

# Currency Converter 
## Android App

This is an Android app developed in Android Studio as my final project in the Mobile Programming with Native Technologies course at OAMK. 
It is a simple app with three screens, API call and a some software logic. 

### Acomplished requirements:
- Error handling.
- ViewModel model class is properly implemented.
- API used: currencyapi.
- MVVM architecure.
- Good code structure.
- Theming: colors and fonts are not default.
- Loading message is shown on API call.
- App is developed using Kotlin and Jetpack Compose.
- Proper naming.
- All the strings are saved under resource file.

### Presentation of the app
YouTube link: https://youtu.be/7NItTf8Y5vw

### Code Structure

1. **MainActivity.kt**  
   - This is the entry point of the Android app. 

2. **model**  
   - Contains data classes and UI helpers.  
   - **CurrencyDropdownMenu.kt**: Displaying currency options in a dropdown.  
   - **CurrencyResponse.kt**: A data class modeling the JSON response from the currency API.

3. **navigation**  
   - Holds the classes and composables that define how to navigate through the app.  
   - **BottomNavigation.kt** and **BottomNavigationApp.kt**: Implement the bottom navigation bar and navigation routes.

4. **network**  
   - Contains the code for network requests.  
   - **CurrencyApiService.kt**: Defines the Retrofit HTTP client methods to fetch currency data from a remote API.

5. **screens**  
   - Houses the main UI composables for each screen in the app.  
   - **MainScreen.kt**, **InfoScreen.kt**, **CurrencyListScreen.kt**: Each file represents a distinct screen with its own UI and logic.

6. **theme**  
   - Manages the app-wide design elements, such as colors, typography, and shapes.  
   - **Color.kt**, **Theme.kt**, **Type.kt**: Provide consistent styling across the Compose components.

7. **viewmodel**  
   - Holds the ViewModel class.
   - **CurrencyViewModel.kt**: Contains logic for fetching currency data, storing state (like selected currencies or conversion results), and exposing it to the UI.


Overall, I group my code by feature or purpose:
- **UI code** in `screens` and `model` (for UI helpers).
- **App styling** in `theme`.
- **State management** in `viewmodel`.
- **Navigation** in `navigation`.
- **Network calls** in `network`.
- **App entry point** in `MainActivity.kt` and `AndroidManifest.xml`.

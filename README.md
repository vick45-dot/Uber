# Ride & Delivery App (Uber + Glovo for Kenya)

Kotlin + Jetpack Compose + MVVM Android app combining ride-hailing (taxi, car, boda boda)
and food/shop delivery, with Firebase backend, Google Maps/Places, role-based access, and
guest browsing (no forced login).

---

## 1. Project Structure

```
app/src/main/java/com/safiri/ridedelivery/
├── MainActivity.kt                # Compose host, wires shared ViewModels into NavGraph
├── RideDeliveryApp.kt             # Application: Places SDK + notification channel
├── data/
│   ├── model/Models.kt            # All domain models + enums (Firestore-ready)
│   └── repository/Repositories.kt # Auth, Catalog, Order, Ride, Driver, Restaurant repos
├── viewmodel/ViewModels.kt        # Auth, Catalog, Cart, Ride, Driver, Admin, Restaurant VMs
├── navigation/
│   ├── Routes.kt                  # Route constants
│   └── NavGraph.kt                # NavHost + argument wiring
├── ui/
│   ├── theme/Theme.kt             # Light/dark Material3 theme (Uber-black + Glovo-green)
│   ├── components/Components.kt   # Bottom bar, LoginRequiredDialog, shared widgets
│   ├── splash/                    # Splash -> Home (no forced login)
│   ├── home/                      # Home: quick actions, drivers nearby, restaurants
│   ├── food/                      # Food list, restaurant menu, cart
│   ├── ride/                      # Ride booking + fare estimate
│   ├── search/                    # Search food/shops/services
│   ├── auth/                      # Login/Register with role selection
│   ├── profile/                   # Guest vs logged-in profile + role dashboards
│   ├── payment/                   # M-Pesa / Card / Cash selection
│   ├── tracking/                  # Live order + ride tracking (Google Maps)
│   ├── driver/                    # Driver & Rider dashboard (shared)
│   ├── restaurant/                # Restaurant dashboard (menu + orders)
│   └── admin/                     # Admin dashboard (approvals)
└── util/
    ├── FareCalculator.kt          # KES fare model per vehicle type (Haversine-based)
    └── RideDeliveryMessagingService.kt  # FCM push handler
```

---

## 2. Database Schema (Firestore)

| Collection   | Doc ID        | Key fields |
|--------------|---------------|------------|
| `users`      | `uid`         | name, email, phone, **role** (CUSTOMER/DRIVER/RIDER/RESTAURANT/ADMIN), savedAddresses[] |
| `restaurants`| auto          | ownerUid, name, category, rating, etaMinutes, deliveryFee, location{lat,lng,address}, isOpen |
| `menuItems`  | auto          | restaurantId, name, price (KES), available |
| `shops`      | auto          | ownerUid, name, category, rating, etaMinutes, location |
| `drivers`    | `uid`         | vehicleType, vehicleModel, plateNumber, rating, **approval** (PENDING/APPROVED/REJECTED), isOnline, currentLocation, totalEarnings |
| `orders`     | auto          | customerUid, restaurantId, items[], subtotal, deliveryFee, total, **status**, riderUid, pickup, dropoff, paymentMethod |
| `rides`      | auto          | customerUid, driverUid, vehicleType, pickup, destination, fareEstimate, distanceKm, **status**, paymentMethod |

Enums are stored as their `.name` strings. Security rules in `firestore.rules` enforce:
**public read on catalog/drivers**, owner-only writes, and admin overrides.

---

## 3. Firebase Configuration

1. Create a project at https://console.firebase.google.com.
2. Add an Android app with package name **`com.safiri.ridedelivery`**.
3. Download `google-services.json` into `app/` (a `.template` is included for reference).
4. Enable **Authentication → Email/Password**.
5. Create **Firestore Database** (production mode), then paste `firestore.rules` under Rules.
6. Enable **Cloud Messaging** and **Storage** (for vehicle/menu photos).
7. The `com.google.gms.google-services` plugin (already in Gradle) reads the JSON at build time.

### Seed an admin
Register any account, then in Firestore manually set that user's `role` field to `ADMIN`.

---

## 4. Google Maps & Places Setup

1. In Google Cloud Console enable: **Maps SDK for Android**, **Places API**, **Directions API**.
2. Create an API key, restrict it to your app's SHA-1 + package name.
3. Put it in `local.properties`:  `MAPS_API_KEY=AIza...`
   - Gradle injects it into the manifest via `manifestPlaceholders`.
   - Also set `maps_api_key` in `res/values/strings.xml` so the Places SDK initialises.

---

## 5. API Integration Examples

### Google Places autocomplete (replace the demo text fields in RideScreen)
```kotlin
val fields = listOf(Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ADDRESS)
val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(context)
launcher.launch(intent)   // onResult -> Autocomplete.getPlaceFromIntent(data).latLng
```

### M-Pesa STK Push (server-side — recommended via Cloud Function)
Never embed the Daraja consumer secret in the app. Trigger from `PaymentScreen` by calling
your backend, which performs the STK push and writes the result back to the order doc:
```
POST https://sandbox.safaricom.co.ke/mpesa/stkpush/v1/processrequest
Authorization: Bearer {oauth_token}
{ "BusinessShortCode": 174379, "Amount": 250, "PartyA": "2547XXXXXXXX",
  "PhoneNumber": "2547XXXXXXXX", "CallBackURL": "https://.../callback", ... }
```

### FCM targeted push (server)
Store each user's FCM token (from `onNewToken`) on their `users` doc, then send via
the Admin SDK / HTTP v1 API to notify drivers of new ride requests.

---

## 6. Step-by-Step Implementation Guide

1. **Install** Android Studio (Koala+), SDK 34, JDK 17.
2. **Open** this folder (it has `settings.gradle.kts` at root).
3. Copy `local.properties.template` → `local.properties`, set `sdk.dir` + `MAPS_API_KEY`.
4. Add your real `google-services.json` to `app/`.
5. Paste your Maps key into `res/values/strings.xml` (`maps_api_key`).
6. **Sync Gradle** (downloads Firebase, Maps Compose, Coil, etc.).
7. **Run** on a device/emulator with Google Play services.
8. App opens on **Splash → Home as a guest** — browse restaurants, shops, drivers freely.
9. Tap **Add → Cart → checkout**, or **Book a ride**: the app prompts login only here.
10. Register as DRIVER/RIDER/RESTAURANT to see those dashboards; set a user to ADMIN to approve drivers.
11. Deploy `firestore.rules`, seed a few restaurants/menuItems for a populated demo.

---

## 7. How requirements map to code

- **Browse without login** → `CatalogRepository` (public reads) + guest Home/Food/Search.
- **Login only on action** → `LoginRequiredDialog` gates cart checkout & ride booking; `AuthViewModel.isLoggedIn`.
- **Role-based access** → `UserRole` on the user doc; `AuthScreen` routes by role; `firestore.rules` enforce.
- **Fare estimate before booking** → `FareCalculator` + `RideScreen`.
- **Real-time tracking** → Firestore snapshot Flows in `OrderRepository`/`RideRepository` + Maps Compose.
- **Driver accept/reject** → `RideRepository.pendingRequests()` + `DriverViewModel.respond()`.
- **Admin approvals** → `DriverRepository.pendingApprovals()` + `AdminDashboardScreen`.
- **Notifications** → `RideDeliveryMessagingService` + channel in `RideDeliveryApp`.
- **Light/dark** → `RideDeliveryTheme` follows system setting.


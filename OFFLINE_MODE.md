# Running in Offline Mode (No Firebase Billing)

The app is currently set to **offline mode** — it runs entirely on in-memory mock data,
so you do NOT need Firestore, billing, or a payment card. Everything is demoable:
browsing, search, cart, checkout, ride fare estimates, ride booking, and all four
dashboards (Driver, Rider, Restaurant, Admin).

## The switch

`app/src/main/java/com/safiri/ridedelivery/data/repository/AppConfig.kt`

```kotlin
object AppConfig {
    const val USE_FIREBASE = false   // false = offline mock; true = live Firebase
}
```

## What works offline

- **Browse** 4 Nakuru restaurants, 3 shops, 3 online drivers (seeded in MockData.kt).
- **Search** food and shops by name/category.
- **Cart + checkout** — login is still required at checkout (any email/password works
  in offline mode; it fabricates a demo account).
- **Book a ride** — pick vehicle type, see a live KES fare estimate, confirm. A mock
  driver is auto-assigned so the tracking screen has data.
- **Register** as Driver/Rider/Restaurant/Admin to see those dashboards. In offline mode,
  drivers/riders are auto-approved so you can use the dashboard immediately.
- **Admin dashboard** shows one pre-seeded pending driver (Samuel Kiprotich) you can
  approve or reject.

## What's limited offline

- **Maps** (order/ride tracking screens) show a grey grid unless you add a Google Maps
  API key — see below. The rest of those screens (status, driver, fare) still display.
- Data resets every time you restart the app (it's in-memory by design).

## Optional: make the maps show tiles

1. In Google Cloud Console enable **Maps SDK for Android**, create an API key.
2. Add to `local.properties`:  `MAPS_API_KEY=AIza...your_key...`
3. Also set it in `res/values/strings.xml` (`maps_api_key`) for Places.
   (You can skip this entirely for an offline demo — maps just stay grey.)

## Switching to live Firebase later

When you've created the free **(default) Native** Firestore database (no billing path),
flip `USE_FIREBASE = true`, publish the rules from `firestore.rules`, and the same
screens will read/write live cloud data with zero other code changes.

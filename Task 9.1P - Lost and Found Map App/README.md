# Lost & Found Map App

An Android application that lets users post **lost** and **found** items, attach a real-world location to each post, and view all items on a Google Map with a **radius-based search** that filters items by distance from the user's current location.

Built for **SIT708 – Task 9.1**. Written in **Java**, minimum **API 24**.

---

## Features

- **Create an advert** — post a Lost or Found item with name, phone, description, date, and a location.
- **Two ways to set a location:**
  - **Places Autocomplete** — search for any place using the Google Places SDK.
  - **Get Current Location** — pull the device's GPS position via the Fused Location Provider.
- **View all items** — a scrollable list of every posted item, backed by a local Room database.
- **Map view** — all items shown as markers (violet = Lost, blue = Found), with the user's location in green.
- **Radius-based search (subtask)** — a slider (1–50 km) filters the map to show only items within the chosen distance of the user, using the **Haversine** great-circle distance formula. A live count shows how many items fall within range.
- **Minimalist black-and-white UI** — custom drawables, no heavy Material chrome.

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java |
| UI | Android Views (XML layouts), RecyclerView |
| Maps | Google Maps SDK for Android |
| Place search | Google Places SDK for Android |
| Location | Google Play Services — Fused Location Provider |
| Local storage | Room (SQLite) |
| Min / Target SDK | 24 / 36 |

---

## Project Structure

```
com.josephb.lostandfoundmapapp
├── MainActivity.java            # Home screen — three navigation buttons
├── CreateAdvertActivity.java    # Form to create a new lost/found advert
├── PlacePickerActivity.java     # Places Autocomplete + current-location picker
├── AllItemsActivity.java        # RecyclerView list of all stored items
├── MapActivity.java             # Map, markers, radius slider, Haversine filter
├── db/
│   ├── Item.java                # Room @Entity
│   ├── ItemDao.java             # insert + getAll queries
│   └── AppDatabase.java         # Room database singleton
└── ui/
    └── ItemAdapter.java         # RecyclerView adapter for the item list
```

---

## Setup

### Prerequisites

- Android Studio (latest stable)
- Android SDK Platform **36** installed (SDK Manager → SDK Platforms)
- A Google Cloud project with **billing enabled** and these APIs turned on:
  - Maps SDK for Android
  - Places API (New)
  - Geocoding API

### 1. Clone

```bash
git clone https://github.com/JosephKS10/SIT708.git
```

### 2. Add your Google Maps API key

This project reads the API key from `gradle.properties` and injects it into the manifest at build time, so the key is **never committed to source control**.

Open (or create) `gradle.properties` in the project root and add:

```properties
MAPS_API_KEY=YOUR_API_KEY_HERE
```

### 3. Build and run

Open the project in Android Studio, let Gradle sync, then run on an emulator or device (API 24+).

> **Emulator note:** a fresh emulator defaults its GPS to Mountain View, California. To test current-location features, open **Extended Controls (•••) → Location**, set a point (e.g. Melbourne: `-37.8136, 144.9631`), and send it. The app uses `getCurrentLocation()` to request a fresh fix.

---

## How the Radius Search Works

When the map opens, the app loads all items from the Room database and requests the user's current location. The seek bar selects a radius from 1–50 km. On every change, `MapActivity` clears the map, draws a circle of the chosen radius around the user, and loops through every item, computing the distance with the Haversine formula:

```java
private static double haversineKm(double lat1, double lon1, double lat2, double lon2) {
    double R = 6371.0; // Earth's radius in km
    double dLat = Math.toRadians(lat2 - lat1);
    double dLon = Math.toRadians(lon2 - lon1);
    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
             + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
             * Math.sin(dLon / 2) * Math.sin(dLon / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return R * c;
}
```

Only items whose distance is less than or equal to the selected radius get a marker. The count label updates to show, for example, "3 of 8 items within 10 km".

---

## Data Model

Each item is stored as a Room entity:

| Field | Type | Notes |
|-------|------|-------|
| id | long | Auto-generated primary key |
| type | String | "LOST" or "FOUND" |
| name | String | Item name |
| phone | String | Contact number |
| description | String | Free text |
| date | String | yyyy-MM-dd |
| locationName | String | Display name of the chosen place |
| latitude | double | For map placement and radius math |
| longitude | double | For map placement and radius math |


# Lost and Found App

An Android application that helps connect lost items with their owners. Users can post **lost** or **found** item adverts, browse all listings, search and filter by category, view full details, and remove an advert once the item has been returned.

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [How the Subtasks Are Met](#how-the-subtasks-are-met)
- [Tech Stack](#tech-stack)
- [Screens](#screens)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Database Schema](#database-schema)
- [Key Implementation Details](#key-implementation-details)
- [Build and Run](#build-and-run)
- [Usage Walkthrough](#usage-walkthrough)
- [Design Decisions](#design-decisions)
- [Known Limitations](#known-limitations)
- [Future Directions](#future-directions)
- [Use of AI](#use-of-ai)
- [Author](#author)

---

## Overview

The Lost & Found App is a fully offline, single-device Android application. All data is persisted locally using **SQLite**, and all uploaded images are copied into the app's **private internal storage**. The app requires no internet connection, no backend, no API keys, and no runtime permissions to function.

The core workflow is: a user creates an advert describing a lost or found item (with a photo), other users browse and search the list of adverts, and once an item is reunited with its owner the advert is removed.

---

## Features

- **Create adverts** for either lost or found items, capturing post type, name, phone, description, incident date, location, category, and an image.
- **Mandatory image upload** — an advert cannot be saved without an attached photo.
- **SQLite persistence** — adverts and their metadata survive app restarts.
- **Live search** by item name or description (case-insensitive substring match).
- **Category filtering** across six categories: Electronics, Pets, Wallets, Documents, Clothing, Other.
- **Relative timestamps** ("5 minutes ago", "2 days ago", "1 week ago") so users can gauge how recent a listing is.
- **Detail view** showing all advert information at full size.
- **Remove with confirmation** — deletes both the database row and the stored image file, guarded by a confirmation dialog.
- **Minimalist monochrome UI** — a strict black-and-white palette applied app-wide, with forced light mode for visual consistency.

---

## Tech Stack

| Concern | Choice |
|---------|--------|
| Language | Java |
| Minimum SDK | API 24 (Android 7.0 Nougat) |
| Target SDK | API 36 |
| Persistence | SQLite via `SQLiteOpenHelper` |
| List rendering | `RecyclerView` + `CardView`-style rows |
| Image selection | `ActivityResultContracts.PickVisualMedia` (Android Photo Picker) |
| Image storage | App internal storage (`getFilesDir()`), path stored in DB |
| Theme | Material Components Light, forced light mode |
| Build system | Gradle (Groovy DSL) |

No third-party image-loading libraries (Glide, Picasso) are used — thumbnails are decoded and downsampled manually to keep the dependency footprint minimal.

---

## Screens

| Screen | Class | Purpose |
|--------|-------|---------|
| Main / Landing | `MainActivity` | Two buttons: create a new advert, or show all items. |
| Create Advert | `CreateAdvertActivity` | Form with radio buttons, text fields, date picker, category spinner, and image picker. |
| List | `ListActivity` | Scrollable list of all adverts with live search and category filter. |
| Detail | `DetailActivity` | Full advert view with image and a remove action. |

---

## Architecture

The app follows a simple, layered structure appropriate to its scope:

```
UI layer (Activities + layouts)
        |
        v
Adapter layer (ItemAdapter for RecyclerView)
        |
        v
Data layer (DatabaseHelper -> SQLite, ImageStorageUtil -> filesystem)
        |
        v
Model (Item)
```

- **`Item`** is a plain data model (POJO) carrying all advert fields.
- **`DatabaseHelper`** owns all SQL — table creation, insert, delete, single fetch, and filtered fetch. No SQL lives anywhere else.
- **`ImageStorageUtil`** is a stateless utility that copies a picked image URI into internal storage and returns its absolute path.
- **`TimeFormatter`** is a stateless utility that converts an epoch-millis timestamp into a relative string.
- **`ItemAdapter`** binds `Item` objects to list rows and handles thumbnail decoding.
- The **Activities** orchestrate the above and contain no business logic beyond UI wiring and input validation.

This keeps each class single-purpose and makes the data flow easy to trace for the demonstration video.

---

## Project Structure

```
Task 7.1P - Lost and Found App/
├── app/
│   ├── src/main/
│   │   ├── java/com/josephb/lostandfoundapp/
│   │   │   ├── MainActivity.java          # Landing screen, navigation
│   │   │   ├── CreateAdvertActivity.java  # Create-advert form + save
│   │   │   ├── ListActivity.java          # List + search + filter
│   │   │   ├── DetailActivity.java        # Detail view + remove
│   │   │   ├── ItemAdapter.java           # RecyclerView adapter
│   │   │   ├── DatabaseHelper.java        # SQLite CRUD
│   │   │   ├── ImageStorageUtil.java      # Image copy to internal storage
│   │   │   ├── TimeFormatter.java         # Relative timestamp logic
│   │   │   └── Item.java                  # Data model
│   │   ├── res/
│   │   │   ├── layout/                    # All activity + item layouts
│   │   │   ├── values/                    # strings, colors, themes
│   │   │   ├── values-night/              # forced-light theme override
│   │   │   └── drawable/                  # divider + button shapes
│   │   └── AndroidManifest.xml
│   └── build.gradle                       # Module-level build config
├── build.gradle                           # Project-level build config
├── settings.gradle
└── README.md
```

---

## Database Schema

A single table, `items`, in the database file `lostandfound.db` (version 1):

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `id` | INTEGER | PRIMARY KEY AUTOINCREMENT | Unique advert identifier |
| `post_type` | TEXT | NOT NULL | `"Lost"` or `"Found"` |
| `name` | TEXT | NOT NULL | Item / contact name |
| `phone` | TEXT | | Optional contact number |
| `description` | TEXT | | Free-text description |
| `incident_date` | TEXT | | User-entered date, `YYYY-MM-DD` |
| `location` | TEXT | | Where the item was lost/found |
| `category` | TEXT | NOT NULL | One of the six fixed categories |
| `image_path` | TEXT | | Absolute path to the stored image |
| `created_at` | INTEGER | NOT NULL | `System.currentTimeMillis()` at insert |

The schema deliberately separates two date concepts:

- **`incident_date`** — when the item was lost or found (user input).
- **`created_at`** — when the advert was posted (automatic), used for the "how recent is this listing" timestamp and for ordering the list newest-first.

---

## Key Implementation Details

### Image storage strategy

When a user picks an image, the app does **not** simply store the gallery URI. Instead, `ImageStorageUtil.copyUriToInternal()` reads the image bytes through a `ContentResolver` stream and writes them to a uniquely-named file (`img_<UUID>.jpg`) inside `getFilesDir()/item_images/`. Only the resulting absolute path is stored in SQLite.

**Why:** content URIs are not durable. If the user later deletes the original photo from their gallery, a stored URI would break. Copying the bytes into private app storage guarantees the advert keeps its image for the life of the advert. When an advert is removed, its image file is deleted too, so storage does not leak.

### No-permission image picking

The app uses `ActivityResultContracts.PickVisualMedia`, the modern Android Photo Picker. This launches a system-provided picker that returns access to exactly one user-selected image, with **no runtime permission prompt** on API 24+. This avoids the historical complexity of `READ_EXTERNAL_STORAGE` (API <= 32) versus `READ_MEDIA_IMAGES` (API 33+).

### Bitmap downsampling

`ItemAdapter.decodeSampledBitmap()` decodes image bounds first (`inJustDecodeBounds = true`), computes an `inSampleSize` power-of-two, then decodes the scaled-down bitmap. A 4000x3000 camera photo loaded full-size into a 64dp thumbnail would risk an `OutOfMemoryError`; downsampling prevents this.

### Dynamic filtering in one query

`DatabaseHelper.getItemsFiltered(category, searchQuery)` builds its `WHERE` clause conditionally:

- If a category other than `"All"` is supplied, it adds `category = ?`.
- If a non-empty search query is supplied, it adds `(LOWER(name) LIKE ? OR LOWER(description) LIKE ?)`.
- Both can apply simultaneously, joined with `AND`.

Results are always ordered by `created_at DESC` (newest first). This single method backs both Subtask 1 requirements (search and category filter).

### Relative timestamps

`TimeFormatter.relative()` takes an epoch-millis value, subtracts it from "now", and returns a human-readable string with correct singular/plural handling, escalating through seconds, minutes, hours, days, weeks, months, and years. It is pure Java with no Android dependencies, making its behaviour fully predictable across API levels.

### Data refresh on return

`ListActivity` re-queries the database in `onResume()`. This means that after creating a new advert or removing one from the detail screen, the list reflects the change automatically when the user navigates back — without manual refresh logic.

---

## Build and Run

**Prerequisites:** Android Studio (Iguana or newer recommended), Android SDK with API 24+ installed.

1. Clone the repository:
   ```bash
   git clone https://github.com/JosephKS10/SIT708.git
   ```
2. In Android Studio, open the `Task 7.1P - Lost and Found App` project folder.
3. Allow Gradle to sync and download dependencies.
4. Select an emulator or connect a physical device running API 24 or higher.
5. Click **Run**.

No API keys, environment variables, signing configuration, or backend setup are required.

---

## Usage Walkthrough

1. **Launch** the app to reach the landing screen.
2. Tap **CREATE A NEW ADVERT**.
3. Choose **Lost** or **Found**, fill in the details, tap **PICK DATE** to set the incident date, choose a **category**, and tap **PICK IMAGE** to attach a photo.
4. Tap **SAVE**. (Saving is blocked until required fields and an image are provided.)
5. From the landing screen, tap **SHOW ALL LOST & FOUND ITEMS**.
6. **Search** by typing into the search bar, or **filter** using the category dropdown.
7. **Tap an advert** to open its detail view.
8. Tap **REMOVE** and confirm to delete the advert.

---


## Future Directions

Planned enhancements, described in full in the accompanying Future Direction Report, include:

- Migrating to a shared cloud backend (Firebase Firestore) so adverts are discoverable across devices.
- Adding location intelligence via the Geocoding API and map-based proximity search.
- Push notifications (Firebase Cloud Messaging) to alert users when a matching item is posted.
- On-device image matching using TensorFlow Lite to suggest probable lost/found matches.
- Trust-and-safety features: verified contact, in-app messaging, and listing reports.


## Author

**Joseph K. Saji**
SIT708 Mobile Application Development — Deakin University

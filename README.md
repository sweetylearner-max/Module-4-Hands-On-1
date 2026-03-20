# Offline-First Notes App with Synchronization

An Android notes app that works fully offline using Room database, and syncs with a remote server via WorkManager when the network becomes available.

---

## Output Screenshots

<p align="center">
  <img src="https://github.com/user-attachments/assets/3a36b0e8-4d75-4df8-bb81-a5ba63493b60" width="22%"/>
  <img src="https://github.com/user-attachments/assets/8d0a9aeb-0508-4212-ab6d-6ae9cdfcb1fe" width="22%"/>
  <img src="https://github.com/user-attachments/assets/73c86f7e-cbba-4995-bd29-d9ce61e5a967" width="22%"/>
  <img src="https://github.com/user-attachments/assets/d028f6d4-61c8-4fca-8ea6-e9441caae281" width="22%"/>
</p>

---

## Features

- **Offline-first** — notes stored in Room DB, available without internet
- **Sync status bar** — 🟢 Online / 🟠 Offline indicator with "Sync Now" button
- **Per-note sync icon** — ✓ green (synced) / ⟳ orange (pending upload)
- **SwipeRefreshLayout** — pull to trigger sync
- **WorkManager** `SyncWorker` — auto-sync when network reconnects
- **Add / Edit / Delete** notes via `MaterialAlertDialog`
- **ViewModel + LiveData** — reactive UI updates
- **Retrofit + Room** — remote API and local DB

---

## Architecture

```
UI Layer
└── MainActivity → NotesAdapter (RecyclerView)

ViewModel Layer
└── NoteViewModel → LiveData<List<Note>>, syncStatus, isOnline

Data Layer
├── NoteRepository
│   ├── NoteDao (Room)         ← local CRUD
│   └── ApiService (Retrofit)  ← remote sync
└── SyncWorker (WorkManager)   ← background sync
```

---

## Project Structure

```
src/main/java/com/project/mod4pro1/
├── MainActivity.kt
├── data/dao/
│   ├── NoteDao.kt
│   ├── model/Note.kt
│   └── repository/
│       ├── AppDatabase.kt
│       ├── NoteRepository.kt
│       └── Converters.kt
├── network/
│   ├── ApiService.kt
│   └── ApiClient.kt
├── ui/NotesAdapter.kt
├── util/NetworkUtils.kt
├── viewmodel/NoteViewModel.kt
└── work/SyncWorker.kt
```

---

## Requirements

- Android Studio Hedgehog or later
- Min SDK: 24
- Language: Kotlin
- Libraries: Room, Retrofit, WorkManager, LiveData, ViewModel
- Theme: Material3 Purple (`#6200EE`)

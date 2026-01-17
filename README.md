# QuickLens 🔍

**QuickLens** brings a "Circle to Search" experience to any Android device. Instantly search anything on your screen using multiple search engines — just like the flagship feature on premium devices, but available everywhere.

<p align="center">
  <img src="https://img.shields.io/badge/Version-1.0-blue?style=for-the-badge" alt="Version 1.0">
  <img src="https://img.shields.io/badge/Min%20SDK-29-green?style=for-the-badge" alt="Min SDK 29">
  <img src="https://img.shields.io/badge/Target%20SDK-36-orange?style=for-the-badge" alt="Target SDK 36">
  <img src="https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge" alt="MIT License">
</p>

---

## ✨ Features

### 🎯 Trigger Methods
- **Default Assistant Gesture** — Long-press the Home button or swipe diagonally from corners to instantly capture and search
- **Floating Bubble** — Optional draggable trigger bubble for one-tap access (opt-in)

### 🔎 Search Capabilities
- **Multi-Engine Support** — Seamlessly switch between Google Lens, Bing, and Yandex
- **Crop & Search** — Select any region of your screenshot to search
- **Visual Search** — Upload cropped images directly to reverse image search

### 🎨 Customization
- **Theme Options** — System, Light, and Dark modes
- **Desktop Mode** — Request desktop versions of search results
- **Open Links Externally** — Choose to open results in your default browser
- **Auto-clear History** — Configure automatic history cleanup (Never, 1/7/15/30 days)

### 📱 Smart History
- Visual snapshots of all your searches
- Searchable history with timestamps
- One-tap to re-open previous searches

### 🔒 Privacy First
- **No data collection** — Everything stays on your device
- **No background processing** — App only activates on explicit user action
- **Direct to provider** — Search queries go directly to the search engine

---

## 🚀 Getting Started

### Prerequisites
- Android 10+ (API 29)
- ~2.5 MB storage space

### Installation

1. **Download** the latest APK from [Releases](../../releases)
2. **Install** the APK on your device
3. **Enable Accessibility Service**
   - Open QuickLens → Tap "Enable Accessibility"
   - Find QuickLens in the list and enable it
4. **Set as Default Assistant**
   - Open QuickLens → Tap "Enable Trigger (Default Assistant)"
   - Select QuickLens as your digital assistant app
5. **Start Searching!**
   - Long-press Home button or swipe diagonally from screen corners

---

## 🎮 How to Use

| Action | Trigger |
|--------|---------|
| **Capture Screen** | Long-press Home / Swipe diagonal |
| **Crop Selection** | Drag on screenshot to select area |
| **Switch Engine** | Tap engine buttons (Lens/Bing/Yandex) |
| **View History** | Tap History icon in app |
| **Close Overlay** | Tap outside the result sheet or swipe down |

---

## 🛠️ Built With

| Technology | Purpose |
|------------|---------|
| **Kotlin** | Primary language |
| **Jetpack Compose** | Modern declarative UI |
| **Material 3** | Design system |
| **Room Database** | Local history storage |
| **Coroutines** | Async operations |
| **VoiceInteractionService** | Assistant trigger integration |
| **AccessibilityService** | Screen capture capability |


---

## 🔧 Building from Source

```bash
# Clone the repository
git clone https://github.com/quantavil/QuickLens.git
cd QuickLens

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease
```

The release APK will be at: `app/build/outputs/apk/release/app-release.apk`

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

- Inspired by Google's Circle to Search feature
- Built with ❤️ using Jetpack Compose

---

<p align="center">
  <b>QuickLens</b> — Search anything, anywhere, instantly.
</p>

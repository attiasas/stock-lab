# StockLab

Android investment portfolio **simulation** app. Manage multiple profiles, browse real-time stock data, and place simulated buy/sell orders. All transactions and portfolio values are simulated; real-world prices are used for realism.

## Features

- **Profiles**: Create, delete, and switch between multiple simulation profiles (like game saves).
- **Starting conditions**: When creating a profile, set starting capital, currency, and difficulty (Easy/Normal/Hard).
- **Portfolio**: View balance, total value, and holdings.
- **Stocks**: Search and browse stocks using real-time data (Alpha Vantage API).
- **Trade**: Buy and sell at real-time prices; operations are simulated (no real money).
- **History**: View transaction history by time.
- **Analysis**: Simulation score and performance (return %, transaction count) to track progress.

## Requirements

- Android SDK 26+
- Java 17
- Gradle 8.14, Android Gradle Plugin 8.5.2

## Setup

1. Clone or open the project.
2. **Gradle Wrapper**: The project expects Gradle 8.14. If `./gradlew` fails (e.g. missing `gradle-wrapper.jar`), run:
   ```bash
   gradle wrapper --gradle-version 8.14
   ```
   This generates `gradle/wrapper/gradle-wrapper.jar` and the full wrapper scripts. Or open the project in **Android Studio** and let it sync.
3. **Stock API key** (optional but recommended): The app uses [Alpha Vantage](https://www.alphavantage.co/support/#api-key) for real-time quotes and symbol search. The demo key has strict rate limits.
   - Get a free API key from Alpha Vantage.
   - Add to `gradle.properties`: `STOCK_API_KEY=your_key_here` (the app reads it via BuildConfig). Or call `StockApiClient.setApiKey("YOUR_KEY")` in `StockLabApp.onCreate()`.

## Build & Run

```bash
./gradlew assembleDebug
```

Install on a device or emulator, or run from Android Studio.

## Project structure

- `app/src/main/java/com/stocklab/`
  - `data/` – local (Room) and remote (Retrofit) data, repositories
  - `model/` – domain models
  - `ui/` – activities, fragments, view models (profiles, portfolio, stocks, history, analysis)
  - `util/` – e.g. `AnalysisHelper` for score and return %

## License

Use and modify as needed for your project.

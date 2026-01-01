<p align="center">
  <img src="src/main/resources/logo.png" alt="Hywatch Logo" />
</p>

# Hywatch Mock Client

A simple Java Swing application to simulate a Minecraft client sending telemetry events to the Hywatch backend. This tool is used for development and testing of the Hywatch ingestion pipeline.

## Features
- **Simulate Events**: Send `stat_update`, `kill`, `death`, `building`, and `block_break` events.
- **Server Selection**: Toggle between Localhost (`localhost:4000`) and Production (`hywatchbackend.hydmg.com`).
- **Auto-Login**: Authenticate as a bot user against the Hywatch Auth service (PocketBase).
- **Randomized Data**: Events are populated with randomized stats, positions, and metadata matching the Hywatch schema.

## Prerequisites
- Java 21 or higher.
- Bash (for running the start script).
- Internet connection (to download JSON dependencies on first run).

## Quick Start

1.  **Run the client**:
    ```bash
    ./run.sh
    ```
    This script will:
    - Create a `lib/` directory and download `gson-2.10.1.jar` if missing.
    - Compile the sources into `bin/`.
    - Launch the GUI.

2.  **Select Server**:
    - Use the dropdown at the bottom to select **Localhost** or **Production**.

3.  **Send Events**:
    - Click the buttons (**Stats**, **Kill**, **Death**, **Build**, **Mine**) to generate and send events.
    - View logs in the center text area.

## Configuration

### Auto-Login
The client attempts to auto-login using credentials provided via environment variables. If these are not set, you can still send events if the backend allows unauthenticated ingestion (or if you manually implement token handling).

Set these variables in your shell or edit `run.sh`:

```bash
export HYWATCH_BOT_IDENTITY="your-bot-identity"
export HYWATCH_BOT_PASSWORD="your-bot-password"
```

*Note: `run.sh` has default credentials configured for the `hywatch-backend-prod` identity.*

## Project Structure
- `src/`: Java source code.
- `sample_events/`: Reference JSON files for the supported event types.
- `run.sh`: Helper script to compile and run the application.
- `lib/`: Directory for dependencies (Gson).

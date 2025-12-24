# StudyAI

StudyAI is a Java 17 desktop app that turns your study notes into practice Q&A using Cohere. It ships with a Swing GUI plus a simple console menu for quick runs.

## What it does
- Load or paste notes (plain text or `.txt` files).
- Normalizes and batches notes, then calls Cohere `generate` with the `command` model.
- Parses numbered `Q - A` pairs and shows them in a clean viewer with answers hidden behind checkboxes.
- Builds a self-contained (shaded) jar for easy distribution.

## Requirements
- Java 17+
- Maven 3.8+
- Cohere API key with access to the `command` model
- Internet access for API calls

## Configure your Cohere API key
The client currently reads the key from `CohereClient.API_KEY`.
1) Open `StudyAI/src/main/java/com/jacob/studyai/CohereClient.java`.
2) Replace the placeholder `API_KEY` value with your key.
3) Avoid committing real keys to version control.

## Build
From the `StudyAI` directory:
```bash
mvn clean package
```
This produces `target/StudyAI-1.0-SNAPSHOT.jar` (shaded with dependencies).

## Run the GUI (NotesUI)
- After building: `java -jar target/StudyAI-1.0-SNAPSHOT.jar`
- Windows shortcut: double-click `run.bat`
- Dev mode: `mvn exec:java` (uses `NotesUI` as the main class)

### Using the GUI
1) Launch the app.
2) Click **Load Notes** to choose a `.txt`/`.md` file, or paste text directly.
3) Click **Generate Q&A**. The app cleans your text, batches sentences, and calls Cohere.
4) A new window shows numbered questions; tick **Show Answer** beside any item to reveal it.

## Optional console menu
Run the text-based menu if you prefer the terminal:
```bash
mvn exec:java -Dexec.mainClass=com.jacob.studyai.ConsoleMenu
```
Follow the prompts to load notes and generate Q&A.

## Prompting and parsing notes
- Notes are split on `. ` into batches of five definitions per API call.
- Ensure sentences end with periods to keep questions aligned with your definitions.
- Responses are expected in `1. Question? - Answer.` format; the parser extracts those lines.

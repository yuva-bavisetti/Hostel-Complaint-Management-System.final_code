# Hostel Complaint Management System — Student & Admin (Java)

A **local Java Swing desktop application** for an Object-Oriented Methodology course project. Students can submit hostel-related complaints and check their status. An admin can review and update complaint statuses.

## Features

**Student**
- Sign in with a demonstration student account
- Select Hostel, Room, or Washroom issues
- Enter a location and complaint details
- Receive a unique complaint ID
- View only their own complaints and their current status

**Admin**
- Sign in with a separate demonstration admin account
- View all complaints
- Search and filter complaints
- View complaint details
- Update status to Pending, In Progress, or Resolved

## Requirements

- Windows laptop
- Java Development Kit (JDK) installed and available on `PATH` (the project has been used with Java 26)

No web hosting, internet connection, or database installation is needed to run this classroom demo.

## How to run

1. Click **Code → Download ZIP** on this repository.
2. Extract the ZIP file.
3. Open the extracted project folder containing `START_APP.bat`.
4. Double-click **`START_APP.bat`**.
5. Choose **Student** or **Admin** on the login screen.

Alternatively, open Command Prompt in the project folder and run:

```cmd
mkdir out
javac -encoding UTF-8 -d out *.java GUI\*.java
java -cp out complaint_main
```

## Demonstration login credentials

## Student Login Credentials

The system supports 101 predefined student accounts.

Username range: IIT2025100 to IIT2025200

Password: Last three digits of the roll number.

Examples:

| Username | Password |
|---|---|
| IIT2025100 | 100 |
| IIT2025150 | 150 |
| IIT2025200 | 200 |

## Admin Login

Username: admin
Password: admin123

**Security note:** These are hard-coded classroom-only accounts. Do not use this application with real students' personal information or deploy it as a production system.

## Example demonstration

1. Sign in as Student (`IIT2025100` / `100`).
2. Open **Room → Electrician → Fan**.
3. Enter location `A-101` and details `Ceiling fan is not working`.
4. Submit the complaint and note its `CMP-...` ID.
5. View **My Complaints** and confirm that the status is **Pending**.
6. Log out and sign in as Admin (`admin` / `admin123`).
7. Find the complaint and change its status to **In Progress**, then **Resolved**.
8. Log out, sign in again as Student, and confirm the updated status.

## Testing

On Windows, run `RUN_TESTS.bat` for automated checks. The source for the automated tests is in `tests/ProjectSelfTest.java`.

## Project structure

- `GUI/` — login and Student/Admin desktop windows
- `tests/` — self-test source
- `AuthService.java` — demonstration role authentication
- `ComplaintStore.java` — local complaint storage and status updates
- `complaint_main.java` — entry point
- `START_APP.bat` — compile and launch the application
- `RUN_TESTS.bat` — compile and run the automated checks

Complaint records are stored locally in a generated `complaints/` folder and are **not synchronized between computers**. Do not upload this folder to GitHub.

## Object-Oriented Methodology concepts

This project includes classes and objects, inheritance (`Complaint extends User`), an interface (`worker`), and interface implementation (`room implements worker`). The student/admin update also separates authentication, storage, and interface responsibilities.

## Original project and attribution

This coursework version is adapted from **Shashankagarwal181996's Hostel Complaint Management System**:

https://github.com/Shashankagarwal181996/Hostel-Complaint-Management-System

The upgraded classroom version adds distinct Student/Admin workflows, stored complaint records, status updates, and tests. Please comply with the original author's applicable permissions before publicly redistributing adapted source code.

## Limitations

This is a local classroom demonstration, not a production hostel information system. In particular, demo passwords are hard-coded, complaint storage uses local `.properties` files, and there is no secure networked database or multi-computer synchronization.

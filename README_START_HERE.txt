HOSTEL COMPLAINT MANAGEMENT SYSTEM - LOCAL STUDENT + ADMIN EDITION
=================================================================

WHAT THIS IS
------------
Windows Java Swing desktop app. One login screen with two roles.
No web hosting, internet connection or database installation is needed.
Complaints are stored as .properties files in the local "complaints" folder.

IMPORTANT - USE THIS EXTRACTED FOLDER, NOT THE OLD NESTED MASTER FOLDER.
This is a separate complete project. Your old project is not overwritten.

START ON WINDOWS
----------------
1. Right-click the downloaded ZIP, choose Extract All.
2. Open the extracted folder "Hostel_Complaint_System_Dual_Role".
3. Double-click START_APP.bat. Leave the Command Prompt window open.
4. The Java login window appears. Select Student or Admin.

If the ZIP extraction creates an extra outer folder, open it until you see
START_APP.bat, AuthService.java, ComplaintStore.java and the GUI folder.

ALTERNATIVE COMMAND PROMPT COMMANDS
-----------------------------------
Open CMD in the extracted project folder, then type these one at a time:
    javac -encoding UTF-8 -d out *.java GUI\*.java
    java -cp out complaint_main
The 'out' folder is created by START_APP.bat automatically; if using CMD
and out does not exist yet, type: mkdir out

DEMO ACCOUNTS (NOT SECURE, ONLY FOR CLASSROOM TESTING)
-----------------------------------------------------
Student role: username v        password v
Student role: username saana    password saana
Student role: username shashanka password shashanka
Student role: username shashankg password shashankg
Admin role:   username admin    password admin123

STUDENT FUNCTIONS
-----------------
- Login as a student.
- Choose Hostel, Room or Washroom and select the problem checkbox.
- Enter a location (room, wing or washroom number).
- Optionally add a description.
- Submit Complaint -> see a unique CMP-... ID and initial status Pending.
- View My Complaints -> read only the logged-in student's complaints.
- Refresh status, view details and log out.

ADMIN FUNCTIONS
---------------
- Login as admin.
- See all students' complaints.
- Search by ID, student username, category, location or issue.
- Filter by Pending / In Progress / Resolved.
- Select a complaint and View Details or change the status.
- Click Refresh to load recently submitted complaints.
- Log out.

DEMONSTRATE THE TWO ROLES LOCALLY
--------------------------------
1. Student login v / v -> Room > Electrician > Fan.
2. Enter location A-101 and description 'Ceiling fan is not working'.
3. Click Submit Complaint. Note the CMP-... complaint ID.
4. View My Complaints -> verify Pending. Close history and Log Out.
5. Choose Admin; login admin / admin123.
6. Find complaint by student v or by the CMP-... ID.
7. Select it -> Mark In Progress -> Mark Resolved.
8. Log Out -> Student v/v -> View My Complaints -> verify Resolved.
9. Log Out -> Student saana/saana -> verify saana cannot see v's record.

WHERE DATA GOES
---------------
The directory 'complaints' is created inside this project folder after a
complaint is submitted. Each record is a local .properties text file.
The data remains when you close and reopen the app as long as you launch
from the same project folder. Do not move/delete the complaints folder.

To bring your *existing* test complaints from the earlier upgraded project:
close BOTH apps, then copy your old project's 'complaints' folder into this
new project folder, merging files if prompted. Back up both folders first.

TESTS
-----
Double-click RUN_TESTS.bat to check login roles, unique IDs, saved status,
invalid status handling, etc. It writes separate test records in
"test_workspace/complaints" and does not touch your real complaint records.
Also perform the GUI demonstration manually to verify checkboxes/screens.

WHAT THIS PROJECT DOES NOT DO
-----------------------------
- It is NOT an online web application; the records are local to one folder.
- On different computers, there is no automatic synchronization.
- There is no production security: passwords are hard-coded demonstration
  credentials, and .properties files are plain text and editable by anyone
  with filesystem access. Role separation is UI-level, not server-enforced.
- It does not send emails/SMS, and it has no actual external database.
- If running two app windows at the same time on one laptop, press Refresh
  to see changes made by the other window. File writes have no concurrency
  locking, so use sequential login/logout for a reliable class demo.

SOURCE AND ATTRIBUTION
----------------------
Based on the Java files provided from the GitHub project:
https://github.com/Shashankagarwal181996/Hostel-Complaint-Management-System
Original author: Shashankagarwal181996. The added role-based dashboards,
authentication helper, and local complaint storage are classroom demo
modifications. Credit the original author and follow your lecturer's reuse
and academic integrity requirements.

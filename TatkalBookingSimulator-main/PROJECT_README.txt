IRCTC TATKAL TICKET BOOKING SIMULATOR (ANDROID / JAVA)

Implemented in this source package:
- Train, Coach, Seat, Passenger, Booking, Ticket and BookingStatus model classes.
- Sample train search and dynamic booking screens.
- Thread-safe synchronized booking engine with atomic booking IDs.
- CONFIRMED, RAC (limited to five RAC places per train), WAITING and CANCELLED states.
- FIFO waiting queue and promotion when confirmed/RAC bookings are cancelled.
- Ticket confirmation screen with passenger, train, journey date, status and seat/queue position.
- Booking report UI, cancel-by-PNR UI, and a competing-thread demonstration (1-200 threads).
- Export booking report as a text file to the app's private internal storage.
- JUnit tests for full-train status allocation and cancellation promotion.

IMPORTANT LIMITATIONS:
- This is an offline educational simulator, not connected to IRCTC and not a real ticketing service.
- Booking/train state is in memory and resets when Android kills the app. The user requested database integration as the next phase; no database is added in this version.
- Android Studio's full Gradle build could not be run in this environment because the Gradle 9.6.0 distribution was not cached and external network access was unavailable. The core model/engine Java classes were compiled with javac successfully. Please sync/build on your Android Studio machine and share any first build error if it appears.
- The assignment mentions Swing GUI; this deliverable uses Android UI because the supplied project is an Android app. It covers the same application workflows in Android rather than Swing.

HOW TO TEST:
1. Open this folder in Android Studio and let Gradle sync.
2. Run the app; search Mumbai -> Pune for train 12001.
3. Book 16+ passengers on the same train to see CONFIRMED, RAC and WAITING states.
4. Open "Booking Report / Cancel / Concurrency Test" from the search screen.
5. Cancel a PNR to test promotion; run 25+ concurrent attempts to see synchronization in action.
6. Use Export report to text file to write tatkal_booking_report.txt in app-private storage.

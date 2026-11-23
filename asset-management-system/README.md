# Enterprise Asset Management System

A production-grade, full-stack **Asset Management System** built with **Angular 18**, **Java Spring Boot 3**, and **MongoDB**. Designed strictly for enterprise hardware lifecycle management, employee device allocation, cellular identity tracking (SIM & carrier management), and telecom recharge validity monitoring.

---

## 1. Technology Stack

### Frontend
- **Angular 18** (Standalone components, reactive forms, modern control flow)
- **TypeScript 5.4** & **HTML5 / CSS3**
- **Bootstrap 5.3** & **Bootstrap Icons** (Responsive layouts, badges, and modals)
- **Chart.js 4.4** (Interactive donut charts for hardware allocation and SIM deployment)
- **Angular Router** with **Auth Route Guards**
- **HttpClient** with **JWT Interceptor** for Bearer token authorization

### Backend
- **Java 17 / 21**
- **Spring Boot 3.2.5**
- **Spring Security 6** with **Stateless JWT Authentication** (`io.jsonwebtoken 0.12.5`)
- **Spring Data MongoDB** with indexed collections & repositories
- **BCrypt Password Encryption** (`BCryptPasswordEncoder`)
- **Jakarta Bean Validation** (`@Valid`, `@NotBlank`, `@DecimalMin`, etc.)
- **Spring Task Scheduling** (`@Scheduled` cron job for daily SIM validity checks)
- **Maven 3.9**

### Database
- **MongoDB** (Database name: `asset_management`)
- Native MongoDB document persistence (No MySQL, JDBC, JPA, or SQL queries).

---

## 2. Business Rules & Architecture Highlights

1. **Laptop Constraints**:
   - One employee can have a maximum of **one active laptop**.
   - One laptop can be assigned to **only one employee** at a time.
   - Laptops with `Under Repair` or `Retired` status cannot be assigned.
2. **Mobile Constraints**:
   - One employee can have a maximum of **one active mobile phone**.
   - One mobile phone can be assigned to **only one employee** at a time.
   - Unavailable, under repair, or retired mobile phones cannot be assigned.
3. **SIM Constraints & Dual-SIM Association**:
   - One employee can have a maximum of **two active SIM cards**.
   - One mobile phone can hold a maximum of **two associated SIM cards**.
   - **Crucial Rule**: A SIM must be associated with the employee's currently assigned mobile phone. SIM assignments cannot be made without an active mobile device for that employee.
4. **Mobile Return Safeguard (Prevent Orphan SIMs)**:
   - When attempting to return a mobile phone that has active SIM assignments, the system **blocks the return** and instructs the administrator to return or reassign the associated SIM cards first.
5. **Inactive Employee Restriction**:
   - Employees marked `Inactive` cannot receive any new asset allocations, but historical asset records and previous assignments are fully preserved.
6. **Audit & History Immutability**:
   - Historical assignment and recharge records are **never deleted**. All past assignments are recorded in `laptop_assignments`, `mobile_assignments`, and `sim_assignments`.
7. **Telecom Recharges & Expiry Engine**:
   - Multiple recharge records can be added per SIM. Old records are preserved.
   - Daily automated scheduler evaluates remaining validity:
     - **Normal**: More than 7 days
     - **Expiring Soon**: 1 to 7 days
     - **Expires Today**: 0 days
     - **Expired**: Less than 0 days
8. **No Laptop Issue Management**:
   - As per specifications, **Laptop Issues / complaint tracking does not exist** anywhere in this application. It is exclusively an asset management system.

---

## 3. Project Directory Structure

```text
asset-management-system/
│
├── backend/
│   ├── src/main/java/com/assetmanagement/
│   │   ├── config/               # Security, JWT Provider, Filters, CORS, UserDetailsService
│   │   ├── controller/           # REST Controllers (Auth, Employees, Laptops, Mobiles, SIMs, History, Dashboard)
│   │   ├── dto/                  # Request/Response Data Transfer Objects
│   │   ├── exception/            # BusinessRuleException, GlobalExceptionHandler
│   │   ├── model/                # MongoDB Documents (Employee, Laptop, Mobile, SimCard, Recharge, etc.)
│   │   ├── repository/           # Spring Data MongoDB Repositories
│   │   ├── scheduler/            # SimExpiryScheduler (@Scheduled cron)
│   │   ├── service/              # Core business logic, rule validations, history aggregations
│   │   └── AssetManagementApplication.java
│   ├── src/main/resources/
│   │   └── application.properties
│   ├── pom.xml
│   └── target/asset-management-backend-1.0.0.jar
│
├── frontend/
│   ├── src/
│   │   ├── app/
│   │   │   ├── components/
│   │   │   │   ├── layout/       # Navy sidebar, sticky topbar with notification bell & badge
│   │   │   │   ├── login/        # Admin login page with reactive validation
│   │   │   │   ├── dashboard/    # Dynamic KPI cards, Chart.js donut charts, recent additions
│   │   │   │   ├── employees/    # Employee CRUD, department filter, asset modal
│   │   │   │   ├── laptops/      # Laptop asset management, assignment, return with condition
│   │   │   │   ├── mobiles/      # Mobile asset management, IMEI unique checks, return protection
│   │   │   │   ├── sim-cards/    # SIM management, carrier & type tracking, details modal
│   │   │   │   ├── sim-assignments/ # Combined SIM assignments & Telecom recharge validity
│   │   │   │   └── asset-history/   # Unified audit trail across laptops, mobiles, and SIMs
│   │   │   ├── guards/           # auth.guard.ts
│   │   │   ├── interceptors/     # jwt.interceptor.ts
│   │   │   ├── models/           # TypeScript interfaces
│   │   │   ├── services/         # Angular HttpClient services
│   │   │   ├── app.routes.ts     # Route mapping with AuthGuard
│   │   │   ├── app.config.ts
│   │   │   └── app.component.ts
│   │   ├── assets/
│   │   ├── index.html
│   │   ├── main.ts
│   │   └── styles.css
│   ├── angular.json
│   ├── tsconfig.json
│   ├── package.json
│   └── dist/asset-management-frontend/
│
├── database/
│   └── sample-data.json          # Pre-configured dataset with test records
│
├── .env.example                  # Environment configuration variables template
└── README.md                     # Installation, execution, and verification guide
```

---

## 4. Database Setup & Configuration

### Standard MongoDB Setup
Ensure MongoDB Community Server is installed and running on default port `27017`.
Database Name: `asset_management`

The backend connects automatically via:
```properties
spring.data.mongodb.uri=mongodb://localhost:27017/asset_management
spring.data.mongodb.auto-index-creation=true
```

### Initial Data & Admin Seeding
On first application startup, `DataInitializationService` automatically:
1. Creates the default admin account:
   - **Username**: `admin`
   - **Password**: `admin123`
2. Seeds sample employees, laptops, mobile phones, dual-SIM assignments, and recharge records.
3. Automatically triggers the SIM validity notification calculator.

You can also import `database/sample-data.json` directly using `mongoimport`:
```bash
mongoimport --db asset_management --collection admins --file database/sample-data.json --jsonArray
```

### Configuring MongoDB Replica Set (For Multi-Document Transactions)
MongoDB requires a replica set for multi-document transaction support. To convert a standalone local instance into a single-node replica set:

1. Stop running MongoDB service or process.
2. Start mongod with `--replSet rs0`:
   ```bash
   mongod --dbpath "C:\data\db" --replSet rs0 --port 27017
   ```
3. Open `mongosh` and initiate the replica set:
   ```javascript
   rs.initiate({
     _id: "rs0",
     members: [ { _id: 0, host: "127.0.0.1:27017" } ]
   })
   ```
4. Verify replica status with `rs.status()`.

---

## 5. Execution & Running Instructions

### Running the Backend

#### Option A: Run Pre-Packaged Spring Boot JAR
```bash
cd backend
java -jar target/asset-management-backend-1.0.0.jar
```

#### Option B: Run via Maven
```bash
cd backend
mvn spring-boot:run
```
The backend starts on `http://localhost:8085`.

### Running the Frontend

#### Development Mode (with Live Reload):
```bash
cd frontend
npm start
```
Open your browser and navigate to `http://localhost:4300`.

#### Production Mode (Pre-built Dist):
The production distribution is already compiled in `frontend/dist/asset-management-frontend/browser`.
You can serve it using any HTTP server:
```bash
npx serve dist/asset-management-frontend/browser -p 4300
```

---

## 6. Admin Authentication & Default Credentials

| Field | Default Value |
|---|---|
| **URL** | `http://localhost:4300/login` |
| **Username** | `admin` |
| **Password** | `admin123` |
| **Role** | System Administrator (`ROLE_ADMIN`) |

All application routes are protected by Angular's `authGuard`. Direct access without a valid JWT token automatically redirects to `/login`.

---

## 7. Key Features Walkthrough

### 1. Dashboard
- 8 KPI summary cards dynamically querying MongoDB.
- 3 interactive donut charts powered by Chart.js (Laptop allocation, Mobile status, SIM deployment).
- SIM recharge alerts banner showing expiring soon and expired SIMs with remaining days.
- Recent additions for employees and assets.
- Quick action navigation pills.

### 2. Employee Management
- Search, filter by department and status.
- Modal to view currently assigned assets (Laptop, Mobile, dual-SIMs) and full audit history.
- Marking an employee `Inactive` preserves history but prevents new asset allocations.

### 3. Laptop Asset Management
- Add/Edit laptops with unique Serial Number enforcement.
- Assign laptop modal (enforces maximum 1 laptop per employee rule).
- Return laptop modal (records return condition: Good, Minor Scratches, Damaged, and comments).
- Full assignment history modal.

### 4. Mobile Phone Management
- Add/Edit smartphones with 15-digit unique IMEI enforcement.
- SIM capacity badge (`0/2`, `1/2`, `2/2` active SIMs).
- Return Protection: Returns are blocked if active SIM cards remain associated.

### 5. SIM Cards & Assignments & Recharges
- SIM card inventory with carrier identification (Airtel, Jio, Vi, BSNL, Other).
- SIM assignment enforcing pairing with employee's current active mobile phone.
- SIM recharge logging with automatic population of phone number, provider, and assigned employee.
- Automatic plan expiry calculation with real-time notification updates.

### 6. Asset History
- Unified, immutable audit log across all asset types.
- Filter by employee, asset type, assignment status, and date range.

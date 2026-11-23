package com.assetmanagement.service;

import com.assetmanagement.model.*;
import com.assetmanagement.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class DataInitializationService implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializationService.class);

    private final AdminRepository adminRepository;
    private final EmployeeRepository employeeRepository;
    private final LaptopRepository laptopRepository;
    private final LaptopAssignmentRepository laptopAssignmentRepository;
    private final MobileRepository mobileRepository;
    private final MobileAssignmentRepository mobileAssignmentRepository;
    private final SimCardRepository simCardRepository;
    private final SimAssignmentRepository simAssignmentRepository;
    private final RechargeRepository rechargeRepository;
    private final NotificationSchedulerService notificationSchedulerService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username:admin}")
    private String adminUsername;

    @Value("${app.admin.password:admin123}")
    private String adminPassword;

    @Value("${app.admin.fullName:System Administrator}")
    private String adminFullName;

    @Value("${app.admin.email:admin@company.com}")
    private String adminEmail;

    public DataInitializationService(AdminRepository adminRepository,
                                   EmployeeRepository employeeRepository,
                                   LaptopRepository laptopRepository,
                                   LaptopAssignmentRepository laptopAssignmentRepository,
                                   MobileRepository mobileRepository,
                                   MobileAssignmentRepository mobileAssignmentRepository,
                                   SimCardRepository simCardRepository,
                                   SimAssignmentRepository simAssignmentRepository,
                                   RechargeRepository rechargeRepository,
                                   NotificationSchedulerService notificationSchedulerService,
                                   PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.employeeRepository = employeeRepository;
        this.laptopRepository = laptopRepository;
        this.laptopAssignmentRepository = laptopAssignmentRepository;
        this.mobileRepository = mobileRepository;
        this.mobileAssignmentRepository = mobileAssignmentRepository;
        this.simCardRepository = simCardRepository;
        this.simAssignmentRepository = simAssignmentRepository;
        this.rechargeRepository = rechargeRepository;
        this.notificationSchedulerService = notificationSchedulerService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        initAdmin();
        initSampleData();
        notificationSchedulerService.updateSimExpiryNotifications();
    }

    private void initAdmin() {
        if (!adminRepository.existsByUsername(adminUsername)) {
            Admin admin = new Admin();
            admin.setUsername(adminUsername);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setFullName(adminFullName);
            admin.setEmail(adminEmail);
            admin.setRole("ROLE_ADMIN");
            adminRepository.save(admin);
            logger.info("Admin account initialized: username = {}", adminUsername);
        }
    }

    private void initSampleData() {
        if (employeeRepository.count() > 0) {
            return; // Data already exists
        }

        logger.info("Seeding initial sample data...");

        // Employees
        Employee e1 = employeeRepository.save(new Employee("EMP-101", "Preetha Sharma", "Developers", "Senior Software Engineer", LocalDate.now().minusMonths(14), "preetha.s@company.com", "9876500001", "Active"));
        Employee e2 = employeeRepository.save(new Employee("EMP-102", "Rahul Verma", "Developers", "Product Manager", LocalDate.now().minusMonths(9), "rahul.v@company.com", "9876500002", "Active"));
        Employee e3 = employeeRepository.save(new Employee("EMP-103", "Ananya Iyer", "HR", "HR Generalist", LocalDate.now().minusMonths(6), "ananya.i@company.com", "9876500003", "Active"));
        Employee e4 = employeeRepository.save(new Employee("EMP-104", "Vikram Patel", "Admin", "Operations Lead", LocalDate.now().minusMonths(18), "vikram.p@company.com", "9876500004", "Inactive"));

        // Laptops
        Laptop l1 = new Laptop("LAP-0001", "ThinkPad T14 Gen 4", "Lenovo", "SN-LNV-2024-001", "Project Titan", "Core i7, 16GB, 512GB SSD", "Intel Core i7", "16 GB", "512 GB SSD", LocalDate.now().minusMonths(10), LocalDate.now().plusMonths(26), "Assigned");
        l1.setAssignedEmployeeId(e1.getEmployeeId());
        l1.setAssignedEmployeeName(e1.getName());
        l1.setAssignedDate(LocalDate.now().minusMonths(8));
        laptopRepository.save(l1);

        laptopAssignmentRepository.save(new LaptopAssignment(l1.getId(), l1.getLaptopId(), l1.getName(), l1.getSerialNumber(),
                e1.getEmployeeId(), e1.getName(), e1.getDepartment(), LocalDate.now().minusMonths(8), "Allocated for engineering development"));

        Laptop l2 = new Laptop("LAP-0002", "MacBook Pro 14", "Apple", "SN-APL-2024-002", "FinTech Core", "Apple M3, 16GB, 512GB SSD", "Apple M3", "16 GB", "512 GB SSD", LocalDate.now().minusMonths(5), LocalDate.now().plusMonths(19), "Assigned");
        l2.setAssignedEmployeeId(e2.getEmployeeId());
        l2.setAssignedEmployeeName(e2.getName());
        l2.setAssignedDate(LocalDate.now().minusMonths(4));
        laptopRepository.save(l2);

        laptopAssignmentRepository.save(new LaptopAssignment(l2.getId(), l2.getLaptopId(), l2.getName(), l2.getSerialNumber(),
                e2.getEmployeeId(), e2.getName(), e2.getDepartment(), LocalDate.now().minusMonths(4), "Standard PM laptop allocation"));

        Laptop l3 = new Laptop("LAP-0003", "Latitude 5440", "Dell", "SN-DEL-2024-003", "Healthcare Portal", "Core i5, 16GB, 512GB SSD", "Intel Core i5", "16 GB", "512 GB SSD", LocalDate.now().minusMonths(3), LocalDate.now().plusMonths(33), "Available");
        laptopRepository.save(l3);

        Laptop l4 = new Laptop("LAP-0004", "EliteBook 840 G10", "HP", "SN-HP-2024-004", "Internal Operations", "Core i7, 32GB, 1TB SSD", "Intel Core i7", "32 GB", "1 TB SSD", LocalDate.now().minusMonths(2), LocalDate.now().plusMonths(34), "Available");
        laptopRepository.save(l4);

        // Mobiles (No mobile number on Mobile asset!)
        Mobile m1 = new Mobile("MOB-0001", "Galaxy S23", "Samsung", "864201049281001", "Project Titan", "Dual SIM", LocalDate.now().minusMonths(8), LocalDate.now().plusMonths(16), "Assigned");
        m1.setAssignedEmployeeId(e1.getEmployeeId());
        m1.setAssignedEmployeeName(e1.getName());
        m1.setAssignedDate(LocalDate.now().minusMonths(7));
        m1.setSlot1SimId("SIM-001");
        m1.setSlot1MobileNumber("9876543210");
        m1.setSlot1Provider("Airtel");
        m1.setSlot2SimId("SIM-002");
        m1.setSlot2MobileNumber("9876543211");
        m1.setSlot2Provider("Jio");
        m1.setActiveSimCount(2); // Preetha has 2 SIM cards
        mobileRepository.save(m1);

        mobileAssignmentRepository.save(new MobileAssignment(m1.getId(), m1.getMobileId(), m1.getName(), m1.getImeiNumber(),
                e1.getEmployeeId(), e1.getName(), e1.getDepartment(), LocalDate.now().minusMonths(7), "Dual-SIM testing device"));

        Mobile m2 = new Mobile("MOB-0002", "iPhone 15", "Apple", "358765091234567", "FinTech Core", "Dual SIM", LocalDate.now().minusMonths(6), LocalDate.now().plusMonths(18), "Assigned");
        m2.setAssignedEmployeeId(e2.getEmployeeId());
        m2.setAssignedEmployeeName(e2.getName());
        m2.setAssignedDate(LocalDate.now().minusMonths(5));
        m2.setSlot1SimId("SIM-003");
        m2.setSlot1MobileNumber("9876543212");
        m2.setSlot1Provider("Vi");
        m2.setActiveSimCount(1);
        mobileRepository.save(m2);

        mobileAssignmentRepository.save(new MobileAssignment(m2.getId(), m2.getMobileId(), m2.getName(), m2.getImeiNumber(),
                e2.getEmployeeId(), e2.getName(), e2.getDepartment(), LocalDate.now().minusMonths(5), "Corporate mobile phone"));

        Mobile m3 = new Mobile("MOB-0003", "Pixel 8", "Google Pixel", "354982103948576", "Healthcare Portal", "Single SIM", LocalDate.now().minusMonths(2), LocalDate.now().plusMonths(22), "Available");
        mobileRepository.save(m3);

        // SIM Cards
        SimCard s1 = new SimCard("SIM-001", "9876543210", "Airtel", "899110120240001", "Physical SIM", "Assigned");
        s1.setAssignedEmployeeId(e1.getEmployeeId());
        s1.setAssignedEmployeeName(e1.getName());
        s1.setAssignedMobileId(m1.getMobileId());
        s1.setAssignedMobileName(m1.getName());
        s1.setLatestRechargePlan("Corporate Unlimited 84D");
        s1.setLatestRechargeAmount(719.0);
        s1.setLatestRechargeDate(LocalDate.now().minusDays(80));
        s1.setLatestPlanEndDate(LocalDate.now().plusDays(4)); // Expiring in 4 days! -> Expiring Soon
        simCardRepository.save(s1);

        SimAssignment sa1 = new SimAssignment(s1.getId(), s1.getSimId(), s1.getMobileNumber(), s1.getProvider(),
                e1.getEmployeeId(), e1.getName(), m1.getMobileId(), m1.getName(), LocalDate.now().minusMonths(7), "Primary company SIM");
        sa1.setSlotNumber(1);
        simAssignmentRepository.save(sa1);

        rechargeRepository.save(new Recharge(s1.getSimId(), s1.getMobileNumber(), s1.getProvider(),
                e1.getEmployeeId(), e1.getName(), 719.0, "Corporate Unlimited 84D", LocalDate.now().minusDays(80), LocalDate.now().plusDays(4), "Quarterly corporate plan"));

        SimCard s2 = new SimCard("SIM-002", "9876543211", "Jio", "899120120240002", "Physical SIM", "Assigned");
        s2.setAssignedEmployeeId(e1.getEmployeeId());
        s2.setAssignedEmployeeName(e1.getName());
        s2.setAssignedMobileId(m1.getMobileId());
        s2.setAssignedMobileName(m1.getName());
        s2.setLatestRechargePlan("5G Data Plus 28D");
        s2.setLatestRechargeAmount(349.0);
        s2.setLatestRechargeDate(LocalDate.now().minusDays(30));
        s2.setLatestPlanEndDate(LocalDate.now().minusDays(2)); // Expired 2 days ago! -> Expired
        simCardRepository.save(s2);

        SimAssignment sa2 = new SimAssignment(s2.getId(), s2.getSimId(), s2.getMobileNumber(), s2.getProvider(),
                e1.getEmployeeId(), e1.getName(), m1.getMobileId(), m1.getName(), LocalDate.now().minusMonths(6), "Secondary data SIM (Preetha SIM 2)");
        sa2.setSlotNumber(2);
        simAssignmentRepository.save(sa2);

        rechargeRepository.save(new Recharge(s2.getSimId(), s2.getMobileNumber(), s2.getProvider(),
                e1.getEmployeeId(), e1.getName(), 349.0, "5G Data Plus 28D", LocalDate.now().minusDays(30), LocalDate.now().minusDays(2), "Monthly data plan"));

        SimCard s3 = new SimCard("SIM-003", "9876543212", "Vi", "899130120240003", "eSIM", "Assigned");
        s3.setAssignedEmployeeId(e2.getEmployeeId());
        s3.setAssignedEmployeeName(e2.getName());
        s3.setAssignedMobileId(m2.getMobileId());
        s3.setAssignedMobileName(m2.getName());
        s3.setLatestRechargePlan("Unlimited Calling + 100GB");
        s3.setLatestRechargeAmount(999.0);
        s3.setLatestRechargeDate(LocalDate.now().minusDays(15));
        s3.setLatestPlanEndDate(LocalDate.now().plusDays(69)); // Normal
        simCardRepository.save(s3);

        SimAssignment sa3 = new SimAssignment(s3.getId(), s3.getSimId(), s3.getMobileNumber(), s3.getProvider(),
                e2.getEmployeeId(), e2.getName(), m2.getMobileId(), m2.getName(), LocalDate.now().minusMonths(5), "Assigned eSIM for PM calls");
        sa3.setSlotNumber(1);
        simAssignmentRepository.save(sa3);

        rechargeRepository.save(new Recharge(s3.getSimId(), s3.getMobileNumber(), s3.getProvider(),
                e2.getEmployeeId(), e2.getName(), 999.0, "Unlimited Calling + 100GB", LocalDate.now().minusDays(15), LocalDate.now().plusDays(69), "Corporate quarterly"));

        SimCard s4 = new SimCard("SIM-004", "9876543213", "BSNL", "899140120240004", "Physical SIM", "Available");
        simCardRepository.save(s4);

        logger.info("Sample data initialization complete.");
    }
}

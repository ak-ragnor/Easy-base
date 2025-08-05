package com.easyBase.config;

import com.easyBase.common.enums.SiteStatus;
import com.easyBase.common.enums.UserRole;
import com.easyBase.common.enums.UserStatus;
import com.easyBase.domain.entity.site.Site;
import com.easyBase.domain.entity.site.UserSite;
import com.easyBase.domain.entity.user.User;
import com.easyBase.domain.repository.jpa.site.SiteRepository;
import com.easyBase.domain.repository.jpa.site.UserSiteRepository;
import com.easyBase.domain.repository.jpa.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * Fixed Data Initializer - Creates default users and sites with ALL required fields
 *
 * This component runs after the Spring context is fully initialized
 * and ensures that default users and sites exist with properly set required fields.
 *
 * FIXES:
 * - Sets languageCode (required NOT NULL field)
 * - Sets timeZone (required NOT NULL field)
 * - Creates proper user-site relationships
 * - Handles password encoding correctly
 */
@Component
public class DataInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private UserSiteRepository userSiteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private boolean initialized = false;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (!initialized) {
            logger.info("Starting data initialization...");
            try {
                createDefaultData();
                logger.info("✅ Data initialization completed successfully");
            } catch (Exception e) {
                logger.error("❌ Data initialization failed: {}", e.getMessage(), e);
                throw e; // Re-throw to prevent application startup with bad data
            }
            initialized = true;
        }
    }

    private void createDefaultData() {
        // Step 1: Create default site with ALL required fields
        Site defaultSite = createDefaultSite();

        // Step 2: Create users with passwords and required fields
        User adminUser = createUserIfNotExists(
                "admin@easybase.com", "System Administrator", "admin123", UserRole.ADMIN
        );

        User managerUser = createUserIfNotExists(
                "manager@easybase.com", "Default Manager", "manager123", UserRole.MANAGER
        );

        User regularUser = createUserIfNotExists(
                "user@easybase.com", "Default User", "user123", UserRole.USER
        );

        // Step 3: Link users to default site with proper roles
        linkUserToSite(adminUser, defaultSite, UserRole.ADMIN);
        linkUserToSite(managerUser, defaultSite, UserRole.MANAGER);
        linkUserToSite(regularUser, defaultSite, UserRole.USER);

        // Step 4: Create additional sites for testing (if in dev mode)
        if (isDevEnvironment()) {
            createAdditionalTestSites();
            createBulkTestUsers(defaultSite);
        }

        logger.info("Created default site '{}' with {} users",
                defaultSite.getCode(), defaultSite.getUserSites().size());
    }

    /**
     * Create default site with ALL required fields properly set
     */
    private Site createDefaultSite() {
        return siteRepository.findByCode("DEFAULT")
                .orElseGet(() -> {
                    logger.info("Creating default site...");

                    Site site = new Site();

                    // Required fields - MUST be set or DB constraint violation occurs
                    site.setCode("DEFAULT");
                    site.setName("Default Site");
                    site.setStatus(SiteStatus.ACTIVE);
                    site.setTimeZone("UTC");              // ✅ FIXED: Set required timeZone
                    site.setLanguageCode("en");           // ✅ FIXED: Set required languageCode

                    // Optional fields
                    site.setDescription("Default site for application access");

                    // Audit fields
                    site.setCreatedAt(ZonedDateTime.now());
                    site.setLastModified(ZonedDateTime.now());
                    site.setVersion(0L);

                    Site savedSite = siteRepository.save(site);
                    logger.info("✅ Created default site: {} (ID: {})", savedSite.getCode(), savedSite.getId());
                    return savedSite;
                });
    }

    /**
     * Create user if not exists with proper password encoding
     */
    private User createUserIfNotExists(String email, String name, String password, UserRole role) {
        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isEmpty()) {
            logger.info("Creating user: {} ({})", email, role);

            User user = new User();

            // Required fields
            user.setName(name);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));  // ✅ Properly encoded password
            user.setRole(role);
            user.setStatus(UserStatus.ACTIVE);

            // Optional fields with defaults
            user.setUserTimezone("UTC");

            // Audit fields
            user.setCreatedAt(ZonedDateTime.now());
            user.setLastModified(ZonedDateTime.now());
            user.setVersion(0L);

            User savedUser = userRepository.save(user);
            logger.info("✅ Created user: {} (ID: {}, Role: {})", email, savedUser.getId(), role);
            return savedUser;
        } else {
            logger.debug("User already exists: {}", email);
            return existingUser.get();
        }
    }

    /**
     * Link user to site with specific role if not already linked
     */
    private void linkUserToSite(User user, Site site, UserRole role) {
        if (!userSiteRepository.existsByUserIdAndSiteIdAndIsActive(user.getId(), site.getId(),true)) {
            logger.info("Linking user {} to site {} with role {}",
                    user.getEmail(), site.getCode(), role);

            UserSite userSite = new UserSite();
            userSite.setUser(user);
            userSite.setSite(site);
            userSite.setRole(role);
            userSite.setIsActive(true);
            userSite.setAccessGrantedAt(ZonedDateTime.now());
            userSite.setCreatedAt(ZonedDateTime.now());
            userSite.setLastModified(ZonedDateTime.now());
            userSite.setVersion(0L);

            userSiteRepository.save(userSite);
            logger.info("✅ Linked user {} to site {}", user.getEmail(), site.getCode());
        } else {
            logger.debug("User {} already linked to site {}", user.getEmail(), site.getCode());
        }
    }

    /**
     * Create additional test sites for development
     */
    private void createAdditionalTestSites() {
        logger.info("Creating additional test sites for development...");

        // North America Site
        createTestSite("US_EAST", "US East Coast Site", "America/New_York", "en-US");
        createTestSite("US_WEST", "US West Coast Site", "America/Los_Angeles", "en-US");

        // Europe Sites
        createTestSite("EU_LONDON", "London Site", "Europe/London", "en-GB");
        createTestSite("EU_PARIS", "Paris Site", "Europe/Paris", "fr-FR");

        // Asia Sites
        createTestSite("ASIA_TOKYO", "Tokyo Site", "Asia/Tokyo", "ja");
        createTestSite("ASIA_SINGAPORE", "Singapore Site", "Asia/Singapore", "en-SG");
    }

    /**
     * Create a test site with all required fields
     */
    private void createTestSite(String code, String name, String timeZone, String languageCode) {
        if (!siteRepository.findByCode(code).isPresent()) {
            Site site = new Site();
            site.setCode(code);
            site.setName(name);
            site.setStatus(SiteStatus.ACTIVE);
            site.setTimeZone(timeZone);
            site.setLanguageCode(languageCode);
            site.setDescription("Test site for " + name);
            site.setCreatedAt(ZonedDateTime.now());
            site.setLastModified(ZonedDateTime.now());
            site.setVersion(0L);

            Site savedSite = siteRepository.save(site);
            logger.info("✅ Created test site: {} ({})", code, name);
        }
    }

    /**
     * Create bulk test users for development and testing
     */
    private void createBulkTestUsers(Site defaultSite) {
        logger.info("Creating bulk test users for development...");

        // Create test users with different roles and timezones
        String[] timezones = {"UTC", "America/New_York", "Europe/London", "Asia/Tokyo"};
        UserRole[] roles = {UserRole.USER, UserRole.MANAGER, UserRole.ADMIN};

        for (int i = 1; i <= 20; i++) {
            String email = String.format("testuser%02d@easybase.com", i);
            String name = String.format("Test User %02d", i);
            String timezone = timezones[i % timezones.length];
            UserRole role = roles[i % roles.length];

            User testUser = createUserIfNotExists(email, name, "test123", role);
            testUser.setUserTimezone(timezone);
            userRepository.save(testUser);

            // Link to default site
            linkUserToSite(testUser, defaultSite, role);
        }

        logger.info("✅ Created 20 test users for development");
    }

    /**
     * Check if running in development environment
     */
    private boolean isDevEnvironment() {
        String profiles = System.getProperty("spring.profiles.active", "dev");
        return profiles.contains("dev") || profiles.contains("test");
    }
}
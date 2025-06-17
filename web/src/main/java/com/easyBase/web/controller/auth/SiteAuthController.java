package com.easyBase.web.controller.auth;

import com.easyBase.common.dto.auth.SiteLoginRequest;
import com.easyBase.common.dto.auth.SiteLoginResponse;
import com.easyBase.service.security.SiteAuthenticationService;
import com.easyBase.web.controller.base.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Site Authentication Controller
 * Handles site-specific user authentication and JWT token generation
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Site Authentication", description = "Site-specific authentication operations")
public class SiteAuthController extends BaseController {

    @Autowired
    private SiteAuthenticationService siteAuthenticationService;

    @PostMapping("/{siteId}/login")
    @Operation(summary = "Authenticate user for specific site",
            description = "Authenticates a user for a specific site and returns JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "403", description = "User not authorized for this site"),
            @ApiResponse(responseCode = "404", description = "Site not found")
    })
    public ResponseEntity<SiteLoginResponse> loginToSite(
            @Parameter(description = "Site ID") @PathVariable Long siteId,
            @Valid @RequestBody SiteLoginRequest request) {

        _log.info("Site login attempt for site {} by user {}", siteId, request.getEmail());

        SiteLoginResponse response = siteAuthenticationService.authenticateUserForSite(
                siteId, request.getEmail(), request.getPassword());

        _log.info("Site login successful for site {} by user {}", siteId, request.getEmail());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/site/{siteCode}/login")
    @Operation(summary = "Authenticate user for specific site by code",
            description = "Authenticates a user for a specific site using site code")
    public ResponseEntity<SiteLoginResponse> loginToSiteBySiteCode(
            @Parameter(description = "Site Code") @PathVariable String siteCode,
            @Valid @RequestBody SiteLoginRequest request) {

        _log.info("Site login attempt for site {} by user {}", siteCode, request.getEmail());

        SiteLoginResponse response = siteAuthenticationService.authenticateUserForSite(
                siteCode, request.getEmail(), request.getPassword());

        _log.info("Site login successful for site {} by user {}", siteCode, request.getEmail());

        return ResponseEntity.ok(response);
    }

    private static final Logger _log = LoggerFactory.getLogger(SiteAuthController.class);
}
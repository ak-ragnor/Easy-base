package com.easyBase.web.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Enhanced React SPA servlet with detailed debugging and path resolution
 */
public class ReactSpaServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(ReactSpaServlet.class);
    private static final String DEFAULT_INDEX_PATH = "/index.html";

    private String indexPath;
    private String indexContent;

    @Override
    public void init() throws ServletException {
        super.init();

        indexPath = getInitParameter("indexPath");
        if (indexPath == null || indexPath.trim().isEmpty()) {
            indexPath = DEFAULT_INDEX_PATH;
        }

        logger.info("ReactSpaServlet initialized with index path: {}", indexPath);
        logger.info("Context path: {}", getServletContext().getContextPath());
        logger.info("Servlet context real path: {}", getServletContext().getRealPath("/"));

        // List all files in webapp for debugging
        debugWebappContents();

        // Pre-load the index.html content for performance
        loadIndexContent();
    }

    private void debugWebappContents() {
        try {
            String webappPath = getServletContext().getRealPath("/");
            if (webappPath != null) {
                File webappDir = new File(webappPath);
                logger.info("=== WEBAPP DIRECTORY CONTENTS ===");
                logger.info("Webapp root: {}", webappPath);
                listDirectory(webappDir, "", 0);
                logger.info("=== END WEBAPP CONTENTS ===");
            }
        } catch (Exception e) {
            logger.error("Failed to debug webapp contents", e);
        }
    }

    private void listDirectory(File dir, String prefix, int depth) {
        if (depth > 3) return; // Limit recursion depth

        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    logger.info("{}DIR: {}/", prefix, file.getName());
                    listDirectory(file, prefix + "  ", depth + 1);
                } else {
                    logger.info("{}FILE: {} ({})", prefix, file.getName(), file.length());
                }
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        String path = requestURI.substring(contextPath.length());

        logger.info("=== REQUEST DEBUG ===");
        logger.info("Request URI: {}", requestURI);
        logger.info("Context Path: {}", contextPath);
        logger.info("Servlet Path: {}", path);
        logger.info("Query String: {}", request.getQueryString());
        logger.info("===================");

        // Check if this is an API request (shouldn't happen due to servlet mapping)
        if (path.startsWith("/api/")) {
            logger.warn("API request received by ReactSpaServlet: {}", path);
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Check if this is a static asset request
        if (isStaticAsset(path)) {
            logger.info("Static asset request detected: {}", path);

            // Try to serve the static file directly
            String realPath = getServletContext().getRealPath(path);
            logger.info("Real path for static asset: {}", realPath);

            if (realPath != null && new File(realPath).exists()) {
                logger.info("Static file exists, forwarding to default servlet");
                getServletContext().getNamedDispatcher("default").forward(request, response);
                return;
            } else {
                logger.error("Static file not found: {} (real path: {})", path, realPath);

                // List what's actually in the static directory
                String staticDirPath = getServletContext().getRealPath("/static");
                if (staticDirPath != null) {
                    File staticDir = new File(staticDirPath);
                    if (staticDir.exists() && staticDir.isDirectory()) {
                        logger.info("Contents of /static directory:");
                        listDirectory(staticDir, "  ", 0);
                    } else {
                        logger.error("Static directory does not exist: {}", staticDirPath);
                    }
                }

                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Static asset not found: " + path);
                return;
            }
        }

        // For all other requests (React routes), serve the index.html
        serveReactApp(request, response, path);
    }

    private void serveReactApp(HttpServletRequest request, HttpServletResponse response, String path)
            throws IOException {

        logger.info("Serving React app for route: {} (original: {})", path, request.getRequestURI());

        // Set appropriate headers for HTML content
        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        // Add cache control headers
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        // Write the pre-loaded index content
        if (indexContent != null) {
            response.getWriter().write(indexContent);
            logger.info("Served pre-loaded index.html content ({} characters)", indexContent.length());
        } else {
            // Fallback: load content on-demand
            loadAndServeIndexContent(response);
        }

        response.getWriter().flush();
    }

    private void loadIndexContent() {
        try (InputStream inputStream = getServletContext().getResourceAsStream(indexPath)) {
            if (inputStream != null) {
                indexContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                logger.info("Pre-loaded index.html content ({} characters)", indexContent.length());
                logger.debug("Index content preview: {}", indexContent.substring(0, Math.min(200, indexContent.length())));
            } else {
                logger.error("Could not find index.html at path: {}", indexPath);

                // Try to find index.html in the webapp
                String realIndexPath = getServletContext().getRealPath(indexPath);
                logger.error("Real path for index.html: {}", realIndexPath);

                if (realIndexPath != null) {
                    File indexFile = new File(realIndexPath);
                    logger.error("Index file exists: {}", indexFile.exists());
                }
            }
        } catch (IOException e) {
            logger.error("Error loading index.html content", e);
        }
    }

    private void loadAndServeIndexContent(HttpServletResponse response) throws IOException {
        try (InputStream inputStream = getServletContext().getResourceAsStream(indexPath)) {
            if (inputStream != null) {
                String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                response.getWriter().write(content);
                logger.info("Served index.html content on-demand ({} characters)", content.length());
            } else {
                logger.error("Index file not found during on-demand load: {}", indexPath);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "React app not found");
            }
        }
    }

    private boolean isStaticAsset(String path) {
        // Check for static asset patterns
        boolean isStatic = path.startsWith("/static/") ||
                path.endsWith(".js") ||
                path.endsWith(".css") ||
                path.endsWith(".ico") ||
                path.endsWith(".png") ||
                path.endsWith(".jpg") ||
                path.endsWith(".jpeg") ||
                path.endsWith(".gif") ||
                path.endsWith(".svg") ||
                path.endsWith(".woff") ||
                path.endsWith(".woff2") ||
                path.endsWith(".ttf") ||
                path.endsWith(".eot") ||
                path.endsWith(".json") ||
                path.endsWith(".xml") ||
                path.endsWith(".txt");

        logger.debug("Path '{}' is static asset: {}", path, isStatic);
        return isStatic;
    }
}
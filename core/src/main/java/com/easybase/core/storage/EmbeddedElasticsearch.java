package com.easybase.core.storage;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.concurrent.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Manages an embedded Elasticsearch instance.
 * Implements Builder pattern for configuration and Observer pattern for progress tracking.
 */
public class EmbeddedElasticsearch {
    private static final Logger logger = LoggerFactory.getLogger(EmbeddedElasticsearch.class);

    // Default settings
    private static final int DEFAULT_PORT = 9200;
    private static final String DEFAULT_DATA_DIR = "./data/elasticsearch";
    private static final String DEFAULT_DOWNLOAD_DIR = "./downloads";
    private static final int DEFAULT_BUFFER_SIZE = 16384; // 16KB buffer
    private static final long DEFAULT_DOWNLOAD_TIMEOUT = 30; // 30 minutes

    private final String version;
    private final int port;
    private final Path dataDirectory;
    private final Path downloadDirectory;
    private final boolean cleanInstall;
    private final int bufferSize;
    private final long downloadTimeoutMinutes;
    private final ExecutorService executorService;

    private volatile Process process;
    private final DownloadProgressListener progressListener;

    private EmbeddedElasticsearch(Builder builder) {
        this.version = builder.version;
        this.port = builder.port;
        this.dataDirectory = builder.dataDirectory;
        this.downloadDirectory = builder.downloadDirectory;
        this.cleanInstall = builder.cleanInstall;
        this.bufferSize = builder.bufferSize;
        this.downloadTimeoutMinutes = builder.downloadTimeoutMinutes;
        this.progressListener = builder.progressListener;
        this.executorService = Executors.newFixedThreadPool(3); // Thread pool for parallel operations
    }

    /**
     * Builder for EmbeddedElasticsearch.
     */
    public static class Builder {
        private final String version;
        private int port = DEFAULT_PORT;
        private Path dataDirectory = Paths.get(DEFAULT_DATA_DIR);
        private Path downloadDirectory = Paths.get(DEFAULT_DOWNLOAD_DIR);
        private boolean cleanInstall = false;
        private int bufferSize = DEFAULT_BUFFER_SIZE;
        private long downloadTimeoutMinutes = DEFAULT_DOWNLOAD_TIMEOUT;
        private DownloadProgressListener progressListener;

        public Builder(String version) {
            this.version = version;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder dataDirectory(Path dataDirectory) {
            this.dataDirectory = dataDirectory;
            return this;
        }

        public Builder downloadDirectory(Path downloadDirectory) {
            this.downloadDirectory = downloadDirectory;
            return this;
        }

        public Builder cleanInstall(boolean cleanInstall) {
            this.cleanInstall = cleanInstall;
            return this;
        }

        public Builder bufferSize(int bufferSize) {
            this.bufferSize = bufferSize;
            return this;
        }

        public Builder downloadTimeoutMinutes(long downloadTimeoutMinutes) {
            this.downloadTimeoutMinutes = downloadTimeoutMinutes;
            return this;
        }

        public Builder progressListener(DownloadProgressListener progressListener) {
            this.progressListener = progressListener;
            return this;
        }

        public EmbeddedElasticsearch build() {
            return new EmbeddedElasticsearch(this);
        }
    }

    /**
     * Interface for tracking download progress (Observer pattern).
     */
    public interface DownloadProgressListener {
        void onProgress(long bytesRead, long totalBytes, int percentage);
        void onComplete(long totalBytes);
        void onError(Exception e);
    }

    /**
     * Starts the embedded Elasticsearch instance.
     * @return Future that completes when Elasticsearch is ready
     */
    public CompletableFuture<Void> start() {
        if (process != null && process.isAlive()) {
            logger.info("Embedded Elasticsearch is already running");
            return CompletableFuture.completedFuture(null);
        }

        return CompletableFuture.runAsync(() -> {
            try {
                // Create necessary directories
                Files.createDirectories(dataDirectory);
                Files.createDirectories(downloadDirectory);

                // Use strategy pattern for download and extraction
                Path elasticsearchHome = _downloadAndExtractElasticsearch();

                // Build the command to start Elasticsearch using the Template Method pattern
                ProcessBuilder processBuilder = _buildProcessCommand(elasticsearchHome);

                logger.info("Starting embedded Elasticsearch with version {}", version);
                process = processBuilder.start();

                // Use executor service to handle output logging
                Future<?> loggerFuture = executorService.submit(() -> _handleProcessOutput(process));

                // Wait for Elasticsearch to start
                boolean started = _waitForElasticsearchToStart();

                if (!started) {
                    if (process.isAlive()) {
                        process.destroy();
                    }
                    throw new RuntimeException("Elasticsearch failed to start within the timeout period");
                }

                logger.info("Embedded Elasticsearch started on port {}", port);

            } catch (Exception e) {
                logger.error("Failed to start embedded Elasticsearch", e);
                if (progressListener != null) {
                    progressListener.onError(e);
                }
                throw new CompletionException(e);
            }
        }, executorService);
    }

    /**
     * Stops the embedded Elasticsearch instance.
     */
    public void stop() {
        if (process != null && process.isAlive()) {
            logger.info("Stopping embedded Elasticsearch");
            process.destroy();

            try {
                // Give it some time to shut down gracefully
                if (!process.waitFor(30, TimeUnit.SECONDS)) {
                    logger.warn("Embedded Elasticsearch didn't shut down gracefully, forcing termination");
                    process.destroyForcibly();
                }
            } catch (InterruptedException e) {
                logger.error("Interrupted while waiting for Elasticsearch to stop", e);
                Thread.currentThread().interrupt();
            }

            logger.info("Embedded Elasticsearch stopped");
        }

        // Shutdown executor service
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public int getPort() {
        return port;
    }

    /**
     * Strategy for handling process output.
     */
    private void _handleProcessOutput(Process process) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                logger.debug("ES: {}", line);
            }
        } catch (IOException e) {
            logger.error("Error reading Elasticsearch output", e);
        }
    }

    /**
     * Strategy for building process command.
     */
    private ProcessBuilder _buildProcessCommand(Path elasticsearchHome) {
        ProcessBuilder processBuilder = new ProcessBuilder();

        // Determine the executable based on OS
        String executable = System.getProperty("os.name").toLowerCase().contains("windows")
                ? "elasticsearch.bat" : "elasticsearch";
        Path elasticsearchExe = elasticsearchHome.resolve("bin").resolve(executable);

        // Set Elasticsearch options
        processBuilder.command(
                elasticsearchExe.toString(),
                "-Ecluster.name=easybase-embedded",
                "-Ehttp.port=" + port,
                "-Epath.data=" + dataDirectory.toAbsolutePath(),
                "-Expack.security.enabled=false",
                "-Ediscovery.type=single-node"
        );

        // Redirect output to our logs
        processBuilder.redirectErrorStream(true);

        return processBuilder;
    }

    /**
     * Downloads and extracts Elasticsearch using the appropriate strategy.
     * Uses Strategy pattern to handle different archive types.
     */
    private Path _downloadAndExtractElasticsearch() throws IOException {
        // Create strategy context for OS-specific operations
        ArchiveStrategy archiveStrategy = _createArchiveStrategy();

        String fileName = archiveStrategy.getFileName(version);
        String downloadUrl = archiveStrategy.getDownloadUrl(fileName);

        // Path to downloaded file
        Path downloadedFile = downloadDirectory.resolve(fileName);

        // Path to extracted directory
        String extractedDirName = "elasticsearch-" + version;
        Path extractedDir = downloadDirectory.resolve(extractedDirName);

        // Clean install if requested
        if (cleanInstall && Files.exists(extractedDir)) {
            FileUtils.deleteDirectory(extractedDir.toFile());
        }

        // Check if already extracted
        if (Files.exists(extractedDir) && archiveStrategy.isValidInstallation(extractedDir)) {
            logger.info("Elasticsearch {} already downloaded and extracted", version);
            return extractedDir;
        }

        // Using CompletableFuture for asynchronous download
        CompletableFuture<Path> downloadFuture;

        // Check if already downloaded
        if (!Files.exists(downloadedFile)) {
            logger.info("Downloading Elasticsearch {} from {}", version, downloadUrl);
            Path finalDownloadedFile = downloadedFile;
            downloadFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    _downloadFile(downloadUrl, finalDownloadedFile);
                    return finalDownloadedFile;
                } catch (IOException e) {
                    throw new CompletionException(e);
                }
            }, executorService);
        } else {
            logger.info("Elasticsearch {} already downloaded", version);
            downloadFuture = CompletableFuture.completedFuture(downloadedFile);
        }

        // Wait for download to complete with timeout
        try {
            downloadedFile = downloadFuture.get(downloadTimeoutMinutes, TimeUnit.MINUTES);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new IOException("Download failed: " + e.getMessage(), e);
        }

        // Extract the file
        logger.info("Extracting Elasticsearch to {}", extractedDir);

        // Delete existing extraction directory if it exists
        if (Files.exists(extractedDir)) {
            FileUtils.deleteDirectory(extractedDir.toFile());
        }

        // Create extraction directory
        Files.createDirectories(extractedDir);

        // Extract using the appropriate strategy
        archiveStrategy.extract(downloadedFile, extractedDir, bufferSize);

        logger.info("Elasticsearch {} extracted successfully", version);
        return extractedDir;
    }

    /**
     * Factory method to create the appropriate archive strategy.
     */
    private ArchiveStrategy _createArchiveStrategy() {
        String osName = System.getProperty("os.name").toLowerCase();
        String osArch = System.getProperty("os.arch").toLowerCase();

        if (osName.contains("windows")) {
            return new WindowsArchiveStrategy(osArch);
        } else {
            return new LinuxArchiveStrategy(osArch);
        }
    }

    /**
     * Strategy interface for handling different archive types.
     */
    private interface ArchiveStrategy {
        String getFileName(String version);
        String getDownloadUrl(String fileName);
        void extract(Path archiveFile, Path targetDir, int bufferSize) throws IOException;
        boolean isValidInstallation(Path installationDir);
    }

    /**
     * Strategy for Windows (zip files).
     */
    private class WindowsArchiveStrategy implements ArchiveStrategy {
        private final String architecture;

        public WindowsArchiveStrategy(String osArch) {
            if (osArch.contains("aarch64") || osArch.contains("arm64")) {
                this.architecture = "aarch64";
            } else {
                this.architecture = "x86_64";
            }
        }

        @Override
        public String getFileName(String version) {
            // For versions 8.x, Elasticsearch uses a different naming scheme
            if (version.startsWith("8.")) {
                return String.format("elasticsearch-%s-windows-%s.zip", version, architecture);
            } else {
                return String.format("elasticsearch-%s.zip", version);
            }
        }

        @Override
        public String getDownloadUrl(String fileName) {
            return String.format("https://artifacts.elastic.co/downloads/elasticsearch/%s", fileName);
        }

        @Override
        public void extract(Path archiveFile, Path targetDir, int bufferSize) throws IOException {
            try (ZipFile zip = new ZipFile(archiveFile.toFile())) {
                // Use parallel processing for extraction
                CountDownLatch latch = new CountDownLatch(zip.size());

                // Get the root directory name in the zip file
                String rootDirName = _getRootDirName(zip);

                // First create all directories to avoid race conditions
                Enumeration<? extends ZipEntry> entries = zip.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    String name = _getAdjustedEntryName(entry.getName(), rootDirName);

                    if (entry.isDirectory()) {
                        Path entryPath = targetDir.resolve(name);
                        Files.createDirectories(entryPath);
                    }
                }

                // Now extract all files using a custom thread pool
                entries = zip.entries();
                BlockingQueue<ZipEntry> fileEntries = new LinkedBlockingQueue<>();

                // Fill the queue with file entries
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    if (!entry.isDirectory()) {
                        fileEntries.add(entry);
                    } else {
                        latch.countDown(); // Count down for directory entries
                    }
                }

                // Process files in parallel
                int nThreads = Math.min(Runtime.getRuntime().availableProcessors(), 4);
                for (int i = 0; i < nThreads; i++) {
                    executorService.submit(() -> {
                        try {
                            ZipEntry entry;
                            while ((entry = fileEntries.poll()) != null) {
                                try {
                                    String name = _getAdjustedEntryName(entry.getName(), rootDirName);
                                    Path entryPath = targetDir.resolve(name);

                                    // Ensure parent directories exist
                                    Files.createDirectories(entryPath.getParent());

                                    // Extract file
                                    try (InputStream in = zip.getInputStream(entry);
                                         OutputStream out = new BufferedOutputStream(
                                                 Files.newOutputStream(entryPath), bufferSize)) {
                                        byte[] buffer = new byte[bufferSize];
                                        int bytesRead;
                                        while ((bytesRead = in.read(buffer)) != -1) {
                                            out.write(buffer, 0, bytesRead);
                                        }
                                        out.flush();
                                    }

                                    // Set executable permission for bin files
                                    if (name.startsWith("bin/") && !name.endsWith(".bat") &&
                                            !name.endsWith(".txt")) {
                                        entryPath.toFile().setExecutable(true);
                                    }
                                } finally {
                                    latch.countDown();
                                }
                            }
                        } catch (Exception e) {
                            logger.error("Error extracting zip entry", e);
                        }
                    });
                }

                // Wait for all extractions to complete
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Extraction interrupted", e);
                }
            }
        }

        @Override
        public boolean isValidInstallation(Path installationDir) {
            return Files.exists(installationDir.resolve("bin/elasticsearch.bat"));
        }

        private String _getRootDirName(ZipFile zip) {
            String rootDirName = null;
            Enumeration<? extends ZipEntry> entries = zip.entries();
            if (entries.hasMoreElements()) {
                ZipEntry firstEntry = entries.nextElement();
                String name = firstEntry.getName();
                // Get the root directory name
                int firstSlash = name.indexOf('/');
                if (firstSlash > 0) {
                    rootDirName = name.substring(0, firstSlash);
                }
            }
            return rootDirName;
        }

        private String _getAdjustedEntryName(String entryName, String rootDirName) {
            // Skip the root directory to extract directly to targetDir
            if (rootDirName != null && entryName.startsWith(rootDirName)) {
                String name = entryName.substring(rootDirName.length());
                if (name.isEmpty() || name.equals("/")) {
                    return "";
                }
                if (name.startsWith("/")) {
                    name = name.substring(1);
                }
                return name;
            }
            return entryName;
        }
    }

    /**
     * Strategy for Linux/macOS (tar.gz files).
     */
    private static class LinuxArchiveStrategy implements ArchiveStrategy {
        private final String architecture;

        public LinuxArchiveStrategy(String osArch) {
            if (osArch.contains("aarch64") || osArch.contains("arm64")) {
                this.architecture = "aarch64";
            } else {
                this.architecture = "x86_64";
            }
        }

        @Override
        public String getFileName(String version) {
            // For versions 8.x, Elasticsearch uses a different naming scheme
            if (version.startsWith("8.")) {
                return String.format("elasticsearch-%s-linux-%s.tar.gz", version, architecture);
            } else {
                return String.format("elasticsearch-%s.tar.gz", version);
            }
        }

        @Override
        public String getDownloadUrl(String fileName) {
            return String.format("https://artifacts.elastic.co/downloads/elasticsearch/%s", fileName);
        }

        @Override
        public void extract(Path archiveFile, Path targetDir, int bufferSize) throws IOException {
            // First pass to get root directory name
            String rootDirName = null;
            try (InputStream fileIn = Files.newInputStream(archiveFile);
                 BufferedInputStream bufferedIn = new BufferedInputStream(fileIn, bufferSize);
                 GzipCompressorInputStream gzipIn = new GzipCompressorInputStream(bufferedIn);
                 TarArchiveInputStream tarIn = new TarArchiveInputStream(gzipIn)) {

                TarArchiveEntry entry = tarIn.getNextTarEntry();
                if (entry != null) {
                    String name = entry.getName();
                    int firstSlash = name.indexOf('/');
                    if (firstSlash > 0) {
                        rootDirName = name.substring(0, firstSlash);
                    }
                }
            }

            // Second pass to extract files
            try (InputStream fileIn = Files.newInputStream(archiveFile);
                 BufferedInputStream bufferedIn = new BufferedInputStream(fileIn, bufferSize);
                 GzipCompressorInputStream gzipIn = new GzipCompressorInputStream(bufferedIn);
                 TarArchiveInputStream tarIn = new TarArchiveInputStream(gzipIn)) {

                TarArchiveEntry entry;
                while ((entry = tarIn.getNextTarEntry()) != null) {
                    String name = entry.getName();

                    // Skip the root directory to extract directly to targetDir
                    if (rootDirName != null && name.startsWith(rootDirName)) {
                        name = name.substring(rootDirName.length());
                        if (name.isEmpty() || name.equals("/")) {
                            continue;
                        }
                        if (name.startsWith("/")) {
                            name = name.substring(1);
                        }
                    }

                    Path entryPath = targetDir.resolve(name);

                    if (entry.isDirectory()) {
                        Files.createDirectories(entryPath);
                    } else {
                        // Create parent directories if they don't exist
                        Files.createDirectories(entryPath.getParent());

                        // Extract file
                        try (OutputStream out = new BufferedOutputStream(
                                Files.newOutputStream(entryPath), bufferSize)) {
                            byte[] buffer = new byte[bufferSize];
                            int bytesRead;
                            while ((bytesRead = tarIn.read(buffer)) != -1) {
                                out.write(buffer, 0, bytesRead);
                            }
                            out.flush();
                        }

                        // Set executable permission for bin files
                        if (name.startsWith("bin/") && !name.endsWith(".bat") && !name.endsWith(".txt")) {
                            entryPath.toFile().setExecutable(true);
                        }
                    }
                }
            }
        }

        @Override
        public boolean isValidInstallation(Path installationDir) {
            return Files.exists(installationDir.resolve("bin/elasticsearch"));
        }
    }

    /**
     * Downloads a file from a URL with progress reporting.
     */
    private void _downloadFile(String fileUrl, Path targetPath) throws IOException {
        URL url = new URL(fileUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(60000);

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Failed to download Elasticsearch: HTTP error code " + responseCode);
        }

        int contentLength = connection.getContentLength();

        try (InputStream in = new BufferedInputStream(connection.getInputStream(), bufferSize);
             OutputStream out = new BufferedOutputStream(Files.newOutputStream(targetPath), bufferSize)) {

            byte[] buffer = new byte[bufferSize];
            long totalBytesRead = 0;
            int bytesRead;
            long lastLogTime = System.currentTimeMillis();

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;

                // Report progress
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastLogTime > 2000) {
                    if (contentLength > 0) {
                        int progress = (int) (totalBytesRead * 100 / contentLength);
                        logger.info("Download progress: {}% ({} / {} bytes)",
                                progress, totalBytesRead, contentLength);

                        if (progressListener != null) {
                            progressListener.onProgress(totalBytesRead, contentLength, progress);
                        }
                    } else {
                        logger.info("Download progress: {} bytes", totalBytesRead);

                        if (progressListener != null) {
                            progressListener.onProgress(totalBytesRead, -1, -1);
                        }
                    }
                    lastLogTime = currentTime;
                }
            }

            out.flush();
            logger.info("Download completed: {} bytes", totalBytesRead);

            if (progressListener != null) {
                progressListener.onComplete(totalBytesRead);
            }
        } catch (IOException e) {
            if (progressListener != null) {
                progressListener.onError(e);
            }
            throw e;
        }
    }

    /**
     * Waits for Elasticsearch to be ready to accept connections.
     */
    private boolean _waitForElasticsearchToStart() {
        logger.info("Waiting for Elasticsearch to start...");

        // Use exponential backoff for connection attempts
        long initialDelay = 100; // milliseconds
        long maxDelay = 5000; // 5 seconds
        long delay = initialDelay;
        long timeout = TimeUnit.MINUTES.toMillis(2); // 2 minute timeout
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < timeout) {
            try {
                URL url = new URL("http://localhost:" + port);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout((int) Math.min(delay, 5000));
                connection.setReadTimeout((int) Math.min(delay, 5000));

                int responseCode = connection.getResponseCode();
                if (responseCode >= 200 && responseCode < 300) {
                    logger.info("Elasticsearch is ready");
                    return true;
                }
            } catch (IOException e) {
                // Connection failed, Elasticsearch probably not ready yet
            }

            try {
                Thread.sleep(delay);
                delay = Math.min(delay * 2, maxDelay); // Exponential backoff
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }

        logger.error("Elasticsearch failed to start within the timeout period");
        return false;
    }
}
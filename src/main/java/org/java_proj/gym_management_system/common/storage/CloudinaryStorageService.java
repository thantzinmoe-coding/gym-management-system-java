package org.java_proj.gym_management_system.common.storage;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.Set;

/**
 * Stores pictures on Cloudinary (free tier) so they survive redeploys and have a public https link.
 * Used when CLOUDINARY_CLOUD_NAME, CLOUDINARY_API_KEY and CLOUDINARY_API_SECRET are set.
 * Talks to Cloudinary's signed-upload REST API directly, so no extra library is needed.
 */
@Service("cloudinaryStorageService")
public class CloudinaryStorageService implements StorageService {

    private static final Logger log = LoggerFactory.getLogger(CloudinaryStorageService.class);
    private static final Set<String> IMAGE_TYPES = Set.of("image/jpeg", "image/png", "image/webp", "image/gif");

    private final RestClient client = RestClient.create();
    private final ObjectMapper mapper = new ObjectMapper();

    private final String cloudName;
    private final String apiKey;
    private final String apiSecret;
    private final String folder;

    public CloudinaryStorageService(
            @Value("${cloudinary.cloud-name:}") String cloudName,
            @Value("${cloudinary.api-key:}") String apiKey,
            @Value("${cloudinary.api-secret:}") String apiSecret,
            @Value("${cloudinary.folder:gym}") String folder) {
        this.cloudName = cloudName;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.folder = folder;
    }

    public boolean isConfigured() {
        return !cloudName.isBlank() && !apiKey.isBlank() && !apiSecret.isBlank();
    }

    @Override
    public void init() {
        // nothing to prepare: the pictures live at Cloudinary
    }

    @Override
    public String store(MultipartFile file) {
        return storeAndGetUrl(file);
    }

    @Override
    public String storeAndGetUrl(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Failed to store empty file.");
        }
        if (!IMAGE_TYPES.contains(String.valueOf(file.getContentType()).toLowerCase())) {
            throw new IllegalArgumentException("Only JPEG, PNG, WebP or GIF images can be uploaded.");
        }
        try {
            final long timestamp = System.currentTimeMillis() / 1000;
            // Cloudinary signs the sorted parameters (without file and api_key) followed by the secret.
            final String signature = sha1("folder=" + folder + "&timestamp=" + timestamp + apiSecret);
            final String originalName = file.getOriginalFilename() == null ? "upload" : file.getOriginalFilename();

            final MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
            form.add("file", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return originalName;
                }
            });
            form.add("api_key", apiKey);
            form.add("timestamp", String.valueOf(timestamp));
            form.add("folder", folder);
            form.add("signature", signature);

            final String body = client.post()
                    .uri("https://api.cloudinary.com/v1_1/" + cloudName + "/image/upload")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(form)
                    .retrieve()
                    .body(String.class);

            final JsonNode json = mapper.readTree(body);
            final String url = json.path("secure_url").asText("");
            if (url.isBlank()) {
                throw new IllegalStateException("Cloudinary did not return a link.");
            }
            log.info("Stored picture on Cloudinary: {}", json.path("public_id").asText());
            return url;
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            log.error("Cloudinary upload failed", e);
            throw new RuntimeException("Failed to upload the picture. Please try again.", e);
        }
    }

    private static String sha1(String text) throws Exception {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-1").digest(text.getBytes(StandardCharsets.UTF_8)));
    }

    // Pictures are served by Cloudinary directly, so the /files endpoint has nothing to load.
    @Override
    public List<Path> loadAll() {
        return List.of();
    }

    @Override
    public Path load(String filename) {
        throw new UnsupportedOperationException("Pictures are stored on Cloudinary.");
    }

    @Override
    public Resource loadAsResource(String filename) {
        throw new UnsupportedOperationException("Pictures are stored on Cloudinary.");
    }

    @Override
    public void delete(String filename) {
        // old pictures are left in Cloudinary
    }

    @Override
    public void deleteAll() {
    }

    @Override
    public String update(MultipartFile newFile, String publicId, String folderName) {
        return storeAndGetUrl(newFile);
    }
}

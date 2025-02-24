package uz.pdp.ilmpay.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.pdp.ilmpay.dto.SupportLogoDTO;
import uz.pdp.ilmpay.dto.ReorderItemDTO;
import uz.pdp.ilmpay.exception.ResourceNotFoundException;
import uz.pdp.ilmpay.model.SupportLogo;
import uz.pdp.ilmpay.repository.SupportLogoRepository;
import uz.pdp.ilmpay.service.S3Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class SupportLogoService {
    private final SupportLogoRepository supportLogoRepository;
    private final S3Service s3Service;

    // S3 folder for support logos
    private static final String S3_FOLDER = "support-logos";

    @Cacheable(value = "supportLogos", key = "'allActive'") // Cache the result of this method
    public List<SupportLogoDTO> findAllActive() {
        log.info("🔍 Fetching all active support logos");
        List<SupportLogo> logos = supportLogoRepository.findByActiveTrueOrderByDisplayOrder();
        log.info("✨ Found {} active support logos", logos.size());
        return logos.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "supportLogos", allEntries = true) // Clear cache on create
    public SupportLogoDTO create(SupportLogoDTO dto) {
        log.info("🎨 Starting creation of new support logo: {}", dto.getName());
        validateLogo(dto);

        SupportLogo logo = new SupportLogo();
        logo.setName(dto.getName());

        // Handle image source priority: File > URL
        try {
            // 📸 If both file and URL are provided, file takes precedence
            if (dto.getImageFile() != null && !dto.getImageFile().isEmpty()) {
                log.debug("📤 Uploading image file for logo: {}, size: {} bytes",
                        dto.getName(), dto.getImageFile().getSize());
                log.info("🔄 File upload will take precedence over provided URL");

                String fileUrl = s3Service.uploadFile(dto.getImageFile(), S3_FOLDER);
                logo.setImageUrl(fileUrl);
                log.debug("✅ Image uploaded successfully to: {}", fileUrl);
            } else if (dto.getImageUrl() != null && !dto.getImageUrl().trim().isEmpty()) {
                // 🔗 Use URL if no file is provided
                log.debug("🔗 Using provided image URL: {}", dto.getImageUrl());
                logo.setImageUrl(dto.getImageUrl());
            } else {
                // This shouldn't happen due to validateLogo, but just in case
                log.error("❌ No image source provided for logo: {}", dto.getName());
                throw new IllegalArgumentException("Either image file or URL must be provided");
            }

            logo.setWebsiteUrl(dto.getWebsiteUrl());
            logo.setActive(true);
            logo.setDisplayOrder(dto.getDisplayOrder());
            logo.setCreatedAt(LocalDateTime.now());

            SupportLogo savedLogo = supportLogoRepository.save(logo);
            log.info("✨ Successfully created support logo: {}, id: {}", dto.getName(), savedLogo.getId());
            return toDTO(savedLogo);
        } catch (Exception e) {
            log.error("❌ Failed to create support logo: {}, error: {}", dto.getName(), e.getMessage(), e);
            throw e;
        }
    }

    @CachePut(value = "supportLogos", key = "'allActive'") // Update cache on update
    @CacheEvict(value = "supportLogos", key = "#id") // Clear individual cache entry
    public SupportLogoDTO update(Long id, SupportLogoDTO dto) {
        log.info("🔄 Starting update of support logo id: {}, name: {}", id, dto.getName());

        try {
            SupportLogo logo = supportLogoRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("❌ Support logo not found with id: {}", id);
                        return new ResourceNotFoundException("Logo not found with id: " + id);
                    });

            // For update, we don't require a new image if one already exists
            if (dto.getImageFile() == null && (dto.getImageUrl() == null || dto.getImageUrl().trim().isEmpty())) {
                log.debug("🖼️ No new image provided, keeping existing image: {}", logo.getImageUrl());
            } else {
                // Handle file update with priority: File > URL
                if (dto.getImageFile() != null && !dto.getImageFile().isEmpty()) {
                    log.debug("📤 Uploading new image file for logo: {}, size: {} bytes",
                            dto.getName(), dto.getImageFile().getSize());

                    // If updating with file, log that it takes precedence
                    if (dto.getImageUrl() != null && !dto.getImageUrl().trim().isEmpty()) {
                        log.info("🔄 New file upload will take precedence over provided URL");
                    }

                    // Delete old image if it's an S3 URL
                    if (logo.getImageUrl() != null && logo.getImageUrl().contains("s3.amazonaws.com")) {
                        log.debug("🗑️ Deleting old S3 image: {}", logo.getImageUrl());
                        s3Service.deleteFile(logo.getImageUrl());
                    }

                    // Upload new image
                    String fileUrl = s3Service.uploadFile(dto.getImageFile(), S3_FOLDER);
                    logo.setImageUrl(fileUrl);
                    log.debug("✅ New image uploaded successfully to: {}", fileUrl);
                } else if (dto.getImageUrl() != null && !dto.getImageUrl().trim().isEmpty()) {
                    // Only update URL if no file is provided
                    log.debug("🔗 Updating to new image URL: {}", dto.getImageUrl());

                    // Delete old S3 image if exists
                    if (logo.getImageUrl() != null && logo.getImageUrl().contains("s3.amazonaws.com")) {
                        log.debug("🗑️ Deleting old S3 image: {}", logo.getImageUrl());
                        s3Service.deleteFile(logo.getImageUrl());
                    }

                    logo.setImageUrl(dto.getImageUrl());
                }
            }

            // Update other fields
            logo.setName(dto.getName());
            logo.setWebsiteUrl(dto.getWebsiteUrl());
            if (dto.getDisplayOrder() != null) {
                logo.setDisplayOrder(dto.getDisplayOrder());
            }
            logo.setActive(true); // Keep it active on update

            SupportLogo savedLogo = supportLogoRepository.save(logo);
            log.info("✨ Successfully updated support logo id: {}, name: {}", id, dto.getName());
            return toDTO(savedLogo);
        } catch (Exception e) {
            log.error("❌ Failed to update support logo id: {}, name: {}, error: {}",
                    id, dto.getName(), e.getMessage(), e);
            throw e;
        }
    }

    public void delete(Long id) {
        SupportLogo logo = supportLogoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Logo not found with id: " + id));

        // Delete image from S3 if it's an S3 URL
        if (logo.getImageUrl() != null && logo.getImageUrl().contains("s3.amazonaws.com")) {
            s3Service.deleteFile(logo.getImageUrl());
        }

        logo.setActive(false);
        log.info("Soft deleting support logo with id: {}", id);
        supportLogoRepository.save(logo);
    }

    @Cacheable(value = "supportLogos", key = "#id") // Cache individual logo
    public SupportLogoDTO findById(Long id) {
        SupportLogo logo = supportLogoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Logo not found with id: " + id));
        return toDTO(logo);
    }

    @CacheEvict(value = "supportLogos", allEntries = true) // Clear cache on reorder

    @Transactional
    public List<SupportLogoDTO> reorder(List<ReorderItemDTO> reorderItems) {
        log.info("🔄 Starting reorder operation for {} items", reorderItems.size());

        try {
            // Validate input
            if (reorderItems == null || reorderItems.isEmpty()) {
                log.warn("❌ No reorder items provided");
                throw new IllegalArgumentException("No reorder items provided");
            }

            // Log incoming items
            reorderItems.forEach(item -> 
                log.debug("📦 Processing reorder item - ID: {}, New Order: {}", item.getId(), item.getDisplayOrder())
            );

            // Get all active logos that need to be updated
            List<SupportLogo> logos = supportLogoRepository.findByActiveTrueOrderByDisplayOrder();
            log.debug("📋 Found {} active logos in database", logos.size());
            
            if (logos.isEmpty()) {
                String errorMsg = "No active logos found to reorder";
                log.error("❌ {}", errorMsg);
                throw new ResourceNotFoundException(errorMsg);
            }

            // Create a map of id to order for quick lookup
            Map<Long, Integer> orderMap;
            try {
                orderMap = reorderItems.stream()
                        .collect(Collectors.toMap(
                                ReorderItemDTO::getId,
                                ReorderItemDTO::getDisplayOrder
                        ));
            } catch (IllegalStateException e) {
                String errorMsg = "Duplicate logo IDs found in request";
                log.error("❌ {}", errorMsg);
                throw new IllegalArgumentException(errorMsg);
            }

            // Validate all IDs exist
            Set<Long> existingIds = logos.stream()
                    .map(SupportLogo::getId)
                    .collect(Collectors.toSet());
            
            Set<Long> requestedIds = orderMap.keySet();
            
            if (!existingIds.containsAll(requestedIds)) {
                Set<Long> invalidIds = new HashSet<>(requestedIds);
                invalidIds.removeAll(existingIds);
                String errorMsg = String.format("Invalid logo IDs in request: %s", invalidIds);
                log.error("❌ {}", errorMsg);
                throw new IllegalArgumentException(errorMsg);
            }

            // Update orders
            logos.forEach(logo -> {
                Integer newOrder = orderMap.get(logo.getId());
                if (newOrder != null) {
                    log.debug("🔄 Updating logo {} order from {} to {}", 
                        logo.getId(), logo.getDisplayOrder(), newOrder);
                    logo.setDisplayOrder(newOrder);
                }
            });

            try {
                List<SupportLogo> updatedLogos = supportLogoRepository.saveAll(logos);
                log.info("✨ Successfully updated orders for {} logos", updatedLogos.size());
                return updatedLogos.stream()
                        .map(this::toDTO)
                        .collect(Collectors.toList());
            } catch (Exception e) {
                String errorMsg = "Failed to save updated logo orders";
                log.error("❌ {} - Error: {}", errorMsg, e.getMessage());
                throw new RuntimeException(errorMsg, e);
            }
        } catch (Exception e) {
            log.error("💥 Error during reorder operation: {}", e.getMessage(), e);
            throw e;
        }
    }

    @CacheEvict(value = "supportLogos", allEntries = true) // Clear cache on delete

    private void validateLogo(SupportLogoDTO dto) {
        // For create, require either image file or URL
        if (!dto.getImageFile().isEmpty() && dto.getImageFile() == null
                && (dto.getImageUrl() == null || dto.getImageUrl().trim().isEmpty())) {
            throw new IllegalArgumentException("Either image file or URL must be provided");
        }
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
    }

    private SupportLogoDTO toDTO(SupportLogo logo) {
        return new SupportLogoDTO(
                logo.getId(),
                logo.getName(),
                logo.getImageUrl(),
                logo.getWebsiteUrl(),
                logo.isActive(),
                logo.getDisplayOrder());
    }

}

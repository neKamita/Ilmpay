package uz.pdp.ilmpay.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.pdp.ilmpay.dto.SupportLogoDTO;
import uz.pdp.ilmpay.exception.ResourceNotFoundException;
import uz.pdp.ilmpay.model.SupportLogo;
import uz.pdp.ilmpay.repository.SupportLogoRepository;
import uz.pdp.ilmpay.service.S3Service;

import java.time.LocalDateTime;
import java.util.List;
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

    public List<SupportLogoDTO> findAllActive() {
        return supportLogoRepository.findByActiveTrueOrderByOrder()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

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
            logo.setOrder(dto.getOrder());
            logo.setCreatedAt(LocalDateTime.now());

            SupportLogo savedLogo = supportLogoRepository.save(logo);
            log.info("✨ Successfully created support logo: {}, id: {}", dto.getName(), savedLogo.getId());
            return toDTO(savedLogo);
        } catch (Exception e) {
            log.error("❌ Failed to create support logo: {}, error: {}", dto.getName(), e.getMessage(), e);
            throw e;
        }
    }

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
            if (dto.getOrder() != null) {
                logo.setOrder(dto.getOrder());
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

    public SupportLogoDTO findById(Long id) {
        SupportLogo logo = supportLogoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Logo not found with id: " + id));
        return toDTO(logo);
    }

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
                logo.getOrder());
    }

    public List<SupportLogo> getAllActive() {
        return supportLogoRepository.findByActiveTrue();
    }
}
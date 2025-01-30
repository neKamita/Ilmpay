package uz.pdp.ilmpay.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.pdp.ilmpay.dto.SupportLogoDTO;
import uz.pdp.ilmpay.exception.ResourceNotFoundException;
import uz.pdp.ilmpay.model.SupportLogo;
import uz.pdp.ilmpay.repository.SupportLogoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class SupportLogoService {
    private final SupportLogoRepository supportLogoRepository;

    public List<SupportLogoDTO> findAllActive() {
        return supportLogoRepository.findByActiveTrue()
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    public SupportLogoDTO create(SupportLogoDTO dto) {
        validateLogo(dto);
        
        SupportLogo logo = new SupportLogo();
        logo.setName(dto.getName());
        logo.setImageUrl(dto.getImageUrl());
        logo.setWebsiteUrl(dto.getWebsiteUrl());
        logo.setActive(true);
        logo.setCreatedAt(LocalDateTime.now());

        log.info("Creating new support logo: {}", dto.getName());
        return toDTO(supportLogoRepository.save(logo));
    }

    public SupportLogoDTO update(Long id, SupportLogoDTO dto) {
        validateLogo(dto);
        
        SupportLogo logo = supportLogoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Logo not found with id: " + id));

        logo.setName(dto.getName());
        logo.setImageUrl(dto.getImageUrl());
        logo.setWebsiteUrl(dto.getWebsiteUrl());
        logo.setActive(dto.isActive());

        log.info("Updating support logo with id: {}", id);
        return toDTO(supportLogoRepository.save(logo));
    }

    public void delete(Long id) {
        SupportLogo logo = supportLogoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Logo not found with id: " + id));
            
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
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (dto.getImageUrl() == null || dto.getImageUrl().trim().isEmpty()) {
            throw new IllegalArgumentException("Image URL is required");
        }
    }

    private SupportLogoDTO toDTO(SupportLogo logo) {
        return new SupportLogoDTO(
            logo.getId(),
            logo.getName(),
            logo.getImageUrl(),
            logo.getWebsiteUrl(),
            logo.isActive()
        );
    }

    public List<SupportLogo> getAllActive() {
        return supportLogoRepository.findByActiveTrue();
    }
} 
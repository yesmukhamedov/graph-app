package com.example.graph.service.phone;

import com.example.graph.model.NodeEntity;
import com.example.graph.model.phone.PhoneEntity;
import com.example.graph.model.phone.PhonePatternEntity;
import com.example.graph.model.phone.PhoneValueEntity;
import com.example.graph.repository.NodeRepository;
import com.example.graph.repository.PhonePatternRepository;
import com.example.graph.repository.PhoneRepository;
import com.example.graph.service.value.NodeValueService;
import com.example.graph.validate.PhoneDigitsValidator;
import com.example.graph.validate.ValidationException;
import com.example.graph.web.dto.PhoneDto;
import com.example.graph.web.dto.PhonePatternDto;
import com.example.graph.web.dto.PhoneValueDto;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PhoneService {
    private final PhoneRepository phoneRepository;
    private final PhonePatternRepository phonePatternRepository;
    private final NodeRepository nodeRepository;
    private final PhoneValueService phoneValueService;
    private final NodeValueService nodeValueService;
    private final PhoneDigitsValidator phoneDigitsValidator;

    public PhoneService(PhoneRepository phoneRepository,
                        PhonePatternRepository phonePatternRepository,
                        NodeRepository nodeRepository,
                        PhoneValueService phoneValueService,
                        NodeValueService nodeValueService,
                        PhoneDigitsValidator phoneDigitsValidator) {
        this.phoneRepository = phoneRepository;
        this.phonePatternRepository = phonePatternRepository;
        this.nodeRepository = nodeRepository;
        this.phoneValueService = phoneValueService;
        this.nodeValueService = nodeValueService;
        this.phoneDigitsValidator = phoneDigitsValidator;
    }

    public PhoneEntity createPhone(Long nodeId, Long patternId, String digits) {
        if (nodeId == null) {
            throw new IllegalArgumentException("Node is required.");
        }
        if (patternId == null) {
            throw new IllegalArgumentException("Pattern is required.");
        }
        if (digits == null || digits.isBlank()) {
            throw new IllegalArgumentException("Digits are required.");
        }
        String normalizedDigits = digits.trim();
        NodeEntity node = nodeRepository.findById(nodeId)
            .orElseThrow(() -> new IllegalArgumentException("Node not found."));
        PhonePatternEntity pattern = phonePatternRepository.findById(patternId)
            .orElseThrow(() -> new IllegalArgumentException("Pattern not found."));
        if (phoneRepository.existsByNodeId(nodeId)) {
            throw new IllegalArgumentException("Selected node already has a phone.");
        }
        if (phoneValueService.existsByValue(normalizedDigits)) {
            throw new IllegalArgumentException("Phone value already exists.");
        }
        try {
            phoneDigitsValidator.validateDigitsAgainstPattern(normalizedDigits, pattern);
        } catch (ValidationException ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
        PhoneEntity phone = new PhoneEntity();
        phone.setNode(node);
        PhoneEntity savedPhone = phoneRepository.save(phone);
        phoneValueService.createCurrentValue(savedPhone, pattern, normalizedDigits, java.time.OffsetDateTime.now());
        return savedPhone;
    }

    public void deletePhone(Long id) {
        phoneRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<PhoneDto> listPhonesDto() {
        java.time.OffsetDateTime now = java.time.OffsetDateTime.now();
        Map<Long, String> nodeNames = nodeValueService.getCurrentValues(now);
        Map<Long, PhoneValueEntity> phoneValues = phoneValueService.getCurrentValues(now);
        return phoneRepository.findAll().stream()
            .map(phone -> new PhoneDto(
                phone.getId(),
                nodeNames.getOrDefault(phone.getNode().getId(), "—"),
                resolveCurrentValue(phoneValues.get(phone.getId()))
            ))
            .toList();
    }

    @Transactional(readOnly = true)
    public List<PhonePatternDto> listPatternsDto() {
        return phonePatternRepository.findAll().stream()
            .map(pattern -> new PhonePatternDto(pattern.getId(), pattern.getCode(), pattern.getValue()))
            .sorted(Comparator.comparing(PhonePatternDto::getCode, String.CASE_INSENSITIVE_ORDER))
            .toList();
    }

    private PhoneValueDto resolveCurrentValue(PhoneValueEntity value) {
        if (value == null) {
            return new PhoneValueDto(null, null, new PhonePatternDto(null, "—", null));
        }
        PhonePatternDto pattern = value.getPattern() == null
            ? new PhonePatternDto(null, "—", null)
            : new PhonePatternDto(value.getPattern().getId(), value.getPattern().getCode(),
            value.getPattern().getValue());
        String displayValue = PhoneFormatUtils.formatPhone(pattern.getValue(), value.getValue());
        if (displayValue == null) {
            displayValue = value.getValue();
        }
        return new PhoneValueDto(value.getValue(), displayValue, pattern);
    }
}

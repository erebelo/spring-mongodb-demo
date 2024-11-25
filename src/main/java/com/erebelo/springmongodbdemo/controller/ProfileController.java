package com.erebelo.springmongodbdemo.controller;

import static com.erebelo.springmongodbdemo.constant.BusinessConstant.MERGE_PATCH_MEDIA_TYPE;
import static com.erebelo.springmongodbdemo.constant.BusinessConstant.PROFILES;

import com.erebelo.springmongodbdemo.context.interceptor.HeaderLoggedInUser;
import com.erebelo.springmongodbdemo.context.resolver.UserId;
import com.erebelo.springmongodbdemo.domain.request.ProfileRequest;
import com.erebelo.springmongodbdemo.domain.response.ProfileResponse;
import com.erebelo.springmongodbdemo.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Log4j2
@RestController
@RequestMapping(PROFILES)
@HeaderLoggedInUser
@RequiredArgsConstructor
@Tag(name = "Profiles API")
public class ProfileController {

    private final ProfileService service;

    @Operation(summary = "GET Profiles")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProfileResponse> getProfile(@UserId String userId) {
        log.info("GET {}", PROFILES);
        return ResponseEntity.ok(service.getProfile(userId));
    }

    @Operation(summary = "POST Profiles")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProfileResponse> insertProfile(@UserId String userId,
            @Valid @RequestBody ProfileRequest profileRequest) {
        log.info("POST {} - userId={}", PROFILES, userId);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().buildAndExpand().toUri())
                .body(service.insertProfile(userId, profileRequest));
    }

    @Operation(summary = "PUT Profiles")
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProfileResponse> updateProfile(@UserId String userId,
            @Valid @RequestBody ProfileRequest profileRequest) {
        log.info("PUT {} - userId={}", PROFILES, userId);
        return ResponseEntity.ok(service.updateProfile(userId, profileRequest));
    }

    @Operation(summary = "PATCH Profiles")
    @PatchMapping(consumes = MERGE_PATCH_MEDIA_TYPE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProfileResponse> patchProfile(@UserId String userId,
            @Valid @RequestBody Map<String, Object> profileRequestMap) {
        log.info("PATCH {} - userId={}", PROFILES, userId);
        return ResponseEntity.ok(service.patchProfile(userId, profileRequestMap));
    }

    @Operation(summary = "DELETE Profiles")
    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteProfile(@UserId String userId) {
        log.info("DELETE {} - userId={}", PROFILES, userId);
        service.deleteProfile(userId);

        return ResponseEntity.noContent().build();
    }
}

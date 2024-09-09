package com.erebelo.springmongodbdemo.controller;

import com.erebelo.springmongodbdemo.context.interceptor.HeaderLoggedInUser;
import com.erebelo.springmongodbdemo.context.resolver.UserId;
import com.erebelo.springmongodbdemo.domain.request.ProfileRequest;
import com.erebelo.springmongodbdemo.domain.response.ProfileResponse;
import com.erebelo.springmongodbdemo.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

import java.util.Map;

import static com.erebelo.springmongodbdemo.constant.BusinessConstant.MERGE_PATCH_MEDIA_TYPE;
import static com.erebelo.springmongodbdemo.constant.BusinessConstant.PROFILE;

@Log4j2
@RestController
@RequestMapping(PROFILE)
@HeaderLoggedInUser
@RequiredArgsConstructor
@Tag(name = "Profile API")
public class ProfileController {

    private final ProfileService service;

    private static final String RESPONSE_BODY_LOGGER = "Response body: {}";

    @Operation(summary = "GET Profile")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProfileResponse> getProfile(@UserId String userId) {
        log.info("Getting profile");
        var response = service.getProfile(userId);

        log.info(RESPONSE_BODY_LOGGER, response);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "POST Profile")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProfileResponse> insertProfile(@UserId String userId, @Valid @RequestBody ProfileRequest profileRequest) {
        log.info("Inserting profile");
        var response = service.insertProfile(userId, profileRequest);

        log.info(RESPONSE_BODY_LOGGER, response);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().buildAndExpand().toUri()).body(response);
    }

    @Operation(summary = "PUT Profile")
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProfileResponse> updateProfile(@UserId String userId, @Valid @RequestBody ProfileRequest profileRequest) {
        log.info("Updating profile");
        var response = service.updateProfile(userId, profileRequest);

        log.info(RESPONSE_BODY_LOGGER, response);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "PATCH Profile")
    @PatchMapping(consumes = MERGE_PATCH_MEDIA_TYPE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProfileResponse> patchProfile(@UserId String userId, @Valid @RequestBody Map<String, Object> profileRequestMap) {
        log.info("Patching profile");
        var response = service.patchProfile(userId, profileRequestMap);

        log.info(RESPONSE_BODY_LOGGER, response);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "DELETE Profile")
    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteProfile(@UserId String userId) {
        log.info("Deleting profile");
        service.deleteProfile(userId);

        return ResponseEntity.noContent().build();
    }
}

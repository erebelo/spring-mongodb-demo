package com.erebelo.springmongodbdemo.controller;

import com.erebelo.springmongodbdemo.context.interceptor.HeaderLoggedInUser;
import com.erebelo.springmongodbdemo.context.resolver.UserId;
import com.erebelo.springmongodbdemo.domain.request.ProfileRequest;
import com.erebelo.springmongodbdemo.domain.response.ProfileResponse;
import com.erebelo.springmongodbdemo.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;

import static com.erebelo.springmongodbdemo.constants.BusinessConstants.MERGE_PATCH_MEDIA_TYPE;
import static com.erebelo.springmongodbdemo.constants.BusinessConstants.PROFILE;

@Validated
@RestController
@RequestMapping(PROFILE)
@HeaderLoggedInUser
@RequiredArgsConstructor
@Tag(name = "Profile API")
public class ProfileController {

    private final ProfileService service;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileController.class);
    private static final String PROFILE_RESPONSE = "Profile response: {}";

    @Operation(summary = "Get profile")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProfileResponse> getProfileByUserId(@UserId String userId) {
        LOGGER.info("Getting profile by userId");
        var response = service.getProfileByUserId(userId);

        LOGGER.info(PROFILE_RESPONSE, response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Insert profile")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProfileResponse> insertProfile(@UserId String userId, @Valid @RequestBody ProfileRequest profileRequest) {
        LOGGER.info("Inserting profile - Request body: {}", profileRequest);
        var response = service.insertProfile(userId, profileRequest);

        LOGGER.info(PROFILE_RESPONSE, response);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Update profile")
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProfileResponse> updateProfile(@UserId String userId, @Valid @RequestBody ProfileRequest profileRequest) {
        LOGGER.info("Updating profile - Request body: {}", profileRequest);
        var response = service.updateProfile(userId, profileRequest);

        LOGGER.info(PROFILE_RESPONSE, response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Patch profile")
    @PatchMapping(consumes = MERGE_PATCH_MEDIA_TYPE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProfileResponse> patchProfile(@UserId String userId, @Valid @RequestBody Map<String, Object> profileRequestMap) {
        LOGGER.info("Patching profile");
        var response = service.patchProfile(userId, profileRequestMap);

        LOGGER.info(PROFILE_RESPONSE, response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Delete profile")
    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteProfile(@UserId String userId) {
        LOGGER.info("Deleting profile");
        service.deleteProfile(userId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

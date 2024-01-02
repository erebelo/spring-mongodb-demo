package com.erebelo.springmongodbdemo.mock;

import com.erebelo.springmongodbdemo.domain.entity.ProfileContact;
import com.erebelo.springmongodbdemo.domain.entity.ProfileEntity;
import com.erebelo.springmongodbdemo.domain.entity.ProfileLocation;
import com.erebelo.springmongodbdemo.domain.entity.SpouseProfile;
import com.erebelo.springmongodbdemo.domain.entity.UserProfile;
import com.erebelo.springmongodbdemo.domain.enumeration.ContactTypeEnum;
import com.erebelo.springmongodbdemo.domain.enumeration.EmploymentStatusEnum;
import com.erebelo.springmongodbdemo.domain.enumeration.GenderEnum;
import com.erebelo.springmongodbdemo.domain.enumeration.HealthLevelEnum;
import com.erebelo.springmongodbdemo.domain.enumeration.MaritalStatusEnum;
import com.erebelo.springmongodbdemo.domain.request.ProfileContactDTO;
import com.erebelo.springmongodbdemo.domain.request.ProfileLocationDTO;
import com.erebelo.springmongodbdemo.domain.request.SpouseProfileDTO;
import com.erebelo.springmongodbdemo.domain.response.ProfileResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProfileMock {

    private static final String ID = "653307bb6dadb04fa2dc6f77";
    private static final String HASH_OBJECT = "426f65b70a600a628be40394f77bf887216aab2f719b784725bd82fc0c4e18ed";
    public static final String USER_ID = "12345";
    private static final String CREATED_BY = "12345";
    private static final String MODIFIED_BY = "12345";
    private static final LocalDateTime CREATED_DATE_TIME = LocalDateTime.of(2024, 1, 2, 19, 30, 54, 873);
    private static final LocalDateTime MODIFIED_DATE_TIME = LocalDateTime.of(2024, 1, 2, 19, 30, 54, 873);
    private static final Long VERSION = 0L;
    private static final String FIRST_NAME = "Bruce";
    private static final String LAST_NAME = "Wayne";
    private static final LocalDate DATE_OF_BIRTH = LocalDate.of(1980, 10, 22);
    private static final Integer NUMBER_OF_DEPENDENTS = 1;
    private static final BigDecimal ESTIMATED_ANNUAL_INCOME = BigDecimal.valueOf(230410.05);
    private static final BigDecimal ESTIMATED_NET_WORTH = BigDecimal.valueOf(1800900.01);
    private static final GenderEnum GENDER = GenderEnum.MALE;
    private static final MaritalStatusEnum MARITAL_STATUS = MaritalStatusEnum.SINGLE;
    private static final EmploymentStatusEnum EMPLOYMENT_STATUS = EmploymentStatusEnum.EMPLOYED;
    private static final HealthLevelEnum HEALTH_LEVEL = HealthLevelEnum.AVERAGE;
    private static final ContactTypeEnum CONTACT_TYPE = ContactTypeEnum.EMAIL;
    private static final String CONTACT_VALUE = "bruce.wayne@corp.com";
    private static final String ADDRESS = "200 7th Ave";
    private static final String CITY = "New York";
    private static final String STATE = "NY";
    private static final String COUNTRY = "USA";
    private static final String POSTAL_CODE = "10009";
    private static final String SPOUSE_FIRST_NAME = "Diana";
    private static final String SPOUSE_LAST_NAME = "Themyscira";
    private static final LocalDate SPOUSE_DATE_OF_BIRTH = LocalDate.of(1983, 7, 15);
    private static final GenderEnum SPOUSE_GENDER = GenderEnum.FEMALE;
    private static final EmploymentStatusEnum SPOUSE_EMPLOYMENT_STATUS = EmploymentStatusEnum.NOT_EMPLOYED;

    public static Optional<ProfileEntity> getProfileEntity() {
        return Optional.ofNullable(ProfileEntity.builder()
                .id(ID)
                .hashObject(HASH_OBJECT)
                .userId(USER_ID)
                .createdBy(CREATED_BY)
                .modifiedBy(MODIFIED_BY)
                .createdDateTime(CREATED_DATE_TIME)
                .modifiedDateTime(MODIFIED_DATE_TIME)
                .version(VERSION)
                .profile(UserProfile.builder()
                        .firstName(FIRST_NAME)
                        .lastName(LAST_NAME)
                        .dateOfBirth(DATE_OF_BIRTH)
                        .numberOfDependents(NUMBER_OF_DEPENDENTS)
                        .estimatedAnnualIncome(ESTIMATED_ANNUAL_INCOME)
                        .estimatedNetWorth(ESTIMATED_NET_WORTH)
                        .gender(GENDER)
                        .maritalStatus(MARITAL_STATUS)
                        .employmentStatus(EMPLOYMENT_STATUS)
                        .healthLevel(HEALTH_LEVEL)
                        .contactNumbers(Collections.singletonList(ProfileContact.builder()
                                .contactType(CONTACT_TYPE)
                                .contactValue(CONTACT_VALUE)
                                .build()))
                        .currentLocation(ProfileLocation.builder()
                                .address(ADDRESS)
                                .city(CITY)
                                .state(STATE)
                                .country(COUNTRY)
                                .postalCode(POSTAL_CODE)
                                .build())
                        .spouseProfile(SpouseProfile.builder()
                                .firstName(SPOUSE_FIRST_NAME)
                                .lastName(SPOUSE_LAST_NAME)
                                .dateOfBirth(SPOUSE_DATE_OF_BIRTH)
                                .gender(SPOUSE_GENDER)
                                .employmentStatus(SPOUSE_EMPLOYMENT_STATUS)
                                .build())
                        .build())
                .build());
    }

    public static ProfileResponse getProfileResponse() {
        return ProfileResponse.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .dateOfBirth(DATE_OF_BIRTH)
                .numberOfDependents(NUMBER_OF_DEPENDENTS)
                .estimatedAnnualIncome(ESTIMATED_ANNUAL_INCOME)
                .estimatedNetWorth(ESTIMATED_NET_WORTH)
                .gender(GENDER)
                .maritalStatus(MARITAL_STATUS)
                .employmentStatus(EMPLOYMENT_STATUS)
                .healthLevel(HEALTH_LEVEL)
                .contactNumbers(Collections.singletonList(ProfileContactDTO.builder()
                        .contactType(CONTACT_TYPE)
                        .contactValue(CONTACT_VALUE)
                        .build()))
                .currentLocation(ProfileLocationDTO.builder()
                        .address(ADDRESS)
                        .city(CITY)
                        .state(STATE)
                        .country(COUNTRY)
                        .postalCode(POSTAL_CODE)
                        .build())
                .spouseProfile(SpouseProfileDTO.builder()
                        .firstName(SPOUSE_FIRST_NAME)
                        .lastName(SPOUSE_LAST_NAME)
                        .dateOfBirth(SPOUSE_DATE_OF_BIRTH)
                        .gender(SPOUSE_GENDER)
                        .employmentStatus(SPOUSE_EMPLOYMENT_STATUS)
                        .build())
                .build();
    }
}

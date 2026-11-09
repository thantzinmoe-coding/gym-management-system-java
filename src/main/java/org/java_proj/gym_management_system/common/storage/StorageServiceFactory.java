package org.java_proj.gym_management_system.common.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StorageServiceFactory {

    private final StorageService localStorageService;
    private final CloudinaryStorageService cloudinaryStorageService;

    @Autowired
    public StorageServiceFactory(
            @Qualifier("localStorageService") final StorageService localStorageService,
            final CloudinaryStorageService cloudinaryStorageService) {
        this.localStorageService = localStorageService;
        this.cloudinaryStorageService = cloudinaryStorageService;
    }

    /** Cloudinary when its three settings are present, otherwise local disk (local development). */
    public StorageService getConfiguredStorageService() {
        return cloudinaryStorageService.isConfigured() ? cloudinaryStorageService : this.localStorageService;
    }

    /** The /files endpoint only ever serves what is on local disk (older uploads). */
    public StorageService getLocalStorageService() {
        return this.localStorageService;
    }
}

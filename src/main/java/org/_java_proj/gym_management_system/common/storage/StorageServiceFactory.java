package org._java_proj.gym_management_system.common.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StorageServiceFactory {

    private final StorageService localStorageService;

    @Autowired
    public StorageServiceFactory(
            @Qualifier("localStorageService") final  StorageService localStorageService) {
        this.localStorageService = localStorageService;

    }

    public StorageService getConfiguredStorageService() {
        return this.localStorageService;
    }
}

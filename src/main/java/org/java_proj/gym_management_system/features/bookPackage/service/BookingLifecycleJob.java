package org.java_proj.gym_management_system.features.bookPackage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.java_proj.gym_management_system.common.constant.MemberStatus;
import org.java_proj.gym_management_system.features.bookPackage.repository.BookPackageRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Closes bookings whose package has ended, so booking history is kept with a final status:
 * ACTIVE becomes COMPLETED, and PENDING requests nobody approved become CANCELLED.
 * Runs nightly and once at start-up (to catch up after downtime).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BookingLifecycleJob {

    private final BookPackageRepository bookPackageRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional // the listener is invoked through the proxy, so the inner call runs in this transaction
    public void onStartup() {
        closeEndedBookings();
    }

    @Scheduled(cron = "0 5 0 * * ?") // every day at 00:05
    @Transactional
    public void closeEndedBookings() {
        final LocalDate today = LocalDate.now();
        final LocalDateTime now = LocalDateTime.now();
        final int completed = bookPackageRepository.moveEndedBookings(MemberStatus.ACTIVE, MemberStatus.COMPLETED, today, now);
        final int expired = bookPackageRepository.moveEndedBookings(MemberStatus.PENDING, MemberStatus.CANCELLED, today, now);
        if (completed + expired > 0) {
            log.info("Closed ended bookings: {} completed, {} unapproved requests cancelled", completed, expired);
        }
    }
}

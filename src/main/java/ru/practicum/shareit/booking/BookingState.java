package ru.practicum.shareit.booking;

import java.time.LocalDateTime;
import java.util.List;

public enum BookingState {
    ALL {
        @Override
        public List<Booking> findForBooker(BookingRepository repo, Long userId, LocalDateTime now) {
            return repo.findByBookerIdOrderByStartDesc(userId);
        }

        @Override
        public List<Booking> findForOwner(BookingRepository repo, Long ownerId, LocalDateTime now) {
            return repo.findByItemOwnerIdOrderByStartDesc(ownerId);
        }
    },
    CURRENT {
        @Override
        public List<Booking> findForBooker(BookingRepository repo, Long userId, LocalDateTime now) {
            return repo.findCurrentByBooker(userId, now);
        }

        @Override
        public List<Booking> findForOwner(BookingRepository repo, Long ownerId, LocalDateTime now) {
            return repo.findCurrentByOwner(ownerId, now);
        }
    },
    PAST {
        @Override
        public List<Booking> findForBooker(BookingRepository repo, Long userId, LocalDateTime now) {
            return repo.findByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
        }

        @Override
        public List<Booking> findForOwner(BookingRepository repo, Long ownerId, LocalDateTime now) {
            return repo.findByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, now);
        }
    },
    FUTURE {
        @Override
        public List<Booking> findForBooker(BookingRepository repo, Long userId, LocalDateTime now) {
            return repo.findByBookerIdAndStartAfterOrderByStartDesc(userId, now);
        }

        @Override
        public List<Booking> findForOwner(BookingRepository repo, Long ownerId, LocalDateTime now) {
            return repo.findByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, now);
        }
    },
    WAITING {
        @Override
        public List<Booking> findForBooker(BookingRepository repo, Long userId, LocalDateTime now) {
            return repo.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
        }

        @Override
        public List<Booking> findForOwner(BookingRepository repo, Long ownerId, LocalDateTime now) {
            return repo.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING);
        }
    },
    REJECTED {
        @Override
        public List<Booking> findForBooker(BookingRepository repo, Long userId, LocalDateTime now) {
            return repo.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
        }

        @Override
        public List<Booking> findForOwner(BookingRepository repo, Long ownerId, LocalDateTime now) {
            return repo.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED);
        }
    };

    public abstract List<Booking> findForBooker(BookingRepository repo, Long userId, LocalDateTime now);

    public abstract List<Booking> findForOwner(BookingRepository repo, Long ownerId, LocalDateTime now);
}

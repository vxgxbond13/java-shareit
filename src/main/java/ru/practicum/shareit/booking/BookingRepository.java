package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    @Query("select b from Booking b " +
            "where b.booker.id = :userId " +
            "  and b.start <= :now and b.end >= :now " +
            "order by b.start desc")
    List<Booking> findCurrentByBooker(@Param("userId") Long userId,
                                      @Param("now") LocalDateTime now);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime now);


    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime now);


    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);


    List<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId);

    @Query("select b from Booking b " +
            "where b.item.owner.id = :ownerId " +
            "  and b.start <= :now and b.end >= :now " +
            "order by b.start desc")
    List<Booking> findCurrentByOwner(@Param("ownerId") Long ownerId,
                                     @Param("now") LocalDateTime now);

    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime now);

    List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime now);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);

    Optional<Booking> findFirstByItemIdAndEndBeforeOrderByEndDesc(Long itemId, LocalDateTime now);

    Optional<Booking> findFirstByItemIdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime now);


    @Query("select count(b) > 0 from Booking b " +
            "where b.booker.id = :userId " +
            "  and b.item.id = :itemId " +
            "  and b.status = 'APPROVED' " +
            "  and b.end < :now")
    boolean existsCompletedBooking(@Param("userId") Long userId,
                                   @Param("itemId") Long itemId,
                                   @Param("now") LocalDateTime now);
}

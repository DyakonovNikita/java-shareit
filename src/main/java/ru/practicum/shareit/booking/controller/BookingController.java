package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.service.BookingService;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<?> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestBody BookingRequestDto bookingRequestDto) {
        return new ResponseEntity<>(bookingService.create(userId, bookingRequestDto), HttpStatus.OK);
    }

    @GetMapping("{bookingId}")
    public ResponseEntity<?> getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @PathVariable("bookingId") Long bookingId) {
        return new ResponseEntity<>(bookingService.getById(userId, bookingId), HttpStatus.OK);
    }

    @PatchMapping("{bookingId}")
    public ResponseEntity<?> updateStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable("bookingId") Long bookingId,
                                          @RequestParam("approved") Boolean approved) {
        return new ResponseEntity<>(bookingService.updateStatus(userId, bookingId, approved), HttpStatus.OK);
    }

    @GetMapping("owner")
    public ResponseEntity<?> getBookingsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam(value = "state",
                                                defaultValue = "ALL") String state) {
        return new ResponseEntity<>(bookingService.getBookingsByOwner(userId, state), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getBookingsByBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam(value = "state",
                                                 defaultValue = "ALL") String state) {
        return new ResponseEntity<>(bookingService.getBookingsByBooker(userId, state), HttpStatus.OK);
    }
}
package com.example.myevent_be.service;

import com.example.myevent_be.dto.request.RentalRequest;
import com.example.myevent_be.dto.response.RentalResponse;
import com.example.myevent_be.entity.Event;
import com.example.myevent_be.entity.Rental;
import com.example.myevent_be.entity.User;
import com.example.myevent_be.mapper.RentalMapper;
import com.example.myevent_be.repository.EventRepository;
import com.example.myevent_be.repository.RentalRepository;
import com.example.myevent_be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RentalService {
    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RentalMapper rentalMapper;

    public List<RentalResponse> getAllRentals() {
        return rentalRepository.findAll().stream()
                .map(rentalMapper::toRentalResponse)
                .toList();
    }

    public Optional<RentalResponse> getRentalById(String id) {
        return rentalRepository.findById(id).map(rentalMapper::toRentalResponse);
    }

    public RentalResponse createRental(RentalRequest request) {
        Rental rental = new Rental();
        rental.setTotal_price(request.getTotalPrice());
        rental.setRental_start_time(request.getRentalStartTime());
        rental.setRental_end_time(request.getRentalEndTime());
        rental.setCustom_location(request.getCustomLocation());
        if (request.getUserId() != null) {
            User user = userRepository.findById(request.getUserId()).orElse(null);
            rental.setUser(user);
        }
        if (request.getEventId() != null) {
            Event event = eventRepository.findById(request.getEventId()).orElse(null);
            rental.setEvent(event);
        }
        Rental saved = rentalRepository.save(rental);
        return rentalMapper.toRentalResponse(saved);
    }

    public Optional<RentalResponse> updateRental(String id, RentalRequest request) {
        return rentalRepository.findById(id).map(rental -> {
            rental.setTotal_price(request.getTotalPrice());
            rental.setRental_start_time(request.getRentalStartTime());
            rental.setRental_end_time(request.getRentalEndTime());
            rental.setCustom_location(request.getCustomLocation());
            if (request.getUserId() != null) {
                User user = userRepository.findById(request.getUserId()).orElse(null);
                rental.setUser(user);
            }
            if (request.getEventId() != null) {
                Event event = eventRepository.findById(request.getEventId()).orElse(null);
                rental.setEvent(event);
            }
            return rentalMapper.toRentalResponse(rentalRepository.save(rental));
        });
    }

    public boolean deleteRental(String id) {
        if (!rentalRepository.existsById(id)) {
            return false;
        }
        rentalRepository.deleteById(id);
        return true;
    }
}

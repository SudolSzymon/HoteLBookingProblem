package main.java;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.requireNonNull;

public class HotelBookingManager implements BookingManager {

    private static final Map<LocalDate, Map<Integer, String>> BOOKINGS = new ConcurrentHashMap<>();
    public static final Set<Integer> ROOM_NUMBERS = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 234, 4324, 23239871, 101));

    private static class SingletonHolder {
        static final HotelBookingManager INSTANCE = new HotelBookingManager();
    }

    public static HotelBookingManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * Prevent  initialisation outside this class
     */
    private HotelBookingManager() {
    }

    @Override
    public boolean isRoomAvailable(Integer room, LocalDate date) {
        requireNonNull(room, "room");
        requireNonNull(date, "date");
        if (!ROOM_NUMBERS.contains(room))
            throw new IllegalArgumentException(String.format("Room %s does not exist", room));

        Map<Integer, String> bookingsForDate = BOOKINGS.get(date);
        return bookingsForDate == null || !bookingsForDate.containsKey(room);
    }

    @Override
    public void addBooking(String guest, Integer room, LocalDate date) throws IllegalArgumentException{
        requireNonNull(room, "room");
        requireNonNull(date, "date");
        requireNonNull(guest, "guest");
        if (!ROOM_NUMBERS.contains(room))
            throw new IllegalArgumentException(String.format("Room %s does not exist", room));

        Map<Integer, String> bookingsForDate = new ConcurrentHashMap<>();
        if (BOOKINGS.putIfAbsent(date, bookingsForDate) != null)
            bookingsForDate = BOOKINGS.get(date);
        if (bookingsForDate.putIfAbsent(room, guest) != null)
            throw new IllegalArgumentException(String.format("This room [%s] is already booked on %s", room, date.toString()));
    }

    @Override
    public Iterable<Integer> getAvailableRooms(LocalDate date) {
        requireNonNull(date, "No date specified");

        Map<Integer, String> bookingsForDate = BOOKINGS.get(date);
        Set<Integer> bookedRooms = bookingsForDate == null ? Collections.EMPTY_SET : bookingsForDate.keySet();
        Set<Integer> roomNumbersCopy = new HashSet<>(ROOM_NUMBERS);
        roomNumbersCopy.removeAll(bookedRooms);
        return roomNumbersCopy;
    }
}

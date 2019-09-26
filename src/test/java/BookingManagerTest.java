package test.java;

import main.java.BookingManager;
import main.java.HotelBookingManager;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;

public class BookingManagerTest {

    private static BookingManager manager;

    @BeforeClass
    public static void setup() {
        manager = HotelBookingManager.getInstance();
    }

    @Test
    public void generalTest() {
        LocalDate today = LocalDate.parse("2012-07-21");
        LocalDate notToday = LocalDate.parse("2012-07-22");
        Iterable<Integer> available = manager.getAvailableRooms(today);
        for (Integer room : available) {
            Assert.assertTrue(HotelBookingManager.ROOM_NUMBERS.contains(room));
        }
        Assert.assertTrue(manager.isRoomAvailable(101, today)); // outputs true
        manager.addBooking("Smith", 101, today);
        Assert.assertFalse(manager.isRoomAvailable(101, today)); // outputs false
        available = manager.getAvailableRooms(today);
        for (Integer room : available) {
            Assert.assertTrue(HotelBookingManager.ROOM_NUMBERS.contains(room));
            Assert.assertNotEquals(101, (int) room);
        }
        Assert.assertTrue(manager.isRoomAvailable(1, today)); // outputs true
        manager.addBooking("Smith", 1, today);
        Assert.assertFalse(manager.isRoomAvailable(1, today)); // outputs false

        Assert.assertTrue(manager.isRoomAvailable(101, notToday)); // outputs true
        manager.addBooking("Smith", 101, notToday);
        Assert.assertFalse(manager.isRoomAvailable(101, notToday)); // outputs false

    }

    @Test
    public void multiThreadedTest() {
        LocalDate today = LocalDate.parse("2012-07-25");
        for (Integer room : HotelBookingManager.ROOM_NUMBERS) { //Book each room in separate thread to simulate  multiple people using same  manager
            Runnable runnable = () -> {
                Assert.assertTrue(manager.isRoomAvailable(room, today)); // outputs true
                manager.addBooking("Smith", room, today);
                Assert.assertFalse(manager.isRoomAvailable(room, today)); // outputs false
            };
            Thread t = new Thread(runnable);
            t.start();
        }
       }
}

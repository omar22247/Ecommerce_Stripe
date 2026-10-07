package com.omar.ecommerce.repository;

import com.omar.ecommerce.entity.Address;
import com.omar.ecommerce.entity.Role;
import com.omar.ecommerce.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class AddressRepositoryTest {

    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private UserRepository userRepository;
    private User user;

    List<Address> addresses;
    @BeforeEach
    void setup() {
         user = new User();
        user.setEmail("omar1889@example.com");
        user.setRole(Role.ADMIN);
        user.setFirstName("Omar");
        user.setLastName("Abohashim");
        user.setPassword("Test@@1235445");
        user.setEnabled(true);
        userRepository.save(user);
        addresses = new ArrayList<>();

        for (int i=0; i<10; i++) {
            Address address = new Address();
            address.setCity("Test"+i);
            address.setCountry("Test"+i);
            address.setStreet("Test"+i);
            address.setState("Test"+i);
            address.setPostalCode("Test"+i);
            if(i==5) {
                address.setDefault(true);
            }
            address.setUser(user);
            addressRepository.save(address);
            addresses.add(address);
        }
    }

    @Test
    void findByUserIdAndIsDefaultTrueShouldReturnDefaultAddresses() {

        Optional<Address> result = addressRepository.findByUserIdAndIsDefaultTrue(user.getId());
        System.out.println("result"+result.get().getId());

        assertTrue(result.isPresent());
        assertEquals("Test5", result.get().getCity());
    }

    @Test
    void findByUserIdShouldReturnListOfAddresses() {

        List<Address> result = addressRepository.findByUserId(user.getId());

        assertEquals(10, result.size());
        assertTrue(result.stream().allMatch(a -> a.getUser().getId().equals(user.getId())));

    }

}
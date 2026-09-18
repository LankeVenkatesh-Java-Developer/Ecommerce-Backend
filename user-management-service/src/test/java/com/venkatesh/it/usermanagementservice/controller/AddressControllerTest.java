package com.venkatesh.it.usermanagementservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.venkatesh.it.usermanagementservice.config.TestSecurityConfig;
import com.venkatesh.it.usermanagementservice.model.dto.AddressRequest;
import com.venkatesh.it.usermanagementservice.model.dto.AddressResponse;
import com.venkatesh.it.usermanagementservice.model.enums.AddressType;
import com.venkatesh.it.usermanagementservice.service.AddressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AddressController.class)
@Import(TestSecurityConfig.class)
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AddressService addressService;

    private AddressResponse testAddressResponse;
    private AddressRequest addressRequest;

    @BeforeEach
    void setUp() {
        testAddressResponse = AddressResponse.builder()
                .id(1L)
                .userId(1L)
                .addressLine1("123 Main St")
                .addressLine2("Apt 4B")
                .city("New York")
                .state("NY")
                .country("USA")
                .postalCode("10001")
                .addressType(AddressType.HOME)
                .isDefault(false)
                .build();

        addressRequest = AddressRequest.builder()
                .addressLine1("123 Main St")
                .addressLine2("Apt 4B")
                .city("New York")
                .state("NY")
                .country("USA")
                .postalCode("10001")
                .addressType(AddressType.HOME)
                .isDefault(false)
                .build();
    }

    @Test
    void createAddressForUser_Success() throws Exception {
        when(addressService.createAddress(anyLong(), any(AddressRequest.class))).thenReturn(testAddressResponse);

        mockMvc.perform(post("/users/addresses/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addressRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getAllAddressesByUserId_Success() throws Exception {
        List<AddressResponse> addresses = Arrays.asList(testAddressResponse);
        when(addressService.getAllAddressesByUserId(1L)).thenReturn(addresses);

        mockMvc.perform(get("/users/addresses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].userId").value(1L));
    }

    @Test
    void getAddressById_Success() throws Exception {
        when(addressService.getAddressById(1L, 1L)).thenReturn(testAddressResponse);

        mockMvc.perform(get("/users/addresses/1/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.addressLine1").value("123 Main St"));
    }

    @Test
    void getDefaultAddress_Success() throws Exception {
        testAddressResponse.setIsDefault(true);
        when(addressService.getDefaultAddress(1L)).thenReturn(testAddressResponse);

        mockMvc.perform(get("/users/addresses/1/default"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isDefault").value(true));
    }

    @Test
    void getAddressesByType_Success() throws Exception {
        List<AddressResponse> addresses = Arrays.asList(testAddressResponse);
        when(addressService.getAddressesByType(1L, "HOME")).thenReturn(addresses);

        mockMvc.perform(get("/users/addresses/1/type/HOME"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].addressType").value("HOME"));
    }

    @Test
    void updateAddressForUser_Success() throws Exception {
        AddressResponse updatedResponse = AddressResponse.builder()
                .id(1L)
                .userId(1L)
                .addressLine1("789 Pine St")
                .city("Chicago")
                .state("IL")
                .country("USA")
                .postalCode("60601")
                .addressType(AddressType.HOME)
                .isDefault(false)
                .build();

        when(addressService.updateAddress(anyLong(), anyLong(), any(AddressRequest.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/users/addresses/1/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addressRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.addressLine1").value("789 Pine St"));
    }

    @Test
    void setDefaultAddress_Success() throws Exception {
        testAddressResponse.setIsDefault(true);
        when(addressService.setDefaultAddress(1L, 1L)).thenReturn(testAddressResponse);

        mockMvc.perform(patch("/users/addresses/1/1/default")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isDefault").value(true));
    }

    @Test
    void deleteAddress_Success() throws Exception {
        mockMvc.perform(delete("/users/addresses/1/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}

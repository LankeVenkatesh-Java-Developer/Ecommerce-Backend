package com.venkatesh.it.usermanagementservice.repository;

import com.venkatesh.it.usermanagementservice.model.Address;
import com.venkatesh.it.usermanagementservice.model.enums.AddressType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findByUserId(Long userId);

    Optional<Address> findByIdAndUserId(Long id, Long userId);

    List<Address> findByUserIdAndAddressType(Long userId, AddressType addressType);

    Optional<Address> findByUserIdAndIsDefaultTrue(Long userId);

    @Modifying
    @Query("UPDATE Address a SET a.isDefault = false WHERE a.user.id = :userId AND a.id != :excludeId")
    void unsetDefaultAddressForUser(@Param("userId") Long userId, @Param("excludeId") Long excludeId);

    @Modifying
    @Query("UPDATE Address a SET a.isDefault = false WHERE a.user.id = :userId")
    void unsetAllDefaultAddressesForUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(a) > 0 FROM Address a WHERE a.user.id = :userId AND a.isDefault = true")
    boolean hasDefaultAddress(@Param("userId") Long userId);
}

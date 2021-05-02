package me.modernpage.repository;

import me.modernpage.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface AccountRepository extends JpaRepository<Account, Long>{
	Optional<Account> findAccountByUsername(String username);
	Optional<Account> findAccountByEmail(String email);
	Optional<Account> findAccountByProfileId(Long profileId);
	boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}

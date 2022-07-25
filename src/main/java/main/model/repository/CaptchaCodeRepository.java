package main.model.repository;

import main.model.CaptchaCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CaptchaCodeRepository extends JpaRepository<CaptchaCode, Integer> {
    Optional<CaptchaCode> getCaptchaCodeBySecretCode(String secretCode);
}

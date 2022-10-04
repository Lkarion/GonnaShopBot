package bots.gonna_shop_bot_onspring.repository;

import bots.gonna_shop_bot_onspring.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findByName(String name);
    List<Product> findAllByChatId(Long chatId);
    Boolean existsByName(String name);
}

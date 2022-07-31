package com.buezman.inventory_service;

import com.buezman.inventory_service.model.Inventory;
import com.buezman.inventory_service.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
@EnableEurekaClient
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner loadData(InventoryRepository inventoryRepository) {
		return args -> {
			Inventory Hennessy = Inventory.builder()
					.skuCode("Hennessy")
					.quantity(100)
					.build();

			Inventory Ciroc = Inventory.builder()
					.skuCode("Ciroc")
					.quantity(50)
					.build();

			Inventory Azul = Inventory.builder()
					.skuCode("Azul")
					.quantity(20)
					.build();

			Inventory Martelle = Inventory.builder()
					.skuCode("Martelle")
					.quantity(0)
					.build();

			inventoryRepository.saveAll(List.of(Hennessy, Ciroc, Azul, Martelle));
		};
	}
}

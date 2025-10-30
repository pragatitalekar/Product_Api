package com.products.pragati;

import com.products.pragati.repository.Product;
import com.products.pragati.repository.ProductRepo;
import com.products.pragati.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ProductServiceIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepo productRepo;

    @Test
    @DisplayName("Full Integration Flow: Add → Get → Update → Filter → Delete")
    void fullProductServiceIntegrationFlow() {

        // 1️⃣ Add Products
        Product p1 = new Product("Laptop", 50000.0);
        Product p2 = new Product("Mobile", 20000.0);
        Product p3 = new Product("Tablet", 30000.0);

        p1 = productService.addProduct(p1);
        p2 = productService.addProduct(p2);
        p3 = productService.addProduct(p3);

        assertThat(productRepo.count()).isEqualTo(3);

        // 2️⃣ Get All Products
        List<Product> allProducts = productService.getAllProducts();
        assertThat(allProducts).hasSize(3);

        // 3️⃣ Get Product by ID
        Optional<Product> fetched = productService.getProductById(p2.getId());
        assertThat(fetched).isPresent();
        assertThat(fetched.get().getName()).isEqualTo("Mobile");

        // 4️⃣ Update Product Name
        Product updatedName = productService.updateName(p1.getId(), "Gaming Laptop");
        assertThat(updatedName.getName()).isEqualTo("Gaming Laptop");

        // 5️⃣ Update Product Price
        Product updatedPrice = productService.updatePrice(p3.getId(), 35000.0);
        assertThat(updatedPrice.getPrice()).isEqualTo(35000.0);

        // 6️⃣ Filter by Name (case-insensitive)
        List<Product> nameFiltered = productService.filterByName("mobile");
        assertThat(nameFiltered).hasSize(1);
        assertThat(nameFiltered.get(0).getName()).isEqualTo("Mobile");

        // 7️⃣ Filter by Price
        List<Product> priceFiltered = productService.filterByPrice(20000.0);
        assertThat(priceFiltered).hasSize(1);
        assertThat(priceFiltered.get(0).getPrice()).isEqualTo(20000.0);

        // 8️⃣ Filter by Price Range
        List<Product> rangeFiltered = productService.filterByPriceRange(25000.0, 60000.0);
        assertThat(rangeFiltered)
                .extracting(Product::getName)
                .containsExactlyInAnyOrder("Gaming Laptop", "Tablet");

        // 9️⃣ Delete Product
        productService.deleteProduct(p2.getId());
        assertThat(productRepo.existsById(p2.getId())).isFalse();

        // 🔟 Final Check
        List<Product> remaining = productService.getAllProducts();
        assertThat(remaining).hasSize(2);

        System.out.println("✅ Integration Flow Completed Successfully!");
    }
}

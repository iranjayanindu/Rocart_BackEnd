package com.masai.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.masai.models.CategoryEnum;
import com.masai.models.Product;
import com.masai.models.ProductDTO;
import com.masai.models.ProductStatus;


@Repository
public interface ProductDao extends JpaRepository<Product, Integer> {
	
	
	@Query("select new com.masai.models.ProductDTO(p.productName,p.manufacturer,p.price,p.quantity) "
			+ "from Product p where p.category=:catenum")
	public List<ProductDTO> getAllProductsInACategory(@Param("catenum") CategoryEnum catenum);
	
	
	@Query("select new com.masai.models.ProductDTO(p.productName,p.manufacturer,p.price,p.quantity) "
			+ "from Product p where p.status=:status")
	public List<ProductDTO> getProductsWithStatus(@Param("status") ProductStatus status);
	
	@Query("select new com.masai.models.ProductDTO(p.productName,p.manufacturer,p.price,p.quantity) "
			+ "from Product p where p.seller.sellerId=:id")
	public List<ProductDTO> getProductsOfASeller(@Param("id") Integer id);

	@Query("SELECT p.category, COUNT(p) FROM Product p GROUP BY p.category")
	List<Object[]> getCategoryProductCount();


	List<Product> findByProductNameContainingIgnoreCase(String partialName);

	@Query(value = "SELECT * FROM product ORDER BY selling_count DESC LIMIT :count", nativeQuery = true)
	List<Product> findTopSellingProducts(@Param("count") int count);

	@Query(value = "SELECT * FROM product ORDER BY create_time DESC LIMIT :count", nativeQuery = true)
	List<Product> findNewestProducts(@Param("count") int count);

	@Query(value = "SELECT  * FROM product WHERE seller_seller_id = :count ORDER BY selling_count DESC", nativeQuery = true)
	List<Product> findSellerTopProducts(@Param("count") int count);

}

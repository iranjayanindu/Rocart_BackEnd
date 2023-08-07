package com.masai.service;

import java.util.List;

import com.masai.models.*;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {

	public Product addProductToCatalog(String token, Product product,MultipartFile[] imageFiles);

	public Product getProductFromCatalogById(Integer id);

	public String deleteProductFromCatalog(Integer id);

	public Product updateProductIncatalog(Product product);
	
	public List<Product> getAllProductsIncatalog();
	
	public List<ProductDTO> getAllProductsOfSeller(Integer id);
	
	public List<ProductDTO> getProductsOfCategory(CategoryEnum catenum);
	
	public List<ProductDTO> getProductsOfStatus(ProductStatus status);

	public List<String> saveImages(MultipartFile[] imageFiles);
	
	public Product updateProductQuantityWithId(Integer id,ProductDTO prodDTO);

	List<CategoryProductCountDTO> getCategoryProductCount();

	List<Product> searchProductsByName(String productName);
}

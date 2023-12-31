package com.masai.service;

import java.util.List;

import com.masai.models.*;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {

	public Product addProductToCatalog(String token, Product product,MultipartFile[] imageFiles);

	public Product addProduct(String token, Product product) throws Exception;

	public Product getProductFromCatalogById(Integer id);

	public String deleteProductFromCatalog(Integer id);

	public Product updateProductIncatalog(Product product);
	
	public List<Product> getAllProductsIncatalog();

	public List<Product> getAllSellerProducts(String token);

	public List<ProductDTO> getAllProductsOfSeller(Integer id);

	List<Product> findProductImage(Product product) throws Exception;
	
	public List<ProductDTO> getProductsOfCategory(CategoryEnum catenum);
	
	public List<ProductDTO> getProductsOfStatus(ProductStatus status);

	public List<String> saveImages(MultipartFile[] imageFiles);
	
	public Product updateProductQuantityWithId(Integer id,ProductDTO prodDTO);

	List<CategoryProductCountDTO> getCategoryProductCount();

	List<Product> searchProductsByName(String productName);

    List<Product> getTopTenSellingProducts();

	List<Product> getNewTenProducts();

	List<Product> getSellerTopProducts(String token);

	int getSellerProductsCount(String token);

	Product updateRating(String token,int rate,Integer id)throws Exception;

	public Resource loadImage();

}

package com.masai.controller;

import java.util.List;

import javax.validation.Valid;

import com.masai.models.*;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.masai.service.ProductService;

import io.swagger.v3.oas.models.security.SecurityScheme.In;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ProductController {

	@Autowired
	private ProductService pService;

	// this method adds new product to catalog by seller(if seller is new it adds
	// seller as well
	// if seller is already existing products will be mapped to same seller) and
	// returns added product

	@PostMapping("/products")
	public ResponseEntity<Product> addProductToCatalogHandler(@RequestHeader("token") String token,
															  @RequestParam("productName") String productName,
															  @RequestParam("price") double price,
															  @RequestParam("description") String description,
															  @RequestParam("manufacturer") String manufacturer,
															  @RequestParam("quantity") int quantity,
															  @RequestParam("category") String category,
															  @RequestParam("status") String status, @RequestParam("imageFiles") MultipartFile[] imageFiles) {
		Product product = new Product();
		product.setProductName(productName);
		product.setPrice(price);
		product.setDescription(description);
		product.setManufacturer(manufacturer);
		product.setQuantity(quantity);
		product.setCategory(CategoryEnum.ELECTRONICS);
		product.setStatus(ProductStatus.AVAILABLE);

		Product prod = pService.addProductToCatalog(token, product, imageFiles);

		return new ResponseEntity<Product>(prod, HttpStatus.ACCEPTED);

	}

	// This method gets the product which needs to be added to the cart returns
	// product

	@GetMapping("/product/{id}")
	public ResponseEntity<Product> getProductFromCatalogByIdHandler(@PathVariable("id") Integer id) {

		Product prod = pService.getProductFromCatalogById(id);

		return new ResponseEntity<Product>(prod, HttpStatus.FOUND);

	}

	// This method will delete the product from catalog and returns the response
	// This will be called only when the product qty will be zero or seller wants to
	// delete for any other reason

	@DeleteMapping("/product/{id}")
	public ResponseEntity<String> deleteProductFromCatalogHandler(@PathVariable("id") Integer id) {
		
		String res = pService.deleteProductFromCatalog(id);
		return new ResponseEntity<String>(res, HttpStatus.OK);
	}

	@PutMapping("/products")
	public ResponseEntity<Product> updateProductInCatalogHandler(@Valid @RequestBody Product prod) {

		Product prod1 = pService.updateProductIncatalog(prod);

		return new ResponseEntity<Product>(prod1, HttpStatus.OK);

	}

	@GetMapping("/products")
	public ResponseEntity<List<Product>> getAllProductsHandler() {

		List<Product> list = pService.getAllProductsIncatalog();

		return new ResponseEntity<List<Product>>(list, HttpStatus.OK);
	}
	
  //this method gets the products mapped to a particular seller
	@GetMapping("/products/seller/{id}")
	public ResponseEntity<List<ProductDTO>> getAllProductsOfSellerHandler(@PathVariable("id") Integer id) {

		List<ProductDTO> list = pService.getAllProductsOfSeller(id);

		return new ResponseEntity<List<ProductDTO>>(list, HttpStatus.OK);
	}

	@GetMapping("/products/{catenum}")
	public ResponseEntity<List<ProductDTO>> getAllProductsInCategory(@PathVariable("catenum") String catenum) {
		CategoryEnum ce = CategoryEnum.valueOf(catenum.toUpperCase());
		List<ProductDTO> list = pService.getProductsOfCategory(ce);
		return new ResponseEntity<List<ProductDTO>>(list, HttpStatus.OK);

	}

	@GetMapping("/products/status/{status}")
	public ResponseEntity<List<ProductDTO>> getProductsWithStatusHandler(@PathVariable("status") String status) {

		ProductStatus ps = ProductStatus.valueOf(status.toUpperCase());
		List<ProductDTO> list = pService.getProductsOfStatus(ps);

		return new ResponseEntity<List<ProductDTO>>(list, HttpStatus.OK);

	}
	
	
	@PutMapping("/products/{id}")
	public ResponseEntity<Product> updateQuantityOfProduct(@PathVariable("id") Integer id,@RequestBody ProductDTO prodDto){
		
		 Product prod =   pService.updateProductQuantityWithId(id, prodDto);
		
		 return new ResponseEntity<Product>(prod,HttpStatus.ACCEPTED);
	}

	@GetMapping("/category/product-count")
	public ResponseEntity<List<CategoryProductCountDTO>> getCategoryProductCount() {
		List<CategoryProductCountDTO> response = pService.getCategoryProductCount();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/search")
	public ResponseEntity<List<Product>> searchProducts(@RequestParam("name") String productName) {
		List<Product> products = pService.searchProductsByName(productName);
		return new ResponseEntity<>(products, HttpStatus.OK);
	}

	@GetMapping("/products/top-selling")
	public ResponseEntity<List<Product>> getTopTenSellingProducts() {
		List<Product> topSellingProducts = pService.getTopTenSellingProducts();
		return new ResponseEntity<>(topSellingProducts, HttpStatus.OK);
	}
	
	@GetMapping("/products/new-products")
	public ResponseEntity<List<Product>> getNewProducts() {
		List<Product> newTenProducts = pService.getNewTenProducts();
		return new ResponseEntity<>(newTenProducts, HttpStatus.OK);
	}

	@GetMapping("/products/seller/Top-products")
	public ResponseEntity<List<Product>> getSellerTopProducts(@RequestHeader String token){
		List<Product> sellerTopProducts = pService.getSellerTopProducts(token);
		return new ResponseEntity<>(sellerTopProducts, HttpStatus.OK);
	}

	@PutMapping("/product/customer/rating")
	public ResponseEntity<Product> addRatingbycustomer(@RequestHeader String token,@RequestParam("rate") int rating,@RequestParam("id") Integer productId) throws Exception {
		Product product = pService.updateRating(token, rating, productId);
		return new ResponseEntity<>(product,HttpStatus.ACCEPTED);
	}

}

package com.masai.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import com.masai.exception.LoginException;
import com.masai.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import com.masai.exception.CategoryNotFoundException;
import com.masai.exception.ProductNotFoundException;
import com.masai.repository.ProductDao;
import com.masai.repository.SellerDao;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductDao prodDao;

	@Autowired
	private SellerService sService;

	@Autowired
	private SellerDao sDao;

	@Autowired
	private LoginLogoutService loginService;

	@Value("${upload.path}") // Spring Boot property for the file upload path.
	private String uploadPath;

	@Override
	public Product 	addProductToCatalog(String token, Product product, MultipartFile[] imageFiles) {

		Product prod = null;
		Seller seller1 = sService.getCurrentlyLoggedInSeller(token);
		product.setSeller(seller1);

		Seller Existingseller = sService.getSellerByMobile(product.getSeller().getMobile(), token);
		Optional<Seller> opt = sDao.findById(Existingseller.getSellerId());
		product.setCreateTime(Date.from(Instant.now()));
		if (opt.isPresent()) {
			Seller seller = opt.get();
			List<String> imageUrl = saveImages(imageFiles);
			product.setSeller(seller);
			product.setImageUrls(imageUrl);

			prod = prodDao.save(product);
			;

			seller.getProduct().add(product);
			sDao.save(seller);

		} else {
			List<String> imageUrl = saveImages(imageFiles);
			prod.setImageUrls(imageUrl);
			prod = prodDao.save(product);
			;
		}

		return prod;
	}

	@Override
	public Product getProductFromCatalogById(Integer id) throws ProductNotFoundException {

		Optional<Product> opt = prodDao.findById(id);
		if (opt.isPresent()) {
			return opt.get();
		}

		else
			throw new ProductNotFoundException("Product not found with given id");
	}

	@Override
	public String deleteProductFromCatalog(Integer id) throws ProductNotFoundException {
		Optional<Product> opt = prodDao.findById(id);
		
		if (opt.isPresent()) {
			Product prod = opt.get();
			System.out.println(prod);
			prodDao.delete(prod);
			return "Product deleted from catalog";
		} else
			throw new ProductNotFoundException("Product not found with given id");

	}

	@Override
	public Product updateProductIncatalog(Product prod) throws ProductNotFoundException {

		Optional<Product> opt = prodDao.findById(prod.getProductId());

		if (opt.isPresent()) {
			opt.get();
			Product prod1 = prodDao.save(prod);
			return prod1;
		} else
			throw new ProductNotFoundException("Product not found with given id");
	}

	@Override
	public List<Product> getAllProductsIncatalog() {
		List<Product> list = prodDao.findAll();
		
		if (list.size() > 0) {
			return list;
		} else
			throw new ProductNotFoundException("No products in catalog");

	}

	@Override
	public List<ProductDTO> getProductsOfCategory(CategoryEnum catenum) {

		List<ProductDTO> list = prodDao.getAllProductsInACategory(catenum);
		if (list.size() > 0) {

			return list;
		} else
			throw new CategoryNotFoundException("No products found with category:" + catenum);
	}

	@Override
	public List<ProductDTO> getProductsOfStatus(ProductStatus status) {

		List<ProductDTO> list = prodDao.getProductsWithStatus(status);

		if (list.size() > 0) {
			return list;
		} else
			throw new ProductNotFoundException("No products found with given status:" + status);
	}

	@Override
	public List<String> saveImages(MultipartFile[] imageFiles) {
		List<String> imageUrls = new ArrayList<>();
		for (MultipartFile imageFile : imageFiles) {
			try {
				String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
				System.out.println("file name : "+fileName);
				String filePath = Paths.get(uploadPath, fileName).toString();
				imageFile.transferTo(new File(filePath));
				imageUrls.add(fileName); // Add the relative URL to the list.
			} catch (IOException e) {
				// Handle the exception as needed.
				throw new RuntimeException("Failed to save image.", e);
			}
		}
		return imageUrls;
	}

	@Override
	public Product updateProductQuantityWithId(Integer id,ProductDTO prodDto) {
		Product prod = null;
		 Optional<Product> opt = prodDao.findById(id);
		 
		 if(opt!=null) {
			  prod = opt.get();
			 prod.setQuantity(prod.getQuantity()+prodDto.getQuantity());
			 if(prod.getQuantity()>0) {
				 prod.setStatus(ProductStatus.AVAILABLE);
			 }
			 prodDao.save(prod);
			 
		 }
		 else
			 throw new ProductNotFoundException("No product found with this Id");
		
		return prod;
	}

	@Override
	public List<CategoryProductCountDTO> getCategoryProductCount() {
		List<Object[]> results = prodDao.getCategoryProductCount();
		Map<CategoryEnum, Long> categoryProductCountMap = results.stream()
				.collect(Collectors.toMap(
						result -> (CategoryEnum) result[0],
						result -> (long) result[1]
				));

		List<CategoryProductCountDTO> response = new ArrayList<>();
		for (CategoryEnum category : CategoryEnum.values()) {
			CategoryProductCountDTO categoryResponse = new CategoryProductCountDTO();
			categoryResponse.setCategory(category);
			categoryResponse.setProductCount(categoryProductCountMap.getOrDefault(category, 0L));
			response.add(categoryResponse);
		}

		return response;
	}

	@Override
	public List<Product> searchProductsByName(String productName) {
		return prodDao.findByProductNameContainingIgnoreCase(productName);
	}

	@Override
	public List<Product> getTopTenSellingProducts() {
		List<Product> topSellingProducts = prodDao.findTopSellingProducts(10);
		return topSellingProducts;
	}

	@Override
	public List<Product> getNewTenProducts() {
		List<Product> newestProducts = prodDao.findNewestProducts(10);
		return newestProducts;
	}

	@Override
	public List<Product> getSellerTopProducts(String token) {
		Seller seller1 = sService.getCurrentlyLoggedInSeller(token);
		Seller existingseller = sService.getSellerByMobile(seller1.getMobile(), token);
		int sellerId = existingseller.getSellerId();
		System.out.println("seller id : "+sellerId);
		return prodDao.findSellerTopProducts(sellerId);
	}

	@Override
	public Product updateRating(String token, int rate, Integer id) throws Exception {

		if(token.contains("customer") == false) {
			throw new LoginException("Invalid session token for customer");
		}

		loginService.checkTokenStatus(token);

		Product product = prodDao.findById(id)
				.orElseThrow(() -> new ProductNotFoundException("Product not found"));

		if (rate >= 1 && rate <= 5) {
			int totalRating = product.getTotalRating() + rate;
			int numRating = product.getNumRatings() + 1;
			product.setTotalRating(totalRating);
			product.setNumRatings(numRating);
			product.setAvgRating(calculateAverageRating(totalRating,numRating));
			return prodDao.save(product);

		} else {
			throw new IllegalArgumentException("Rating must be between 1 and 5");
		}
	}

	private double calculateAverageRating(int totalRating, int numRating) {
		if (numRating > 0) {
			return (double) totalRating / numRating;
		} else {
			return 0.0;
		}
	}

	@Override
	public List<ProductDTO> getAllProductsOfSeller(Integer id) {
		
		List<ProductDTO> list = prodDao.getProductsOfASeller(id);
		
		if(list.size()>0) {
			
			return list;
			
		}
		
		else {
			throw new ProductNotFoundException("No products with SellerId: "+id);
		}
	}

}

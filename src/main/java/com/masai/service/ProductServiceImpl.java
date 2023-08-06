package com.masai.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import com.masai.exception.CategoryNotFoundException;
import com.masai.exception.ProductNotFoundException;
import com.masai.models.CategoryEnum;
import com.masai.models.Product;
import com.masai.models.ProductDTO;
import com.masai.models.ProductStatus;
import com.masai.models.Seller;
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

	@Value("${upload.path}") // Spring Boot property for the file upload path.
	private String uploadPath;

	@Override
	public Product 	addProductToCatalog(String token, Product product, MultipartFile[] imageFiles) {

		Product prod = null;
		Seller seller1 = sService.getCurrentlyLoggedInSeller(token);
		product.setSeller(seller1);

		Seller Existingseller = sService.getSellerByMobile(product.getSeller().getMobile(), token);
		Optional<Seller> opt = sDao.findById(Existingseller.getSellerId());

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
				imageUrls.add("/images/" + fileName); // Add the relative URL to the list.
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

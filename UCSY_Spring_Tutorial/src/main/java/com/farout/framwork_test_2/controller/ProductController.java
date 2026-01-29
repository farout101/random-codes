package com.farout.framwork_test_2.controller;

import com.farout.framwork_test_2.model.Product;
import com.farout.framwork_test_2.service.ExcelService;
import com.farout.framwork_test_2.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ProductController {
    @Autowired
    private ExcelService excelService;
    @Autowired
    private ProductService productService;

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "5") int size, @RequestParam(required = false) String search, Model model) {

        // Get paginated products
        Page<Product> productPage = productService.getProductsWithPagination(page, size, search);

        // Add all necessary attributes to the model
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalProducts", productPage.getTotalElements());
        model.addAttribute("search", search);
        model.addAttribute("stats", productService.getDashboardStatistics());
        model.addAttribute("categoryData", productService.getProductsByCategory());
        model.addAttribute("lowStockProducts", productService.getLowStockProducts());


        return "dashboard";
    }

    @PostMapping("/api/upload-excel")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> uploadExcel(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "Please select a file to upload");
                return ResponseEntity.badRequest().body(response);
            }
// Check file type
            String fileName = file.getOriginalFilename();
            if (fileName != null && !(fileName.endsWith(".xlsx") || fileName.endsWith(".xls") || fileName.endsWith(".csv"))) {
                response.put("success", false);
                response.put("message", "Only Excel files (.xlsx, .xls, .csv) are allowed");
                return ResponseEntity.badRequest().body(response);
            }
// Process Excel file
            Map<String, Object> processResult = excelService.processExcelFile(file);
            response.put("success", processResult.get("success"));
            response.put("message", processResult.get("message"));
            response.put("data", processResult);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error processing file: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/api/download-template")
    public ResponseEntity<Resource> downloadTemplate() throws IOException {
        byte[] excelData = excelService.generateExcelTemplate();
        ByteArrayResource resource = new ByteArrayResource(excelData);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=products_template.xlsx").contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")).contentLength(excelData.length).body(resource);
    }

    @GetMapping("/api/products")
    @ResponseBody
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @DeleteMapping("/api/products/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            productService.deleteProduct(id);
            response.put("success", true);
            response.put("message", "Product deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error deleting product: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }


}

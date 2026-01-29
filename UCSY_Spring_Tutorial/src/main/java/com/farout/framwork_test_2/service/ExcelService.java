package com.farout.framwork_test_2.service;

import com.farout.framwork_test_2.model.Product;
import com.farout.framwork_test_2.repository.ProductRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

@Service
public class ExcelService   {
    @Autowired
    private ProductRepository productRepository;

    public Map<String, Object> processExcelFile(MultipartFile file) throws IOException {
        Map<String, Object> result = new HashMap<>();
        List<Product> products = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        int successCount = 0;
        int errorCount = 0;

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            // Skip header row
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }

            int rowNum = 2; // Excel row numbers start from 1, header is row 1
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                try {
                    Product product = parseProductFromRow(row);
                    // Validate product
                    if (isValidProduct(product)) {
                        // Check if product already exists
                        Optional<Product> existingProduct = productRepository.findByProductCode(product.getProductCode());

                        if (existingProduct.isPresent()) {
                            // Update existing product
                            Product existing = existingProduct.get();
                            updateProduct(existing, product);
                            productRepository.save(existing);
                        } else {
                            // Save new product
                            productRepository.save(product);
                        }

                        products.add(product);
                        successCount++;
                    } else {
                        errors.add("Row " + rowNum + ": Invalid product data");
                        errorCount++;
                    }
                } catch (Exception e) {
                    errors.add("Row " + rowNum + ": " + e.getMessage());
                    errorCount++;
                }
                rowNum++;
            }

            result.put("success", true);
            result.put("message", "File processed successfully");
            result.put("totalRows", rowNum - 2);
            result.put("successCount", successCount);
            result.put("errorCount", errorCount);
            result.put("errors", errors);
            result.put("products", products);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error processing file: " + e.getMessage());
        }
        return result;
    }

    private Product parseProductFromRow(Row row) {
        Product product = new Product();

        // Product Code (Column A)
        Cell cellA = row.getCell(0);
        if (cellA != null) {
            product.setProductCode(getCellValue(cellA));
        }

        // Product Name (Column B)
        Cell cellB = row.getCell(1);
        if (cellB != null) {
            product.setProductName(getCellValue(cellB));
        }

        // Category (Column C)
        Cell cellC = row.getCell(2);
        if (cellC != null) {
            product.setCategory(getCellValue(cellC));
        }

        // Description (Column D)
        Cell cellD = row.getCell(3);
        if (cellD != null) {
            product.setDescription(getCellValue(cellD));
        }

        // Price (Column E)
        Cell cellE = row.getCell(4);
        if (cellE != null) {
            try {

                BigDecimal price = new BigDecimal(getCellValue(cellE));
                product.setPrice(price);
            } catch (NumberFormatException e) {
                product.setPrice(BigDecimal.ZERO);
            }
        }

        // Quantity (Column F)
        Cell cellF = row.getCell(5);
        if (cellF != null) {
            try {
                int quantity = (int) Double.parseDouble(getCellValue(cellF));
                product.setQuantity(quantity);
            } catch (NumberFormatException e) {
                product.setQuantity(0);
            }
        }

        // Unit (Column G)
        Cell cellG = row.getCell(6);
        if (cellG != null) {
            product.setUnit(getCellValue(cellG));
        }

        // Supplier (Column H)
        Cell cellH = row.getCell(7);
        if (cellH != null) {
            product.setSupplier(getCellValue(cellH));
        }

        // Set status based on quantity
        if (product.getQuantity() != null) {
            if (product.getQuantity() == 0) {
                product.setStatus(Product.ProductStatus.OUT_OF_STOCK);
            } else if (product.getQuantity() <= 10) {
                product.setStatus(Product.ProductStatus.LOW_STOCK);
            } else {
                product.setStatus(Product.ProductStatus.IN_STOCK);
            }
        }
        return product;
    }

    private String getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // Remove trailing zeros
                    double value = cell.getNumericCellValue();
                    if (value == Math.floor(value)) {
                        return String.valueOf((int) value);
                    } else {
                        return String.valueOf(value);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    private boolean isValidProduct(Product product) {
        if (product.getProductCode() == null || product.getProductCode().isEmpty()) {
            return false;
        }
        if (product.getProductName() == null || product.getProductName().isEmpty()) {
            return false;
        }
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            return false;
        }
        return true;
    }

    private void updateProduct(Product existing, Product newData) {
        existing.setProductName(newData.getProductName());
        existing.setCategory(newData.getCategory());
        existing.setDescription(newData.getDescription());
        existing.setPrice(newData.getPrice());
        existing.setQuantity(newData.getQuantity());
        existing.setUnit(newData.getUnit());
        existing.setSupplier(newData.getSupplier());
        existing.setStatus(newData.getStatus());
    }

    // Generate Excel template for download
    public byte[] generateExcelTemplate() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Products Template");

        // Create header row
        Row headerRow = sheet.createRow(0);

        String[] headers = {
                "Product Code*", "Product Name*", "Category", "Description",
                "Price*", "Quantity*", "Unit", "Supplier"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            // Style for header
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            cell.setCellStyle(headerStyle);
        }

        // Add sample data row
        Row sampleRow = sheet.createRow(1);
        String[] sampleData = {
                "P001", "iPhone 14 Pro", "Electronics", "Latest Apple smartphone", "999.99", "50", "Piece", "Apple Inc"
        };

        for (int i = 0; i < sampleData.length; i++) {
            sampleRow.createCell(i).setCellValue(sampleData[i]);
        }

        // Auto size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Convert to byte array
        byte[] excelBytes;
        try (var outputStream = new java.io.ByteArrayOutputStream()) {
            workbook.write(outputStream);
            excelBytes = outputStream.toByteArray();
        }
        workbook.close();
        return excelBytes;
    }
}

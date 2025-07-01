package com.monarchsolutions.sms.service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monarchsolutions.sms.dto.coffee.CoffeeMenuDTO;
import com.monarchsolutions.sms.dto.coffee.CreateCoffeeMenuDTO;
import com.monarchsolutions.sms.dto.coffee.ProcessCoffeeSaleDTO;
import com.monarchsolutions.sms.dto.coffee.SaleGroupDTO;
import com.monarchsolutions.sms.dto.coffee.UpdateCoffeeMenuDTO;
import com.monarchsolutions.sms.dto.coffee.UserPurchasesDTO;
import com.monarchsolutions.sms.dto.common.PageResult;
import com.monarchsolutions.sms.repository.CoffeeRepository;

@Service
public class CoffeeService {
  
  // ← make this a String
  @Value("${app.coffee-dir}")
  private String coffeeDir;

  private final CoffeeRepository coffeeRepository;
  private final ObjectMapper objectMapper;

  public CoffeeService(CoffeeRepository coffeeRepository,
                        ObjectMapper objectMapper) {
    this.coffeeRepository = coffeeRepository;
    this.objectMapper     = objectMapper;
  }

  /**
   * Fetches the last 7 rows, then groups them by `sale`.
   * Each group has the shared `sale` and `created_at`,
   * a computed group total, and the list of line-items.
   */
  public List<SaleGroupDTO> getGroupedUserCoffeePurchases(Long userId, String lang) {
    List<UserPurchasesDTO> flat = coffeeRepository.getUserCoffeePurchases(userId, lang);

    // group by sale in insertion order
    var grouped = flat.stream()
      .collect(Collectors.groupingBy(
        UserPurchasesDTO::getSale,
        LinkedHashMap::new,
        Collectors.toList()
      ));

    List<SaleGroupDTO> result = new ArrayList<>(grouped.size());
    for (var entry : grouped.entrySet()) {
      Number sale = entry.getKey();
      List<UserPurchasesDTO> items = entry.getValue();

      // all items share the same created_at
      LocalDateTime createdAt = items.get(0).getCreated_at();

      // sum up the item-level totals
      BigDecimal groupTotal = items.stream()
        .map(dto -> dto.getTotal())
        .reduce(BigDecimal.ZERO, BigDecimal::add);

      result.add(new SaleGroupDTO(sale, createdAt, groupTotal, items));
    }

    return result;
  }

  @Transactional(readOnly = true)
  public PageResult<Map<String,Object>> getCoffeeSales(
    Long token_school_id,
    Long user_id,
    Long school_id,
    String full_name,
    String item_name,
    LocalDate created_at,
    String lang,
    int page,
    int size,
    Boolean exportAll,
    String order_by,
    String order_dir
  ) throws Exception {
    return coffeeRepository.getCoffeeSales(
      token_school_id,
      user_id,
      school_id,
      full_name,
      item_name,
      created_at,
      lang,
      page,
      size,
      exportAll,
      order_by,
      order_dir
    );
  }


  @Transactional(readOnly = true)
  public PageResult<Map<String,Object>> getCoffeeMenu(
		Long token_school_id,
		Long menu_id,
    String search_criteria,
    Boolean enabled,
    String lang,
    int page,
    int size,
    Boolean exportAll,
    String order_by,
    String order_dir
  ) throws Exception {
    return coffeeRepository.getCoffeeMenu(
      token_school_id,
      menu_id,
      search_criteria,
      enabled,
      lang,
      page,
      size,
      exportAll,
      order_by,
      order_dir
    );
  }

  public String updateCoffeeMenu(
    Long token_user_id, 
    Long menu_id, 
    String lang, 
    UpdateCoffeeMenuDTO request,
    boolean removeImage,
    MultipartFile newImage
  ) throws Exception {
    
    // 1) If removeImage, fetch menuData record & delete its file
    if (removeImage) {
      CoffeeMenuDTO  menuData = getMenuDetails(null ,menu_id);
      // String oldPath = (String) menuData.getImage();
      String oldPath = menuData.getImage();
      if (StringUtils.hasText(oldPath)) {
        Files.deleteIfExists(Paths.get(coffeeDir).resolve(oldPath));
      }
      request.setImage(null);
    }

    // 2) If there's a new file, save it exactly like in createCoffeeMenu()
    if (newImage != null && !newImage.isEmpty()) {
      String original = StringUtils.cleanPath(newImage.getOriginalFilename());
      String timestamp = LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
      // String storedName = menuData.getMenuId() + ".jpg";
      String storedName = timestamp + "_" + original;  // e.g. "20250623_142305_menu.jpg"

      Path dir = Paths.get(coffeeDir);
      Files.createDirectories(dir);
      try (InputStream in = newImage.getInputStream()) {
        Files.copy(in, dir.resolve(storedName), StandardCopyOption.REPLACE_EXISTING);
      }
      request.setImage(storedName);
    }

    // 3) Delegate to your repository — pass the DTO, not a JSON string
    return coffeeRepository.updateCoffeeMenu(
    token_user_id, 
    menu_id, 
    lang, 
    request,
    removeImage
    );
  }

  public String changeCoffeeMenuStatus(Long token_user_id, Long menu_id, String lang) throws Exception {
    String jsonResponse = coffeeRepository.changeCoffeeMenuStatus(token_user_id, menu_id, lang);
    return jsonResponse;
  }

  public CoffeeMenuDTO getMenuDetails(Long schoolId, Long menuId) {
    return coffeeRepository.findMenuDetails(schoolId, menuId);
  }

  public String processSale(
      Long tokenUserId,
      ProcessCoffeeSaleDTO sale,
      String lang
  ) throws Exception {
    return coffeeRepository.processCoffeeSale(tokenUserId, sale, lang);
  }

  public String createCoffeeMenu(
      Long tokenUserId,
      CreateCoffeeMenuDTO dto,
      MultipartFile image,
      String lang
  ) throws Exception {
    // 1) If there's an image, save it
    if (image != null && !image.isEmpty()) {
      // only JPEG
      if (!"image/jpeg".equals(image.getContentType())) {
        throw new IllegalArgumentException("Only JPG images allowed");
      }
      String original = StringUtils.cleanPath(image.getOriginalFilename());
      String timestamp = LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
      String filename = timestamp + "_" + original;  // e.g. "20250623_142305_menu.jpg"

      Path dir = Paths.get(coffeeDir);
      Files.createDirectories(dir);
      try (InputStream in = image.getInputStream()) {
        Files.copy(in, dir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
      }
      dto.setImage(filename);
    }

    // 2) Call the SP
    return coffeeRepository.createCoffeeMenu(tokenUserId, dto, lang);
  }


}

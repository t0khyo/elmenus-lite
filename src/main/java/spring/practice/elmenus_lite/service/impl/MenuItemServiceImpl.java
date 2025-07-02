package spring.practice.elmenus_lite.service.impl;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import spring.practice.elmenus_lite.dto.request.MenuItemCreateRequest;
import spring.practice.elmenus_lite.dto.request.MenuItemUpdateRequest;
import spring.practice.elmenus_lite.dto.response.MenuItemResponse;
import spring.practice.elmenus_lite.exception.DuplicateResourceException;
import spring.practice.elmenus_lite.exception.InvalidInputException;
import spring.practice.elmenus_lite.exception.ResourceNotFoundException;
import spring.practice.elmenus_lite.mapper.MenuItemMapper;
import spring.practice.elmenus_lite.model.Menu;
import spring.practice.elmenus_lite.model.MenuItem;
import spring.practice.elmenus_lite.model.Restaurant;
import spring.practice.elmenus_lite.repostory.MenuItemRepository;
import spring.practice.elmenus_lite.repostory.MenuRepository;
import spring.practice.elmenus_lite.repostory.RestaurantRepository;
import spring.practice.elmenus_lite.service.MenuItemService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@Slf4j
public class MenuItemServiceImpl implements MenuItemService {


    private final MenuItemRepository menuItemRepository;
    private final MenuRepository menuRepository; // To validate menu existence and relationships
    private final RestaurantRepository restaurantRepository; // To validate restaurant existence
    private final MenuItemMapper menuItemMapper;

    @Autowired
    public MenuItemServiceImpl(MenuItemRepository menuItemRepository,
                               MenuRepository menuRepository,
                               RestaurantRepository restaurantRepository,
                               MenuItemMapper menuItemMapper) {
        this.menuItemRepository = menuItemRepository;
        this.menuRepository = menuRepository;
        this.restaurantRepository = restaurantRepository;
        this.menuItemMapper = menuItemMapper;
    }

    @Override
    public Integer createMenuItem(Integer menuId, MenuItemCreateRequest request) {
        log.info("Attempting to create a new menu item for menu ID: {} with name: {}", menuId, request.name());

        // 1. Validate Menu existence
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found with ID: " + menuId));



        // 2. Validate Menu Item name uniqueness within this menu
        if (request.name() != null && menuItemRepository.findByNameAndMenuId(request.name(), menuId).isPresent()) {
            throw new DuplicateResourceException("Menu item with name '" + request.name() + "' already exists for menu ID: " + menuId);
        }

        // 3. Map DTO to Entity
        MenuItem menuItem = menuItemMapper.toMenuItemEntity(request);
        menuItem.setMenu(menu);


        // 4. Save the MenuItem entity
        MenuItem savedMenuItem = menuItemRepository.save(menuItem);
        log.info("Successfully created menu item with ID: {} for menu ID: {}", savedMenuItem.getId(), menuId);
        return savedMenuItem.getId();
    }

    @Override
    public Page<MenuItemResponse> getMenuItemsByRestaurantId(Integer restaurantId, String name, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        log.info("Retrieving menu items for restaurant ID: {} with filters - name: {}, minPrice: {}, maxPrice: {}",
                restaurantId, name, minPrice, maxPrice);

        // 1. Validate Restaurant existence
        restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + restaurantId));

        // 2. Build Specification for dynamic querying
        Specification<MenuItem> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Join to Menu and then to Restaurant to filter by restaurantId
            Join<MenuItem, Menu> menuJoin = root.join("menu", JoinType.INNER);
            Join<Menu, Restaurant> restaurantJoin = menuJoin.join("restaurant", JoinType.INNER);
            predicates.add(cb.equal(restaurantJoin.get("restaurantId"), restaurantId));

            if (name != null && !name.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // 3. Fetch menu items using the specification and pageable
        Page<MenuItem> menuItemsPage = menuItemRepository.findAll(spec, pageable);

        // 4. Map entities to DTOs
        return menuItemsPage.map(menuItemMapper::toMenuItemResponse);
    }

    @Override
    public Page<MenuItemResponse> searchMenuItems(String keyword, Pageable pageable) {
        log.info("Searching menu items with keyword: '{}' ", keyword);

        if (keyword == null || keyword.isBlank()) {
            throw new InvalidInputException("Search keyword cannot be empty.");
        }

        // The custom repository method handles the filtering by restaurantId and menuId directly in the query.
        Page<MenuItem> menuItemsPage = menuItemRepository.searchByKeyword(keyword, pageable);

        return menuItemsPage.map(menuItemMapper::toMenuItemResponse);
    }

    @Override
    public void updateMenuItem(Integer menuItemId, MenuItemUpdateRequest request) {
        log.info("Attempting to update menu item with ID: {}", menuItemId);

        // 1. Find the existing menu item
        MenuItem existingMenuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with ID: " + menuItemId));


        // 2. Business validation: Check for unique name if name is being updated
        if (request.name() != null && !request.name().isBlank() &&
                !request.name().equals(existingMenuItem.getName())) {
            // Check if the new name is already taken by another menu item in the SAME menu
            if (menuItemRepository.findByNameAndIdNotAndMenuId(
                    request.name(), menuItemId, existingMenuItem.getMenu().getId()).isPresent()) {
                throw new DuplicateResourceException("Menu item with name '" + request.name() + "' already exists for this menu.");
            }
            existingMenuItem.setName(request.name());
        }
        if (request.price() != null && request.price().compareTo(existingMenuItem.getPrice()) != 0) {
           existingMenuItem.setPrice(request.price());
        }

        // 3. Save the updated menu item entity
        menuItemRepository.save(existingMenuItem);
        log.info("Menu item with ID: {} updated successfully.", menuItemId);
    }

    @Override
    public void deleteMenuItem(Integer menuItemId) {
        log.info("Attempting to delete menu item with ID: {}", menuItemId);
        // 1. Find the existing menu item
        MenuItem menuItemToDelete = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with ID: " + menuItemId));

        // 2. Delete the menu item
        menuItemRepository.delete(menuItemToDelete);
        log.info("Menu item with ID: {} deleted successfully.", menuItemId);

    }
}

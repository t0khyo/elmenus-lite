package spring.practice.elmenus_lite.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spring.practice.elmenus_lite.dto.request.MenuCreateRequest;
import spring.practice.elmenus_lite.dto.request.MenuUpdateRequest;
import spring.practice.elmenus_lite.dto.response.MenuResponse;
import spring.practice.elmenus_lite.exception.DuplicateResourceException;
import spring.practice.elmenus_lite.exception.ResourceNotFoundException;
import spring.practice.elmenus_lite.mapper.MenuMapper;
import spring.practice.elmenus_lite.model.Menu;
import spring.practice.elmenus_lite.model.Restaurant;
import spring.practice.elmenus_lite.repostory.MenuRepository;
import spring.practice.elmenus_lite.repostory.RestaurantRepository;
import spring.practice.elmenus_lite.service.MenuService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {
    private final MenuRepository menuRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuMapper menuMapper;



    @Override
    public Integer createMenu(Integer restaurantId, MenuCreateRequest request) {
        log.info("Attempting to create a new menu for restaurant ID: {} with name: {}", restaurantId, request.name());

        // 1. Validate Restaurant existence
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + restaurantId));

        // 2. Validate Menu name uniqueness within this restaurant
        if (request.name() != null && menuRepository.findByNameAndRestaurantId(request.name(), restaurantId).isPresent()) {
            throw new DuplicateResourceException("Menu with name '" + request.name() + "' already exists for restaurant ID: " + restaurantId);
        }

        // 3. Map DTO to Entity
        Menu menu = menuMapper.toMenuEntity(request);
        menu.setRestaurant(restaurant); // Set the association

        // 4. Save the Menu entity
        Menu savedMenu = menuRepository.save(menu);
        log.info("Successfully created menu with ID: {} for restaurant ID: {}", savedMenu.getId(), restaurantId);
        return savedMenu.getId();
    }

    @Override
    public List<MenuResponse> getMenusByRestaurantId(Integer restaurantId) {
        log.info("Retrieving menus for restaurant ID: {}", restaurantId);

        // 1. Validate Restaurant existence
        restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + restaurantId));

        // 2. Fetch menus by restaurant ID
        List<Menu> menus = menuRepository.findByRestaurantId(restaurantId);

        // 3. Map entities to DTOs
        List<MenuResponse> responses = menus.stream()
                .map(menuMapper::toMenuResponse)
                .collect(Collectors.toList());

        log.info("Found {} menus for restaurant ID: {}", responses.size(), restaurantId);
        return responses;
    }

    @Override
    public void updateMenu(Integer menuId, MenuUpdateRequest request) {
        log.info("Attempting to update menu with ID: {}", menuId);

        // 1. Find the existing menu
        Menu existingMenu = menuRepository.findById(menuId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found with ID: " + menuId));

        // 2. Business validation: Check for unique name if name is being updated
        if (request.name() != null && !request.name().isBlank() &&
                !request.name().equals(existingMenu.getName())) {
            // Check if the new name is already taken by another menu in the SAME restaurant
            if (menuRepository.findByNameAndIdNotAndRestaurantId(
                    request.name(), menuId, existingMenu.getRestaurant().getId()).isPresent()) {
                throw new DuplicateResourceException("Menu with name '" + request.name() + "' already exists for this restaurant.");
            }
        }

        // 3. Update Name
        existingMenu.setName(request.name());

        // 4. Save the updated menu entity
        menuRepository.save(existingMenu);
        log.info("Menu with ID: {} updated successfully.", menuId);

    }

    @Override
    public void deleteMenu(Integer menuId) {
        log.info("Attempting to delete menu with ID: {}", menuId);

        Menu menuToDelete = menuRepository.findById(menuId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found with ID: " + menuId));

        menuRepository.delete(menuToDelete);
        log.info("Menu with ID: {} deleted successfully.", menuId);

    }
}

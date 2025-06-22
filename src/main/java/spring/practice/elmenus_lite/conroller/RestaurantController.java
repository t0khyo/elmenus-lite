package spring.practice.elmenus_lite.conroller;

import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.practice.elmenus_lite.dto.RestaurantRequest;
import spring.practice.elmenus_lite.dto.RestaurantResponse;
import spring.practice.elmenus_lite.exception.InvalidInputException;
import spring.practice.elmenus_lite.service.RestaurantService;

import java.sql.Time;
import java.util.List;

@RestController
@RequestMapping("api/v1/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @Autowired
    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }


    @PostMapping
    public ResponseEntity<Integer> addRestaurant(@RequestBody RestaurantRequest request) {
        // Basic validation for create operation
        if (request.name() == null || request.name().isBlank()) {
            throw new InvalidInputException("Restaurant name cannot be null or empty for creation.");
        }
        if (request.openTime() == null) {
            throw new InvalidInputException("Open time cannot be null for creation.");
        }
        if (request.closeTime() == null) {
            throw new InvalidInputException("Close time cannot be null for creation.");
        }

        Integer restaurantId = restaurantService.createRestaurant(request);
        return new ResponseEntity<>(restaurantId, HttpStatus.CREATED);
    }

    @PutMapping("/{restaurantId}")
    public ResponseEntity<Void> updateRestaurantProfile(@PathVariable Integer restaurantId,  @RequestBody RestaurantRequest request) {
        restaurantService.updateRestaurantProfile(restaurantId, request);
        return ResponseEntity.ok().build();
    }


    @GetMapping
    public ResponseEntity<List<RestaurantResponse>> getRestaurants(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Time time,
            @RequestParam(required = false) Integer minRating,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        List<RestaurantResponse> restaurants = restaurantService.getRestaurants(
                category, name, time, minRating, page, size);
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantResponse> getRestaurantById(@PathVariable Integer restaurantId) {
        RestaurantResponse restaurant = restaurantService.getRestaurantById(restaurantId);
        return ResponseEntity.ok(restaurant);
    }
    @GetMapping("/search")
    public ResponseEntity<List<RestaurantResponse>> searchRestaurants(@RequestParam @NotBlank(message = "Search keyword cannot be empty") String keyword) {
        List<RestaurantResponse> restaurants = restaurantService.searchRestaurants(keyword);
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("/top-rated")
    public ResponseEntity<List<RestaurantResponse>> getTopRatedRestaurants(@RequestParam(defaultValue = "10") Integer limit) {
        List<RestaurantResponse> restaurants = restaurantService.getTopRatedRestaurants(limit);
        return ResponseEntity.ok(restaurants);
    }

    @PostMapping("/{restaurantId}/categories/{categoryId}")
    public ResponseEntity<Void> assignCategoryToRestaurant(
            @PathVariable Integer restaurantId,
            @PathVariable Integer categoryId) {
        restaurantService.assignCategoryToRestaurant(restaurantId, categoryId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{restaurantId}/categories/{categoryId}")
    public ResponseEntity<Void> removeCategoryFromRestaurant(
            @PathVariable Integer restaurantId,
            @PathVariable Integer categoryId) {
        restaurantService.removeCategoryFromRestaurant(restaurantId, categoryId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/hi")
    public String sayHi() {
        return "Hi!";
    }
}

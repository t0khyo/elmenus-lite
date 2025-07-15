package spring.practice.elmenus_lite.conroller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.practice.elmenus_lite.dto.request.RestaurantRequest;
import spring.practice.elmenus_lite.dto.response.RestaurantResponse;
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
    public ResponseEntity<Void> addRestaurant(@RequestBody @Valid RestaurantRequest request) {
        // Basic validation for create operation
        request.validateTimes();
        restaurantService.addRestaurant(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{restaurantId}")
    public ResponseEntity<Void> updateRestaurantProfile(@PathVariable Integer restaurantId,  @RequestBody RestaurantRequest request) {
        request.validateTimes();
        restaurantService.updateRestaurantProfile(restaurantId, request);
        return ResponseEntity.ok().build();
    }


    @PostMapping
    public ResponseEntity<List<RestaurantResponse>> getRestaurantsByFilters(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Time time,
            @RequestParam(required = false) Integer minRating,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        List<RestaurantResponse> restaurants = restaurantService.getRestaurantsByFilters(
                category, name, time, minRating, page, size);
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantResponse> getRestaurant(@PathVariable Integer restaurantId) {
        RestaurantResponse restaurant = restaurantService.getRestaurant(restaurantId);
        return ResponseEntity.ok(restaurant);
    }
    @GetMapping("/search")
    public ResponseEntity<List<RestaurantResponse>> searchRestaurants(@RequestParam @NotBlank(message = "Search keyword cannot be empty") String keyword) {
        List<RestaurantResponse> restaurants = restaurantService.searchRestaurants(keyword);
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("/top-rated")
    public ResponseEntity<List<RestaurantResponse>> getTopRatedRestaurants() {
        List<RestaurantResponse> restaurants = restaurantService.getTopRatedRestaurants();
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

}

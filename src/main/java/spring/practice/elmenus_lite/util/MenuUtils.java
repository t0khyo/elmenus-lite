package spring.practice.elmenus_lite.util;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spring.practice.elmenus_lite.enums.ErrorMessage;
import spring.practice.elmenus_lite.model.Menu;
import spring.practice.elmenus_lite.model.MenuItem;
import spring.practice.elmenus_lite.repostory.MenuItemRepository;
import spring.practice.elmenus_lite.repostory.MenuRepository;

@Service
@RequiredArgsConstructor
public class MenuUtils {
    private final MenuItemRepository menuItemRepository;
    private final MenuRepository menuRepository;

    public Menu fetchAndValidateMenu(Integer menuId) {
        return menuRepository.findById(menuId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.MENU_NOT_FOUND.getFinalMessage(menuId)));
    }

    public MenuItem fetchAndValidateMenuItem(Integer menuItemId) {
        return menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.MENU_ITEM_NOT_FOUND.getFinalMessage(menuItemId)));
    }
}

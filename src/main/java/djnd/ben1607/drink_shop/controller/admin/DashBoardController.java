package djnd.ben1607.drink_shop.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import djnd.ben1607.drink_shop.service.DashBoardService;
import djnd.ben1607.drink_shop.utils.annotation.ApiMessage;

@RequestMapping("/api/v1/dashboard")
@RestController
public class DashBoardController {
    private final DashBoardService dashBoardService;

    public DashBoardController(
            DashBoardService dashBoardService) {
        this.dashBoardService = dashBoardService;
    }

    @GetMapping("")
    @ApiMessage("Count total users, orders and books")
    public ResponseEntity<?> getCountDashBoard() {
        return ResponseEntity.ok(this.dashBoardService.getCountDashBoard());
    }
}

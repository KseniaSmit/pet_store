package com.mirea.petshop.controllers;

import com.mirea.petshop.models.Product;
import com.mirea.petshop.models.User;
import com.mirea.petshop.services.CriteriaService;
import com.mirea.petshop.services.ProductService;
import com.mirea.petshop.services.TypeService;
import com.mirea.petshop.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/")
public class UserController {
    private final TypeService typeService;
    private final ProductService productService;
    private final CriteriaService criteriaService;
    private final UserService userService;

    public UserController(TypeService typeService, ProductService productService, CriteriaService criteriaService, UserService userService) {
        this.typeService = typeService;
        this.productService = productService;
        this.criteriaService = criteriaService;
        this.userService = userService;
    }
    @GetMapping
    public String index(@RequestParam(name = "typeId", required = false) Integer typeId,
                           Model model, Authentication authentication){
        String userRole = getUserRole(authentication);
        if (typeId == null) typeId = 0;
        model.addAttribute("userRole", userRole);
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("types", typeService.getAllTypes());
        model.addAttribute("typeId", typeId);
        return "UserController/index";
    }
    @GetMapping("/products")
    public String products(@RequestParam(name = "typeId", required = false) Integer typeId,
                           Model model, Authentication authentication){
        String userRole = getUserRole(authentication);
        if (typeId == null) typeId = 0;
        model.addAttribute("userRole", userRole);
        model.addAttribute("products", productService.getAllProductsByTypeId(typeId));
        model.addAttribute("types", typeService.getAllTypes());
        model.addAttribute("typeId", typeId);
        return "UserController/products";
    }
    @GetMapping("/product")
    public  String product(@RequestParam(name = "productId",required = false) Integer productId,
                           @RequestParam(name = "typeId", required = false) Integer typeId,
                           Model model, Authentication authentication){
        String userRole = getUserRole(authentication);
        if (productId == null) productId = 0;
        if (typeId == null) typeId = 0;
        model.addAttribute("userRole", userRole);
        model.addAttribute("products", productService.getAllProductsByTypeId(typeId));
        model.addAttribute("types", typeService.getAllTypes());
        model.addAttribute("typeId", typeId);
        Product product = productService.getProductById(productId);
        model.addAttribute("product", product);
        return "UserController/product";
    }
    @GetMapping("/search")
    public String searchProduct(@RequestParam(name ="name") String name,
                                Model model, Authentication authentication){
        String userRole = getUserRole(authentication);
        model.addAttribute("userRole", userRole);
        model.addAttribute("types", typeService.getAllTypes());
        model.addAttribute("products",criteriaService.getAllByName(name));
        return "UserController/search";
    }
    private String getUserRole(Authentication authentication) {
        if (authentication == null)
            return "GUEST";
        else
            return ((User)userService.loadUserByUsername(authentication.getName())).getRole();
    }
    @GetMapping("/sign")
    public String sign() {
        return "UserController/registration";
    }
    @PostMapping("/sign")
    public String signCreate(HttpServletRequest request,
                             @RequestParam(name = "email") String email,
                             @RequestParam(name = "username") String username,
                             @RequestParam(name = "password") String password,
                             Model model) {
        if (userService.loadUserByUsername(username) != null) {
            model.addAttribute("Status", "user_exists");
            return "UserController/registration";
        }
        else {
            userService.create(email,username,password);
            authWithHttpServletRequest(request, username, password);
            return "redirect:/";
        }
    }
    public void authWithHttpServletRequest(HttpServletRequest request, String username, String password) {
        try {
            request.login(username, password);
        } catch (ServletException e) { }
    }
}

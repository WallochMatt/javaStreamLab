package com.dcc.jpa_stream_lab.service;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dcc.jpa_stream_lab.repository.ProductsRepository;
import com.dcc.jpa_stream_lab.repository.RolesRepository;
import com.dcc.jpa_stream_lab.repository.ShoppingcartItemRepository;
import com.dcc.jpa_stream_lab.repository.UsersRepository;
import com.dcc.jpa_stream_lab.models.Product;
import com.dcc.jpa_stream_lab.models.Role;
import com.dcc.jpa_stream_lab.models.ShoppingcartItem;
import com.dcc.jpa_stream_lab.models.User;

@Transactional
@Service
public class StreamLabService {
	
	@Autowired
	private ProductsRepository products;
	@Autowired
	private RolesRepository roles;
	@Autowired
	private UsersRepository users;
	@Autowired
	private ShoppingcartItemRepository shoppingcartitems;


    // <><><><><><><><> R Actions (Read) <><><><><><><><><>

    public List<User> RDemoOne() {
    	// This query will return all the users from the User table.
    	return users.findAll().stream().toList();
    }

    public long RProblemOne()
    {
        // Return the COUNT of all the users from the User table.
        // You MUST use a .stream(), don't listen to the squiggle here!
        // Remember yellow squiggles are warnings and can be ignored.
    	return users.findAll().stream().count();
    }

    public List<Product> RDemoTwo()
    {
        // This query will get each product whose price is greater than $150.
    	return products.findAll().stream().filter(p -> p.getPrice() > 150).toList();
    }

    public List<Product> RProblemTwo()
    {
        // Write a query that gets each product whose price is less than or equal to $100.
        // Return the list
        return products.findAll().stream().filter(p -> p.getPrice() <= 100).toList();
    }

    public List<Product> RProblemThree()
    {
        // Write a query that gets each product that CONTAINS an "s" in the products name.
        // Return the list
    	return products.findAll().stream().filter(p -> p.getName().toLowerCase().contains("s")).toList();
    }

    public List<User> RProblemFour()
    {
        // Write a query that gets all the users who registered BEFORE 2016
        // Return the list
        // Research 'java create specific date' and 'java compare dates'
        // You may need to use the helper classes imported above!
    	Date b2016 = new Date(116, Calendar.JANUARY, 1);
        return users.findAll().stream().filter(u -> u.getRegistrationDate().before(b2016)).toList();
    }

    public List<User> RProblemFive()
    {
        // Write a query that gets all of the users who registered AFTER 2016 and BEFORE 2018
        // Return the list
        Date b2017 = new Date(117, Calendar.JANUARY, 1);
        Date b2018 = new Date(118, Calendar.JANUARY, 1);
        return users.findAll().stream().filter(u -> u.getRegistrationDate().before(b2018) && u.getRegistrationDate().after(b2017)).toList();
    }

    // <><><><><><><><> R Actions (Read) with Foreign Keys <><><><><><><><><>

    public List<User> RDemoThree()
    {
        // Write a query that retrieves all of the users who are assigned to the role of Customer.
    	Role customerRole = roles.findAll().stream().filter(r -> r.getName().equals("Customer")).findFirst().orElse(null);
    	List<User> customers = users.findAll().stream().filter(u -> u.getRoles().contains(customerRole)).toList();

    	return customers;
    }

    public List<Product> RProblemSix()
    {
        // Write a query that retrieves all of the products in the shopping cart of the user who has the email "afton@gmail.com".
        // Return the list
        User afton = users.findAll().stream().filter(r -> r.getEmail().equals("afton@gmail.com")).findFirst().orElse(null);
        List<ShoppingcartItem> productInCart = shoppingcartitems.findAll().stream().filter(uid -> uid.getUser().equals(afton)).toList();
        List<Product> theirItems = productInCart.stream().map((product) -> product.getProduct()).toList();

//        for(ShoppingcartItem item: productInCart){
//            System.out.println(item.getProduct().getName());
//        }

        return theirItems;
    }

    public long RProblemSeven()
    {
        // Write a query that retrieves all of the products in the shopping cart of the user who has the email "oda@gmail.com" and returns the sum of all of the products prices.
    	// Remember to break the problem down and take it one step at a time!
        User oda = users.findAll().stream().filter(r -> r.getEmail().equals("oda@gmail.com")).findFirst().orElse(null);
        List<ShoppingcartItem> inOdaCart = shoppingcartitems.findAll().stream().filter(product -> product.getUser().equals(oda)).toList();

        long total = 0;
        for(ShoppingcartItem p: inOdaCart){
            total += p.getProduct().getPrice();
        }
    	return total;

    }

    public List<Product> RProblemEight()
    {
        // Write a query that retrieves all of the products in the shopping cart of users who have the role of "Employee".
    	// Return the list
        Role getEmployee = roles.findAll().stream().filter(r -> r.getName().equals("Employee")).findFirst().orElse(null);
        List<User> allEmployees = users.findAll().stream().filter(u -> u.getRoles().contains(getEmployee)).toList();
        List<List<ShoppingcartItem>> allCarts = allEmployees.stream().map(u -> u.getShoppingcartItems()).toList();
        List<Product> allProducts = allCarts.stream().flatMap((c) -> c.stream().map(p -> p.getProduct())).toList();

    	return allProducts;
    }

    // <><><><><><><><> CUD (Create, Update, Delete) Actions <><><><><><><><><>

    // <><> C Actions (Create) <><>

    public User CDemoOne()
    {
        // Create a new User object and add that user to the Users table.
        User newUser = new User();        
        newUser.setEmail("david@gmail.com");
        newUser.setPassword("DavidsPass123");
        users.save(newUser);
        return newUser;
    }

    public Product CProblemOne()
    {
        // Create a new Product object and add that product to the Products table.
        // Return the product
        Product myProduct = new Product();
        myProduct.setName("Wrist Splints");
        myProduct.setDescription("Keep your wrists stable with these splints. Perfect for those programmer induced carpal tunnel issues");
        myProduct.setPrice(52);
    	products.save(myProduct);

    	return myProduct;

    }

    public List<Role> CDemoTwo()
    {
        // Add the role of "Customer" to the user we just created in the UserRoles junction table.
    	Role customerRole = roles.findAll().stream().filter(r -> r.getName().equals("Customer")).findFirst().orElse(null);
    	User david = users.findAll().stream().filter(u -> u.getEmail().equals("david@gmail.com")).findFirst().orElse(null);
    	david.addRole(customerRole);
    	return david.getRoles();
    }

    public ShoppingcartItem CProblemTwo()
    {
    	// Create a new ShoppingCartItem to represent the new product you created being added to the new User you created's shopping cart.
        // Add the product you created to the user we created in the ShoppingCart junction table.
        // Return the ShoppingcartItem
        User david = users.findAll().stream().filter(u -> u.getEmail().equals("david@gmail.com")).findFirst().orElse(null);
        Product wristSplints = products.findAll().stream().filter(p -> p.getName().equals("Wrist Splints")).findFirst().orElse(null);
        ShoppingcartItem newEntry = new ShoppingcartItem();
        newEntry.setUser(david);
        newEntry.setProduct(wristSplints);
        newEntry.setQuantity(2);
        shoppingcartitems.save(newEntry);

    	return newEntry;
    	
    }

    // <><> U Actions (Update) <><>

    public User UDemoOne()
    {
         //Update the email of the user we created in problem 11 to "mike@gmail.com"
          User user = users.findAll().stream().filter(u -> u.getEmail().equals("david@gmail.com")).findFirst().orElse(null);
          user.setEmail("mike@gmail.com");
          return user;
    }

    public Product UProblemOne()
    {
        // Update the price of the product you created to a different value.
        // Return the updated product
        Product updateSplints = products.findAll().stream().filter(p -> p.getName().equals("Wrist Splints")).findFirst().orElse(null);
        updateSplints.setPrice(65);
    	return updateSplints;
    }

    public User UProblemTwo()
    {
        // Change the role of the user we created to "Employee"
        // HINT: You need to delete the existing role relationship and then create a new UserRole object and add it to the UserRoles table

        Role customerRole = roles.findAll().stream().filter(r -> r.getName().equals("Customer")).findFirst().orElse(null);
        Role employeeRole = roles.findAll().stream().filter(r -> r.getName().equals("Employee")).findFirst().orElse(null);
        User mike = users.findAll().stream().filter(u -> u.getEmail().equals("mike@gmail.com")).findFirst().orElse(null);
        mike.removeRole(customerRole);
        mike.addRole(employeeRole);

    	return mike;
    }

    //BONUS:
    // <><> D Actions (Delete) <><>

    // For these bonus problems, you will also need to create their associated routes in the Controller file!
    
    // DProblemOne
    // Delete the role relationship from the user who has the email "oda@gmail.com".
    public String DProblemOne()
    {
        User oda = users.findAll().stream().filter(r -> r.getEmail().equals("oda@gmail.com")).findFirst().orElse(null);
        Role odaRole = roles.findAll().stream().filter(r -> r.getUsers().contains(oda)).findFirst().orElse(null);
        oda.removeRole(odaRole);
        return "Entry deleted";

    }
    // DProblemTwo
    // Delete all the product relationships to the user with the email "oda@gmail.com" in the ShoppingCart table.
    public String DProblemTwo()
    {
        User oda = users.findAll().stream().filter(r -> r.getEmail().equals("oda@gmail.com")).findFirst().orElse(null);
        List<ShoppingcartItem> odaCart = shoppingcartitems.findAll().stream().filter(sc -> sc.getUser().equals(oda)).toList();
        for(ShoppingcartItem item: odaCart){
            shoppingcartitems.delete(item);
        }
        return "Items removed";

    }

    // DProblemThree
    // Delete the user with the email "oda@gmail.com" from the Users table.
    public String DProblemThree()
    {
        User oda = users.findAll().stream().filter(r -> r.getEmail().equals("oda@gmail.com")).findFirst().orElse(null);
        users.delete(oda);

        return "User deleted";
    }
}

package Dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
//import java.util.Base64;
import java.util.List;
import java.util.Map;

import beans.Category;

public class CategoryDAO {
private Connection connection;

public CategoryDAO(){
}

public CategoryDAO(Connection con) {
	this.connection = con;
}

    public List<Category> findAllCategories() throws SQLException {
        List<Category> Categories = new ArrayList<Category>();
        

        try (PreparedStatement pstatement = connection.prepareStatement("SELECT * FROM tiw_categories.category");) {
            try (ResultSet result = pstatement.executeQuery();) {
                while (result.next()) {
                    Category C = new Category();
                    C.setId(result.getInt("id"));
                    C.setName(result.getString("name"));
                    C.setFather(result.getInt("father_id"));
                    
                    //byte[] imgData = result.getBytes("image");
                    //String encodedImg= Base64.getEncoder().encodeToString(imgData);
                    //C.setImage(encodedImg);

                    Categories.add(C);
                }
            }
        }
        
        return Categories;
    }

    public List<Category> findTopLevelCategories() throws SQLException{
    	int order=1;
    	List<Category> Categories = new ArrayList<Category>();
    	List<Category> topLevelCategories = new ArrayList<Category>();
    	
    	Categories = findAllCategories();
    	
    	 for (Category category : Categories) {
             if (category.getFather() == 0) {
                 category.setLevel(0);
                 category.setOrder(order);
                 order++;
                 topLevelCategories.add(category);
             }
             
         }

         for (Category parent : topLevelCategories) {
             parent.setSubcategories(getSubcategories(parent, Categories));
         }
         
         return topLevelCategories;
    	
    }
    


    private List<Category> getSubcategories(Category parent, List<Category> categories) {
        List<Category> subcategories = new ArrayList<>();
        int order=1;

        for (Category category : categories) {
            if (category.getFather()!=0 && category.getFather().equals(parent.getId())) {
                category.setLevel(parent.getLevel() + 1);
                
                category.setOrder(order);
                order++;
                
                if(parent.getIsRed()) category.setIsRed(true);
                
                subcategories.add(category);
                category.setSubcategories(getSubcategories(category, categories));
            }
        }

        return subcategories;
    }
    
    public void createCategory(String name, int father) throws SQLException {
		String query = "INSERT into tiw_categories.category (name, father_id) VALUES(?, ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
		
			pstatement.setString(1, name);
			pstatement.setInt(2, father);
			pstatement.executeUpdate();
		}
	}
    
    
    public void createCategoryNoFather(String name) throws SQLException {
		String query = "INSERT into tiw_categories.category (name) VALUES(?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
		
			pstatement.setString(1, name);
			pstatement.executeUpdate();
		}
	}
    
   
    public void insertListCategory(int father, List<Category> categories) throws SQLException{
    	Connection connection = null;
    	
    try {
    	
    	//String driver = "com.mysql.cj.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/tiw?serverTimezone=UTC";
		String user = "root";
		String password = "Gabrielegaia01";
		
		//Class.forName(driver);
		
		connection = DriverManager.getConnection(url, user, password);
    	
    	
  	   	connection.setAutoCommit(false);
		
  	     insertListCategoryDatabase(father, categories);
  	     
  	     connection.commit();
  	     
  	     
    }catch(SQLException e) {
    	if (connection != null) {
            connection.rollback();
        }
        throw e;
    } finally {
        if (connection != null) {
            connection.setAutoCommit(true);
            connection.close();
        }
    	
    }
}
			
		
    private void insertListCategoryDatabase(int father, List<Category> categories) throws SQLException{

    	List<Integer> precId = new ArrayList<>();
    	
    	Map<String, Integer> data = new HashMap<>();
    	
    	Category parent;
    	Category C;
    	
    	C = categories.get(0);
    	
    	if(father!=0) {
    		try {
    		createCategory(C.getName(), father);
    		}catch(SQLException e) {
    			
    		}
    	
    	}else {
    		
    		createCategoryNoFather(C.getName());
    		
    	}
    	
    	data.put(C.getName(), C.getId());
    	
    	precId.add(C.getId());
    	
    	//categories.remove(0);
    	
    	parent = findCategoryByNameAndFather(C.getName(), father);
    	

		for(Category category: categories) {
			
			if(category.getFather()==C.getId()) {
				
				createCategory(category.getName(), parent.getId());
				
				parent = findCategoryByNameAndFather(category.getName(), parent.getId());
				
				data.put(parent.getName(), parent.getId());
				
				C = category;
				
				precId.add(category.getId());
				
			}else {
				
				for(int p : precId) {
					
					if(p==category.getFather()) {
						
						Category temp = findCategoryById(p);
						
						int f = data.get(temp.getName());
						
						createCategory(category.getName(), f);
						
						C = category;
						
					}
					
				}
				
				precId.add(category.getId());
			}
			
			
		}
    	
    	
    	
    }

		
    	 
    public List<Category> setFlag(Category father) throws SQLException{
    	int order=1;
    	boolean isRed=true;
    	List<Category> Categories = new ArrayList<Category>();
    	List<Category> topLevelCategories = new ArrayList<Category>();
    	
    	Categories = findAllCategories();
    	
    	 for (Category category : Categories) {
             if (category.getFather() == 0) {
                 category.setLevel(0);
                 category.setOrder(order);
                 order++;
                 
                 if(category.getId()==father.getId()) category.setIsRed(isRed);
                 
                 topLevelCategories.add(category);
             }
             
         }

         for (Category parent : topLevelCategories) {
             parent.setSubcategories(setFlagSubcategories(father, parent, Categories));
         }
         
         return topLevelCategories;
    }
    
    
    public List<Category> categoriesToInsert(List<Category> categories){
    	
    	List<Category> ToInsert = new ArrayList<>();
    	List<Category> subToInsert = new ArrayList<>();
    	
    	for(Category category: categories) {
    		if(category.getIsRed()) {
    			ToInsert.add(category);
    		}
    		
    		subToInsert = categoriesToInsert(category.getSubcategories());
    		
    		
    		for(Category a: subToInsert) {
    		ToInsert.add(a);
    		
    		}
    		
    	}
    	
    	return ToInsert;
    }
    
    public List<Category> setFlagSubcategories(Category flag, Category parent, List<Category> categories){
    	 List<Category> subcategories = new ArrayList<>();
         int order=1;

         for (Category category : categories) {
             if (category.getFather()!=0 && category.getFather().equals(parent.getId())) {
                 category.setLevel(parent.getLevel() + 1);
                 
                 category.setOrder(order);
                 order++;
                 
                 if(parent.getIsRed()) category.setIsRed(true);
              
                 if(flag.getId()==category.getId()) category.setIsRed(true);
                 
                 subcategories.add(category);
                 category.setSubcategories(setFlagSubcategories(flag, category, categories));
             }
         }

         return subcategories;
    }
    
    
    public Category findCategoryById(int id) throws SQLException{
    	Category category = null;
    	
    	String query = "SELECT * FROM tiw_categories.category WHERE id = ?";
    	

    	
    	try(PreparedStatement pstatement = connection.prepareStatement(query);){
    	       pstatement.setInt(1, id);
    	       
    	    try (ResultSet result = pstatement.executeQuery();){
    	    
    	       while (result.next()) {
    	    	   
    	    	   Category C = new Category();
    	    	   
    	    	   C.setId(result.getInt("id"));
                   C.setName(result.getString("name"));
                   C.setFather(result.getInt("father_id"));
                   
                   category = C;
    	    	    
    	       }
    	          }
    	    }
    	
		return category;
    	
    }
    
    public Category findCategoryByNameAndFather(String name, int father) throws SQLException{
    	
    	Category category = null;
    	
    	String query = "SELECT * FROM tiw_categories.category WHERE name = ? AND father_id= ? ";
    	

    	
    	try(PreparedStatement pstatement = connection.prepareStatement(query);){
    	       pstatement.setString(1, name);
    	       pstatement.setInt(2, father);
    	       
    	    try (ResultSet result = pstatement.executeQuery();){
    	    
    	       while (result.next()) {
    	    	   
    	    	   Category C = new Category();
    	    	   
    	    	   C.setId(result.getInt("id"));
                   C.setName(result.getString("name"));
                   C.setFather(result.getInt("father_id"));
                   
                   category = C;
    	    	    
    	       }
    	          }
    	    }
    	
		return category;
    	
    }
    
    public List<Category> findSons(int father) throws SQLException{
    	
    	List<Category> sons = new ArrayList<>();
    	
    	String query = "SELECT * FROM tiw_categories.category WHERE father_id= ? ";
    	
    	try(PreparedStatement pstatement = connection.prepareStatement(query);){
 	       
 	       pstatement.setInt(1, father);
 	       
 	    try (ResultSet result = pstatement.executeQuery();){
 	    
 	       while (result.next()) {
 	    	   
 	    	   Category C = new Category();
 	    	   
 	    	   C.setId(result.getInt("id"));
               C.setName(result.getString("name"));
               C.setFather(result.getInt("father_id"));
                
               sons.add(C);
 	    	    
 	       }
 	          }
 	    }
 	
		return sons;
    	
    	
    }
    
    
    public void updateCategoryName(int id, String newName) throws SQLException {
    	
    	String query = "UPDATE tiw_categories.category SET name = ? WHERE id = ?";
    	
    	try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, newName);
            statement.setInt(2, id);
            
            int rowsUpdated = statement.executeUpdate();
            
            if (rowsUpdated == 0) {
                throw new SQLException("Failed to update category name. Category with ID " + id + " not found.");
            }
        }
    	
    	
    }
    
     
    public boolean checkOrder(int father) throws SQLException{
    	
    	List<Category> topLevel = new ArrayList<>();
    	
    	topLevel = findTopLevelCategories();
    	
    	int size = topLevel.size();
    	
    	if(father==0) {
    		
    		if(size==9) return false;
    		
    	}else {
    		
    		
    		List<Category> subcategories = findSons(father);
    		int num = subcategories.size();
    		
    		if(num==9) return false;
    		
    	}
    	
    	
    	return true;
    }


}

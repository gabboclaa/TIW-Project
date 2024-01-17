/**
 * 
 */

function generateCategoryTreeHTML(categories, callback) {
  var html = '';
  
  var rootOrder = 1;
  categories.forEach(function(category) {
	
    html += generateCategoryNodeHTML(category, rootOrder);
    rootOrder++;
  });
  
  // Chiamata alla callback con l'HTML generato
  if (typeof callback == 'function') {
    callback(html);
  }
}

function generateCategoryNodeHTML(category, order) {
var html = '<li draggable="true">';
  
  html += '<span>' + order + ' ' + category.name + '</span>';
  
  if (category.subcategories.length > 0) {
    html += '<ul style="list-style-type: none; padding: 0; margin: 0;">';
    
    category.subcategories.forEach(function(subcategory, index) {
      var subcategoryOrder = order + '.' + (index + 1); // Calcola l'ordine della sottocategoria
      
      html += generateCategoryNodeHTML(subcategory, subcategoryOrder);
    });
    
    html += '</ul>';
  }
  
  html += '</li>';
  
  return html;
  }


 function findCategoryById(categoryId, callback) {
  // Effettua una richiesta AJAX al server per ottenere i dati della categoria con l'ID specificato
  var xhr = new XMLHttpRequest();
  xhr.open('GET', './AskCategory?categoryId=' + categoryId, true);
  xhr.onreadystatechange = function() {
    if (xhr.readyState === 4 && xhr.status === 200) {
      var category = JSON.parse(xhr.responseText);
      callback(category); // Restituisci i dati della categoria utilizzando la callback
    
	}
  };
  xhr.send();
}


function copySubtree(categoryId, targetCategoryId, callback) {
  // Trova la categoria radice del sottoalbero
  findCategoryById(categoryId, function(rootCategory) {
    // Copia il sottoalbero nel lato client dell'albero
    var copiedSubtree = rootCategory;
    
    //console.log(copiedSubtree);

    // Aggiungi il sottoalbero copiato alla categoria di destinazione nel lato client dell'albero
    findCategoryById(targetCategoryId, function(targetCategory) {
		  
		targetCategory.subcategories.push(copiedSubtree);
		  
		console.log(targetCategory);
		  
		// Crea un array che include le categorie originali insieme alla categoria di destinazione aggiornata
        // Ottieni le categorie aggiornate
      	getUpdatedCategories(targetCategory, function(updatedCategories) {
			  
			 //console.log(updatedCategories);
			 
			 callback(updatedCategories);

      });
       
      
    });
  });
  
}

function copySubtreeAtTheEnd(categoryId, callback) {
  // Trova la categoria radice del sottoalbero
  findCategoryById(categoryId, function(rootCategory) {
    // Copia il sottoalbero nel lato client dell'albero
    var copiedSubtree = rootCategory;
    
    console.log(copiedSubtree);


// Crea un array che include le categorie originali insieme alla categoria di destinazione aggiornata
// Ottieni le categorie aggiornate
getUpdatedCategoriesAtTheEnd(copiedSubtree, function(updatedCategories) {
			  
	console.log(updatedCategories);
			 
	callback(updatedCategories);

      });
       
      
    
  });
  
}

function getUpdatedCategoriesAtTheEnd(copiedSubtree, callback){
	
	getUpdatedTopData(function(categories){
	
	var updatedCategories = categories.concat(copiedSubtree);
	//console.log(updatedCategories);
	
	callback(updatedCategories);
	
	});
	
}

function getUpdatedCategories(targetCategory, callback){
	
	getUpdatedTopData(function(categories){
	
	var updatedCategories = replaceCategory(targetCategory, categories);
	//console.log(updatedCategories);
	
	callback(updatedCategories);
	
	});
	
}

function replaceCategory(newCategory, categories) {
  var updatedCategories = categories.map(function(category) {
    if (category.id === newCategory.id) {
      return newCategory;
    } else if (category.subcategories.length > 0) {
      return {
        id: category.id,
        name: category.name,
        father_id: category.father_id,
        subcategories: replaceCategory(newCategory, category.subcategories)
      };
    } else {
      return category;
    }
  });

  return updatedCategories;
}

// Funzione per ottenere i nuovi dati aggiornati per il categoriesContainer
function getUpdatedTopData(callback) {
  var xhr = new XMLHttpRequest();
  xhr.open('GET', 'Top', true);
  xhr.onload = function() {
    if (xhr.status === 200) {
      var response = JSON.parse(xhr.responseText);
      
      callback(response);
      
    }
  };
  xhr.send();
}

















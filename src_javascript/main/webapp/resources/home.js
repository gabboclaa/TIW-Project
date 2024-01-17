/**
 * 
 */

{

// Funzione ricorsiva per generare la struttura ad albero delle categorie
function generateCategoryTree(categories, parentElement, parentOrder = '') {
  var ul = document.createElement('ul');
  ul.style.margin="10px";
  ul.style.padding ="0";
  
  // Aggiungi l'ID della categoria di destinazione come attributo personalizzato all'elemento <ul>
  ul.setAttribute('data-category-id', parentElement.getAttribute('data-category-id'));
 

  
  categories.forEach(function(category) {
    var li = document.createElement('li');
    li.draggable = true;
    
    // Assegna l'ID della categoria direttamente come proprietà personalizzata
	li.categoryId = category.id;

	// Imposta l'attributo data-category-id con l'ID della categoria
	li.setAttribute('data-category-id', category.id);
    
    
    // EventListener per l'inzio dell'evento di drag
    li.addEventListener('dragstart', function(event) {
  	// Trova l'elemento li corretto a partire dall'evento di dragstart
      var draggedLi = event.target.closest('li');
      
      console.log(draggedLi);

      // Salva l'ID della categoria trascinata nei dati del trascinamento
      event.dataTransfer.setData('text/plain', draggedLi.dataset.categoryId);
  	
	});
	
	li.addEventListener('dragenter', function(event) {
  	event.preventDefault();
  	// Aggiungi una classe CSS per indicare che l'elemento è in una posizione valida per il drop
  	li.classList.add('valid-drop-target');
	});
	
	li.addEventListener('dragover', function(event) {
  	event.preventDefault();
  	
  	ul.classList.remove("valid-drop-target");
	});

	li.addEventListener('dragleave', function(event) {
  	// Rimuovi la classe CSS per indicare che l'elemento non è più una posizione valida per il drop
  	li.classList.remove('valid-drop-target');
	});

	var confirmed = false;
	
	// EventListener per l'evento di drop
	li.addEventListener('drop', function(event) {
  	
  	event.preventDefault();
  	
  	// Blocca la propagazione dell'evento agli elementi genitori
  	event.stopPropagation();
  
  	// Ottieni l'ID della categoria trascinata dai dati del trascinamento
  	var categoryId = event.dataTransfer.getData('text/plain');
  	
  	// Ottieni l'ID della categoria di destinazione
  	var targetCategoryId = category.id;
  	
  	console.log(targetCategoryId);
  	
  	
  	// Verifica se la conferma è stata già effettuata
    if (!confirmed) {
        // Mostra la finestra di dialogo di conferma solo se non è stata ancora confermata
        confirmed = confirm('Confermi la copia del sottoalbero?');
    }

    // Continua con il resto del codice solo se la conferma è stata effettuata
    if (confirmed) {
		
		removeErrorMsg();
		 
   	 	// Utilizzo della funzione copySubtree e passaggio delle categorie modificate alla funzione generateCategoryTreeHTML
  		copySubtree(categoryId, targetCategoryId, function(updatedCategories) {
		  
    	generateCategoryTreeHTML(updatedCategories, function(updatedHTML) {
      		// Utilizza l'HTML generato come desiderato
      		console.log(updatedHTML);
      		
      		var parentElement = document.getElementById('CategoriesContainer');
      
      		// Aggiorna il contenuto HTML dell'elemento con l'HTML aggiornato
      		parentElement.innerHTML = updatedHTML;
    	});
  	});
    
    	var button = document.getElementById('button');
    
    	generateButton(button,categoryId, targetCategoryId);
    	
    	// Mostra il bottone "Salva"
    	showSaveButton();
    	
  	} else {
		  
		  removeErrorMsg();
    	// Ripristina lo stato precedente al drag & drop
    	getUpdatedTopData();
    	removeErrorMsg();
  	}
  	
	});
    
    var span = document.createElement('span');
    var categoryOrder = parentOrder + category.order + '.';
    span.textContent = categoryOrder + ' ' + category.name;
    span.value = category.id;
    
     // Aggiungi l'evento di click al tag <span> per consentire la modifica del nome
    span.addEventListener('click', function() {
      var input = document.createElement('input');
      input.type = 'text';
      input.value = category.name;
      
      // Aggiungi l'evento di blur al campo di input per salvare le modifiche nel database
      input.addEventListener('blur', function() {
        var newName = input.value;
        
        if(category.name!=newName){
			
			if(/\d/.test(newName) || newName.length>=45){
				
				//messaggio di errore
				var msg = "Non puoi modificare la categoria con questo nome";
				generateErrorMsg(msg);
				getUpdatedTopData();
			
			}else{
        		// Esegui la richiesta AJAX per salvare il nuovo nome nel database
        		saveCategoryName(category.id, newName);
        		removeErrorMsg();
        	}
        
        }else{
			
			getUpdatedTopData();
			removeErrorMsg();
		
		}
       
        // Sostituisci il campo di input con il nuovo nome nella visualizzazione
        span.textContent = category.order + ' ' + newName;
        span.value = category.id;
      });
      
      // Sostituisci il tag <span> con il campo di input
      li.replaceChild(input, span);
      
      // Imposta il focus sul campo di input
      input.focus();
    });
    
    // Aggiungi l'eventuale logica per l'aggiunta di un elemento <select> e le relative opzioni
    
    li.appendChild(span);
    
    if (category.subcategories.length > 0) {
      var subcategoryUl = generateCategoryTree(category.subcategories, li, categoryOrder);
      li.appendChild(subcategoryUl);
    }
    
    ul.appendChild(li);
    
  
 });
 
 // Se è l'ultimo elemento <ul>, aggiungi l'elemento "Drop qui"
  if (parentElement === document.getElementById('CategoriesContainer')) {
    var dropHereLi = document.createElement('li');
    dropHereLi.textContent = 'Drop qui';
    dropHereLi.classList.add('drop-here');
    
    dropHereLi.addEventListener('dragenter', function (event) {
      event.preventDefault();
      // Aggiungi una classe CSS per indicare che l'elemento è in una posizione valida per il drop
      dropHereLi.classList.add('valid-drop-target');
    });

    dropHereLi.addEventListener('dragover', function (event) {
      event.preventDefault();
      ul.classList.remove("valid-drop-target");
    });

    dropHereLi.addEventListener('dragleave', function (event) {
      // Rimuovi la classe CSS per indicare che l'elemento non è più una posizione valida per il drop
      dropHereLi.classList.remove('valid-drop-target');
    });

    // EventListener per l'evento di drop sull'elemento "Drop qui"
    dropHereLi.addEventListener('drop', function(event) {
      event.preventDefault();
      event.stopPropagation();

	// Ottieni l'ID della categoria trascinata dai dati del trascinamento
  	var categoryId = event.dataTransfer.getData('text/plain');
      
     // Imposta targetCategoryId come undefined per indicare il drop alla fine
     var targetCategoryId = 0;
     
     console.log(categoryId);

	var confirmed = false;
      // Resto del codice per gestire il drop alla fine della classifica (uguale a prima)
      // Verifica se la conferma è stata già effettuata
    if (!confirmed) {
        // Mostra la finestra di dialogo di conferma solo se non è stata ancora confermata
        confirmed = confirm('Confermi la copia del sottoalbero?');
    }

    // Continua con il resto del codice solo se la conferma è stata effettuata
    if (confirmed) {
		
		removeErrorMsg();
		 
   	 	// Utilizzo della funzione copySubtree e passaggio delle categorie modificate alla funzione generateCategoryTreeHTML
  	copySubtreeAtTheEnd(categoryId, function(updatedCategories) {
		  
    generateCategoryTreeHTML(updatedCategories, function(updatedHTML) {
     // Utilizza l'HTML generato come desiderato
     //console.log(updatedHTML);
      		
      var parentElement = document.getElementById('CategoriesContainer');
      
      // Aggiorna il contenuto HTML dell'elemento con l'HTML aggiornato
      parentElement.innerHTML = updatedHTML;
    	});
  	});
    
    var button = document.getElementById('button');
    
    generateButton(button,categoryId, targetCategoryId);
    	
    // Mostra il bottone "Salva"
    showSaveButton();
  	} else {
		  
    // Ripristina lo stato precedente al drag & drop
    getUpdatedTopData();
    removeErrorMsg();
  	}
      
    });

    ul.appendChild(dropHereLi);
  }


  parentElement.appendChild(ul);
  
  return ul;
}	


// Funzione per salvare il nuovo nome della categoria nel database
function saveCategoryName(categoryId, newName) {
  // Effettua una richiesta AJAX per salvare il nuovo nome nel database
  var xhr = new XMLHttpRequest();
  xhr.open('POST', 'Update', true); // Sostituisci con l'URL corretto per raggiungere la tua servlet per la modifica del nome
  xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
  xhr.onreadystatechange = function() {
    if (xhr.readyState === 4 && xhr.status === 200) {
      console.log('Nome categoria modificato con successo nel database');
      
      // Dopo aver salvato il nome nel database, effettua una chiamata AJAX per ottenere i nuovi dati aggiornati
      getUpdatedTopData();
      getUpdatedFormData();
    }else{
		
		//var msg = "C'è stato un errore!";
		//generateErrorMsg(msg);
		
	}
  };
  var params = 'categoryId=' + encodeURIComponent(categoryId) + '&newName=' + encodeURIComponent(newName);
  xhr.send(params);
}

// Funzione per ottenere i nuovi dati aggiornati per il categoriesContainer
function getUpdatedTopData() {
  var xhr = new XMLHttpRequest();
  xhr.open('GET', 'Top', true);
  xhr.onload = function() {
    if (xhr.status === 200) {
      var response = JSON.parse(xhr.responseText);
      console.log(response);
      
      // Aggiorna il contenuto HTML con i nuovi dati ottenuti
      var CategoriesContainer = document.getElementById('CategoriesContainer');
      CategoriesContainer.innerHTML = ''; // Svuota il contenuto precedente
      
      // Genera la struttura ad albero delle categorie utilizzando i nuovi dati
      generateCategoryTree(response, CategoriesContainer);
    }
  };
  xhr.send();
}

// Funzione per ottenere i nuovi dati aggiornati per il form
function getUpdatedFormData() {
  var xhr = new XMLHttpRequest();
  xhr.open('GET', 'AllCategories', true); // Sostituisci con l'URL corretto per raggiungere la tua servlet per ottenere i dati aggiornati
  xhr.onload = function() {
	  if(xhr.readyState === 4){
    if (xhr.status === 200) {
      var response = JSON.parse(xhr.responseText);
      console.log(response);
      
      // Aggiorna il contenuto HTML con i nuovi dati ottenuti
      var formContainer = document.getElementById('formContainer');
      formContainer.innerHTML = ''; // Svuota il contenuto precedente
      
      // Genera la struttura ad albero delle categorie utilizzando i nuovi dati
      generateCategoryForm(response, formContainer);
    }else if(xhr.status === 401){
		
		//msg = "C'è stato un errore!";
		//generateErrorMsg(msg);
		
	}
	}
  };
  xhr.send();
}




function moveCategory(categoryId, targetCategoryId){
	
	// Effettua una richiesta AJAX per spostare la categoria nel database
  	var xhr = new XMLHttpRequest();
  	xhr.open('POST', 'MoveCategory', true); // Sostituisci con l'URL corretto per raggiungere la tua servlet per lo spostamento della categoria
  	xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
  	xhr.onreadystatechange = function() {
    if (xhr.readyState === 4 ) {
		if(xhr.status === 200){
      	console.log('Categoria spostata con successo nel database');
      
      	// Dopo lo spostamento della categoria, effettua una chiamata AJAX per ottenere i nuovi dati aggiornati
      	getUpdatedTopData();
      	getUpdatedFormData();
    	}else if(xhr.status === 401){
		
		console.log('Categoria non inserita');
		var msg = "Non puoi inserire la categoria in questo punto! (order max = 9)";
		getUpdatedTopData();
      	getUpdatedFormData();
      	generateErrorMsg(msg);
	}
	}
  	};
  	
  var params = 'categoryId=' + encodeURIComponent(categoryId) + '&targetCategoryId=' + encodeURIComponent(targetCategoryId);
  xhr.send(params);
	
	
}


function showSaveButton() {
  var saveButton = document.getElementById('saveButton');
  //saveButton.style.position = 'absolute';
  saveButton.style.right = '5px';
  saveButton.style.display = 'block';
}


function generateButton(parentElement, categoryId, targetCategoryId){
	
	var saveButton = document.createElement('button');
	saveButton.setAttribute('id', 'saveButton');
	saveButton.textContent = 'SALVA';
	saveButton.style.display = 'none';
	
	saveButton.addEventListener('click', function() {
  		moveCategory(categoryId, targetCategoryId);
  		saveButton.removeEventListener('click', arguments.callee); // Rimuove l'evento di click
    	saveButton.remove(); // Rimuove il bottone dal DOM
	});
	
	parentElement.appendChild(saveButton);

	
}

function generateErrorMsg(message){
	
	var error = document.getElementById("errorMsg");
	//var msg = document.createElement('span');
	error.textContent = message;
	//error.appendChild(msg);
	
}

function removeErrorMsg(){
	var error = document.getElementById("errorMsg");
	error.textContent ='';
}


function generateCategoryForm(categories, parentElement){
	
	// Creazione del form
    var form = document.createElement("form");
    form.setAttribute("id", "myForm");
    form.setAttribute("action", "Create");
    form.setAttribute("method", "POST");
    form.style.backgroundColor = "lightgray";
	form.style.padding = "20px";

    // Creazione dell'elemento di input per il nome
    var nameLabel = document.createElement("label");
    nameLabel.textContent = "Nome categoria: ";
    var nameInput = document.createElement("input");
    nameInput.setAttribute("type", "text");
    nameInput.setAttribute("name", "name");
    nameInput.style.width = "200px";
	nameInput.style.padding = "5px";
	nameInput.style.marginBottom = "10px";
	nameInput.style.marginRight = "10px";
	nameInput.style.marginLeft = "5px";
    
    // Creazione della barra di selezione per il nome della categoria padre
    var parentLabel = document.createElement("label");
    parentLabel.textContent = "Categoria Padre: ";
    var parentSelect = document.createElement("select");
    parentSelect.setAttribute("name", "parentCategory");
    parentSelect.style.width = "200px";
	parentSelect.style.padding = "5px";
	parentSelect.style.marginBottom = "10px";
	parentSelect.style.marginLeft = "5px";
	
	// Popola la select con i dati ottenuti
    categories.forEach(function(category) { 
     var option = document.createElement("option");  
     option.textContent = category.name;
     option.value = category.id;
     parentSelect.appendChild(option);
     });
     
     
    // Aggiunta dell'etichetta e della barra di selezione al form
    form.appendChild(nameLabel);
    form.appendChild(nameInput);
    form.appendChild(parentLabel);
    form.appendChild(parentSelect);

    // Creazione del pulsante di invio
    var submitButton = document.createElement("input");
    submitButton.setAttribute("type", "submit");
    submitButton.setAttribute("value", "Invia");
    submitButton.style.padding="10px";
    submitButton.style.backgroundColor="#4CAF50";
    submitButton.style.color="#fff";
    submitButton.style.border="none";
    submitButton.style.borderRadius="5px";
    submitButton.style.cursor="pointer";
    submitButton.style.marginLeft="10px";
    
    

    // Aggiunta del pulsante di invio al form
    form.appendChild(submitButton);

    // Aggiunta del form al contenitore
    parentElement.appendChild(form);
    
   // Rimuovi il gestore di eventi submit esistente, se presente
	form.removeEventListener('submit', handleSubmit);

	// Aggiungi il gestore di eventi submit
	// Aggiungi il gestore di eventi submit
  form.addEventListener('submit', function(event) {
    handleSubmit(event, form); // Passa form come parametro alla funzione handleSubmit
  });
    
   
}


 // Funzione di gestione dell'evento di invio del form
function handleSubmit(event, form) { 		
	event.preventDefault(); // Evita l'invio del form normale

  	// Ottieni i dati dal modulo utilizzando FormData
  var formData = new FormData(form);
  var nameValue = formData.get('name');
  var parentValue = formData.get('parentCategory');

  // Crea l'oggetto formData
  var data = {
    name: nameValue,
    parentCategory: parentValue
  };
  // Converte l'oggetto in una stringa JSON
  var jsonData = JSON.stringify(data);
  	
  	console.log(data);

  	// Invia la richiesta AJAX alla servlet
  	var xhr = new XMLHttpRequest();
  	xhr.open(form.getAttribute('method'), form.getAttribute('action'), true);
  	xhr.setRequestHeader('Content-type', 'application/json');
  	xhr.onreadystatechange = function() {
    if (xhr.readyState === 4 ) {
	if(xhr.status === 200){
     // La richiesta è stata completata con successo
      console.log(xhr.responseText);
      	
      // Dopo aver creato la nuova categoria nel database, effettua una chiamata AJAX per ottenere i nuovi dati aggiornati
      getUpdatedTopData();
      getUpdatedFormData();
      }else if(xhr.status === 401){
		  
		 console.log('Categoria non inserita');
		var msg = "Non puoi creare la categoria (order max=9)";
		getUpdatedTopData();
      	getUpdatedFormData();
      	generateErrorMsg(msg);
		  
	  }
   	}
  	};
 	xhr.send(jsonData);
 }

// Effettua una richiesta AJAX per ottenere le categorie padre dal server
var xhr = new XMLHttpRequest();
xhr.open("GET", "Top", true); 
xhr.onload = function() {
	if (xhr.readyState === 4 ) {
  if (xhr.status === 200) {
    var response = JSON.parse(xhr.responseText);
    console.log(response);
    
    // Genera la struttura ad albero delle categorie utilizzando i dati ottenuti
    var CategoriesContainer = document.getElementById('CategoriesContainer');
    
    //CategoriesContainer.style.widht = "auto";
    //CategoriesContainer.style.height = "400px";
    CategoriesContainer.style.border= "1px solid #ccc";
    CategoriesContainer.style.padding = "10px";
    
    
    generateCategoryTree(response, CategoriesContainer);
  }else if(xhr.status === 401){
		
		//msg = "C'è stato un errore!";
		//generateErrorMsg(msg);
	}
	}
};
xhr.send();

document.addEventListener("DOMContentLoaded", function() {
			
	// Effettua una richiesta AJAX per ottenere i dati dal server
    var xhr = new XMLHttpRequest();
    xhr.open("GET", "AllCategories", true); // Sostituisci con l'URL corretto per raggiungere la tua servlet
    xhr.onload = function() {
		if (xhr.readyState === 4 ) {
        if (xhr.status === 200) {
            var response = JSON.parse(xhr.responseText);
           
           // Ottenere il riferimento all'elemento <div> con id "formContainer"
    		var formContainer = document.getElementById("formContainer");
    
    		generateCategoryForm(response , formContainer); 
            
        }else if(xhr.status === 401){
		
			//msg = "C'è stato un errore!";
			//generateErrorMsg(msg);
		}
		}
    };
    xhr.send();
	
    })
    
    
 window.addEventListener("load", () => {
	    if (sessionStorage.getItem("username") == null) {
	      window.location.href = "Index.html";
	    } else {
	      
	     }
	       // display initial content
}, false);

}
	  
	  
	  
    
     
    
/**
 * 
 */

document.addEventListener("DOMContentLoaded", function() {

 // Creazione del form
var form = document.createElement('form');
form.setAttribute("action", "login");
    form.setAttribute("method", "POST");

var h = document.createElement('header');
h.textContent = 'Login';
h.classList.add('form-header');
h.style.textAlign = 'center';
h.style.fontfamily= 'Arial, sans-serif';
h.style.fontSize = '25px';
h.style.marginBottom = '15px';


// Creazione del campo di input per l'username
var usernameInput = document.createElement('input');
usernameInput.type = 'text';
usernameInput.id = 'username';
usernameInput.setAttribute('username', 'username');
usernameInput.required = true;
usernameInput.placeholder = 'Username';

// Creazione del campo di input per la password
var passwordInput = document.createElement('input');
passwordInput.type = 'password';
passwordInput.id = 'password';
passwordInput.setAttribute('password', 'password');
passwordInput.required = true;
passwordInput.placeholder = 'Password';

// Creazione del pulsante di submit
var submitButton = document.createElement('button');
submitButton.textContent = 'Accedi';

// Aggiunta degli elementi al form
form.appendChild(usernameInput);
form.appendChild(passwordInput);
form.appendChild(submitButton);

// Aggiunta del form al documento
var container = document.getElementById('container'); // Sostituisci 'container' con l'ID del tuo elemento di destinazione
container.appendChild(h);
container.appendChild(form);


container.style.maxWidth = '400px';
container.style.margin = '0 auto';
container.style.marginTop = '200px';
container.style.padding = '40px';
container.style.border = '1px solid #ccc';
container.style.borderRadius = '5px';
container.style.backgroundColor = '#f7f7f7';

form.style.display = 'flex';
form.style.flexDirection = 'column';
form.style.alignItems = 'center';

// Regole CSS per i campi di input
usernameInput.style.width = '70%'; // Imposta la larghezza del campo di input a 100%
usernameInput.style.padding = '5px'; // Aumenta il padding per aumentare l'altezza del campo di input
usernameInput.style.marginBottom = '10px'; // Aumenta il margine inferiore per separare i campi di input

passwordInput.style.width = '70%';
passwordInput.style.padding = '5px';
passwordInput.style.marginBottom = '10px';

submitButton.style.width = '75%';
submitButton.style.padding = '10px';
submitButton.style.backgroundColor = '#4CAF50';
submitButton.style.color = '#fff';
submitButton.style.border = 'none';
submitButton.style.borderRadius = '5px';
submitButton.style.cursor = 'pointer';


if(sessionStorage.getItem("username") !== null){
		window.location.href = "Home.html";
		return;
	}
	
container.addEventListener("submit", (e) =>{
		
		e.preventDefault();
		
        var formData = {
			username: usernameInput.value,
			password: passwordInput.value
		}
        console.log(formData);

        // Converte l'oggetto in una stringa JSON
  		var jsonData = JSON.stringify(formData);
  	
  		console.log(formData);
  		
  		var form = e.target.closest("form");
    if (form.checkValidity()) {

  		// Invia la richiesta AJAX alla servlet
  		var xhr = new XMLHttpRequest();
  		xhr.open(form.getAttribute('method'), form.getAttribute('action'), true);
  		xhr.setRequestHeader('Content-type', 'application/json');
  		xhr.onreadystatechange = function() {
    	if (xhr.readyState === 4) {
			
			let message = xhr.responseText;
			
     		switch(xhr.status){
					case 200: 
	                     sessionStorage.setItem("username", message);
	                     window.location.href = "Home.html";
	                     break;
	                case 403:
						sessionStorage.setItem("username",xhr.getResponseHeader("username"));
						window.location.href = xhr.getResponseHeader("location");	
						break;        
					default:
                      	document.getElementById("login-error").textContent = message;
                      	document.getElementById("login-error").style.textAlign = 'center';
                      	break;
                      	
                	}
      	
   			}
  		};
 		xhr.send(jsonData);
 		}else{
			 form.reportValidity();
		 }
 	});

});
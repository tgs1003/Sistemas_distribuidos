function refreshIframe() { // recarga el iframe de la página
	frames[0].location.reload(true);
}

// Valida que no esté vacio
function validar_nombreusuario(elemento) {
	var nombreusuario = document.getElementById(elemento).value;
	if (nombreusuario.length == 0 || !nombreusuario.trim() || nombreusuario == null) {
		alert("Introduce un nombre de usuario");
	} else {
		document.getElementById("formulario_login").submit();
	}
}
